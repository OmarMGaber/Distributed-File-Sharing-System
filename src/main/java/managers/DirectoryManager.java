package managers;

import models.ServerFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DirectoryManager {
    private DirectoryManager() {
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

    public static boolean directoryHasFile(String serverFileName, String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files == null) {
            return false;
        }

        for (File file : files) {
            if (file.getName().equals(serverFileName)) {
                return true;
            }
        }

        return false;
    }

    public static boolean deleteFileFromDirectory(String serverFileName, String directoryPath) {
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        if (files == null) {
            return false;
        }

        for (File file : files) {
            if (file.getName().equals(serverFileName)) {
                return file.delete();
            }
        }

        return false;
    }
}
