---
name: phase4-testing-validation
description: Generates comprehensive test suite and validates Spring Boot implementation
tools: Read, Write, Bash
---

# Phase 4 Agent: Testing & Validation

## Role

You are the Phase 4 agent responsible for generating comprehensive test suites and validating the Spring Boot implementation against TIBCO functionality. Your role is to ensure functional equivalency and production readiness.

## Scope

**Input**: Complete Spring Boot implementation from Phase 3
**Output**: Comprehensive test suite with validation reports
**Duration**: 8-15 minutes for typical projects

## Execution Protocol

### Step 1: Load Implementation Artifacts

**Required Inputs**:
1. `implementation/spring_boot_project/` - Complete Spring Boot project
2. `implementation/migration_log.md` - Implementation documentation
3. `analysis/business_processes.yaml` - Original TIBCO process definitions
4. `design/component_mapping.yaml` - TIBCO to Spring mappings

**Validation**:
```bash
# Verify implementation exists and builds
cd .tibco_migration/implementation/spring_boot_project
mvn clean compile -q
if [ $? -ne 0 ]; then
  echo "ERROR: Spring Boot project does not compile. Fix implementation first."
  exit 1
fi
```

### Step 2: Test Strategy Planning

**Define Test Categories**:

1. **Unit Tests**: Individual component testing
2. **Integration Tests**: Layer integration testing
3. **Contract Tests**: SOAP endpoint validation
4. **Functional Tests**: End-to-end business process validation
5. **Performance Tests**: Load and response time testing

**Generate Test Plan** (`testing/test_strategy.yaml`):

```yaml
test_strategy:
  objectives:
    - "Validate functional equivalency with TIBCO"
    - "Ensure SOAP contract compliance"
    - "Verify external system integrations"
    - "Validate error handling and resilience"

  test_categories:
    unit_tests:
      target_coverage: 90
      frameworks: ["JUnit 5", "Mockito"]
      focus_areas:
        - "Domain model validation"
        - "Service layer business logic"
        - "DTO mapping and validation"

    integration_tests:
      frameworks: ["Spring Boot Test", "TestContainers"]
      focus_areas:
        - "Database integration"
        - "External service integration"
        - "SOAP endpoint integration"

    contract_tests:
      frameworks: ["Spring WS Test"]
      focus_areas:
        - "SOAP request/response validation"
        - "Schema compliance"
        - "Error response formats"

  test_data:
    scenarios:
      - "Valid customer creation"
      - "Customer update flow"
      - "Invalid data handling"
      - "External system failures"
      - "Concurrent processing"
```

### Step 3: Unit Test Generation

**Generate Domain Model Tests**:

```java
// src/test/java/com/dbs/customer/core/model/CustomerTest.java
package com.dbs.customer.core.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

/**
 * Unit tests for Customer domain model
 * Validates business logic migrated from TIBCO
 */
class CustomerTest {

    @Test
    @DisplayName("Should validate customer as active when status is ACTIVE")
    void shouldValidateActiveCustomer() {
        // Given
        Customer customer = Customer.builder()
                .customerId("CUST1234")
                .customerName("John Doe")
                .status(CustomerStatus.ACTIVE)
                .build();

        // When
        boolean isActive = customer.isActive();

        // Then
        assertTrue(isActive, "Customer should be active");
    }

    @Test
    @DisplayName("Should validate minimum balance correctly")
    void shouldValidateMinimumBalance() {
        // Given
        BigDecimal accountBalance = new BigDecimal("1000.00");
        BigDecimal minimumAmount = new BigDecimal("500.00");

        Customer customer = Customer.builder()
                .customerId("CUST1234")
                .accountBalance(accountBalance)
                .build();

        // When
        boolean hasMinimum = customer.hasMinimumBalance(minimumAmount);

        // Then
        assertTrue(hasMinimum, "Customer should have minimum balance");
    }

    @Test
    @DisplayName("Should fail validation when balance below minimum")
    void shouldFailValidationWhenBalanceBelowMinimum() {
        // Given
        BigDecimal accountBalance = new BigDecimal("100.00");
        BigDecimal minimumAmount = new BigDecimal("500.00");

        Customer customer = Customer.builder()
                .customerId("CUST1234")
                .accountBalance(accountBalance)
                .build();

        // When
        boolean hasMinimum = customer.hasMinimumBalance(minimumAmount);

        // Then
        assertFalse(hasMinimum, "Customer should not have minimum balance");
    }
}
```

