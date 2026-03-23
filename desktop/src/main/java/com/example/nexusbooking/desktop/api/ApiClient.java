package com.example.nexusbooking.desktop.api;

import com.example.nexusbooking.desktop.Config;
import com.example.nexusbooking.desktop.model.UserResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;

public class ApiClient {

    private final String BASE_URL = Config.getBaseUrl();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient http = new OkHttpClient();
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
}
