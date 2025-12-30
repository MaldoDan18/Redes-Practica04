import java.io.*;
import java.net.*;

public class ServidorSecundario {
    public static void main(String[] args) {
        int puerto = 8081; // Este escucha en el puerto de la redirección
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor Secundario (Sucursal 2) activo en puerto " + puerto);
            
            while (true) {
                try (Socket cliente = serverSocket.accept();
                     PrintWriter out = new PrintWriter(cliente.getOutputStream(), true)) {
                    
                    System.out.println("Recibí a un cliente redirigido.");
                    
                    // Respondemos algo que demuestre que estamos en el segundo servidor
                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Type: text/html; charset=utf-8");
                    out.println("");
                    out.println("<html><body style='background-color: #ffe0e0;'>");
                    out.println("<h1>⚠️ Estás en el Servidor SECUNDARIO (Puerto 8081)</h1>");
                    out.println("<p>Llegaste aquí porque el servidor principal estaba lleno.</p>");
                    out.println("</body></html>");
                }
            }
        } catch (IOException e) {
            System.err.println("Error en secundario: " + e.getMessage());
        }
    }
}