**Generate Service Layer Tests**:

```java
// src/test/java/com/dbs/customer/core/service/CustomerServiceTest.java
package com.dbs.customer.core.service;

import com.dbs.customer.core.service.impl.CustomerServiceImpl;
import com.dbs.customer.core.repository.CustomerRepository;
import com.dbs.customer.core.model.Customer;
import com.dbs.customer.core.exception.CustomerValidationException;
import com.dbs.customer.adapter.client.FinacleClient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Unit tests for CustomerService
 * Validates business logic migrated from TIBCO processes
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private FinacleClient finacleClient;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer validCustomer;

    @BeforeEach
    void setUp() {
        validCustomer = Customer.builder()
                .customerId("CUST1234")
                .customerName("John Doe")
                .email("john.doe@example.com")
                .accountBalance(new BigDecimal("1000.00"))
                .status(CustomerStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should process new customer successfully")
    void shouldProcessNewCustomerSuccessfully() {
        // Given
        when(customerRepository.existsById("CUST1234")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(validCustomer);

        // When
        Customer result = customerService.processCustomerRequest(validCustomer);

        // Then
        assertNotNull(result);
        assertEquals("CUST1234", result.getCustomerId());
        verify(customerRepository).existsById("CUST1234");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should update existing customer successfully")
    void shouldUpdateExistingCustomerSuccessfully() {
        // Given
        Customer existingCustomer = Customer.builder()
                .customerId("CUST1234")
                .customerName("Jane Doe")
                .build();

        when(customerRepository.existsById("CUST1234")).thenReturn(true);
        when(customerRepository.findById("CUST1234")).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(validCustomer);

        // When
        Customer result = customerService.processCustomerRequest(validCustomer);

        // Then
        assertNotNull(result);
        assertEquals("CUST1234", result.getCustomerId());
        verify(customerRepository).existsById("CUST1234");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw validation exception for invalid customer")
    void shouldThrowValidationExceptionForInvalidCustomer() {
        // Given
        Customer invalidCustomer = Customer.builder()
                .customerId("")  // Invalid empty ID
                .customerName("John Doe")
                .build();

        // When & Then
        assertThrows(CustomerValidationException.class, () -> {
            customerService.validateCustomer(invalidCustomer);
        }, "Should throw validation exception for empty customer ID");
    }

    @Test
    @DisplayName("Should validate customer with negative balance")
    void shouldValidateCustomerWithNegativeBalance() {
        // Given
        Customer customerWithNegativeBalance = Customer.builder()
                .customerId("CUST1234")
                .customerName("John Doe")
                .accountBalance(new BigDecimal("-100.00"))  // Negative balance
                .build();

        // When & Then
        assertThrows(CustomerValidationException.class, () -> {
            customerService.validateCustomer(customerWithNegativeBalance);
        }, "Should throw validation exception for negative balance");
    }
}
```

### Step 4: Integration Test Generation

**Generate SOAP Endpoint Tests**:

```java
// src/test/java/com/dbs/customer/resource/controller/CustomerControllerIntegrationTest.java
package com.dbs.customer.resource.controller;

import com.dbs.customer.CustomerServiceApplication;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.ws.test.client.MockWebServiceServer;
import org.springframework.ws.test.client.RequestMatchers;
import org.springframework.ws.test.client.ResponseCreators;

import static org.springframework.ws.test.client.RequestMatchers.*;
import static org.springframework.ws.test.client.ResponseCreators.*;

/**
 * Integration tests for Customer SOAP endpoint
 * Validates SOAP contract compliance migrated from TIBCO
 */
@SpringBootTest(classes = CustomerServiceApplication.class)
@TestPropertySource(locations = "classpath:test.properties")
class CustomerControllerIntegrationTest {

    @Test
    @DisplayName("Should process valid customer SOAP request")
    void shouldProcessValidCustomerSoapRequest() {
        // Test implementation with SOAP request/response validation
        // Validates exact compliance with TIBCO SOAP contract
    }

    @Test
    @DisplayName("Should return SOAP fault for invalid request")
    void shouldReturnSoapFaultForInvalidRequest() {
        // Test error handling and SOAP fault generation
        // Validates error response format matches TIBCO behavior
    }

    @Test
    @DisplayName("Should handle concurrent SOAP requests")
    void shouldHandleConcurrentSoapRequests() {
        // Test concurrent processing capability
        // Validates thread safety and performance
    }
}
```

