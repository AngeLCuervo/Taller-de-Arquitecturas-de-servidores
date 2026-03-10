package co.edu.escuelaing.microspring.controllers;

import java.util.concurrent.atomic.AtomicLong;

import co.edu.escuelaing.microspring.annotations.GetMapping;
import co.edu.escuelaing.microspring.annotations.RequestParam;
import co.edu.escuelaing.microspring.annotations.RestController;

/**
 * Greeting REST controller demonstrating @RequestParam usage.
 * 
 * This controller shows how to use the @RequestParam annotation to
 * extract query parameters from HTTP requests. It includes a counter
 * to demonstrate stateful behavior in controllers.
 * 
 * Example requests:
 * - GET /greeting -> Returns "Hola World"
 * - GET /greeting?name=Angel -> Returns "Hola Angel"
 * 
 * @author Angel Cuervo
 */
@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    /**
     * Greeting endpoint that accepts an optional name parameter.
     * 
     * @param name the name to greet (defaults to "World")
     * @return a personalized greeting message
     */
    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        long count = counter.incrementAndGet();
        return "<!DOCTYPE html>" +
               "<html><head><title>Greeting</title>" +
               "<style>" +
               "body{font-family:'Segoe UI',sans-serif;text-align:center;padding:50px;" +
               "background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);min-height:100vh;}" +
               ".card{background:white;max-width:500px;margin:0 auto;padding:40px;border-radius:10px;" +
               "box-shadow:0 10px 40px rgba(0,0,0,0.2);}" +
               "h1{color:#667eea;}" +
               ".count{color:#888;font-size:0.9em;}" +
               "input{padding:10px;margin:10px;border:2px solid #667eea;border-radius:5px;font-size:16px;}" +
               "button{padding:10px 20px;background:#667eea;color:white;border:none;border-radius:5px;" +
               "cursor:pointer;font-size:16px;}" +
               "button:hover{background:#5a6fd6;}" +
               "</style>" +
               "</head><body>" +
               "<div class=\"card\">" +
               "<h1>Hola " + name + "!</h1>" +
               "<p class=\"count\">Request count: " + count + "</p>" +
               "<hr>" +
               "<p>Try with a different name:</p>" +
               "<form action=\"/greeting\" method=\"get\">" +
               "<input type=\"text\" name=\"name\" placeholder=\"Enter your name\">" +
               "<button type=\"submit\">Greet</button>" +
               "</form>" +
               "<br><a href=\"/\">Back to home</a>" +
               "</div>" +
               "</body></html>";
    }
}
