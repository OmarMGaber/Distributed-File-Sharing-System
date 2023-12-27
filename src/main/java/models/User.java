package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class User implements Serializable {
    private static int userCount = 0;
//    private final int USER_ID;
    private final String username;

    private final HashSet<ServerFile> files;

    public User(String username) {
        userCount++;

        // User ID is system assigned
//        this.USER_ID = userCount;

        validateString(username, "Username");

        this.username = username;
        this.files = new HashSet<>();
    }

    private void validateString(String string, String fieldName) {
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User otherUser = (User) obj;
        return username.equals(otherUser.username);
    }

    public boolean addFile(ServerFile file) {
        return this.files.add(file);
    }

    public boolean removeFile(ServerFile file) {
        return this.files.remove(file);
    }

    public HashSet<ServerFile> getFiles() {
        return this.files;
    }

    public String getUsername() {
        return this.username;
    }

}
