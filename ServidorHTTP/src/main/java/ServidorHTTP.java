import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ServidorHTTP {
    // Este contador nos dice cuánta gente hay adentro del restaurante ahorita
    public static final AtomicInteger conexionesActivas = new AtomicInteger(0);
    private static int MAX_POOL;

    public static void main(String[] args) {
        Scanner leer = new Scanner(System.in);
        System.out.print("¿Cuántos hilos tendrá el pool? (Ejemplo: 4): ");
        MAX_POOL = leer.nextInt();

        // El Pool de conexiones (los cocineros fijos)
        ExecutorService pool = Executors.newFixedThreadPool(MAX_POOL);

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Servidor Principal iniciado en el puerto 8080...");

            while (true) {
                // El servidor se queda esperando aquí hasta que alguien entra a la página
                Socket cliente = serverSocket.accept();
                
                // Sumamos 1 al contador porque llegó alguien
                int actuales = conexionesActivas.incrementAndGet();
                
                // REGLA: Si hay más gente que la MITAD del pool, redirigimos
                if (actuales > (MAX_POOL / 2)) {
                    System.out.println("¡Mucho tráfico! Redirigiendo cliente...");
                    redirigir(cliente);
                    conexionesActivas.decrementAndGet(); // Ya no cuenta porque se fue
                } else {
                    // Si hay espacio, lo atiende un cocinero del pool
                    System.out.println("Atendiendo cliente. Conexiones: " + actuales);
                    pool.execute(new ClientHandler(cliente));
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // El mensaje que le enviamos al navegador para que se vaya a otro lado
    private static void redirigir(Socket cliente) throws IOException {
        PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
        out.println("HTTP/1.1 307 Temporary Redirect");
        out.println("Location: http://localhost:8081/"); // Lo mandamos al puerto 8081
        out.println("Content-Length: 0");
        out.println("");
        cliente.close();
    }
}