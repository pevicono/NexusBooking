package com.example.nexusbooking.desktop.controller;

import com.example.nexusbooking.desktop.App;
import com.example.nexusbooking.desktop.api.ApiException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Dialog;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HomeController {

    private static final double SIDEBAR_EXPANDED_WIDTH = 240;
    private static final double SIDEBAR_COLLAPSED_WIDTH = 84;
    private static final double COLLAPSE_BREAKPOINT = 980;

    @FXML private Label welcomeLabel;
    @FXML private Label pageTitleLabel;
    @FXML private Label upcomingCountLabel;
    @FXML private Label groupsCountLabel;
    @FXML private Label messageLabel;

    @FXML private HBox rootShell;
    @FXML private VBox sidebar;
    @FXML private Label sidebarBrandLabel;
    @FXML private Label sidebarSubtitleLabel;
    @FXML private ImageView sidebarFooterLogoImage;
    @FXML private VBox sidebarFooterMetaBox;
    @FXML private Button menuToggleButton;

    @FXML private Button navDashboardButton;
    @FXML private Button navBookingsButton;
    @FXML private Button navGroupsButton;
    @FXML private Button logoutButton;
    @FXML private Button createBookingButton;
    @FXML private Button createGroupButton;
    @FXML private Button createIncidentButton;
    @FXML private Button joinGroupButton;

    @FXML private VBox dashboardPage;
    @FXML private VBox bookingsPage;
    @FXML private VBox groupsPage;
    @FXML private StackPane pageStack;
    @FXML private StackPane metricIconBookings;
    @FXML private StackPane metricIconGroups;
    @FXML private StackPane metricIconFacilities;
    @FXML private StackPane metricIconMonthly;

    @FXML private VBox bookingsCardsContainer;
    @FXML private VBox groupsCardsContainer;
    @FXML private VBox upcomingBookingsContainer;
    @FXML private VBox dashboardGroupsContainer;
    @FXML private VBox availableFacilitiesContainer;
    @FXML private TextField joinGroupId;
    @FXML private Label dashboardFacilitiesCountLabel;
    @FXML private Label dashboardBookingsMonthCountLabel;

    private Long currentUserId;
    private boolean compactSidebar;
    private boolean sidebarExpanded = true;

    private static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter UI_DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private void initialize() {
        setupNavigation();
        setupResponsiveLayout();
        refreshAll();
        showDashboardPage();
    }

    private void setupNavigation() {
        setButtonGraphic(menuToggleButton, FontAwesomeSolid.BARS, 14, Color.web("#1d4174"), 18, false);
        setButtonGraphic(navDashboardButton, FontAwesomeSolid.HOME, 14);
        setButtonGraphic(navBookingsButton, FontAwesomeSolid.CALENDAR, 14);
        setButtonGraphic(navGroupsButton, FontAwesomeSolid.USERS, 14);
        setButtonGraphic(logoutButton, FontAwesomeSolid.SIGN_OUT_ALT, 13);
        setButtonGraphic(createBookingButton, FontAwesomeSolid.PLUS, 13);
        setButtonGraphic(createGroupButton, FontAwesomeSolid.PLUS, 13);
        setButtonGraphic(createIncidentButton, FontAwesomeSolid.EXCLAMATION_TRIANGLE, 13);
        setButtonGraphic(joinGroupButton, FontAwesomeSolid.ARROW_RIGHT, 13);

        Image source = new Image(getClass().getResourceAsStream("/com/example/nexusbooking/desktop/images/logo.png"));
        WritableImage whiteLogo = createWhiteLogo(source);
        if (sidebarFooterLogoImage != null) {
            sidebarFooterLogoImage.setImage(whiteLogo);
        }

        setupMetricIcons();
        showOnlyPage(dashboardPage, navDashboardButton);
    }

    private void setupMetricIcons() {
        installMetricIcon(metricIconBookings, FontAwesomeSolid.CALENDAR_ALT, 24);
        installMetricIcon(metricIconGroups, FontAwesomeSolid.OBJECT_GROUP, 24);
        installMetricIcon(metricIconFacilities, FontAwesomeSolid.BUILDING, 24);
        installMetricIcon(metricIconMonthly, FontAwesomeSolid.STAR, 24);
    }

    private void installMetricIcon(StackPane container, FontAwesomeSolid icon, int size) {
        if (container == null) {
            return;
        }
        container.getChildren().clear();
        FontIcon iconNode = FontIcon.of(icon, size);
        iconNode.setIconColor(Color.WHITE);
        container.getChildren().add(iconNode);
    }

    private void setButtonGraphic(Button button, FontAwesomeSolid iconCode, int size) {
        setButtonGraphic(button, iconCode, size, resolveButtonIconColor(button), 20, true);
    }

    private void setButtonGraphic(Button button, FontAwesomeSolid iconCode, int size, Color color, double slotWidth, boolean addStyleClass) {
        if (button == null) {
            return;
        }
        FontIcon icon = FontIcon.of(iconCode, size);
        icon.setIconColor(color);
        StackPane iconSlot = new StackPane(icon);
        iconSlot.getStyleClass().add("button-icon-slot");
        iconSlot.setMinWidth(slotWidth);
        iconSlot.setPrefWidth(slotWidth);
        iconSlot.setMaxWidth(slotWidth);
        iconSlot.setMinHeight(18);
        iconSlot.setPrefHeight(18);
        button.setGraphic(iconSlot);
        button.setContentDisplay(ContentDisplay.LEFT);
        button.setGraphicTextGap(10);
        if (addStyleClass && !button.getStyleClass().contains("icon-button")) {
            button.getStyleClass().add("icon-button");
        }
    }

    private Color resolveButtonIconColor(Button button) {
        if (button == null) {
            return Color.WHITE;
        }
        if (button.getStyleClass().contains("btn-secondary")) {
            return Color.web("#2a3b58");
        }
        return Color.WHITE;
    }

    private void setupResponsiveLayout() {
        if (pageStack == null) {
            return;
        }
        pageStack.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                return;
            }
            updateResponsiveLayout(newScene.getWidth());
            newScene.widthProperty().addListener((widthObs, oldWidth, newWidth) -> updateResponsiveLayout(newWidth.doubleValue()));
        });
    }

    private void updateResponsiveLayout(double width) {
        boolean shouldCollapse = width < COLLAPSE_BREAKPOINT;
        if (shouldCollapse != compactSidebar) {
            compactSidebar = shouldCollapse;
            if (!compactSidebar) {
                sidebarExpanded = true;
            }
            applySidebarState();
        } else if (compactSidebar) {
            applySidebarState();
        }
    }

    private void applySidebarState() {
        if (sidebar == null) {
            return;
        }
        boolean collapsed = compactSidebar && !sidebarExpanded;
        sidebar.getStyleClass().remove("sidebar-collapsed");
        if (collapsed) {
            sidebar.getStyleClass().add("sidebar-collapsed");
        }

        double width = collapsed ? SIDEBAR_COLLAPSED_WIDTH : SIDEBAR_EXPANDED_WIDTH;
        sidebar.setMinWidth(width);
        sidebar.setPrefWidth(width);
        sidebar.setMaxWidth(width);

        if (menuToggleButton != null) {
            menuToggleButton.setVisible(compactSidebar);
            menuToggleButton.setManaged(compactSidebar);
        }
        if (sidebarBrandLabel != null) {
            sidebarBrandLabel.setVisible(!collapsed);
            sidebarBrandLabel.setManaged(!collapsed);
        }
        if (sidebarSubtitleLabel != null) {
            sidebarSubtitleLabel.setVisible(true);
            sidebarSubtitleLabel.setManaged(true);
        }
        if (sidebarFooterMetaBox != null) {
            sidebarFooterMetaBox.setVisible(true);
            sidebarFooterMetaBox.setManaged(true);
        }

        updateSidebarButtonLabel(navDashboardButton, collapsed);
        updateSidebarButtonLabel(navBookingsButton, collapsed);
        updateSidebarButtonLabel(navGroupsButton, collapsed);
    }

    private void updateSidebarButtonLabel(Button button, boolean collapsed) {
        if (button == null) {
            return;
        }
        if (button.getUserData() == null) {
            button.setUserData(button.getText());
        }
        String label = String.valueOf(button.getUserData());
        button.setText(collapsed ? "" : label);
        button.setTooltip(collapsed ? new Tooltip(label) : null);
        button.setContentDisplay(ContentDisplay.LEFT);
    }

    @FXML
    private void toggleSidebar() {
        if (!compactSidebar) {
            return;
        }
        sidebarExpanded = !sidebarExpanded;
        applySidebarState();
    }

    private void showOnlyPage(VBox selectedPage, Button selectedButton) {
        java.util.List.of(dashboardPage, bookingsPage, groupsPage).forEach(page -> {
            if (page != null) {
                boolean visible = page == selectedPage;
                page.setVisible(visible);
                page.setManaged(visible);
            }
        });

        java.util.List.of(navDashboardButton, navBookingsButton, navGroupsButton).forEach(button -> {
            if (button != null) {
                button.getStyleClass().remove("nav-button-active");
            }
        });

        if (selectedButton != null && !selectedButton.getStyleClass().contains("nav-button-active")) {
            selectedButton.getStyleClass().add("nav-button-active");
        }
    }

    @FXML private void showDashboardPage() { pageTitleLabel.setText("Dashboard"); showOnlyPage(dashboardPage, navDashboardButton); }
    @FXML private void showBookingsPage() { pageTitleLabel.setText("Reserves"); showOnlyPage(bookingsPage, navBookingsButton); }
    @FXML private void showGroupsPage() { pageTitleLabel.setText("Grups"); showOnlyPage(groupsPage, navGroupsButton); }

    @FXML
    private void refreshAll() {
        messageLabel.setText("");
        runAsync(() -> {
            try {
                JsonArray bookings = App.getApiClient().getMyBookings();
                JsonArray groups = App.getApiClient().getMyGroups();
                JsonArray facilities = App.getApiClient().getFacilities();
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
                    if (dashboardFacilitiesCountLabel != null) {
                        dashboardFacilitiesCountLabel.setText(String.valueOf(facilities.size()));
                    }
                    if (dashboardBookingsMonthCountLabel != null) {
                        int thisMonthBookings = countBookingsThisMonth(bookings);
                        dashboardBookingsMonthCountLabel.setText(String.valueOf(thisMonthBookings));
                    }

                    renderDashboardUpcomingBookings(bookings);
                    renderDashboardGroupWidgets(groups);
                    renderDashboardAvailableFacilities(facilities);
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

        java.util.List<JsonObject> futureBookings = new java.util.ArrayList<>();
        java.util.List<JsonObject> pastBookings = new java.util.ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < bookings.size(); i++) {
            JsonObject b = bookings.get(i).getAsJsonObject();
            if (!b.has("startTime") || b.get("startTime").isJsonNull()) continue;
            try {
                LocalDateTime start = LocalDateTime.parse(b.get("startTime").getAsString(), ISO_DATE_TIME);
                if (start.isAfter(now)) {
                    futureBookings.add(b);
                } else {
                    pastBookings.add(b);
                }
            } catch (Exception ignored) {
            }
        }

        futureBookings.sort((a, b) -> {
            LocalDateTime aStart = LocalDateTime.parse(a.get("startTime").getAsString(), ISO_DATE_TIME);
            LocalDateTime bStart = LocalDateTime.parse(b.get("startTime").getAsString(), ISO_DATE_TIME);
            return aStart.compareTo(bStart);
        });
        pastBookings.sort((a, b) -> {
            LocalDateTime aStart = LocalDateTime.parse(a.get("startTime").getAsString(), ISO_DATE_TIME);
            LocalDateTime bStart = LocalDateTime.parse(b.get("startTime").getAsString(), ISO_DATE_TIME);
            return bStart.compareTo(aStart);
        });

        if (!futureBookings.isEmpty()) {
            Label futureTitle = new Label("Pròximes reserves");
            futureTitle.getStyleClass().add("section-subtitle");
            bookingsCardsContainer.getChildren().add(futureTitle);
        }

        futureBookings.forEach(e -> {
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
            setButtonGraphic(editBtn, FontAwesomeSolid.PENCIL_ALT, 12, Color.web("#2a3b58"), 18, true);
            setButtonGraphic(cancelBtn, FontAwesomeSolid.TIMES, 12, Color.WHITE, 18, true);

            long bookingId = b.get("id").getAsLong();
            editBtn.setId("bookingEditBtn-" + bookingId);
            cancelBtn.setId("bookingCancelBtn-" + bookingId);
            editBtn.setOnAction(evt -> showEditBookingDialog(b));
            cancelBtn.setOnAction(evt -> cancelBooking(bookingId));

            actions.getChildren().addAll(editBtn, cancelBtn);
            card.getChildren().addAll(title, details, actions);
            bookingsCardsContainer.getChildren().add(card);
        });

        if (!pastBookings.isEmpty()) {
            Label pastTitle = new Label("Reserves passades");
            pastTitle.getStyleClass().add("section-subtitle");
            bookingsCardsContainer.getChildren().add(pastTitle);
            pastBookings.forEach(e -> {
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
                title.setStyle("-fx-text-fill: #888888;");

                Label details = new Label("Grup: " + group + "\nInici: " + start + "\nFinal: " + end + "\nEstat: " + status);
                details.setWrapText(true);

                card.getChildren().addAll(title, details);
                bookingsCardsContainer.getChildren().add(card);
            });
        }
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
            setButtonGraphic(editBtn, FontAwesomeSolid.PENCIL_ALT, 12, Color.web("#2a3b58"), 18, true);
            long groupId = g.get("id").getAsLong();
            editBtn.setId("groupEditBtn-" + groupId);
            editBtn.setOnAction(evt -> showEditGroupDialog(g));

            Button membersBtn = new Button("Membres");
            membersBtn.getStyleClass().add("btn-secondary");
            setButtonGraphic(membersBtn, FontAwesomeSolid.USERS, 12, Color.web("#2a3b58"), 18, true);
            membersBtn.setOnAction(evt -> showGroupMembersDialog(g));

            actions.getChildren().add(membersBtn);
            actions.getChildren().add(editBtn);

            boolean canLeave = ownerId == null || currentUserId == null || !ownerId.equals(currentUserId);
            Button leaveBtn = new Button("Sortir del grup");
            leaveBtn.getStyleClass().add("btn-danger");
            setButtonGraphic(leaveBtn, FontAwesomeSolid.SIGN_OUT_ALT, 12, Color.WHITE, 18, true);
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

    private int countBookingsThisMonth(JsonArray bookings) {
        LocalDateTime now = LocalDateTime.now();
        int count = 0;
        for (int i = 0; i < bookings.size(); i++) {
            JsonObject booking = bookings.get(i).getAsJsonObject();
            if (!booking.has("startTime") || booking.get("startTime").isJsonNull()) {
                continue;
            }
            try {
                LocalDateTime start = LocalDateTime.parse(booking.get("startTime").getAsString(), ISO_DATE_TIME);
                if (start.getYear() == now.getYear() && start.getMonthValue() == now.getMonthValue()) {
                    count++;
                }
            } catch (Exception ignored) {
            }
        }
        return count;
    }

    private void renderDashboardUpcomingBookings(JsonArray bookings) {
        if (upcomingBookingsContainer == null) {
            return;
        }
        upcomingBookingsContainer.getChildren().clear();

        java.util.List<JsonObject> upcoming = new java.util.ArrayList<>();
        for (int i = 0; i < bookings.size(); i++) {
            JsonObject booking = bookings.get(i).getAsJsonObject();
            if (!booking.has("startTime") || booking.get("startTime").isJsonNull()) {
                continue;
            }
            try {
                LocalDateTime start = LocalDateTime.parse(booking.get("startTime").getAsString(), ISO_DATE_TIME);
                if (start.isAfter(LocalDateTime.now())) {
                    upcoming.add(booking);
                }
            } catch (Exception ignored) {
            }
        }

        upcoming.sort((a, b) -> {
            LocalDateTime aStart = LocalDateTime.parse(a.get("startTime").getAsString(), ISO_DATE_TIME);
            LocalDateTime bStart = LocalDateTime.parse(b.get("startTime").getAsString(), ISO_DATE_TIME);
            return aStart.compareTo(bStart);
        });

        if (upcoming.isEmpty()) {
            Label empty = new Label("No tens reserves futures.");
            empty.getStyleClass().add("dashboard-empty-state");
            upcomingBookingsContainer.getChildren().add(empty);
            return;
        }

        int max = Math.min(3, upcoming.size());
        for (int i = 0; i < max; i++) {
            JsonObject booking = upcoming.get(i);
            String facility = booking.has("facilityName") ? booking.get("facilityName").getAsString() : "Instal·lació";
            String when = booking.has("startTime") ? formatDateTime(booking.get("startTime").getAsString()) : "-";
            upcomingBookingsContainer.getChildren().add(buildDashboardItem(facility, when));
        }
    }

    private void renderDashboardGroupWidgets(JsonArray groups) {
        if (dashboardGroupsContainer == null) {
            return;
        }
        dashboardGroupsContainer.getChildren().clear();

        if (groups.isEmpty()) {
            Label empty = new Label("No formes part de cap grup.");
            empty.getStyleClass().add("dashboard-empty-state");
            dashboardGroupsContainer.getChildren().add(empty);
            return;
        }

        int max = Math.min(3, groups.size());
        for (int i = 0; i < max; i++) {
            JsonObject group = groups.get(i).getAsJsonObject();
            String name = group.has("name") ? group.get("name").getAsString() : "Grup";
            int members = group.has("memberCount") ? group.get("memberCount").getAsInt() : 0;
            dashboardGroupsContainer.getChildren().add(buildDashboardItem(name, members + " membres"));
        }
    }

    private void renderDashboardAvailableFacilities(JsonArray facilities) {
        if (availableFacilitiesContainer == null) {
            return;
        }
        availableFacilitiesContainer.getChildren().clear();

        if (facilities.isEmpty()) {
            Label empty = new Label("No hi ha instal·lacions disponibles.");
            empty.getStyleClass().add("dashboard-empty-state");
            availableFacilitiesContainer.getChildren().add(empty);
            return;
        }

        int max = Math.min(3, facilities.size());
        for (int i = 0; i < max; i++) {
            JsonObject facility = facilities.get(i).getAsJsonObject();
            String name = facility.has("name") ? facility.get("name").getAsString() : "Instal·lació";
            String location = facility.has("location") && !facility.get("location").isJsonNull()
                    ? facility.get("location").getAsString()
                    : "Ubicació no indicada";
            HBox row = new HBox(8);
            row.getStyleClass().add("dashboard-list-item");
            row.setFillHeight(true);

            VBox text = new VBox(2);
            Label title = new Label(name);
            title.getStyleClass().add("dashboard-list-item-title");
            Label subtitle = new Label(location);
            subtitle.getStyleClass().add("dashboard-list-item-subtitle");
            text.getChildren().addAll(title, subtitle);

            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            Button bookBtn = new Button("Reservar");
            bookBtn.getStyleClass().add("btn-primary");
            setButtonGraphic(bookBtn, FontAwesomeSolid.CALENDAR_PLUS, 11, Color.WHITE, 16, true);
            bookBtn.setOnAction(evt -> showCreateBookingDialog());

            row.getChildren().addAll(text, spacer, bookBtn);
            availableFacilitiesContainer.getChildren().add(row);
        }
    }

    private HBox buildDashboardItem(String titleText, String subtitleText) {
        HBox item = new HBox(8);
        item.getStyleClass().add("dashboard-list-item");
        VBox text = new VBox(2);
        Label title = new Label(titleText);
        title.getStyleClass().add("dashboard-list-item-title");
        Label subtitle = new Label(subtitleText);
        subtitle.getStyleClass().add("dashboard-list-item-subtitle");
        text.getChildren().addAll(title, subtitle);
        item.getChildren().add(text);
        return item;
    }

    private void showGroupMembersDialog(JsonObject group) {
        long groupId = group.get("id").getAsLong();
        Long ownerId = group.has("ownerId") && !group.get("ownerId").isJsonNull() ? group.get("ownerId").getAsLong() : null;
        boolean isOwner = ownerId != null && currentUserId != null && ownerId.equals(currentUserId);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Membres del grup");
        dialog.setHeaderText(group.has("name") ? group.get("name").getAsString() : "Grup");

        VBox content = new VBox(10);
        content.setPadding(new Insets(14));
        Label info = new Label(isOwner ? "Com a owner pots fer fora membres." : "Només l'owner pot fer fora membres.");
        info.getStyleClass().add("field-value");
        VBox membersBox = new VBox(8);
        content.getChildren().addAll(info, membersBox);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        applyDialogStyling(dialog, content);

        loadGroupMembers(groupId, isOwner, membersBox);
        dialog.showAndWait();
    }

    private void loadGroupMembers(long groupId, boolean isOwner, VBox membersBox) {
        membersBox.getChildren().clear();
        Label loading = new Label("Carregant membres...");
        loading.getStyleClass().add("dashboard-empty-state");
        membersBox.getChildren().add(loading);

        runAsync(() -> {
            try {
                JsonArray members = App.getApiClient().getGroupMembers(groupId);
                Platform.runLater(() -> {
                    membersBox.getChildren().clear();
                    if (members.isEmpty()) {
                        Label empty = new Label("No hi ha membres al grup.");
                        empty.getStyleClass().add("dashboard-empty-state");
                        membersBox.getChildren().add(empty);
                        return;
                    }

                    for (int i = 0; i < members.size(); i++) {
                        JsonObject member = members.get(i).getAsJsonObject();
                        long userId = member.has("userId") ? member.get("userId").getAsLong() : -1L;
                        String email = member.has("email") ? member.get("email").getAsString() : "usuari";
                        String role = member.has("role") ? member.get("role").getAsString() : "MEMBER";

                        HBox row = new HBox(8);
                        row.getStyleClass().add("dashboard-list-item");

                        VBox text = new VBox(2);
                        Label title = new Label(email);
                        title.getStyleClass().add("dashboard-list-item-title");
                        Label subtitle = new Label(role);
                        subtitle.getStyleClass().add("dashboard-list-item-subtitle");
                        text.getChildren().addAll(title, subtitle);

                        Region spacer = new Region();
                        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                        row.getChildren().addAll(text, spacer);

                        if (isOwner && userId > 0 && !"OWNER".equalsIgnoreCase(role)) {
                            Button removeBtn = new Button("Treure");
                            removeBtn.getStyleClass().add("btn-danger");
                            setButtonGraphic(removeBtn, FontAwesomeSolid.TRASH, 11, Color.WHITE, 16, true);
                            removeBtn.setOnAction(evt -> removeGroupMember(groupId, userId, membersBox, isOwner));
                            row.getChildren().add(removeBtn);
                        }

                        membersBox.getChildren().add(row);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    membersBox.getChildren().clear();
                    Label error = new Label(errorMessage(e));
                    error.getStyleClass().add("error-label");
                    membersBox.getChildren().add(error);
                });
            }
        });
    }

    private void removeGroupMember(long groupId, long userId, VBox membersBox, boolean isOwner) {
        Dialog<ButtonType> confirm = new Dialog<>();
        confirm.setTitle("Confirmar");
        confirm.setHeaderText("Vols treure aquest membre del grup?");
        confirm.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        applyDialogStyling(confirm.getDialogPane());
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        runAsync(() -> {
            try {
                App.getApiClient().removeGroupMember(groupId, userId);
                Platform.runLater(() -> {
                    refreshAll();
                    loadGroupMembers(groupId, isOwner, membersBox);
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
        dialog.setHeaderText("Selecciona una reserva");

        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        TextField bookingField = createPickerField("Selecciona una reserva");
        TextField titleField = new TextField();
        titleField.setPromptText("Títol");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Descripció");
        descriptionArea.setPrefRowCount(4);

        java.util.List<String> bookingOptions = new java.util.ArrayList<>();
        java.util.Map<String, JsonObject> bookingMap = new java.util.HashMap<>();
        final Long[] selectedFacilityId = {null};
        final String[] selectedBooking = {null};

        try {
            JsonArray myBookings = App.getApiClient().getMyBookings();
            for (int i = 0; i < myBookings.size(); i++) {
                JsonObject b = myBookings.get(i).getAsJsonObject();
                try {
                    LocalDateTime start = LocalDateTime.parse(b.get("startTime").getAsString(), ISO_DATE_TIME);
                    LocalDateTime now = LocalDateTime.now();
                    if (start.isAfter(now.minusHours(1))) continue;
                } catch (Exception ignored) {
                    continue;
                }
                String facility = b.has("facilityName") ? b.get("facilityName").getAsString() : "Instal·lació";
                String when = b.has("startTime") ? formatDateTime(b.get("startTime").getAsString()) : "-";
                String key = b.get("id").getAsLong() + " - " + facility + " (" + when + ")";
                bookingOptions.add(key);
                bookingMap.put(key, b);
            }
        } catch (Exception e) {
            messageLabel.setText(errorMessage(e));
            return;
        }

        if (bookingOptions.isEmpty()) {
            messageLabel.setText("No hi ha reserves passades o en curs");
            return;
        }

        Button bookingPickButton = new Button("Cercar...");
        bookingPickButton.getStyleClass().add("btn-secondary");
        bookingPickButton.setOnAction(evt -> {
            String picked = openSearchablePickerDialog("Selecciona una reserva", "Cercar reserva", bookingOptions, selectedBooking[0]);
            if (picked != null) {
                selectedBooking[0] = picked;
                bookingField.setText(picked);
                JsonObject booking = bookingMap.get(picked);
                if (booking != null && booking.has("facilityId")) {
                    selectedFacilityId[0] = booking.get("facilityId").getAsLong();
                }
            }
        });

        content.getChildren().addAll(
                new Label("Reserva *"), createPickerRow(bookingField, bookingPickButton),
                new Label("Títol *"), titleField,
                new Label("Descripció:"), descriptionArea
        );

        Label validationLabel = createDialogValidationLabel(content);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        applyDialogStyling(dialog, content);
        installDialogValidation(dialog, validationLabel, () -> {
            String title = titleField.getText() == null ? "" : titleField.getText().trim();
            return (selectedBooking[0] == null || title.isBlank()) ? "Completa tots els camps obligatoris" : null;
        });

        if (dialog.showAndWait().isEmpty()) {
            return;
        }

        String title = titleField.getText() == null ? "" : titleField.getText().trim();
        String description = descriptionArea.getText() == null ? "" : descriptionArea.getText().trim();

        runAsync(() -> {
            try {
                App.getApiClient().createIncident(selectedFacilityId[0], title, description);
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
                new Label("Instal·lació *"), facilityCombo,
                new Label("Grup *"), groupCombo,
                new Label("Data inici *"), startDatePicker,
                new Label("Hora inici *"), startTimeCombo,
                new Label("Data final *"), endDatePicker,
                new Label("Hora final *"), endTimeCombo,
                new Label("Notes:"), notesField
        );

        Label validationLabel = createDialogValidationLabel(content);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        applyDialogStyling(dialog, content);
        installDialogValidation(dialog, validationLabel, () -> {
            if (facilityCombo.getItems().isEmpty()) {
                return "No hi ha instal·lacions disponibles per reservar";
            }
            if (groupCombo.getItems().isEmpty()) {
                return "No formes part de cap grup. Crea'n un o uneix-te abans de reservar";
            }
            if (facilityCombo.getValue() == null || groupCombo.getValue() == null
                    || startDatePicker.getValue() == null || endDatePicker.getValue() == null
                    || startTimeCombo.getValue() == null || endTimeCombo.getValue() == null) {
                return "Completa tots els camps obligatoris";
            }
            String startValue = startDatePicker.getValue() + "T" + startTimeCombo.getValue() + ":00";
            String endValue = endDatePicker.getValue() + "T" + endTimeCombo.getValue() + ":00";
            if (!isStartInFuture(startValue)) {
                return "L'inici de la reserva ha de ser en futur";
            }
            if (!isEndAfterStart(startValue, endValue)) {
                return "La data/hora final ha de ser posterior a la inicial";
            }
            return null;
        });

        if (dialog.showAndWait().isEmpty()) return;

        long facilityId = Long.parseLong(facilityCombo.getValue().split(" - ")[0]);
        long groupId = Long.parseLong(groupCombo.getValue().split(" - ")[0]);
        String start = startDatePicker.getValue() + "T" + startTimeCombo.getValue() + ":00";
        String end = endDatePicker.getValue() + "T" + endTimeCombo.getValue() + ":00";
        String notes = notesField.getText() == null ? "" : notesField.getText().trim();

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
                new Label("Instal·lació *"), facilityCombo,
                new Label("Grup *"), groupCombo,
                new Label("Data inici *"), startDatePicker,
                new Label("Hora inici *"), startTimeCombo,
                new Label("Data final *"), endDatePicker,
                new Label("Hora final *"), endTimeCombo,
                new Label("Notes:"), notesField
        );

        Label validationLabel = createDialogValidationLabel(content);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        applyDialogStyling(dialog, content);
        installDialogValidation(dialog, validationLabel, () -> {
            if (facilityCombo.getValue() == null || groupCombo.getValue() == null
                    || startDatePicker.getValue() == null || endDatePicker.getValue() == null
                    || startTimeCombo.getValue() == null || endTimeCombo.getValue() == null) {
                return "Completa tots els camps obligatoris";
            }
            String startValue = startDatePicker.getValue() + "T" + startTimeCombo.getValue() + ":00";
            String endValue = endDatePicker.getValue() + "T" + endTimeCombo.getValue() + ":00";
            if (!isEndAfterStart(startValue, endValue)) {
                return "La data/hora final ha de ser posterior a la inicial";
            }
            return null;
        });

        if (dialog.showAndWait().isEmpty()) return;

        long bookingId = booking.get("id").getAsLong();
        long facilityId = Long.parseLong(facilityCombo.getValue().split(" - ")[0]);
        long groupId = Long.parseLong(groupCombo.getValue().split(" - ")[0]);
        String start = startDatePicker.getValue() + "T" + startTimeCombo.getValue() + ":00";
        String end = endDatePicker.getValue() + "T" + endTimeCombo.getValue() + ":00";
        String notes = notesField.getText() == null ? "" : notesField.getText().trim();

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
        applyDialogStyling(confirm.getDialogPane());
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
                new Label("Nom del grup *"), nameField,
                new Label("Descripció:"), descriptionField
        );

        Label validationLabel = createDialogValidationLabel(content);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        applyDialogStyling(dialog, content);
        installDialogValidation(dialog, validationLabel, () -> {
            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            return name.isBlank() ? "El nom del grup és obligatori" : null;
        });

        if (dialog.showAndWait().isEmpty()) return;

        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String description = descriptionField.getText() == null ? "" : descriptionField.getText().trim();

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
                new Label("Nom del grup *"), nameField,
                new Label("Descripció:"), descriptionField
        );

        Label validationLabel = createDialogValidationLabel(content);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        applyDialogStyling(dialog, content);
        installDialogValidation(dialog, validationLabel, () -> {
            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            return name.isBlank() ? "El nom del grup és obligatori" : null;
        });

        if (dialog.showAndWait().isEmpty()) return;

        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String description = descriptionField.getText() == null ? "" : descriptionField.getText().trim();

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
        applyDialogStyling(confirm.getDialogPane());
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

    private TextField createPickerField(String promptText) {
        TextField field = new TextField();
        field.setEditable(false);
        field.setPromptText(promptText);
        return field;
    }

    private HBox createPickerRow(TextField field, Button button) {
        HBox row = new HBox(8, field, button);
        field.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(field, javafx.scene.layout.Priority.ALWAYS);
        return row;
    }

    private String openSearchablePickerDialog(String title, String header, java.util.List<String> sourceOptions, String selectedValue) {
        Dialog<String> picker = new Dialog<>();
        picker.setTitle(title);
        picker.setHeaderText(header);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        TextField searchField = new TextField();
        searchField.setPromptText("Escriu per filtrar...");
        searchField.getStyleClass().add("picker-search-field");
        javafx.scene.control.ListView<String> listView = new javafx.scene.control.ListView<>();
        listView.setPrefHeight(280);
        listView.getStyleClass().add("picker-list-view");
        ObservableList<String> options = FXCollections.observableArrayList(sourceOptions);
        listView.setItems(options);

        Runnable applyFilter = () -> {
            String filter = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase(java.util.Locale.ROOT);
            if (filter.isBlank()) {
                listView.setItems(FXCollections.observableArrayList(sourceOptions));
                if (selectedValue != null) {
                    listView.getSelectionModel().select(selectedValue);
                }
                return;
            }
            ObservableList<String> filtered = FXCollections.observableArrayList();
            for (String option : sourceOptions) {
                if (option.toLowerCase(java.util.Locale.ROOT).contains(filter)) {
                    filtered.add(option);
                }
            }
            listView.setItems(filtered);
            if (selectedValue != null && filtered.contains(selectedValue)) {
                listView.getSelectionModel().select(selectedValue);
            }
        };
        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilter.run());

        if (selectedValue != null) {
            listView.getSelectionModel().select(selectedValue);
        }
        if (!options.isEmpty() && listView.getSelectionModel().getSelectedItem() == null) {
            listView.getSelectionModel().select(0);
        }

        content.getChildren().addAll(searchField, listView);
        picker.getDialogPane().setContent(content);
        applyDialogStyling(picker, content);
        picker.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Label validationLabel = createDialogValidationLabel(content);
        installDialogValidation(picker, validationLabel, () ->
            listView.getSelectionModel().getSelectedItem() == null ? "Selecciona un element" : null
        );

        java.util.Optional<String> result = picker.showAndWait();
        return result.isPresent() ? listView.getSelectionModel().getSelectedItem() : null;
    }

    private String errorMessage(Exception e) {
        if (e instanceof ApiException api) return api.getMessage();
        return "Error de connexió amb el servidor";
    }

    private Label createDialogValidationLabel(VBox content) {
        Label validationLabel = new Label();
        validationLabel.getStyleClass().addAll("error-label", "dialog-validation-label");
        validationLabel.setWrapText(true);
        validationLabel.setManaged(false);
        validationLabel.setVisible(false);
        content.getChildren().add(validationLabel);
        return validationLabel;
    }

    private void applyDialogStyling(DialogPane dialogPane) {
        dialogPane.getStylesheets().add(
            getClass().getResource("/com/example/nexusbooking/desktop/css/style.css").toExternalForm()
        );
        if (!dialogPane.getStyleClass().contains("dialog-pane")) {
            dialogPane.getStyleClass().add("dialog-pane");
        }
    }

    private void applyDialogStyling(Dialog<?> dialog, VBox content) {
        applyDialogStyling(dialog.getDialogPane());

        if (!content.getStyleClass().contains("dialog-content")) {
            content.getStyleClass().add("dialog-content");
        }

        for (javafx.scene.Node node : content.getChildren()) {
            if (node instanceof Label) {
                node.getStyleClass().add("dialog-label");
            } else if (node instanceof TextField || node instanceof PasswordField
                    || node instanceof ComboBox<?> || node instanceof DatePicker) {
                node.getStyleClass().add("dialog-field");
            } else if (node instanceof TextArea) {
                node.getStyleClass().add("dialog-field");
            }
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefViewportHeight(520);
        scrollPane.setMaxHeight(560);
        if (!scrollPane.getStyleClass().contains("dialog-content-scroll")) {
            scrollPane.getStyleClass().add("dialog-content-scroll");
        }
        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    }

    private void installDialogValidation(Dialog<?> dialog, Label validationLabel, java.util.function.Supplier<String> validator) {
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        if (okButton == null) {
            return;
        }
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            String error = validator.get();
            if (error == null || error.isBlank()) {
                validationLabel.setText("");
                validationLabel.setManaged(false);
                validationLabel.setVisible(false);
                return;
            }
            validationLabel.setText(error);
            validationLabel.setManaged(true);
            validationLabel.setVisible(true);
            event.consume();
        });
    }

    private void runAsync(Runnable task) {
        Thread worker = new Thread(task, "desktop-home-async");
        worker.setDaemon(true);
        worker.start();
    }
}
