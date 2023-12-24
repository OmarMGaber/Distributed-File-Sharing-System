package servers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class ServerNode implements Runnable {
    private static int numberOfNodes = 0;

    {
        numberOfNodes++; // Increment number of nodes when a new node is created
    }

    private final int NODE_ID;
    public static final int SERVERS_PORT = 5000;

    private SocketServer server;

    public static int getNumberOfNodes() {
        return numberOfNodes;
    }

    public ServerNode() {
        this.NODE_ID = numberOfNodes;
        this.server = new SocketServer(SERVERS_PORT + this.NODE_ID);
    }

    public boolean addPeer(ServerNode serverNode) {
        return server.getServerPeers().add(serverNode);
    }

    boolean removePeer(ServerNode serverNode) {
        return server.getServerPeers().remove(serverNode);
    }

    static void broadcast(Server server, String message) {
        System.out.println("Broadcasting message: " + message);
        for (ServerNode node : server.getServerPeers()) {
            try {
                System.out.println("Sending message to node " + node.NODE_ID + " on port " + node.server.getPort() + "...");
                node.server.sendToAll(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void run() {
        this.server.run();
    }
}

