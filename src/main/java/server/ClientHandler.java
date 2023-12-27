package server;

import managers.FileManager;
import operations.FileOperations;
import models.ServerFile;
import models.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class ClientHandler implements Runnable {
    private final ServerNode serverNode;
    private final Socket clientSocket;
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;

    ClientHandler(ServerNode serverNode, Socket clientSocket) throws IOException {
        this.serverNode = serverNode;
        this.clientSocket = clientSocket;
        try {
            this.objectInputStream = new ObjectInputStream(this.clientSocket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        try {
            while (!clientSocket.isClosed()) {

                handleClientRequest();

            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                System.out.println("Closing client socket...");
                clientSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleClientRequest() throws IOException, ClassNotFoundException {
        FileOperations.OperationType operation = (FileOperations.OperationType) objectInputStream.readUnshared();
        User user = (User) objectInputStream.readUnshared();

        switch (operation) {
            case FileOperations.OperationType.STORE_FILE:
                handleStoreFile(user);
                break;
            case FileOperations.OperationType.RETRIEVE_FILE:
                handleRetrieveFile(user);
                break;
            case FileOperations.OperationType.DELETE_FILE:
                handleDeleteFile(user);
                break;
            case FileOperations.OperationType.LIST_FILES:
                handleListFiles(user);
                break;
        }
    }


    private void handleStoreFile(User user) throws IOException, ClassNotFoundException {
        ServerFile serverFile = (ServerFile) objectInputStream.readUnshared();
        boolean isStored = serverNode.preformOperation(FileOperations.OperationType.STORE_FILE, user, serverFile);

        objectOutputStream.writeUnshared(isStored);
        objectOutputStream.flush();

        boolean isSaved = FileManager.saveFileToDirectory(serverFile,
                "E:\\Projects\\JavaFX\\DistributedSystems\\Distributed-File-Sharing-System\\ReceivedFiles\\"
                        + "( " + user.getUsername() + " )\\");

        System.out.println("User: " + user.getUsername() + ((isStored) ? " stored file " + serverFile.getFileFullName() + " successfully" : " failed to store file " + serverFile.getFileFullName()));
        System.out.println("File Manager: " + serverFile.getFileFullName() + ((isSaved) ? " saved successfully" : " failed to save"));
    }

    private void handleRetrieveFile(User user) throws IOException, ClassNotFoundException {
        String fileFullName = (String) objectInputStream.readUnshared();
        ServerFile retrievedFile = (ServerFile) serverNode.preformOperation(FileOperations.OperationType.RETRIEVE_FILE, user, fileFullName);

        objectOutputStream.writeUnshared(retrievedFile);
        objectOutputStream.flush();

        System.out.println("User: " + user.getUsername() + " retrieved file " + fileFullName + " successfully");
    }

    private void handleDeleteFile(User user) throws IOException, ClassNotFoundException {
        String fileFullName = (String) objectInputStream.readUnshared();
        boolean deleted = (boolean) serverNode.preformOperation(FileOperations.OperationType.DELETE_FILE, user, fileFullName);

        objectOutputStream.writeUnshared(deleted);
        objectOutputStream.flush();

        if (deleted) {
            FileManager.deleteFileFromDirectory(fileFullName,
                    "E:\\Projects\\JavaFX\\DistributedSystems\\Distributed-File-Sharing-System\\ReceivedFiles\\"
                            + "( " + user.getUsername() + " )\\");
        } else {
            System.out.println("File Manager: " + fileFullName + " does not exist");
        }

        System.out.println("User: " + user.getUsername() + ((deleted) ? " deleted file " + fileFullName + " successfully" : " failed to delete file " + fileFullName));
    }

    private void handleListFiles(User user) throws IOException {
        HashSet<ServerFile> serverFiles = (HashSet<ServerFile>) serverNode.preformOperation(FileOperations.OperationType.LIST_FILES, user);

        objectOutputStream.writeUnshared(serverFiles);
        objectOutputStream.flush();

        System.out.println("User: " + user.getUsername() + ((serverFiles == null) ? " has no files" : " listed files successfully"));
    }
}
