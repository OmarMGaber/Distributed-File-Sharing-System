module ds.dfss.distributedfilesharingsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;

    opens app to javafx.fxml;
    exports app;
    exports server;
    exports client;
    exports models;
    exports managers;
    exports operations;
}