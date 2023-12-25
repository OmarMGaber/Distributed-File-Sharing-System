package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

public class ServerFile implements Serializable {

    private String fileName;
    private String fileFullName;
    private String extension;
    private byte[] content;

    public ServerFile(String filePath) throws IOException {
        File file = new File(filePath);
        validateFile(file);
        this.fileFullName = file.getName();
        this.fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));
        this.extension = getExtension(fileName);
        this.content = readFileContent(file);
    }

    private void validateFile(File file) throws IOException {
        if (!file.exists()) {
            throw new IOException("File not found: " + file.getAbsolutePath());
        }
        if (!file.isFile()) {
            throw new IOException("Not a file: " + file.getAbsolutePath());
        }
    }

    private String getExtension(String name) {
        int dotIndex = name.lastIndexOf('.');
        return dotIndex == -1 ? "" : name.substring(dotIndex + 1);
    }

    private byte[] readFileContent(File file) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] content = new byte[(int) file.length()];
            fileInputStream.read(content);
            return content;
        }
    }

    public String getFileName() {
        return fileName;
    }

    public String getExtension() {
        return extension;
    }

    public byte[] getContent() {
        return content;
    }

    public String getFileFullName() {
        return fileFullName;
    }

    public void setFileFullName(String newFileFullName) {
        this.fileFullName = newFileFullName;
    }

    public void setContent(byte[] newContent) {
        this.content = newContent;
    }

    public void setFileName(String newFileName) {
        this.fileName = newFileName;
    }

    public void setExtension(String newExtension) {
        this.extension = newExtension;
    }

    @Override
    public String toString() {
        return "File name: " + this.fileName + "\n" +
                "File extension: " + this.extension + "\n" +
                "File content: " + new String(this.content);
    }

    @Override
    public int hashCode() {
        return this.fileFullName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof ServerFile)) return false;
        ServerFile otherFile = (ServerFile) obj;
        return this.fileFullName.equals(otherFile.fileFullName);
    }
}