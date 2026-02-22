"Apologies for the delay in updating the research log for the First Week of the Decision Companion System project. Here are the key activities and findings from this week:
### *Day 1* (14th Feburary 2026)
 **Knowing the Problem**: I spent time understanding the problem statement and the requirements for the Decision Companion System. This involved 
 1. Asking Claude for a detailed explanation of the problem and what the system is expected to achieve.
    Here's the prompts I used:
    - What do it mean by Decision Companion System?
        It helped me understand the concept of a Decision Companion System, which is designed to assist users in making informed decisions by providing relevant information, analysis, and recommendations.
    - What can I use to build such a system?
        It provided insights on various tools and technologies that can be used to build a Decision Companion System, such as natural language processing (NLP) libraries and machine learning frameworks.
 2. Created a git repository and set up the initial project structure for the Decision Companion System. This included creating folders for documentation and research materials.
### *Day 2*
**Researching Existing Solutions**: I google searched for existing Decision Companion Systems and found a research paper titled "AI Veterinary Assistance: Enhancing Clinical Decision-Making in Animal Healthcare" by Y. -G. Jin et al. 
Here's the link to the paper: [AI Veterinary Assistance: Enhancing Clinical Decision-Making in Animal Healthcare](https://doi.org/10.1109/ACCESS.2025.3587787).
I prompted ChatGPT to find key informations for my project:
    - What are the key findings and insights from the paper "AI Veterinary Assistance:Enhancing Clinical Decision-Making in Animal Healthcare" by Y. -G. Jin et al.?
    It provided information about "The AVA Framework" AI Veterinary Assistance (AVA), a clinical decision-support system designed specifically for early-stage veterinary consultations. AVA combines:
    - A large language model (GPT-4o) for natural language understanding
    - A veterinarian-certified disease–symptom database for reliability and explainability
I understood that though it uses a large language model, it is not solely reliant on it. The system is designed to provide accurate and reliable information to assist veterinarians in making informed decisions during consultations. This insight give one of the many ways to build a Decision Companion System, which is to combine the power of large language models with domain-specific databases to enhance the reliability and explainability of the system.
### *Day 3*
Continuing on my research, I explored whether a decision companion system can be built without relying on a large language model or machine learning. I asked Claude the following question:
- Can a decision companion system be built without relying on a large language model or machine learning?
    It provided me with a solution and that is MCDM (Multi-Criteria Decision Making) which is a method used to evaluate and prioritize different options based on multiple criteria. This approach can be used to build a decision companion system that does not rely on large language models or machine learning. It uses pure mathematics to evaluate and prioritize options based on predefined criteria, making it a viable alternative for building a decision companion system without the need for complex AI models.
### *Day 4*
Continuing on my research, I asked ChatGPT the following prompt:
- Some examples multi criteria decision making algorithms
It provided me with several examples of multi-criteria decision-making algorithms which are AHP, TOPSIS, ELECTRE, PROMETHEE, MAUT, and VIKOR.
### *Day 5*
I spent time researching and understanding the AHP (Analytic Hierarchy Process) algorithm, which is one of the multi-criteria decision-making algorithms. I found a research paper titled "Supplier Selection In Automotive Industry Based On Analytical Hierarchy Process" by Muhammad Dawood Idrees,  Kashan Ahmed, UroosaAli, Arsalan Ansari, Abdul Sami. 
Here's the link to the paper: [Supplier Selection In Automotive Industry Based On Analytical Hierarchy Process](https://ijstr.org/final-print/may2021/Supplier-Selection-In-Automotive-Industry-Based-On-Analytical-Hierarchy-Process.pdf).
I prompted ChatGPT to find key informations for my project:
- What are the key findings and insights from the paper "Supplier Selection In Automotive Industry Based On Analytical Hierarchy Process" by Muhammad Dawood Idrees,  Kashan Ahmed, UroosaAli, Arsalan Ansari, Abdul Sami.?
It provided me with a solution in the form of MCDM (Multi-Criteria Decision Making), specifically the Analytic Hierarchy Process (AHP), which is used to evaluate and rank suppliers based on multiple criteria such as service, delivery time, price, and quality. This approach can be used to build a decision companion system that supports managerial decision-making without relying on large language models or machine learning. Instead, it applies structured pairwise comparisons and mathematical consistency checks to prioritize alternatives based on predefined criteria, making it a transparent and reliable method for developing a decision companion system without complex AI models.
### *Day 6*
I continued my research on the TOPSIS (Technique for Order Preference by Similarity to Ideal Solution) algorithm, ELECTRE (Elimination and Choice Expressing Reality) algorithm, PROMETHEE (Preference Ranking Organization Method for Enrichment Evaluations) algorithm, MAUT (Multi-Attribute Utility Theory) algorithm, and VIKOR (VlseKriterijumska Optimizacija I Kompromisno Resenje) algorithm. I prompted these algorithms and to find their definitions, applications, and examples of how they are used in decision-making processes.
Prompts I used:
- TOPSIS (Technique for Order Preference by Similarity to Ideal Solution) algorithm, ELECTRE (Elimination and Choice Expressing Reality) algorithm, PROMETHEE (Preference Ranking Organization Method for Enrichment Evaluations) algorithm, MAUT (Multi-Attribute Utility Theory) algorithm, and VIKOR (VlseKriterijumska Optimizacija I Kompromisno Resenje) algorithm. What is the definition, difference, examples of their usage.  
It provided me with a through explanation of each algorithm, their differences, and examples of their applications in various decision-making scenarios. This information is crucial for understanding the different approaches to multi-criteria decision-making and how they can be applied in the context of building a decision companion system. Each algorithm has its own strengths and weaknesses, and understanding these can help in selecting the most appropriate method for a given decision-making problem.
### *Day 7*
I attended the google meet session by Vonnue where they discussed the project requirements, expectations, and provided guidance on how to approach the assignment. This session was helpful in clarifying any doubts and ensuring that I am on the right track with my research and development of the Decision Companion System. I also had the opportunity to ask questions and receive feedback from the instructors, which will be valuable as I continue to work on the project in the coming weeks.
Google search command for initialize gradle project:
- How to initialize a gradle project?
    It provided me with the steps to initialize a Gradle project, which include:
    1. Installing Gradle on your system.
    2. Creating a new directory for your project and navigating into it.
    3. Running the command `gradle init` to set up the basic structure of the project.
    4. Choosing the type of project (e.g., Java application) and providing necessary details when prompted.
### *Day 8*
Started implementing the initial architecture and project structure for the Decision Companion System. I set up the Gradle build script to manage dependencies and automate the build process. This initial setup will serve as the foundation for developing the various components of the Decision Companion System.

Deleted App.java file and created a basic structure to test the logic of the project.
Domain Models
- Criterion.java — name + weight
- Option.java — name + map of criterion scores
- DecisionRequest.java — holds list of options + criteria
- DecisionResult.java — holds ranked options + scores + explanation

Decision Engine
- DecisionEngine.java — interface
- WeightedSumEngine.java — WSM logic (normalize → multiply → sum → rank)

CLI Runner
- Main.java — hardcoded sample input to test the engine end to end"
Prompts I used:
- create a basic structure which has the most basic features to test the WSM algorithm.
