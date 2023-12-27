package managers;

import models.ServerFile;
import models.User;
import operations.FileOperations;
import server.ServerNode;

import java.util.HashMap;
import java.util.HashSet;

public class FileOperationsManager implements FileOperations {
    private final ServerNode serverNode;

    public FileOperationsManager(ServerNode serverNode) {
        this.serverNode = serverNode;
    }

    public boolean hasUser(User user) {
        return serverNode.getUserFilesMap().containsKey(user);
    }

    public void addUserToServerTable(User user) {
        serverNode.getUserFilesMap().put(user, new HashSet<ServerFile>());
    }

    public boolean userHas(User user, ServerFile serverFile) {
        return serverNode.getUserFilesMap().get(user).contains(serverFile);
    }

    @Override
    public boolean storeFile(User user, ServerFile serverFile) {
        if (!hasUser(user)) {
            addUserToServerTable(user);
        }

        return serverNode.getUserFilesMap().get(user).add(serverFile);
    }

    @Override
    public boolean deleteFile(User user, String fileFullName) {
        if (!hasUser(user)) {
            return false;
        }

        for (ServerFile serverFile : serverNode.getUserFilesMap().get(user)) {
            if (serverFile.getFileFullName().equals(fileFullName)) {
                serverNode.getUserFilesMap().get(user).remove(serverFile);
                return true;
            }
        }

        return false;
    }

    @Override
    public ServerFile retrieveFile(User user, String fileFullName) {
        if (!hasUser(user)) {
            return null;
        }

        for (ServerFile serverFile : serverNode.getUserFilesMap().get(user)) {
            if (serverFile.getFileFullName().equals(fileFullName)) {
                return serverFile;
            }
        }

        return null;
    }

    @Override
    public HashSet<ServerFile> listFiles(User user) {
        if (!hasUser(user)) {
            return null;
        }

        return serverNode.getUserFilesMap().get(user);
    }

    public Object preformOperation(FileOperations.OperationType operation, User user, String fileFullName) {
        int depth = 5;

        switch (operation) {
            case RETRIEVE_FILE:
                ServerFile serverFile = retrieveFile(user, fileFullName);
                if (serverFile == null) {
                    serverFile = serverNode.retrieveFileFromPeers(user, fileFullName, depth);
                }
                return serverFile;
            case DELETE_FILE:
                boolean deleted = deleteFile(user, fileFullName);
                if (!deleted) {
                    deleted = serverNode.deleteFileFromPeers(user, fileFullName, depth);
                }
                return deleted;
        }
        return null;
    }

    public HashSet<ServerFile> preformOperation(FileOperations.OperationType operation, User user) {
        HashMap<ServerNode, Boolean> visited = new HashMap<>();
        return serverNode.retrieveAllUserFiles(user, visited);
    }
}