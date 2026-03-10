package co.edu.escuelaing.microspring.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import co.edu.escuelaing.microspring.ioc.MicroSpringContext;

/**
 * Simple HTTP server implementation that handles both static files
 * (HTML, PNG, CSS, JS) and dynamic REST endpoints managed by the
 * MicroSpring IoC framework.
 * 
 * This server is designed to handle multiple non-concurrent requests
 * and provides functionality similar to Apache web server.
 * 
 * @author Angel Cuervo
 */
public class HttpServer {
    
    private static final int DEFAULT_PORT = 8080;
    private static final String STATIC_FILES_PATH = "src/main/resources/static";
    
    private final int port;
    private final MicroSpringContext context;
    private boolean running;
    
    /**
     * Creates a new HTTP server with the specified port.
     * @param port the port to listen on
     * @param context the MicroSpring context for handling REST endpoints
     */
    public HttpServer(int port, MicroSpringContext context) {
        this.port = port;
        this.context = context;
        this.running = false;
    }
    
    /**
     * Creates a new HTTP server with the default port (8080).
     * @param context the MicroSpring context for handling REST endpoints
     */
    public HttpServer(MicroSpringContext context) {
        this(DEFAULT_PORT, context);
    }
    
    /**
     * Starts the HTTP server and begins listening for incoming connections.
     * The server will handle requests sequentially (non-concurrent).
     */
    public void start() {
        running = true;
        System.out.println("========================================");
        System.out.println("   MicroSpring Server Starting...");
        System.out.println("========================================");
        System.out.println("Port: " + port);
        System.out.println("Static files: " + STATIC_FILES_PATH);
        System.out.println("Registered endpoints:");
        context.getEndpoints().forEach((path, handler) -> 
            System.out.println("  GET " + path));
        System.out.println("========================================");
        System.out.println("Server ready at http://localhost:" + port);
        System.out.println("========================================\n");
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (running) {
                Socket clientSocket = serverSocket.accept();
                handleRequest(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Stops the server.
     */
    public void stop() {
        running = false;
    }
    
    /**
     * Handles an individual HTTP request.
     * @param clientSocket the client socket connection
     */
    private void handleRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {
            
            // Read the request line
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                clientSocket.close();
                return;
            }
            
            System.out.println("Request: " + requestLine);
            
            // Parse the request
            String[] requestParts = requestLine.split(" ");
            if (requestParts.length < 2) {
                sendErrorResponse(out, 400, "Bad Request");
                clientSocket.close();
                return;
            }
            
            String method = requestParts[0];
            String fullPath = requestParts[1];
            
            // Parse path and query parameters
            String path;
            Map<String, String> queryParams = new HashMap<>();
            
            int queryIndex = fullPath.indexOf('?');
            if (queryIndex != -1) {
                path = fullPath.substring(0, queryIndex);
                String queryString = fullPath.substring(queryIndex + 1);
                queryParams = parseQueryParams(queryString);
            } else {
                path = fullPath;
            }
            
            // Consume headers (we don't use them for now)
            String headerLine;
            while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
                // Just consume headers
            }
            
            // Handle the request based on method
            if ("GET".equalsIgnoreCase(method)) {
                handleGetRequest(out, path, queryParams);
            } else {
                sendErrorResponse(out, 405, "Method Not Allowed");
            }
            
        } catch (IOException e) {
            System.err.println("Error handling request: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                // Ignore close errors
            }
        }
    }
    
    /**
     * Handles GET requests - checks for REST endpoints first, then static files.
     */
    private void handleGetRequest(OutputStream out, String path, Map<String, String> queryParams) throws IOException {
        // First, check if this is a registered REST endpoint
        if (context.hasEndpoint(path)) {
            try {
                String response = context.invokeEndpoint(path, queryParams);
                sendResponse(out, 200, "OK", "text/html; charset=UTF-8", response);
            } catch (Exception e) {
                System.err.println("Error invoking endpoint: " + e.getMessage());
                e.printStackTrace();
                sendErrorResponse(out, 500, "Internal Server Error: " + e.getMessage());
            }
            return;
        }
        
        // Otherwise, try to serve a static file
        serveStaticFile(out, path);
    }
    
    /**
     * Serves a static file from the resources directory.
     */
    private void serveStaticFile(OutputStream out, String path) throws IOException {
        // Default to index.html for root path
        if (path.equals("/")) {
            path = "/index.html";
        }
        
        // Prevent directory traversal attacks
        if (path.contains("..")) {
            sendErrorResponse(out, 403, "Forbidden");
            return;
        }
        
        Path filePath = Paths.get(STATIC_FILES_PATH + path);
        
        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            sendErrorResponse(out, 404, "Not Found");
            return;
        }
        
        // Determine content type
        String contentType = getContentType(path);
        
        // Read and send the file
        byte[] fileContent = Files.readAllBytes(filePath);
        sendBinaryResponse(out, 200, "OK", contentType, fileContent);
    }
    
    /**
     * Parses query parameters from a query string.
     */
    private Map<String, String> parseQueryParams(String queryString) {
        Map<String, String> params = new HashMap<>();
        if (queryString == null || queryString.isEmpty()) {
            return params;
        }
        
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                params.put(key, value);
            } else if (keyValue.length == 1) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                params.put(key, "");
            }
        }
        
        return params;
    }
    
    /**
     * Determines the content type based on file extension.
     */
    private String getContentType(String path) {
        String lowerPath = path.toLowerCase();
        if (lowerPath.endsWith(".html") || lowerPath.endsWith(".htm")) {
            return "text/html; charset=UTF-8";
        } else if (lowerPath.endsWith(".css")) {
            return "text/css; charset=UTF-8";
        } else if (lowerPath.endsWith(".js")) {
            return "application/javascript; charset=UTF-8";
        } else if (lowerPath.endsWith(".json")) {
            return "application/json; charset=UTF-8";
        } else if (lowerPath.endsWith(".png")) {
            return "image/png";
        } else if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerPath.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerPath.endsWith(".ico")) {
            return "image/x-icon";
        } else if (lowerPath.endsWith(".svg")) {
            return "image/svg+xml";
        } else {
            return "application/octet-stream";
        }
    }
    
    /**
     * Sends an HTTP text response.
     */
    private void sendResponse(OutputStream out, int statusCode, String statusText, 
                              String contentType, String body) throws IOException {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        sendBinaryResponse(out, statusCode, statusText, contentType, bodyBytes);
    }
    
    /**
     * Sends an HTTP binary response.
     */
    private void sendBinaryResponse(OutputStream out, int statusCode, String statusText,
                                    String contentType, byte[] body) throws IOException {
        StringBuilder headers = new StringBuilder();
        headers.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusText).append("\r\n");
        headers.append("Content-Type: ").append(contentType).append("\r\n");
        headers.append("Content-Length: ").append(body.length).append("\r\n");
        headers.append("Connection: close\r\n");
        headers.append("\r\n");
        
        out.write(headers.toString().getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }
    
    /**
     * Sends an HTTP error response.
     */
    private void sendErrorResponse(OutputStream out, int statusCode, String message) throws IOException {
        String body = String.format(
            "<!DOCTYPE html><html><head><title>%d %s</title></head>" +
            "<body><h1>%d %s</h1><p>%s</p></body></html>",
            statusCode, message, statusCode, message, message
        );
        sendResponse(out, statusCode, message, "text/html; charset=UTF-8", body);
    }
    
    /**
     * Gets the port this server is running on.
     * @return the port number
     */
    public int getPort() {
        return port;
    }
}
