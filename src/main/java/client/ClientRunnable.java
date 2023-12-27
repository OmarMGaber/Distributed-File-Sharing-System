package client;

import files.FileManager;
import models.ServerFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

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
            case ArrayList arrayList -> this.handleReceivedServerFiles((ArrayList<ServerFile>) object);
            case Boolean b -> {
                if (b) {
                    System.out.println("Operation done successfully");
                } else {
                    System.out.println("Operation failed");
                }
            }
            case null, default -> System.out.println("Unknown object received: " + object);
        }
    }

    private void handleReceivedServerFile(ServerFile serverFile) {
        System.out.println("File " + serverFile.getFileFullName() + " retrieved successfully");
        boolean isSaved = FileManager.saveFileToDirectory(serverFile, this.userFilesPath);

        if (isSaved) {
            System.out.println("File: " + serverFile.getFileFullName() + " saved successfully");
        } else {
            System.out.println("File: " + serverFile.getFileFullName() + " failed to save");
        }
    }

    private void handleReceivedServerFiles(ArrayList<ServerFile> serverFiles) {
        for (ServerFile serverFile : serverFiles) {
            this.handleReceivedServerFile(serverFile);
        }
        System.out.println("Files retrieved successfully");
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