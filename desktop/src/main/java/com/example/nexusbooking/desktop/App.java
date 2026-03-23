package com.example.nexusbooking.desktop;

import com.example.nexusbooking.desktop.api.ApiClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/login.fxml"));
        Scene scene = new Scene(loader.load(), 480, 560);
        primaryStage.setScene(scene);
    }

    public static void showProfile() throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/profile.fxml"));
        Scene scene = new Scene(loader.load(), 560, 520);
        primaryStage.setScene(scene);
    }

    public static ApiClient getApiClient() {
        return apiClient;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
