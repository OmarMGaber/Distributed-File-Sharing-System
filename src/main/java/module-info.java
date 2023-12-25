module ds.dfss.distributedfilesharingsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;

    opens app to javafx.fxml;
    exports app;
    exports view;
    exports servers;
    exports client;
    exports models;
    exports operations;
}