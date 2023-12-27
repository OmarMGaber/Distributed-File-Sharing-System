package server;

import models.ServerFile;
import models.User;
import managers.FileOperationsManager;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ServerNode implements Runnable, Comparable<ServerNode> {
    private static int numberOfNodes = 0;
    public final int NODE_ID;
    private final int PORT;
    public static final int SERVERS_PORT = 5000;
    private final FileOperationsManager fileOperationsManager;

    private final List<ServerNode> serverPeers;
    private final HashMap<User, HashSet<ServerFile>> userFilesMap;

    public ServerNode() {
        numberOfNodes++; // Increment number of nodes when a new node is created
        this.NODE_ID = numberOfNodes;
        this.serverPeers = new ArrayList<>();
        this.userFilesMap = new HashMap<>();
        this.PORT = SERVERS_PORT + this.NODE_ID;
        this.fileOperationsManager = new FileOperationsManager(this);
    }

    public ServerNode(int port) {
        numberOfNodes++; // Increment number of nodes when a new node is created
        this.NODE_ID = numberOfNodes;
        this.serverPeers = new ArrayList<>();
        this.userFilesMap = new HashMap<>();
        this.PORT = port;
        this.fileOperationsManager = new FileOperationsManager(this);
    }

    public boolean addPeer(ServerNode serverNode) {
        return this.serverPeers.add(serverNode);
    }

    @Override
    public int compareTo(ServerNode serverNodeObject) {
        return Integer.compare(this.NODE_ID, serverNodeObject.NODE_ID);
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.PORT);
            System.out.println("Server " + this.NODE_ID + " started on port " + this.PORT);

            while (!serverSocket.isClosed()) {

                new Thread(new ClientHandler(this.fileOperationsManager, serverSocket.accept())).start();
                System.out.println("New Client at port:" + this.PORT);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HashSet<ServerFile> retrieveAllUserFiles(User user, HashMap<ServerNode, Boolean> visited) {
        HashSet<ServerFile> userFiles = new HashSet<>();

        if (visited.containsKey(this)) {
            return userFiles;
        }

        visited.put(this, true);

        if (fileOperationsManager.hasUser(user)) {
            userFiles.addAll(fileOperationsManager.listFiles(user));
        }

        for (ServerNode serverNode : this.serverPeers) {
            userFiles.addAll(serverNode.retrieveAllUserFiles(user, visited));
        }

        return userFiles;
    }

    public ServerFile retrieveFileFromPeers(User user, String fileFullName, int depth) {
        if (depth == 0) {
            return null;
        }

        for (ServerNode serverNode : this.serverPeers) {
            if (serverNode.fileOperationsManager.hasUser(user)) {
                ServerFile serverFile = serverNode.fileOperationsManager.retrieveFile(user, fileFullName);
                if (serverFile != null) {
                    return serverFile;
                }
            }
            ServerFile serverFile = serverNode.retrieveFileFromPeers(user, fileFullName, depth - 1); // Recursively retrieve file from peers
            if (serverFile != null) {
                return serverFile;
            }
        }

        return null;
    }

    public boolean deleteFileFromPeers(User user, String fileFullName, int depth) {
        if (depth == 0) {
            return false;
        }

        for (ServerNode serverNode : this.serverPeers) {
            if (serverNode.fileOperationsManager.hasUser(user)) {
                if (serverNode.fileOperationsManager.deleteFile(user, fileFullName)) {
                    return true;
                }
            }
            if (serverNode.deleteFileFromPeers(user, fileFullName, depth - 1)) { // Recursively delete file from peers
                return true;
            }
        }

        return false;
    }

    public HashMap<User, HashSet<ServerFile>> getUserFilesMap() {
        return this.userFilesMap;
    }
}