package co.edu.escuelaing.microspring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for custom annotations.
 * Tests that annotations are properly defined with correct retention and target policies.
 * 
 * @author Angel Cuervo
 */
class AnnotationsTest {

    @Test
    @DisplayName("RestController annotation should have RUNTIME retention")
    void testRestControllerRetention() {
        Retention retention = RestController.class.getAnnotation(Retention.class);
        assertNotNull(retention, "RestController should have @Retention annotation");
        assertEquals(RetentionPolicy.RUNTIME, retention.value(), 
            "RestController should have RUNTIME retention for reflection");
    }

    @Test
    @DisplayName("RestController annotation should target TYPE (classes)")
    void testRestControllerTarget() {
        Target target = RestController.class.getAnnotation(Target.class);
        assertNotNull(target, "RestController should have @Target annotation");
        assertArrayEquals(new ElementType[]{ElementType.TYPE}, target.value(),
            "RestController should target TYPE (classes)");
    }

    @Test
    @DisplayName("GetMapping annotation should have RUNTIME retention")
    void testGetMappingRetention() {
        Retention retention = GetMapping.class.getAnnotation(Retention.class);
        assertNotNull(retention, "GetMapping should have @Retention annotation");
        assertEquals(RetentionPolicy.RUNTIME, retention.value(),
            "GetMapping should have RUNTIME retention for reflection");
    }

    @Test
    @DisplayName("GetMapping annotation should target METHOD")
    void testGetMappingTarget() {
        Target target = GetMapping.class.getAnnotation(Target.class);
        assertNotNull(target, "GetMapping should have @Target annotation");
        assertArrayEquals(new ElementType[]{ElementType.METHOD}, target.value(),
            "GetMapping should target METHOD");
    }

    @Test
    @DisplayName("RequestParam annotation should have RUNTIME retention")
    void testRequestParamRetention() {
        Retention retention = RequestParam.class.getAnnotation(Retention.class);
        assertNotNull(retention, "RequestParam should have @Retention annotation");
        assertEquals(RetentionPolicy.RUNTIME, retention.value(),
            "RequestParam should have RUNTIME retention for reflection");
    }

    @Test
    @DisplayName("RequestParam annotation should target PARAMETER")
    void testRequestParamTarget() {
        Target target = RequestParam.class.getAnnotation(Target.class);
        assertNotNull(target, "RequestParam should have @Target annotation");
        assertArrayEquals(new ElementType[]{ElementType.PARAMETER}, target.value(),
            "RequestParam should target PARAMETER");
    }
}
