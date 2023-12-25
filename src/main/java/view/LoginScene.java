package view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class LoginScene implements Scene {


    private Label titleLabel;
    private Label usernameLabel;
    private Label passwordLabel;

    private TextField usernameField;
    private TextField passwordField;

    private Button loginButton;
    private Button registerButton;

    private GridPane gridPane;

    public LoginScene() {
        initializeControls();
        renderScene();
        applyStyles();
        addActions();
    }

    @Override
    public void initializeControls() {
        titleLabel = new Label("Distributed File Sharing System");
        usernameLabel = new Label("Username");
        passwordLabel = new Label("Password");
        usernameField = new TextField();
        passwordField = new TextField();

        loginButton = new Button("Login");
        registerButton = new Button("Register");

        gridPane = new GridPane();
    }

    @Override
    public void renderScene() {
        gridPane.add(titleLabel, 0, 0, 2, 1);
        gridPane.add(usernameLabel, 0, 1);
        gridPane.add(usernameField, 1, 1);
        gridPane.add(passwordLabel, 0, 2);
        gridPane.add(passwordField, 1, 2);
        gridPane.add(loginButton, 0, 3);
        gridPane.add(registerButton, 1, 3);

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(javafx.geometry.Pos.CENTER);
    }

    @Override
    public void applyStyles() {

    }

    @Override
    public void addActions() {

    }

    @Override
    public javafx.scene.Scene getScene() {
        return new javafx.scene.Scene(gridPane, WIDTH, HEIGHT);
    }

    @Override
    public javafx.scene.Node getAsElement() {
        return gridPane;
    }
}
