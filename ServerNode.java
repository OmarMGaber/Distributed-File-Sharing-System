import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ServerNode implements Runnable {
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
                    ServerNode.broadcast(ServerNode.this, message);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static int numberOfNodes = 0;
    private final int NODE_ID;
    private static final int SERVERS_PORT = 5000;
    private final int PORT;
    private ServerSocket serverSocket;
    private List<ServerNode> serverPeers = Collections.synchronizedList(new ArrayList<>());
    private List<Socket> clientSockets = Collections.synchronizedList(new ArrayList<>());

    ServerNode() {
        numberOfNodes++;
        this.NODE_ID = numberOfNodes;
        this.PORT = SERVERS_PORT + this.NODE_ID;
    }

    boolean addPeer(ServerNode serverNode) {
        return serverPeers.add(serverNode);
    }

    boolean removePeer(ServerNode serverNode) {
        return serverPeers.remove(serverNode);
    }

    static void broadcast(ServerNode serverNode, String message) {
        System.out.println("Broadcasting message: " + message);
        for (ServerNode node : serverNode.serverPeers) {
            try {
                System.out.println("Sending message to node " + node.NODE_ID + " on port " + node.PORT + "...");
                for (Socket socket : node.clientSockets) {
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    outputStream.writeUTF(message);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(this.PORT);
            System.out.println("Node " + this.NODE_ID + " is running as a server on port " + this.PORT);

            while (!this.serverSocket.isClosed()) {
                System.out.printf("Waiting for new client to connect on port %d\n", this.PORT);
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                clientSockets.add(clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

