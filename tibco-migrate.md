in commands DIR :tibco-migrate.md

# /tibco-migrate Command

## Command Definition

**Name**: `/tibco-migrate`

**Purpose**: Migrate TIBCO BusinessWorks applications to Spring Boot microservices through an automated 5-agent pipeline.

**Syntax**: `/tibco-migrate <path/to/tibco/project>`

## Description

The `/tibco-migrate` command initiates a comprehensive migration process that transforms TIBCO BusinessWorks artifacts into production-ready Spring Boot applications. The command invokes the **Orchestrator Agent** (`.claude/agents/orchestrator.md`) which coordinates five specialized phase agents that work sequentially to analyze, design, code, compile, and test the migrated application.

## Usage

### Basic Usage

```bash
/tibco-migrate path/to/tibco/project
```

### Example

```bash
/tibco-migrate tibco_projects/order_processing
```

## Prerequisites

Before running the command, ensure:

1. **TIBCO Project Structure**: The target directory contains:
   - `*.process` files (TIBCO BusinessWorks processes)
   - `*.prj` files (TIBCO project descriptors)
   - Optional: `TIBCO.md` (project documentation)

2. **Environment Setup**:
   - Java 17 or higher installed
   - Maven 3.9+ or Gradle 7.5+ installed
   - Sufficient disk space for generated code

3. **Working Directory**: Execute from repository root containing `.claude/` directory

## Execution Flow

When you invoke `/tibco-migrate`, the command immediately hands control to the **Orchestrator Agent** (`.claude/agents/orchestrator.md`), which then executes the following sequence:

### Phase 1: Initialization (Orchestrator)

1. **Validate Input**:
   - Verify TIBCO project path exists
   - Check for required TIBCO artifacts
   - Validate project structure

2. **Create Migration Workspace**:
   ```
   <project>/.tibco_migrate/
   ├── analysis.md (pending)
   ├── design.md (pending)
   ├── compilation-notes.md (pending)
   ├── unit-test-summary.md (pending)
   └── migration-session.json
   ```

3. **Initialize Session State**:
   - Record start time
   - Set current agent to "analysis"
   - Initialize agent status tracking

### Phase 2: Phase Agent Execution (Orchestrated by Orchestrator)

The Orchestrator Agent sequentially invokes each phase agent:

#### Agent 1: Analysis Agent (5-10 minutes)

**Input**: TIBCO project artifacts

**Actions**:
- Analyze TIBCO processes, adapters, and configurations
- Extract business intent and logic
- Document message transformations
- Map integration patterns
- Identify external dependencies
- Create execution sequence diagrams

**Output**: `.tibco_migrate/analysis.md`

**User Notification**:
```
✅ Analysis Agent completed (8 minutes)
   Output: .tibco_migrate/analysis.md
   Components analyzed: 5 processes, 3 adapters, 2 queues
```

---

#### Agent 2: Design Agent (5-8 minutes)

**Input**: `.tibco_migrate/analysis.md`

**Actions**:
- Design microservice boundaries
- Define REST API endpoints
- Design messaging interfaces
- Create TIBCO → Spring Boot component mapping
- Design error handling strategy
- Plan configuration management
- Generate architecture diagrams

**Output**: `.tibco_migrate/design.md`

**User Notification**:
```
✅ Design Agent completed (6 minutes)
   Output: .tibco_migrate/design.md
   Services designed: 2 microservices, 8 REST endpoints, 4 message listeners
```

---

#### Agent 3: Coding Agent (10-15 minutes)

**Input**: `.tibco_migrate/analysis.md`, `.tibco_migrate/design.md`

**Actions**:
- Generate Spring Boot project structure
- Implement Controllers, Services, Repositories
- Implement DTOs and domain models
- Implement messaging components
- Create configuration files
- Generate build configuration (Maven/Gradle)
- Create comprehensive README

**Output**: `<project>/springboot-app/` (complete Spring Boot project)

**User Notification**:
```
✅ Coding Agent completed (12 minutes)
   Output: springboot-app/
   Files generated: 45 Java files, 3 config files, 1 pom.xml
```

---

#### Agent 4: Compilation Agent (3-5 minutes)

**Input**: `<project>/springboot-app/`

**Actions**:
- Compile Spring Boot application
- Identify and fix compilation errors
- Resolve dependency conflicts
- Fix configuration issues
- Verify application startup
- Document all fixes applied

**Output**: `.tibco_migrate/compilation-notes.md`, Updated source code (if needed)

**User Notification**:
```
✅ Compilation Agent completed (4 minutes)
   Output: .tibco_migrate/compilation-notes.md
   Build status: SUCCESS
   Issues fixed: 3 (dependency conflicts, type mismatches)
```

---

#### Agent 5: Unit Testing Agent (8-12 minutes)

**Input**: `<project>/springboot-app/` (compiled application)

**Actions**:
- Generate unit tests for Controllers
- Generate unit tests for Services
- Generate integration tests
- Run all tests
- Generate coverage report
- Document test summary

**Output**: Test files in `springboot-app/src/test/java/`, `.tibco_migrate/unit-test-summary.md`

**User Notification**:
```
✅ Unit Testing Agent completed (10 minutes)
   Output: .tibco_migrate/unit-test-summary.md
   Tests generated: 45 tests
   Test results: 45 passed, 0 failed
   Coverage: 87%
```

---

### Phase 3: Final Report (Generated by Orchestrator)

After all phase agents complete successfully, the Orchestrator Agent generates:

**File**: `.tibco_migrate/migration-summary.md`

**Contents**:
- Project information
- Agent execution summary
- Migration artifacts locations
- Next steps for deployment
- Manual intervention items (if any)

