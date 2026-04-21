package com.example.nexusbooking;

import com.example.nexusbooking.model.User;
import com.example.nexusbooking.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FullFlowsE2EIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @BeforeEach
    void cleanDatabase() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "incidents",
                "bookings",
                "group_members",
                "groups",
                "facilities",
                "users");
    }

    @Test
    void adminFlow_shouldCoverCrudAndPermissions() throws Exception {
        String adminToken = bootstrapAdminAndGetToken();

        long user1Id = createUserByAdmin(adminToken, "u1@example.com", "password123", "USER");
        long user2Id = createUserByAdmin(adminToken, "u2@example.com", "password123", "USER");

        mockMvc.perform(withAuth(get("/api/users/valid-for-groups"), adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.email=='admin@example.com')]").isEmpty())
                .andExpect(jsonPath("$[*].role").isNotEmpty());

        long facilityId = createFacility(adminToken, "Pista Central");
        updateFacility(adminToken, facilityId, "Pista Central Renovada");

        long bookingGroupId = createGroupByAdmin(adminToken, "Team Alpha", "Grup principal", user1Id);
        updateGroupByAdmin(adminToken, bookingGroupId, "Team Alpha Editat", "Descripcio nova");
        long deletableGroupId = createGroupByAdmin(adminToken, "Team Beta", "Grup eliminable", user1Id);

        LocalDateTime start = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusHours(1);

        long bookingId = createBookingByAdmin(adminToken, user1Id, facilityId, bookingGroupId, start, end, "Reserva admin");
        updateBookingByAdmin(adminToken, bookingId, user1Id, facilityId, bookingGroupId, start.plusHours(1), end.plusHours(1), "Reserva editada");

        mockMvc.perform(withAuth(post("/api/admin/bookings/{id}/cancel", bookingId), adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Booking cancelled"));

        String user1Token = loginAndGetToken("u1@example.com", "password123");
        long incidentId = createIncident(user1Token, facilityId, "Llum fosa");

        mockMvc.perform(withAuth(put("/api/incidents/{id}/status", incidentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "IN_PROGRESS"
                                }
                                """), adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        mockMvc.perform(withAuth(delete("/api/incidents/{id}", incidentId), adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Incident deleted successfully"));

        mockMvc.perform(withAuth(delete("/api/admin/groups/{id}", deletableGroupId), adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Group deleted successfully"));

        mockMvc.perform(withAuth(delete("/api/admin/users/{id}", user2Id), adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));

        mockMvc.perform(withAuth(get("/api/admin/dashboard"), adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").exists())
                .andExpect(jsonPath("$.bookings").exists())
                .andExpect(jsonPath("$.groups").exists())
                .andExpect(jsonPath("$.incidents").exists());
    }

    @Test
    void userFlow_shouldCoverCreateEditJoinCancelLeaveDelete() throws Exception {
        String adminToken = bootstrapAdminAndGetToken();
        long user1Id = createUserByAdmin(adminToken, "player1@example.com", "password123", "USER");
        createUserByAdmin(adminToken, "player2@example.com", "password123", "USER");

        long facilityId = createFacility(adminToken, "Piscina Municipal");

        String user1Token = loginAndGetToken("player1@example.com", "password123");
        String user2Token = loginAndGetToken("player2@example.com", "password123");

        long managedGroupId = createGroupByUser(user1Token, "Grup Caps", "Initial desc");
        String joinCode = getGroupJoinCodeForUser(user1Token, managedGroupId);
        assertThat(joinCode).isNotBlank();

        mockMvc.perform(withAuth(put("/api/groups/{id}", managedGroupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Grup Caps Editat",
                                  "description": "Desc editada"
                                }
                                """), user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Grup Caps Editat"));

        mockMvc.perform(withAuth(post("/api/groups/join-by-code").param("code", joinCode), user2Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(managedGroupId));

        long bookingGroupId = createGroupByUser(user1Token, "Grup Reserves", "Booking group");

        LocalDateTime start = LocalDateTime.now().plusDays(3).withHour(9).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusHours(1);

        long bookingId = createBookingByUser(user1Token, facilityId, bookingGroupId, start, end, "User booking");

        mockMvc.perform(withAuth(put("/api/bookings/{id}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "facilityId", facilityId,
                                "groupId", bookingGroupId,
                                "startTime", start.plusHours(2).format(ISO),
                                "endTime", end.plusHours(2).format(ISO),
                                "notes", "Updated by user"
                        ))), user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notes").value("Updated by user"));

        mockMvc.perform(withAuth(post("/api/bookings/{id}/cancel", bookingId), user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        mockMvc.perform(withAuth(post("/api/groups/{id}/leave", managedGroupId), user2Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Left group successfully"));

        mockMvc.perform(withAuth(delete("/api/groups/{id}", managedGroupId), user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Group deleted successfully"));

        mockMvc.perform(withAuth(get("/api/bookings/mine"), user1Token))
                .andExpect(status().isOk());

        mockMvc.perform(withAuth(get("/api/groups/mine"), user1Token))
                .andExpect(status().isOk());

        // Admin user must not be allowed to create group-owned booking
        mockMvc.perform(withAuth(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "facilityId", facilityId,
                                "groupId", bookingGroupId,
                                "startTime", LocalDateTime.now().plusDays(5).format(ISO),
                                "endTime", LocalDateTime.now().plusDays(5).plusHours(1).format(ISO),
                                "notes", "admin should fail"
                        ))), adminToken))
                .andExpect(status().isBadRequest());

        assertThat(userRepository.findById(user1Id)).isPresent();
    }

    private String bootstrapAdminAndGetToken() throws Exception {
        register("admin@example.com", "password123");
        User admin = userRepository.findByEmail("admin@example.com").orElseThrow();
        admin.setRole(User.Role.ADMIN);
        userRepository.save(admin);
        return loginAndGetToken("admin@example.com", "password123");
    }

    private long createUserByAdmin(String adminToken, String email, String password, String role) throws Exception {
        MvcResult result = mockMvc.perform(withAuth(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "password", password,
                                "role", role
                        ))), adminToken))
                .andExpect(status().isCreated())
                .andReturn();
        return json(result).get("id").asLong();
    }

    private long createFacility(String adminToken, String name) throws Exception {
        MvcResult result = mockMvc.perform(withAuth(post("/api/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", name,
                                "description", "Desc",
                                "type", "SPORT",
                                "capacity", 20,
                                "location", "Barcelona",
                                "status", "ACTIVE"
                        ))), adminToken))
                .andExpect(status().isCreated())
                .andReturn();
        return json(result).get("id").asLong();
    }

    private void updateFacility(String adminToken, long facilityId, String newName) throws Exception {
        mockMvc.perform(withAuth(put("/api/facilities/{id}", facilityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", newName,
                                "description", "Desc updated",
                                "type", "SPORT",
                                "capacity", 25,
                                "location", "Girona",
                                "status", "ACTIVE"
                        ))), adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newName));
    }

    private long createGroupByAdmin(String adminToken, String name, String description, long ownerId) throws Exception {
        MvcResult result = mockMvc.perform(withAuth(post("/api/admin/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", name,
                                "description", description,
                                "ownerId", ownerId
                        ))), adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.joinCode").isString())
                .andReturn();
        return json(result).get("id").asLong();
    }

    private void updateGroupByAdmin(String adminToken, long groupId, String name, String description) throws Exception {
        mockMvc.perform(withAuth(put("/api/admin/groups/{id}", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", name,
                                "description", description
                        ))), adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name));
    }

    private long createBookingByAdmin(String adminToken,
                                      long userId,
                                      long facilityId,
                                      long groupId,
                                      LocalDateTime start,
                                      LocalDateTime end,
                                      String notes) throws Exception {
        MvcResult result = mockMvc.perform(withAuth(post("/api/admin/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "userId", userId,
                                "facilityId", facilityId,
                                "groupId", groupId,
                                "startTime", start.format(ISO),
                                "endTime", end.format(ISO),
                                "notes", notes
                        ))), adminToken))
                .andExpect(status().isCreated())
                .andReturn();
        return json(result).get("id").asLong();
    }

    private void updateBookingByAdmin(String adminToken,
                                      long bookingId,
                                      long userId,
                                      long facilityId,
                                      long groupId,
                                      LocalDateTime start,
                                      LocalDateTime end,
                                      String notes) throws Exception {
        mockMvc.perform(withAuth(put("/api/admin/bookings/{id}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "userId", userId,
                                "facilityId", facilityId,
                                "groupId", groupId,
                                "startTime", start.format(ISO),
                                "endTime", end.format(ISO),
                                "notes", notes
                        ))), adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notes").value(notes));
    }

    private long createIncident(String userToken, long facilityId, String title) throws Exception {
        MvcResult result = mockMvc.perform(withAuth(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "facilityId", facilityId,
                                "title", title,
                                "description", "detall"
                        ))), userToken))
                .andExpect(status().isCreated())
                .andReturn();
        return json(result).get("id").asLong();
    }

    private long createGroupByUser(String userToken, String name, String description) throws Exception {
        MvcResult result = mockMvc.perform(withAuth(post("/api/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", name,
                                "description", description
                        ))), userToken))
                .andExpect(status().isCreated())
                .andReturn();
        return json(result).get("id").asLong();
    }

    private String getGroupJoinCodeForUser(String userToken, long groupId) throws Exception {
        MvcResult result = mockMvc.perform(withAuth(get("/api/groups/mine"), userToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = json(result);
        for (JsonNode group : root) {
            if (group.get("id").asLong() == groupId) {
                return group.get("joinCode").asText();
            }
        }
        throw new IllegalStateException("Group not found in /mine response");
    }

    private long createBookingByUser(String userToken,
                                     long facilityId,
                                     long groupId,
                                     LocalDateTime start,
                                     LocalDateTime end,
                                     String notes) throws Exception {
        MvcResult result = mockMvc.perform(withAuth(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "facilityId", facilityId,
                                "groupId", groupId,
                                "startTime", start.format(ISO),
                                "endTime", end.format(ISO),
                                "notes", notes
                        ))), userToken))
                .andExpect(status().isCreated())
                .andReturn();
        return json(result).get("id").asLong();
    }

    private void register(String email, String password) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "password", password
                        ))))
                .andExpect(status().isCreated());
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "password", password
                        ))))
                .andExpect(status().isOk())
                .andReturn();
        return json(result).get("token").asText();
    }

    private MockHttpServletRequestBuilder withAuth(MockHttpServletRequestBuilder builder, String token) {
        return builder.header("Authorization", "Bearer " + token);
    }

    private JsonNode json(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
