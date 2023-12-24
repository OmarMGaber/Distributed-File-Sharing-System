import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    static class ClientRunnable implements Runnable {
        Socket socket;

        ClientRunnable(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            DataInputStream inputStream = null;
            try {
                inputStream = new DataInputStream(this.socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!socket.isClosed()) {
                try {
                    System.out.println("Server Sent: " + inputStream.readUTF());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 5002);
        System.out.println("Client Started");
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        ClientRunnable clientRunnable = new ClientRunnable(socket);
        new Thread(clientRunnable).start();

        while (!socket.isClosed()) {
            System.out.println("Enter a message: ");
            String message = new java.util.Scanner(System.in).nextLine();
            outputStream.writeUTF(message);
        }
    }

}
