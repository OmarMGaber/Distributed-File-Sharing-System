package client;

import models.Message;
import models.ServerFile;
import models.User;
import operations.FileOperations;
import operations.ServerOperation;
import servers.ServerNode;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client {
    static class ClientRunnable implements Runnable {
        private final Socket socket;

        ClientRunnable(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(this.socket.getInputStream());

                while (!socket.isClosed()) {
                    Object object = objectInputStream.readObject();
                    if (object instanceof ServerFile file) {
                        System.out.println(file.getFileFullName());
                        System.out.println(file);
                    } else if (object instanceof List) {
                        ArrayList files = (ArrayList) object;
                        files.forEach(file -> System.out.println(((ServerFile) file).getFileFullName()));
                    } else if (object instanceof Boolean result) {
                        System.out.println(result);
                    }
                }


            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static BufferedReader reader;
    static ObjectOutputStream outputStream;


    static int generateRandomPort(int lowerBound, int upperBound) {
        System.out.println("Generating random port between " + lowerBound + " and " + upperBound);
        return (int) (Math.random() * (upperBound - lowerBound)) + lowerBound;
    }

    public static void main(String[] args) {
        int numberOfNodes = 3;
        int port = generateRandomPort(ServerNode.SERVERS_PORT + 1, ServerNode.SERVERS_PORT + numberOfNodes + 1);
        reader = new BufferedReader(new InputStreamReader(System.in));

        try (Socket socket = new Socket("localhost", port)) {
            System.out.println("Client Started at port " + port);

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

            ClientRunnable clientRunnable = new ClientRunnable(socket);
            new Thread(clientRunnable).start();

            User user = new User("user", "pass", "Omar", "Muhammad");
            while (!socket.isClosed()) {
                getAndSendUserData(outputStream, user, socket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void getAndSendUserData(ObjectOutputStream outputStream, User user, Socket socket) throws IOException {
        System.out.println("1. Store File" +
                "\n2. Delete File" +
                "\n3. Retrieve File" +
                "\n4. List Files" +
                "\n5. Update File" +
                "\n0. Exit" +
                "\nChoose an operation:");
        String userInput = reader.readLine();

        switch (userInput) {
            case "1":
                System.out.println("Enter file name: ");
                String fileName = reader.readLine();

                fileName = "src/main/java/app/" + fileName;
                ServerFile file = new ServerFile(fileName);

                outputStream.writeUnshared(new Object[]{new FileOperations.StoreFile(), user, file});
                outputStream.flush();
                break;
            case "2":
                System.out.println("Enter file name: ");
                fileName = reader.readLine();

                outputStream.writeUnshared(new Object[]{new FileOperations.DeleteFile(), user, fileName});
                outputStream.flush();
                break;
            case "3":
                System.out.println("Enter file name: ");
                fileName = reader.readLine();

                outputStream.writeUnshared(new Object[]{new FileOperations.RetrieveFile(), user, fileName});
                outputStream.flush();
                break;
            case "4":
                outputStream.writeUnshared(new Object[]{new FileOperations.ListFiles(), user});
                outputStream.flush();
                break;
            case "5":
                System.out.println("Unsupported operation");
                break;
            case "0":
                outputStream.close();
                socket.close();
                break;
        }
    }

}
