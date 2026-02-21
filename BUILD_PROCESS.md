## 1. Technology and Environment Setup
To build the Decision Companion System, Stack Chosen:
- **Programming Language**: Java
- **Framework**: Spring Boot
- **Build Tool**: Gradle
- **Version Control**: Git
- **IDE**: Visual Studio Code
I chose Java due to its robustness, scalability, and extensive libraries that can support the development of a complex system like the Decision Companion System. Spring Boot is a popular framework for building web applications and microservices, which will allow for rapid development and easy integration of various components. Gradle is a powerful build tool that can manage dependencies and automate the build process efficiently. Git will be used for version control to track changes and collaborate effectively. Visual Studio Code is a versatile IDE that supports Java development and offers various extensions to enhance productivity.

## 2. Initial Architecture & Project Structure

I structured the application following a layered architecture:

- Controller Layer – Handles HTTP requests and responses
- Service Layer – Contains business logic
- Repository Layer – Manages data persistence
- Model/Entity Layer – Defines domain objects

This separation of concerns improves:
- Maintainability
- Testability
- Scalability

The project follows standard Spring Boot conventions to ensure clarity and alignment with industry practices
