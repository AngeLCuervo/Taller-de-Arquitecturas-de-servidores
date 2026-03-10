package co.edu.escuelaing.microspring.ioc;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.edu.escuelaing.microspring.annotations.GetMapping;
import co.edu.escuelaing.microspring.annotations.RequestParam;
import co.edu.escuelaing.microspring.annotations.RestController;

/**
 * MicroSpring IoC Context - The core of the dependency injection framework.
 * 
 * This class is responsible for:
 * - Scanning the classpath for classes annotated with @RestController
 * - Registering and managing REST endpoint handlers
 * - Using Java reflection to invoke handler methods
 * - Processing @GetMapping and @RequestParam annotations
 * 
 * The context supports automatic component discovery by scanning packages
 * for classes with the @RestController annotation.
 * 
 * @author Angel Cuervo
 */
public class MicroSpringContext {
    
    private final Map<String, EndpointHandler> endpoints;
    private final Map<Class<?>, Object> beans;
    
    /**
     * Creates a new MicroSpring context.
     */
    public MicroSpringContext() {
        this.endpoints = new HashMap<>();
        this.beans = new HashMap<>();
    }
    
    /**
     * Scans a package for classes annotated with @RestController and registers them.
     * This method explores the classpath recursively to find all controller components.
     * 
     * @param basePackage the base package to scan (e.g., "co.edu.escuelaing")
     * @throws Exception if scanning fails
     */
    public void scan(String basePackage) throws Exception {
        System.out.println("Scanning package: " + basePackage);
        
        String path = basePackage.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);
        
