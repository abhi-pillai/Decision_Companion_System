## 1. Technology and Environment Setup
To build the Decision Companion System, Stack Chosen:
- **Programming Language**: Java
- **Framework**: Spring Boot
- **Build Tool**: Gradle
- **Version Control**: Git
- **IDE**: Visual Studio Code
- **Frontend**: HTML, CSS, Vanilla JavaScript
I chose Java due to its robustness, scalability, and extensive libraries that can support the development of a complex system like the Decision Companion System. Spring Boot is a popular framework for building web applications and microservices, which will allow for rapid development and easy integration of various components. Gradle is a powerful build tool that can manage dependencies and automate the build process efficiently. Git will be used for version control to track changes and collaborate effectively. Visual Studio Code is a versatile IDE that supports Java development and offers various extensions to enhance productivity. For the frontend, I opted for plain HTML, CSS, and JavaScript to keep the user interface simple and lightweight, ensuring that the focus remains on the functionality of the decision-making process rather than on complex frontend frameworks. This technology stack provides a solid foundation for building a scalable and maintainable Decision Companion System.

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

## 4. Algorithm Selection
Initially, I implemented the Weighted Sum Model (WSM) as it is a straightforward and widely used method for multi-criteria decision making. But now I have also implemented the TOPSIS algorithm, which provides a more nuanced evaluation by considering the distance of each option from an ideal solution. This allows for a more comprehensive analysis of the options based on their performance across multiple criteria. The TOPSIS implementation normalizes the scores, calculates the ideal and anti-ideal solutions, and ranks the options based on their relative closeness to the ideal solution. This addition enhances the robustness of the Decision Companion System by offering users multiple methods for evaluating their decisions.

## 5. Architecture Refinement

The initial architecture included a Repository Layer for persistence. After further thinking, this was removed entirely. The system is stateless by design — each evaluation is computed on demand and returned immediately. There is no need to store results between sessions. Removing persistence keeps the system simpler, faster, and more explainable.

The architecture was refined to:

- **Presentation Layer** — REST Controller, no logic
- **Application Layer** — DecisionService, validates and orchestrates
- **Domain Layer** — Engines and Models, no Spring annotations
- **Infrastructure Layer** — AI Advisory (future only)

A key architectural principle was established: **`DecisionEngine` never knows AI exists.** The AI advisory module sits entirely outside the decision flow and can only advise the user before they submit input.


## 6. Transition from CLI to Web Application

The initial CLI runner (`Main.java`) was sufficient to test the WSM algorithm but could not support the full user flow required. The system needed to accept dynamic input from a real user, not hardcoded test data.

The transition involved:
- Migrating `build.gradle` from plain Java CLI to Spring Boot web application
- Removing the `application` plugin and `mainClass` entry
- Adding `spring-boot-starter-web` for REST, embedded Tomcat, and Jackson JSON handling
- Updating all domain models to support Jackson deserialization by adding no-arg constructors and setters
- Adding `category` field to `DecisionRequest` to support the final verdict message
- Creating `CombinedDecisionResponse.java` to bundle both engine results and verdict strings into one response object

Frontend technology chosen: **HTML + CSS + Vanilla JS** — no framework. The goal is a working decision tool, not a showcase of frontend complexity.

## 7. Scoring Design Decision
A plain 1–10 rating system:

> "Rate each metric from 1 to 10. Higher score = more of it. 1/10 means low, 9–10/10 means high."

For example: 9/10 on Price means high price. 9/10 on Battery means high battery life. The user decides whether high is good or bad for their own context. The system does not interpret or invert any metric.

This was chosen because it keeps the system truly generic, puts full interpretive authority with the user, and avoids the false objectivity of automated normalization. A UI instruction informs users about inverse metrics so they can score accordingly. This design also allows for future AI advisory to suggest scores without needing to know the semantics of each metric.

## 8. TopsisEngine Implementation

`TopsisEngine.java` was implemented with the full 5-step TOPSIS algorithm:

1. Build raw decision matrix from option scores
2. Normalize each column by its vector length: `xij / sqrt(Σxij²)`
3. Apply weights: `vij = wj × normalized`
4. Compute ideal solution A+ (max per column) and anti-ideal A- (min per column)
5. Compute closeness ratio: `Ci = d⁻ / (d⁺ + d⁻)` — higher means better

Results are sorted by closeness ratio descending. The explanation string attached to each result shows `d+`, `d-`, and `Ci` so the user can see exactly how each option was evaluated.

### Files Modified

