package co.edu.escuelaing.microspring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a class as a REST controller component.
 * Classes annotated with @RestController will be automatically discovered
 * and registered by the MicroSpring IoC framework.
 * 
 * This annotation indicates that the class contains methods that handle
 * HTTP requests using the @GetMapping annotation.
 * 
 * @author Angel Cuervo
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestController {
    
    /**
     * Optional value to specify a base path for all endpoints in this controller.
     * @return the base path for the controller
     */
    String value() default "";
}
