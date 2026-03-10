package co.edu.escuelaing.microspring.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import co.edu.escuelaing.microspring.annotations.GetMapping;
import co.edu.escuelaing.microspring.annotations.RequestParam;
import co.edu.escuelaing.microspring.annotations.RestController;

/**
 * API REST controller demonstrating various endpoint capabilities.
 * 
 * This controller provides several API endpoints to demonstrate:
 * - Multiple endpoints in a single controller
 * - Different response types
 * - Query parameter handling
 * - Server status information
 * 
 * @author Angel Cuervo
 */
@RestController
public class ApiController {

    private final LocalDateTime startTime = LocalDateTime.now();

    /**
     * Returns the server status information.
     * 
     * @return JSON-formatted status information
     */
    @GetMapping("/api/status")
    public String status() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        return "<!DOCTYPE html>" +
               "<html><head><title>Server Status</title>" +
               "<style>" +
               "body{font-family:monospace;background:#1a1a2e;color:#16c79a;padding:40px;}" +
               ".status{background:#16213e;padding:30px;border-radius:10px;max-width:600px;margin:0 auto;}" +
               "h1{color:#e94560;}" +
               ".item{margin:10px 0;padding:10px;background:#0f3460;border-radius:5px;}" +
               ".label{color:#e94560;font-weight:bold;}" +
               "a{color:#16c79a;}" +
               "</style>" +
               "</head><body>" +
               "<div class=\"status\">" +
               "<h1>Server Status</h1>" +
               "<div class=\"item\"><span class=\"label\">Status:</span> Running</div>" +
               "<div class=\"item\"><span class=\"label\">Server:</span> MicroSpring Framework 1.0</div>" +
               "<div class=\"item\"><span class=\"label\">Java Version:</span> " + System.getProperty("java.version") + "</div>" +
               "<div class=\"item\"><span class=\"label\">OS:</span> " + System.getProperty("os.name") + "</div>" +
               "<div class=\"item\"><span class=\"label\">Started At:</span> " + startTime.format(formatter) + "</div>" +
               "<div class=\"item\"><span class=\"label\">Current Time:</span> " + now.format(formatter) + "</div>" +
               "<div class=\"item\"><span class=\"label\">Available Processors:</span> " + Runtime.getRuntime().availableProcessors() + "</div>" +
               "<div class=\"item\"><span class=\"label\">Total Memory:</span> " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " MB</div>" +
               "<div class=\"item\"><span class=\"label\">Free Memory:</span> " + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " MB</div>" +
               "<br><a href=\"/\">Back to home</a>" +
               "</div>" +
               "</body></html>";
    }

    /**
     * Simple echo endpoint that returns the input message.
     * 
     * @param message the message to echo
     * @return the echoed message
     */
    @GetMapping("/api/echo")
    public String echo(@RequestParam(value = "message", defaultValue = "Hello!") String message) {
        return "<!DOCTYPE html>" +
               "<html><head><title>Echo</title>" +
               "<style>" +
               "body{font-family:sans-serif;text-align:center;padding:50px;background:#f0f0f0;}" +
               ".echo{background:white;max-width:500px;margin:0 auto;padding:30px;border-radius:10px;box-shadow:0 2px 10px rgba(0,0,0,0.1);}" +
               "h2{color:#333;}" +
               ".message{font-size:24px;color:#667eea;margin:20px 0;padding:20px;background:#f8f9fa;border-radius:5px;}" +
               "input{padding:10px;width:200px;border:1px solid #ddd;border-radius:5px;}" +
               "button{padding:10px 20px;background:#667eea;color:white;border:none;border-radius:5px;cursor:pointer;}" +
               "</style>" +
               "</head><body>" +
               "<div class=\"echo\">" +
               "<h2>Echo Service</h2>" +
               "<div class=\"message\">\"" + message + "\"</div>" +
               "<form action=\"/api/echo\" method=\"get\">" +
               "<input type=\"text\" name=\"message\" placeholder=\"Enter message\">" +
               "<button type=\"submit\">Echo</button>" +
               "</form>" +
               "<br><a href=\"/\">Back to home</a>" +
               "</div>" +
               "</body></html>";
    }

    /**
     * Calculator endpoint for basic math operations.
     * 
     * @param a first number
     * @param b second number
     * @param op operation (add, sub, mul, div)
     * @return the result of the calculation
     */
    @GetMapping("/api/calc")
    public String calculate(
            @RequestParam(value = "a", defaultValue = "0") String a,
            @RequestParam(value = "b", defaultValue = "0") String b,
            @RequestParam(value = "op", defaultValue = "add") String op) {
        
        double numA = Double.parseDouble(a);
        double numB = Double.parseDouble(b);
        double result;
        String operation;
        
        switch (op.toLowerCase()) {
            case "sub":
                result = numA - numB;
                operation = "-";
                break;
            case "mul":
                result = numA * numB;
                operation = "*";
                break;
            case "div":
                result = numB != 0 ? numA / numB : 0;
                operation = "/";
                break;
            default:
                result = numA + numB;
                operation = "+";
        }
        
        return "<!DOCTYPE html>" +
               "<html><head><title>Calculator</title>" +
               "<style>" +
               "body{font-family:sans-serif;text-align:center;padding:50px;background:#2d3436;}" +
               ".calc{background:#636e72;color:white;max-width:400px;margin:0 auto;padding:30px;border-radius:15px;}" +
               ".result{font-size:32px;margin:20px 0;padding:20px;background:#00b894;border-radius:10px;}" +
               ".formula{font-size:18px;color:#ddd;}" +
               "input{padding:10px;width:60px;margin:5px;border:none;border-radius:5px;text-align:center;}" +
               "select{padding:10px;margin:5px;border:none;border-radius:5px;}" +
               "button{padding:10px 30px;background:#00b894;color:white;border:none;border-radius:5px;cursor:pointer;font-size:16px;}" +
               "a{color:#00b894;}" +
               "</style>" +
               "</head><body>" +
               "<div class=\"calc\">" +
               "<h2>Calculator</h2>" +
               "<div class=\"formula\">" + numA + " " + operation + " " + numB + " =</div>" +
               "<div class=\"result\">" + result + "</div>" +
               "<form action=\"/api/calc\" method=\"get\">" +
               "<input type=\"number\" name=\"a\" value=\"" + a + "\" step=\"any\">" +
               "<select name=\"op\">" +
               "<option value=\"add\"" + (op.equals("add") ? " selected" : "") + ">+</option>" +
               "<option value=\"sub\"" + (op.equals("sub") ? " selected" : "") + ">-</option>" +
               "<option value=\"mul\"" + (op.equals("mul") ? " selected" : "") + ">*</option>" +
               "<option value=\"div\"" + (op.equals("div") ? " selected" : "") + ">/</option>" +
               "</select>" +
               "<input type=\"number\" name=\"b\" value=\"" + b + "\" step=\"any\">" +
               "<br><br><button type=\"submit\">Calculate</button>" +
               "</form>" +
               "<br><a href=\"/\">Back to home</a>" +
               "</div>" +
               "</body></html>";
    }
}
