package com.example.nexusbooking.desktop.controller;

import com.example.nexusbooking.desktop.App;
import com.example.nexusbooking.desktop.api.ApiException;
import com.example.nexusbooking.desktop.model.UserResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class BackofficeController {

    private static final double SIDEBAR_EXPANDED_WIDTH = 240;
    private static final double SIDEBAR_COLLAPSED_WIDTH = 84;
    private static final double COLLAPSE_BREAKPOINT = 1100;

    @FXML private HBox rootShell;
    @FXML private VBox sidebar;
    @FXML private ImageView logoImage;
    @FXML private ImageView sidebarFooterLogoImage;
    @FXML private VBox sidebarFooterMetaBox;
    @FXML private Label sidebarBrandLabel;
    @FXML private Button menuToggleButton;
    @FXML private Label pageTitleLabel;
    @FXML private Label statsLabel;
    @FXML private Label messageLabel;
    @FXML private Label currentUserLabel;

    @FXML private Button navDashboardButton;
    @FXML private Button navUsersButton;
    @FXML private Button navFacilitiesButton;
    @FXML private Button navBookingsButton;
    @FXML private Button navGroupsButton;
    @FXML private Button navIncidentsButton;
    @FXML private Button navCalendarButton;
    @FXML private Button refreshButton;
    @FXML private Button logoutButton;
    @FXML private Button createUserButton;
    @FXML private Button createFacilityButton;
    @FXML private Button createBookingButton;
    @FXML private Button createIncidentButton;
    @FXML private Button createAdminGroupButton;

    @FXML private VBox dashboardPage;
    @FXML private VBox usersPage;
    @FXML private VBox facilitiesPage;
    @FXML private VBox bookingsPage;
    @FXML private VBox incidentsPage;
    @FXML private VBox groupsPage;
    @FXML private VBox calendarPage;
    @FXML private StackPane pageStack;

    @FXML private Label dashboardUsersCountLabel;
    @FXML private Label dashboardBookingsCountLabel;
    @FXML private Label dashboardGroupsCountLabel;
    @FXML private Label dashboardIncidentsCountLabel;
    @FXML private Label dashboardUsersTrendLabel;
    @FXML private Label dashboardBookingsTrendLabel;
    @FXML private Label dashboardGroupsTrendLabel;
    @FXML private Label dashboardIncidentsTrendLabel;
    @FXML private VBox upcomingBookingsContainer;
    @FXML private VBox recentActivityContainer;
    @FXML private VBox chartContainer;

    @FXML private ListView<UserRow> usersList;
    @FXML private ListView<FacilityRow> facilitiesList;
    @FXML private ListView<BookingRow> bookingsList;
    @FXML private ListView<IncidentRow> incidentsList;
    @FXML private ListView<GroupRow> groupsList;
    @FXML private TextField usersSearchField;
    @FXML private TextField facilitiesSearchField;
    @FXML private TextField bookingsSearchField;
    @FXML private TextField incidentsSearchField;
    @FXML private TextField groupsSearchField;
    @FXML private Label usersHeaderId;
    @FXML private Label usersHeaderName;
    @FXML private Label usersHeaderEmail;
    @FXML private Label usersHeaderRole;
    @FXML private Label usersHeaderStatus;
    @FXML private Label usersHeaderActions;
    @FXML private Label facilitiesHeaderId;
    @FXML private Label facilitiesHeaderName;
    @FXML private Label facilitiesHeaderTypeCapacity;
    @FXML private Label facilitiesHeaderLocation;
    @FXML private Label facilitiesHeaderStatus;
    @FXML private Label facilitiesHeaderActions;
    @FXML private Label bookingsHeaderId;
    @FXML private Label bookingsHeaderFacility;
    @FXML private Label bookingsHeaderUser;
    @FXML private Label bookingsHeaderStart;
    @FXML private Label bookingsHeaderStatus;
    @FXML private Label bookingsHeaderActions;
    @FXML private Label incidentsHeaderId;
    @FXML private Label incidentsHeaderTitle;
    @FXML private Label incidentsHeaderFacility;
    @FXML private Label incidentsHeaderDate;
    @FXML private Label incidentsHeaderStatus;
    @FXML private Label incidentsHeaderActions;
    @FXML private Label groupsHeaderId;
    @FXML private Label groupsHeaderName;
    @FXML private Label groupsHeaderOwner;
    @FXML private Label groupsHeaderMembers;
    @FXML private Label groupsHeaderCode;
    @FXML private Label groupsHeaderActions;

    @FXML private ComboBox<String> calendarFacilityCombo;
    @FXML private GridPane calendarGrid;
    @FXML private Label calendarMonthLabel;
    @FXML private ComboBox<Integer> calendarYearCombo;

    private YearMonth currentCalendarMonth = YearMonth.now();
    private java.util.Map<LocalDate, Integer> bookingCountByDate = new java.util.HashMap<>();
    private java.util.Map<LocalDate, java.util.List<String>> bookingDetailsByDate = new java.util.HashMap<>();
    private final AtomicLong calendarRefreshVersion = new AtomicLong(0);
    private static final int CALENDAR_CELL_WIDTH = 120;
    private static final int CALENDAR_CELL_HEIGHT = 130;
    private static final int CALENDAR_HEADER_HEIGHT = 34;
    private static final int CALENDAR_GAP = 10;
    private static final int CALENDAR_PADDING = 12;

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

    private final ObservableList<UserRow> usersMasterData = FXCollections.observableArrayList();
    private final ObservableList<UserRow> usersViewData = FXCollections.observableArrayList();
    private final ObservableList<FacilityRow> facilitiesMasterData = FXCollections.observableArrayList();
    private final ObservableList<FacilityRow> facilitiesViewData = FXCollections.observableArrayList();
    private final ObservableList<BookingRow> bookingsMasterData = FXCollections.observableArrayList();
    private final ObservableList<BookingRow> bookingsViewData = FXCollections.observableArrayList();
    private final ObservableList<IncidentRow> incidentsMasterData = FXCollections.observableArrayList();
    private final ObservableList<IncidentRow> incidentsViewData = FXCollections.observableArrayList();
    private final ObservableList<GroupRow> groupsMasterData = FXCollections.observableArrayList();
    private final ObservableList<GroupRow> groupsViewData = FXCollections.observableArrayList();

    private final PauseTransition usersSearchDebounce = new PauseTransition(Duration.millis(140));
    private final PauseTransition facilitiesSearchDebounce = new PauseTransition(Duration.millis(140));
    private final PauseTransition bookingsSearchDebounce = new PauseTransition(Duration.millis(140));
    private final PauseTransition incidentsSearchDebounce = new PauseTransition(Duration.millis(140));
    private final PauseTransition groupsSearchDebounce = new PauseTransition(Duration.millis(140));

    private String usersSortKey = "id";
    private boolean usersSortAsc = true;
    private String facilitiesSortKey = "id";
    private boolean facilitiesSortAsc = true;
    private String bookingsSortKey = "id";
    private boolean bookingsSortAsc = true;
    private String incidentsSortKey = "id";
    private boolean incidentsSortAsc = true;
    private String groupsSortKey = "id";
    private boolean groupsSortAsc = true;

    private boolean compactSidebar;
    private boolean sidebarExpanded = true;

    @FXML
    private void initialize() {
        setupListViews();
        setupNavigation();
        setupListFilteringAndSorting();
        configureHeaderLayout();
        refreshSortIndicators();
        setupResponsiveLayout();
        loadConnectedUser();
        refreshAll();
        setupCalendarYearSelector();
        preSizeCalendarGrid();
        showDashboardPage();
    }

    private void configureHeaderLayout() {
        applyHeaderColumnWidths(usersHeaderId, usersHeaderName, usersHeaderEmail, usersHeaderRole, usersHeaderStatus, usersHeaderActions);
        applyHeaderColumnWidths(facilitiesHeaderId, facilitiesHeaderName, facilitiesHeaderTypeCapacity, facilitiesHeaderLocation, facilitiesHeaderStatus, facilitiesHeaderActions);
        applyHeaderColumnWidths(bookingsHeaderId, bookingsHeaderFacility, bookingsHeaderUser, bookingsHeaderStart, bookingsHeaderStatus, bookingsHeaderActions);
        applyHeaderColumnWidths(incidentsHeaderId, incidentsHeaderTitle, incidentsHeaderFacility, incidentsHeaderDate, incidentsHeaderStatus, incidentsHeaderActions);
        applyHeaderColumnWidths(groupsHeaderId, groupsHeaderName, groupsHeaderOwner, groupsHeaderMembers, groupsHeaderCode, groupsHeaderActions);

        setSortableHeaderCursor(usersHeaderId, usersHeaderName, usersHeaderEmail, usersHeaderRole, usersHeaderStatus);
        setSortableHeaderCursor(facilitiesHeaderId, facilitiesHeaderName, facilitiesHeaderTypeCapacity, facilitiesHeaderLocation, facilitiesHeaderStatus);
        setSortableHeaderCursor(bookingsHeaderId, bookingsHeaderFacility, bookingsHeaderUser, bookingsHeaderStart, bookingsHeaderStatus);
        setSortableHeaderCursor(incidentsHeaderId, incidentsHeaderTitle, incidentsHeaderFacility, incidentsHeaderDate, incidentsHeaderStatus);
        setSortableHeaderCursor(groupsHeaderId, groupsHeaderName, groupsHeaderOwner, groupsHeaderMembers, groupsHeaderCode);
    }

    private void applyHeaderColumnWidths(Label id, Label main, Label secondary, Label small, Label status, Label actions) {
        setFixedWidth(id, 56, javafx.geometry.Pos.CENTER_LEFT);
        setFlexibleWidth(main, 120, 180, javafx.geometry.Pos.CENTER_LEFT);
        setFlexibleWidth(secondary, 140, 220, javafx.geometry.Pos.CENTER_LEFT);
        setFixedWidth(small, 96, javafx.geometry.Pos.CENTER_LEFT);
        setFixedWidth(status, 96, javafx.geometry.Pos.CENTER);
        setFixedWidth(actions, 82, javafx.geometry.Pos.CENTER);
    }

    private void setFixedWidth(Label label, double width, javafx.geometry.Pos alignment) {
        if (label == null) {
            return;
        }
        label.setMinWidth(width);
        label.setPrefWidth(width);
        label.setMaxWidth(width);
        label.setAlignment(alignment);
    }

    private void setFlexibleWidth(Label label, double minWidth, double prefWidth, javafx.geometry.Pos alignment) {
        if (label == null) {
            return;
        }
        label.setMinWidth(minWidth);
        label.setPrefWidth(prefWidth);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(alignment);
        HBox.setHgrow(label, javafx.scene.layout.Priority.ALWAYS);
    }

    private void setSortableHeaderCursor(Label... labels) {
        for (Label label : labels) {
            if (label != null) {
                label.setStyle("-fx-cursor: hand;");
            }
        }
    }

    private void refreshSortIndicators() {
        updateUsersSortIndicators();
        updateFacilitiesSortIndicators();
        updateBookingsSortIndicators();
        updateIncidentsSortIndicators();
        updateGroupsSortIndicators();
    }

    private void updateUsersSortIndicators() {
        setSortIndicator(usersHeaderId, "ID", "id", usersSortKey, usersSortAsc);
        setSortIndicator(usersHeaderName, "Nom", "name", usersSortKey, usersSortAsc);
        setSortIndicator(usersHeaderEmail, "Email", "email", usersSortKey, usersSortAsc);
        setSortIndicator(usersHeaderRole, "Rol", "role", usersSortKey, usersSortAsc);
        setSortIndicator(usersHeaderStatus, "Estat", "status", usersSortKey, usersSortAsc);
    }

    private void updateFacilitiesSortIndicators() {
        setSortIndicator(facilitiesHeaderId, "ID", "id", facilitiesSortKey, facilitiesSortAsc);
        setSortIndicator(facilitiesHeaderName, "Nom", "name", facilitiesSortKey, facilitiesSortAsc);
        setSortIndicator(facilitiesHeaderTypeCapacity, "Tipus / Capacitat", "typeCapacity", facilitiesSortKey, facilitiesSortAsc);
        setSortIndicator(facilitiesHeaderLocation, "Ubicació", "location", facilitiesSortKey, facilitiesSortAsc);
        setSortIndicator(facilitiesHeaderStatus, "Estat", "status", facilitiesSortKey, facilitiesSortAsc);
    }

    private void updateBookingsSortIndicators() {
        setSortIndicator(bookingsHeaderId, "ID", "id", bookingsSortKey, bookingsSortAsc);
        setSortIndicator(bookingsHeaderFacility, "Instal·lació", "facility", bookingsSortKey, bookingsSortAsc);
        setSortIndicator(bookingsHeaderUser, "Usuari", "user", bookingsSortKey, bookingsSortAsc);
        setSortIndicator(bookingsHeaderStart, "Inici", "start", bookingsSortKey, bookingsSortAsc);
        setSortIndicator(bookingsHeaderStatus, "Estat", "status", bookingsSortKey, bookingsSortAsc);
    }

    private void updateIncidentsSortIndicators() {
        setSortIndicator(incidentsHeaderId, "ID", "id", incidentsSortKey, incidentsSortAsc);
        setSortIndicator(incidentsHeaderTitle, "Títol", "title", incidentsSortKey, incidentsSortAsc);
        setSortIndicator(incidentsHeaderFacility, "Instal·lació", "facility", incidentsSortKey, incidentsSortAsc);
        setSortIndicator(incidentsHeaderDate, "Data", "date", incidentsSortKey, incidentsSortAsc);
        setSortIndicator(incidentsHeaderStatus, "Estat", "status", incidentsSortKey, incidentsSortAsc);
    }

    private void updateGroupsSortIndicators() {
        setSortIndicator(groupsHeaderId, "ID", "id", groupsSortKey, groupsSortAsc);
        setSortIndicator(groupsHeaderName, "Nom", "name", groupsSortKey, groupsSortAsc);
        setSortIndicator(groupsHeaderOwner, "Propietari", "owner", groupsSortKey, groupsSortAsc);
        setSortIndicator(groupsHeaderMembers, "Membres", "members", groupsSortKey, groupsSortAsc);
        setSortIndicator(groupsHeaderCode, "Codi", "code", groupsSortKey, groupsSortAsc);
    }

    private void setSortIndicator(Label label, String baseText, String key, String activeKey, boolean asc) {
        if (label == null) {
            return;
        }
        if (key.equals(activeKey)) {
            label.setText(baseText + (asc ? " ▲" : " ▼"));
        } else {
            label.setText(baseText);
        }
    }

    private void setupListFilteringAndSorting() {
        if (usersList != null) {
            usersList.setItems(usersViewData);
        }
        if (facilitiesList != null) {
            facilitiesList.setItems(facilitiesViewData);
        }
        if (bookingsList != null) {
            bookingsList.setItems(bookingsViewData);
        }
        if (incidentsList != null) {
            incidentsList.setItems(incidentsViewData);
        }
        if (groupsList != null) {
            groupsList.setItems(groupsViewData);
        }

        if (usersSearchField != null) {
            usersSearchField.textProperty().addListener((obs, oldValue, newValue) -> {
                usersSearchDebounce.setOnFinished(event -> applyUsersFilterAndSort());
                usersSearchDebounce.playFromStart();
            });
        }
        if (facilitiesSearchField != null) {
            facilitiesSearchField.textProperty().addListener((obs, oldValue, newValue) -> {
                facilitiesSearchDebounce.setOnFinished(event -> applyFacilitiesFilterAndSort());
                facilitiesSearchDebounce.playFromStart();
            });
        }
        if (bookingsSearchField != null) {
            bookingsSearchField.textProperty().addListener((obs, oldValue, newValue) -> {
                bookingsSearchDebounce.setOnFinished(event -> applyBookingsFilterAndSort());
                bookingsSearchDebounce.playFromStart();
            });
        }
        if (incidentsSearchField != null) {
            incidentsSearchField.textProperty().addListener((obs, oldValue, newValue) -> {
                incidentsSearchDebounce.setOnFinished(event -> applyIncidentsFilterAndSort());
                incidentsSearchDebounce.playFromStart();
            });
        }
        if (groupsSearchField != null) {
            groupsSearchField.textProperty().addListener((obs, oldValue, newValue) -> {
                groupsSearchDebounce.setOnFinished(event -> applyGroupsFilterAndSort());
                groupsSearchDebounce.playFromStart();
            });
        }
    }

    private void setupNavigation() {
        setButtonGraphic(menuToggleButton, FontAwesomeSolid.BARS, 14, Color.web("#1d4174"), 18, false);
        setButtonGraphic(navDashboardButton, FontAwesomeSolid.HOME, 14);
        setButtonGraphic(navUsersButton, FontAwesomeSolid.USERS, 14);
        setButtonGraphic(navFacilitiesButton, FontAwesomeSolid.BUILDING, 14);
        setButtonGraphic(navBookingsButton, FontAwesomeSolid.CALENDAR_ALT, 14);
        setButtonGraphic(navGroupsButton, FontAwesomeSolid.OBJECT_GROUP, 14);
        setButtonGraphic(navIncidentsButton, FontAwesomeSolid.EXCLAMATION_TRIANGLE, 14);
        setButtonGraphic(navCalendarButton, FontAwesomeSolid.CALENDAR, 14);
        setButtonGraphic(refreshButton, FontAwesomeSolid.SYNC, 13);
        setButtonGraphic(logoutButton, FontAwesomeSolid.SIGN_OUT_ALT, 13);
        setButtonGraphic(createUserButton, FontAwesomeSolid.PLUS, 13);
        setButtonGraphic(createFacilityButton, FontAwesomeSolid.PLUS, 13);
        setButtonGraphic(createBookingButton, FontAwesomeSolid.PLUS, 13);
        setButtonGraphic(createIncidentButton, FontAwesomeSolid.PLUS, 13);
        setButtonGraphic(createAdminGroupButton, FontAwesomeSolid.PLUS, 13);

        Image source = new Image(getClass().getResourceAsStream("/com/example/nexusbooking/desktop/images/logo.png"));
        WritableImage whiteLogo = createWhiteLogo(source);
        if (logoImage != null) {
            logoImage.setImage(whiteLogo);
        }
        if (sidebarFooterLogoImage != null) {
            sidebarFooterLogoImage.setImage(whiteLogo);
        }
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
        if (sidebarFooterMetaBox != null) {
            sidebarFooterMetaBox.setVisible(true);
            sidebarFooterMetaBox.setManaged(true);
        }

        updateSidebarButtonLabel(navDashboardButton, collapsed);
        updateSidebarButtonLabel(navUsersButton, collapsed);
        updateSidebarButtonLabel(navFacilitiesButton, collapsed);
        updateSidebarButtonLabel(navBookingsButton, collapsed);
        updateSidebarButtonLabel(navGroupsButton, collapsed);
        updateSidebarButtonLabel(navIncidentsButton, collapsed);
        updateSidebarButtonLabel(navCalendarButton, collapsed);
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

    private void showOnlyPage(VBox selectedPage, Button selectedButton, String title) {
        java.util.List.of(dashboardPage, usersPage, facilitiesPage, bookingsPage, incidentsPage, groupsPage, calendarPage).forEach(page -> {
            if (page != null) {
                boolean visible = page == selectedPage;
                page.setVisible(visible);
                page.setManaged(visible);
            }
        });

        java.util.List.of(navDashboardButton, navUsersButton, navFacilitiesButton, navBookingsButton, navGroupsButton, navIncidentsButton, navCalendarButton).forEach(button -> {
            if (button != null) {
                button.getStyleClass().remove("nav-button-active");
            }
        });

        if (selectedButton != null && !selectedButton.getStyleClass().contains("nav-button-active")) {
            selectedButton.getStyleClass().add("nav-button-active");
        }

        if (pageTitleLabel != null) {
            pageTitleLabel.setText(title);
        }
    }

    @FXML private void showDashboardPage() { showOnlyPage(dashboardPage, navDashboardButton, "Dashboard"); }
    @FXML private void showUsersPage() { showOnlyPage(usersPage, navUsersButton, "Usuaris"); }
    @FXML private void showFacilitiesPage() { showOnlyPage(facilitiesPage, navFacilitiesButton, "Instal·lacions"); }
    @FXML private void showBookingsPage() { showOnlyPage(bookingsPage, navBookingsButton, "Reserves"); }
    @FXML private void showGroupsPage() { showOnlyPage(groupsPage, navGroupsButton, "Grups"); }
    @FXML private void showIncidentsPage() { showOnlyPage(incidentsPage, navIncidentsButton, "Incidències"); }
    @FXML private void showCalendarPage() { showOnlyPage(calendarPage, navCalendarButton, "Calendari"); }

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

    private void loadConnectedUser() {
        if (currentUserLabel == null) {
            return;
        }
        currentUserLabel.setText("...");
        runAsync(() -> {
            try {
                UserResponse me = App.getApiClient().getCurrentUser();
                Platform.runLater(() -> {
                    if (me != null && me.getEmail() != null && !me.getEmail().isBlank()) {
                        currentUserLabel.setText(me.getEmail());
                    } else {
                        currentUserLabel.setText("Usuari");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> currentUserLabel.setText("Usuari"));
            }
        });
    }

    private void preSizeCalendarGrid() {
        double gridWidth = (7 * CALENDAR_CELL_WIDTH) + (6 * CALENDAR_GAP) + (2.0 * CALENDAR_PADDING);
        double gridHeight = CALENDAR_HEADER_HEIGHT + (6 * CALENDAR_CELL_HEIGHT) + (6 * CALENDAR_GAP) + (2.0 * CALENDAR_PADDING);
        calendarGrid.setMinWidth(gridWidth);
        calendarGrid.setPrefWidth(gridWidth);
        calendarGrid.setMinHeight(gridHeight);
        calendarGrid.setPrefHeight(gridHeight);
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

    private void setupListViews() {
        configureListView(usersList, this::buildUserListItem);
        configureListView(facilitiesList, this::buildFacilityListItem);
        configureListView(bookingsList, this::buildBookingListItem);
        configureListView(incidentsList, this::buildIncidentListItem);
        configureListView(groupsList, this::buildGroupListItem);
    }

    private <T> void configureListView(ListView<T> listView, java.util.function.Function<T, javafx.scene.Node> renderer) {
        if (listView == null) {
            return;
        }
        listView.getStyleClass().add("admin-list-view");
        listView.setFocusTraversable(false);
        Label placeholder = new Label("No hi ha elements per mostrar");
        placeholder.getStyleClass().add("admin-list-empty");
        listView.setPlaceholder(placeholder);
        listView.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (isActionTarget(event.getTarget())) {
                return;
            }
            listView.getSelectionModel().clearSelection();
            event.consume();
        });
        listView.setCellFactory(view -> new ListCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                    return;
                }
                try {
                    javafx.scene.Node rowNode = renderer.apply(item);
                    if (rowNode == null) {
                        Label fallback = new Label("Element no disponible");
                        fallback.getStyleClass().add("admin-list-empty");
                        setGraphic(fallback);
                    } else {
                        if (rowNode instanceof Region region) {
                            region.setMaxWidth(Double.MAX_VALUE);
                            region.prefWidthProperty().bind(view.widthProperty().subtract(22));
                        }
                        setGraphic(rowNode);
                    }
                    setText(null);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                } catch (Exception ex) {
                    Label fallback = new Label("Error mostrant l'element");
                    fallback.getStyleClass().add("admin-list-empty");
                    setGraphic(fallback);
                    setText(null);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                }
            }
        });
    }

    private HBox buildUserListItem(UserRow row) {
        HBox actions = createActionRow(
            createListIconActionButton("Editar usuari", "btn-secondary", FontAwesomeSolid.PENCIL_ALT, Color.web("#2a3b58"), e -> showEditUserDialog(row)),
            createListIconActionButton("Eliminar usuari", "btn-danger", FontAwesomeSolid.TRASH_ALT, Color.WHITE, e -> deleteUser(row.id))
        );
        return createAdminListRow(
            createColumnLabel(String.valueOf(row.id), "admin-list-col-id"),
            createColumnLabel(displayNameFromEmail(row.email), "admin-list-col-main"),
            createColumnLabel(safeValue(row.email), "admin-list-col-secondary"),
            createColumnLabel(safeValue(row.role), "admin-list-col-small"),
            createActiveStatusBadge(Boolean.TRUE.equals(row.active)),
            actions
        );
    }

    private HBox buildFacilityListItem(FacilityRow row) {
        HBox actions = createActionRow(
            createListIconActionButton("Editar instal·lació", "btn-secondary", FontAwesomeSolid.PENCIL_ALT, Color.web("#2a3b58"), e -> showEditFacilityDialog(row)),
            createListIconActionButton("Eliminar instal·lació", "btn-danger", FontAwesomeSolid.TRASH_ALT, Color.WHITE, e -> deleteFacility(row.id))
        );
        String details = (row.type == null ? "Sense tipus" : row.type) + " · "
            + (row.capacity == null ? "Sense capacitat" : row.capacity + " places");
        return createAdminListRow(
            createColumnLabel(String.valueOf(row.id), "admin-list-col-id"),
            createColumnLabel(safeValue(row.name), "admin-list-col-main"),
            createColumnLabel(details, "admin-list-col-secondary"),
            createColumnLabel(safeValue(row.location), "admin-list-col-small"),
            createColumnLabel("Operativa", "admin-list-col-status-text"),
            actions
        );
    }

    private HBox buildBookingListItem(BookingRow row) {
        HBox actions = createActionRow(
            createListIconActionButton("Editar reserva", "btn-secondary", FontAwesomeSolid.PENCIL_ALT, Color.web("#2a3b58"), e -> showEditBookingDialog(row.id)),
            createListIconActionButton("Cancel·lar reserva", "btn-danger", FontAwesomeSolid.TIMES, Color.WHITE, e -> cancelBooking(row.id))
        );
        return createAdminListRow(
            createColumnLabel(String.valueOf(row.id), "admin-list-col-id"),
            createColumnLabel(safeValue(row.facilityName), "admin-list-col-main"),
            createColumnLabel(safeValue(row.userName), "admin-list-col-secondary"),
            createColumnLabel(safeValue(row.startTime), "admin-list-col-small"),
            createBookingStatusBadge(row.status, row.rawStartTime),
            actions
        );
    }

    private HBox buildIncidentListItem(IncidentRow row) {
        HBox actions = createActionRow(
            createListIconActionButton("Editar incidència", "btn-secondary", FontAwesomeSolid.PENCIL_ALT, Color.web("#2a3b58"), e -> showEditIncidentDialog(row)),
            createListIconActionButton("Eliminar incidència", "btn-danger", FontAwesomeSolid.TRASH_ALT, Color.WHITE, e -> deleteIncident(row.id))
        );
        return createAdminListRow(
            createColumnLabel(String.valueOf(row.id), "admin-list-col-id"),
            createColumnLabel(safeValue(row.title), "admin-list-col-main"),
            createColumnLabel(safeValue(row.facilityName), "admin-list-col-secondary"),
            createColumnLabel(safeValue(row.createdAt), "admin-list-col-small"),
            createIncidentStatusBadge(row.status, row.statusLabel),
            actions
        );
    }

    private HBox buildGroupListItem(GroupRow row) {
        HBox actions = createActionRow(
            createListIconActionButton("Editar grup", "btn-secondary", FontAwesomeSolid.PENCIL_ALT, Color.web("#2a3b58"), e -> showEditGroupDialog(row)),
            createListIconActionButton("Eliminar grup", "btn-danger", FontAwesomeSolid.TRASH_ALT, Color.WHITE, e -> deleteGroup(row.id))
        );
        return createAdminListRow(
            createColumnLabel(String.valueOf(row.id), "admin-list-col-id"),
            createColumnLabel(safeValue(row.name), "admin-list-col-main"),
            createColumnLabel(safeValue(row.ownerEmail), "admin-list-col-secondary"),
            createColumnLabel(String.valueOf(row.memberCount), "admin-list-col-small"),
            createColumnLabel(safeValue(row.joinCode), "admin-list-col-status-text"),
            actions
        );
    }

    private HBox createAdminListRow(Label idCol, Label mainCol, Label secondaryCol, Label auxCol, javafx.scene.Node statusNode, HBox actions) {
        HBox row = new HBox(10);
        row.getStyleClass().add("admin-list-row");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);

        StackPane statusWrap = new StackPane(statusNode);
        statusWrap.getStyleClass().add("admin-list-col-status-wrap");
        statusWrap.setAlignment(javafx.geometry.Pos.CENTER);

        setFixedWidth(idCol, 56, javafx.geometry.Pos.CENTER_LEFT);
        setFlexibleWidth(mainCol, 120, 180, javafx.geometry.Pos.CENTER_LEFT);
        setFlexibleWidth(secondaryCol, 140, 220, javafx.geometry.Pos.CENTER_LEFT);
        setFixedWidth(auxCol, 96, javafx.geometry.Pos.CENTER_LEFT);
        statusWrap.setMinWidth(96);
        statusWrap.setPrefWidth(96);
        statusWrap.setMaxWidth(96);
        actions.setMinWidth(82);
        actions.setPrefWidth(82);
        actions.setMaxWidth(82);

        row.getChildren().addAll(idCol, mainCol, secondaryCol, auxCol, statusWrap, actions);
        return row;
    }

    private HBox createActionRow(Button... buttons) {
        HBox actions = new HBox(8, buttons);
        actions.getStyleClass().addAll("admin-list-actions", "admin-list-col-actions");
        actions.setAlignment(javafx.geometry.Pos.CENTER);
        return actions;
    }

    private boolean isActionTarget(Object target) {
        if (!(target instanceof Node node)) {
            return false;
        }
        Node current = node;
        while (current != null) {
            if (current instanceof Button || current instanceof ScrollBar) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    private Label createColumnLabel(String text, String styleClass) {
        Label label = new Label(text == null || text.isBlank() ? "-" : text);
        label.getStyleClass().addAll("admin-list-cell", styleClass);
        return label;
    }

    private Label createActiveStatusBadge(boolean active) {
        Label badge = new Label(active ? "Actiu" : "Inactiu");
        badge.getStyleClass().add("status-pill");
        badge.getStyleClass().add(active ? "status-resolved" : "status-cancelled");
        return badge;
    }

    private Button createListIconActionButton(String tooltipText, String styleClass, FontAwesomeSolid iconCode, Color iconColor, javafx.event.EventHandler<ActionEvent> handler) {
        Button button = new Button();
        button.getStyleClass().addAll(styleClass, "list-icon-action-button");
        setActionButtonGraphic(button, iconCode, iconColor);
        button.setTooltip(new Tooltip(tooltipText));
        button.setOnAction(handler);
        return button;
    }

    private String safeValue(String value) {
        return (value == null || value.isBlank()) ? "-" : value;
    }

    private String displayNameFromEmail(String email) {
        if (email == null || email.isBlank()) {
            return "Usuari";
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return email;
        }
        String localPart = email.substring(0, atIndex).replace('.', ' ').replace('_', ' ').trim();
        if (localPart.isBlank()) {
            return email;
        }
        return Character.toUpperCase(localPart.charAt(0)) + localPart.substring(1);
    }

    private Label createBookingStatusBadge(String status, String startDateTimeValue) {
        String code = bookingTimelineCategory(status, startDateTimeValue);
        String label = "COMPLETED".equals(code) ? "Completada" : "PENDING".equals(code) ? "Pendent" : "Cancel·lada";
        Label badge = new Label(label);
        badge.getStyleClass().add("status-pill");
        if ("COMPLETED".equals(code)) {
            badge.getStyleClass().add("status-resolved");
        } else if ("CANCELLED".equals(code)) {
            badge.getStyleClass().add("status-cancelled");
        } else {
            badge.getStyleClass().add("status-pending");
        }
        return badge;
    }

    private Label createIncidentStatusBadge(String status, String statusLabel) {
        String code = status == null ? "OPEN" : status;
        Label badge = new Label(statusLabel == null ? code : statusLabel);
        badge.getStyleClass().add("status-pill");
        if ("IN_PROGRESS".equals(code)) {
            badge.getStyleClass().add("status-progress");
        } else if ("RESOLVED".equals(code) || "CLOSED".equals(code)) {
            badge.getStyleClass().add("status-resolved");
        } else {
            badge.getStyleClass().add("status-open");
        }
        return badge;
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

    private String bookingTimelineCategory(String rawStatus, String startDateTimeValue) {
        if ("CANCELLED".equalsIgnoreCase(rawStatus)) {
            return "CANCELLED";
        }
        LocalDateTime start = parseFlexibleDateTime(startDateTimeValue);
        if (start != null && start.isAfter(LocalDateTime.now())) {
            return "PENDING";
        }
        return "COMPLETED";
    }

    private LocalDateTime parseFlexibleDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, ISO_DATE_TIME);
        } catch (Exception ignored) {
        }
        try {
            return LocalDateTime.parse(value, UI_DATE_TIME);
        } catch (Exception ignored) {
        }
        return null;
    }

    private ObservableList<String> buildTimeSlots() {
        ObservableList<String> slots = FXCollections.observableArrayList();
        for (int hour = 0; hour < 24; hour++) {
            slots.add(String.format("%02d:00", hour));
            slots.add(String.format("%02d:30", hour));
        }
        return slots;
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
                    updateDashboardWidgets(dashboard, bookings, incidents, groups, facilities, users);

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

    private void updateDashboardWidgets(JsonObject dashboard, JsonArray bookings, JsonArray incidents, JsonArray groups, JsonArray facilities, JsonArray users) {
        int totalUsers = dashboard != null && dashboard.has("users") ? dashboard.get("users").getAsInt() : users.size();
        int totalBookings = dashboard != null && dashboard.has("bookings") ? dashboard.get("bookings").getAsInt() : bookings.size();
        int totalGroups = dashboard != null && dashboard.has("groups") ? dashboard.get("groups").getAsInt() : groups.size();
        int totalIncidents = dashboard != null && dashboard.has("incidents") ? dashboard.get("incidents").getAsInt() : incidents.size();

        if (dashboardUsersCountLabel != null) dashboardUsersCountLabel.setText(String.valueOf(totalUsers));
        if (dashboardBookingsCountLabel != null) dashboardBookingsCountLabel.setText(String.valueOf(totalBookings));
        if (dashboardGroupsCountLabel != null) dashboardGroupsCountLabel.setText(String.valueOf(totalGroups));
        if (dashboardIncidentsCountLabel != null) dashboardIncidentsCountLabel.setText(String.valueOf(totalIncidents));

        int activeUsers = 0;
        for (int i = 0; i < users.size(); i++) {
            JsonObject user = users.get(i).getAsJsonObject();
            if (!user.has("active") || user.get("active").getAsBoolean()) {
                activeUsers++;
            }
        }

        int completedBookings = 0;
        int pendingBookings = 0;
        int cancelledBookings = 0;
        java.util.Map<String, Integer> bookingStatusCounts = new java.util.LinkedHashMap<>();
        bookingStatusCounts.put("COMPLETED", 0);
        bookingStatusCounts.put("PENDING", 0);
        bookingStatusCounts.put("CANCELLED", 0);
        for (int i = 0; i < bookings.size(); i++) {
            JsonObject booking = bookings.get(i).getAsJsonObject();
            String status = booking.has("status") && !booking.get("status").isJsonNull() ? booking.get("status").getAsString() : "CONFIRMED";
            String startTime = booking.has("startTime") && !booking.get("startTime").isJsonNull() ? booking.get("startTime").getAsString() : "";
            String category = bookingTimelineCategory(status, startTime);
            bookingStatusCounts.put(category, bookingStatusCounts.getOrDefault(category, 0) + 1);
            if ("PENDING".equals(category)) {
                pendingBookings++;
            } else if ("CANCELLED".equals(category)) {
                cancelledBookings++;
            } else {
                completedBookings++;
            }
        }

        int openIncidents = 0;
        int resolvedIncidents = 0;
        for (int i = 0; i < incidents.size(); i++) {
            JsonObject incident = incidents.get(i).getAsJsonObject();
            String status = incident.has("status") && !incident.get("status").isJsonNull() ? incident.get("status").getAsString() : "OPEN";
            if ("OPEN".equals(status) || "IN_PROGRESS".equals(status)) {
                openIncidents++;
            } else if ("RESOLVED".equals(status) || "CLOSED".equals(status)) {
                resolvedIncidents++;
            }
        }

        int inactiveUsers = Math.max(0, totalUsers - activeUsers);
        if (dashboardUsersTrendLabel != null) dashboardUsersTrendLabel.setText("Inactius: " + inactiveUsers);
        if (dashboardBookingsTrendLabel != null) dashboardBookingsTrendLabel.setText("Completades: " + completedBookings + " · Pendents: " + pendingBookings + " · Cancel·lades: " + cancelledBookings);
        if (dashboardGroupsTrendLabel != null) dashboardGroupsTrendLabel.setText("Amb més d'1 membre: " + countGroupsWithAdditionalMembers(groups));
        if (dashboardIncidentsTrendLabel != null) dashboardIncidentsTrendLabel.setText("Tancades: " + resolvedIncidents);
        if (dashboardIncidentsCountLabel != null) dashboardIncidentsCountLabel.setText(String.valueOf(openIncidents));

        renderUpcomingBookings(bookings);
        renderRecentActivity(incidents);
        renderBookingStatusChart(bookingStatusCounts);

        if (statsLabel != null) {
            statsLabel.setText("Usuaris: " + totalUsers + " | Reserves: " + totalBookings + " | Grups: " + totalGroups + " | Incidències: " + totalIncidents);
        }
    }

    private int countGroupsWithAdditionalMembers(JsonArray groups) {
        int count = 0;
        for (int i = 0; i < groups.size(); i++) {
            JsonObject group = groups.get(i).getAsJsonObject();
            if (group.has("memberCount") && group.get("memberCount").getAsInt() > 1) {
                count++;
            }
        }
        return count;
    }

    private void renderUpcomingBookings(JsonArray bookings) {
        if (upcomingBookingsContainer == null) return;

        upcomingBookingsContainer.getChildren().clear();
        java.util.List<JsonObject> upcoming = new java.util.ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < bookings.size(); i++) {
            JsonObject booking = bookings.get(i).getAsJsonObject();
            try {
                LocalDateTime start = LocalDateTime.parse(booking.get("startTime").getAsString(), ISO_DATE_TIME);
                if (start.isAfter(now)) {
                    upcoming.add(booking);
                }
            } catch (Exception ignored) {}
        }

        upcoming.sort(java.util.Comparator.comparing(b -> {
            try {
                return LocalDateTime.parse(b.get("startTime").getAsString(), ISO_DATE_TIME);
            } catch (Exception ex) {
                return LocalDateTime.MAX;
            }
        }));

        if (upcoming.isEmpty()) {
            upcomingBookingsContainer.getChildren().add(buildDashboardEmptyState("No hi ha reserves futures."));
            return;
        }

        for (int i = 0; i < Math.min(upcoming.size(), 4); i++) {
            JsonObject booking = upcoming.get(i);
            String rawStatus = booking.has("status") && !booking.get("status").isJsonNull() ? booking.get("status").getAsString() : "CONFIRMED";
            String startTime = booking.has("startTime") && !booking.get("startTime").isJsonNull() ? booking.get("startTime").getAsString() : "";
            String category = bookingTimelineCategory(rawStatus, startTime);
            upcomingBookingsContainer.getChildren().add(buildDashboardItem(
                booking.has("facilityName") ? booking.get("facilityName").getAsString() : "Instal·lació",
                (booking.has("groupName") && !booking.get("groupName").isJsonNull() ? booking.get("groupName").getAsString() : "Sense grup") + " · " + formatDateTime(booking.get("startTime").getAsString()),
                category
            ));
        }
    }

    private void renderRecentActivity(JsonArray incidents) {
        if (recentActivityContainer == null) return;

        recentActivityContainer.getChildren().clear();
        java.util.List<JsonObject> recent = new java.util.ArrayList<>();
        for (int i = 0; i < incidents.size(); i++) {
            recent.add(incidents.get(i).getAsJsonObject());
        }

        recent.sort((left, right) -> {
            String leftDate = left.has("createdAt") ? left.get("createdAt").getAsString() : "";
            String rightDate = right.has("createdAt") ? right.get("createdAt").getAsString() : "";
            return rightDate.compareTo(leftDate);
        });

        if (recent.isEmpty()) {
            recentActivityContainer.getChildren().add(buildDashboardEmptyState("Sense incidències recents."));
            return;
        }

        for (int i = 0; i < Math.min(recent.size(), 4); i++) {
            JsonObject incident = recent.get(i);
            recentActivityContainer.getChildren().add(buildDashboardItem(
                incident.has("title") ? incident.get("title").getAsString() : "Incidència",
                (incident.has("facilityName") ? incident.get("facilityName").getAsString() : "Instal·lació") + " · " + (incident.has("createdAt") ? formatDateTime(incident.get("createdAt").getAsString()) : ""),
                incident.has("status") ? incident.get("status").getAsString() : "OPEN"
            ));
        }
    }

    private VBox buildDashboardItem(String title, String subtitle, String status) {
        VBox card = new VBox(4);
        card.getStyleClass().add("dashboard-list-item");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("dashboard-list-item-title");
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("dashboard-list-item-subtitle");
        Label statusLabel = new Label(status);
        statusLabel.getStyleClass().addAll("status-pill", statusStyleClass(status));
        card.getChildren().addAll(titleLabel, subtitleLabel, statusLabel);
        return card;
    }

    private VBox buildDashboardEmptyState(String text) {
        VBox card = new VBox();
        card.getStyleClass().add("dashboard-empty-state");
        card.getChildren().add(new Label(text));
        return card;
    }

    private String statusStyleClass(String status) {
        if (status == null) return "status-pending";
        if ("COMPLETED".equals(status)) return "status-resolved";
        if ("CONFIRMED".equals(status)) return "status-confirmed";
        if ("PENDING".equals(status)) return "status-pending";
        if ("CANCELLED".equals(status)) return "status-cancelled";
        if ("OPEN".equals(status)) return "status-open";
        if ("IN_PROGRESS".equals(status)) return "status-progress";
        if ("RESOLVED".equals(status)) return "status-resolved";
        if ("CLOSED".equals(status)) return "status-cancelled";
        return "status-pending";
    }

    private void renderBookingStatusChart(java.util.Map<String, Integer> statusCounts) {
        if (chartContainer == null) return;

        chartContainer.getChildren().clear();

        PieChart.Data confirmed = new PieChart.Data("Completades", statusCounts.getOrDefault("COMPLETED", 0));
        PieChart.Data pending = new PieChart.Data("Pendents", statusCounts.getOrDefault("PENDING", 0));
        PieChart.Data cancelled = new PieChart.Data("Cancel·lades", statusCounts.getOrDefault("CANCELLED", 0));

        PieChart chart = new PieChart(FXCollections.observableArrayList(confirmed, pending, cancelled));
        chart.setLabelsVisible(true);
        chart.setLegendSide(javafx.geometry.Side.RIGHT);
        chart.setTitle("Estat temporal de reserves");
        chart.setClockwise(true);
        chart.setPrefSize(340, 240);
        chart.setMinSize(320, 220);
        chart.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        chart.getStyleClass().add("dashboard-pie-chart");

        chartContainer.getChildren().add(chart);
    }

    private void loadUsersTable(JsonArray users) {
        usersMasterData.clear();
        users.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            usersMasterData.add(new UserRow(
                obj.get("id").getAsLong(),
                obj.get("email").getAsString(),
                obj.has("role") ? obj.get("role").getAsString() : "USER",
                obj.has("active") ? obj.get("active").getAsBoolean() : true
            ));
        });
        applyUsersFilterAndSort();
    }

    private void applyUsersFilterAndSort() {
        if (usersList == null || usersViewData == null) {
            return;
        }

        String filterText = usersSearchField == null || usersSearchField.getText() == null
            ? ""
            : usersSearchField.getText().trim().toLowerCase(Locale.ROOT);

        java.util.List<UserRow> filtered = new java.util.ArrayList<>();
        for (UserRow row : usersMasterData) {
            String displayName = displayNameFromEmail(row.email).toLowerCase(Locale.ROOT);
            String email = safeValue(row.email).toLowerCase(Locale.ROOT);
            String role = safeValue(row.role).toLowerCase(Locale.ROOT);
            if (filterText.isBlank()
                    || displayName.contains(filterText)
                    || email.contains(filterText)
                    || role.contains(filterText)) {
                filtered.add(row);
            }
        }

        java.util.Comparator<UserRow> comparator;
        switch (usersSortKey) {
            case "name" -> comparator = java.util.Comparator.comparing((UserRow row) -> displayNameFromEmail(row.email), String.CASE_INSENSITIVE_ORDER);
            case "email" -> comparator = java.util.Comparator.comparing((UserRow row) -> safeValue(row.email), String.CASE_INSENSITIVE_ORDER);
            case "role" -> comparator = java.util.Comparator.comparing((UserRow row) -> safeValue(row.role), String.CASE_INSENSITIVE_ORDER)
                .thenComparing((UserRow row) -> safeValue(row.email), String.CASE_INSENSITIVE_ORDER);
            case "status" -> comparator = java.util.Comparator.comparing((UserRow row) -> Boolean.TRUE.equals(row.active) ? 1 : 0);
            default -> comparator = java.util.Comparator.comparing((UserRow row) -> row.id);
        }

        if (!usersSortAsc) {
            comparator = comparator.reversed();
        }

        filtered.sort(comparator);
        usersViewData.setAll(filtered);
    }

    private void loadFacilitiesTable(JsonArray facilities) {
        facilitiesMasterData.clear();
        facilities.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            facilitiesMasterData.add(new FacilityRow(
                obj.get("id").getAsLong(),
                obj.get("name").getAsString(),
                obj.get("type").getAsString(),
                obj.has("capacity") ? obj.get("capacity").getAsInt() : null,
                obj.has("location") ? obj.get("location").getAsString() : "",
                obj.has("description") ? obj.get("description").getAsString() : ""
            ));
        });
        applyFacilitiesFilterAndSort();
    }

    private void loadBookingsTable(JsonArray bookings) {
        bookingsMasterData.clear();
        bookings.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            String userEmail = obj.has("userEmail") ? obj.get("userEmail").getAsString() : "N/A";
            String groupName = obj.has("groupName") && !obj.get("groupName").isJsonNull()
                    ? obj.get("groupName").getAsString()
                    : "Sense grup";
            bookingsMasterData.add(new BookingRow(
                obj.get("id").getAsLong(),
                userEmail + " / " + groupName,
                obj.has("facilityName") ? obj.get("facilityName").getAsString() : "N/A",
                formatDateTime(obj.get("startTime").getAsString()),
                formatDateTime(obj.get("endTime").getAsString()),
                obj.has("status") ? obj.get("status").getAsString() : "CONFIRMED",
                obj.has("notes") ? obj.get("notes").getAsString() : "",
                obj.has("startTime") ? obj.get("startTime").getAsString() : ""
            ));
        });
        applyBookingsFilterAndSort();
    }

    private void loadIncidentsTable(JsonArray incidents) {
        incidentsMasterData.clear();
        incidents.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            incidentsMasterData.add(new IncidentRow(
                obj.get("id").getAsLong(),
                obj.get("title").getAsString(),
                obj.has("facilityName") ? obj.get("facilityName").getAsString() : "N/A",
                obj.has("status") ? obj.get("status").getAsString() : "OPEN",
                obj.has("statusLabel") ? obj.get("statusLabel").getAsString() : obj.has("status") ? obj.get("status").getAsString() : "Obert",
                obj.has("createdAt") ? formatDateTime(obj.get("createdAt").getAsString()) : ""
            ));
        });
        applyIncidentsFilterAndSort();
    }

    private void applyFacilitiesFilterAndSort() {
        String filterText = facilitiesSearchField == null || facilitiesSearchField.getText() == null
            ? ""
            : facilitiesSearchField.getText().trim().toLowerCase(Locale.ROOT);
        java.util.List<FacilityRow> filtered = new java.util.ArrayList<>();
        for (FacilityRow row : facilitiesMasterData) {
            String details = (row.type == null ? "" : row.type) + " " + (row.location == null ? "" : row.location);
            if (filterText.isBlank() || safeValue(row.name).toLowerCase(Locale.ROOT).contains(filterText)
                    || details.toLowerCase(Locale.ROOT).contains(filterText)) {
                filtered.add(row);
            }
        }
        java.util.Comparator<FacilityRow> comparator;
        switch (facilitiesSortKey) {
            case "name" -> comparator = java.util.Comparator.comparing((FacilityRow row) -> safeValue(row.name), String.CASE_INSENSITIVE_ORDER);
            case "typeCapacity" -> comparator = java.util.Comparator.comparing((FacilityRow row) -> safeValue(row.type), String.CASE_INSENSITIVE_ORDER)
                .thenComparing((FacilityRow row) -> row.capacity == null ? -1 : row.capacity);
            case "location" -> comparator = java.util.Comparator.comparing((FacilityRow row) -> safeValue(row.location), String.CASE_INSENSITIVE_ORDER);
            case "status" -> comparator = java.util.Comparator.comparing((FacilityRow row) -> 1);
            default -> comparator = java.util.Comparator.comparing((FacilityRow row) -> row.id);
        }
        if (!facilitiesSortAsc) comparator = comparator.reversed();
        filtered.sort(comparator);
        facilitiesViewData.setAll(filtered);
    }

    private void applyBookingsFilterAndSort() {
        String filterText = bookingsSearchField == null || bookingsSearchField.getText() == null
            ? ""
            : bookingsSearchField.getText().trim().toLowerCase(Locale.ROOT);
        java.util.List<BookingRow> filtered = new java.util.ArrayList<>();
        for (BookingRow row : bookingsMasterData) {
            if (filterText.isBlank()
                    || safeValue(row.facilityName).toLowerCase(Locale.ROOT).contains(filterText)
                    || safeValue(row.userName).toLowerCase(Locale.ROOT).contains(filterText)
                    || safeValue(row.status).toLowerCase(Locale.ROOT).contains(filterText)) {
                filtered.add(row);
            }
        }
        java.util.Comparator<BookingRow> comparator;
        switch (bookingsSortKey) {
            case "facility" -> comparator = java.util.Comparator.comparing((BookingRow row) -> safeValue(row.facilityName), String.CASE_INSENSITIVE_ORDER);
            case "user" -> comparator = java.util.Comparator.comparing((BookingRow row) -> safeValue(row.userName), String.CASE_INSENSITIVE_ORDER);
            case "start" -> comparator = java.util.Comparator.comparing((BookingRow row) -> safeValue(row.startTime), String.CASE_INSENSITIVE_ORDER);
            case "status" -> comparator = java.util.Comparator.comparing((BookingRow row) -> safeValue(row.status), String.CASE_INSENSITIVE_ORDER);
            default -> comparator = java.util.Comparator.comparing((BookingRow row) -> row.id);
        }
        if (!bookingsSortAsc) comparator = comparator.reversed();
        filtered.sort(comparator);
        bookingsViewData.setAll(filtered);
    }

    private void applyIncidentsFilterAndSort() {
        String filterText = incidentsSearchField == null || incidentsSearchField.getText() == null
            ? ""
            : incidentsSearchField.getText().trim().toLowerCase(Locale.ROOT);
        java.util.List<IncidentRow> filtered = new java.util.ArrayList<>();
        for (IncidentRow row : incidentsMasterData) {
            if (filterText.isBlank()
                    || safeValue(row.title).toLowerCase(Locale.ROOT).contains(filterText)
                    || safeValue(row.facilityName).toLowerCase(Locale.ROOT).contains(filterText)
                    || safeValue(row.statusLabel).toLowerCase(Locale.ROOT).contains(filterText)) {
                filtered.add(row);
            }
        }
        java.util.Comparator<IncidentRow> comparator;
        switch (incidentsSortKey) {
            case "title" -> comparator = java.util.Comparator.comparing((IncidentRow row) -> safeValue(row.title), String.CASE_INSENSITIVE_ORDER);
            case "facility" -> comparator = java.util.Comparator.comparing((IncidentRow row) -> safeValue(row.facilityName), String.CASE_INSENSITIVE_ORDER);
            case "date" -> comparator = java.util.Comparator.comparing((IncidentRow row) -> safeValue(row.createdAt), String.CASE_INSENSITIVE_ORDER);
            case "status" -> comparator = java.util.Comparator.comparing((IncidentRow row) -> safeValue(row.statusLabel), String.CASE_INSENSITIVE_ORDER);
            default -> comparator = java.util.Comparator.comparing((IncidentRow row) -> row.id);
        }
        if (!incidentsSortAsc) comparator = comparator.reversed();
        filtered.sort(comparator);
        incidentsViewData.setAll(filtered);
    }

    private void applyGroupsFilterAndSort() {
        String filterText = groupsSearchField == null || groupsSearchField.getText() == null
            ? ""
            : groupsSearchField.getText().trim().toLowerCase(Locale.ROOT);
        java.util.List<GroupRow> filtered = new java.util.ArrayList<>();
        for (GroupRow row : groupsMasterData) {
            if (filterText.isBlank()
                    || safeValue(row.name).toLowerCase(Locale.ROOT).contains(filterText)
                    || safeValue(row.ownerEmail).toLowerCase(Locale.ROOT).contains(filterText)
                    || safeValue(row.joinCode).toLowerCase(Locale.ROOT).contains(filterText)) {
                filtered.add(row);
            }
        }
        java.util.Comparator<GroupRow> comparator;
        switch (groupsSortKey) {
            case "name" -> comparator = java.util.Comparator.comparing((GroupRow row) -> safeValue(row.name), String.CASE_INSENSITIVE_ORDER);
            case "owner" -> comparator = java.util.Comparator.comparing((GroupRow row) -> safeValue(row.ownerEmail), String.CASE_INSENSITIVE_ORDER);
            case "members" -> comparator = java.util.Comparator.comparing((GroupRow row) -> row.memberCount == null ? 0 : row.memberCount);
            case "code" -> comparator = java.util.Comparator.comparing((GroupRow row) -> safeValue(row.joinCode), String.CASE_INSENSITIVE_ORDER);
            default -> comparator = java.util.Comparator.comparing((GroupRow row) -> row.id);
        }
        if (!groupsSortAsc) comparator = comparator.reversed();
        filtered.sort(comparator);
        groupsViewData.setAll(filtered);
    }

    @FXML
    private void showCreateUserDialog() {
        messageLabel.setText("");
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
            new Label("Email *"),
            emailField,
            new Label("Contrasenya *"),
            passwordField,
            new Label("Role:"),
            roleCombo
        );

        Label validationLabel = createDialogValidationLabel(content);

        dialog.getDialogPane().setContent(content);
        applyDialogStyling(dialog, content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        installDialogValidation(dialog, validationLabel, () -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            if (email.isEmpty() || password.isEmpty()) {
                return "Email i contrasenya són obligatoris";
            }
            return null;
        });

        Optional<UserRow> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String role = roleCombo.getValue();

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

        ToggleButton activeCheck = createSwitch("Usuari actiu", "Usuari inactiu", user.active != null && user.active);
        
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList("ADMIN", "USER"));
        roleCombo.setValue(user.role);

        content.getChildren().addAll(
            new Label("Email: " + user.email),
            new Label("Rol:"),
            roleCombo,
            new Label("Actiu:"),
            activeCheck
        );

        dialog.getDialogPane().setContent(content);
        applyDialogStyling(dialog, content);
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
        applyDialogStyling(confirm.getDialogPane());
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
            new Label("Nom *"),
            nameField,
            new Label("Tipus *"),
            typeField,
            new Label("Capacitat:"),
            capacityField,
            new Label("Localització:"),
            locationField,
            new Label("Descripció:"),
            descriptionArea
        );

        Label validationLabel = createDialogValidationLabel(content);

        dialog.getDialogPane().setContent(content);
        applyDialogStyling(dialog, content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        installDialogValidation(dialog, validationLabel, () -> {
            String name = nameField.getText().trim();
            String type = typeField.getText().trim();
            if (name.isEmpty() || type.isEmpty()) {
                return "Nom i tipus són obligatoris";
            }
            try {
                if (!capacityField.getText().trim().isEmpty()) {
                    Integer.parseInt(capacityField.getText().trim());
                }
            } catch (NumberFormatException e) {
                return "Capacitat ha de ser un nombre";
            }
            return null;
        });

        Optional<Void> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String name = nameField.getText().trim();
        String type = typeField.getText().trim();

        Integer capacity = null;
        try {
            if (!capacityField.getText().trim().isEmpty()) {
                capacity = Integer.parseInt(capacityField.getText().trim());
            }
        } catch (NumberFormatException e) {
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
            new Label("Nom *"),
            nameField,
            new Label("Tipus *"),
            typeField,
            new Label("Capacitat:"),
            capacityField,
            new Label("Localització:"),
            locationField,
            new Label("Descripció:"),
            descriptionArea
        );

        Label validationLabel = createDialogValidationLabel(content);

        dialog.getDialogPane().setContent(content);
        applyDialogStyling(dialog, content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        installDialogValidation(dialog, validationLabel, () -> {
            String updatedName = nameField.getText().trim();
            String updatedType = typeField.getText().trim();
            if (updatedName.isEmpty() || updatedType.isEmpty()) {
                return "Nom i tipus són obligatoris";
            }
            try {
                if (!capacityField.getText().trim().isEmpty()) {
                    Integer.parseInt(capacityField.getText().trim());
                }
            } catch (NumberFormatException e) {
                return "Capacitat ha de ser un nombre";
            }
            return null;
        });

        Optional<Void> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String name = nameField.getText().trim();
        String type = typeField.getText().trim();

        Integer capacity = null;
        try {
            if (!capacityField.getText().trim().isEmpty()) {
                capacity = Integer.parseInt(capacityField.getText().trim());
            }
        } catch (NumberFormatException e) {
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
        applyDialogStyling(confirm.getDialogPane());
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

        java.util.List<String> facilityOptions = new java.util.ArrayList<>();
        facilityOptions.add("Sin instal·lació");
        final String[] selectedFacility = new String[] {"Sin instal·lació"};
        TextField facilityField = createPickerField("Sin instal·lació");
        facilityField.setText("Sin instal·lació");
        Button facilityPickButton = createPickerButton("Selecciona instal·lació", "Cerca instal·lació", facilityOptions, selectedFacility, facilityField);
        facilities.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            facilityOptions.add(obj.get("id") + " - " + obj.get("name").getAsString());
        });

        TextField titleField = new TextField();
        titleField.setPromptText("Títol");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Descripció");
        descriptionArea.setPrefRowCount(4);

        content.getChildren().addAll(
            new Label("Instal·lació:"), createPickerRow(facilityField, facilityPickButton),
            new Label("Títol *"),
            titleField,
            new Label("Descripció:"),
            descriptionArea
        );

        Label validationLabel = createDialogValidationLabel(content);

        dialog.getDialogPane().setContent(content);
        applyDialogStyling(dialog, content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        installDialogValidation(dialog, validationLabel, () -> {
            String title = titleField.getText().trim();
            return title.isEmpty() ? "Títol és obligatori" : null;
        });

        Optional<Void> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String title = titleField.getText().trim();

        Long facilityId = null;
        String facilityStr = selectedFacility[0];
        if (facilityStr != null && !facilityStr.equals("Sin instal·lació")) {
            long parsedId = parseSelectionId(facilityStr);
            if (parsedId > 0) {
                facilityId = parsedId;
            }
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
        applyDialogStyling(dialog, content);
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
        applyDialogStyling(confirm.getDialogPane());
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
        groupsMasterData.clear();
        groups.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            groupsMasterData.add(new GroupRow(
                obj.get("id").getAsLong(),
                obj.get("name").getAsString(),
                obj.has("ownerEmail") ? obj.get("ownerEmail").getAsString() : "N/A",
                obj.has("memberCount") ? obj.get("memberCount").getAsInt() : 0,
                obj.has("joinCode") && !obj.get("joinCode").isJsonNull() ? obj.get("joinCode").getAsString() : "-"
            ));
        });
        applyGroupsFilterAndSort();
    }

    @FXML private void sortUsersById() { toggleUsersSort("id"); }
    @FXML private void sortUsersByName() { toggleUsersSort("name"); }
    @FXML private void sortUsersByEmail() { toggleUsersSort("email"); }
    @FXML private void sortUsersByRole() { toggleUsersSort("role"); }
    @FXML private void sortUsersByStatus() { toggleUsersSort("status"); }

    @FXML private void sortFacilitiesById() { toggleFacilitiesSort("id"); }
    @FXML private void sortFacilitiesByName() { toggleFacilitiesSort("name"); }
    @FXML private void sortFacilitiesByTypeCapacity() { toggleFacilitiesSort("typeCapacity"); }
    @FXML private void sortFacilitiesByLocation() { toggleFacilitiesSort("location"); }
    @FXML private void sortFacilitiesByStatus() { toggleFacilitiesSort("status"); }

    @FXML private void sortBookingsById() { toggleBookingsSort("id"); }
    @FXML private void sortBookingsByFacility() { toggleBookingsSort("facility"); }
    @FXML private void sortBookingsByUser() { toggleBookingsSort("user"); }
    @FXML private void sortBookingsByStart() { toggleBookingsSort("start"); }
    @FXML private void sortBookingsByStatus() { toggleBookingsSort("status"); }

    @FXML private void sortIncidentsById() { toggleIncidentsSort("id"); }
    @FXML private void sortIncidentsByTitle() { toggleIncidentsSort("title"); }
    @FXML private void sortIncidentsByFacility() { toggleIncidentsSort("facility"); }
    @FXML private void sortIncidentsByDate() { toggleIncidentsSort("date"); }
    @FXML private void sortIncidentsByStatus() { toggleIncidentsSort("status"); }

    @FXML private void sortGroupsById() { toggleGroupsSort("id"); }
    @FXML private void sortGroupsByName() { toggleGroupsSort("name"); }
    @FXML private void sortGroupsByOwner() { toggleGroupsSort("owner"); }
    @FXML private void sortGroupsByMembers() { toggleGroupsSort("members"); }
    @FXML private void sortGroupsByCode() { toggleGroupsSort("code"); }

    private void toggleUsersSort(String key) {
        if (usersSortKey.equals(key)) {
            usersSortAsc = !usersSortAsc;
        } else {
            usersSortKey = key;
            usersSortAsc = true;
        }
        updateUsersSortIndicators();
        applyUsersFilterAndSort();
    }

    private void toggleFacilitiesSort(String key) {
        if (facilitiesSortKey.equals(key)) {
            facilitiesSortAsc = !facilitiesSortAsc;
        } else {
            facilitiesSortKey = key;
            facilitiesSortAsc = true;
        }
        updateFacilitiesSortIndicators();
        applyFacilitiesFilterAndSort();
    }

    private void toggleBookingsSort(String key) {
        if (bookingsSortKey.equals(key)) {
            bookingsSortAsc = !bookingsSortAsc;
        } else {
            bookingsSortKey = key;
            bookingsSortAsc = true;
        }
        updateBookingsSortIndicators();
        applyBookingsFilterAndSort();
    }

    private void toggleIncidentsSort(String key) {
        if (incidentsSortKey.equals(key)) {
            incidentsSortAsc = !incidentsSortAsc;
        } else {
            incidentsSortKey = key;
            incidentsSortAsc = true;
        }
        updateIncidentsSortIndicators();
        applyIncidentsFilterAndSort();
    }

    private void toggleGroupsSort(String key) {
        if (groupsSortKey.equals(key)) {
            groupsSortAsc = !groupsSortAsc;
        } else {
            groupsSortKey = key;
            groupsSortAsc = true;
        }
        updateGroupsSortIndicators();
        applyGroupsFilterAndSort();
    }

    private void populateCalendarFacilityCombo() {
        String previousSelection = calendarFacilityCombo.getValue();
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("Totes"); // Default option to show all facilities
        facilities.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            items.add(obj.get("id") + " - " + obj.get("name").getAsString());
        });
        calendarFacilityCombo.setItems(items);
        if (previousSelection != null && items.contains(previousSelection)) {
            calendarFacilityCombo.setValue(previousSelection);
        } else {
            calendarFacilityCombo.setValue("Totes");
        }
        refreshCalendarView();
    }


    @FXML
    private void showCreateBookingDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Crear reserva");
        dialog.setHeaderText("Crear nova reserva (admin)");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        java.util.List<String> userOptions = new java.util.ArrayList<>();
        java.util.List<String> groupOptions = new java.util.ArrayList<>();
        java.util.List<String> facilityOptions = new java.util.ArrayList<>();
        final String[] selectedUser = new String[1];
        final String[] selectedGroup = new String[1];
        final String[] selectedFacility = new String[1];

        TextField userField = createPickerField("Selecciona usuari");
        TextField groupField = createPickerField("Selecciona grup");
        TextField facilityField = createPickerField("Selecciona instal·lació");
        Button userPickButton = createPickerButton("Selecciona usuari", "Cerca usuari", userOptions, selectedUser, userField);
        Button groupPickButton = createPickerButton("Selecciona grup", "Cerca grup", groupOptions, selectedGroup, groupField);
        Button facilityPickButton = createPickerButton("Selecciona instal·lació", "Cerca instal·lació", facilityOptions, selectedFacility, facilityField);

        try {
            JsonArray validUsers = App.getApiClient().getValidUsersForGroups();
            validUsers.forEach(el -> {
                JsonObject obj = el.getAsJsonObject();
                userOptions.add(obj.get("id") + " - " + obj.get("email").getAsString());
            });

            JsonArray groups = App.getApiClient().getAdminGroups();
            groups.forEach(el -> {
                JsonObject obj = el.getAsJsonObject();
                String code = obj.has("joinCode") && !obj.get("joinCode").isJsonNull() ? obj.get("joinCode").getAsString() : "-";
                groupOptions.add(obj.get("id") + " - " + obj.get("name").getAsString() + " (" + code + ")");
            });
        } catch (Exception e) {
            messageLabel.setText(errorMessage(e));
            return;
        }

        facilities.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            facilityOptions.add(obj.get("id") + " - " + obj.get("name").getAsString());
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
            new Label("Usuari *"), createPickerRow(userField, userPickButton),
            new Label("Grup *"), createPickerRow(groupField, groupPickButton),
            new Label("Instal·lació *"), createPickerRow(facilityField, facilityPickButton),
            new Label("Data inici *"), startDatePicker,
            new Label("Hora inici *"), startTimeCombo,
            new Label("Data final *"), endDatePicker,
            new Label("Hora final *"), endTimeCombo,
            new Label("Notes:"), notesField
        );

        Label validationLabel = createDialogValidationLabel(content);

        dialog.getDialogPane().setContent(content);
        applyDialogStyling(dialog, content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        installDialogValidation(dialog, validationLabel, () -> {
            if (selectedUser[0] == null || selectedGroup[0] == null || selectedFacility[0] == null
                    || startDatePicker.getValue() == null || endDatePicker.getValue() == null
                    || startTimeCombo.getValue() == null || endTimeCombo.getValue() == null) {
                return "Usuari, grup, instal·lació, inici i final són obligatoris";
            }
            String startValue = startDatePicker.getValue() + "T" + startTimeCombo.getValue() + ":00";
            String endValue = endDatePicker.getValue() + "T" + endTimeCombo.getValue() + ":00";
            try {
                LocalDateTime startDt = LocalDateTime.parse(startValue, ISO_DATE_TIME);
                LocalDateTime endDt = LocalDateTime.parse(endValue, ISO_DATE_TIME);
                if (!endDt.isAfter(startDt)) {
                    return "La data/hora final ha de ser posterior a la inicial";
                }
            } catch (Exception ex) {
                return "Format de data/hora invàlid";
            }
            return null;
        });

        Optional<Void> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        long userId = parseSelectionId(selectedUser[0]);
        long groupId = parseSelectionId(selectedGroup[0]);
        long facilityId = parseSelectionId(selectedFacility[0]);
        if (userId < 0 || groupId < 0 || facilityId < 0) {
            return;
        }
        String start = startDatePicker.getValue() + "T" + startTimeCombo.getValue() + ":00";
        String end = endDatePicker.getValue() + "T" + endTimeCombo.getValue() + ":00";
        String notes = notesField.getText().trim();

        try {
            LocalDateTime startDt = LocalDateTime.parse(start, ISO_DATE_TIME);
            LocalDateTime endDt = LocalDateTime.parse(end, ISO_DATE_TIME);
            if (!endDt.isAfter(startDt)) {
                return;
            }
        } catch (Exception ex) {
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
        applyDialogStyling(confirm.getDialogPane());
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

        java.util.List<String> userOptions = new java.util.ArrayList<>();
        java.util.List<String> groupOptions = new java.util.ArrayList<>();
        java.util.List<String> facilityOptions = new java.util.ArrayList<>();
        final String[] selectedUser = new String[1];
        final String[] selectedGroup = new String[1];
        final String[] selectedFacility = new String[1];

        TextField userField = createPickerField("Selecciona usuari");
        TextField groupField = createPickerField("Selecciona grup");
        TextField facilityField = createPickerField("Selecciona instal·lació");
        Button userPickButton = createPickerButton("Selecciona usuari", "Cerca usuari", userOptions, selectedUser, userField);
        Button groupPickButton = createPickerButton("Selecciona grup", "Cerca grup", groupOptions, selectedGroup, groupField);
        Button facilityPickButton = createPickerButton("Selecciona instal·lació", "Cerca instal·lació", facilityOptions, selectedFacility, facilityField);

        try {
            JsonArray validUsers = App.getApiClient().getValidUsersForGroups();
            validUsers.forEach(el -> {
                JsonObject obj = el.getAsJsonObject();
                String item = obj.get("id") + " - " + obj.get("email").getAsString();
                userOptions.add(item);
                if (selectedBooking.has("userId") && obj.get("id").getAsLong() == selectedBooking.get("userId").getAsLong()) {
                    selectedUser[0] = item;
                    userField.setText(item);
                }
            });

            JsonArray groups = App.getApiClient().getAdminGroups();
            groups.forEach(el -> {
                JsonObject obj = el.getAsJsonObject();
                String code = obj.has("joinCode") && !obj.get("joinCode").isJsonNull() ? obj.get("joinCode").getAsString() : "-";
                String item = obj.get("id") + " - " + obj.get("name").getAsString() + " (" + code + ")";
                groupOptions.add(item);
                if (selectedBooking.has("groupId") && !selectedBooking.get("groupId").isJsonNull() && obj.get("id").getAsLong() == selectedBooking.get("groupId").getAsLong()) {
                    selectedGroup[0] = item;
                    groupField.setText(item);
                }
            });

            facilities.forEach(el -> {
                JsonObject obj = el.getAsJsonObject();
                String item = obj.get("id") + " - " + obj.get("name").getAsString();
                facilityOptions.add(item);
                if (selectedBooking.has("facilityId") && obj.get("id").getAsLong() == selectedBooking.get("facilityId").getAsLong()) {
                    selectedFacility[0] = item;
                    facilityField.setText(item);
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
            new Label("Usuari *"), createPickerRow(userField, userPickButton),
            new Label("Grup *"), createPickerRow(groupField, groupPickButton),
            new Label("Instal·lació *"), createPickerRow(facilityField, facilityPickButton),
            new Label("Data inici *"), startDatePicker,
            new Label("Hora inici *"), startTimeCombo,
            new Label("Data final *"), endDatePicker,
            new Label("Hora final *"), endTimeCombo,
            new Label("Notes:"), notesField
        );

        Label validationLabel = createDialogValidationLabel(content);

        dialog.getDialogPane().setContent(content);
        applyDialogStyling(dialog, content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        installDialogValidation(dialog, validationLabel, () -> {
            if (selectedUser[0] == null || selectedGroup[0] == null || selectedFacility[0] == null
                    || startDatePicker.getValue() == null || startTimeCombo.getValue() == null
                    || endDatePicker.getValue() == null || endTimeCombo.getValue() == null) {
                return "Completa tots els camps obligatoris";
            }
            String startValue = startDatePicker.getValue() + "T" + startTimeCombo.getValue() + ":00";
            String endValue = endDatePicker.getValue() + "T" + endTimeCombo.getValue() + ":00";
            try {
                LocalDateTime startDt = LocalDateTime.parse(startValue, ISO_DATE_TIME);
                LocalDateTime endDt = LocalDateTime.parse(endValue, ISO_DATE_TIME);
                if (!endDt.isAfter(startDt)) {
                    return "La data/hora final ha de ser posterior a la inicial";
                }
            } catch (Exception ex) {
                return "Format de data/hora invàlid";
            }
            return null;
        });
        if (dialog.showAndWait().isEmpty()) return;

        long userId = parseSelectionId(selectedUser[0]);
        long groupId = parseSelectionId(selectedGroup[0]);
        long facilityId = parseSelectionId(selectedFacility[0]);
        if (userId < 0 || groupId < 0 || facilityId < 0) {
            return;
        }
        String start = startDatePicker.getValue() + "T" + startTimeCombo.getValue() + ":00";
        String end = endDatePicker.getValue() + "T" + endTimeCombo.getValue() + ":00";

        try {
            LocalDateTime startDt = LocalDateTime.parse(start, ISO_DATE_TIME);
            LocalDateTime endDt = LocalDateTime.parse(end, ISO_DATE_TIME);
            if (!endDt.isAfter(startDt)) {
                return;
            }
        } catch (Exception ex) {
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

        ListView<String> membersListView = new ListView<>();
        membersListView.setPrefHeight(170);
        membersListView.getStyleClass().addAll("admin-list-view", "group-members-list");
        membersListView.setPlaceholder(new Label("Aquest grup encara no té membres"));
        Label membersHintLabel = new Label("Selecciona un membre i prem 'Treure membre' per eliminar-lo.");
        membersHintLabel.getStyleClass().add("field-label");
        Label membersFeedbackLabel = new Label();
        membersFeedbackLabel.getStyleClass().addAll("success-label", "group-members-feedback");
        java.util.Map<Long, String> memberRoles = new java.util.HashMap<>();
        java.util.List<String> validUserOptions = new java.util.ArrayList<>();
        java.util.function.Consumer<JsonArray> refreshMemberList = members -> {
            ObservableList<String> rows = FXCollections.observableArrayList();
            memberRoles.clear();
            members.forEach(memberEl -> {
                JsonObject member = memberEl.getAsJsonObject();
                long userId = member.has("userId") ? member.get("userId").getAsLong() : -1;
                String email = member.has("email") && !member.get("email").isJsonNull() ? member.get("email").getAsString() : "Usuari";
                String role = member.has("role") && !member.get("role").isJsonNull() ? member.get("role").getAsString() : "MEMBER";
                memberRoles.put(userId, role);
                rows.add(userId + " - " + email + " (" + role + ")");
            });
            membersListView.setItems(rows);
        };

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
            JsonArray validUsers = App.getApiClient().getValidUsersForGroups();
            validUsers.forEach(userEl -> {
                JsonObject user = userEl.getAsJsonObject();
                validUserOptions.add(user.get("id") + " - " + user.get("email").getAsString());
            });
            refreshMemberList.accept(App.getApiClient().adminGetGroupMembers(row.id));
        } catch (Exception e) {
            membersFeedbackLabel.setText("No s'han pogut carregar membres: " + errorMessage(e));
        }

        Button addMemberButton = new Button("Afegir membre");
        addMemberButton.getStyleClass().add("btn-secondary");
        addMemberButton.setOnAction(event -> {
            java.util.Set<Long> existingIds = new java.util.HashSet<>(memberRoles.keySet());
            java.util.List<String> available = new java.util.ArrayList<>();
            for (String option : validUserOptions) {
                long candidateId = parseSelectionId(option);
                if (candidateId > 0 && !existingIds.contains(candidateId)) {
                    available.add(option);
                }
            }
            if (available.isEmpty()) {
                membersFeedbackLabel.setText("No hi ha usuaris disponibles per afegir");
                return;
            }
            String selected = openSearchablePickerDialog("Afegir membre", "Cerca usuari", available, null);
            if (selected == null) {
                return;
            }
            long userId = parseSelectionId(selected);
            if (userId < 0) {
                return;
            }
            try {
                App.getApiClient().adminAddGroupMember(row.id, userId);
                refreshMemberList.accept(App.getApiClient().adminGetGroupMembers(row.id));
                membersFeedbackLabel.setText("Membre afegit correctament");
            } catch (Exception e) {
                membersFeedbackLabel.setText(errorMessage(e));
            }
        });

        Button removeMemberButton = new Button("Treure membre");
        removeMemberButton.getStyleClass().add("btn-danger");
        removeMemberButton.setOnAction(event -> {
            String selected = membersListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                membersFeedbackLabel.setText("Selecciona un membre");
                return;
            }
            long userId = parseSelectionId(selected);
            String role = memberRoles.getOrDefault(userId, "MEMBER");
            if ("OWNER".equalsIgnoreCase(role)) {
                membersFeedbackLabel.setText("No es pot eliminar el propietari");
                return;
            }
            try {
                App.getApiClient().adminRemoveGroupMember(row.id, userId);
                refreshMemberList.accept(App.getApiClient().adminGetGroupMembers(row.id));
                membersFeedbackLabel.setText("Membre eliminat correctament");
            } catch (Exception e) {
                membersFeedbackLabel.setText(errorMessage(e));
            }
        });

        HBox membersActionRow = new HBox(8, addMemberButton, removeMemberButton);
        membersActionRow.getStyleClass().add("group-members-actions");

        content.getChildren().addAll(
            new Label("Nom *"), nameField,
            new Label("Descripció:"), descriptionField,
            new Label("Membres"), membersListView,
            membersHintLabel,
            membersActionRow,
            membersFeedbackLabel
        );

        Label validationLabel = createDialogValidationLabel(content);

        dialog.getDialogPane().setContent(content);
        applyDialogStyling(dialog, content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        installDialogValidation(dialog, validationLabel, () -> {
            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            return name.isBlank() ? "El nom del grup és obligatori" : null;
        });
        if (dialog.showAndWait().isEmpty()) return;

        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String description = descriptionField.getText() == null ? "" : descriptionField.getText().trim();
        if (name.isBlank()) return;

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
        applyDialogStyling(confirm.getDialogPane());
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
        runAsync(() -> {
            try {
                JsonArray validUsers = App.getApiClient().getValidUsersForGroups();
                Platform.runLater(() -> openCreateGroupDialogWith(validUsers));
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    private void openCreateGroupDialogWith(JsonArray validUsers) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Crear grup");
        dialog.setHeaderText("Crear nou grup (admin)");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        java.util.List<String> ownerOptions = new java.util.ArrayList<>();
        final String[] selectedOwner = new String[1];
        TextField ownerField = new TextField();
        ownerField.setEditable(false);
        ownerField.setPromptText("Selecciona propietari");
        ownerField.setId("adminGroupOwnerCombo");
        Button ownerSelectButton = new Button("Cercar...");
        ownerSelectButton.getStyleClass().add("btn-secondary");
        ownerSelectButton.setOnAction(e -> {
            String picked = openSearchablePickerDialog("Selecciona propietari", "Cerca usuari", ownerOptions, selectedOwner[0]);
            if (picked != null) {
                selectedOwner[0] = picked;
                ownerField.setText(picked);
            }
        });
        HBox ownerRow = new HBox(8, ownerField, ownerSelectButton);
        ownerField.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(ownerField, javafx.scene.layout.Priority.ALWAYS);

        TextField nameField = new TextField();
        nameField.setId("adminGroupNameField");
        nameField.setPromptText("Nom del grup");
        TextField descriptionField = new TextField();
        descriptionField.setId("adminGroupDescriptionField");
        descriptionField.setPromptText("Descripció");

        validUsers.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            ownerOptions.add(obj.get("id") + " - " + obj.get("email").getAsString());
        });

        content.getChildren().addAll(
            new Label("Propietari (no admin) *"), ownerRow,
            new Label("Nom *"), nameField,
            new Label("Descripció:"), descriptionField
        );

        Label validationLabel = createDialogValidationLabel(content);

        dialog.getDialogPane().setContent(content);
        applyDialogStyling(dialog, content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        installDialogValidation(dialog, validationLabel, () -> {
            if (selectedOwner[0] == null || selectedOwner[0].isBlank()
                    || nameField.getText() == null || nameField.getText().trim().isBlank()) {
                return "Propietari i nom són obligatoris";
            }
            return null;
        });
        if (dialog.showAndWait().isEmpty()) return;

        long ownerId = parseSelectionId(selectedOwner[0]);
        if (ownerId < 0) {
            return;
        }
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
        YearMonth targetMonth = currentCalendarMonth;
        String facilitySelection = calendarFacilityCombo != null ? calendarFacilityCombo.getValue() : null;
        long refreshVersion = calendarRefreshVersion.incrementAndGet();

        runAsync(() -> {
            try {
                JsonArray allBookings = App.getApiClient().getAdminBookings();
                long selectedFacilityId = -1;

                // Parse facility selection: "id - name" format, or all if "Totes"
                if (facilitySelection != null && !facilitySelection.isBlank()) {
                    if (!facilitySelection.contains(" - ")) {
                        // "Totes" option
                        selectedFacilityId = -1;
                    } else {
                        selectedFacilityId = parseSelectionId(facilitySelection);
                    }
                }
                final long facilityIdFinal = selectedFacilityId;

                // Build both count and compact booking details by date for selected facility
                java.util.Map<LocalDate, Integer> countByDate = new java.util.HashMap<>();
                java.util.Map<LocalDate, java.util.List<String>> detailsByDate = new java.util.HashMap<>();
                allBookings.forEach(el -> {
                    JsonObject obj = el.getAsJsonObject();
                    long fId = obj.has("facilityId") ? obj.get("facilityId").getAsLong() : -1;
                    if (facilityIdFinal == -1 || fId == facilityIdFinal) {
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
                    if (refreshVersion != calendarRefreshVersion.get()) {
                        return;
                    }
                    String monthLabel = targetMonth.getMonth().getDisplayName(TextStyle.FULL, new Locale("ca", "ES"));
                    monthLabel = monthLabel.substring(0, 1).toUpperCase(new Locale("ca", "ES")) + monthLabel.substring(1);
                    calendarMonthLabel.setText(monthLabel + " " + targetMonth.getYear());
                    renderCalendarGrid();
                });
            } catch (Exception e) {
                Platform.runLater(() -> messageLabel.setText(errorMessage(e)));
            }
        });
    }

    private void renderCalendarGrid() {
        calendarGrid.getChildren().clear();
        calendarGrid.setHgap(CALENDAR_GAP);
        calendarGrid.setVgap(CALENDAR_GAP);
        calendarGrid.setPadding(new Insets(CALENDAR_PADDING));

        double gridWidth = (7 * CALENDAR_CELL_WIDTH) + (6 * CALENDAR_GAP) + (2.0 * CALENDAR_PADDING);
        double gridHeight = CALENDAR_HEADER_HEIGHT + (6 * CALENDAR_CELL_HEIGHT) + (6 * CALENDAR_GAP) + (2.0 * CALENDAR_PADDING);
        calendarGrid.setMinWidth(gridWidth);
        calendarGrid.setPrefWidth(gridWidth);
        calendarGrid.setMinHeight(gridHeight);
        calendarGrid.setPrefHeight(gridHeight);

        // Header: Days of week
        String[] dayNames = {"Dg", "Dl", "Dt", "Dc", "Dj", "Dv", "Ds"};
        for (int i = 0; i < 7; i++) {
            Label header = new Label(dayNames[i]);
            header.getStyleClass().add("calendar-day-header");
            header.setMaxWidth(Double.MAX_VALUE);
            header.setPrefHeight(CALENDAR_HEADER_HEIGHT);
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
                Region empty = new Region();
                empty.getStyleClass().add("calendar-void-cell");
                empty.setPrefWidth(CALENDAR_CELL_WIDTH);
                empty.setPrefHeight(CALENDAR_CELL_HEIGHT);
                GridPane.setColumnIndex(empty, col);
                GridPane.setRowIndex(empty, row);
                calendarGrid.add(empty, col, row);
                continue;
            }

            LocalDate date = LocalDate.of(currentCalendarMonth.getYear(), currentCalendarMonth.getMonth(), dayNumber);
            int count = bookingCountByDate.getOrDefault(date, 0);
            java.util.List<String> details = bookingDetailsByDate.getOrDefault(date, java.util.Collections.emptyList());
            java.util.List<String> sortedDetails = new java.util.ArrayList<>(details);
            sortedDetails.sort(String::compareTo);

            VBox dayCell = new VBox();
            dayCell.getStyleClass().add("calendar-day-cell");
            if (date.equals(LocalDate.now())) {
                dayCell.getStyleClass().add("calendar-day-cell-today");
            }
            dayCell.setSpacing(4);
            dayCell.setPrefHeight(CALENDAR_CELL_HEIGHT);
            dayCell.setPrefWidth(CALENDAR_CELL_WIDTH);

            Label dayLabel = new Label(String.valueOf(dayNumber));
            dayLabel.getStyleClass().add("calendar-day-number");
            dayCell.getChildren().add(dayLabel);

            if (count > 0) {
                Label countLabel = new Label(count + (count == 1 ? " reserva" : " reserves"));
                countLabel.getStyleClass().add("calendar-count-label");
                dayCell.getChildren().add(countLabel);

                int maxVisible = 3;
                for (int i = 0; i < Math.min(sortedDetails.size(), maxVisible); i++) {
                    Label bookingLabel = new Label(sortedDetails.get(i));
                    bookingLabel.getStyleClass().add("calendar-booking-pill");
                    bookingLabel.setTooltip(new Tooltip(sortedDetails.get(i)));
                    dayCell.getChildren().add(bookingLabel);
                }
                if (sortedDetails.size() > maxVisible) {
                    Label moreLabel = new Label("+" + (sortedDetails.size() - maxVisible) + " més");
                    moreLabel.getStyleClass().add("calendar-more-label");
                    dayCell.getChildren().add(moreLabel);
                }
            } else {
                Label emptyLabel = new Label("Sense reserves");
                emptyLabel.getStyleClass().add("calendar-empty-label");
                dayCell.getChildren().add(emptyLabel);
            }

            GridPane.setColumnIndex(dayCell, col);
            GridPane.setRowIndex(dayCell, row);
            calendarGrid.add(dayCell, col, row);
        }
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
        public String rawStartTime;

        public BookingRow(Long id, String userName, String facilityName, String startTime, String endTime, String status, String notes, String rawStartTime) {
            this.id = id;
            this.userName = userName;
            this.facilityName = facilityName;
            this.startTime = startTime;
            this.endTime = endTime;
            this.status = status;
            this.notes = notes;
            this.rawStartTime = rawStartTime;
        }
    }

    public static class GroupRow {
        public Long id;
        public String name;
        public String ownerEmail;
        public Integer memberCount;
        public String joinCode;

        public GroupRow(Long id, String name, String ownerEmail, Integer memberCount, String joinCode) {
            this.id = id;
            this.name = name;
            this.ownerEmail = ownerEmail;
            this.memberCount = memberCount;
            this.joinCode = joinCode;
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
        
        // Style labels and fields
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

    private void setActionButtonGraphic(Button button, FontAwesomeSolid iconCode, Color color) {
        setButtonGraphic(button, iconCode, 12, color, 18, true);
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

    private Button createPickerButton(String title, String header, java.util.List<String> options, String[] selectedHolder, TextField targetField) {
        Button button = new Button("Cercar...");
        button.getStyleClass().add("btn-secondary");
        button.setOnAction(e -> {
            String picked = openSearchablePickerDialog(title, header, options, selectedHolder[0]);
            if (picked != null) {
                selectedHolder[0] = picked;
                targetField.setText(picked);
            }
        });
        return button;
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
        ListView<String> listView = new ListView<>();
        listView.setPrefHeight(280);
        listView.getStyleClass().add("picker-list-view");
        ObservableList<String> options = FXCollections.observableArrayList(sourceOptions);
        listView.setItems(options);

        Runnable applyFilter = () -> {
            String filter = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
            if (filter.isBlank()) {
                listView.setItems(FXCollections.observableArrayList(sourceOptions));
                if (selectedValue != null) {
                    listView.getSelectionModel().select(selectedValue);
                }
                return;
            }
            ObservableList<String> filtered = FXCollections.observableArrayList();
            for (String option : sourceOptions) {
                if (option.toLowerCase(Locale.ROOT).contains(filter)) {
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

        picker.setResultConverter(buttonType -> buttonType == ButtonType.OK
            ? listView.getSelectionModel().getSelectedItem()
            : null);
        return picker.showAndWait().orElse(null);
    }

    private long parseSelectionId(String selection) {
        if (selection == null || !selection.contains(" - ")) {
            return -1;
        }
        try {
            return Long.parseLong(selection.substring(0, selection.indexOf(" - ")).trim());
        } catch (Exception ignored) {
            return -1;
        }
    }

    private ToggleButton createSwitch(String onText, String offText, boolean selected) {
        ToggleButton toggle = new ToggleButton();
        toggle.getStyleClass().add("dialog-switch-ios");
        StackPane track = new StackPane();
        track.getStyleClass().add("dialog-switch-track");
        Region thumb = new Region();
        thumb.getStyleClass().add("dialog-switch-thumb");
        track.getChildren().add(thumb);
        track.setAlignment(selected ? javafx.geometry.Pos.CENTER_RIGHT : javafx.geometry.Pos.CENTER_LEFT);
        toggle.setGraphic(track);
        toggle.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        toggle.setText(null);
        toggle.setSelected(selected);
        toggle.setTooltip(new Tooltip(selected ? onText : offText));
        toggle.selectedProperty().addListener((obs, oldValue, isSelected) -> {
            track.setAlignment(isSelected ? javafx.geometry.Pos.CENTER_RIGHT : javafx.geometry.Pos.CENTER_LEFT);
            toggle.setTooltip(new Tooltip(isSelected ? onText : offText));
        });
        return toggle;
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
}
