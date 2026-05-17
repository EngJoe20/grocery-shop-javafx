module client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;

    opens   client to javafx.fxml, javafx.graphics;
    exports client to javafx.graphics;
}