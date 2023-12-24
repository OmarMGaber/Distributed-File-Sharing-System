package servers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SocketServer extends Server {
    class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private DataInputStream input;
        private DataOutputStream output;

        ClientHandler(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            this.input = new DataInputStream(clientSocket.getInputStream());
            this.output = new DataOutputStream(clientSocket.getOutputStream());
        }


        @Override
        public void run() {
            try {
                while (!clientSocket.isClosed()) {
                    String message = input.readUTF();
                    System.out.println(message);
                    output.writeUTF("Hello from server");
                    ServerNode.broadcast(SocketServer.this, message);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private final int PORT;
    private ServerSocket serverSocket;
    private List<ServerNode> serverPeers = Collections.synchronizedList(new ArrayList<>());
    private List<Socket> clientSockets = Collections.synchronizedList(new ArrayList<>());

    SocketServer(int port) {
        this.PORT = port;
    }

    public int getPort() {
        return this.PORT;
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }


    public List<ServerNode> getServerPeers() {
        return this.serverPeers;
    }

    public List<Socket> getClientSockets() {
        return this.clientSockets;
    }

    public void sendToAll(String message) throws IOException {
        for (Socket socket : this.clientSockets) {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(message);
        }
    }

    public void run() {
        try {
            this.serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running on port " + this.PORT);

            while (!serverSocket.isClosed()) {
                System.out.println("Waiting for client to connect on port " + this.PORT);

                Socket clientSocket = serverSocket.accept();
                this.clientSockets.add(clientSocket);
                System.out.println("Client connected on port " + this.PORT);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