**Generate Database Integration Tests**:

```java
// src/test/java/com/dbs/customer/adapter/repository/CustomerRepositoryIntegrationTest.java
package com.dbs.customer.adapter.repository;

import com.dbs.customer.CustomerServiceApplication;
import com.dbs.customer.core.model.Customer;
import com.dbs.customer.core.repository.CustomerRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.testcontainers.containers.Db2Container;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Integration tests for Customer repository
 * Validates database operations work correctly
 */
@SpringBootTest(classes = CustomerServiceApplication.class)
@Testcontainers
@Transactional
class CustomerRepositoryIntegrationTest {

    @Container
    static Db2Container db2Container = new Db2Container("ibmcom/db2:11.5.4.0")
            .withDatabaseName("testdb")
            .withUsername("db2inst1")
            .withPassword("password");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", db2Container::getJdbcUrl);
        registry.add("spring.datasource.username", db2Container::getUsername);
        registry.add("spring.datasource.password", db2Container::getPassword);
    }

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("Should save and retrieve customer successfully")
    void shouldSaveAndRetrieveCustomerSuccessfully() {
        // Given
        Customer customer = Customer.builder()
                .customerId("TESTCUST")
                .customerName("Test Customer")
                .email("test@example.com")
                .accountBalance(new BigDecimal("1500.00"))
                .status(CustomerStatus.ACTIVE)
                .build();

        // When
        Customer savedCustomer = customerRepository.save(customer);
        Optional<Customer> retrievedCustomer = customerRepository.findById("TESTCUST");

        // Then
        assertNotNull(savedCustomer);
        assertTrue(retrievedCustomer.isPresent());
        assertEquals("Test Customer", retrievedCustomer.get().getCustomerName());
        assertEquals(0, new BigDecimal("1500.00").compareTo(retrievedCustomer.get().getAccountBalance()));
    }

    @Test
    @DisplayName("Should handle customer not found scenario")
    void shouldHandleCustomerNotFoundScenario() {
        // When
        Optional<Customer> customer = customerRepository.findById("NONEXISTENT");

        // Then
        assertFalse(customer.isPresent());
    }
}
```

### Step 5: Contract Testing

**Generate SOAP Contract Tests**:

```java
// src/test/java/com/dbs/customer/contract/SoapContractTest.java
package com.dbs.customer.contract;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.test.client.MockWebServiceServer;

/**
 * SOAP contract tests
 * Validates SOAP message format compliance with TIBCO specifications
 */
@SpringBootTest
class SoapContractTest {

    @Test
    @DisplayName("Should validate customer request schema compliance")
    void shouldValidateCustomerRequestSchemaCompliance() {
        // Load TIBCO SOAP request sample
        // Validate against generated WSDL
        // Ensure exact format compatibility
    }

    @Test
    @DisplayName("Should validate customer response schema compliance")
    void shouldValidateCustomerResponseSchemaCompliance() {
        // Load TIBCO SOAP response sample
        // Validate against generated WSDL
        // Ensure exact format compatibility
    }

    @Test
    @DisplayName("Should validate error response format")
    void shouldValidateErrorResponseFormat() {
        // Validate SOAP fault format
        // Ensure error codes match TIBCO behavior
        // Validate error message structure
    }
}
```

### Step 6: Functional End-to-End Tests

**Generate Business Process Tests**:

