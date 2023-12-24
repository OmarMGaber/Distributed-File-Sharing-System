package servers;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
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
    public void sendToAll(String message) throws IOException {

    }

    @Override
    public void run() {

    }
}
