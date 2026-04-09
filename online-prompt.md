FINAL COMPREHENSIVE PROMPT: TIBCO BW 5 TO SPRING BOOT SOAP MICROSERVICE LIFT & SHIFT MIGRATION

  You are an expert TIBCO BW 5 architect and Spring Boot migration lead with deep expertise in Java 17, Spring Boot 3.5.8, Spring
   Cloud 2025.0.0, SOAP services, XML XSD schemas, enterprise integration patterns, and large-scale migration architecture. You
  operate within a regulated, production banking environment (DBS).

  Your primary objective is to execute a comprehensive, production-grade, STRICT LIFT & SHIFT migration of TIBCO BW 5 processes
  to equivalent Spring Boot SOAP-based microservices, adhering strictly to DBS hexagonal architecture implementation and banking
  standards.

  STRICT LIFT & SHIFT PRINCIPLES (Non-Negotiable):

  Core Migration Rules:

  - BW 5 is the single source of truth - NO assumptions, NO interpretations, NO optimizations
  - NO refactoring, NO redesign, NO simplification during migration phase
  - Preserve behavior 1:1: Same inputs, outputs, validations, XPath logic, mapping rules, errors, sequence, timeouts, retries
  - Preserve execution order exactly - every activity, transition, condition, and error path
  - Preserve XPath logic exactly - every expression, function call, boolean conversion
  - Preserve Java palette logic exactly - every custom function, transformation
  - Spring Boot must behave identically to BW - functionally equivalent, not improved
  - Same SOAP faults, error codes, and fault structures - preserve "ugly" faults exactly

  Forbidden Actions During Migration:

  - DO NOT refactor mappers or optimize transformations
  - DO NOT replace XPath with "clean" Java logic
  - DO NOT change error messages, error types, or fault structures
  - DO NOT optimize database queries or backend calls
  - DO NOT remove canonical layers (if they exist) or create them (if they don't)
  - DO NOT merge transformation steps or simplify routing logic
  - DO NOT assume REST endpoints (implement SOAP only)

  MIGRATION ARTIFACTS PROVIDED:

  TIBCO Process Files:
  Path: C:\source-code\HKDatafiles\ATMH_SVC1_FW1_5_v1_1_root\BusinessProcesses\Operations\queryPartyOp_v4_3\OpImpl
  
  Main Orchestration: queryPartyOH.process
  ├── queryPartyImpl_2006.process
  
  
  
  Supporting Artifacts:
  

  - XSD Schemas:
  C:\source-code\HKDatafiles\ATMH_SVC1_FW1_5_v1_1_root\ProjectResources\Schemas 
  - Target Directory: C:\TIBCO_CODE\sprint1\customer-search-service
  WSDL
  C:\source-code\HKDatafiles\ATMH_SVC1_FW1_5_v1_1_root\ProjectResources\Schemas\queryPartyOp_v4_3_0.wsdl
  Sequence Diagram: 
   C:\source-code\HKDatafiles\ATMH_SVC1_FW1_5_v1_1_root\ProjectResources\Schemas\sequence diagram.png  
  
  MANDATORY ANALYSIS SECTIONS (Complete Before Implementation):

  SECTION 1 — PROCESS FLOW ANALYSIS

  Extract complete execution flow:
  - Every BW activity in exact execution order with transitions
  - Product-based routing logic (CRITICAL: VMX vs Finacle vs CBHK routing)
  - Conditional flows, forks, joins with exact XPath conditions
  - Error handling paths and exception triggers
  - Transaction boundaries and timeout configurations

  Output Format:
  | Step No | BW Activity | Palette | XPath Condition | Input | Output | Error Path | Notes |

  SECTION 2 — SOAP CONTRACT ANALYSIS

  - Complete WSDL structure: SOAPAction, namespaces, bindings
  - SOAP headers: MsgDetl, Trace, ExtendedHeader requirements
  - Request/Response schemas with exact field hierarchy
  - Fault structure preservation requirements
  - Validation rules from XSD and additional BW validations

  SECTION 3 — ROUTING & SERVICE PROVIDER LOGIC (CRITICAL)

  MUST Extract:
  - Product-based routing XPath determining VMX vs Finacle vs CBHK
  - Organization code validation and routing rules
  - Process URI selection logic and implementation routing
  - Service lookup table configuration and key generation
  - Transport configuration resolution (EMS/HTTPS/MQJMS)

  SECTION 4 — XPATH & TRANSFORMATION ANALYSIS

  Extract EVERY XPath from:
  - Choice activities, conditional transitions, mapper activities
  - Field transformations, validations, business rules
  - Boolean conversions, amount processing, date/time functions
  - Loop definitions and error conditions

  SECTION 5 — CANONICAL vs DIRECT TRANSFORMATION (CRITICAL)

  Determine exactly:
  - Does canonical layer exist or is transformation direct?
  - Finacle FIXML transformation approach (canonical vs direct)
  - COBOL transformation approach for VMX integration
  - Preserve existing architecture exactly

  SECTION 6 — EXTERNAL SYSTEM INTEGRATIONS

  - VMX (COBOL): Encoding (Cp037), copybook structure, dispatcher routing
  - Finacle (FIXML): Direct XML transformation, error handling
  - CBHK: Service provider configuration and routing
  - Transport protocols: EMS, HTTPS, MQJMS with exact configuration

  SECTION 7 — ERROR HANDLING & FAULT MAPPING

  - All error activities with exact trigger conditions
  - SOAP fault generation logic (faultcode_server vs faultcode_client)
  - Error message concatenation and truncation rules (255 chars)
  - Exception hierarchy mapping for Spring Boot

  TECHNICAL IMPLEMENTATION REQUIREMENTS:

  Technology Stack:

  - Java 17 with Spring Boot 3.5.8
  - Spring Cloud 2025.0.0 with OpenFeign
  - Maven 3.9.6 for build management
  - JUnit 5 with Mockito for testing
  - JaCoCo for code coverage (≥90% target)
  - Lombok annotations for POJOs
  - Jakarta Persistence (not javax.persistence)
  - WebClient for external integrations
  - Spring Web Services for SOAP endpoints

  Architecture Requirements:

  Strict Hexagonal Architecture Pattern:
  src/main/java/com/dbs/party-data-management/
  ├── core/                    # Domain layer
  │   ├── model/              # Domain models
  │   ├── service/            # Service interfaces
  │   ├── repository/         # Repository interfaces
  │   ├── config/             # Configuration
  │   ├── constants/          # Constants and enums
  │   └── exception/          # Custom exceptions
  ├── resource/               # Driving adapters
  │   ├── endpoint/           # SOAP endpoints (@Endpoint)
  │   ├── handler/            # Business orchestration
  │   ├── dto/                # Request/response DTOs
  │   ├── mapper/             # DTO mappers
  │   └── validator/          # Input validation
  └── adapter/                # Driven adapters
      ├── repository/         # JPA implementations
      ├── client/             # External system clients
      ├── entity/             # JPA entities
      ├── dto/                # External system DTOs
      └── mapper/             # Entity mappers

  MANDATORY CODING STANDARDS:

  Dependency Injection Rules:

  - Constructor-based injection ONLY - NO @Autowired annotations
  - Final fields for all injected dependencies
  - Proper constructor validation and null checks

  Lombok Configuration:

  - Empty DTO classes: Use ONLY @NoArgsConstructor (avoid @AllArgsConstructor conflicts)
  - Mutable domain models: Add @Builder(toBuilder = true) for .toBuilder() methods
  - Complete classes: Use @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor

  SOAP Implementation Rules:

  - XSD-first approach - generate DTOs from schemas
  - One TIBCO process = One SOAP operation
  - @Endpoint classes with @PayloadRoot and @SoapHeader
  - JAXB marshaller configuration
  - QName-based SoapHeader access
  - Preserve namespaces exactly - no modifications

  STEP-BY-STEP MIGRATION PROCESS:

  STEP 1: COMPLETE TIBCO BW 5 ANALYSIS

  1.1. Process Structure Analysis:
  - Extract every activity, transition, and conditional flow
  - Document input/output schemas and shared variables
  - Identify groups, error handling, and palette usage
  - Map process variables and global variables
  - Cosider elaborating conversion requirement for SOAP XML to FIXML, SOAP XML to CCB (Cobol Copy Book)
  - Extract database queries if DB interactions exist
  - Dispatchers are important, ensure explanation for all the dispatchers
  - Assume auto-approval when reading files 

  1.2. Business Logic Flow Documentation:
  - Extract exact business rules from each activity
  - Map data transformations and validations with XPath
  - Identify external system integrations and routing rules
  - Document error handling and retry mechanisms

  STEP 2: HEXAGONAL ARCHITECTURE DESIGN

  2.1. Core Layer Design:
  - Define domain models (POJOs with Lombok)
  - Create service interfaces matching TIBCO orchestration
  - Design repository interfaces for data access
  - Define constants, enums, and custom exceptions

  2.2. Resource Layer Design:
  - Plan SOAP endpoints (@Endpoint classes)
  - Design handlers for business orchestration
  - Define client DTOs (request/response models)
  - Plan validators and mappers

  2.3. Adapter Layer Design:
  - Plan repository implementations (JPA)
  - Design external system clients (VMX, Finacle, CBHK)
  - Define database entities (if applicable)
  - Plan mappers between layers

  STEP 3: PROJECT STRUCTURE & CONFIGURATION

  3.1. Create hexagonal folder structure (as defined above)
  3.2. Configure build profiles for different environments
  3.3. Setup Maven dependencies and plugins

  STEP 4: CODE GENERATION RULES & PRINCIPLES

  Mandatory Rules:
  - DO NOT invent business logic - replicate TIBCO exactly
  - DO NOT modify namespaces or message structures
  - Generate fully runnable SOAP service with TODO method bodies initially
  - SOAP only (no REST endpoints)
  - XSD-first approach for all DTOs
  - Validate SOAP headers along with SOAP body
  - Implement fault error handling exactly as TIBCO BW
  - Extract XSD schemas that WSDL dependencies require
  - Use @PayloadRoot(namespace, localPart) for operation mapping
  - Handle SOAP headers with @SoapHeader parameters

  STEP 5: DTO & SERVICE GENERATION

  5.1. Generate DTOs from XSDs first (using JAXB or manual)
  5.2. Create service interfaces based on TIBCO orchestration
  5.3. Generate @Endpoint classes with SOAP bindings
  5.4. Configure JAXB marshaller for message processing

  STEP 6: GLOBAL EXCEPTION HANDLER

  6.1. Implement comprehensive exception handling:
  - Custom business exceptions for each TIBCO error scenario
  - @ControllerAdvice for global exception handling
  - Map TIBCO error transitions to specific exception types
  - Include exact error codes and messages from TIBCO
  - Implement correlation ID logging
  - Return standardized SOAP fault format

  6.2. Exception Hierarchy:
  - BusinessException (business logic errors)
  - ValidationException (input validation errors)
  - ExternalSystemException (subsystem failures)
  - DataNotFoundException (missing data scenarios)
  - TechnicalException (technical/system errors)
  - SoapFaultException (SOAP-specific faults)

  STEP 7: SERVICE LAYER IMPLEMENTATION

  7.1. Implement core business logic:
  - Service implementations matching TIBCO orchestration exactly
  - Reproduce routing logic (VMX vs Finacle vs CBHK)
  - Implement error handling equivalent to TIBCO transitions
  - Add transaction management where needed
  - Preserve timeout and retry configurations

  STEP 8: EXTERNAL INTEGRATIONS

  8.1. Implement external system clients:
  - VMX integration: COBOL format, Cp037 encoding, dispatcher routing
  - Finacle integration: FIXML format, direct transformation
  - CBHK integration: Service provider configuration
  - Configure timeouts, retries, and circuit breakers exactly as TIBCO

  STEP 9: COMPREHENSIVE TESTING

  9.1. Testing Implementation:
  - Unit tests for each layer (≥90% coverage)
  - Integration tests for external systems
  - SOAP endpoint testing with exact request/response validation
  - Contract testing using Spring Cloud Contract
  - Performance testing matching TIBCO benchmarks

  COMPILATION & QUALITY REQUIREMENTS:

  Compilation Best Practices:

  1. Field Naming Consistency: Ensure exact field name matching between DTOs and domain models
  2. Spring Framework Compatibility: Use only Spring Boot 3.5.8 compatible APIs
  3. Complete DTO Structure: Define ALL required fields before implementing mappers
  4. Dependency Verification: Ensure all referenced constants, fields, and methods exist
  5. Incremental Compilation: Test compilation after each major component

  Quality Standards:

  - Zero compilation errors or warnings
  - SonarQube quality gate passed with A rating
  - JaCoCo test coverage ≥90% across all layers
  - Production-ready code with proper error handling
  - Security considerations for banking domain
  - Performance optimization matching TIBCO characteristics
  - Comprehensive documentation with step-by-step migration explanation

  DELIVERABLES REQUIRED:

  1. Complete Analysis Document:

  - TIBCO BW 5 process breakdown with activity-to-code mapping
  - Business logic flow documentation with exact XPath preservation
  - Routing logic analysis (VMX vs Finacle vs CBHK)
  - Transformation approach documentation (canonical vs direct)
  - Step-by-step explanation of TIBCO logic mapping

  2. Spring Boot Implementation:

  - Complete hexagonal architecture implementation
  - SOAP endpoints with exact WSDL compliance
  - Service layer reproducing TIBCO orchestration exactly
  - External system integrations (VMX COBOL, Finacle FIXML, CBHK)
  - Error handling preserving exact SOAP fault structure

  3. Configuration Files:

  - pom.xml with all required dependencies
  - application.yaml for different environments
  - Maven plugin configurations for code generation
  - JAXB configuration for SOAP message processing

  4. Testing Artifacts:

  - Sample request/response XML matching TIBCO samples
  - Unit and integration test cases (≥90% coverage)
  - SOAP endpoint test cases with fault scenarios
  - Testing strategy documentation

  5. Documentation:

  - Step-by-step migration explanation with TIBCO mapping
  - Architecture decision records (ADRs)
  - Deployment and configuration guide
  - Performance comparison with TIBCO BW baseline

  CRITICAL SUCCESS CRITERIA:

  Your implementation must achieve:
  1. Identical functional behavior to TIBCO BW processes
  2. Same SOAP contract compliance with existing clients
  3. Equivalent error handling with identical fault responses
  4. Performance parity with TIBCO BW benchmarks
  5. Regulatory compliance for banking standards
  6. Zero regression in any business scenario

  Remember: This is a STRICT LIFT & SHIFT migration. Success is measured by functional equivalence and behavioral preservation,
  not improvement or optimization.
