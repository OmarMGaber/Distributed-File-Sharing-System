import servers.ServerNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    static class ClientRunnable implements Runnable {
        private Socket socket;
        private DataInputStream inputStream;

        ClientRunnable(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
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

    static int generateRandomPort(int lowerBound, int upperBound) {
        System.out.println("Generating random port between " + lowerBound + " and " + upperBound);
        return (int) (Math.random() * (upperBound - lowerBound)) + lowerBound;
    }

    public static void main(String[] args) throws IOException {
        int numberOfNodes = 3;
        int port = generateRandomPort(ServerNode.SERVERS_PORT + 1, ServerNode.SERVERS_PORT + numberOfNodes + 1);

        System.out.println("Client Started at port " + port);

        Socket socket = new Socket("localhost", port);

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
