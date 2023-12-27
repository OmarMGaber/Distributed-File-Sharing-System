package client;

import managers.FileManager;
import models.ServerFile;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class ClientRunnable implements Runnable {
    private final Socket socket;
    private final String userFilesPath;

    ClientRunnable(Socket socket, String path) {
        this.socket = socket;
        this.userFilesPath = path;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(this.socket.getInputStream());

            while (!socket.isClosed()) {
                this.handleReceivedObject(objectInputStream.readUnshared());
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            this.closeSocket();
        }
    }

    private void handleReceivedObject(Object object) {
        switch (object) {
            case String s -> System.out.println(object);
            case ServerFile serverFile -> this.handleReceivedServerFile(serverFile);
            case HashSet arrayList -> this.handleReceivedServerFiles((HashSet<ServerFile>) object);
            case Boolean b -> {
                if (b) {
                    System.out.println("Server: Operation done successfully");
                } else {
                    System.out.println("Server: Operation failed");
                }
            }
            case null, default -> System.out.println("Error: Unknown object received: " + object);
        }
    }

    private void handleReceivedServerFile(ServerFile serverFile) {
        System.out.println("File Manager: " + serverFile.getFileFullName() + " retrieved successfully");

        if (FileManager.directoryHasFile(serverFile.getFileFullName(), this.userFilesPath)) {
            System.out.println("File Manager: " + serverFile.getFileFullName() + " already exists in the directory");
            return;
        }

        boolean isSaved = FileManager.saveFileToDirectory(serverFile, this.userFilesPath);

        System.out.println("File Manager: " + serverFile.getFileFullName() + ((isSaved) ? " saved successfully" : " failed to save"));
    }

    private void handleReceivedServerFiles(HashSet<ServerFile> serverFiles) {
        for (ServerFile serverFile : serverFiles) {
            this.handleReceivedServerFile(serverFile);
        }

        System.out.println("File Manager: " + serverFiles.size() + " files retrieved successfully");
    }

    private void closeSocket() {
        try {
            System.out.println("Closing client socket...");
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}