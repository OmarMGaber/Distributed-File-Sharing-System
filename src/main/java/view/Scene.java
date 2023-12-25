package view;

import javafx.scene.Node;

public interface Scene {

    final int WIDTH = 800, HEIGHT = 600;

    void initializeControls();

    void renderScene();

    void applyStyles();

    void addActions();

    javafx.scene.Scene getScene();

    Node getAsElement();
}