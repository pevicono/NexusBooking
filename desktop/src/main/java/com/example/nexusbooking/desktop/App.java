package com.example.nexusbooking.desktop;

import com.example.nexusbooking.desktop.api.ApiClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;

public class App extends Application {

    private static Stage primaryStage;
    private static final ApiClient apiClient = new ApiClient();

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        stage.setTitle("NexusBooking");
        stage.setResizable(true);
        try {
            Image source = new Image(getClass().getResourceAsStream("/com/example/nexusbooking/desktop/images/logo.png"));
            Image icon = createTransparentAppIcon(source);
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Error loading app icon: " + e.getMessage());
        }
        showLogin();
        stage.show();
    }

    private Image createTransparentAppIcon(Image source) {
        if (source == null || source.getPixelReader() == null) {
            return source;
        }

        int width = (int) Math.round(source.getWidth());
        int height = (int) Math.round(source.getHeight());
        if (width <= 0 || height <= 0) {
            return source;
        }

        PixelReader reader = source.getPixelReader();
        WritableImage transparent = new WritableImage(width, height);
        PixelWriter writer = transparent.getPixelWriter();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setColor(x, y, reader.getColor(x, y));
            }
        }

        Color background = reader.getColor(0, 0);
        double tolerance = 0.10;
        boolean[] visited = new boolean[width * height];
        ArrayDeque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{0, 0});
        queue.add(new int[]{width - 1, 0});
        queue.add(new int[]{0, height - 1});
        queue.add(new int[]{width - 1, height - 1});

        while (!queue.isEmpty()) {
            int[] point = queue.poll();
            int x = point[0];
            int y = point[1];

            if (x < 0 || y < 0 || x >= width || y >= height) {
                continue;
            }

            int idx = y * width + x;
            if (visited[idx]) {
                continue;
            }
            visited[idx] = true;

            Color color = reader.getColor(x, y);
            if (!isNearColor(color, background, tolerance)) {
                continue;
            }

            writer.setColor(x, y, Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.0));
            queue.add(new int[]{x + 1, y});
            queue.add(new int[]{x - 1, y});
            queue.add(new int[]{x, y + 1});
            queue.add(new int[]{x, y - 1});
        }

        return transparent;
    }

    private boolean isNearColor(Color color, Color target, double tolerance) {
        return Math.abs(color.getRed() - target.getRed()) <= tolerance
                && Math.abs(color.getGreen() - target.getGreen()) <= tolerance
                && Math.abs(color.getBlue() - target.getBlue()) <= tolerance;
    }

    public static void showLogin() throws Exception {
        loadScene("/com/example/nexusbooking/desktop/fxml/login.fxml", 420, 620, 470, 720);
    }

    public static void showProfile() throws Exception {
        loadScene("/com/example/nexusbooking/desktop/fxml/profile.fxml", 760, 580, 900, 620);
    }

    public static void showHome() throws Exception {
        loadScene("/com/example/nexusbooking/desktop/fxml/home.fxml", 720, 560, 1040, 720);
    }

    public static void showBackoffice() throws Exception {
        loadScene("/com/example/nexusbooking/desktop/fxml/backoffice.fxml", 760, 580, 1100, 760);
    }

    private static void loadScene(String resourcePath, double minWidth, double minHeight, double targetWidth, double targetHeight) throws IOException {
        URL resource = App.class.getResource(resourcePath);
        if (resource == null) {
            throw new IOException("FXML resource not found: " + resourcePath);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(minWidth);
        primaryStage.setMinHeight(minHeight);

        if (primaryStage.getWidth() < targetWidth) {
            primaryStage.setWidth(targetWidth);
        }
        if (primaryStage.getHeight() < targetHeight) {
            primaryStage.setHeight(targetHeight);
        }

        primaryStage.centerOnScreen();
    }

    public static ApiClient getApiClient() {
        return apiClient;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