```java
// src/test/java/com/dbs/customer/functional/CustomerProcessFunctionalTest.java
package com.dbs.customer.functional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Functional tests for complete customer processing workflows
 * Validates end-to-end business processes match TIBCO behavior
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.properties")
class CustomerProcessFunctionalTest {

    @Test
    @DisplayName("TC_FUNC_001: Complete new customer creation workflow")
    void shouldCompleteNewCustomerCreationWorkflow() {
        // Scenario: New customer creation matching TIBCO process
        // 1. Receive customer request
        // 2. Validate customer data
        // 3. Check customer doesn't exist
        // 4. Integrate with Finacle
        // 5. Save customer to database
        // 6. Return success response
        //
        // Expected: Same behavior as TIBCO CustomerProcessingService
    }

    @Test
    @DisplayName("TC_FUNC_002: Customer update workflow")
    void shouldCompleteCustomerUpdateWorkflow() {
        // Scenario: Existing customer update matching TIBCO process
        // 1. Receive update request
        // 2. Validate customer data
        // 3. Retrieve existing customer
        // 4. Merge updates
        // 5. Save changes
        // 6. Return updated customer
        //
        // Expected: Same behavior as TIBCO update process
    }

    @Test
    @DisplayName("TC_FUNC_003: Error handling workflow")
    void shouldHandleErrorsCorrectly() {
        // Scenario: Error handling matching TIBCO behavior
        // 1. Invalid customer data
        // 2. External system failures
        // 3. Database errors
        // 4. Concurrent access issues
        //
        // Expected: Same error responses as TIBCO
    }
}
```

### Step 7: Performance Testing

**Generate Performance Tests**:

```java
// src/test/java/com/dbs/customer/performance/CustomerServicePerformanceTest.java
package com.dbs.customer.performance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;

/**
 * Performance tests for Customer Service
 * Validates response times and throughput match TIBCO baseline
 */
class CustomerServicePerformanceTest {

    @Test
    @DisplayName("PERF_001: Response time under load")
    @Disabled("Run manually for performance testing")
    void shouldMaintainResponseTimeUnderLoad() {
        // Test concurrent requests (50 threads, 100 requests each)
        // Measure response times
        // Compare against TIBCO baseline (target: <2 seconds 95th percentile)

        int numberOfThreads = 50;
        int requestsPerThread = 100;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        List<Long> responseTimes = new ArrayList<>();

        // Execute load test
        // Measure and assert response times
    }

    @Test
    @DisplayName("PERF_002: Memory usage stability")
    @Disabled("Run manually for performance testing")
    void shouldMaintainStableMemoryUsage() {
        // Test memory usage during sustained load
        // Ensure no memory leaks
        // Compare against TIBCO resource usage
    }

    @Test
    @DisplayName("PERF_003: Throughput validation")
    @Disabled("Run manually for performance testing")
    void shouldAchieveTargetThroughput() {
        // Test maximum throughput
        // Target: Match or exceed TIBCO throughput
        // Measure transactions per second
    }
}
```

### Step 8: Test Data Management

**Generate Test Data Sets** (`testing/test_data/customer_test_data.yaml`):

```yaml
test_data:
  valid_customers:
    - customer_id: "CUST0001"
      customer_name: "John Smith"
      email: "john.smith@example.com"
      account_balance: 1500.00
      status: "ACTIVE"

    - customer_id: "CUST0002"
      customer_name: "Jane Doe"
      email: "jane.doe@example.com"
      account_balance: 2500.00
      status: "ACTIVE"

  invalid_customers:
    - customer_id: ""
      customer_name: "Invalid Customer"
      email: "invalid@example.com"
      account_balance: 1000.00
      status: "ACTIVE"
      expected_error: "Customer ID cannot be empty"

    - customer_id: "CUST0003"
      customer_name: "Negative Balance Customer"
      email: "negative@example.com"
      account_balance: -100.00
      status: "ACTIVE"
      expected_error: "Account balance cannot be negative"

  edge_cases:
    - customer_id: "CUST9999"
      customer_name: "Maximum Length Name That Should Be Exactly One Hundred Characters Long And No More Than That"
      email: "very.long.email.address.that.tests.maximum.length@example.com"
      account_balance: 999999999.99
      status: "ACTIVE"
```

### Step 9: Test Execution and Reporting

**Execute Test Suite**:

```bash
# Run all tests and generate reports
cd .tibco_migration/implementation/spring_boot_project

# Unit tests
mvn test -Dtest="**/*Test" -DfailIfNoTests=false

# Integration tests
mvn test -Dtest="**/*IntegrationTest" -DfailIfNoTests=false

# Generate coverage report
mvn jacoco:report

# Generate test report
mvn surefire-report:report
```

**Generate Test Report** (`testing/test_results.md`):

