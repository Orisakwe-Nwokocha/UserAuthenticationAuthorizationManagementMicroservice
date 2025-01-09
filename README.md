[![Build](https://github.com/Orisakwe-Nwokocha/kafka-tutorial/actions/workflows/build.yml/badge.svg)](https://github.com/Orisakwe-Nwokocha/kafka-tutorial/actions/workflows/build)

# Orisha User Authentication and Authorization Management System
## Overview
This microservice manages user authentication and authorization for your application. It provides endpoints for 
registering users, logging them in, generating and verifying JWT tokens, and ensuring role-based access control (RBAC) 
for all secured resources. The microservice uses Spring Security for authentication and access control.

## Run the App
### From the Command Line
```shell
./mvnw spring-boot:run
```

## Branches
Here are the available branches:

| Branch name | Description                                            |
|-------------|--------------------------------------------------------|
| `master`    | The main branch and publicly available branch for use. |
| `orisha`    | The unpublished end-result of the project.             |

## Features
- JWT Authentication: Users authenticate using their username and password, and are issued a JWT token.
- Role-Based Authorization: Endpoints are protected by roles like USER, ADMIN, and custom roles, restricting access based on user roles.
- Custom Error Handling: Custom error messages for unauthorized access and invalid input.
- Access Control: Secure endpoints that can only be accessed by authenticated users with appropriate roles.
- Logging: Uses SLF4J for logging and tracking authentication and authorization events.

## Technologies
- Spring Boot: For building the microservice.
- Spring Security: For handling authentication and authorization.
- JWT (JSON Web Tokens): For token-based user authentication.
- Spring Web: For creating RESTful APIs.
- Lombok: For reducing boilerplate code, such as getters, setters, and toString methods.
- PostgreSQL Database: For user data storage (can be replaced with other databases like MySql).
- JUnit/MockMvc: For unit and integration testing
- ModelMapper: For simplifying the mapping of objects between different models, such as DTOs and entities.
- Jakarta Validation API: For performing data validation in the application using annotations like @NotNull, @NotBlank, and custom validation rules.
