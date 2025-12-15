
// ===================== Servidor.java =====================
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Servidor {
    private final int port;
    private final ExecutorService pool;
    private final ServerManager manager;
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private volatile boolean running = true;
    private ServerSocket serverSocket;

    public Servidor(int port, int poolSize, ServerManager manager) throws IOException {
        this.port = port;
        this.pool = Executors.newFixedThreadPool(poolSize);
        this.manager = manager;
        this.serverSocket = new ServerSocket(port);
        System.out.println("Servidor iniciado en puerto " + port);
    }

    public void start() {
        // Hilo para aceptar clientes
        new Thread(() -> {
            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    int current = activeConnections.incrementAndGet();

                    // Revisar si hay que iniciar servidor secundario
                    if (manager.shouldStartSecondary(current)) {
                        startSecondaryServer();
                    }

                    pool.execute(new ClientHandler(client));
                } catch (IOException e) {
                    if (running) e.printStackTrace();
                }
            }
        }).start();

        // Hilo para comandos del servidor (apagar)
        new Thread(() -> {
            try (Scanner sc = new Scanner(System.in)) {
                while (running) {
                    String cmd = sc.nextLine();
                    if (cmd.equalsIgnoreCase("apagar")) {
                        shutdown();
                    }
                }
            }
        }).start();
    }

    private void startSecondaryServer() {
        if (manager.isSecondaryRunning()) return;
        manager.markSecondaryRunning();
        new Thread(() -> {
            try {
                Servidor secondary = new Servidor(8081, 4, manager);
                secondary.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        System.out.println("Servidor secundario iniciado en puerto 8081");
    }

    public void shutdown() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException ignored) {}
        pool.shutdownNow();
        System.out.println("Servidor en puerto " + port + " apagado.");
    }

    private class ClientHandler implements Runnable {
        private final Socket client;

        ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream(), true)
            ) {
                out.println("Conectado al servidor en puerto " + port);
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.equalsIgnoreCase("salir")) {
                        out.println("Conexion cerrada.");
                        break;
                    }
                    out.println("Echo: " + line);
                }
            } catch (IOException e) {
                // cliente desconectado
            } finally {
                activeConnections.decrementAndGet();
                try { client.close(); } catch (IOException ignored) {}
            }
        }
    }

    public static void main(String[] args) throws Exception {
        int poolSize = 4;
        ServerManager manager = new ServerManager(poolSize);
        Servidor server = new Servidor(8080, poolSize, manager);
        server.start();
    }
}
