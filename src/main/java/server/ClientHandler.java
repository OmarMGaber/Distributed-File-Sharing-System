package server;

import files.FileOperations;
import models.ServerFile;
import models.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

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
        FileOperations operation = (FileOperations) objectInputStream.readUnshared();
        User user = (User) objectInputStream.readUnshared();

        switch (operation) {
            case STORE_FILE:
                handleStoreFile(user);
                break;
            case RETRIEVE_FILE:
                handleRetrieveFile(user);
                break;
            case DELETE_FILE:
                handleDeleteFile(user);
                break;
            case LIST_FILES:
                handleListFiles(user);
                break;
        }
    }

    private void handleStoreFile(User user) throws IOException, ClassNotFoundException {
        ServerFile serverFile = (ServerFile) objectInputStream.readUnshared();
        boolean stored = serverNode.preformOperation(FileOperations.STORE_FILE, user, serverFile);

        objectOutputStream.writeUnshared(stored);
        objectOutputStream.flush();

        System.out.println("User " + user.getUsername() + " stored file " + serverFile.getFileFullName() + " successfully");
    }

    private void handleRetrieveFile(User user) throws IOException, ClassNotFoundException {
        String fileFullName = (String) objectInputStream.readUnshared();
        ServerFile retrievedFile = (ServerFile) serverNode.preformOperation(FileOperations.RETRIEVE_FILE, user, fileFullName);

        objectOutputStream.writeUnshared(retrievedFile);
        objectOutputStream.flush();

        System.out.println("User " + user.getUsername() + " retrieved file " + fileFullName + " successfully");
    }

    private void handleDeleteFile(User user) throws IOException, ClassNotFoundException {
        String fileFullName = (String) objectInputStream.readUnshared();
        boolean deleted = (boolean) serverNode.preformOperation(FileOperations.DELETE_FILE, user, fileFullName);

        objectOutputStream.writeUnshared(deleted);
        objectOutputStream.flush();

        if (deleted) {
            System.out.println("User " + user.getUsername() + " deleted file " + fileFullName + " successfully");
        } else {
            System.out.println("User " + user.getUsername() + " failed to delete file " + fileFullName);
        }
    }


    private void handleListFiles(User user) throws IOException {
        ArrayList<ServerFile> serverFiles = (ArrayList<ServerFile>) serverNode.preformOperation(FileOperations.LIST_FILES, user);

        objectOutputStream.writeUnshared(serverFiles);
        objectOutputStream.flush();

        if (serverFiles != null) {
            System.out.println("User " + user.getUsername() + " listed files successfully");
        } else {
            System.out.println("User " + user.getUsername() + " failed to list files");
        }
    }
}
