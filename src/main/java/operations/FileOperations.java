package operations;

import models.ServerFile;
import models.User;

import java.io.Serializable;
import java.util.HashSet;

public interface FileOperations {
    public static enum OperationType implements Serializable {
        STORE_FILE,
        RETRIEVE_FILE,
        DELETE_FILE,
        LIST_FILES,
    }

    boolean storeFile(User user, ServerFile serverFile);

    ServerFile retrieveFile(User user, String fileFullName);

    boolean deleteFile(User user, String fileFullName);

    HashSet<ServerFile> listFiles(User user);
}
