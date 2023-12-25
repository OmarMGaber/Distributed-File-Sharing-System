package operations;

import models.User;
import models.ServerFile;
import operations.FileOperations;

import java.util.ArrayList;
import java.util.List;

public interface FileManager {
    Boolean containsUser(User user);

    Boolean storeFile(User user, ServerFile file);

    Boolean deleteFile(User user, String fileName);

    ServerFile retrieveFile(User user, String fileName);

    ArrayList<ServerFile> listFiles(User user);

    Boolean updateFile(User user, String fileName, ServerFile newFile);
}