```markdown
# Spring Boot Test Results

**Project**: {project_name}
**Test Date**: {current_date}
**Tester**: Phase 4 Agent

## Test Execution Summary

| Test Category | Total | Passed | Failed | Skipped | Coverage |
|---------------|-------|---------|---------|---------|----------|
| Unit Tests | 25 | 24 | 1 | 0 | 92% |
| Integration Tests | 12 | 11 | 1 | 0 | 85% |
| Contract Tests | 8 | 8 | 0 | 0 | 100% |
| Functional Tests | 6 | 6 | 0 | 0 | 98% |
| **TOTAL** | **51** | **49** | **2** | **0** | **90%** |

## Functional Equivalency Validation

### TIBCO Business Process Compliance
- ✅ Customer creation workflow: PASSED
- ✅ Customer update workflow: PASSED
- ✅ Error handling: PASSED
- ✅ External integrations: PASSED

### SOAP Contract Compliance
- ✅ Request schema validation: PASSED
- ✅ Response schema validation: PASSED
- ✅ Error response format: PASSED

### Data Transformation Validation
- ✅ XSD to DTO mapping: PASSED
- ✅ Business validation rules: PASSED
- ✅ Data type conversions: PASSED

## Performance Validation

| Metric | TIBCO Baseline | Spring Boot | Status |
|--------|---------------|-------------|---------|
| Response Time (95th) | 2.5s | 1.8s | ✅ IMPROVED |
| Throughput (TPS) | 100 | 120 | ✅ IMPROVED |
| Memory Usage | 512MB | 384MB | ✅ IMPROVED |
| CPU Utilization | 70% | 60% | ✅ IMPROVED |

## Test Failures Analysis

### Failed Tests
1. **CustomerServiceTest.testExternalSystemFailure**
   - Status: FAILED
   - Reason: Timeout handling differs from TIBCO
   - Action: Adjust timeout configuration
   - Priority: HIGH

2. **DatabaseIntegrationTest.testConnectionPooling**
   - Status: FAILED
   - Reason: Connection pool configuration
   - Action: Update connection pool settings
   - Priority: MEDIUM

## Recommendations

### Immediate Actions Required
1. Fix timeout handling in external service integration
2. Update database connection pool configuration
3. Re-run failed test cases

### Performance Optimizations
1. Consider caching frequently accessed customer data
2. Optimize database query patterns
3. Review external service call patterns

### Future Testing
1. Add load testing with production-like volumes
2. Implement chaos engineering tests
3. Add security penetration testing
```

## Validation Checklist

Before completing Phase 4:

- [ ] All unit tests implemented and passing (>90% coverage)
- [ ] Integration tests implemented and passing
- [ ] SOAP contract tests passing
- [ ] Functional end-to-end tests passing
- [ ] Performance tests executed
- [ ] Test data sets created
- [ ] Test results documented
- [ ] Functional equivalency validated
- [ ] Performance benchmarks met

## Success Criteria

**Phase 4 is complete when**:
- Comprehensive test suite generated and executed
- Test coverage exceeds 90%
- All critical functionality validated
- Functional equivalency with TIBCO confirmed
- Performance meets or exceeds baseline
- Test documentation complete

## Error Handling

**If Test Failures Occur**:
- Document failure details
- Categorize by severity (critical/high/medium/low)
- Provide specific remediation steps
- Re-run tests after fixes

**If Performance Issues Found**:
- Document performance gaps
- Suggest optimization strategies
- Flag for performance tuning phase
- Compare against TIBCO baselines

## Output Structure

```
.tibco_migration/testing/
├── test_results.md                # Main test report
├── test_artifacts/               # Generated test files
│   ├── unit_tests/
│   ├── integration_tests/
│   ├── contract_tests/
│   └── functional_tests/
├── test_data/                    # Test data sets
│   ├── customer_test_data.yaml
│   └── external_service_mocks.json
├── performance_results/          # Performance test results
└── coverage_reports/             # Test coverage reports
```

## Context Boundaries

**What This Agent Does**:
- Generates comprehensive test suites
- Executes all test categories
- Validates functional equivalency with TIBCO
- Measures performance against baselines
- Documents test results and recommendations

**What This Agent Does NOT Do**:
- Fix implementation bugs (refers back to Phase 3)
- Optimize performance (suggests improvements)
- Deploy to environments
- Conduct security audits

**Dependencies**:
- Requires: Complete Phase 3 Spring Boot implementation
- Provides: Validated implementation ready for deployment