**User Notification**:
```
✅ Migration completed successfully!

📁 Artifacts generated:
   - Analysis: .tibco_migrate/analysis.md
   - Design: .tibco_migrate/design.md
   - Spring Boot App: springboot-app/
   - Tests: springboot-app/src/test/java/
   
🚀 Next steps:
   1. cd springboot-app
   2. mvn spring-boot:run
   3. mvn test
   
📖 Review migration-summary.md for details
```

## Output Structure

After successful migration:

```
tibco_project/
├── [original TIBCO files]
├── springboot-app/                    # Generated Spring Boot project
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   │       └── java/
│   ├── pom.xml
│   └── README.md
└── .tibco_migrate/                    # Migration artifacts
    ├── analysis.md
    ├── design.md
    ├── compilation-notes.md
    ├── unit-test-summary.md
    ├── migration-session.json
    └── migration-summary.md
```

## Error Handling

### Scenario 1: TIBCO Artifacts Not Found

**Error**:
```
❌ Error: No TIBCO artifacts found in specified path
   Path: /path/to/project
   Expected: *.process or *.prj files
```

**Resolution**: Verify the path contains TIBCO BusinessWorks files

---

### Scenario 2: Agent Failure

**Error**:
```
❌ Analysis Agent failed
   Error: Could not parse TIBCO process XML
   File: OrderProcessing.process
```

**Resolution**: 
- Check TIBCO file format
- Ensure valid XML structure
- Review error details in migration-session.json

---

### Scenario 3: Compilation Failure

**Error**:
```
❌ Compilation Agent failed
   Error: Maven build failed with errors
   Details: See .tibco_migrate/compilation-notes.md
```

**Resolution**:
- Review compilation-notes.md for specific errors
- May require manual intervention
- Compilation Agent attempts auto-fix for common issues

---

### Scenario 4: Test Failures

**Warning** (not fatal):
```
⚠️  Unit Testing Agent completed with warnings
   Tests: 40 passed, 5 failed
   Coverage: 75% (below target of 80%)
   Details: See .tibco_migrate/unit-test-summary.md
```

**Resolution**:
- Review test failures
- May indicate business logic issues
- Migration can proceed, but review recommended

## Command Options

### Verbose Mode (Future Enhancement)

```bash
/tibco-migrate path/to/project --verbose
```

Shows detailed progress for each agent step.

### Dry Run Mode (Future Enhancement)

```bash
/tibco-migrate path/to/project --dry-run
```

Validates prerequisites without executing migration.

### Resume Mode (Future Enhancement)

```bash
/tibco-migrate path/to/project --resume
```

Resumes from last successful agent if previous run failed.

## Time Estimates

| Project Size | Total Time | Breakdown |
|--------------|-----------|-----------|
| Small (1-2 processes) | 30-40 min | Analysis: 5min, Design: 5min, Coding: 10min, Compile: 3min, Test: 8min |
| Medium (3-5 processes) | 45-60 min | Analysis: 8min, Design: 6min, Coding: 12min, Compile: 4min, Test: 10min |
| Large (6+ processes) | 60-90 min | Analysis: 10min, Design: 8min, Coding: 15min, Compile: 5min, Test: 12min |

## Success Criteria

Migration is successful when:
- ✅ All 5 agents complete without errors
- ✅ Spring Boot application compiles
- ✅ Application starts successfully
- ✅ All unit tests pass
- ✅ Test coverage ≥ 80%
- ✅ All artifacts are generated
- ✅ migration-summary.md is complete

## Post-Migration Steps

After successful migration:

1. **Review Generated Code**:
   ```bash
   cd springboot-app
   cat README.md  # Review TIBCO → Spring Boot mapping
   ```

2. **Run Application**:
   ```bash
   mvn spring-boot:run
   ```

3. **Run Tests**:
   ```bash
   mvn test
   ```

4. **Review Documentation**:
   - Read `.tibco_migrate/analysis.md` for business logic
   - Read `.tibco_migrate/design.md` for architecture
   - Read `springboot-app/README.md` for component mapping

5. **Configure Environment**:
   - Set database credentials
   - Configure message broker
   - Update application.yml for your environment

6. **Deploy**:
   - Build Docker image
   - Deploy to Kubernetes/Cloud platform
   - Configure monitoring and logging

## Troubleshooting

### Command Not Recognized

**Issue**: `/tibco-migrate` command not found

**Solution**:
- Ensure `.claude/commands/tibco-migrate.md` exists
- Verify you're in repository root
- Restart Claude Code CLI session

### Migration Hangs

**Issue**: Agent appears stuck

**Solution**:
- Check migration-session.json for current agent
- Review agent logs for errors
- May need to cancel and retry

### Incomplete Migration

**Issue**: Some agents didn't execute

**Solution**:
- Check migration-session.json for agent status
- Review error messages
- Use resume mode (when available)

## Related Documentation

- **System Overview**: [`CLAUDE.md`](../CLAUDE.md)
- **Orchestrator**: [`.claude/agents/orchestrator.md`](../agents/orchestrator.md)
- **Analysis Agent**: [`.claude/agents/analysis_agent.md`](../agents/analysis_agent.md)
- **Design Agent**: [`.claude/agents/design_agent.md`](../agents/design_agent.md)
- **Coding Agent**: [`.claude/agents/coding_agent.md`](../agents/coding_agent.md)
- **Compilation Agent**: [`.claude/agents/compilation_agent.md`](../agents/compilation_agent.md)
- **Unit Testing Agent**: [`.claude/agents/unit_testing_agent.md`](../agents/unit_testing_agent.md)

## Version

**Command Version**: 1.1
**Compatible with**: TIBCO Migration System v1.1
**Last Updated**: February 2026
