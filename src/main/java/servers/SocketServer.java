package servers;

import models.ServerFile;
import models.User;
import operations.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SocketServer implements Server {
    class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final ObjectInputStream objectInputStream;
        private final ObjectOutputStream objectOutputStream;

        ClientHandler(Socket clientSocket) throws IOException {
            this.clientSocket = clientSocket;
            try {
                this.objectInputStream = new ObjectInputStream(this.clientSocket.getInputStream());
                this.objectOutputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            try {
                ServerOperations serverOperations = new ConcreteServerOperations();
                while (!clientSocket.isClosed()) {
                    Object[] objects = (Object[]) objectInputStream.readObject();

                    System.out.println("Received " + objects[0] + " from client " + clientSocket.getInetAddress() + " on port " + clientSocket.getPort());

                    ServerOperation operation = (ServerOperation) objects[0];
                    Object result = serverOperations.preformOperation(SocketServer.this, operation, objects);

                    objectOutputStream.writeUnshared(result);
                    objectOutputStream.flush();
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private final int PORT;
    private ServerSocket serverSocket;
    private final HashMap<User, ArrayList<ServerFile>> usersFilesTable;
    private final List<ServerNode> serverPeers;
    private final List<Socket> clientSockets;

    SocketServer(int port) {
        this.PORT = port;
        this.clientSockets = Collections.synchronizedList(new ArrayList<>());
        this.serverPeers = Collections.synchronizedList(new ArrayList<>());
        this.usersFilesTable = new HashMap<User, ArrayList<ServerFile>>();
    }

    public int getPort() {
        return this.PORT;
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }


    public List<ServerNode> getServerPeers() {
        return this.serverPeers;
    }

    @Override
    public HashMap<User, ArrayList<ServerFile>> getUsersFilesTable() {
        return this.usersFilesTable;
    }

    public List<Socket> getClientSockets() {
        return this.clientSockets;
    }

    public void sendToAll(String message) throws IOException {
        for (Socket socket : this.clientSockets) {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(message);
        }
    }


    public void run() {
        try {
            this.serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running on port " + this.PORT);

            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                this.clientSockets.add(clientSocket);
                System.out.println("Client connected on port " + this.PORT);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean containsUser(User user) {
        return this.usersFilesTable.containsKey(user);
    }

    @Override
    public Boolean storeFile(User user, ServerFile file) {
        if (this.usersFilesTable.containsKey(user)) {
            if (this.usersFilesTable.get(user).contains(file)) {
                return false;
            }
        }

        this.usersFilesTable.computeIfAbsent(user, k -> new ArrayList<>());
        return this.usersFilesTable.get(user).add(file);
    }

    @Override
    public Boolean deleteFile(User user, String fileName) {
        if (this.usersFilesTable.containsKey(user)) {
            for (ServerFile file : this.usersFilesTable.get(user)) {
                System.out.printf("Comparing %s with %s\n", file.getFileFullName(), fileName);
                if (file.getFileFullName().equals(fileName)) {
                    System.out.printf("Removing file %s\n", file.getFileFullName());
                    return this.usersFilesTable.get(user).remove(file);
                }
            }
        }
        return false;
    }

    @Override
    public ServerFile retrieveFile(User user, String fileName) {
        if (this.usersFilesTable.containsKey(user)) {
            for (ServerFile file : this.usersFilesTable.get(user)) {
                if (file.getFileFullName().equals(fileName)) {
                    return file;
                }
            }
        }
        return null;
    }

    @Override
    public ArrayList<ServerFile> listFiles(User user) {
        ArrayList<ServerFile> x = this.usersFilesTable.getOrDefault(user, null);
        for (ServerFile file : x) {
            System.out.println(file.getFileFullName());
        }
        return x;
    }

    @Override
    public Boolean updateFile(User user, String fileName, ServerFile newFile) {
        if (this.usersFilesTable.containsKey(user)) {
            for (ServerFile file : this.usersFilesTable.get(user)) {
                if (file.getFileFullName().equals(fileName)) {
                    this.usersFilesTable.get(user).remove(file);
                    this.usersFilesTable.get(user).add(newFile);
                    return true;
                }
            }
        }
        return false;
    }
}
