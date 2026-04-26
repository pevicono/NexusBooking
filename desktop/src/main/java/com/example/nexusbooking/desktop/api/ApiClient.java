package com.example.nexusbooking.desktop.api;

import com.example.nexusbooking.desktop.Config;
import com.example.nexusbooking.desktop.model.UserResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;

public class ApiClient {

    private final String BASE_URL = Config.getBaseUrl();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient http = new OkHttpClient.Builder()
        .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
        .addNetworkInterceptor(new SecurityHeaderInterceptor())  // Add security headers
        .build();
        
    private final Gson gson = new Gson();

    private String token;


    public String login(String email, String password) throws IOException, ApiException {
        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        body.addProperty("password", password);

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/auth/login")
                .post(RequestBody.create(body.toString(), JSON))
                .build();

        try (Response response = http.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (!response.isSuccessful()) {
                throw new ApiException(extractMessage(responseBody, "Login failed"));
            }
            JsonObject json = gson.fromJson(responseBody, JsonObject.class);
            token = json.get("token").getAsString();
            return token;
        }
    }

    public JsonArray getFacilities() throws IOException, ApiException {
        return gson.fromJson(sendAuthorizedRequest("/api/facilities", "GET", null), JsonArray.class);
    }

    public JsonArray getMyBookings() throws IOException, ApiException {
        return gson.fromJson(sendAuthorizedRequest("/api/bookings/mine", "GET", null), JsonArray.class);
    }

    public void createBooking(long facilityId, long groupId, String startTime, String endTime, String notes) throws IOException, ApiException {
        JsonObject body = new JsonObject();
        body.addProperty("facilityId", facilityId);
        body.addProperty("groupId", groupId);
        body.addProperty("startTime", startTime);
        body.addProperty("endTime", endTime);
        body.addProperty("notes", notes == null ? "" : notes);
        sendAuthorizedRequest("/api/bookings", "POST", body);
    }

    public void updateBooking(long bookingId, long facilityId, long groupId, String startTime, String endTime, String notes) throws IOException, ApiException {
        JsonObject body = new JsonObject();
        body.addProperty("facilityId", facilityId);
        body.addProperty("groupId", groupId);
        body.addProperty("startTime", startTime);
        body.addProperty("endTime", endTime);
        body.addProperty("notes", notes == null ? "" : notes);
        sendAuthorizedRequest("/api/bookings/" + bookingId, "PUT", body);
    }

    public void cancelBooking(long bookingId) throws IOException, ApiException {
        sendAuthorizedRequest("/api/bookings/" + bookingId + "/cancel", "POST", null);
    }

    public JsonArray getGroups() throws IOException, ApiException {
        return gson.fromJson(sendAuthorizedRequest("/api/groups", "GET", null), JsonArray.class);
    }

    public JsonArray getMyGroups() throws IOException, ApiException {
        return gson.fromJson(sendAuthorizedRequest("/api/groups/mine", "GET", null), JsonArray.class);
    }

    public void createGroup(String name, String description) throws IOException, ApiException {
        JsonObject body = new JsonObject();
        body.addProperty("name", name);
        body.addProperty("description", description == null ? "" : description);
        sendAuthorizedRequest("/api/groups", "POST", body);
    }

    public void updateGroup(long groupId, String name, String description) throws IOException, ApiException {
        JsonObject body = new JsonObject();
        body.addProperty("name", name);
        body.addProperty("description", description == null ? "" : description);
        sendAuthorizedRequest("/api/groups/" + groupId, "PUT", body);
    }

    public void joinGroup(long groupId) throws IOException, ApiException {
        sendAuthorizedRequest("/api/groups/" + groupId + "/join", "POST", null);
    }

    public void joinGroupByCode(String code) throws IOException, ApiException {
        sendAuthorizedRequest("/api/groups/join-by-code?code=" + java.net.URLEncoder.encode(code, java.nio.charset.StandardCharsets.UTF_8), "POST", null);
    }

    public void leaveGroup(long groupId) throws IOException, ApiException {
        sendAuthorizedRequest("/api/groups/" + groupId + "/leave", "POST", null);
    }

    public JsonObject getAdminDashboard() throws IOException, ApiException {
        return gson.fromJson(sendAuthorizedRequest("/api/admin/dashboard", "GET", null), JsonObject.class);
    }

    public JsonArray getAdminUsers() throws IOException, ApiException {
        return gson.fromJson(sendAuthorizedRequest("/api/admin/users", "GET", null), JsonArray.class);
    }

    public JsonArray getValidUsersForGroups() throws IOException, ApiException {
        return gson.fromJson(sendAuthorizedRequest("/api/users/valid-for-groups", "GET", null), JsonArray.class);
    }

