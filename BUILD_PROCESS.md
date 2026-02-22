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
## 3. Domain Models & Decision Engine
First, I defined the core domain models:
- **Criterion**: Represents a decision criterion with a name and weight.
- **Option**: Represents a decision option with a name and a map of criterion scores.
- **DecisionRequest**: Encapsulates a list of options and criteria for evaluation.
- **DecisionResult**: Represents the outcome of the decision evaluation, including ranked options, scores, and explanations.
Next, I created the `DecisionEngine` interface, which defines the contract for evaluating a `DecisionRequest` and returning a list of `DecisionResult`. This allows for flexibility in implementing different decision-making algorithms in the future.
I implemented the `WeightedSumEngine` class, which provides a concrete implementation of the `DecisionEngine` interface using the Weighted Sum Method (WSM). This engine normalizes the criterion weights, multiplies them by the corresponding scores for each option, sums the results, and ranks the options accordingly. This approach allows for a straightforward evaluation of options based on multiple criteria and their respective weights.
The basic structure of the `Main` class serves as a CLI runner to test the logic of the decision engine end-to-end. It sets up a sample decision scenario with hardcoded options and criteria, runs the evaluation through the `WeightedSumEngine`, and prints the ranked results along with a recommendation for the best option. This setup allows for easy testing and validation of the decision-making logic before integrating it into a web application or other interfaces.