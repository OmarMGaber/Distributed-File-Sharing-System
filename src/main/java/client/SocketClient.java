package client;

import models.ServerFile;
import models.User;
import files.FileOperations;
import server.ServerNode;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class SocketClient {

    static class ClientHandler implements Runnable {
        private final Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(this.socket.getInputStream());

                while (!socket.isClosed()) {
                    handleReceivedObject(objectInputStream.readUnshared());
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                closeSocket();
            }
        }

        private void handleReceivedObject(Object object) {
            if (object instanceof String) {
                System.out.println(object);
            } else if (object instanceof ServerFile) {
                handleReceivedServerFile((ServerFile) object);
            } else if (object instanceof ArrayList) {
                handleReceivedServerFiles((ArrayList<ServerFile>) object);
            } else if (object instanceof Boolean) {
                if ((Boolean) object) {
                    System.out.println("Operation done successfully");
                } else {
                    System.out.println("Operation failed");
                }
            } else {
                System.out.println("Unknown object received: " + object);
            }
        }

        private void saveFileToDirectory(ServerFile serverFile) {
            try {
                // save file new directory (client_files)
                File file = new File("C:\\Users\\Omar\\Desktop\\client_files\\" + serverFile.getFileFullName());
                file.getParentFile().mkdirs();
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                fileOutputStream.write(serverFile.getContent());
                fileOutputStream.flush();
                fileOutputStream.close();

                System.out.println("File " + serverFile.getFileFullName() + " saved successfully");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void handleReceivedServerFile(ServerFile serverFile) {
            System.out.println("File " + serverFile.getFileFullName() + " retrieved successfully");
            saveFileToDirectory(serverFile);
        }

        private void handleReceivedServerFiles(ArrayList<ServerFile> serverFiles) {
            for (ServerFile serverFile : serverFiles) {
                handleReceivedServerFile(serverFile);
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

    private static BufferedReader reader;
    private static ObjectOutputStream outputStream;

    public static void main(String[] args) {
        int numberOfNodes = 3;
        int port = ServerNode.SERVERS_PORT + 1 + (int) (Math.random() * numberOfNodes);
        reader = new BufferedReader(new InputStreamReader(System.in));

        try (Socket socket = new Socket("localhost", port)) {
            System.out.println("Client Started at port " + port);

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            new Thread(new ClientHandler(socket)).start();

            User user = getUserData();
            while (!socket.isClosed()) {
                getAndSendUserData(user);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static User getUserData() throws IOException {
        System.out.println("Enter username:");
        String username = reader.readLine();
        System.out.println("Enter first name:");
        String firstName = reader.readLine();
        System.out.println("Enter last name:");
        String lastName = reader.readLine();
        return new User(username, "0000", firstName, lastName);
    }

    private static void getAndSendUserData(User user) throws IOException {
        System.out.println("\t1. Store File\n" +
                "\t2. Delete File\n" +
                "\t3. Retrieve File\n" +
                "\t4. List Files\n" +
                "\t5. Update File\n" +
                "\t0. Exit\n" +
                "Choose an operation:");
        String userInput = reader.readLine();

        switch (userInput) {
            case "1":
                handleStoreFile(user);
                break;
            case "2":
                handleDeleteFile(user);
                break;
            case "3":
                handleRetrieveFile(user);
                break;
            case "4":
                handleListFiles(user);
                break;
        }

        if (userInput.equals("0")) {
            outputStream.close();
        }
    }

    private static void handleStoreFile(User user) throws IOException {
        System.out.println("Enter file path:");
        String filePath = reader.readLine();
        ServerFile serverFile = new ServerFile(filePath);

        writeObjectsToServer(FileOperations.STORE_FILE, user, serverFile);
    }

    private static void handleDeleteFile(User user) throws IOException {
        System.out.println("Enter file name:");
        String fileName = reader.readLine();

        writeObjectsToServer(FileOperations.DELETE_FILE, user, fileName);
    }

    private static void handleRetrieveFile(User user) throws IOException {
        System.out.println("Enter file name:");
        String fileName = reader.readLine();

        writeObjectsToServer(FileOperations.RETRIEVE_FILE, user, fileName);
    }

    private static void handleListFiles(User user) throws IOException {
        writeObjectsToServer(FileOperations.LIST_FILES, user);
    }

    private static void writeObjectsToServer(Object... objects) throws IOException {
        for (Object object : objects) {
            outputStream.writeUnshared(object);
        }
        outputStream.flush();
    }
}
