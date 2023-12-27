package server;

import files.FileOperations;
import files.FileOperationsManager;
import models.ServerFile;
import models.User;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerNode implements Runnable, Comparable<ServerNode>, FileOperationsManager {

    public boolean preformOperation(FileOperations operation, User user, ServerFile serverFile) {
        return this.storeFile(user, serverFile);
    }

    public Object preformOperation(FileOperations operation, User user, String fileFullName) {
        int depth = 5;

        switch (operation) {
            case RETRIEVE_FILE:
                ServerFile serverFile = this.retrieveFile(user, fileFullName);
                if (serverFile == null) {
                    serverFile = this.retrieveFileFromPeers(user, fileFullName, depth);
                }
                return serverFile;
            case DELETE_FILE:
                boolean deleted = this.deleteFile(user, fileFullName);
                if (!deleted) {
                    deleted = this.deleteFileFromPeers(user, fileFullName, depth);
                }
                return deleted;
        }
        return null;
    }

    public ArrayList<ServerFile> preformOperation(FileOperations operation, User user) {
        HashMap<ServerNode, Boolean> visited = new HashMap<>();
        return this.retrieveAllUserFiles(user, visited);
    }

    public ArrayList<ServerFile> retrieveAllUserFiles(User user, HashMap<ServerNode, Boolean> visited) {
        ArrayList<ServerFile> userFiles = new ArrayList<>();

        if (visited.containsKey(this)) {
            return userFiles;
        }

        visited.put(this, true);

        if (this.hasUser(user)) {
            userFiles.addAll(this.listFiles(user));
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
            if (serverNode.hasUser(user)) {
                ServerFile serverFile = serverNode.retrieveFile(user, fileFullName);
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
            if (serverNode.hasUser(user)) {
                if (serverNode.deleteFile(user, fileFullName)) {
                    return true;
                }
            }
            if (serverNode.deleteFileFromPeers(user, fileFullName, depth - 1)) { // Recursively delete file from peers
                return true;
            }
        }

        return false;
    }

    private static int numberOfNodes = 0;

    {
        numberOfNodes++; // Increment number of nodes when a new node is created
    }

    private final int NODE_ID;
    private final int PORT;
    public static final int SERVERS_PORT = 5000;
    private ServerSocket serverSocket;

    private final List<ServerNode> serverPeers;
    private final HashMap<User, ArrayList<ServerFile>> userFilesMap;

    public ServerNode() {
        this.NODE_ID = numberOfNodes;
        this.serverPeers = new ArrayList<>();
        this.userFilesMap = new HashMap<>();
        this.PORT = SERVERS_PORT + this.NODE_ID;
    }

    public ServerNode(int port) {
        this.NODE_ID = numberOfNodes;
        this.serverPeers = new ArrayList<>();
        this.userFilesMap = new HashMap<>();
        this.PORT = port;
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
            this.serverSocket = new ServerSocket(this.PORT);
            System.out.println("Server " + this.NODE_ID + " started on port " + this.PORT);

            while (!this.serverSocket.isClosed()) {

                new Thread(new ClientHandler(this, this.serverSocket.accept())).start();
                System.out.println("New Client at port:" + this.PORT);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasUser(User user) {
        return this.userFilesMap.containsKey(user);
    }

    public void addUserToServerTable(User user) {
        this.userFilesMap.put(user, new ArrayList<>());
    }

    public boolean userHas(User user, ServerFile serverFile) {
        return this.userFilesMap.get(user).contains(serverFile);
    }

    @Override
    public boolean storeFile(User user, ServerFile serverFile) {
        if (!this.hasUser(user)) {
            this.addUserToServerTable(user);
        }

        if (!this.userHas(user, serverFile)) {
            this.userFilesMap.get(user).add(serverFile);
            return true;
        }

        return false;
    }

    @Override
    public boolean deleteFile(User user, String fileFullName) {
        if (!this.hasUser(user)) {
            return false;
        }

        for (ServerFile serverFile : this.userFilesMap.get(user)) {
            if (serverFile.getFileFullName().equals(fileFullName)) {
                return this.userFilesMap.get(user).remove(serverFile);
            }
        }

        return false;
    }

    @Override
    public ServerFile retrieveFile(User user, String fileFullName) {
        if (!this.hasUser(user)) {
            return null;
        }

        for (ServerFile serverFile : this.userFilesMap.get(user)) {
            if (serverFile.getFileFullName().equals(fileFullName)) {
                return serverFile;
            }
        }

        return null;
    }

    @Override
    public ArrayList<ServerFile> listFiles(User user) {
        if (!this.hasUser(user)) {
            return null;
        }

        return this.userFilesMap.get(user);
    }
}