package co.edu.escuelaing.microspring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to extract query parameters from HTTP requests.
 * This annotation can be used on method parameters to bind
 * request parameters to method arguments.
 * 
 * Example usage:
 * <pre>
 * {@code
 * @GetMapping("/greeting")
 * public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
 *     return "Hello " + name;
 * }
 * }
 * </pre>
 * 
 * @author Angel Cuervo
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {
    
    /**
     * The name of the request parameter to bind.
     * @return the parameter name
     */
    String value();
    
    /**
     * The default value to use if the parameter is not present in the request.
     * @return the default value
     */
    String defaultValue() default "";
}
