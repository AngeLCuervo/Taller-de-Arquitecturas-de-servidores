# MicroSpring Framework

**Author:** Angel Cuervo  
**Workshop:** Server Architecture, Object Meta-Protocols, IoC Pattern, and Reflection

A lightweight IoC (Inversion of Control) web framework built from scratch using Java Reflection. This project implements a simple web server (similar to Apache) capable of serving static HTML pages and PNG images, while also providing a framework for building web applications using Plain Old Java Objects (POJOs).

![Java](https://img.shields.io/badge/Java-17-orange)
![Maven](https://img.shields.io/badge/Maven-3.x-blue)
![License](https://img.shields.io/badge/License-MIT-green)

---

## Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Features](#features)
4. [Project Structure](#project-structure)
5. [Installation](#installation)
6. [Running the Application](#running-the-application)
7. [Usage Examples](#usage-examples)
8. [API Reference](#api-reference)
9. [Testing](#testing)
10. [AWS Deployment](#aws-deployment)
11. [Design Decisions](#design-decisions)
12. [Technologies Used](#technologies-used)

---

## Overview

MicroSpring is a minimalist web framework that demonstrates the power of Java Reflection and the Inversion of Control (IoC) design pattern. It mimics some of the core functionality of Spring Framework, including:

- **Automatic component discovery** using custom annotations
- **REST endpoint mapping** with `@GetMapping`
- **Query parameter extraction** with `@RequestParam`
- **Built-in HTTP server** for serving both static and dynamic content

---

## Architecture

The framework follows a layered architecture:

```
┌─────────────────────────────────────────────────────────────┐
│                     HTTP Request                            │
└─────────────────────────────┬───────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     HttpServer                              │
│  - Listens on port 8080                                     │
│  - Parses HTTP requests                                     │
│  - Routes to static files or IoC context                    │
└─────────────────────────────┬───────────────────────────────┘
                              │
              ┌───────────────┴───────────────┐
              │                               │
              ▼                               ▼
┌─────────────────────────┐     ┌─────────────────────────────┐
│   Static File Handler   │     │   MicroSpringContext (IoC)  │
│  - HTML pages          │     │  - Component scanning        │
│  - PNG/SVG images      │     │  - Endpoint registration     │
│  - CSS/JS files        │     │  - Method invocation         │
└─────────────────────────┘     └─────────────────────────────┘
                                              │
                                              ▼
                              ┌─────────────────────────────────┐
                              │      REST Controllers           │
                              │  @RestController annotated      │
                              │  - HelloController              │
                              │  - GreetingController           │
                              │  - ApiController                │
                              └─────────────────────────────────┘
```

### Component Discovery Flow

```
┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐
│   Scan Package   │───▶│  Find Classes    │───▶│ Check Annotation │
│  (classpath)     │    │ (.class files)   │    │ (@RestController)│
└──────────────────┘    └──────────────────┘    └────────┬─────────┘
                                                         │
                        ┌────────────────────────────────┘
                        ▼
┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐
│ Create Instance  │───▶│  Scan Methods    │───▶│Register Endpoints│
│ (Reflection)     │    │ (@GetMapping)    │    │ (path -> handler)│
└──────────────────┘    └──────────────────┘    └──────────────────┘
```

---

## Features

### Core Features

| Feature | Description |
|---------|-------------|
| **@RestController** | Marks classes as REST controllers that will be automatically discovered |
| **@GetMapping** | Maps HTTP GET requests to handler methods |
| **@RequestParam** | Extracts query parameters from HTTP requests with support for default values |
| **Automatic Scanning** | Explores classpath to find and register all controller components |
| **Static File Serving** | Serves HTML, PNG, CSS, JS, and other static files |

### Additional Features

- **Command-line controller loading**: Load specific controllers via command line arguments
- **Graceful shutdown**: Proper shutdown hook for clean server termination
- **Content-type detection**: Automatic MIME type detection for responses
- **URL decoding**: Proper handling of encoded URL parameters
- **Error handling**: Custom error pages for 404 and 500 errors

---

## Project Structure

```
Taller-de-Arquitecturas-de-servidores/
├── pom.xml                              # Maven configuration
├── .gitignore                           # Git ignore rules
├── README.md                            # This documentation
└── src/
    ├── main/
    │   ├── java/
    │   │   └── co/edu/escuelaing/microspring/
    │   │       ├── MicroSpringBoot.java           # Main entry point
    │   │       ├── annotations/
    │   │       │   ├── GetMapping.java            # GET endpoint annotation
    │   │       │   ├── RequestParam.java          # Query parameter annotation
    │   │       │   └── RestController.java        # Controller annotation
    │   │       ├── controllers/
    │   │       │   ├── ApiController.java         # API endpoints
    │   │       │   ├── GreetingController.java    # Greeting with @RequestParam
    │   │       │   └── HelloController.java       # Hello world controller
    │   │       ├── ioc/
    │   │       │   └── MicroSpringContext.java    # IoC container
    │   │       ├── server/
    │   │       │   └── HttpServer.java            # HTTP server implementation
    │   │       └── util/
    │   │           └── LogoGenerator.java         # PNG logo generator
    │   └── resources/
    │       └── static/
    │           ├── index.html                     # Static HTML page
    │           └── images/
    │               └── logo.svg                   # SVG logo
    └── test/
        └── java/
            └── co/edu/escuelaing/microspring/
                ├── annotations/
                │   └── AnnotationsTest.java       # Annotation tests
                ├── controllers/
                │   └── ControllersTest.java       # Controller tests
                ├── ioc/
                │   └── MicroSpringContextTest.java # IoC tests
                └── server/
                    └── HttpServerTest.java        # Integration tests
```

---

## Installation

### Prerequisites

- **Java Development Kit (JDK) 17** or higher
- **Apache Maven 3.6** or higher
- **Git** (optional, for cloning)

### Clone the Repository

```bash
git clone https://github.com/[your-username]/Taller-de-Arquitecturas-de-servidores.git
cd Taller-de-Arquitecturas-de-servidores
```

### Build the Project

```bash
# Compile the project
mvn clean compile

# Package the project (creates JAR)
mvn clean package

# Run tests
mvn test
```

---

## Running the Application

### Method 1: Using Maven

```bash
mvn exec:java
```

### Method 2: Using Java directly

```bash
# First, compile the project
mvn clean compile

# Run with automatic component scanning
java -cp target/classes co.edu.escuelaing.microspring.MicroSpringBoot

# Or run with a specific controller
java -cp target/classes co.edu.escuelaing.microspring.MicroSpringBoot co.edu.escuelaing.microspring.controllers.HelloController
```

### Method 3: Using the packaged JAR

```bash
mvn clean package
java -jar target/microspring-1.0-SNAPSHOT.jar
```

### Server Output

When the server starts, you'll see:

```
Automatic mode: Scanning for @RestController components...

Scanning package: co.edu.escuelaing.microspring
Registered controller: co.edu.escuelaing.microspring.controllers.HelloController
  Mapped: GET / -> index()
  Mapped: GET /hello -> hello()
Registered controller: co.edu.escuelaing.microspring.controllers.GreetingController
  Mapped: GET /greeting -> greeting()
Registered controller: co.edu.escuelaing.microspring.controllers.ApiController
  Mapped: GET /api/status -> status()
  Mapped: GET /api/echo -> echo()
  Mapped: GET /api/calc -> calculate()
Scanning completed. Found 3 controller(s) with 6 endpoint(s).
========================================
   MicroSpring Server Starting...
========================================
Port: 8080
Static files: src/main/resources/static
Registered endpoints:
  GET /
  GET /hello
  GET /greeting
  GET /api/status
  GET /api/echo
  GET /api/calc
========================================
Server ready at http://localhost:8080
========================================
```

---

## Usage Examples

### Accessing Endpoints

Once the server is running, open your browser and navigate to:

| Endpoint | URL | Description |
|----------|-----|-------------|
| Welcome Page | http://localhost:8080/ | Dynamic welcome page with links |
| Hello | http://localhost:8080/hello | Simple hello message |
| Greeting (default) | http://localhost:8080/greeting | Greets "World" |
| Greeting (custom) | http://localhost:8080/greeting?name=Angel | Greets "Angel" |
| Server Status | http://localhost:8080/api/status | Server information |
| Echo | http://localhost:8080/api/echo?message=Hello | Echo service |
| Calculator | http://localhost:8080/api/calc?a=10&b=5&op=add | Calculator |
| Static HTML | http://localhost:8080/index.html | Static HTML page |

### Creating Your Own Controller

```java
package co.edu.escuelaing.microspring.controllers;

import co.edu.escuelaing.microspring.annotations.GetMapping;
import co.edu.escuelaing.microspring.annotations.RequestParam;
import co.edu.escuelaing.microspring.annotations.RestController;

@RestController
public class MyController {

    @GetMapping("/myendpoint")
    public String myEndpoint() {
        return "Hello from my custom endpoint!";
    }

    @GetMapping("/greet")
    public String greet(@RequestParam(value = "name", defaultValue = "Guest") String name) {
        return "Welcome, " + name + "!";
    }
}
```

---

## API Reference

### Annotations

#### @RestController

Marks a class as a REST controller component that will be automatically discovered and registered.

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestController {
    String value() default "";  // Optional base path
}
```

#### @GetMapping

Maps HTTP GET requests to a handler method.

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GetMapping {
    String value();  // The URI path to map
}
```

#### @RequestParam

Extracts query parameters from HTTP requests.

```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {
    String value();           // Parameter name
    String defaultValue() default "";  // Default value if not provided
}
```

### MicroSpringContext API

| Method | Description |
|--------|-------------|
| `scan(String basePackage)` | Scans a package for @RestController classes |
| `registerController(String className)` | Registers a specific controller by class name |
| `registerController(Class<?> clazz)` | Registers a specific controller class |
| `hasEndpoint(String path)` | Checks if an endpoint exists for the path |
| `invokeEndpoint(String path, Map<String, String> params)` | Invokes an endpoint handler |
| `getEndpoints()` | Returns all registered endpoints |
| `getBean(Class<T> clazz)` | Gets a registered bean instance |

---

## Testing

### Running All Tests

```bash
mvn test
```

### Test Coverage

The project includes comprehensive tests for:

1. **Annotation Tests** (`AnnotationsTest.java`)
   - Verifies correct retention policies (RUNTIME)
   - Verifies correct target types (TYPE, METHOD, PARAMETER)

2. **IoC Context Tests** (`MicroSpringContextTest.java`)
   - Component registration
   - Endpoint discovery
   - Method invocation with/without parameters
   - Default value handling
   - Error handling for non-existent endpoints

3. **Controller Tests** (`ControllersTest.java`)
   - Annotation presence verification
   - Method mapping verification
   - Parameter annotation verification

4. **Integration Tests** (`HttpServerTest.java`)
   - Full HTTP request/response cycle
   - Query parameter handling
   - Content-type verification
   - 404 error handling

### Test Results

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running co.edu.escuelaing.microspring.annotations.AnnotationsTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running co.edu.escuelaing.microspring.controllers.ControllersTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running co.edu.escuelaing.microspring.ioc.MicroSpringContextTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running co.edu.escuelaing.microspring.server.HttpServerTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] -------------------------------------------------------
[INFO] Results:
[INFO] Tests run: 28, Failures: 0, Errors: 0, Skipped: 0
[INFO] -------------------------------------------------------
```

---

## AWS Deployment


## Design Decisions

### Why Custom Annotations?

Using custom annotations (`@RestController`, `@GetMapping`, `@RequestParam`) provides:
- **Declarative programming**: Controllers are self-documenting
- **Separation of concerns**: Business logic is separate from framework logic
- **Scalability**: Easy to add new endpoints without modifying core code

### Why Reflection?

Java Reflection enables:
- **Runtime component discovery**: No need to manually register controllers
- **Dynamic method invocation**: Call methods based on URL paths
- **Parameter inspection**: Extract method parameter information at runtime

### Why Single-threaded Server?

The single-threaded approach:
- **Simplifies implementation**: No concurrency issues
- **Demonstrates core concepts**: Focus on IoC and reflection
- **Meets requirements**: Multiple non-concurrent requests are handled sequentially

### Why Maven?

Maven provides:
- **Standardized project structure**: Industry-standard organization
- **Dependency management**: Easy testing framework integration
- **Build lifecycle**: Compile, test, and package with simple commands

---

## Technologies Used

| Technology | Purpose |
|------------|---------|
| **Java 17** | Core programming language |
| **Maven** | Build and dependency management |
| **JUnit 5** | Unit and integration testing |
| **Java Reflection API** | Runtime class inspection and invocation |
| **Java Annotations** | Metadata for component discovery |
| **Java Networking** | HTTP server implementation |
| **Java NIO** | File reading for static content |


## Author

**Angel Cuervo**  
Escuela Colombiana de Ingeniería Julio Garavito