        List<File> directories = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            directories.add(new File(resource.toURI()));
        }
        
        for (File directory : directories) {
            findAndRegisterControllers(directory, basePackage);
        }
        
        System.out.println("Scanning completed. Found " + beans.size() + " controller(s) with " + 
                          endpoints.size() + " endpoint(s).");
    }
    
    /**
     * Recursively finds and registers controller classes in a directory.
     */
    private void findAndRegisterControllers(File directory, String packageName) throws Exception {
        if (!directory.exists()) {
            return;
        }
        
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        
        for (File file : files) {
            if (file.isDirectory()) {
                // Recursively scan subdirectories
                findAndRegisterControllers(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                // Load and check the class
                String className = packageName + "." + file.getName().replace(".class", "");
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(RestController.class)) {
                        registerController(clazz);
                    }
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    // Skip classes that can't be loaded
                    System.err.println("Warning: Could not load class " + className);
                }
            }
        }
    }
    
    /**
     * Registers a specific controller class by its fully qualified name.
     * This method allows loading a controller directly from command line.
     * 
     * @param className the fully qualified class name
     * @throws Exception if the class cannot be loaded or is not a valid controller
     */
    public void registerController(String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        if (!clazz.isAnnotationPresent(RestController.class)) {
            throw new IllegalArgumentException("Class " + className + " is not annotated with @RestController");
        }
        registerController(clazz);
    }
    
    /**
     * Registers a controller class and its endpoint methods.
     * 
     * @param controllerClass the controller class to register
     * @throws Exception if registration fails
     */
    public void registerController(Class<?> controllerClass) throws Exception {
        // Create an instance of the controller (simple IoC - no dependency injection yet)
        Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
        beans.put(controllerClass, controllerInstance);
        
        System.out.println("Registered controller: " + controllerClass.getName());
        
        // Get the base path from @RestController if specified
        RestController restController = controllerClass.getAnnotation(RestController.class);
        String basePath = restController.value();
        
        // Find and register all methods annotated with @GetMapping
        for (Method method : controllerClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping getMapping = method.getAnnotation(GetMapping.class);
                String path = basePath + getMapping.value();
                
                // Normalize the path
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
                
                EndpointHandler handler = new EndpointHandler(controllerInstance, method);
                endpoints.put(path, handler);
                
                System.out.println("  Mapped: GET " + path + " -> " + method.getName() + "()");
            }
        }
    }
    
    /**
     * Checks if an endpoint exists for the given path.
     * 
     * @param path the URL path
     * @return true if an endpoint is registered for this path
     */
    public boolean hasEndpoint(String path) {
        return endpoints.containsKey(path);
    }
    
    /**
     * Invokes an endpoint handler with the given query parameters.
     * 
     * @param path the URL path
     * @param queryParams the query parameters from the request
     * @return the response string from the handler
     * @throws Exception if invocation fails
     */
    public String invokeEndpoint(String path, Map<String, String> queryParams) throws Exception {
        EndpointHandler handler = endpoints.get(path);
        if (handler == null) {
            throw new IllegalArgumentException("No endpoint found for path: " + path);
        }
        return handler.invoke(queryParams);
    }
    
    /**
     * Gets all registered endpoints.
     * 
     * @return an unmodifiable map of path to handler
     */
    public Map<String, EndpointHandler> getEndpoints() {
        return Collections.unmodifiableMap(endpoints);
    }
    
    /**
     * Gets a bean instance by its class.
     * 
     * @param clazz the class of the bean
     * @return the bean instance, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        return (T) beans.get(clazz);
    }
    
    /**
     * Inner class representing an endpoint handler.
     * Contains the controller instance and the method to invoke.
     */
    public static class EndpointHandler {
        private final Object controller;
        private final Method method;
        private final List<ParameterInfo> parameterInfos;
        
        public EndpointHandler(Object controller, Method method) {
            this.controller = controller;
            this.method = method;
            this.parameterInfos = extractParameterInfo(method);
        }
        
        /**
         * Extracts parameter information from the method, including @RequestParam annotations.
         */
        private List<ParameterInfo> extractParameterInfo(Method method) {
            List<ParameterInfo> infos = new ArrayList<>();
            Parameter[] parameters = method.getParameters();
            
            for (Parameter parameter : parameters) {
                if (parameter.isAnnotationPresent(RequestParam.class)) {
                    RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                    infos.add(new ParameterInfo(
                        requestParam.value(),
                        requestParam.defaultValue(),
                        parameter.getType()
                    ));
                } else {
                    // Parameter without @RequestParam - use parameter name if available
                    infos.add(new ParameterInfo(
                        parameter.getName(),
                        "",
                        parameter.getType()
                    ));
                }
            }
            
            return infos;
        }
        
        /**
         * Invokes the handler method with the given query parameters.
         * 
         * @param queryParams the query parameters
         * @return the response string
         * @throws Exception if invocation fails
         */
        public String invoke(Map<String, String> queryParams) throws Exception {
            Object[] args = new Object[parameterInfos.size()];
            
            for (int i = 0; i < parameterInfos.size(); i++) {
                ParameterInfo info = parameterInfos.get(i);
                String value = queryParams.get(info.name);
                
                if (value == null || value.isEmpty()) {
                    value = info.defaultValue;
                }
                
                args[i] = convertValue(value, info.type);
            }
            
            Object result = method.invoke(controller, args);
            return result != null ? result.toString() : "";
        }
        
        /**
         * Converts a string value to the target type.
         */
        private Object convertValue(String value, Class<?> targetType) {
            if (targetType == String.class) {
                return value;
            } else if (targetType == int.class || targetType == Integer.class) {
                return value.isEmpty() ? 0 : Integer.parseInt(value);
            } else if (targetType == long.class || targetType == Long.class) {
                return value.isEmpty() ? 0L : Long.parseLong(value);
            } else if (targetType == double.class || targetType == Double.class) {
                return value.isEmpty() ? 0.0 : Double.parseDouble(value);
            } else if (targetType == boolean.class || targetType == Boolean.class) {
                return Boolean.parseBoolean(value);
            }
            return value;
        }
        
        /**
         * Gets the method this handler invokes.
         */
        public Method getMethod() {
            return method;
        }
        
        /**
         * Gets the controller instance.
         */
        public Object getController() {
            return controller;
        }
    }
    
    /**
     * Helper class to store parameter information.
     */
    private static class ParameterInfo {
        final String name;
        final String defaultValue;
        final Class<?> type;
        
        ParameterInfo(String name, String defaultValue, Class<?> type) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.type = type;
        }
    }
}
