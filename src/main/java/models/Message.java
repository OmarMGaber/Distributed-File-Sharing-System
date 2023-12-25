package models;

import java.io.Serializable;

public record Message(String id, String message) implements Serializable {

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
