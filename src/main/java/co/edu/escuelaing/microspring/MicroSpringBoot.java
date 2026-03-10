package co.edu.escuelaing.microspring;

import co.edu.escuelaing.microspring.ioc.MicroSpringContext;
import co.edu.escuelaing.microspring.server.HttpServer;

/**
 * MicroSpringBoot - Main entry point for the MicroSpring framework.
 * 
 * This class provides two modes of operation:
 * 
 * 1. Automatic component scanning: When run without arguments, it scans
 *    the default package for classes annotated with @RestController.
 * 
 * 2. Manual controller loading: When run with a class name argument,
 *    it loads only the specified controller class.
 * 
 * Example usage:
 * <pre>
 * // Automatic scanning
 * java -cp target/classes co.edu.escuelaing.microspring.MicroSpringBoot
 * 
 * // Manual loading
 * java -cp target/classes co.edu.escuelaing.microspring.MicroSpringBoot co.edu.escuelaing.microspring.controllers.HelloController
 * </pre>
 * 
 * @author Angel Cuervo
 */
public class MicroSpringBoot {
    
    private static final String DEFAULT_PACKAGE = "co.edu.escuelaing.microspring";
    private static final int DEFAULT_PORT = 8080;
    
    /**
     * Main entry point for the MicroSpring application.
     * 
     * @param args optional: class name(s) to load, or empty for auto-scanning
     */
    public static void main(String[] args) {
        try {
            // Parse port from environment variable or use default
            int port = DEFAULT_PORT;
            String portEnv = System.getenv("PORT");
            if (portEnv != null && !portEnv.isEmpty()) {
                try {
                    port = Integer.parseInt(portEnv);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid PORT environment variable, using default: " + DEFAULT_PORT);
                }
            }
            
            // Create the IoC context
            MicroSpringContext context = new MicroSpringContext();
            
            if (args.length > 0) {
                // Manual mode: Load specified controller class(es)
                System.out.println("Manual mode: Loading specified controller(s)...\n");
                for (String className : args) {
                    try {
                        context.registerController(className);
                    } catch (Exception e) {
                        System.err.println("Failed to load controller: " + className);
                        System.err.println("Error: " + e.getMessage());
                    }
                }
            } else {
                // Automatic mode: Scan for controllers
                System.out.println("Automatic mode: Scanning for @RestController components...\n");
                context.scan(DEFAULT_PACKAGE);
            }
            
            // Start the HTTP server
            HttpServer server = new HttpServer(port, context);
            
            // Add shutdown hook for graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nShutting down server...");
                server.stop();
            }));
            
            server.start();
            
        } catch (Exception e) {
            System.err.println("Failed to start MicroSpring application:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
