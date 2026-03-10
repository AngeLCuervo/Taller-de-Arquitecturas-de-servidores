package co.edu.escuelaing.microspring.ioc;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.escuelaing.microspring.annotations.GetMapping;
import co.edu.escuelaing.microspring.annotations.RequestParam;
import co.edu.escuelaing.microspring.annotations.RestController;

/**
 * Unit tests for the MicroSpring IoC Context.
 * Tests component registration, endpoint discovery, and method invocation.
 * 
 * @author Angel Cuervo
 */
class MicroSpringContextTest {

    private MicroSpringContext context;

    @BeforeEach
    void setUp() {
        context = new MicroSpringContext();
    }

    @Test
    @DisplayName("Should register a controller class")
    void testRegisterController() throws Exception {
        context.registerController(TestController.class);
        
        assertNotNull(context.getBean(TestController.class), 
            "Controller should be registered as a bean");
    }

    @Test
    @DisplayName("Should discover endpoints from @GetMapping annotations")
    void testEndpointDiscovery() throws Exception {
        context.registerController(TestController.class);
        
        assertTrue(context.hasEndpoint("/test"), 
            "Should discover /test endpoint from @GetMapping");
        assertTrue(context.hasEndpoint("/test/hello"),
            "Should discover /test/hello endpoint from @GetMapping");
    }

    @Test
    @DisplayName("Should invoke endpoint without parameters")
    void testInvokeEndpointNoParams() throws Exception {
        context.registerController(TestController.class);
        
        Map<String, String> params = new HashMap<>();
        String result = context.invokeEndpoint("/test", params);
        
        assertEquals("Test response", result, 
            "Should return correct response from endpoint");
    }

    @Test
    @DisplayName("Should invoke endpoint with @RequestParam")
    void testInvokeEndpointWithParam() throws Exception {
        context.registerController(TestController.class);
        
        Map<String, String> params = new HashMap<>();
        params.put("name", "Angel");
        String result = context.invokeEndpoint("/test/greet", params);
        
        assertEquals("Hello Angel", result,
            "Should pass request parameter to method");
    }

    @Test
    @DisplayName("Should use default value when @RequestParam is not provided")
    void testRequestParamDefaultValue() throws Exception {
        context.registerController(TestController.class);
        
        Map<String, String> params = new HashMap<>();
        String result = context.invokeEndpoint("/test/greet", params);
        
        assertEquals("Hello World", result,
            "Should use default value when parameter is not provided");
    }

    @Test
    @DisplayName("Should throw exception for non-existent endpoint")
    void testNonExistentEndpoint() {
        assertFalse(context.hasEndpoint("/nonexistent"),
            "Should not have non-registered endpoint");
        
        assertThrows(IllegalArgumentException.class, () -> {
            context.invokeEndpoint("/nonexistent", new HashMap<>());
        }, "Should throw exception when invoking non-existent endpoint");
    }

    @Test
    @DisplayName("Should throw exception when registering non-annotated class")
    void testRegisterNonController() {
        assertThrows(IllegalArgumentException.class, () -> {
            context.registerController("java.lang.String");
        }, "Should throw exception for non-controller class");
    }

    @Test
    @DisplayName("Should handle multiple request parameters")
    void testMultipleRequestParams() throws Exception {
        context.registerController(TestController.class);
        
        Map<String, String> params = new HashMap<>();
        params.put("a", "5");
        params.put("b", "3");
        String result = context.invokeEndpoint("/test/add", params);
        
        assertEquals("8", result, 
            "Should correctly handle multiple request parameters");
    }

    @Test
    @DisplayName("Should return all registered endpoints")
    void testGetEndpoints() throws Exception {
        context.registerController(TestController.class);
        
        Map<String, MicroSpringContext.EndpointHandler> endpoints = context.getEndpoints();
        
        assertFalse(endpoints.isEmpty(), "Should have registered endpoints");
        assertTrue(endpoints.containsKey("/test"), "Should contain /test endpoint");
        assertTrue(endpoints.containsKey("/test/hello"), "Should contain /test/hello endpoint");
        assertTrue(endpoints.containsKey("/test/greet"), "Should contain /test/greet endpoint");
    }

    /**
     * Test controller for unit tests.
     */
    @RestController
    public static class TestController {

        @GetMapping("/test")
        public String test() {
            return "Test response";
        }

        @GetMapping("/test/hello")
        public String hello() {
            return "Hello from test!";
        }

        @GetMapping("/test/greet")
        public String greet(@RequestParam(value = "name", defaultValue = "World") String name) {
            return "Hello " + name;
        }

        @GetMapping("/test/add")
        public String add(
                @RequestParam(value = "a", defaultValue = "0") String a,
                @RequestParam(value = "b", defaultValue = "0") String b) {
            int numA = Integer.parseInt(a);
            int numB = Integer.parseInt(b);
            return String.valueOf(numA + numB);
        }
    }
}
