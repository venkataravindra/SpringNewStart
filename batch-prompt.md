You are a senior enterprise integration architect with deep expertise in TIBCO BW 5.x/6.x and Spring Boot + Spring Batch (Java 17).
I am migrating a TIBCO BW batch process to Spring Batch.
scan the current working directory.
🎯 Your Objectives
1. First, deeply analyze the TIBCO BW process
• Do NOT jump into code immediately
• Clearly understand:
• Entry points
• Batch triggers
• File / XML processing
• Loops (Group / Iterate)
• Conditionals (Choice / Null / XPath conditions)
• Error handling paths
• Logging & audit steps
• End / success / failure flows

  MIGRATION ARTIFACTS PROVIDED:

  TIBCO Process Files:
  Path: C:\source-code\BatchDatafiles_hk\HKFinacleMigration_Batch_PBProfileList_HK_FW1_0_v1_0_1_root
  
  Main Orchestration: EnrichProcessOH.process
 
 Spring batch destination location: C:\TIBCO-SOI\Mar\02Mar\retrievePBProfileDailyList
  
2. Explain the TIBCO flow in plain English
• Step-by-step execution order
• Mention BW activities explicitly (Parse XML, Mapper, Group, Catch, Generate Error, etc.)
• Explain what happens on:
• Happy path
• Validation failure
• Business error
• Technical exception
3. Design the Spring Batch architecture
Map each BW concept to Spring Batch:
• BW Process → Spring Batch Job
• BW Group / Iterate → Step (chunk or tasklet)
• BW Mapper → ItemProcessor / Mapper class
• BW File Read → ItemReader
• BW DB / Service calls → ItemWriter / Service
• BW Choice / XPath → JobExecutionDecider
• BW Error / Catch → Skip / Retry / ExitStatus / Listeners
• BW Logging → Job / Step Listeners
• BW Global Variables → JobParameters / ExecutionContext
4. Clearly define Job Flow
• Job name
• Step sequence
• Conditional transitions
• Exit statuses
• Restartability behavior
5. Choose the right Step type
• Explain WHY a step is:
• Chunk-oriented OR
• Tasklet-based
• Justify commit size, retry, skip limits
6. Provide a clean package structure
com.company.batch
├── config
├── job
├── step
├── reader
├── processor
├── writer
├── listener
├── decider
├── domain
└── exception
7. Generate WORKING code
• Java 17
• Spring Boot 3.5.x
• Spring Batch 5.x
• Must compile with mvn clean install
• No deprecated APIs
• No pseudo-code
8. Mandatory Code Sections
• pom.xml
• Batch configuration class
• Job definition
• Step definitions
• Reader / Processor / Writer
• Decider (if needed)
• Listener implementations
• Exception handling strategy
• application.yml
9. Explain startup and runtime behavior
• What initializes at application startup
• What runs only when the job is triggered
• How multiple job executions behave
• How restart works if the job fails mid-way
10. Error Handling Strategy
• Business vs technical errors
• When to FAIL job
• When to SKIP record
• When to RETRY
• How errors are logged and audited
11. Final Verification
• Cross-check each Spring Batch component against the original BW activity
• Explicitly confirm:
“This Spring Batch flow is functionally equivalent to the given TIBCO BW batch.”
⸻
⚠️ Important Rules
• Do NOT rush
• Do NOT hallucinate
• Do NOT oversimplify
• Accuracy > verbosity
• Explain transitions clearly when switching between steps/classes
• Assume this will be reviewed by senior architects
⸻
📎 Input
• Present working directory PWD
🏁 Expected Outcome
A clear, maintainable, production-ready Spring Batch solution that mirrors the exact intent and flow of the original TIBCO BW batch.
 
 
  
  Follow this systematic approach:
 
  PHASE 1: EXHAUSTIVE TIBCO BW ANALYSIS 🔍
  - Find and examine EVERY TIBCO BW file (.process, .bwp, .sharedjdbc, .substvar, .projlib, XML)
  - Document EVERY activity type: JDBC, File, Mapper, Conditional, Loop, SetVariable, Group, Generate Error
  - Specifically look for enterprise patterns: Header/trailer processing, Multi-channel file generation, Shared variable state
  management, Database update patterns, Special file processing, Channel routing logic, Trailer count generation
 
  PHASE 2: COMPLETE FEATURE MATRIX 📊
  - Create 100% complete feature inventory BEFORE writing any code
  - Map every TIBCO BW component to Spring Batch equivalent
  - Verify you have identified 100% of: database operations, file operations, business logic, error scenarios
 
  PHASE 3: COMPLETE IMPLEMENTATION WITH MODERN PATTERNS ⚙️
  - Implement ALL identified features using MODERN SPRING BOOT STANDARDS:
    - JPA instead of JdbcTemplate - Use JPA entities, repositories, and @Query annotations
    - @Slf4j instead of Logger - Use Lombok @Slf4j annotation for all logging
    - Entity Management - Create JPA entities for all database tables with @Data, @NoArgsConstructor, @AllArgsConstructor
    - Repository Pattern - Implement Spring Data JPA repositories with custom @Query methods
    - Transactional Operations - Use @Transactional annotations for database operations
  - Include: Multi-channel file writers, Header/trailer processors, Database update services with JPA, Expiry processors, Channel
   routing, Shared state management, Complete error handling
  - Use Java 17 + Spring Boot 3.5.x + Spring Batch 5.x with JPA and Lombok dependencies
 
  PHASE 4: VALIDATION ✅
  - Verify 95%+ completeness with detailed assessment matrix
  - Ensure every TIBCO BW activity has Spring Batch equivalent
  - Validate all JPA entities and repositories work correctly
 
  Deliver complete working implementation with full TIBCO BW functional equivalence using modern Spring Boot patterns (JPA +
  @Slf4j).
