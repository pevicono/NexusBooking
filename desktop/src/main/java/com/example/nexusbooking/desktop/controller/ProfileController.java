package com.example.nexusbooking.desktop.controller;

import com.example.nexusbooking.desktop.App;
import com.example.nexusbooking.desktop.api.ApiException;
import com.example.nexusbooking.desktop.model.UserResponse;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    @FXML private Label topEmail;
    @FXML private Label lblEmail;
    @FXML private Label lblRole;
    @FXML private Label lblId;

    @FXML private VBox viewPane;
    @FXML private VBox editPane;
    @FXML private VBox passwordPane;

    @FXML private TextField editEmail;
    @FXML private Label editError;
    @FXML private Label editSuccess;

    @FXML private PasswordField currentPassword;
    @FXML private PasswordField newPassword;
    @FXML private PasswordField confirmPassword;
    @FXML private Label passwordError;
    @FXML private Label passwordSuccess;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadProfile();
    }

    private void show(VBox pane) {
        for (VBox p : new VBox[]{viewPane, editPane, passwordPane}) {
            p.setVisible(p == pane);
            p.setManaged(p == pane);
        }
    }

    private void loadProfile() {
        runAsync(() -> {
            try {
                UserResponse user = App.getApiClient().getCurrentUser();
                Platform.runLater(() -> {
                    lblEmail.setText(user.getEmail());
                    lblRole.setText(user.getRole());
                    lblId.setText(String.valueOf(user.getId()));
                    editEmail.setText(user.getEmail());
                    topEmail.setText(user.getEmail());
                });
            } catch (Exception e) {
                Platform.runLater(() -> lblEmail.setText("Failed to load profile."));
            }
        });
    }

    @FXML private void showView() { show(viewPane); }

    @FXML
    private void showEdit() {
        editError.setText("");
        editSuccess.setText("");
        show(editPane);
    }

    @FXML
    private void showChangePassword() {
        currentPassword.clear();
        newPassword.clear();
        confirmPassword.clear();
        passwordError.setText("");
        passwordSuccess.setText("");
        show(passwordPane);
    }

    @FXML
    private void doSaveEmail() {
        editError.setText("");
        editSuccess.setText("");
        String newEmail = editEmail.getText().trim();
        if (newEmail.isEmpty()) { editError.setText("Email cannot be empty."); return; }
        if (!newEmail.matches("^[^@]+@[^@]+\\.[^@]+$")) { editError.setText("Please enter a valid email address."); return; }
        if (newEmail.length() > 50) { editError.setText("Email must be at most 50 characters."); return; }

        runAsync(() -> {
            try {
                UserResponse updated = App.getApiClient().updateEmail(newEmail);
                Platform.runLater(() -> {
                    lblEmail.setText(updated.getEmail());
                    topEmail.setText(updated.getEmail());
                    editEmail.setText(updated.getEmail());
                    editSuccess.setText("Email updated successfully.");
                });
            } catch (ApiException e) {
                Platform.runLater(() -> editError.setText(e.getMessage()));
            } catch (Exception e) {
                Platform.runLater(() -> editError.setText("Could not connect to server."));
            }
        });
    }

    @FXML
    private void doChangePassword() {
        passwordError.setText("");
        passwordSuccess.setText("");
        String current = currentPassword.getText();
        String newPwd = newPassword.getText();
        String confirm = confirmPassword.getText();

        if (current.isEmpty()) { passwordError.setText("Please enter your current password."); return; }
        if (newPwd.isEmpty()) { passwordError.setText("Please enter a new password."); return; }
        if (newPwd.length() < 6) { passwordError.setText("New password must be at least 6 characters."); return; }
        if (newPwd.length() > 40) { passwordError.setText("New password must be at most 40 characters."); return; }
        if (!newPwd.equals(confirm)) { passwordError.setText("Passwords do not match."); return; }

        runAsync(() -> {
            try {
                App.getApiClient().changePassword(current, newPwd);
                Platform.runLater(() -> {
                    currentPassword.clear();
                    newPassword.clear();
                    confirmPassword.clear();
                    passwordSuccess.setText("Password changed successfully.");
                });
            } catch (ApiException e) {
                Platform.runLater(() -> passwordError.setText(e.getMessage()));
            } catch (Exception e) {
                Platform.runLater(() -> passwordError.setText("Could not connect to server."));
            }
        });
    }

    @FXML
    private void doLogout() {
        runAsync(() -> {
            try { App.getApiClient().logout(); } catch (Exception ignored) {}
            finally {
                Platform.runLater(() -> {
                    try { App.showLogin(); } catch (Exception e) { e.printStackTrace(); }
                });
            }
        });
    }

    private void runAsync(Runnable task) {
        Thread worker = new Thread(task, "desktop-profile-async");
        worker.setDaemon(true);
        worker.start();
    }
}


