package servers;

import models.ServerFile;
import models.User;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RMIServer extends UnicastRemoteObject implements Server { // implements ServerOperations interface
    private final int PORT;
    private List<ServerNode> serverPeers;

    protected RMIServer(int Port) throws RemoteException {
        super();
        this.PORT = Port;
    }

    @Override
    public int getPort() {
        return this.PORT;
    }

    @Override
    public List<ServerNode> getServerPeers() {
        return this.serverPeers;
    }

    @Override
    public HashMap<User, ArrayList<ServerFile>> getUsersFilesTable() {
        return null;
    }

    @Override
    public void sendToAll(String message) throws IOException {

    }

    @Override
    public void run() {

    }

    @Override
    public Boolean containsUser(User user) {
        return false;
    }

    @Override
    public Boolean storeFile(User user, ServerFile file) {
        return false;
    }

    @Override
    public Boolean deleteFile(User user, String fileName) {
        return false;
    }

    @Override
    public ServerFile retrieveFile(User user, String fileName) {
        return null;
    }

    @Override
    public ArrayList<ServerFile> listFiles(User user) {
        return null;
    }

    @Override
    public Boolean updateFile(User user, String fileName, ServerFile newFile) {
        return false;
    }
}
