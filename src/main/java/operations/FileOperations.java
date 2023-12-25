package operations;

import servers.Server;
import models.ServerFile;
import models.User;

public class FileOperations {
    private FileOperations() {
    } // Prevents instantiation

    public enum Status {
        FILE_NOT_FOUND,
        FILE_ALREADY_EXISTS,
        FILE_STORED,
        FILE_DELETED,
        FILE_UPDATED,
        FILE_NOT_UPDATED,
        FILE_NOT_DELETED,
        FILE_NOT_RETRIEVED,
        FILE_NOT_STORED,
    }


    public static class RetrieveFile implements ServerOperation {
        @Override
        public Object execute(Server server, Object[] objects) {
            return server.retrieveFile((User) objects[1], (String) objects[2]); // User, fileName
        }
    }

    public static class StoreFile implements ServerOperation {
        @Override
        public Object execute(Server server, Object[] objects) {
            return server.storeFile((User) objects[1], (ServerFile) objects[2]); // User, file
        }
    }

    public static class DeleteFile implements ServerOperation {
        @Override
        public Object execute(Server server, Object[] objects) {
            return server.deleteFile((User) objects[1], (String) objects[2]); // User, fileName
        }
    }

    public static class ListFiles implements ServerOperation {
        @Override
        public Object execute(Server server, Object[] objects) {
            return server.listFiles((User) objects[1]); // User
        }
    }

    public static class UpdateFile implements ServerOperation {
        @Override
        public Object execute(Server server, Object[] objects) {
            return server.updateFile((User) objects[1], (String) objects[2], (ServerFile) objects[3]); // User, fileName, newFile
        }
    }
}