    public void setUserActive(long userId, boolean active) throws IOException, ApiException {
        sendAuthorizedRequest("/api/admin/users/" + userId + "/active?active=" + active, "PUT", null);
    }

    public JsonArray getAdminBookings() throws IOException, ApiException {
        return gson.fromJson(sendAuthorizedRequest("/api/admin/bookings", "GET", null), JsonArray.class);
    }

    public JsonArray getIncidents() throws IOException, ApiException {
        return gson.fromJson(sendAuthorizedRequest("/api/incidents", "GET", null), JsonArray.class);
    }

    public void createIncident(Long facilityId, String title, String description) throws IOException, ApiException {
        JsonObject body = new JsonObject();
        if (facilityId != null) body.addProperty("facilityId", facilityId);
        body.addProperty("title", title);
        body.addProperty("description", description == null ? "" : description);
        sendAuthorizedRequest("/api/incidents", "POST", body);
    }

    public void updateIncidentStatus(long incidentId, String status) throws IOException, ApiException {
        JsonObject body = new JsonObject();
        body.addProperty("status", status);
        sendAuthorizedRequest("/api/incidents/" + incidentId + "/status", "PUT", body);
    }

    public void createFacility(String name, String type, Integer capacity, String location, String description) throws IOException, ApiException {
        JsonObject body = new JsonObject();
        body.addProperty("name", name);
        body.addProperty("type", type);
        if (capacity != null) body.addProperty("capacity", capacity);
        body.addProperty("location", location == null ? "" : location);
        body.addProperty("description", description == null ? "" : description);
        sendAuthorizedRequest("/api/facilities", "POST", body);
    }

