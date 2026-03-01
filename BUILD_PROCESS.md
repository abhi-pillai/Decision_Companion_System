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


---

## 12. AI Advisory Module

With the core MCDM system stable and end-to-end verified, the AI Advisory Module was implemented. The goal was defined carefully before writing any code: Gemini advises but never decides. It explains the decision domain and suggests which metrics may carry more or less weight — but the user still sets all weights and scores manually.

**Key principle preserved:** `DecisionEngine` never knows AI exists. The advisory module sits entirely in the infrastructure layer and is invoked only before the user submits input.

**Three new files created:**

- `AiAdvisoryResponse.java` — domain model holding the structured Gemini response. Contains a nested `Metric` class with `name` and `hint` fields, plus `about` (one sentence on the category) and `examples` (list of candidate names).
- `AiAdvisoryService.java` — builds the Gemini prompt, calls `POST /v1beta/models/{model}:generateContent`, parses the JSON response. Includes a defensive markdown fence stripper in case Gemini returns backticks despite prompt instructions.
- `AiAdvisoryController.java` — REST endpoint `POST /api/advisory/suggest`. Accepts `{ "category": "..." }`, delegates to `AiAdvisoryService`, returns the structured response or a 500 with the error message.

**`application.properties` updated:**
```properties
gemini.api.key=YOUR_KEY_HERE
gemini.model=gemini-3-flash-preview
```

**Gemini prompt design:**

The prompt was written to return only valid JSON — no markdown, no explanation, no extra text:
```
You are a decision-making advisor. The user is deciding about: {category}.
Return a JSON object with exactly this structure:
{
  "about": "one sentence describing this decision domain",
  "metrics": [
    { "name": "metric name", "hint": "one sentence about why this metric may carry more or less weight" }
  ],
  "examples": ["example option 1", "example option 2", "example option 3"]
}
Return only valid JSON. No markdown, no code fences, no explanation, no extra text.
```

The `hint` field per metric was a deliberate design choice — a flat metric list gives no guidance. The hint explains relative importance depending on context without prescribing a weight, keeping the user in control.

---

## 13. Frontend — Three Changes in One Session

Three significant frontend changes were made after the AI Advisory Module was complete.

**Change 1 — AI Advisory Panel**

A "✦ Learn about Metrics" button was added to the category card. It is disabled until the user types a category. On click, the frontend calls `POST /api/advisory/suggest` and renders:
- An **about** section describing the decision domain
- **Metric chips** — each showing name, importance hint, and "+ Add to criteria" action
- **Example candidate chips** — click to add directly to the candidates list

Clicking a metric chip calls `addCriterionWithName()` and marks the chip as "✔ Added" to prevent duplicates. Clicking an example chip calls `addOptionWithName()` and marks similarly. The user still sets all weights and scores manually.

**Change 2 — Fixed Navbar with Section Navigation**

The step indicator was converted into a fixed navbar that stays visible while scrolling. Only one section is visible at a time — clicking a step hides all sections and shows the target one.

A `goToStep(step)` function controls all visibility and navbar state. Next buttons were added to each card: "Next: Define Criteria →", "Next: Add Candidates →", "Next: Evaluate →". After evaluation completes, `renderResults()` calls `goToStep(4)` automatically.

Two approaches were considered and rejected before arriving at this design:
- Scroll to section — rejected. Show/hide is cleaner for a step-by-step flow and prevents the user from seeing incomplete later sections.
- Navbar stays in place — rejected. Fixed position keeps progress always visible regardless of scroll depth.

**Change 3 — Per-Card Inline Validation**

Previously, all errors appeared in a single global error box shown only when Evaluate was clicked. This was replaced with per-card validation triggered by each Next button.

Each card has a `div.card-error` at the bottom, above the Next button. The `validateAndGo(step)` function replaces the plain `goToStep()` call on all Next buttons:

- Step 1 Next — validates category is not empty
- Step 2 Next — validates all criterion names filled, weights sum to exactly 100%
- Step 3 Next — validates at least 2 candidates, all names filled, all scores between 1 and 10

If validation fails, the error appears in that card and navigation is blocked. The Step 4 Evaluate button retains a server-side validation safety net with errors appearing in the Step 4 error box. Advisory errors from Gemini also now appear in the Step 1 card error box.

---

## 14. Added Light and Dark Mode Toggle
A light/dark mode toggle was added to the top right of the navbar. It uses a simple checkbox input styled as a toggle switch. When toggled, it adds or removes a `light-mode` class on the `body` element.
The CSS defines two themes:
- **Dark mode** (default): dark background, light text, blue accent colors
- **Light mode**: light background, dark text, orange accent colors
html:
```html
```
**Bug — h1 title invisible in light mode:**

The `header h1` gradient was `linear-gradient(135deg, #fff 0%, var(--accent) 100%)` — white text on a light background was invisible. Fixed by adding a `body.light header h1` override that changes the gradient start colour from `#fff` to `#1a1a2e` (dark navy), making the title clearly readable in both modes.


## 15. Unit Tests

With the full system stable, both deferred test files were implemented.

