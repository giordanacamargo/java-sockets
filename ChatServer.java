import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    private static HashMap<String, String> users = new HashMap<String, String>();
    private static Set<PrintWriter> writers = new HashSet<>();

    public static void main(String[] args) throws Exception {
        System.out.println("O servidor está rodando...");
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(59898)) {
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }
        }
    }

    private static class Handler implements Runnable {
        private String name;

        private String color;

        private Socket socket;

        private Scanner in;

        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                // Fica fazendo requisições de nome até receber um nome único.
                while (true) {
                    out.println("SUBMITNAME");
                    name = in.nextLine();
                    if (name == null || name.isEmpty()) {
                        out.println("SUBMITNAME");
                        return;
                    }
                    synchronized (users) {
                        if (!name.isEmpty() && users.get(name) == null) {
                            users.put(name, null);
                            break;
                        }
                    }
                }

                // Fica fazendo requisições de cor até receber uma.
                while (true) {
                    out.println("USERCOLOR");
                    color = in.nextLine();
                    if (color == null) {
                        return;
                    } else {
                        users.put(name, color);
                        break;
                    }
                }

                // Identifica que o usuário foi aceito com sucesso.
                out.println("NAMEACCEPTED " + name);

                // Escreve para todos os clientes que o usuário entrou no chat.
                for (PrintWriter writer : writers) {
                    Color c = Color.BLACK;
                    writer.println("MESSAGE;"+ c.getRGB() + "; >>>>" +  name + ";" + " entrou no chat.");
                }
                writers.add(out);

                // Transmite as mensagens para os usuários
                while (true) {
                    String input = in.nextLine();
                    if (input.toLowerCase().startsWith("/quit")) {
                        return;
                    }
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE;"+ color + ";" +  name + ": " + ";" + input);
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (out != null) {
                    writers.remove(out);
                }
                if (name != null) {
                    System.out.println(name + " is leaving");
                    users.remove(name);
                    for (PrintWriter writer : writers) {
                        Color c = Color.BLACK;
                        writer.println("MESSAGE;"+ c.getRGB() + "; >>>>" +  name + ";" + " saiu do chat.");
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}