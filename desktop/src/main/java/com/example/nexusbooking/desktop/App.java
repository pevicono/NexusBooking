package com.example.nexusbooking.desktop;

import com.example.nexusbooking.desktop.api.ApiClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class App extends Application {

    private static Stage primaryStage;
    private static final ApiClient apiClient = new ApiClient();

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        stage.setTitle("NexusBooking");
        stage.setResizable(false);
        showLogin();
        stage.show();
    }

    public static void showLogin() throws Exception {
        loadScene("/com/example/nexusbooking/desktop/fxml/login.fxml", 480, 560);
    }

    public static void showProfile() throws Exception {
        loadScene("/com/example/nexusbooking/desktop/fxml/profile.fxml", 560, 520);
    }

    public static void showHome() throws Exception {
        loadScene("/com/example/nexusbooking/desktop/fxml/home.fxml", 920, 640);
    }

    public static void showBackoffice() throws Exception {
        loadScene("/com/example/nexusbooking/desktop/fxml/backoffice.fxml", 980, 680);
    }

    private static void loadScene(String resourcePath, int width, int height) throws IOException {
        URL resource = App.class.getResource(resourcePath);
        if (resource == null) {
            throw new IOException("FXML resource not found: " + resourcePath);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Scene scene = new Scene(loader.load(), width, height);
        primaryStage.setScene(scene);
    }

    public static ApiClient getApiClient() {
        return apiClient;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
