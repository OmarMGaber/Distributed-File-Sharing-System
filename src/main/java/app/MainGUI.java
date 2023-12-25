package app;

import javafx.application.Application;
import javafx.stage.Stage;
import view.LoginScene;

public class MainGUI extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Distributed File Sharing System");
        stage.setScene(new LoginScene().getScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
