package client;

import models.ServerFile;
import models.User;
import operations.FileOperations;
import server.ServerNode;

import java.io.*;
import java.net.Socket;

public class SocketClient {

    private static BufferedReader reader;
    private static ObjectOutputStream outputStream;
    private static Socket socket;

    public static void main(String[] args) {
        int numberOfNodes = 3;
        int port = ServerNode.SERVERS_PORT + 1 + (int) (Math.random() * numberOfNodes);
        reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            socket = new Socket("localhost", port);
            System.out.println("Client Started at port " + port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());

            System.out.println("========Login to the server========");
            System.out.print("Enter your Username: ");
            User user = new User(reader.readLine());

            new Thread(new ClientRunnable(socket,
                    "C:\\Users\\Omar\\Desktop\\client_files\\" +
                            "( " + user.getUsername() + " )\\")).start();

            while (!socket.isClosed()) {
                getAndSendUserData(user);
                Thread.sleep(1000); // Wait for server to respond before sending another request
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void getAndSendUserData(User user) throws IOException {
        System.out.println("""
                \t1. Store File
                \t2. Delete File
                \t3. Retrieve File
                \t4. List Files
                \t5. Update File
                \t0. Exit
                Choose an operation:""");
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

        writeObjectsToServer(FileOperations.OperationType.STORE_FILE, user, serverFile);
    }

    private static void handleDeleteFile(User user) throws IOException {
        System.out.println("Enter file name:");
        String fileName = reader.readLine();

        writeObjectsToServer(FileOperations.OperationType.DELETE_FILE, user, fileName);
    }

    private static void handleRetrieveFile(User user) throws IOException {
        System.out.println("Enter file name:");
        String fileName = reader.readLine();

        writeObjectsToServer(FileOperations.OperationType.RETRIEVE_FILE, user, fileName);
    }

    private static void handleListFiles(User user) throws IOException {
        writeObjectsToServer(FileOperations.OperationType.LIST_FILES, user);
    }

    private static void writeObjectsToServer(Object... objects) throws IOException {
        for (Object object : objects) {
            outputStream.writeUnshared(object);
        }
        outputStream.flush();
    }
}
