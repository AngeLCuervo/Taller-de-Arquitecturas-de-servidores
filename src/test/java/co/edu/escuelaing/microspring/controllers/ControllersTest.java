package co.edu.escuelaing.microspring.controllers;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import co.edu.escuelaing.microspring.annotations.GetMapping;
import co.edu.escuelaing.microspring.annotations.RequestParam;
import co.edu.escuelaing.microspring.annotations.RestController;

/**
 * Unit tests for the REST controllers.
 * Tests that controllers are properly annotated and methods work correctly.
 * 
 * @author Angel Cuervo
 */
class ControllersTest {

    @Test
    @DisplayName("HelloController should be annotated with @RestController")
    void testHelloControllerAnnotation() {
        assertTrue(HelloController.class.isAnnotationPresent(RestController.class),
            "HelloController should have @RestController annotation");
    }

    @Test
    @DisplayName("HelloController index method should have @GetMapping")
    void testHelloControllerIndexMapping() throws NoSuchMethodException {
        Method method = HelloController.class.getMethod("index");
        assertTrue(method.isAnnotationPresent(GetMapping.class),
            "index method should have @GetMapping annotation");
        
        GetMapping mapping = method.getAnnotation(GetMapping.class);
        assertEquals("/", mapping.value(), "index should be mapped to /");
    }

    @Test
    @DisplayName("HelloController hello method should have @GetMapping")
    void testHelloControllerHelloMapping() throws NoSuchMethodException {
        Method method = HelloController.class.getMethod("hello");
        assertTrue(method.isAnnotationPresent(GetMapping.class),
            "hello method should have @GetMapping annotation");
        
        GetMapping mapping = method.getAnnotation(GetMapping.class);
        assertEquals("/hello", mapping.value(), "hello should be mapped to /hello");
    }

    @Test
    @DisplayName("GreetingController should be annotated with @RestController")
    void testGreetingControllerAnnotation() {
        assertTrue(GreetingController.class.isAnnotationPresent(RestController.class),
            "GreetingController should have @RestController annotation");
    }

    @Test
    @DisplayName("GreetingController greeting method should have @GetMapping")
    void testGreetingControllerMapping() throws NoSuchMethodException {
        Method method = GreetingController.class.getMethod("greeting", String.class);
        assertTrue(method.isAnnotationPresent(GetMapping.class),
            "greeting method should have @GetMapping annotation");
        
        GetMapping mapping = method.getAnnotation(GetMapping.class);
        assertEquals("/greeting", mapping.value(), "greeting should be mapped to /greeting");
    }

    @Test
    @DisplayName("GreetingController should have @RequestParam with defaultValue")
    void testGreetingControllerRequestParam() throws NoSuchMethodException {
        Method method = GreetingController.class.getMethod("greeting", String.class);
        
        assertTrue(method.getParameters()[0].isAnnotationPresent(RequestParam.class),
            "Parameter should have @RequestParam annotation");
        
        RequestParam param = method.getParameters()[0].getAnnotation(RequestParam.class);
        assertEquals("name", param.value(), "Parameter name should be 'name'");
        assertEquals("World", param.defaultValue(), "Default value should be 'World'");
    }

    @Test
    @DisplayName("GreetingController greeting should return correct greeting")
    void testGreetingResponse() {
        GreetingController controller = new GreetingController();
        String result = controller.greeting("Angel");
        
        assertTrue(result.contains("Hola Angel"),
            "Response should contain 'Hola Angel'");
    }

    @Test
    @DisplayName("ApiController should be annotated with @RestController")
    void testApiControllerAnnotation() {
        assertTrue(ApiController.class.isAnnotationPresent(RestController.class),
            "ApiController should have @RestController annotation");
    }

    @Test
    @DisplayName("ApiController status method should have @GetMapping")
    void testApiControllerStatusMapping() throws NoSuchMethodException {
        Method method = ApiController.class.getMethod("status");
        assertTrue(method.isAnnotationPresent(GetMapping.class),
            "status method should have @GetMapping annotation");
        
        GetMapping mapping = method.getAnnotation(GetMapping.class);
        assertEquals("/api/status", mapping.value(), "status should be mapped to /api/status");
    }

    @Test
    @DisplayName("ApiController echo method should support @RequestParam")
    void testApiControllerEchoParam() throws NoSuchMethodException {
        Method method = ApiController.class.getMethod("echo", String.class);
        
        assertTrue(method.getParameters()[0].isAnnotationPresent(RequestParam.class),
            "Echo parameter should have @RequestParam annotation");
        
        RequestParam param = method.getParameters()[0].getAnnotation(RequestParam.class);
        assertEquals("message", param.value(), "Parameter name should be 'message'");
        assertEquals("Hello!", param.defaultValue(), "Default value should be 'Hello!'");
    }

    @Test
    @DisplayName("ApiController calculate method should have multiple @RequestParam")
    void testApiControllerCalcParams() throws NoSuchMethodException {
        Method method = ApiController.class.getMethod("calculate", String.class, String.class, String.class);
        
        assertEquals(3, method.getParameterCount(), "calculate should have 3 parameters");
        
        for (var param : method.getParameters()) {
            assertTrue(param.isAnnotationPresent(RequestParam.class),
                "All calculate parameters should have @RequestParam");
        }
    }
}
