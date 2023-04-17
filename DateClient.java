import java.util.Scanner;
import java.net.Socket;
import java.io.IOException;

/**
 * A command line client for the date server. Requires the IP address of the
 * server as the sole argument. Exits after printing the response.
 */
public class DateClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 59090);
        Scanner in = new Scanner(socket.getInputStream());
        System.out.println("O dia de hoje é: " + in.nextLine());
    }
}