package co.edu.escuelaing.microspring.controllers;

import co.edu.escuelaing.microspring.annotations.GetMapping;
import co.edu.escuelaing.microspring.annotations.RestController;

/**
 * Simple Hello World REST controller.
 * 
 * This controller demonstrates the basic usage of @RestController
 * and @GetMapping annotations. It provides a simple endpoint that
 * returns a greeting message.
 * 
 * @author Angel Cuervo
 */
@RestController
public class HelloController {

    /**
     * Root endpoint that returns a welcome message.
     * 
     * @return a greeting message from MicroSpring
     */
    @GetMapping("/")
    public String index() {
        return "<!DOCTYPE html>" +
               "<html lang=\"en\">" +
               "<head>" +
               "    <meta charset=\"UTF-8\">" +
               "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
               "    <title>MicroSpring - Welcome</title>" +
               "    <style>" +
               "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; " +
               "               max-width: 800px; margin: 50px auto; padding: 20px; " +
               "               background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); " +
               "               min-height: 100vh; }" +
               "        .container { background: white; border-radius: 10px; padding: 40px; " +
               "                     box-shadow: 0 10px 40px rgba(0,0,0,0.2); }" +
               "        h1 { color: #667eea; margin-bottom: 20px; }" +
               "        p { color: #555; line-height: 1.6; }" +
               "        .endpoints { background: #f8f9fa; padding: 20px; border-radius: 5px; margin: 20px 0; }" +
               "        .endpoint { margin: 10px 0; }" +
               "        a { color: #667eea; text-decoration: none; font-weight: bold; }" +
               "        a:hover { text-decoration: underline; }" +
               "        code { background: #eee; padding: 2px 6px; border-radius: 3px; }" +
               "    </style>" +
               "</head>" +
               "<body>" +
               "    <div class=\"container\">" +
               "        <h1>Welcome to MicroSpring Framework!</h1>" +
               "        <p>Greetings from MicroSpring Boot!</p>" +
               "        <p>This is a lightweight IoC web framework built with Java reflection.</p>" +
               "        <div class=\"endpoints\">" +
               "            <h3>Available Endpoints:</h3>" +
               "            <div class=\"endpoint\">GET <a href=\"/\">/</a> - This welcome page</div>" +
               "            <div class=\"endpoint\">GET <a href=\"/hello\">/hello</a> - Simple hello message</div>" +
               "            <div class=\"endpoint\">GET <a href=\"/greeting\">/greeting</a> - Greeting with default name</div>" +
               "            <div class=\"endpoint\">GET <a href=\"/greeting?name=Angel\">/greeting?name=Angel</a> - Greeting with custom name</div>" +
               "            <div class=\"endpoint\">GET <a href=\"/api/status\">/api/status</a> - Server status</div>" +
               "            <div class=\"endpoint\">GET <a href=\"/index.html\">/index.html</a> - Static HTML page</div>" +
               "            <div class=\"endpoint\">GET <a href=\"/images/logo.png\">/images/logo.png</a> - Static PNG image</div>" +
               "        </div>" +
               "        <p><em>Developed by Angel Cuervo - Server Architecture Workshop</em></p>" +
               "    </div>" +
               "</body>" +
               "</html>";
    }

    /**
     * Simple hello endpoint.
     * 
     * @return a simple hello message
     */
    @GetMapping("/hello")
    public String hello() {
        return "<!DOCTYPE html>" +
               "<html><head><title>Hello</title>" +
               "<style>body{font-family:sans-serif;text-align:center;padding:50px;}</style>" +
               "</head><body>" +
               "<h1>Hello from MicroSpring!</h1>" +
               "<p>This endpoint is mapped using @GetMapping(\"/hello\")</p>" +
               "<a href=\"/\">Back to home</a>" +
               "</body></html>";
    }
}