    public void register(String email, String password) throws IOException, ApiException {
        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        body.addProperty("password", password);

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/auth/register")
                .post(RequestBody.create(body.toString(), JSON))
                .build();

        try (Response response = http.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String responseBody = response.body().string();
                throw new ApiException(extractMessage(responseBody, "Registration failed"));
            }
        }
    }

    public void logout() throws IOException {
        if (token == null) return;
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/auth/logout")
                .addHeader("Authorization", "Bearer " + token)
                .post(RequestBody.create("", JSON))
                .build();
        http.newCall(request).execute().close();
        token = null;
    }


    private String extractMessage(String body, String fallback) {
        try {
            JsonObject json = gson.fromJson(body, JsonObject.class);
            if (json.has("message")) return json.get("message").getAsString();
            // Spring validation errors use a different structure
            if (json.has("errors")) return json.getAsJsonArray("errors").get(0).getAsJsonObject().get("defaultMessage").getAsString();
        } catch (Exception ignored) {}
        return fallback;
    }

    public UserResponse getCurrentUser() throws IOException, ApiException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/api/users/me")
                .addHeader("Authorization", "Bearer " + token)
                .get()
                .build();

        try (Response response = http.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (!response.isSuccessful()) {
                throw new ApiException("Failed to fetch user");
            }
            return gson.fromJson(responseBody, UserResponse.class);
        }
    }

    public UserResponse updateEmail(String newEmail) throws IOException, ApiException {
        JsonObject body = new JsonObject();
        body.addProperty("email", newEmail);

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/users/me")
                .addHeader("Authorization", "Bearer " + token)
                .put(RequestBody.create(body.toString(), JSON))
                .build();

        try (Response response = http.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (!response.isSuccessful()) {
                JsonObject error = gson.fromJson(responseBody, JsonObject.class);
                throw new ApiException(error.has("message") ? error.get("message").getAsString() : "Update failed");
            }
            return gson.fromJson(responseBody, UserResponse.class);
        }
    }

    public void changePassword(String currentPassword, String newPassword) throws IOException, ApiException {
        JsonObject body = new JsonObject();
        body.addProperty("currentPassword", currentPassword);
        body.addProperty("newPassword", newPassword);

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/users/me/password")
                .addHeader("Authorization", "Bearer " + token)
                .put(RequestBody.create(body.toString(), JSON))
                .build();

        try (Response response = http.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String responseBody = response.body().string();
                throw new ApiException(extractMessage(responseBody, "Failed to change password"));
            }
        }
    }

    public void createUserWithRole(String email, String password, String role) throws IOException, ApiException {
        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        body.addProperty("password", password);
        body.addProperty("role", role);

        Request request = new Request.Builder()
                .url(BASE_URL + "/api/admin/users")
                .addHeader("Authorization", "Bearer " + token)
                .post(RequestBody.create(body.toString(), JSON))
                .build();

        try (Response response = http.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String responseBody = response.body().string();
                throw new ApiException(extractMessage(responseBody, "Failed to create user"));
            }
        }
    }

    public void setUserRole(long userId, String role) throws IOException, ApiException {
        JsonObject body = new JsonObject();
        body.addProperty("role", role);
        sendAuthorizedRequest("/api/admin/users/" + userId + "/role", "PUT", body);
    }

    public void deleteUser(long userId) throws IOException, ApiException {
        sendAuthorizedRequest("/api/admin/users/" + userId, "DELETE", null);
    }

    public void updateFacility(long facilityId, String name, String type, Integer capacity, String location, String description) throws IOException, ApiException {
        JsonObject body = new JsonObject();
        body.addProperty("name", name);
        body.addProperty("type", type);
        if (capacity != null) body.addProperty("capacity", capacity);
        body.addProperty("location", location == null ? "" : location);
        body.addProperty("description", description == null ? "" : description);
        sendAuthorizedRequest("/api/facilities/" + facilityId, "PUT", body);
    }

    public void deleteFacility(long facilityId) throws IOException, ApiException {
        sendAuthorizedRequest("/api/facilities/" + facilityId, "DELETE", null);
    }

    public void deleteIncident(long incidentId) throws IOException, ApiException {
        sendAuthorizedRequest("/api/incidents/" + incidentId, "DELETE", null);
    }

    public void adminCreateBooking(long userId, long facilityId, long groupId, String startTime, String endTime, String notes) throws IOException, ApiException {
        JsonObject body = new JsonObject();
        body.addProperty("userId", userId);
        body.addProperty("facilityId", facilityId);
        body.addProperty("groupId", groupId);
        body.addProperty("startTime", startTime);
        body.addProperty("endTime", endTime);
        body.addProperty("notes", notes == null ? "" : notes);
        sendAuthorizedRequest("/api/admin/bookings", "POST", body);
    }

    public void adminUpdateBooking(long bookingId, long userId, long facilityId, long groupId, String startTime, String endTime, String notes) throws IOException, ApiException {
        JsonObject body = new JsonObject();
        body.addProperty("userId", userId);
        body.addProperty("facilityId", facilityId);
        body.addProperty("groupId", groupId);
        body.addProperty("startTime", startTime);
        body.addProperty("endTime", endTime);
        body.addProperty("notes", notes == null ? "" : notes);
        sendAuthorizedRequest("/api/admin/bookings/" + bookingId, "PUT", body);
    }

    public void adminCancelBooking(long bookingId) throws IOException, ApiException {
        sendAuthorizedRequest("/api/admin/bookings/" + bookingId + "/cancel", "POST", null);
    }

    public JsonArray getAdminGroups() throws IOException, ApiException {
        return gson.fromJson(sendAuthorizedRequest("/api/admin/groups", "GET", null), JsonArray.class);
    }

    public void adminCreateGroup(long ownerId, String name, String description) throws IOException, ApiException {
        JsonObject body = new JsonObject();
        body.addProperty("ownerId", ownerId);
        body.addProperty("name", name);
        body.addProperty("description", description == null ? "" : description);
        sendAuthorizedRequest("/api/admin/groups", "POST", body);
    }

    public void adminDeleteGroup(long groupId) throws IOException, ApiException {
        sendAuthorizedRequest("/api/admin/groups/" + groupId, "DELETE", null);
    }

    public void adminUpdateGroup(long groupId, String name, String description) throws IOException, ApiException {
        JsonObject body = new JsonObject();
        body.addProperty("name", name);
        body.addProperty("description", description == null ? "" : description);
        sendAuthorizedRequest("/api/admin/groups/" + groupId, "PUT", body);
    }

    private String sendAuthorizedRequest(String path, String method, JsonObject body) throws IOException, ApiException {
        if (token == null || token.isBlank()) {
            throw new ApiException("Not authenticated");
        }

        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + path)
                .addHeader("Authorization", "Bearer " + token);

        RequestBody requestBody = body == null ? RequestBody.create("", JSON) : RequestBody.create(body.toString(), JSON);
        switch (method) {
            case "POST" -> builder.post(requestBody);
            case "PUT" -> builder.put(requestBody);
            case "DELETE" -> builder.delete(requestBody);
            default -> builder.get();
        }

        try (Response response = http.newCall(builder.build()).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw new ApiException(extractMessage(responseBody, "Request failed"));
            }
            return responseBody;
        }
    }

    public void shutdown() {
        http.dispatcher().executorService().shutdownNow();
        http.connectionPool().evictAll();
        if (http.cache() != null) {
            try {
                http.cache().close();
            } catch (IOException ignored) {
                // Best-effort cleanup for tests/app shutdown.
            }
        }
    }
}
