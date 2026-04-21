package com.example.nexusbooking.desktop.controller;

import com.example.nexusbooking.desktop.App;
import com.example.nexusbooking.desktop.api.ApiException;
import com.example.nexusbooking.desktop.model.UserResponse;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class LoginController {

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
}
