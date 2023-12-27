package files;

import models.ServerFile;
import models.User;

import java.util.ArrayList;

public interface FileOperationsManager {
    boolean storeFile(User user, ServerFile serverFile);

    ServerFile retrieveFile(User user, String fileFullName);

    boolean deleteFile(User user, String fileFullName);

    ArrayList<ServerFile> listFiles(User user);
}
