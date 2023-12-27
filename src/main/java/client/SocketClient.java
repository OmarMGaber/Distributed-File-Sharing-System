package client;

import models.ServerFile;
import models.User;
import files.FileOperations;
import server.ServerNode;

import java.io.*;
import java.net.Socket;

public class SocketClient {

    private static BufferedReader reader;
    private static ObjectOutputStream outputStream;

    public static void main(String[] args) {
        int numberOfNodes = 3;
        int port = ServerNode.SERVERS_PORT + 1 + (int) (Math.random() * numberOfNodes);
        reader = new BufferedReader(new InputStreamReader(System.in));

        try (Socket socket = new Socket("localhost", port)) {
            System.out.println("Client Started at port " + port);

            User user = getUserData();

            outputStream = new ObjectOutputStream(socket.getOutputStream());
            new Thread(new ClientRunnable(socket,
                    "C:\\Users\\Omar\\Desktop\\client_files\\"
                            + user.getFirstName() + user.getLastName() +
                            "( " + user.getUsername() + " )\\")).start();

            while (!socket.isClosed()) {
                getAndSendUserData(user);
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
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