**`WeightedSumEngineTest.java` — 6 tests:**

| Test | What it verifies |
|---|---|
| `shouldReturnOneResultPerOption` | Engine returns exactly one result per input option |
| `shouldRankDellXpsFirst` | Dell XPS ranks first — verified against Postman result from Day 11 |
| `shouldReturnResultsSortedByScoreDescending` | Results are ordered rank 1 → rank 3 by score |
| `shouldCalculateCorrectScoreForDellXps` | Dell XPS score = exactly 7.5 (`0.4×9 + 0.3×6 + 0.3×7`) |
| `shouldAttachExplanationToEachResult` | Every result has a non-null, non-blank explanation string |
| `shouldProduceTiedScoresWhenAllOptionsAreIdentical` | Two identical options produce identical scores |

**`TopsisEngineTest.java` — 6 tests:**

| Test | What it verifies |
|---|---|
| `shouldReturnOneResultPerOption` | Engine returns exactly one result per input option |
| `shouldProduceClosenessRatiosBetweenZeroAndOne` | All ratios are in `[0, 1]` — mathematical requirement of TOPSIS |
| `shouldReturnResultsSortedByClosenessDescending` | Results ordered by closeness ratio descending |
| `shouldGiveClosenessOfOneToIdealOption` | All-10 option gets `C = 1.0` because `d⁺ = 0` |
| `shouldGiveClosenessOfZeroToWorstOption` | All-1 option gets `C = 0.0` because `d⁻ = 0` |
| `shouldAttachExplanationToEachResult` | Every result has a non-null, non-blank explanation string |

**Bug — constructor argument order:**

Tests initially used `new DecisionRequest(options, criteria)` — two arguments. Two compilation errors followed:

First: `DecisionRequest(List<Option>, List<Criterion>) is undefined` — category argument missing.

Second: `List<Option> cannot be converted to String` — constructor signature is `(String category, List<Option>, List<Criterion>)`, not `(List<Option>, List<Criterion>, String)`. Fixed by reordering to `new DecisionRequest("Laptop Selection", options, criteria)`.

Result: `BUILD SUCCESSFUL` — all 12 tests passed.

---

## 16. Main.java Removed

`Main.java` was deleted. It was a temporary CLI runner created on Day 8 to test the WSM engine before Spring Boot existed, extended on Day 10 to test both engines simultaneously.

With unit tests now covering engine correctness with proper assertions, `Main.java` served no further purpose. Keeping it would add noise to the project structure and mislead anyone reading the codebase.

### Files Deleted

| File | Reason |
|---|---|
| `Main.java` | Temporary CLI runner — superseded by unit tests and Spring Boot application |

### Files Created

| File | Location | Purpose |
|---|---|---|
| `WeightedSumEngineTest.java` | `test/java/org/example/` | 6 unit tests for WSM engine |
| `TopsisEngineTest.java` | `test/java/org/example/` | 6 unit tests for TOPSIS engine |

## 17. Deployment

The application is deployed on Render's free tier using Docker.

**Live URL:** https://decision-companion-system-3pdl.onrender.com

### Platform Selection

Render was chosen over Railway (credit limited), Fly.io (requires credit card), and Heroku (no free tier). Render's free tier supports Docker, integrates directly with GitHub, and supports environment variables for the Gemini API key.

### Dockerfile — Three Iterations

Render does not support Java as a native runtime on the free tier. Docker was used instead.

**Attempt 1** — Used `FROM gradle:8.7-jdk21` with `RUN gradle bootJar`. Failed — `gradle` system command not available in that image.

**Attempt 2** — Switched to `./gradlew` but `COPY app/ .` meant `gradlew` was missing — it lives at the project root, not inside `app/`.

**Attempt 3** — Used `COPY . .` then `cd app && ./gradlew bootJar`. Failed — `cd app` moved away from `gradlew` which is at root.

**Final Dockerfile:**
```dockerfile
# Stage 1 — Build the JAR
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /project
COPY . .
RUN chmod +x gradlew && ./gradlew -p app clean bootJar --no-daemon

# Stage 2 — Run the JAR
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /project/app/build/libs/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Key decisions:
- `./gradlew -p app` — runs `gradlew` from root but targets `app/` as the project directory
- `clean bootJar` — forces a fresh build, avoids stale Gradle cache inside the container
- Two-stage build — Stage 1 uses JDK to build, Stage 2 uses lightweight JRE to run. Keeps the final image small.

### Environment Variables

| Key | How set |
|---|---|
| `gemini.api.key` | Manually entered in Render dashboard — never committed to GitHub |
| `gemini.model` | Set to `gemini-3-flash-preview` in Render dashboard |

### Free Tier Behaviour

Render spins down free tier services after 15 minutes of inactivity — the next request after idle takes ~30 seconds to respond while the service wakes up. The 750 hours/month are shared across all services on the account.

### Files Created

| File | Location | Purpose |
|---|---|---|
| `Dockerfile` | project root | Two-stage Docker build for Render deployment |
| `render.yaml` | project root | Render service configuration |