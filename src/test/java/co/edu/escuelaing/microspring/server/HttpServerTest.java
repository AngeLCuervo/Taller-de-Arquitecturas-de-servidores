package co.edu.escuelaing.microspring.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.escuelaing.microspring.annotations.GetMapping;
import co.edu.escuelaing.microspring.annotations.RequestParam;
import co.edu.escuelaing.microspring.annotations.RestController;
import co.edu.escuelaing.microspring.ioc.MicroSpringContext;

/**
 * Integration tests for the HTTP Server.
 * Tests the full request/response cycle.
 * 
 * @author Angel Cuervo
 */
class HttpServerTest {

    private static Thread serverThread;
    private static final int TEST_PORT = 8888;
    private static HttpServer server;

    @BeforeAll
    static void startServer() throws Exception {
        MicroSpringContext context = new MicroSpringContext();
        context.registerController(IntegrationTestController.class);
        
        server = new HttpServer(TEST_PORT, context);
        
        serverThread = new Thread(() -> server.start());
        serverThread.setDaemon(true);
        serverThread.start();
        
        // Wait for server to start
        Thread.sleep(1000);
    }

    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.stop();
        }
        if (serverThread != null) {
            serverThread.interrupt();
        }
    }

    @Test
    @DisplayName("Should respond to GET request on registered endpoint")
    void testGetEndpoint() throws Exception {
        URL url = new URL("http://localhost:" + TEST_PORT + "/integration/test");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode, "Should return 200 OK");
        
        String response = readResponse(connection);
        assertEquals("Integration test response", response);
        
        connection.disconnect();
    }

    @Test
    @DisplayName("Should handle query parameters")
    void testQueryParameters() throws Exception {
        URL url = new URL("http://localhost:" + TEST_PORT + "/integration/greet?name=TestUser");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode, "Should return 200 OK");
        
        String response = readResponse(connection);
        assertEquals("Hello TestUser", response);
        
        connection.disconnect();
    }

    @Test
    @DisplayName("Should use default value when query parameter is missing")
    void testDefaultQueryParameter() throws Exception {
        URL url = new URL("http://localhost:" + TEST_PORT + "/integration/greet");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode, "Should return 200 OK");
        
        String response = readResponse(connection);
        assertEquals("Hello Default", response);
        
        connection.disconnect();
    }

    @Test
    @DisplayName("Should return 404 for non-existent endpoint")
    void testNotFoundEndpoint() throws Exception {
        URL url = new URL("http://localhost:" + TEST_PORT + "/nonexistent");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        int responseCode = connection.getResponseCode();
        assertEquals(404, responseCode, "Should return 404 Not Found");
        
        connection.disconnect();
    }

    @Test
    @DisplayName("Server should return correct content type")
    void testContentType() throws Exception {
        URL url = new URL("http://localhost:" + TEST_PORT + "/integration/test");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        
        String contentType = connection.getContentType();
        assertTrue(contentType.contains("text/html"), 
            "Should return text/html content type");
        
        connection.disconnect();
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    /**
     * Test controller for integration tests.
     */
    @RestController
    public static class IntegrationTestController {

        @GetMapping("/integration/test")
        public String test() {
            return "Integration test response";
        }

        @GetMapping("/integration/greet")
        public String greet(@RequestParam(value = "name", defaultValue = "Default") String name) {
            return "Hello " + name;
        }
    }
}
