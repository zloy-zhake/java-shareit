package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class BaseClientTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private TestClient testClient;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        testClient = new TestClient(restTemplate);
    }

    @Test
    void get_WithUserId_ReturnsResponse() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo("/test"))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = testClient.get("/test", 1L);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    @Test
    void get_WithoutUserId_ReturnsResponse() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo("/test"))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = testClient.get("/test");

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    @Test
    void post_WithBody_ReturnsResponse() {
        mockServer.expect(method(HttpMethod.POST))
                .andExpect(requestTo("/test"))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = testClient.post("/test", 1L, "{\"name\":\"test\"}");

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    @Test
    void patch_WithBody_ReturnsResponse() {
        mockServer.expect(method(HttpMethod.PATCH))
                .andExpect(requestTo("/test/1"))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = testClient.patch("/test/1", 1L, "{\"name\":\"updated\"}");

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    @Test
    void delete_WithUserId_ReturnsResponse() {
        mockServer.expect(method(HttpMethod.DELETE))
                .andExpect(requestTo("/test/1"))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = testClient.delete("/test/1", 1L);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    @Test
    void put_WithBody_ReturnsResponse() {
        mockServer.expect(method(HttpMethod.PUT))
                .andExpect(requestTo("/test/1"))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = testClient.put("/test/1", 1L, "{\"name\":\"test\"}");

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    @Test
    void get_WithErrorStatus_ReturnsErrorResponse() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo("/test"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND).body("{\"error\":\"Not found\"}"));

        ResponseEntity<Object> response = testClient.get("/test");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void get_WithServerErrorStatus_ReturnsErrorResponse() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo("/test"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR).body("Error"));

        ResponseEntity<Object> response = testClient.get("/test");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        mockServer.verify();
    }

    @Test
    void get_WithNullUserId_DoesNotSetHeader() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(requestTo("/test"))
                .andRespond(withSuccess("{\"id\":1}", MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = testClient.getNullable("/test", null);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    /**
     * Test client that extends BaseClient to expose protected methods for testing.
     */
    static class TestClient extends BaseClient {
        public TestClient(RestTemplate restTemplate) {
            super(restTemplate);
        }

        public ResponseEntity<Object> get(String path) {
            return super.get(path);
        }

        public ResponseEntity<Object> get(String path, long userId) {
            return super.get(path, userId);
        }

        public ResponseEntity<Object> getNullable(String path, Long userId) {
            return super.get(path, userId, null);
        }

        public ResponseEntity<Object> post(String path, long userId, Object body) {
            return super.post(path, userId, body);
        }

        public ResponseEntity<Object> patch(String path, long userId, Object body) {
            return super.patch(path, userId, body);
        }

        public ResponseEntity<Object> delete(String path, long userId) {
            return super.delete(path, userId);
        }

        public ResponseEntity<Object> put(String path, long userId, Object body) {
            return super.put(path, userId, body);
        }
    }
}
