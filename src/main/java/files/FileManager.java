package files;

import models.ServerFile;
import models.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileManager {
    private FileManager() {
    }

    public static boolean saveFileToDirectory(ServerFile serverFile, String directoryPath) {
        try {
            File file = new File(directoryPath + serverFile.getFileFullName());
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            fileOutputStream.write(serverFile.getContent());
            fileOutputStream.flush();
            fileOutputStream.close();

            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            return false;
        }
    }
}