| File | What Changed | Why |
|---|---|---|
| `build.gradle` | Removed `application` plugin and `mainClass`; added Spring Boot `3.2.5`, dependency management plugin, and `spring-boot-starter-web` | Migrated from plain Java CLI to Spring Boot web application |
| `Criterion.java` | Added no-arg constructor + `setName()`, `setWeight()` | Required for Jackson JSON deserialization |
| `Option.java` | Added no-arg constructor + `setName()`, `setScores()` | Required for Jackson JSON deserialization |
| `DecisionRequest.java` | Added no-arg constructor, all setters, and new `category` field | Required for Jackson deserialization; `category` needed for final verdict message |
| `DecisionResult.java` | Added no-arg constructor + all setters; removed `toString()` | Required for Jackson serialization; `toString()` no longer needed as Jackson handles JSON output |

## 9. Gradle Downgrade
Gradle 9.3.1 was found to be incompatible with Spring Boot 3.2.5 due to a configuration cache serialization error:
error writing value of type 'org.gradle.api.internal.artifacts.configurations.DefaultLegacyConfiguration'
Downgraded to Gradle 8.7 by updating gradle-wrapper.properties. Spring Boot ran successfully after the downgrade. This issue was documented in README under Known Issues.

### Files Modified

| File | Changes Made | Reason |
|------|--------------|--------|
| `gradle-wrapper.properties` | Downgraded Gradle version from **9.3.1** to **8.7** | Gradle 9.3.1 is incompatible with Spring Boot 3.2.5 |
| `WeightedSumEngine.java` | Added score rounding using `Math.round()` | Resolved floating-point precision issue (e.g., `5.300000000000001`) |
| `Main.java` | - Added `TopsisEngine` test execution <br> - Corrected weights to ensure sum equals **1.0** <br> - Added category parameter to constructor <br> - Improved output formatting | Enabled validation of both decision engines and fixed incorrect weight configuration from earlier testing |
| `.gitignore` | - Ignored Gradle build and cache directories <br> - Excluded VS Code workspace settings <br> - Excluded Windows system files <br> - Ignored local environment and Spring Boot override files <br> - Preserved Gradle wrapper for portability | Ensures clean version control by excluding generated, local, and system-specific files while maintaining project portability |

## 10. Backend Completion and Verification

The remaining backend files were completed and verified end-to-end:

- **`DecisionService.java`** — orchestrates the full flow: validates input, normalizes weights from percentage to decimal, runs both WSM and TOPSIS engines, and assembles the combined response with verdict strings
- **`DecisionController.java`** — REST endpoint at `POST /api/decision/evaluate` with `@CrossOrigin` to prevent browser CORS errors; returns 200 OK on success and 400 Bad Request on validation failure
- **`application.properties`** — minimal configuration: app name and port 8080
- **`DecisionCompanionApplication.java`** — Spring Boot entry point with `@SpringBootApplication`

Backend was verified using Postman with a laptop selection test case. Response confirmed correct WSM scores, TOPSIS closeness ratios between 0 and 1, correct verdict strings, and no null fields.

---
### New Files Created

| File | Location | Purpose |
|---|---|---|
| `DecisionService.java` | `service/` | Validation, normalization, orchestration |
| `DecisionController.java` | `controller/` | REST endpoint POST /api/decision/evaluate |
| `DecisionCompanionApplication.java` | `org/example/` | Spring Boot entry point |
| `application.properties` | `resources/` | App name and port configuration |

### 11. Frontend Development
The frontend was developed using plain HTML, CSS, and JavaScript to create a simple user interface. 
Built `index.html` — a single-page web app served by Spring Boot at `http://localhost:8080`. Design: modern dark theme with professional card-based layout using `Syne` and `DM Mono` fonts.

**Features implemented:**
- Step indicator showing progress through 4 stages
- Dynamic criteria input with live weight total (green at 100%, red otherwise)
- Auto-generated score fields per candidate based on criteria names entered
- Side-by-side WSM and TOPSIS ranking cards with animated score bars
- Click-to-expand breakdown explanation per candidate
- Final verdict banner showing both algorithm winners
- Error display for all validation failures
- Loading spinner during API call
- Reset button to start a new decision

**Bug 1 — Candidate data clearing:**
Adding a new candidate wiped all previously entered scores.
**Fix:**
- Value preservation in syncCriteriaToOptions: Before clearing the grid, all existing input values are snapshot into a savedValues object keyed by input ID. After the grid is rebuilt, those values are written back into the new inputs.
- addOption no longer calls syncCriteriaToOptions: Adding a new candidate now directly builds only that new card's score fields inline, so existing candidates are never touched at all. The full sync (which iterates every card) is only needed when criteria names change, not when a new candidate is added.

**404 Error — POST /api/decision/evaluate Not Found**

After fixing the candidate data clearing bug, clicking Evaluate Decision produced:
```
POST http://localhost:8080/api/decision/evaluate 404 (Not Found)
```
**Fix:**
Ran this command in terminal to restart Spring Boot with the latest backend changes:
```
./gradlew clean bootRun
```