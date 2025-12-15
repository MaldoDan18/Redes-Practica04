
// ===================== Cliente.java =====================
import java.io.*;
import java.net.*;
import java.util.*;

public class Cliente {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 8080;

        Socket socket = new Socket(host, port);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        Scanner sc = new Scanner(System.in);

        // Hilo para escuchar mensajes del servidor
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    System.out.println("Servidor: " + msg);
                }
            } catch (IOException ignored) {}
        }).start();

        // Enviar mensajes
        try {
            while (true) {
                String input = sc.nextLine();
                out.println(input);
                if (input.equalsIgnoreCase("salir")) break;
            }
        } finally {
            sc.close();
            socket.close();
        }

        System.out.println("Cliente desconectado.");
    }
}
