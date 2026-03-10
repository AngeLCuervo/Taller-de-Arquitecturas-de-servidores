package co.edu.escuelaing.microspring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to map HTTP GET requests to specific handler methods.
 * Methods annotated with @GetMapping will be invoked when a GET request
 * is received at the specified URI path.
 * 
 * The method return type should be String for returning response content.
 * 
 * Example usage:
 * <pre>
 * {@code
 * @GetMapping("/hello")
 * public String sayHello() {
 *     return "Hello World!";
 * }
 * }
 * </pre>
 * 
 * @author Angel Cuervo
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GetMapping {
    
    /**
     * The URI path to map for this endpoint.
     * @return the path this method handles
     */
    String value();
}
