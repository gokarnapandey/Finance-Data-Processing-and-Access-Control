package com.zorvyn.assignment.financechatbot.client;

import com.zorvyn.assignment.financechatbot.security.CurrentToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Thin client over the downstream Finance Record Management API. Every call
 * forwards the caller's bearer token, so the Finance API re-enforces RBAC.
 * Methods return JSON (or a readable error string) for the LLM to summarise —
 * a 403 simply becomes text the model can relay back to the user.
 */
@Component
public class FinanceApiClient {

    private final RestClient client;
    private final CurrentToken currentToken;

    public FinanceApiClient(RestClient financeRestClient, CurrentToken currentToken) {
        this.client = financeRestClient;
        this.currentToken = currentToken;
    }

    // ----- Dashboard -----------------------------------------------------

    public String dashboardSummary() {
        return get("/api/v1/dashboard/summary", Map.of());
    }

    public String categoryTotals() {
        return get("/api/v1/dashboard/categories", Map.of());
    }

    public String monthlyTrends() {
        return get("/api/v1/dashboard/trends", Map.of());
    }

    public String recentActivities() {
        return get("/api/v1/dashboard/recent", Map.of());
    }

    // ----- Records (read) ------------------------------------------------

    public String listRecords(int page, int size) {
        return get("/api/v1/records", orderedParams("page", page, "size", size));
    }

    public String getRecord(String recordId) {
        return get("/api/v1/records/" + recordId, Map.of());
    }

    public String filterRecords(String category, String type, String startDate, String endDate, int page, int size) {
        Map<String, Object> params = orderedParams(
                "category", category,
                "type", type,
                "startDate", startDate,
                "endDate", endDate,
                "page", page,
                "size", size);
        return get("/api/v1/records/filter", params);
    }

    // ----- Records (write) -----------------------------------------------

    public String createRecord(Object body) {
        return post("/api/v1/records", body);
    }

    public String updateRecord(String recordId, Object body) {
        return put("/api/v1/records/" + recordId, body);
    }

    public String deleteRecord(String recordId) {
        return delete("/api/v1/records/" + recordId);
    }

    // ----- Users (admin) -------------------------------------------------

    public String createUser(Object body) {
        return post("/api/v1/users", body);
    }

    public String listUsers() {
        return get("/api/v1/users", Map.of());
    }

    public String getUser(String userId) {
        return get("/api/v1/users/" + userId, Map.of());
    }

    public String updateUser(String userId, Object body) {
        return put("/api/v1/users/" + userId, body);
    }

    public String updateUserStatus(String userId, boolean active) {
        return patch("/api/v1/users/" + userId + "/status", orderedParams("active", active));
    }

    public String deleteUser(String userId) {
        return delete("/api/v1/users/" + userId);
    }

    public String listDeletedUsers() {
        return get("/api/v1/users/deleted", Map.of());
    }

    // ----- HTTP plumbing -------------------------------------------------

    private String get(String path, Map<String, Object> queryParams) {
        return exec(() -> client.get()
                .uri(uri(path, queryParams))
                .headers(this::auth)
                .retrieve()
                .body(String.class));
    }

    private String post(String path, Object body) {
        return exec(() -> client.post()
                .uri(uri(path, Map.of()))
                .headers(this::auth)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class));
    }

    private String put(String path, Object body) {
        return exec(() -> client.put()
                .uri(uri(path, Map.of()))
                .headers(this::auth)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class));
    }

    private String patch(String path, Map<String, Object> queryParams) {
        return exec(() -> client.patch()
                .uri(uri(path, queryParams))
                .headers(this::auth)
                .retrieve()
                .body(String.class));
    }

    private String delete(String path) {
        return exec(() -> {
            client.delete()
                    .uri(uri(path, Map.of()))
                    .headers(this::auth)
                    .retrieve()
                    .toBodilessEntity();
            return "{\"status\":\"deleted\"}";
        });
    }

    private void auth(org.springframework.http.HttpHeaders headers) {
        String token = currentToken.getToken();
        if (token != null) {
            headers.setBearerAuth(token);
        }
    }

    private Function<UriBuilder, URI> uri(String path, Map<String, Object> queryParams) {
        return builder -> {
            builder.path(path);
            queryParams.forEach((k, v) -> {
                if (v != null) {
                    builder.queryParam(k, v);
                }
            });
            return builder.build();
        };
    }

    /** Runs a downstream call, converting HTTP/connection errors into model-readable text. */
    private String exec(java.util.concurrent.Callable<String> call) {
        try {
            String body = call.call();
            return body == null ? "{}" : body;
        } catch (RestClientResponseException ex) {
            // 4xx/5xx from the Finance API (e.g. 403 forbidden, 404 not found, 400 validation).
            return "ERROR " + ex.getStatusCode().value() + ": " + ex.getResponseBodyAsString();
        } catch (ResourceAccessException ex) {
            return "ERROR: Cannot reach the Finance API. Ensure it is running. (" + ex.getMessage() + ")";
        } catch (Exception ex) {
            return "ERROR: " + ex.getMessage();
        }
    }

    private static Map<String, Object> orderedParams(Object... kv) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            map.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return map;
    }
}
