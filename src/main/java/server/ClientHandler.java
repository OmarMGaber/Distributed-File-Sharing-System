package server;

import managers.DirectoryManager;
import models.ServerFile;
import models.User;
import operations.FileOperations;
import managers.FileOperationsManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;

class ClientHandler implements Runnable {
    private final FileOperationsManager fileOperationsManager;
    private final Socket clientSocket;
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;

    ClientHandler(FileOperationsManager fileOpsHandler, Socket clientSocket) throws IOException {
        this.fileOperationsManager = fileOpsHandler;
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
                this.clientSocket.close();
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
        boolean isStored = fileOperationsManager.storeFile(user, serverFile);
        objectOutputStream.writeUnshared(isStored);
        objectOutputStream.flush();

        boolean isSaved = DirectoryManager.saveFileToDirectory(serverFile,
                "E:\\Projects\\JavaFX\\DistributedSystems\\Distributed-File-Sharing-System\\ReceivedFiles\\"
                        + "( " + user.getUsername() + " )\\");

        System.out.println("User: " + user.getUsername() + ((isStored) ? " stored file " + serverFile.getFileFullName() + " successfully" : " failed to store file " + serverFile.getFileFullName()));
        System.out.println("File Manager: " + serverFile.getFileFullName() + ((isSaved) ? " saved successfully" : " failed to save"));
    }

    private void handleRetrieveFile(User user) throws IOException, ClassNotFoundException {
        String fileFullName = (String) objectInputStream.readUnshared();
        ServerFile retrievedFile = (ServerFile) fileOperationsManager.preformOperation(FileOperations.OperationType.RETRIEVE_FILE, user, fileFullName);

        objectOutputStream.writeUnshared(retrievedFile);
        objectOutputStream.flush();

        System.out.println("User: " + user.getUsername() + " retrieved file " + fileFullName + " successfully");
    }

    private void handleDeleteFile(User user) throws IOException, ClassNotFoundException {
        String fileFullName = (String) objectInputStream.readUnshared();
        boolean deleted = (boolean) fileOperationsManager.preformOperation(FileOperations.OperationType.DELETE_FILE, user, fileFullName);

        objectOutputStream.writeUnshared(deleted);
        objectOutputStream.flush();

        if (deleted) {
            DirectoryManager.deleteFileFromDirectory(fileFullName,
                    "E:\\Projects\\JavaFX\\DistributedSystems\\Distributed-File-Sharing-System\\ReceivedFiles\\"
                            + "( " + user.getUsername() + " )\\");
        } else {
            System.out.println("File Manager: " + fileFullName + " does not exist");
        }

        System.out.println("User: " + user.getUsername() + ((deleted) ? " deleted file " + fileFullName + " successfully" : " failed to delete file " + fileFullName));
    }

    private void handleListFiles(User user) throws IOException {
        HashSet<ServerFile> serverFiles = (HashSet<ServerFile>) fileOperationsManager.preformOperation(FileOperations.OperationType.LIST_FILES, user);

        objectOutputStream.writeUnshared(serverFiles);
        objectOutputStream.flush();

        System.out.println("User: " + user.getUsername() + ((serverFiles == null) ? " has no files" : " listed files successfully"));
    }
}
