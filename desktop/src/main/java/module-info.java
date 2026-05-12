module com.example.nexusbooking.desktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires com.google.gson;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    opens com.example.nexusbooking.desktop to javafx.fxml;
    opens com.example.nexusbooking.desktop.controller to javafx.fxml;
    opens com.example.nexusbooking.desktop.model to com.google.gson;

    exports com.example.nexusbooking.desktop;
    exports com.example.nexusbooking.desktop.api;
}
