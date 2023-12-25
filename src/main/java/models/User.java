package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private static int userCount = 0;
    private final int USER_ID;
    private final String username;
    private final String password;
    private final String firstName;
    private final String lastName;

    private final List<ServerFile> files;

    public User(String username, String password, String firstName, String lastName) {
        userCount++;

        // User ID is system assigned
        this.USER_ID = userCount;

        validateString(username, "Username");
        validateString(password, "Password");
        validateString(firstName, "First name");
        validateString(lastName, "Last name");

        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.files = new ArrayList<>();
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

    public List<ServerFile> getFiles() {
        return this.files;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }
}
