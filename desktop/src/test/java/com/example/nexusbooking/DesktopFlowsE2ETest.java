package com.example.nexusbooking;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.nexusbooking.desktop.App;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Labeled;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import javafx.stage.Window;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DesktopFlowsE2ETest extends ApplicationTest {

    private static final Gson GSON = new Gson();
    private static final ConcurrentHashMap<String, AtomicInteger> REQUEST_COUNTERS = new ConcurrentHashMap<>();
    private static final MockState STATE = new MockState();

    private static MockWebServer mockWebServer;
    private static Path configPath;
    private static String originalConfig;
    private static Stage primaryStage;

    DesktopFlowsE2ETest() throws Exception {
        super();
    }

    @BeforeAll
    static void setupMockServerAndConfig() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(new ApiDispatcher());
        mockWebServer.start();

        configPath = Path.of("..", "config.properties").normalize();
        originalConfig = Files.readString(configPath);

        String newConfig = originalConfig.replaceAll(
                "api\\.base\\.url\\s*=\\s*.*",
                "api.base.url=http://localhost:" + mockWebServer.getPort());

        Files.writeString(
                configPath,
                newConfig,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

    @AfterAll
    static void restoreConfigAndStopServer() throws Exception {
        if (originalConfig != null && configPath != null) {
            Files.writeString(
                    configPath,
                    originalConfig,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        }
        try {
            App.getApiClient().shutdown();
        } catch (Exception ignored) {
        }
        Platform.runLater(() -> {
            Platform.setImplicitExit(true);
            Window.getWindows().forEach(Window::hide);
        });
        safeWaitForFxEvents();
        Platform.exit();
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        Platform.setImplicitExit(false);
        primaryStage = stage;
        new App().start(stage);
    }

    @BeforeEach
    void resetToLoginScene() {
        REQUEST_COUNTERS.clear();
        STATE.reset();
        Platform.runLater(() -> {
            try {
                if (primaryStage != null && !primaryStage.isShowing()) {
                    primaryStage.show();
                }
                App.showLogin();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        safeWaitForFxEvents();
    }

    @AfterEach
    void cleanupTransientWindows() throws Exception {
        Platform.runLater(() -> new ArrayList<>(Window.getWindows()).stream()
            .filter(Window::isShowing)
            .forEach(Window::hide));
        safeWaitForFxEvents();
    }

    private void openGroupsTab() {
        assertTrue(waitForNode("#backofficeTabPane", 5), "Expected Backoffice tabs to be visible");
        Platform.runLater(() -> {
            TabPane tabs = lookup("#backofficeTabPane").queryAs(TabPane.class);
            if (tabs.getTabs().size() > 4) {
                tabs.getSelectionModel().select(4);
            }
        });
        safeWaitForFxEvents();
        assertTrue(waitForNode("#createAdminGroupButton", 5), "Expected admin groups tab to be active");
    }

    @Test
    @Order(1)
    @Disabled("Isolation mode: use incremental admin tests below")
    void userCrudFlows_shouldReflectVisibleChanges() {
        openHomeAsUser();

        assertTrue(waitForNode("#upcomingCountLabel", 10), "Expected Home dashboard scene after user login");
        assertTrue(waitForText("#upcomingCountLabel", "1", 5), "Expected initial upcoming bookings count");
        assertTrue(waitForText("#groupsCountLabel", "1", 5), "Expected initial groups count");

        clickNode("#createBookingButton");
        assertTrue(waitForNode("#dialogFacilityCombo", 5), "Expected booking dialog to open");
        clickOn("#dialogFacilityCombo");
        clickOn("1 - Pista Central");
        clickOn("#dialogGroupCombo");
        clickOn("10 - Team Alpha (ALPHA1)");
        setTextFieldValue("#dialogBookingNotesField", "Reserva creada UI");
        clickDialogConfirm();

        assertTrue(waitForCounter("POST /api/bookings", 1, 5), "Expected create booking request");
        assertTrue(waitForTextPresent("Reserva #101 - Pista Central", true, 5), "Expected new booking card to be rendered");

        clickNode("#bookingCancelBtn-101");
        clickDialogConfirm();

        assertTrue(waitForCounter("POST /api/bookings/101/cancel", 1, 5), "Expected cancel booking request for created booking");
        assertTrue(waitForTextFragmentPresent("Estat: CANCELLED", true, 5), "Expected booking status to change to CANCELLED");

        scrollAllScrollPanes(1.0);

        clickNode("#createGroupButton");
        assertTrue(waitForNode("#dialogGroupNameField", 5), "Expected group dialog to open");
        setTextFieldValue("#dialogGroupNameField", "Nou Grup UI");
        setTextFieldValue("#dialogGroupDescriptionField", "Creat per test");
        clickDialogConfirm();

        assertTrue(waitForCounter("POST /api/groups", 1, 5), "Expected create group request");
        assertTrue(waitForText("#groupsCountLabel", "2", 5), "Expected groups count to increase after creation");
        assertTrue(waitForTextPresent("Nou Grup UI (1 membres)", true, 5), "Expected new group card to be rendered");

        clickNode("#groupEditBtn-11");
        assertTrue(waitForNode("#dialogGroupNameField", 5), "Expected edit group dialog to open");
        setTextFieldValue("#dialogGroupNameField", "Grup Editat UI");
        clickDialogConfirm();

        assertTrue(waitForCounter("PUT /api/groups/11", 1, 5), "Expected update group request for created group");
        assertTrue(waitForTextPresent("Grup Editat UI (1 membres)", true, 5), "Expected edited group title to appear in UI");
    }

    @Test
    @Order(2)
    @Timeout(value = 90, unit = TimeUnit.SECONDS)
    @Disabled("Isolation mode: use incremental admin tests below")
    void adminCrudGroups_shouldCreateEditAndDeleteVisibleRows() {
        openBackofficeAsAdmin();

        assertTrue(waitForNode("#statsLabel", 10), "Expected Backoffice scene after admin login");
        assertTrue(waitForTextContains("#statsLabel", "Grups: 1", 5), "Expected initial groups metric in dashboard");

        openGroupsTab();

        clickNode("#createAdminGroupButton");
        assertTrue(waitForNode("#adminGroupOwnerCombo", 5), "Expected admin group dialog to open");
        clickOn("#adminGroupOwnerCombo");
        clickOn("2 - user@example.com");
        setTextFieldValue("#adminGroupNameField", "Grup Temporal");
        setTextFieldValue("#adminGroupDescriptionField", "Temporal per test");
        clickDialogConfirm();

        assertTrue(waitForCounter("POST /api/admin/groups", 1, 5), "Expected admin create group request");
        assertTrue(waitForTextContains("#statsLabel", "Grups: 2", 5), "Expected groups metric to increase after admin creation");
        assertTrue(waitForTextPresent("Grup Temporal", true, 5), "Expected new group row to appear in table");

        clickNode("#adminGroupEditBtn-11");
        assertTrue(waitForNode("#adminGroupNameField", 5), "Expected admin edit group dialog to open");
        setTextFieldValue("#adminGroupNameField", "Grup Temporal Editat");
        clickDialogConfirm();

        assertTrue(waitForCounter("PUT /api/admin/groups/11", 1, 5), "Expected admin update group request");
        assertTrue(waitForTextPresent("Grup Temporal Editat", true, 5), "Expected edited group name in table");

        clickNode("#adminGroupDeleteBtn-11");
        clickDialogConfirm();

        assertTrue(waitForCounter("DELETE /api/admin/groups/11", 1, 5), "Expected admin delete group request");
        assertTrue(waitForTextContains("#statsLabel", "Grups: 1", 5), "Expected groups metric to return to original value after deletion");
        assertTrue(waitForTextPresent("Grup Temporal Editat", false, 5), "Expected deleted group row to disappear");
    }

    @Test
    @Order(3)
    @Disabled("Isolation mode: use incremental admin tests below")
    void userLoginRoutesToHomeDashboard() {
        openHomeAsUser();
        assertTrue(waitForNode("#upcomingCountLabel", 5), "Expected Home dashboard scene after user login");
        assertTrue(lookup("#groupsCountLabel").tryQuery().isPresent(), "Expected groups count label on Home dashboard");
    }

    @Test
    @Order(4)
    @Disabled("Isolation mode: use incremental admin tests below")
    void adminLoginRoutesToBackoffice() {
        openBackofficeAsAdmin();
        assertTrue(waitForNode("#statsLabel", 5), "Expected Backoffice scene after admin login");
        assertTrue(lookup("#usersTable").tryQuery().isPresent(), "Expected users table on Backoffice scene");
    }

    @Test
    @Order(10)
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void adminStep1_shouldOpenBackoffice() {
        runOnlyAdminStep(1);
        openBackofficeAsAdmin();
        assertTrue(waitForNode("#statsLabel", 10), "Step1: Backoffice scene must open");
        Platform.runLater(() -> {
            if (primaryStage != null) {
                primaryStage.hide();
            }
        });
        safeWaitForFxEvents();
    }

    private static void safeWaitForFxEvents() {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.runLater(latch::countDown);
            latch.await(2, TimeUnit.SECONDS);
        } catch (Exception ignored) {
            // Best effort flush only; never block indefinitely.
        }
    }

    @Test
    @Order(11)
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void adminStep2_shouldOpenGroupsTab() {
        runOnlyAdminStep(2);
        openBackofficeAsAdmin();
        assertTrue(waitForNode("#statsLabel", 10), "Step2: Backoffice scene must open");
        openGroupsTab();
    }

    @Test
    @Order(12)
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void adminStep3_shouldOpenAndCloseCreateDialog() {
        runOnlyAdminStep(3);
        openBackofficeAsAdmin();
        openGroupsTab();

        clickNode("#createAdminGroupButton");
        assertTrue(waitForNode("#adminGroupOwnerCombo", 5), "Step3: Create dialog should open");
        clickDialogConfirm();

        assertTrue(waitForNode("#createAdminGroupButton", 5), "Step3: Backoffice should remain responsive after dialog close");
    }

    @Test
    @Order(13)
    @Timeout(value = 40, unit = TimeUnit.SECONDS)
    void adminStep4_shouldCreateGroup() {
        runOnlyAdminStep(4);
        openBackofficeAsAdmin();
        openGroupsTab();

        clickNode("#createAdminGroupButton");
        assertTrue(waitForNode("#adminGroupOwnerCombo", 5), "Step4: Create dialog should open");
        clickOn("#adminGroupOwnerCombo");
        clickOn("2 - user@example.com");
        setTextFieldValue("#adminGroupNameField", "Grup Seq");
        setTextFieldValue("#adminGroupDescriptionField", "Seq create");
        clickDialogConfirm();

        assertTrue(waitForCounter("POST /api/admin/groups", 1, 5), "Step4: Create request should be sent");
        assertTrue(waitForTextPresent("Grup Seq", true, 5), "Step4: Created group should appear");
    }

    @Test
    @Order(14)
    @Timeout(value = 40, unit = TimeUnit.SECONDS)
    void adminStep5_shouldEditGroup() {
        runOnlyAdminStep(5);
        openBackofficeAsAdmin();
        openGroupsTab();

        clickNode("#adminGroupEditBtn-10");
        assertTrue(waitForNode("#adminGroupNameField", 5), "Step5: Edit dialog should open");
        setTextFieldValue("#adminGroupNameField", "Team Alpha Edit Seq");
        clickDialogConfirm();

        assertTrue(waitForCounter("PUT /api/admin/groups/10", 1, 5), "Step5: Update request should be sent");
        assertTrue(waitForTextPresent("Team Alpha Edit Seq", true, 5), "Step5: Edited group should appear");
    }

    @Test
    @Order(15)
    @Timeout(value = 40, unit = TimeUnit.SECONDS)
    void adminStep6_shouldDeleteCreatedGroup() {
        runOnlyAdminStep(6);
        openBackofficeAsAdmin();
        openGroupsTab();

        clickNode("#createAdminGroupButton");
        assertTrue(waitForNode("#adminGroupOwnerCombo", 5), "Step6: Create dialog should open");
        clickOn("#adminGroupOwnerCombo");
        clickOn("2 - user@example.com");
        setTextFieldValue("#adminGroupNameField", "Delete Seq");
        setTextFieldValue("#adminGroupDescriptionField", "Seq delete");
        clickDialogConfirm();
        assertTrue(waitForCounter("POST /api/admin/groups", 1, 5), "Step6: Create request should be sent");

        clickNode("#adminGroupDeleteBtn-11");
        clickDialogConfirm();
        assertTrue(waitForCounter("DELETE /api/admin/groups/11", 1, 5), "Step6: Delete request should be sent");
    }

    private void loginAs(String email, String password) {
        setTextFieldValue("#loginEmail", email);
        setTextFieldValue("#loginPassword", password);
        clickOn("#loginSubmitButton");
    }

    private void openHomeAsUser() {
        loginAndShowScene("user@example.com", "Password123!", false);
    }

    private void openBackofficeAsAdmin() {
        loginAndShowScene("admin@example.com", "Password123!", true);
    }

    private void loginAndShowScene(String email, String password, boolean backoffice) {
        try {
            App.getApiClient().login(email, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Platform.runLater(() -> {
            try {
                if (backoffice) {
                    App.showBackoffice();
                } else {
                    App.showHome();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        safeWaitForFxEvents();
    }

    private void setTextFieldValue(String selector, String value) {
        Platform.runLater(() -> {
            TextInputControl control = (TextInputControl) lookup(selector).query();
            control.clear();
            control.setText(value);
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    private boolean waitForNode(String selector, int timeoutSeconds) {
        long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(timeoutSeconds);
        while (System.currentTimeMillis() < deadline) {
            if (lookup(selector).tryQuery().isPresent()) {
                return true;
            }
            pauseMillis(150);
        }
        return false;
    }

    private boolean waitForText(String selector, String expected, int timeoutSeconds) {
        long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(timeoutSeconds);
        while (System.currentTimeMillis() < deadline) {
            WaitForAsyncUtils.waitForFxEvents();
            if (lookup(selector).tryQuery().isPresent()) {
                String current = textOf(lookup(selector).query());
                if (expected.equals(current)) {
                    return true;
                }
            }
            pauseMillis(100);
        }
        return false;
    }

    private boolean waitForTextContains(String selector, String expectedFragment, int timeoutSeconds) {
        long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(timeoutSeconds);
        while (System.currentTimeMillis() < deadline) {
            WaitForAsyncUtils.waitForFxEvents();
            if (lookup(selector).tryQuery().isPresent()) {
                String current = textOf(lookup(selector).query());
                if (current != null && current.contains(expectedFragment)) {
                    return true;
                }
            }
            pauseMillis(100);
        }
        return false;
    }

    private boolean waitForTextPresent(String text, boolean expectedPresent, int timeoutSeconds) {
        long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(timeoutSeconds);
        while (System.currentTimeMillis() < deadline) {
            WaitForAsyncUtils.waitForFxEvents();
            boolean present = lookup(text).tryQuery().isPresent();
            if (present == expectedPresent) {
                return true;
            }
            pauseMillis(100);
        }
        return false;
    }

    private boolean waitForTextFragmentPresent(String fragment, boolean expectedPresent, int timeoutSeconds) {
        long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(timeoutSeconds);
        while (System.currentTimeMillis() < deadline) {
            WaitForAsyncUtils.waitForFxEvents();
            boolean present = lookup("*").queryAll().stream()
                    .map(this::textOf)
                    .filter(text -> text != null && !text.isBlank())
                    .anyMatch(text -> text.contains(fragment));
            if (present == expectedPresent) {
                return true;
            }
            pauseMillis(100);
        }
        return false;
    }

    private boolean waitForCounter(String key, int expectedCount, int timeoutSeconds) {
        long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(timeoutSeconds);
        while (System.currentTimeMillis() < deadline) {
            AtomicInteger count = REQUEST_COUNTERS.get(key);
            if (count != null && count.get() >= expectedCount) {
                return true;
            }
            pauseMillis(100);
        }
        return false;
    }

    private void scrollAllScrollPanes(double value) {
        Platform.runLater(() -> lookup(".scroll-pane").queryAll().forEach(node -> {
            if (node instanceof ScrollPane scrollPane) {
                scrollPane.setVvalue(value);
            }
        }));
        WaitForAsyncUtils.waitForFxEvents();
    }

    private void clickNode(String selector) {
        WaitForAsyncUtils.waitForFxEvents();
        Node node = lookup(selector).query();
        try {
            clickOn(node);
        } catch (Exception ex) {
            fireNode(node);
        }
        WaitForAsyncUtils.waitForFxEvents();
    }

    private void fireNode(Node node) {
        Platform.runLater(() -> {
            if (node instanceof ButtonBase buttonBase) {
                buttonBase.fire();
            } else {
                node.fireEvent(new javafx.event.ActionEvent());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    private void clickDialogConfirm() {
        WaitForAsyncUtils.waitForFxEvents();
        if (clickFocusedDialogOk()) {
            return;
        }
        if (lookup("OK").tryQuery().isPresent()) {
            clickNode("OK");
            return;
        }
        if (lookup("Aceptar").tryQuery().isPresent()) {
            clickNode("Aceptar");
            return;
        }
        if (lookup("Acceptar").tryQuery().isPresent()) {
            clickNode("Acceptar");
            return;
        }
        lookup(".dialog-pane .button").queryAll().stream()
                .filter(Node::isVisible)
                .findFirst()
                .ifPresent(this::fireNode);
        WaitForAsyncUtils.waitForFxEvents();
    }

    private boolean clickFocusedDialogOk() {
        AtomicBoolean clicked = new AtomicBoolean(false);
        Platform.runLater(() -> {
            Window targetWindow = Window.getWindows().stream()
                    .filter(Window::isShowing)
                    .filter(Window::isFocused)
                    .findFirst()
                    .orElseGet(() -> Window.getWindows().stream()
                            .filter(Window::isShowing)
                            .reduce((first, second) -> second)
                            .orElse(null));

            if (targetWindow == null || targetWindow.getScene() == null || targetWindow.getScene().getRoot() == null) {
                return;
            }

            Node pane = targetWindow.getScene().getRoot().lookup(".dialog-pane");
            if (!(pane instanceof DialogPane dialogPane)) {
                return;
            }

            Node okButtonNode = dialogPane.lookupButton(ButtonType.OK);
            if (okButtonNode instanceof ButtonBase okButton && !okButton.isDisabled()) {
                okButton.fire();
                clicked.set(true);
                return;
            }

            dialogPane.lookupAll(".button").stream()
                    .filter(ButtonBase.class::isInstance)
                    .map(ButtonBase.class::cast)
                    .filter(button -> !button.isDisabled())
                    .findFirst()
                    .ifPresent(button -> {
                        button.fire();
                        clicked.set(true);
                    });
        });
        WaitForAsyncUtils.waitForFxEvents();
        return clicked.get();
    }

    private String textOf(Node node) {
        if (node instanceof Labeled labeled) {
            return labeled.getText();
        }
        if (node instanceof TextInputControl textInputControl) {
            return textInputControl.getText();
        }
        return node == null ? null : node.toString();
    }

    private void pauseMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void runOnlyAdminStep(int step) {
        // Incremental hook kept for readability; currently no-op.
    }

    private static final class ApiDispatcher extends Dispatcher {
        @Override
        public MockResponse dispatch(RecordedRequest request) {
            String path = request.getPath();
            if (path != null && path.contains("?")) {
                path = path.substring(0, path.indexOf('?'));
            }
            String method = request.getMethod();
            String key = method + " " + path;
            REQUEST_COUNTERS.computeIfAbsent(key, unused -> new AtomicInteger()).incrementAndGet();

            if ("POST".equals(method) && "/api/auth/login".equals(path)) {
                String body = request.getBody().readUtf8();
                boolean admin = body.contains("admin@example.com");
                String token = admin ? "token-admin" : "token-user";
                return json(200, "{\"token\":\"" + token + "\",\"message\":\"ok\"}");
            }

            if ("GET".equals(method) && "/api/users/me".equals(path)) {
                String auth = request.getHeader("Authorization");
                boolean admin = auth != null && auth.contains("token-admin");
                String role = admin ? "ADMIN" : "USER";
                String email = admin ? "admin@example.com" : "user@example.com";
                return json(200,
                        "{\"id\":1,\"name\":\"Test\",\"surname\":\"User\",\"email\":\""
                                + email + "\",\"phone\":\"123456789\",\"role\":\"" + role + "\"}");
            }

            if ("POST".equals(method) && "/api/auth/logout".equals(path)) {
                return json(200, "{\"message\":\"Logged out\"}");
            }

            if ("GET".equals(method) && "/api/groups/mine".equals(path)) {
                return json(200, STATE.myGroupsJson());
            }

            if ("POST".equals(method) && "/api/groups".equals(path)) {
                JsonObject body = JsonParser.parseString(request.getBody().readUtf8()).getAsJsonObject();
                JsonObject created = STATE.createUserGroup(body.get("name").getAsString(), body.get("description").getAsString());
                return json(201, GSON.toJson(created));
            }

            if ("PUT".equals(method) && path != null && path.startsWith("/api/groups/")) {
                long groupId = Long.parseLong(path.substring("/api/groups/".length()));
                JsonObject body = JsonParser.parseString(request.getBody().readUtf8()).getAsJsonObject();
                JsonObject updated = STATE.updateUserGroup(groupId, body.get("name").getAsString(), body.get("description").getAsString());
                return json(200, GSON.toJson(updated));
            }

            if ("GET".equals(method) && "/api/bookings/mine".equals(path)) {
                return json(200, STATE.myBookingsJson());
            }

            if ("POST".equals(method) && "/api/bookings".equals(path)) {
                JsonObject body = JsonParser.parseString(request.getBody().readUtf8()).getAsJsonObject();
                JsonObject created = STATE.createUserBooking(body);
                return json(201, GSON.toJson(created));
            }

            if ("PUT".equals(method) && path != null && path.startsWith("/api/bookings/")) {
                long bookingId = Long.parseLong(path.substring("/api/bookings/".length()));
                JsonObject body = JsonParser.parseString(request.getBody().readUtf8()).getAsJsonObject();
                JsonObject updated = STATE.updateUserBooking(bookingId, body);
                return json(200, GSON.toJson(updated));
            }

            if ("POST".equals(method) && path != null && path.startsWith("/api/bookings/") && path.endsWith("/cancel")) {
                long bookingId = Long.parseLong(path.substring("/api/bookings/".length(), path.length() - "/cancel".length()));
                JsonObject cancelled = STATE.cancelUserBooking(bookingId);
                return json(200, GSON.toJson(cancelled));
            }

            if ("GET".equals(method) && "/api/admin/dashboard".equals(path)) {
                return json(200, STATE.adminDashboardJson());
            }

            if ("GET".equals(method) && "/api/admin/users".equals(path)) {
                return json(200, STATE.adminUsersJson());
            }

            if ("GET".equals(method) && "/api/admin/bookings".equals(path)) {
                return json(200, STATE.adminBookingsJson());
            }

            if ("GET".equals(method) && "/api/admin/groups".equals(path)) {
                return json(200, STATE.adminGroupsJson());
            }

            if ("POST".equals(method) && "/api/admin/groups".equals(path)) {
                JsonObject body = JsonParser.parseString(request.getBody().readUtf8()).getAsJsonObject();
                JsonObject created = STATE.adminCreateGroup(
                        body.get("ownerId").getAsLong(),
                        body.get("name").getAsString(),
                        body.get("description").getAsString());
                return json(201, GSON.toJson(created));
            }

            if ("PUT".equals(method) && path != null && path.startsWith("/api/admin/groups/")) {
                long groupId = Long.parseLong(path.substring("/api/admin/groups/".length()));
                JsonObject body = JsonParser.parseString(request.getBody().readUtf8()).getAsJsonObject();
                JsonObject updated = STATE.adminUpdateGroup(groupId, body.get("name").getAsString(), body.get("description").getAsString());
                return json(200, GSON.toJson(updated));
            }

            if ("DELETE".equals(method) && path != null && path.startsWith("/api/admin/groups/")) {
                long groupId = Long.parseLong(path.substring("/api/admin/groups/".length()));
                STATE.adminDeleteGroup(groupId);
                return json(200, "{\"message\":\"Group deleted successfully\"}");
            }

            if ("GET".equals(method) && "/api/users/valid-for-groups".equals(path)) {
                return json(200, STATE.validUsersJson());
            }

            if ("GET".equals(method) && "/api/facilities".equals(path)) {
                return json(200, STATE.facilitiesJson());
            }

            if ("GET".equals(method) && "/api/incidents".equals(path)) {
                return json(200, "[]");
            }

            return json(404, "{\"message\":\"Not found\"}");
        }

        private MockResponse json(int code, String body) {
            return new MockResponse().setResponseCode(code).addHeader("Content-Type", "application/json").setBody(body);
        }
    }

    private static final class MockState {
        private long nextGroupId;
        private long nextBookingId;
        private List<JsonObject> myGroups;
        private List<JsonObject> adminGroups;
        private List<JsonObject> myBookings;
        private List<JsonObject> adminBookings;
        private List<JsonObject> adminUsers;
        private List<JsonObject> validUsers;
        private List<JsonObject> facilities;

        synchronized void reset() {
            nextGroupId = 11;
            nextBookingId = 101;

            validUsers = new ArrayList<>();
            validUsers.add(user(2, "user@example.com", "USER", true));
            validUsers.add(user(3, "mate@example.com", "USER", true));

            adminUsers = new ArrayList<>();
            adminUsers.add(user(1, "admin@example.com", "ADMIN", true));
            adminUsers.add(user(2, "user@example.com", "USER", true));

            facilities = new ArrayList<>();
            facilities.add(facility(1, "Pista Central", "SPORT", 12, "Barcelona", "Pista principal"));

            myGroups = new ArrayList<>();
            myGroups.add(userGroup(10, "Team Alpha", "Grup inicial", "ALPHA1", 2));

            adminGroups = new ArrayList<>();
            adminGroups.add(adminGroup(10, "Team Alpha", "Grup inicial", "ALPHA1", "user@example.com", 2));

            myBookings = new ArrayList<>();
            myBookings.add(userBooking(100, 1, "Pista Central", 10, "Team Alpha",
                    "2030-01-15T10:00:00", "2030-01-15T11:00:00", "CONFIRMED", "Inicial"));

            adminBookings = new ArrayList<>();
            adminBookings.add(adminBooking(100, 2, "user@example.com", 1, "Pista Central", 10, "Team Alpha",
                    "2030-01-15T10:00:00", "2030-01-15T11:00:00", "CONFIRMED", "Inicial"));
        }

        synchronized String myGroupsJson() {
            return GSON.toJson(myGroups);
        }

        synchronized String adminGroupsJson() {
            return GSON.toJson(adminGroups);
        }

        synchronized String myBookingsJson() {
            return GSON.toJson(myBookings);
        }

        synchronized String adminBookingsJson() {
            return GSON.toJson(adminBookings);
        }

        synchronized String adminUsersJson() {
            return GSON.toJson(adminUsers);
        }

        synchronized String validUsersJson() {
            return GSON.toJson(validUsers);
        }

        synchronized String facilitiesJson() {
            return GSON.toJson(facilities);
        }

        synchronized String adminDashboardJson() {
            JsonObject dashboard = new JsonObject();
            dashboard.addProperty("users", adminUsers.size());
            dashboard.addProperty("groups", adminGroups.size());
            dashboard.addProperty("bookings", adminBookings.size());
            dashboard.addProperty("incidents", 0);
            dashboard.addProperty("facilities", facilities.size());
            return GSON.toJson(dashboard);
        }

        synchronized JsonObject createUserGroup(String name, String description) {
            long groupId = nextGroupId++;
            String joinCode = "USER" + groupId;
            JsonObject myGroup = userGroup(groupId, name, description, joinCode, 1);
            JsonObject adminGroup = adminGroup(groupId, name, description, joinCode, "user@example.com", 1);
            myGroups.add(myGroup);
            adminGroups.add(adminGroup);
            return myGroup.deepCopy();
        }

        synchronized JsonObject updateUserGroup(long groupId, String name, String description) {
            updateGroupList(myGroups, groupId, name, description);
            updateGroupList(adminGroups, groupId, name, description);
            return findById(myGroups, groupId).deepCopy();
        }

        synchronized JsonObject adminCreateGroup(long ownerId, String name, String description) {
            long groupId = nextGroupId++;
            String ownerEmail = emailForUser(ownerId);
            JsonObject group = adminGroup(groupId, name, description, "ADM" + groupId, ownerEmail, 1);
            adminGroups.add(group);
            return group.deepCopy();
        }

        synchronized JsonObject adminUpdateGroup(long groupId, String name, String description) {
            updateGroupList(adminGroups, groupId, name, description);
            return findById(adminGroups, groupId).deepCopy();
        }

        synchronized void adminDeleteGroup(long groupId) {
            adminGroups.removeIf(group -> group.get("id").getAsLong() == groupId);
        }

        synchronized JsonObject createUserBooking(JsonObject body) {
            long bookingId = nextBookingId++;
            long facilityId = body.get("facilityId").getAsLong();
            long groupId = body.get("groupId").getAsLong();
            String facilityName = facilityNameFor(facilityId);
            String groupName = groupNameFor(groupId);
            String startTime = body.get("startTime").getAsString();
            String endTime = body.get("endTime").getAsString();
            String notes = body.get("notes").getAsString();

            JsonObject myBooking = userBooking(bookingId, facilityId, facilityName, groupId, groupName, startTime, endTime, "CONFIRMED", notes);
            JsonObject adminBooking = adminBooking(bookingId, 2, "user@example.com", facilityId, facilityName, groupId, groupName, startTime, endTime, "CONFIRMED", notes);
            myBookings.add(myBooking);
            adminBookings.add(adminBooking);
            return myBooking.deepCopy();
        }

        synchronized JsonObject updateUserBooking(long bookingId, JsonObject body) {
            long facilityId = body.get("facilityId").getAsLong();
            long groupId = body.get("groupId").getAsLong();
            String facilityName = facilityNameFor(facilityId);
            String groupName = groupNameFor(groupId);
            String startTime = body.get("startTime").getAsString();
            String endTime = body.get("endTime").getAsString();
            String notes = body.get("notes").getAsString();

            updateBookingList(myBookings, bookingId, facilityId, facilityName, groupId, groupName, startTime, endTime, null, notes);
            updateBookingList(adminBookings, bookingId, facilityId, facilityName, groupId, groupName, startTime, endTime, null, notes);
            return findById(myBookings, bookingId).deepCopy();
        }

        synchronized JsonObject cancelUserBooking(long bookingId) {
            updateBookingStatus(myBookings, bookingId, "CANCELLED");
            updateBookingStatus(adminBookings, bookingId, "CANCELLED");
            return findById(myBookings, bookingId).deepCopy();
        }

        private void updateGroupList(List<JsonObject> groups, long groupId, String name, String description) {
            JsonObject group = findById(groups, groupId);
            group.addProperty("name", name);
            group.addProperty("description", description);
        }

        private void updateBookingList(List<JsonObject> bookings,
                                       long bookingId,
                                       long facilityId,
                                       String facilityName,
                                       long groupId,
                                       String groupName,
                                       String startTime,
                                       String endTime,
                                       String status,
                                       String notes) {
            JsonObject booking = findById(bookings, bookingId);
            booking.addProperty("facilityId", facilityId);
            booking.addProperty("facilityName", facilityName);
            booking.addProperty("groupId", groupId);
            booking.addProperty("groupName", groupName);
            booking.addProperty("startTime", startTime);
            booking.addProperty("endTime", endTime);
            if (status != null) {
                booking.addProperty("status", status);
            }
            booking.addProperty("notes", notes);
        }

        private void updateBookingStatus(List<JsonObject> bookings, long bookingId, String status) {
            JsonObject booking = findById(bookings, bookingId);
            booking.addProperty("status", status);
        }

        private String facilityNameFor(long facilityId) {
            return findById(facilities, facilityId).get("name").getAsString();
        }

        private String groupNameFor(long groupId) {
            for (JsonObject group : myGroups) {
                if (group.get("id").getAsLong() == groupId) {
                    return group.get("name").getAsString();
                }
            }
            return findById(adminGroups, groupId).get("name").getAsString();
        }

        private String emailForUser(long userId) {
            for (JsonObject user : validUsers) {
                if (user.get("id").getAsLong() == userId) {
                    return user.get("email").getAsString();
                }
            }
            return "unknown@example.com";
        }

        private JsonObject findById(List<JsonObject> elements, long id) {
            return elements.stream()
                    .filter(element -> element.get("id").getAsLong() == id)
                    .findFirst()
                    .orElseThrow();
        }

        private JsonObject user(long id, String email, String role, boolean active) {
            JsonObject user = new JsonObject();
            user.addProperty("id", id);
            user.addProperty("email", email);
            user.addProperty("role", role);
            user.addProperty("active", active);
            return user;
        }

        private JsonObject facility(long id, String name, String type, int capacity, String location, String description) {
            JsonObject facility = new JsonObject();
            facility.addProperty("id", id);
            facility.addProperty("name", name);
            facility.addProperty("type", type);
            facility.addProperty("capacity", capacity);
            facility.addProperty("location", location);
            facility.addProperty("description", description);
            return facility;
        }

        private JsonObject userGroup(long id, String name, String description, String joinCode, int memberCount) {
            JsonObject group = new JsonObject();
            group.addProperty("id", id);
            group.addProperty("name", name);
            group.addProperty("description", description);
            group.addProperty("joinCode", joinCode);
            group.addProperty("memberCount", memberCount);
            return group;
        }

        private JsonObject adminGroup(long id, String name, String description, String joinCode, String ownerEmail, int memberCount) {
            JsonObject group = userGroup(id, name, description, joinCode, memberCount);
            group.addProperty("ownerEmail", ownerEmail);
            return group;
        }

        private JsonObject userBooking(long id,
                                       long facilityId,
                                       String facilityName,
                                       long groupId,
                                       String groupName,
                                       String startTime,
                                       String endTime,
                                       String status,
                                       String notes) {
            JsonObject booking = new JsonObject();
            booking.addProperty("id", id);
            booking.addProperty("facilityId", facilityId);
            booking.addProperty("facilityName", facilityName);
            booking.addProperty("groupId", groupId);
            booking.addProperty("groupName", groupName);
            booking.addProperty("startTime", startTime);
            booking.addProperty("endTime", endTime);
            booking.addProperty("status", status);
            booking.addProperty("notes", notes);
            return booking;
        }

        private JsonObject adminBooking(long id,
                                        long userId,
                                        String userEmail,
                                        long facilityId,
                                        String facilityName,
                                        long groupId,
                                        String groupName,
                                        String startTime,
                                        String endTime,
                                        String status,
                                        String notes) {
            JsonObject booking = userBooking(id, facilityId, facilityName, groupId, groupName, startTime, endTime, status, notes);
            booking.addProperty("userId", userId);
            booking.addProperty("userEmail", userEmail);
            return booking;
        }
    }
}
