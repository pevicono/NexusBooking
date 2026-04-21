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
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;

public class BackofficeController {

    @FXML private Label statsLabel;
    @FXML private Label messageLabel;

    @FXML private TableView<UserRow> usersTable;
    @FXML private TableColumn<UserRow, Long> usersColId;
    @FXML private TableColumn<UserRow, String> usersColEmail;
    @FXML private TableColumn<UserRow, String> usersColRole;
    @FXML private TableColumn<UserRow, Boolean> usersColActive;
    @FXML private TableColumn<UserRow, Void> usersColActions;

    @FXML private TableView<FacilityRow> facilitiesTable;
    @FXML private TableColumn<FacilityRow, Long> facColId;
    @FXML private TableColumn<FacilityRow, String> facColName;
    @FXML private TableColumn<FacilityRow, String> facColType;
    @FXML private TableColumn<FacilityRow, Integer> facColCapacity;
    @FXML private TableColumn<FacilityRow, String> facColLocation;
    @FXML private TableColumn<FacilityRow, Void> facColActions;

    @FXML private TableView<BookingRow> bookingsTable;
    @FXML private TableColumn<BookingRow, Long> bookColId;
    @FXML private TableColumn<BookingRow, String> bookColUser;
    @FXML private TableColumn<BookingRow, String> bookColFacility;
    @FXML private TableColumn<BookingRow, String> bookColStart;
    @FXML private TableColumn<BookingRow, String> bookColEnd;
    @FXML private TableColumn<BookingRow, String> bookColStatus;
    @FXML private TableColumn<BookingRow, Void> bookColActions;

    @FXML private TableView<IncidentRow> incidentsTable;
    @FXML private TableColumn<IncidentRow, Long> incColId;
    @FXML private TableColumn<IncidentRow, String> incColTitle;
    @FXML private TableColumn<IncidentRow, String> incColFacility;
    @FXML private TableColumn<IncidentRow, String> incColStatus;
    @FXML private TableColumn<IncidentRow, String> incColDate;
    @FXML private TableColumn<IncidentRow, Void> incColActions;

    @FXML private TableView<GroupRow> groupsTable;
    @FXML private TableColumn<GroupRow, Long> grpColId;
    @FXML private TableColumn<GroupRow, String> grpColName;
    @FXML private TableColumn<GroupRow, String> grpColOwner;
    @FXML private TableColumn<GroupRow, Integer> grpColMembers;
    @FXML private TableColumn<GroupRow, Void> grpColActions;

    @FXML private ComboBox<String> calendarFacilityCombo;
    @FXML private GridPane calendarGrid;
    @FXML private Label calendarMonthLabel;
    @FXML private ComboBox<Integer> calendarYearCombo;

    private YearMonth currentCalendarMonth = YearMonth.now();
    private java.util.Map<LocalDate, Integer> bookingCountByDate = new java.util.HashMap<>();
    private java.util.Map<LocalDate, java.util.List<String>> bookingDetailsByDate = new java.util.HashMap<>();

    private static final java.util.Map<String, String> STATUS_LABELS = new java.util.LinkedHashMap<>();
    static {
        STATUS_LABELS.put("OPEN", "Obert");
        STATUS_LABELS.put("IN_PROGRESS", "En progrés");
        STATUS_LABELS.put("RESOLVED", "Resolt");
        STATUS_LABELS.put("CLOSED", "Tancat");
    }

    private static final java.util.Map<String, String> BOOKING_STATUS_LABELS = new java.util.LinkedHashMap<>();
    static {
        BOOKING_STATUS_LABELS.put("PENDING", "Pendent");
        BOOKING_STATUS_LABELS.put("CONFIRMED", "Confirmada");
        BOOKING_STATUS_LABELS.put("CANCELLED", "Cancel·lada");
    }

    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter UI_DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private JsonArray facilities = new JsonArray();
    private JsonArray users = new JsonArray();

    @FXML
    private void initialize() {
        setupTableColumns();
        refreshAll();
        setupCalendarYearSelector();
    }

    private void setupCalendarYearSelector() {
        ObservableList<Integer> years = FXCollections.observableArrayList();
        int currentYear = YearMonth.now().getYear();
        for (int year = currentYear - 8; year <= currentYear + 8; year++) {
            years.add(year);
        }
        calendarYearCombo.setItems(years);
        calendarYearCombo.setValue(currentCalendarMonth.getYear());
    }

    @FXML
    private void calendarYearChanged() {
        Integer selectedYear = calendarYearCombo.getValue();
        if (selectedYear == null || selectedYear == currentCalendarMonth.getYear()) {
            return;
        }
        currentCalendarMonth = currentCalendarMonth.withYear(selectedYear);
        refreshCalendarView();
    }

