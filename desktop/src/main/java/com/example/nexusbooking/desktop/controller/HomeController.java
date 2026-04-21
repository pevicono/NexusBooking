package com.example.nexusbooking.desktop.controller;

import com.example.nexusbooking.desktop.App;
import com.example.nexusbooking.desktop.api.ApiException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HomeController {

    @FXML private Label welcomeLabel;
    @FXML private Label upcomingCountLabel;
    @FXML private Label groupsCountLabel;
    @FXML private Label messageLabel;
    @FXML private VBox bookingsCardsContainer;
    @FXML private VBox groupsCardsContainer;
    @FXML private TextField joinGroupId;

    private Long currentUserId;

    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter UI_DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private void initialize() {
        refreshAll();
    }

    @FXML
    private void refreshAll() {
        messageLabel.setText("");
        runAsync(() -> {
            try {
                JsonArray bookings = App.getApiClient().getMyBookings();
                JsonArray groups = App.getApiClient().getMyGroups();
                Platform.runLater(() -> {
                    try {
                        var currentUser = App.getApiClient().getCurrentUser();
                        currentUserId = currentUser.getId();
                        welcomeLabel.setText("Benvingut/da: " + currentUser.getEmail());
                    } catch (Exception ignored) {}

                    renderBookingCards(bookings);
                    renderGroupCards(groups);

                    long upcomingCount = bookings.asList().stream().filter(e -> {
                        JsonObject b = e.getAsJsonObject();
                        if (!b.has("startTime")) return false;
                        try {
                            return LocalDateTime.parse(b.get("startTime").getAsString(), ISO_DATE_TIME)
                                    .isAfter(LocalDateTime.now());
                        } catch (Exception ex) {
                            return false;
                        }
                    }).count();

                    upcomingCountLabel.setText(String.valueOf(upcomingCount));
                    groupsCountLabel.setText(String.valueOf(groups.size()));
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    private void renderBookingCards(JsonArray bookings) {
        bookingsCardsContainer.getChildren().clear();

        if (bookings.isEmpty()) {
            bookingsCardsContainer.getChildren().add(new Label("No tens reserves encara."));
            return;
        }

        bookings.forEach(e -> {
            JsonObject b = e.getAsJsonObject();
            VBox card = new VBox(6);
            card.getStyleClass().add("card");
            card.setPadding(new Insets(10));

            String facility = b.has("facilityName") ? b.get("facilityName").getAsString() : "N/A";
            String group = b.has("groupName") && !b.get("groupName").isJsonNull() ? b.get("groupName").getAsString() : "Sense grup";
            String start = b.has("startTime") ? formatDateTime(b.get("startTime").getAsString()) : "-";
            String end = b.has("endTime") ? formatDateTime(b.get("endTime").getAsString()) : "-";
            String status = b.has("status") ? b.get("status").getAsString() : "CONFIRMED";

            Label title = new Label("Reserva #" + b.get("id").getAsLong() + " - " + facility);
            title.getStyleClass().add("section-subtitle");

            Label details = new Label("Grup: " + group + "\nInici: " + start + "\nFinal: " + end + "\nEstat: " + status);
            details.setWrapText(true);

            HBox actions = new HBox(8);
            Button editBtn = new Button("Editar");
            editBtn.getStyleClass().add("btn-secondary");
            Button cancelBtn = new Button("Cancel·lar");
            cancelBtn.getStyleClass().add("btn-danger");

            long bookingId = b.get("id").getAsLong();
            editBtn.setId("bookingEditBtn-" + bookingId);
            cancelBtn.setId("bookingCancelBtn-" + bookingId);
            editBtn.setOnAction(evt -> showEditBookingDialog(b));
            cancelBtn.setOnAction(evt -> cancelBooking(bookingId));

            actions.getChildren().addAll(editBtn, cancelBtn);
            card.getChildren().addAll(title, details, actions);
            bookingsCardsContainer.getChildren().add(card);
        });
    }

    private void renderGroupCards(JsonArray groups) {
        groupsCardsContainer.getChildren().clear();

        if (groups.isEmpty()) {
            groupsCardsContainer.getChildren().add(new Label("No formes part de cap grup."));
            return;
        }

        groups.forEach(e -> {
            JsonObject g = e.getAsJsonObject();
            VBox card = new VBox(6);
            card.getStyleClass().add("card");
            card.setPadding(new Insets(10));

            String name = g.has("name") ? g.get("name").getAsString() : "Grup";
            String desc = g.has("description") && !g.get("description").isJsonNull() ? g.get("description").getAsString() : "";
            String code = g.has("joinCode") && !g.get("joinCode").isJsonNull() ? g.get("joinCode").getAsString() : "-";
            int members = g.has("memberCount") ? g.get("memberCount").getAsInt() : 0;
            String memberList = buildMembersSummary(g);
            Long ownerId = g.has("ownerId") && !g.get("ownerId").isJsonNull() ? g.get("ownerId").getAsLong() : null;

            Label title = new Label(name + " (" + members + " membres)");
            title.getStyleClass().add("section-subtitle");
            String detailsText = "Codi: " + code
                    + (desc.isBlank() ? "" : "\n" + desc)
                    + (memberList.isBlank() ? "" : "\nMembres: " + memberList);
            Label details = new Label(detailsText);
            details.setWrapText(true);

            HBox actions = new HBox(8);
            Button editBtn = new Button("Editar");
            editBtn.getStyleClass().add("btn-secondary");
            long groupId = g.get("id").getAsLong();
            editBtn.setId("groupEditBtn-" + groupId);
            editBtn.setOnAction(evt -> showEditGroupDialog(g));
            actions.getChildren().add(editBtn);

                boolean canLeave = ownerId == null || currentUserId == null || !ownerId.equals(currentUserId);
            Button leaveBtn = new Button("Sortir del grup");
            leaveBtn.getStyleClass().add("btn-danger");
            leaveBtn.setId("groupLeaveBtn-" + groupId);
            leaveBtn.setDisable(!canLeave);
            leaveBtn.setOnAction(evt -> leaveGroup(groupId));
            actions.getChildren().add(leaveBtn);

            card.getChildren().addAll(title, details, actions);
            groupsCardsContainer.getChildren().add(card);
        });
    }

    private String buildMembersSummary(JsonObject group) {
        if (!group.has("members") || !group.get("members").isJsonArray()) {
            return "";
        }

        JsonArray members = group.getAsJsonArray("members");
        if (members.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        int maxVisible = 4;
        for (int i = 0; i < Math.min(maxVisible, members.size()); i++) {
            JsonObject member = members.get(i).getAsJsonObject();
            String email = member.has("email") ? member.get("email").getAsString() : "usuari";
            String role = member.has("role") ? member.get("role").getAsString() : "MEMBER";
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(email).append(" (").append(role).append(")");
        }
        if (members.size() > maxVisible) {
            sb.append(" +").append(members.size() - maxVisible).append(" més");
        }
        return sb.toString();
    }

    @FXML
    private void showCreateBookingDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Crear reserva");
        dialog.setHeaderText("Nova reserva");

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        ComboBox<String> facilityCombo = new ComboBox<>();
        ComboBox<String> groupCombo = new ComboBox<>();
        facilityCombo.setId("dialogFacilityCombo");
        groupCombo.setId("dialogGroupCombo");

        LocalDateTime suggestedStart = nextSlotFromNow();
        LocalDateTime suggestedEnd = suggestedStart.plusHours(1);

        DatePicker startDatePicker = new DatePicker(suggestedStart.toLocalDate());
        ComboBox<String> startTimeCombo = new ComboBox<>(buildTimeSlots());
        startTimeCombo.setValue(suggestedStart.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        DatePicker endDatePicker = new DatePicker(suggestedEnd.toLocalDate());
        ComboBox<String> endTimeCombo = new ComboBox<>(buildTimeSlots());
        endTimeCombo.setValue(suggestedEnd.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));

        TextField notesField = new TextField();
        notesField.setId("dialogBookingNotesField");
        notesField.setPromptText("Notes (opcional)");

        try {
            JsonArray facilities = App.getApiClient().getFacilities();
            facilities.forEach(el -> {
                JsonObject obj = el.getAsJsonObject();
                facilityCombo.getItems().add(obj.get("id").getAsLong() + " - " + obj.get("name").getAsString());
            });

            JsonArray groups = App.getApiClient().getMyGroups();
            groups.forEach(el -> {
                JsonObject obj = el.getAsJsonObject();
                String code = obj.has("joinCode") && !obj.get("joinCode").isJsonNull() ? obj.get("joinCode").getAsString() : "-";
                groupCombo.getItems().add(obj.get("id").getAsLong() + " - " + obj.get("name").getAsString() + " (" + code + ")");
            });

            if (!facilityCombo.getItems().isEmpty()) {
                facilityCombo.setValue(facilityCombo.getItems().get(0));
            }
            if (!groupCombo.getItems().isEmpty()) {
                groupCombo.setValue(groupCombo.getItems().get(0));
            }
        } catch (Exception ex) {
            messageLabel.setText(errorMessage(ex));
            return;
        }

        content.getChildren().addAll(
                new Label("Instal·lació:"), facilityCombo,
                new Label("Grup:"), groupCombo,
                new Label("Data inici:"), startDatePicker,
                new Label("Hora inici:"), startTimeCombo,
                new Label("Data final:"), endDatePicker,
                new Label("Hora final:"), endTimeCombo,
                new Label("Notes:"), notesField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        if (dialog.showAndWait().isEmpty()) return;

        if (facilityCombo.getItems().isEmpty()) {
            messageLabel.setText("No hi ha instal·lacions disponibles per reservar");
            return;
        }

        if (groupCombo.getItems().isEmpty()) {
            messageLabel.setText("No formes part de cap grup. Crea'n un o uneix-te abans de reservar");
            return;
        }

        if (facilityCombo.getValue() == null || groupCombo.getValue() == null
                || startDatePicker.getValue() == null || endDatePicker.getValue() == null
                || startTimeCombo.getValue() == null || endTimeCombo.getValue() == null) {
            messageLabel.setText("Completa tots els camps obligatoris");
            return;
        }

        long facilityId = Long.parseLong(facilityCombo.getValue().split(" - ")[0]);
        long groupId = Long.parseLong(groupCombo.getValue().split(" - ")[0]);
        String start = startDatePicker.getValue() + "T" + startTimeCombo.getValue() + ":00";
        String end = endDatePicker.getValue() + "T" + endTimeCombo.getValue() + ":00";
        String notes = notesField.getText() == null ? "" : notesField.getText().trim();

        if (!isStartInFuture(start)) {
            messageLabel.setText("L'inici de la reserva ha de ser en futur");
            return;
        }

        if (!isEndAfterStart(start, end)) {
            messageLabel.setText("La data/hora final ha de ser posterior a la inicial");
            return;
        }

        runAsync(() -> {
            try {
                App.getApiClient().createBooking(facilityId, groupId, start, end, notes);
                Platform.runLater(() -> {
                    messageLabel.setText("Reserva creada");
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    private void showEditBookingDialog(JsonObject booking) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Editar reserva");
        dialog.setHeaderText("Modifica la reserva #" + booking.get("id").getAsLong());

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        ComboBox<String> facilityCombo = new ComboBox<>();
        ComboBox<String> groupCombo = new ComboBox<>();
        facilityCombo.setId("dialogFacilityCombo");
        groupCombo.setId("dialogGroupCombo");

        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        ComboBox<String> startTimeCombo = new ComboBox<>(buildTimeSlots());
        DatePicker endDatePicker = new DatePicker(LocalDate.now());
        ComboBox<String> endTimeCombo = new ComboBox<>(buildTimeSlots());

        TextField notesField = new TextField();
        notesField.setId("dialogBookingNotesField");
        notesField.setPromptText("Notes (opcional)");
        notesField.setText(booking.has("notes") && !booking.get("notes").isJsonNull() ? booking.get("notes").getAsString() : "");

        try {
            JsonArray facilities = App.getApiClient().getFacilities();
            facilities.forEach(el -> {
                JsonObject obj = el.getAsJsonObject();
                String item = obj.get("id").getAsLong() + " - " + obj.get("name").getAsString();
                facilityCombo.getItems().add(item);
                if (booking.has("facilityId") && obj.get("id").getAsLong() == booking.get("facilityId").getAsLong()) {
                    facilityCombo.setValue(item);
                }
            });

            JsonArray groups = App.getApiClient().getMyGroups();
            groups.forEach(el -> {
                JsonObject obj = el.getAsJsonObject();
                String code = obj.has("joinCode") && !obj.get("joinCode").isJsonNull() ? obj.get("joinCode").getAsString() : "-";
                String item = obj.get("id").getAsLong() + " - " + obj.get("name").getAsString() + " (" + code + ")";
                groupCombo.getItems().add(item);
                if (booking.has("groupId") && !booking.get("groupId").isJsonNull() && obj.get("id").getAsLong() == booking.get("groupId").getAsLong()) {
                    groupCombo.setValue(item);
                }
            });

            if (booking.has("startTime")) {
                LocalDateTime start = LocalDateTime.parse(booking.get("startTime").getAsString(), ISO_DATE_TIME);
                startDatePicker.setValue(start.toLocalDate());
                startTimeCombo.setValue(start.toLocalTime().withSecond(0).withNano(0).toString());
            }
            if (booking.has("endTime")) {
                LocalDateTime end = LocalDateTime.parse(booking.get("endTime").getAsString(), ISO_DATE_TIME);
                endDatePicker.setValue(end.toLocalDate());
                endTimeCombo.setValue(end.toLocalTime().withSecond(0).withNano(0).toString());
            }
        } catch (Exception ex) {
            messageLabel.setText(errorMessage(ex));
            return;
        }

        content.getChildren().addAll(
                new Label("Instal·lació:"), facilityCombo,
                new Label("Grup:"), groupCombo,
                new Label("Data inici:"), startDatePicker,
                new Label("Hora inici:"), startTimeCombo,
                new Label("Data final:"), endDatePicker,
                new Label("Hora final:"), endTimeCombo,
                new Label("Notes:"), notesField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        if (dialog.showAndWait().isEmpty()) return;

        if (facilityCombo.getValue() == null || groupCombo.getValue() == null
                || startDatePicker.getValue() == null || endDatePicker.getValue() == null
                || startTimeCombo.getValue() == null || endTimeCombo.getValue() == null) {
            messageLabel.setText("Completa tots els camps obligatoris");
            return;
        }

        long bookingId = booking.get("id").getAsLong();
        long facilityId = Long.parseLong(facilityCombo.getValue().split(" - ")[0]);
        long groupId = Long.parseLong(groupCombo.getValue().split(" - ")[0]);
        String start = startDatePicker.getValue() + "T" + startTimeCombo.getValue() + ":00";
        String end = endDatePicker.getValue() + "T" + endTimeCombo.getValue() + ":00";
        String notes = notesField.getText() == null ? "" : notesField.getText().trim();

        if (!isEndAfterStart(start, end)) {
            messageLabel.setText("La data/hora final ha de ser posterior a la inicial");
            return;
        }

        runAsync(() -> {
            try {
                App.getApiClient().updateBooking(bookingId, facilityId, groupId, start, end, notes);
                Platform.runLater(() -> {
                    messageLabel.setText("Reserva actualitzada");
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    private void cancelBooking(long bookingId) {
        Dialog<ButtonType> confirm = new Dialog<>();
        confirm.setTitle("Confirmar");
        confirm.setHeaderText("Vols cancel·lar aquesta reserva?");
        confirm.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        runAsync(() -> {
            try {
                App.getApiClient().cancelBooking(bookingId);
                Platform.runLater(() -> {
                    messageLabel.setText("Reserva cancel·lada");
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    @FXML
    private void showCreateGroupDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Crear grup");
        dialog.setHeaderText("Nou grup");

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        TextField nameField = new TextField();
        TextField descriptionField = new TextField();
        nameField.setId("dialogGroupNameField");
        descriptionField.setId("dialogGroupDescriptionField");

        content.getChildren().addAll(
                new Label("Nom del grup:"), nameField,
                new Label("Descripció:"), descriptionField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        if (dialog.showAndWait().isEmpty()) return;

        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String description = descriptionField.getText() == null ? "" : descriptionField.getText().trim();
        if (name.isBlank()) {
            messageLabel.setText("El nom del grup és obligatori");
            return;
        }

        runAsync(() -> {
            try {
                App.getApiClient().createGroup(name, description);
                Platform.runLater(() -> {
                    messageLabel.setText("Grup creat");
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    private void showEditGroupDialog(JsonObject group) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Editar grup");
        dialog.setHeaderText("Modifica el grup");

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        TextField nameField = new TextField(group.has("name") ? group.get("name").getAsString() : "");
        TextField descriptionField = new TextField(
                group.has("description") && !group.get("description").isJsonNull() ? group.get("description").getAsString() : ""
        );
        nameField.setId("dialogGroupNameField");
        descriptionField.setId("dialogGroupDescriptionField");

        content.getChildren().addAll(
                new Label("Nom del grup:"), nameField,
                new Label("Descripció:"), descriptionField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        if (dialog.showAndWait().isEmpty()) return;

        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String description = descriptionField.getText() == null ? "" : descriptionField.getText().trim();
        if (name.isBlank()) {
            messageLabel.setText("El nom del grup és obligatori");
            return;
        }

        long groupId = group.get("id").getAsLong();

        runAsync(() -> {
            try {
                App.getApiClient().updateGroup(groupId, name, description);
                Platform.runLater(() -> {
                    messageLabel.setText("Grup actualitzat");
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    private void leaveGroup(long groupId) {
        Dialog<ButtonType> confirm = new Dialog<>();
        confirm.setTitle("Confirmar");
        confirm.setHeaderText("Vols sortir d'aquest grup?");
        confirm.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        runAsync(() -> {
            try {
                App.getApiClient().leaveGroup(groupId);
                Platform.runLater(() -> {
                    messageLabel.setText("Has sortit del grup");
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    @FXML
    private void joinGroup() {
        messageLabel.setText("");
        String code = joinGroupId.getText() == null ? "" : joinGroupId.getText().trim();
        if (code.isBlank()) {
            messageLabel.setText("Introdueix el codi del grup");
            return;
        }

        runAsync(() -> {
            try {
                App.getApiClient().joinGroupByCode(code);
                Platform.runLater(() -> {
                    messageLabel.setText("Has entrat al grup");
                    joinGroupId.clear();
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    @FXML
    private void logout() {
        runAsync(() -> {
            try { App.getApiClient().logout(); } catch (Exception ignored) {}
            Platform.runLater(() -> {
                try {
                    App.showLogin();
                } catch (Exception e) {
                    messageLabel.setText("No s'ha pogut tancar sessió");
                }
            });
        });
    }

    private ObservableList<String> buildTimeSlots() {
        ObservableList<String> slots = FXCollections.observableArrayList();
        for (int hour = 0; hour < 24; hour++) {
            slots.add(String.format("%02d:00", hour));
            slots.add(String.format("%02d:30", hour));
        }
        return slots;
    }

    private boolean isEndAfterStart(String start, String end) {
        try {
            LocalDateTime startDt = LocalDateTime.parse(start, ISO_DATE_TIME);
            LocalDateTime endDt = LocalDateTime.parse(end, ISO_DATE_TIME);
            return endDt.isAfter(startDt);
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isStartInFuture(String start) {
        try {
            LocalDateTime startDt = LocalDateTime.parse(start, ISO_DATE_TIME);
            return startDt.isAfter(LocalDateTime.now());
        } catch (Exception ex) {
            return false;
        }
    }

    private LocalDateTime nextSlotFromNow() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        int minute = now.getMinute();
        int delta = (30 - (minute % 30)) % 30;
        if (delta == 0) {
            delta = 30;
        }
        return now.plusMinutes(delta);
    }

    private String formatDateTime(String value) {
        if (value == null || value.isBlank()) return "";
        try {
            return LocalDateTime.parse(value, ISO_DATE_TIME).format(UI_DATE_TIME);
        } catch (Exception ex) {
            return value;
        }
    }

    private String errorMessage(Exception e) {
        if (e instanceof ApiException api) return api.getMessage();
        return "Error de connexió amb el servidor";
    }

    private void runAsync(Runnable task) {
        Thread worker = new Thread(task, "desktop-home-async");
        worker.setDaemon(true);
        worker.start();
    }
}
