package servers;

import models.ServerFile;
import models.User;
import operations.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface Server extends FileManager {
    int getPort();

    List<ServerNode> getServerPeers();
    HashMap<User, ArrayList<ServerFile>> getUsersFilesTable();

    void sendToAll(String message) throws IOException;

    void run();

}
