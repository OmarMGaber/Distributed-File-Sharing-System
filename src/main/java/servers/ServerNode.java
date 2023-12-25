package servers;

import models.User;

public class ServerNode implements Runnable, Comparable<ServerNode> { // implements FileOperations
    private static int numberOfNodes = 0;

    {
        numberOfNodes++; // Increment number of nodes when a new node is created
    }

    private final int NODE_ID;
    public static final int SERVERS_PORT = 5000;

    private final Server server;

    public static int getNumberOfNodes() {
        return numberOfNodes;
    }

    public ServerNode() {
        this.NODE_ID = numberOfNodes;
        this.server = new SocketServer(SERVERS_PORT + this.NODE_ID);
    }

    public ServerNode(Server server) {
        this.NODE_ID = numberOfNodes;
        this.server = server;
    }

    public boolean addPeer(ServerNode serverNode) {
        return this.server.getServerPeers().add(serverNode);
    }

    public boolean removePeer(ServerNode serverNode) {
        return this.server.getServerPeers().remove(serverNode);
    }

    static void retrieveFileFromServer(ServerNode serverNode, User user, String fileName) {

    }

    @Override
    public void run() {
        this.server.run();
    }

    @Override
    public int compareTo(ServerNode serverNodeObject) {
        return Integer.compare(this.NODE_ID, serverNodeObject.NODE_ID);
    }

}

