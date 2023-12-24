package servers;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public abstract class Server {
    public abstract int getPort();

    public abstract List<ServerNode> getServerPeers();

    public abstract void sendToAll(String message) throws IOException;

    public abstract void run();

}