    private void setupTableColumns() {
        // Users table
        usersColId.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().id));
        usersColEmail.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().email));
        usersColRole.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().role));
        usersColActive.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().active));
        usersColId.setSortType(TableColumn.SortType.ASCENDING);
        setupUserActionsColumn();

        // Facilities table
        facColId.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().id));
        facColName.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().name));
        facColType.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().type));
        facColCapacity.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().capacity));
        facColLocation.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().location));
        facColId.setSortType(TableColumn.SortType.ASCENDING);
        setupFacilityActionsColumn();

        // Bookings table
        bookColId.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().id));
        bookColUser.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().userName));
        bookColFacility.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().facilityName));
        bookColStart.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().startTime));
        bookColEnd.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().endTime));
        bookColStatus.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().status));
        setupBookingStatusBadgeColumn(bookColStatus);
        bookColId.setSortType(TableColumn.SortType.ASCENDING);
        setupBookingActionsColumn();

        // Incidents table
        incColId.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().id));
        incColTitle.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().title));
        incColFacility.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().facilityName));
        incColStatus.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().statusLabel));
        setupIncidentStatusBadgeColumn();
        incColDate.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().createdAt));
        incColId.setSortType(TableColumn.SortType.ASCENDING);
        setupIncidentActionsColumn();

        // Groups table
        grpColId.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().id));
        grpColName.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().name));
        grpColOwner.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createStringBinding(() -> cellData.getValue().ownerEmail));
        grpColMembers.setCellValueFactory(cellData -> javafx.beans.binding.Bindings.createObjectBinding(() -> cellData.getValue().memberCount));
        grpColId.setSortType(TableColumn.SortType.ASCENDING);
        setupGroupActionsColumn();
    }

    private void setupBookingStatusBadgeColumn(TableColumn<BookingRow, String> col) {
        col.setCellFactory(c -> new TableCell<BookingRow, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                BookingRow row = getTableView().getItems().get(getIndex());
                String code = row.status == null ? "PENDING" : row.status;
                Label badge = new Label(BOOKING_STATUS_LABELS.getOrDefault(code, code));
                badge.getStyleClass().add("status-pill");
                if ("CONFIRMED".equals(code)) {
                    badge.getStyleClass().add("status-confirmed");
                } else if ("CANCELLED".equals(code)) {
                    badge.getStyleClass().add("status-cancelled");
                } else {
                    badge.getStyleClass().add("status-pending");
                }
                setGraphic(badge);
                setText(null);
            }
        });
    }

    private void setupIncidentStatusBadgeColumn() {
        incColStatus.setCellFactory(c -> new TableCell<IncidentRow, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                IncidentRow row = getTableView().getItems().get(getIndex());
                String code = row.status == null ? "OPEN" : row.status;
                Label badge = new Label(row.statusLabel == null ? code : row.statusLabel);
                badge.getStyleClass().add("status-pill");
                if ("IN_PROGRESS".equals(code)) {
                    badge.getStyleClass().add("status-progress");
                } else if ("RESOLVED".equals(code) || "CLOSED".equals(code)) {
                    badge.getStyleClass().add("status-resolved");
                } else {
                    badge.getStyleClass().add("status-open");
                }
                setGraphic(badge);
                setText(null);
            }
        });
    }

    private String formatDateTime(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        try {
            return LocalDateTime.parse(value, ISO_DATE_TIME).format(UI_DATE_TIME);
        } catch (Exception ignored) {
            return value;
        }
    }

    private ObservableList<String> buildTimeSlots() {
        ObservableList<String> slots = FXCollections.observableArrayList();
        for (int hour = 0; hour < 24; hour++) {
            slots.add(String.format("%02d:00", hour));
            slots.add(String.format("%02d:30", hour));
        }
        return slots;
    }

    private void setupUserActionsColumn() {
        usersColActions.setCellFactory(col -> new TableCell<UserRow, Void>() {
            private final Button editBtn = new Button("Editar");
            private final Button deleteBtn = new Button("Eliminar");

            {
                editBtn.setStyle("-fx-font-size: 11;");
                deleteBtn.setStyle("-fx-font-size: 11; -fx-padding: 4;");
                editBtn.setOnAction(e -> {
                    UserRow row = getTableView().getItems().get(getIndex());
                    showEditUserDialog(row);
                });
                deleteBtn.setOnAction(e -> {
                    UserRow row = getTableView().getItems().get(getIndex());
                    deleteUser(row.id);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(4, editBtn, deleteBtn));
            }
        });
    }

    private void setupFacilityActionsColumn() {
        facColActions.setCellFactory(col -> new TableCell<FacilityRow, Void>() {
            private final Button editBtn = new Button("Editar");
            private final Button deleteBtn = new Button("Eliminar");

            {
                editBtn.setStyle("-fx-font-size: 11;");
                deleteBtn.setStyle("-fx-font-size: 11; -fx-padding: 4;");
                editBtn.setOnAction(e -> {
                    FacilityRow row = getTableView().getItems().get(getIndex());
                    showEditFacilityDialog(row);
                });
                deleteBtn.setOnAction(e -> {
                    FacilityRow row = getTableView().getItems().get(getIndex());
                    deleteFacility(row.id);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(4, editBtn, deleteBtn));
            }
        });
    }

    private void setupIncidentActionsColumn() {
        incColActions.setCellFactory(col -> new TableCell<IncidentRow, Void>() {
            private final Button editBtn = new Button("Editar");
            private final Button deleteBtn = new Button("Eliminar");

            {
                editBtn.setStyle("-fx-font-size: 11;");
                deleteBtn.setStyle("-fx-font-size: 11; -fx-padding: 4;");
                editBtn.setOnAction(e -> {
                    IncidentRow row = getTableView().getItems().get(getIndex());
                    showEditIncidentDialog(row);
                });
                deleteBtn.setOnAction(e -> {
                    IncidentRow row = getTableView().getItems().get(getIndex());
                    deleteIncident(row.id);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(4, editBtn, deleteBtn));
            }
        });
    }

    @FXML
    private void refreshAll() {
        messageLabel.setText("");
        runAsync(() -> {
            try {
                JsonObject dashboard = App.getApiClient().getAdminDashboard();
                users = App.getApiClient().getAdminUsers();
                JsonArray bookings = App.getApiClient().getAdminBookings();
                JsonArray incidents = App.getApiClient().getIncidents();
                facilities = App.getApiClient().getFacilities();
                JsonArray groups = App.getApiClient().getAdminGroups();

                Platform.runLater(() -> {
                    statsLabel.setText("Usuaris: " + dashboard.get("users").getAsInt() +
                            " | Reserves: " + dashboard.get("bookings").getAsInt() +
                            " | Grups: " + dashboard.get("groups").getAsInt() +
                            " | Incidències: " + dashboard.get("incidents").getAsInt());

                    loadUsersTable(users);
                    loadFacilitiesTable(facilities);
                    loadBookingsTable(bookings);
                    loadIncidentsTable(incidents);
                    loadGroupsTable(groups);
                    populateCalendarFacilityCombo();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    private void loadUsersTable(JsonArray users) {
        ObservableList<UserRow> data = FXCollections.observableArrayList();
        users.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            data.add(new UserRow(
                obj.get("id").getAsLong(),
                obj.get("email").getAsString(),
                obj.has("role") ? obj.get("role").getAsString() : "USER",
                obj.has("active") ? obj.get("active").getAsBoolean() : true
            ));
        });
        usersTable.setItems(data);
        usersTable.getSortOrder().clear();
        usersTable.getSortOrder().add(usersColId);
    }

    private void loadFacilitiesTable(JsonArray facilities) {
        ObservableList<FacilityRow> data = FXCollections.observableArrayList();
        facilities.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            data.add(new FacilityRow(
                obj.get("id").getAsLong(),
                obj.get("name").getAsString(),
                obj.get("type").getAsString(),
                obj.has("capacity") ? obj.get("capacity").getAsInt() : null,
                obj.has("location") ? obj.get("location").getAsString() : "",
                obj.has("description") ? obj.get("description").getAsString() : ""
            ));
        });
        facilitiesTable.setItems(data);
        facilitiesTable.getSortOrder().clear();
        facilitiesTable.getSortOrder().add(facColId);
    }

    private void loadBookingsTable(JsonArray bookings) {
        ObservableList<BookingRow> data = FXCollections.observableArrayList();
        bookings.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            String userEmail = obj.has("userEmail") ? obj.get("userEmail").getAsString() : "N/A";
            String groupName = obj.has("groupName") && !obj.get("groupName").isJsonNull()
                    ? obj.get("groupName").getAsString()
                    : "Sense grup";
            data.add(new BookingRow(
                obj.get("id").getAsLong(),
                userEmail + " / " + groupName,
                obj.has("facilityName") ? obj.get("facilityName").getAsString() : "N/A",
                formatDateTime(obj.get("startTime").getAsString()),
                formatDateTime(obj.get("endTime").getAsString()),
                obj.has("status") ? obj.get("status").getAsString() : "CONFIRMED",
                obj.has("notes") ? obj.get("notes").getAsString() : ""
            ));
        });
        bookingsTable.setItems(data);
        bookingsTable.getSortOrder().clear();
        bookingsTable.getSortOrder().add(bookColId);
    }

    private void loadIncidentsTable(JsonArray incidents) {
        ObservableList<IncidentRow> data = FXCollections.observableArrayList();
        incidents.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            data.add(new IncidentRow(
                obj.get("id").getAsLong(),
                obj.get("title").getAsString(),
                obj.has("facilityName") ? obj.get("facilityName").getAsString() : "N/A",
                obj.has("status") ? obj.get("status").getAsString() : "OPEN",
                obj.has("statusLabel") ? obj.get("statusLabel").getAsString() : obj.has("status") ? obj.get("status").getAsString() : "Obert",
                obj.has("createdAt") ? formatDateTime(obj.get("createdAt").getAsString()) : ""
            ));
        });
        incidentsTable.setItems(data);
        incidentsTable.getSortOrder().clear();
        incidentsTable.getSortOrder().add(incColId);
    }

    @FXML
    private void showCreateUserDialog() {
        Dialog<UserRow> dialog = new Dialog<>();
        dialog.setTitle("Crear usuari");
        dialog.setHeaderText("Crear nou usuari");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Contrasenya");
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList("ADMIN", "USER"));
        roleCombo.setValue("USER");

        content.getChildren().addAll(
            new Label("Email:"),
            emailField,
            new Label("Contrasenya:"),
            passwordField,
            new Label("Role:"),
            roleCombo
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<UserRow> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String role = roleCombo.getValue();
        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Email i contrasenya son obligatoris");
            return;
        }

        runAsync(() -> {
            try {
                App.getApiClient().createUserWithRole(email, password, role);
                Platform.runLater(() -> {
                    messageLabel.setText("Usuari creat correctament");
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    @FXML
    private void showEditUserDialog(UserRow user) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Editar usuari");
        dialog.setHeaderText("Editar usuari: " + user.email);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        CheckBox activeCheck = new CheckBox("Actiu");
        activeCheck.setSelected(user.active);
        
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList("ADMIN", "USER"));
        roleCombo.setValue(user.role);

        content.getChildren().addAll(
            new Label("Email: " + user.email),
            new Label("Rol:"),
            roleCombo,
            activeCheck
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<Void> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        runAsync(() -> {
            try {
                App.getApiClient().setUserActive(user.id, activeCheck.isSelected());
                if (!user.role.equals(roleCombo.getValue())) {
                    App.getApiClient().setUserRole(user.id, roleCombo.getValue());
                }
                Platform.runLater(() -> {
                    messageLabel.setText("Usuari actualitzat");
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    private void deleteUser(Long id) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminacio");
        confirm.setHeaderText("Estàs segur de que vols eliminar aquest usuari?");
        if (confirm.showAndWait().get() != ButtonType.OK) return;

        messageLabel.setText("Eliminant usuari...");
        runAsync(() -> {
            try {
                App.getApiClient().deleteUser(id);
                Platform.runLater(() -> {
                    messageLabel.setText("Usuari eliminat correctament");
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    @FXML
    private void showCreateFacilityDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Crear instal·lació");
        dialog.setHeaderText("Crear nova instal·lació");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Nom");
        TextField typeField = new TextField();
        typeField.setPromptText("Tipus");
        TextField capacityField = new TextField();
        capacityField.setPromptText("Capacitat");
        TextField locationField = new TextField();
        locationField.setPromptText("Localització");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Descripció");
        descriptionArea.setPrefRowCount(4);

        content.getChildren().addAll(
            new Label("Nom:"),
            nameField,
            new Label("Tipus:"),
            typeField,
            new Label("Capacitat:"),
            capacityField,
            new Label("Localització:"),
            locationField,
            new Label("Descripció:"),
            descriptionArea
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<Void> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String name = nameField.getText().trim();
        String type = typeField.getText().trim();
        if (name.isEmpty() || type.isEmpty()) {
            messageLabel.setText("Nom i Tipus son obligatoris");
            return;
        }

        Integer capacity = null;
        try {
            if (!capacityField.getText().trim().isEmpty()) {
                capacity = Integer.parseInt(capacityField.getText().trim());
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Capacitat ha de ser un nombre");
            return;
        }

        Integer finalCapacity = capacity;
        runAsync(() -> {
            try {
                App.getApiClient().createFacility(
                    name, type, finalCapacity,
                    locationField.getText().trim(),
                    descriptionArea.getText().trim()
                );
                Platform.runLater(() -> {
                    messageLabel.setText("Instal·lació creada");
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    @FXML
    private void showEditFacilityDialog(FacilityRow facility) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Editar instal·lació");
        dialog.setHeaderText("Editar: " + facility.name);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setText(facility.name);
        TextField typeField = new TextField();
        typeField.setText(facility.type);
        TextField capacityField = new TextField();
        if (facility.capacity != null) capacityField.setText(facility.capacity.toString());
        TextField locationField = new TextField();
        locationField.setText(facility.location);
        TextArea descriptionArea = new TextArea();
        descriptionArea.setText(facility.description);
        descriptionArea.setPrefRowCount(4);

        content.getChildren().addAll(
            new Label("Nom:"),
            nameField,
            new Label("Tipus:"),
            typeField,
            new Label("Capacitat:"),
            capacityField,
            new Label("Localització:"),
            locationField,
            new Label("Descripció:"),
            descriptionArea
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<Void> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String name = nameField.getText().trim();
        String type = typeField.getText().trim();
        if (name.isEmpty() || type.isEmpty()) {
            messageLabel.setText("Nom i Tipus son obligatoris");
            return;
        }

        Integer capacity = null;
        try {
            if (!capacityField.getText().trim().isEmpty()) {
                capacity = Integer.parseInt(capacityField.getText().trim());
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Capacitat ha de ser un nombre");
            return;
        }

        Integer finalCapacity = capacity;
        runAsync(() -> {
            try {
                App.getApiClient().updateFacility(
                    facility.id, name, type, finalCapacity,
                    locationField.getText().trim(),
                    descriptionArea.getText().trim()
                );
                Platform.runLater(() -> {
                    messageLabel.setText("Instal·lació actualitzada");
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    private void deleteFacility(Long id) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminacio");
        confirm.setHeaderText("Estàs segur?");
        if (confirm.showAndWait().get() != ButtonType.OK) return;
        
        messageLabel.setText("Eliminant instal·lació...");
        runAsync(() -> {
            try {
                App.getApiClient().deleteFacility(id);
                Platform.runLater(() -> {
                    messageLabel.setText("Instal·lació eliminada correctament");
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    @FXML
    private void showCreateIncidentDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Crear incidència");
        dialog.setHeaderText("Crear nova incidència");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        ComboBox<String> facilityCombo = new ComboBox<>();
        facilityCombo.setItems(FXCollections.observableArrayList("Sin instal·lació"));
        facilities.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            facilityCombo.getItems().add(obj.get("id") + " - " + obj.get("name").getAsString());
        });

        TextField titleField = new TextField();
        titleField.setPromptText("Títol");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Descripció");
        descriptionArea.setPrefRowCount(4);

        content.getChildren().addAll(
            new Label("Instal·lació:"),
            facilityCombo,
            new Label("Títol:"),
            titleField,
            new Label("Descripció:"),
            descriptionArea
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<Void> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            messageLabel.setText("Títol es obligatori");
            return;
        }

        Long facilityId = null;
        String facilityStr = facilityCombo.getValue();
        if (facilityStr != null && !facilityStr.equals("Sin instal·lació")) {
            try {
                facilityId = Long.parseLong(facilityStr.split(" - ")[0]);
            } catch (Exception ignored) {}
        }

        Long finalFacilityId = facilityId;
        runAsync(() -> {
            try {
                App.getApiClient().createIncident(finalFacilityId, title, descriptionArea.getText().trim());
                Platform.runLater(() -> {
                    messageLabel.setText("Incidència creada");
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    @FXML
    private void showEditIncidentDialog(IncidentRow incident) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Editar incidència");
        dialog.setHeaderText("Editar: " + incident.title);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.setItems(FXCollections.observableArrayList(
            "OPEN - Obert", "IN_PROGRESS - En progrés", "RESOLVED - Resolt", "CLOSED - Tancat"
        ));
        // Select current status
        statusCombo.getItems().stream()
            .filter(s -> s.startsWith(incident.status))
            .findFirst()
            .ifPresent(statusCombo::setValue);
        if (statusCombo.getValue() == null) statusCombo.setValue("OPEN - Obert");

        content.getChildren().addAll(
            new Label("Estat:"),
            statusCombo
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<Void> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String selectedCode = statusCombo.getValue().split(" - ")[0];
        runAsync(() -> {
            try {
                App.getApiClient().updateIncidentStatus(incident.id, selectedCode);
                Platform.runLater(() -> {
                    messageLabel.setText("Incidència actualitzada");
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    private void deleteIncident(Long id) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminacio");
        confirm.setHeaderText("Estàs segur?");
        if (confirm.showAndWait().get() != ButtonType.OK) return;
        
        messageLabel.setText("Eliminant incidència...");
        runAsync(() -> {
            try {
                App.getApiClient().deleteIncident(id);
                Platform.runLater(() -> {
                    messageLabel.setText("Incidència eliminada correctament");
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    private void loadGroupsTable(JsonArray groups) {
        ObservableList<GroupRow> data = FXCollections.observableArrayList();
        groups.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            data.add(new GroupRow(
                obj.get("id").getAsLong(),
                obj.get("name").getAsString(),
                obj.has("ownerEmail") ? obj.get("ownerEmail").getAsString() : "N/A",
                obj.has("memberCount") ? obj.get("memberCount").getAsInt() : 0
            ));
        });
        groupsTable.setItems(data);
        groupsTable.getSortOrder().clear();
        groupsTable.getSortOrder().add(grpColId);
    }

    private void populateCalendarFacilityCombo() {
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("Totes"); // Default option to show all facilities
        facilities.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            items.add(obj.get("id") + " - " + obj.get("name").getAsString());
        });
        calendarFacilityCombo.setItems(items);
        calendarFacilityCombo.setValue("Totes");
        refreshCalendarView();
    }

    private void setupBookingActionsColumn() {
        bookColActions.setCellFactory(col -> new TableCell<BookingRow, Void>() {
            private final Button editBtn = new Button("Editar");
            private final Button cancelBtn = new Button("Cancel·lar");

            {
                editBtn.setStyle("-fx-font-size: 11;");
                cancelBtn.setStyle("-fx-font-size: 11;");
                editBtn.setOnAction(e -> {
                    BookingRow row = getTableView().getItems().get(getIndex());
                    showEditBookingDialog(row.id);
                });
                cancelBtn.setOnAction(e -> {
                    BookingRow row = getTableView().getItems().get(getIndex());
                    cancelBooking(row.id);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(6, editBtn, cancelBtn);
                    setGraphic(box);
                }
            }
        });
    }

    private void setupGroupActionsColumn() {
        grpColActions.setCellFactory(col -> new TableCell<GroupRow, Void>() {
            private final Button editBtn = new Button("Editar");
            private final Button deleteBtn = new Button("Eliminar");

            {
                editBtn.setStyle("-fx-font-size: 11;");
                deleteBtn.setStyle("-fx-font-size: 11; -fx-padding: 4;");
                editBtn.setOnAction(e -> {
                    GroupRow row = getTableView().getItems().get(getIndex());
                    showEditGroupDialog(row);
                });
                deleteBtn.setOnAction(e -> {
                    GroupRow row = getTableView().getItems().get(getIndex());
                    deleteGroup(row.id);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    GroupRow row = getTableView().getItems().get(getIndex());
                    editBtn.setId("adminGroupEditBtn-" + row.id);
                    deleteBtn.setId("adminGroupDeleteBtn-" + row.id);
                    HBox box = new HBox(6, editBtn, deleteBtn);
                    setGraphic(box);
                }
            }
        });
    }

    @FXML
    private void showCreateBookingDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Crear reserva");
        dialog.setHeaderText("Crear nova reserva (admin)");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        ComboBox<String> userCombo = new ComboBox<>();
        ComboBox<String> groupCombo = new ComboBox<>();

        try {
            JsonArray validUsers = App.getApiClient().getValidUsersForGroups();
            validUsers.forEach(el -> {
                JsonObject obj = el.getAsJsonObject();
                userCombo.getItems().add(obj.get("id") + " - " + obj.get("email").getAsString());
            });

            JsonArray groups = App.getApiClient().getAdminGroups();
            groups.forEach(el -> {
                JsonObject obj = el.getAsJsonObject();
                String code = obj.has("joinCode") && !obj.get("joinCode").isJsonNull() ? obj.get("joinCode").getAsString() : "-";
                groupCombo.getItems().add(obj.get("id") + " - " + obj.get("name").getAsString() + " (" + code + ")");
            });
        } catch (Exception e) {
            messageLabel.setText(errorMessage(e));
            return;
        }

        ComboBox<String> facilityCombo = new ComboBox<>();
        facilities.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            facilityCombo.getItems().add(obj.get("id") + " - " + obj.get("name").getAsString());
        });

        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        ComboBox<String> startTimeCombo = new ComboBox<>(buildTimeSlots());
        startTimeCombo.setValue("10:00");
        DatePicker endDatePicker = new DatePicker(LocalDate.now());
        ComboBox<String> endTimeCombo = new ComboBox<>(buildTimeSlots());
        endTimeCombo.setValue("11:00");
        TextField notesField = new TextField();
        notesField.setPromptText("Notes (opcional)");

        content.getChildren().addAll(
            new Label("Usuari:"), userCombo,
            new Label("Grup:"), groupCombo,
            new Label("Instal·lació:"), facilityCombo,
            new Label("Data inici:"), startDatePicker,
            new Label("Hora inici:"), startTimeCombo,
            new Label("Data final:"), endDatePicker,
            new Label("Hora final:"), endTimeCombo,
            new Label("Notes:"), notesField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<Void> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        if (userCombo.getValue() == null || groupCombo.getValue() == null || facilityCombo.getValue() == null
            || startDatePicker.getValue() == null || endDatePicker.getValue() == null
            || startTimeCombo.getValue() == null || endTimeCombo.getValue() == null) {
            messageLabel.setText("Usuari, grup, instal·lació, inici i final son obligatoris");
            return;
        }

        long userId = Long.parseLong(userCombo.getValue().split(" - ")[0]);
        long groupId = Long.parseLong(groupCombo.getValue().split(" - ")[0]);
        long facilityId = Long.parseLong(facilityCombo.getValue().split(" - ")[0]);
        String start = startDatePicker.getValue() + "T" + startTimeCombo.getValue() + ":00";
        String end = endDatePicker.getValue() + "T" + endTimeCombo.getValue() + ":00";
        String notes = notesField.getText().trim();

        try {
            LocalDateTime startDt = LocalDateTime.parse(start, ISO_DATE_TIME);
            LocalDateTime endDt = LocalDateTime.parse(end, ISO_DATE_TIME);
            if (!endDt.isAfter(startDt)) {
                messageLabel.setText("La data/hora final ha de ser posterior a la inicial");
                return;
            }
        } catch (Exception ex) {
            messageLabel.setText("Format de data/hora invàlid");
            return;
        }

        runAsync(() -> {
            try {
                App.getApiClient().adminCreateBooking(userId, facilityId, groupId, start, end, notes);
                Platform.runLater(() -> {
                    messageLabel.setText("Reserva creada correctament");
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    private void cancelBooking(Long id) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar cancel·lació");
        confirm.setHeaderText("Estàs segur de que vols cancel·lar aquesta reserva?");
        if (confirm.showAndWait().get() != ButtonType.OK) return;

        runAsync(() -> {
            try {
                App.getApiClient().adminCancelBooking(id);
                Platform.runLater(() -> {
                    messageLabel.setText("Reserva cancel·lada");
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    private void showEditBookingDialog(Long bookingId) {
        JsonObject booking = null;
        for (int i = 0; i < bookingsTable.getItems().size(); i++) {
            BookingRow row = bookingsTable.getItems().get(i);
            if (row.id.equals(bookingId)) {
                break;
            }
        }

        try {
            JsonArray allBookings = App.getApiClient().getAdminBookings();
            for (int i = 0; i < allBookings.size(); i++) {
                JsonObject b = allBookings.get(i).getAsJsonObject();
                if (b.get("id").getAsLong() == bookingId) {
                    booking = b;
                    break;
                }
            }
        } catch (Exception e) {
            messageLabel.setText(errorMessage(e));
            return;
        }

        if (booking == null) {
            messageLabel.setText("No s'ha trobat la reserva");
            return;
        }
        final JsonObject selectedBooking = booking;

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Editar reserva");
        dialog.setHeaderText("Reserva #" + bookingId);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        ComboBox<String> userCombo = new ComboBox<>();
        ComboBox<String> groupCombo = new ComboBox<>();
        ComboBox<String> facilityCombo = new ComboBox<>();

        try {
            JsonArray validUsers = App.getApiClient().getValidUsersForGroups();
            validUsers.forEach(el -> {
                JsonObject obj = el.getAsJsonObject();
                String item = obj.get("id") + " - " + obj.get("email").getAsString();
                userCombo.getItems().add(item);
                if (selectedBooking.has("userId") && obj.get("id").getAsLong() == selectedBooking.get("userId").getAsLong()) {
                    userCombo.setValue(item);
                }
            });

            JsonArray groups = App.getApiClient().getAdminGroups();
            groups.forEach(el -> {
                JsonObject obj = el.getAsJsonObject();
                String code = obj.has("joinCode") && !obj.get("joinCode").isJsonNull() ? obj.get("joinCode").getAsString() : "-";
                String item = obj.get("id") + " - " + obj.get("name").getAsString() + " (" + code + ")";
                groupCombo.getItems().add(item);
                if (selectedBooking.has("groupId") && !selectedBooking.get("groupId").isJsonNull() && obj.get("id").getAsLong() == selectedBooking.get("groupId").getAsLong()) {
                    groupCombo.setValue(item);
                }
            });

            facilities.forEach(el -> {
                JsonObject obj = el.getAsJsonObject();
                String item = obj.get("id") + " - " + obj.get("name").getAsString();
                facilityCombo.getItems().add(item);
                if (selectedBooking.has("facilityId") && obj.get("id").getAsLong() == selectedBooking.get("facilityId").getAsLong()) {
                    facilityCombo.setValue(item);
                }
            });
        } catch (Exception e) {
            messageLabel.setText(errorMessage(e));
            return;
        }

        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        ComboBox<String> startTimeCombo = new ComboBox<>(buildTimeSlots());
        DatePicker endDatePicker = new DatePicker(LocalDate.now());
        ComboBox<String> endTimeCombo = new ComboBox<>(buildTimeSlots());
        TextField notesField = new TextField(booking.has("notes") && !booking.get("notes").isJsonNull() ? booking.get("notes").getAsString() : "");

        try {
            LocalDateTime startDt = LocalDateTime.parse(booking.get("startTime").getAsString(), ISO_DATE_TIME);
            LocalDateTime endDt = LocalDateTime.parse(booking.get("endTime").getAsString(), ISO_DATE_TIME);
            startDatePicker.setValue(startDt.toLocalDate());
            startTimeCombo.setValue(String.format("%02d:%02d", startDt.getHour(), startDt.getMinute()));
            endDatePicker.setValue(endDt.toLocalDate());
            endTimeCombo.setValue(String.format("%02d:%02d", endDt.getHour(), endDt.getMinute()));
        } catch (Exception ignored) {
            startTimeCombo.setValue("10:00");
            endTimeCombo.setValue("11:00");
        }

        content.getChildren().addAll(
            new Label("Usuari:"), userCombo,
            new Label("Grup:"), groupCombo,
            new Label("Instal·lació:"), facilityCombo,
            new Label("Data inici:"), startDatePicker,
            new Label("Hora inici:"), startTimeCombo,
            new Label("Data final:"), endDatePicker,
            new Label("Hora final:"), endTimeCombo,
            new Label("Notes:"), notesField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        if (dialog.showAndWait().isEmpty()) return;

        if (userCombo.getValue() == null || groupCombo.getValue() == null || facilityCombo.getValue() == null
                || startDatePicker.getValue() == null || startTimeCombo.getValue() == null
                || endDatePicker.getValue() == null || endTimeCombo.getValue() == null) {
            messageLabel.setText("Completa tots els camps obligatoris");
            return;
        }

        long userId = Long.parseLong(userCombo.getValue().split(" - ")[0]);
        long groupId = Long.parseLong(groupCombo.getValue().split(" - ")[0]);
        long facilityId = Long.parseLong(facilityCombo.getValue().split(" - ")[0]);
        String start = startDatePicker.getValue() + "T" + startTimeCombo.getValue() + ":00";
        String end = endDatePicker.getValue() + "T" + endTimeCombo.getValue() + ":00";

        try {
            LocalDateTime startDt = LocalDateTime.parse(start, ISO_DATE_TIME);
            LocalDateTime endDt = LocalDateTime.parse(end, ISO_DATE_TIME);
            if (!endDt.isAfter(startDt)) {
                messageLabel.setText("La data/hora final ha de ser posterior a la inicial");
                return;
            }
        } catch (Exception ex) {
            messageLabel.setText("Format de data/hora invàlid");
            return;
        }

        String notes = notesField.getText() == null ? "" : notesField.getText().trim();

        runAsync(() -> {
            try {
                App.getApiClient().adminUpdateBooking(bookingId, userId, facilityId, groupId, start, end, notes);
                Platform.runLater(() -> {
                    messageLabel.setText("Reserva actualitzada");
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    private void showEditGroupDialog(GroupRow row) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Editar grup");
        dialog.setHeaderText("Editar " + row.name);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        TextField nameField = new TextField(row.name);
        TextField descriptionField = new TextField();
        nameField.setId("adminGroupNameField");
        descriptionField.setId("adminGroupDescriptionField");
        descriptionField.setPromptText("Descripció");

        try {
            JsonArray groups = App.getApiClient().getAdminGroups();
            for (int i = 0; i < groups.size(); i++) {
                JsonObject g = groups.get(i).getAsJsonObject();
                if (g.get("id").getAsLong() == row.id) {
                    if (g.has("description") && !g.get("description").isJsonNull()) {
                        descriptionField.setText(g.get("description").getAsString());
                    }
                    break;
                }
            }
        } catch (Exception ignored) {}

        content.getChildren().addAll(
            new Label("Nom:"), nameField,
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
                App.getApiClient().adminUpdateGroup(row.id, name, description);
                Platform.runLater(() -> {
                    messageLabel.setText("Grup actualitzat");
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    private void deleteGroup(Long id) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminació");
        confirm.setHeaderText("Estàs segur de que vols eliminar aquest grup?");
        if (confirm.showAndWait().get() != ButtonType.OK) return;

        runAsync(() -> {
            try {
                App.getApiClient().adminDeleteGroup(id);
                Platform.runLater(() -> {
                    messageLabel.setText("Grup eliminat correctament");
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
        dialog.setHeaderText("Crear nou grup (admin)");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        ComboBox<String> ownerCombo = new ComboBox<>();
        TextField nameField = new TextField();
        ownerCombo.setId("adminGroupOwnerCombo");
        nameField.setId("adminGroupNameField");
        nameField.setPromptText("Nom del grup");
        TextField descriptionField = new TextField();
        descriptionField.setId("adminGroupDescriptionField");
        descriptionField.setPromptText("Descripció");

        try {
            JsonArray validUsers = App.getApiClient().getValidUsersForGroups();
            validUsers.forEach(el -> {
                JsonObject obj = el.getAsJsonObject();
                ownerCombo.getItems().add(obj.get("id") + " - " + obj.get("email").getAsString());
            });
        } catch (Exception e) {
            messageLabel.setText(errorMessage(e));
            return;
        }

        content.getChildren().addAll(
            new Label("Propietari (no admin):"), ownerCombo,
            new Label("Nom:"), nameField,
            new Label("Descripció:"), descriptionField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        if (dialog.showAndWait().isEmpty()) return;

        if (ownerCombo.getValue() == null || nameField.getText() == null || nameField.getText().trim().isBlank()) {
            messageLabel.setText("Propietari i nom són obligatoris");
            return;
        }

        long ownerId = Long.parseLong(ownerCombo.getValue().split(" - ")[0]);
        String name = nameField.getText().trim();
        String description = descriptionField.getText() == null ? "" : descriptionField.getText().trim();

        runAsync(() -> {
            try {
                App.getApiClient().adminCreateGroup(ownerId, name, description);
                Platform.runLater(() -> {
                    messageLabel.setText("Grup creat correctament");
                    refreshAll();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    @FXML
    private void calendarPrevMonth() {
        currentCalendarMonth = currentCalendarMonth.minusMonths(1);
        calendarYearCombo.setValue(currentCalendarMonth.getYear());
        refreshCalendarView();
    }

    @FXML
    private void calendarNextMonth() {
        currentCalendarMonth = currentCalendarMonth.plusMonths(1);
        calendarYearCombo.setValue(currentCalendarMonth.getYear());
        refreshCalendarView();
    }

    @FXML
    private void refreshCalendarView() {
        runAsync(() -> {
            try {
                JsonArray allBookings = App.getApiClient().getAdminBookings();
                String facilityStr = calendarFacilityCombo.getValue();
                long facilityId = -1;

                // Parse facility selection: "id - name" format, or all if "Totes"
                if (facilityStr != null && !facilityStr.isEmpty()) {
                    if (!facilityStr.contains("-")) {
                        // "Totes" option
                        facilityId = -1;
                    } else {
                        try {
                            facilityId = Long.parseLong(facilityStr.split(" - ")[0]);
                        } catch (Exception e) {
                            facilityId = -1;
                        }
                    }
                }
                final long selectedFacilityId = facilityId;

                // Build both count and compact booking details by date for selected facility
                java.util.Map<LocalDate, Integer> countByDate = new java.util.HashMap<>();
                java.util.Map<LocalDate, java.util.List<String>> detailsByDate = new java.util.HashMap<>();
                allBookings.forEach(el -> {
                    JsonObject obj = el.getAsJsonObject();
                    long fId = obj.has("facilityId") ? obj.get("facilityId").getAsLong() : -1;
                    if (selectedFacilityId == -1 || fId == selectedFacilityId) {
                        String startStr = obj.get("startTime").getAsString();
                        try {
                            LocalDateTime startDt = LocalDateTime.parse(startStr, ISO_DATE_TIME);
                            LocalDate date = startDt.toLocalDate();
                            countByDate.put(date, countByDate.getOrDefault(date, 0) + 1);
                            String user = obj.has("userEmail") ? obj.get("userEmail").getAsString() : "Usuari";
                            String label = startDt.toLocalTime().toString().substring(0, 5) + " · " + user;
                            detailsByDate.computeIfAbsent(date, k -> new java.util.ArrayList<>()).add(label);
                        } catch (Exception ignored) {}
                    }
                });

                bookingCountByDate = countByDate;
                bookingDetailsByDate = detailsByDate;

                Platform.runLater(() -> {
                    String monthLabel = currentCalendarMonth.getMonth().getDisplayName(TextStyle.FULL, new Locale("ca", "ES"));
                    monthLabel = monthLabel.substring(0, 1).toUpperCase(new Locale("ca", "ES")) + monthLabel.substring(1);
                    calendarMonthLabel.setText(monthLabel + " " + currentCalendarMonth.getYear());
                    renderCalendarGrid();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    private void renderCalendarGrid() {
        calendarGrid.getChildren().clear();
        calendarGrid.setHgap(2);
        calendarGrid.setVgap(2);
        calendarGrid.setPadding(new Insets(5));

        // Header: Days of week
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            Label header = new Label(dayNames[i]);
            header.setStyle("-fx-font-weight: bold; -fx-text-alignment: center; -fx-padding: 5;");
            GridPane.setColumnIndex(header, i);
            GridPane.setRowIndex(header, 0);
            calendarGrid.add(header, i, 0);
        }

        // Calendar dates (6 rows x 7 cols = 42 fixed cells)
        LocalDate firstDay = currentCalendarMonth.atDay(1);
        int leadingEmptyCells = firstDay.getDayOfWeek().getValue() % 7; // 0 for Sunday
        int daysInMonth = currentCalendarMonth.lengthOfMonth();

        for (int cell = 0; cell < 42; cell++) {
            int col = cell % 7;
            int row = 1 + (cell / 7);
            int dayNumber = cell - leadingEmptyCells + 1;

            if (dayNumber < 1 || dayNumber > daysInMonth) {
                Label empty = new Label("");
                GridPane.setColumnIndex(empty, col);
                GridPane.setRowIndex(empty, row);
                calendarGrid.add(empty, col, row);
                continue;
            }

            LocalDate date = LocalDate.of(currentCalendarMonth.getYear(), currentCalendarMonth.getMonth(), dayNumber);
            int count = bookingCountByDate.getOrDefault(date, 0);
            java.util.List<String> details = bookingDetailsByDate.getOrDefault(date, java.util.Collections.emptyList());

            VBox dayCell = new VBox();
            dayCell.setStyle("-fx-border-color: #ddd; -fx-padding: 5; -fx-spacing: 3; -fx-background-color: " +
                    (count > 0 ? "#e3f2fd" : "white") + ";");
            dayCell.setPrefHeight(95);
            dayCell.setPrefWidth(90);

            Label dayLabel = new Label(String.valueOf(dayNumber));
            dayLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
            dayCell.getChildren().add(dayLabel);

            if (count > 0) {
                Label countLabel = new Label(count + (count == 1 ? " reserva" : " reserves"));
                countLabel.setStyle("-fx-text-fill: #1976d2; -fx-font-size: 10;");
                dayCell.getChildren().add(countLabel);

                int maxVisible = 3;
                for (int i = 0; i < Math.min(details.size(), maxVisible); i++) {
                    Label bookingLabel = new Label("• " + details.get(i));
                    bookingLabel.setStyle("-fx-font-size: 9; -fx-text-fill: #0d47a1;");
                    dayCell.getChildren().add(bookingLabel);
                }
                if (details.size() > maxVisible) {
                    Label moreLabel = new Label("+" + (details.size() - maxVisible) + " més");
                    moreLabel.setStyle("-fx-font-size: 9; -fx-text-fill: #1565c0;");
                    dayCell.getChildren().add(moreLabel);
                }
            }

            GridPane.setColumnIndex(dayCell, col);
            GridPane.setRowIndex(dayCell, row);
            calendarGrid.add(dayCell, col, row);
        }
    }

    private void loadCalendar() {
        // This method is no longer used; use refreshCalendarView() instead
        refreshCalendarView();
    }

    @FXML
    private void logout() {
        runAsync(() -> {
            try { App.getApiClient().logout(); } catch (Exception ignored) {}
            Platform.runLater(() -> {
                try {
                    App.showLogin();
                } catch (Exception e) {
                    messageLabel.setText("Error tancar sessio");
                }
            });
        });
    }

    private String errorMessage(Exception e) {
        if (e instanceof ApiException api) return api.getMessage();
        return "Error de connexio";
    }

    private void runAsync(Runnable task) {
        Thread worker = new Thread(task, "desktop-backoffice-async");
        worker.setDaemon(true);
        worker.start();
    }

    // Data classes for table rows
    public static class UserRow {
        public Long id;
        public String email;
        public String role;
        public Boolean active;

        public UserRow(Long id, String email, String role, Boolean active) {
            this.id = id;
            this.email = email;
            this.role = role;
            this.active = active;
        }
    }

    public static class FacilityRow {
        public Long id;
        public String name;
        public String type;
        public Integer capacity;
        public String location;
        public String description;

        public FacilityRow(Long id, String name, String type, Integer capacity, String location, String description) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.capacity = capacity;
            this.location = location;
            this.description = description;
        }
    }

    public static class BookingRow {
        public Long id;
        public String userName;
        public String facilityName;
        public String startTime;
        public String endTime;
        public String status;
        public String notes;

        public BookingRow(Long id, String userName, String facilityName, String startTime, String endTime, String status, String notes) {
            this.id = id;
            this.userName = userName;
            this.facilityName = facilityName;
            this.startTime = startTime;
            this.endTime = endTime;
            this.status = status;
            this.notes = notes;
        }
    }

    public static class GroupRow {
        public Long id;
        public String name;
        public String ownerEmail;
        public Integer memberCount;

        public GroupRow(Long id, String name, String ownerEmail, Integer memberCount) {
            this.id = id;
            this.name = name;
            this.ownerEmail = ownerEmail;
            this.memberCount = memberCount;
        }
    }

    public static class IncidentRow {
        public Long id;
        public String title;
        public String facilityName;
        public String status;
        public String statusLabel;
        public String createdAt;

        public IncidentRow(Long id, String title, String facilityName, String status, String statusLabel, String createdAt) {
            this.id = id;
            this.title = title;
            this.facilityName = facilityName;
            this.status = status;
            this.statusLabel = statusLabel;
            this.createdAt = createdAt;
        }
    }
}
