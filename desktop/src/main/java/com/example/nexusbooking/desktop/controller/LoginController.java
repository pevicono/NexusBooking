package com.example.nexusbooking.desktop.controller;

import com.example.nexusbooking.desktop.App;
import com.example.nexusbooking.desktop.api.ApiException;
import com.example.nexusbooking.desktop.model.UserResponse;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML private ImageView logoImage;
    @FXML private Button tabLogin;
    @FXML private Button tabRegister;

    @FXML private VBox loginPane;
    @FXML private TextField loginEmail;
    @FXML private PasswordField loginPassword;
    @FXML private Label loginError;

    @FXML private VBox registerPane;
    @FXML private TextField regEmail;
    @FXML private PasswordField regPassword;
    @FXML private PasswordField regConfirm;
    @FXML private Label regError;

    @FXML
    private void initialize() {
        if (logoImage != null) {
            Image source = new Image(getClass().getResourceAsStream("/com/example/nexusbooking/desktop/images/logo.png"));
            logoImage.setImage(createWhiteLogo(source));
        }
        showLogin();
    }

    @FXML
    private void showLogin() {
        loginPane.setVisible(true);
        loginPane.setManaged(true);
        registerPane.setVisible(false);
        registerPane.setManaged(false);
        tabLogin.getStyleClass().add("tab-active");
        tabRegister.getStyleClass().remove("tab-active");
    }

    @FXML
    private void showRegister() {
        loginPane.setVisible(false);
        loginPane.setManaged(false);
        registerPane.setVisible(true);
        registerPane.setManaged(true);
        tabRegister.getStyleClass().add("tab-active");
        tabLogin.getStyleClass().remove("tab-active");
    }

    @FXML
    private void doLogin() {
        loginError.setText("");
        String email = loginEmail.getText().trim();
        String password = loginPassword.getText();

        if (email.isEmpty()) { loginError.setText("Please enter your email."); return; }
        if (!email.matches("^[^@]+@[^@]+\\.[^@]+$")) { loginError.setText("Please enter a valid email address."); return; }
        if (password.isEmpty()) { loginError.setText("Please enter your password."); return; }

        runAsync(() -> {
            try {
                App.getApiClient().login(email, password);
                UserResponse user = App.getApiClient().getCurrentUser();
                Platform.runLater(() -> {
                    try {
                        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                            App.showBackoffice();
                        } else {
                            App.showHome();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        String detail = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
                        loginError.setText("Failed to open dashboard: " + detail);
                    }
                });
            } catch (ApiException e) {
                Platform.runLater(() -> loginError.setText(e.getMessage()));
            } catch (Exception e) {
                Platform.runLater(() -> loginError.setText("Could not connect to server."));
            }
        });
    }

    @FXML
    private void doRegister() {
        regError.setText("");
        regError.setStyle("");
        String email = regEmail.getText().trim();
        String password = regPassword.getText();
        String confirm = regConfirm.getText();

        if (email.isEmpty()) { regError.setText("Please enter an email address."); return; }
        if (!email.matches("^[^@]+@[^@]+\\.[^@]+$")) { regError.setText("Please enter a valid email address."); return; }
        if (password.isEmpty()) { regError.setText("Please enter a password."); return; }
        if (password.length() < 6) { regError.setText("Password must be at least 6 characters."); return; }
        if (password.length() > 40) { regError.setText("Password must be at most 40 characters."); return; }
        
        String strengthMsg = validatePasswordStrength(password);
        if (strengthMsg != null) { 
            regError.setText(strengthMsg);
            regError.setStyle("-fx-text-fill: orange;");
            // Still allow registration but warn user
        }
        
        if (!password.equals(confirm)) { regError.setText("Passwords do not match."); return; }

        runAsync(() -> {
            try {
                App.getApiClient().register(email, password);
                Platform.runLater(() -> {
                    regError.setStyle("-fx-text-fill: green;");
                    regError.setText("Registered! You can now login.");
                    showLogin();
                });
            } catch (ApiException e) {
                Platform.runLater(() -> regError.setText(e.getMessage()));
            } catch (Exception e) {
                Platform.runLater(() -> regError.setText("Could not connect to server."));
            }
        });
    }

    @FXML
    private void showForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText("Password Recovery");
        alert.setContentText(
            "To reset your password, please contact your system administrator.\n\n" +
            "Admin email: admin@nexusbooking.com\n\n" +
            "The administrator can reset your password via the Swagger UI at:\n" +
            "http://<server-ip>:8080/swagger-ui.html"
        );
        alert.showAndWait();
    }

    private void runAsync(Runnable task) {
        Thread worker = new Thread(task, "desktop-login-async");
        worker.setDaemon(true);
        worker.start();
    }

    private WritableImage createWhiteLogo(Image source) {
        int width = (int) source.getWidth();
        int height = (int) source.getHeight();
        WritableImage output = new WritableImage(width, height);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                double max = Math.max(color.getRed(), Math.max(color.getGreen(), color.getBlue()));
                double min = Math.min(color.getRed(), Math.min(color.getGreen(), color.getBlue()));
                double saturation = max <= 0 ? 0 : (max - min) / max;
                double luminance = (color.getRed() + color.getGreen() + color.getBlue()) / 3.0;

                double alphaFromLuma = (1.0 - luminance) * 2.9;
                double alphaFromSaturation = saturation * 2.1;
                double alpha = Math.max(alphaFromLuma, alphaFromSaturation) * color.getOpacity();
                alpha = Math.max(0.0, Math.min(1.0, alpha));

                if (alpha < 0.12) {
                    writer.setColor(x, y, Color.TRANSPARENT);
                } else {
                    writer.setColor(x, y, new Color(1, 1, 1, alpha));
                }
            }
        }

        return output;
    }
    
    /**
        * Validate password strength
     * Returns warning message if password is weak, null if strong
     */
    private String validatePasswordStrength(String password) {
        if (password.length() < 8) {
            return "Warning: Password should be at least 8 characters for better security";
        }
        
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasDigits = password.matches(".*\\d.*");
        boolean hasSpecialChars = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
        
        int strength = 0;
        if (hasUppercase) strength++;
        if (hasLowercase) strength++;
        if (hasDigits) strength++;
        if (hasSpecialChars) strength++;
        
        if (strength < 2) {
            return "Note: Password should include uppercase, lowercase, and numbers for better security";
        }
        
        return null;  // Password is strong enough
    }
}
