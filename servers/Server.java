package servers;

import java.io.IOException;
import java.util.List;

public interface Server {
    int getPort();

    List<ServerNode> getServerPeers();

    void sendToAll(String message) throws IOException;

    void run();

}
