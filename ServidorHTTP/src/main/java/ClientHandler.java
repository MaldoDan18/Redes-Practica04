import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        //RETRASO DE 5 SEGUNDOS
        try {
            Thread.sleep(5000); // 5 segundos de espera
        } catch (InterruptedException e) {
            System.out.println("Error en el retraso: " + e.getMessage());
        }
        
        try (
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedOutputStream dataOut = new BufferedOutputStream(socket.getOutputStream())) {

            String input = in.readLine();
            if (input == null) return;
            
            StringTokenizer parse = new StringTokenizer(input);
            String metodo = parse.nextToken().toUpperCase();
            String archivoSolicitado = parse.nextToken();
            
            System.out.println("Solicitud: " + metodo + " de " + archivoSolicitado);

            if (metodo.equals("GET")) {
                // Si solo ponen /, enviamos el index.html
                if (archivoSolicitado.equals("/")) archivoSolicitado = "/index.html";
                
                // Buscamos el archivo en la carpeta "web"
                File file = new File("web", archivoSolicitado);
                
                if (file.exists()) {
                    byte[] fileData = readFileData(file);
                    String contentType = getContentType(archivoSolicitado);

                    // Enviamos cabeceras HTTP
                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-type: " + contentType);
                    out.println("Content-length: " + fileData.length);
                    out.println(""); // Línea en blanco obligatoria
                    out.flush();

                    // Enviamos los datos del archivo (binario para fotos/texto)
                    dataOut.write(fileData, 0, fileData.length);
                    dataOut.flush();
                } else {
                    out.println("HTTP/1.1 404 Not Found");
                    out.println("");
                    out.println("<h1>404 Archivo no encontrado</h1>");
                }
            } else {
                // Para POST, PUT, DELETE respondemos algo simple
                out.println("HTTP/1.1 200 OK");
                out.println("Content-type: text/plain");
                out.println("");
                out.println("Metodo " + metodo + " procesado correctamente.");
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            try {
                ServidorHTTP.conexionesActivas.decrementAndGet();
                socket.close();
            } catch (IOException e) {}
        }
    }

    private String getContentType(String file) {
        if (file.endsWith(".html")) return "text/html";
        if (file.endsWith(".txt")) return "text/plain";
        if (file.endsWith(".json")) return "application/json";
        if (file.endsWith(".jpg") || file.endsWith(".jpeg")) return "image/jpeg";
        return "text/plain";
    }

    private byte[] readFileData(File file) throws IOException {
        byte[] fileData = new byte[(int) file.length()];
        try (FileInputStream fileIn = new FileInputStream(file)) {
            fileIn.read(fileData);
        }
        return fileData;
    }
}