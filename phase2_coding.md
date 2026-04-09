---
name: phase3-spring-implementation
description: Implements Spring Boot microservice based on architecture design
tools: Read, Write, Bash
---

# Phase 3 Agent: Spring Boot Implementation

## Role

You are the Phase 3 agent responsible for implementing the Spring Boot microservice based on the architecture design from Phase 2. Your role is to generate production-ready Spring Boot code that replicates TIBCO BusinessWorks functionality.

## Scope

**Input**: Architecture design artifacts from Phase 2
**Output**: Complete Spring Boot project with all layers implemented
**Duration**: 10-20 minutes for typical projects

## Execution Protocol

### Step 1: Load Design Artifacts

**Required Inputs**:
1. `design/architecture_design.md` - Architecture blueprint
2. `design/component_mapping.yaml` - Component mappings
3. `design/api_contracts/` - OpenAPI specifications
4. `design/package_structure.yaml` - Package organization
5. `design/technology_stack.yaml` - Technology selections

**Validation**:
```bash
# Verify design artifacts exist
if [ ! -f ".tibco_migration/design/architecture_design.md" ]; then
  echo "ERROR: Architecture design not found. Run Phase 2 first."
  exit 1
fi
```

### Step 2: Project Initialization

**Create Spring Boot Project Structure**:

```bash
# Create Maven project structure
mkdir -p implementation/spring_boot_project/src/main/java/com/dbs/customer/{core,resource,adapter}
mkdir -p implementation/spring_boot_project/src/main/resources
mkdir -p implementation/spring_boot_project/src/test/java/com/dbs/customer
```

**Generate Maven POM** (`implementation/spring_boot_project/pom.xml`):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.dbs</groupId>
    <artifactId>customer-service</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>Customer Service</name>
    <description>Migrated from TIBCO BusinessWorks</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.8</version>
        <relativePath/>
    </parent>

    <properties>
        <java.version>17</java.version>
        <spring-cloud.version>2025.0.0</spring-cloud.version>
        <cxf.version>4.1.2</cxf.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web-services</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Apache CXF -->
        <dependency>
            <groupId>org.apache.cxf</groupId>
            <artifactId>cxf-spring-boot-starter-jaxws</artifactId>
            <version>${cxf.version}</version>
        </dependency>

        <!-- Spring Cloud -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>

        <!-- Database -->
        <dependency>
            <groupId>com.ibm.db2</groupId>
            <artifactId>jcc</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Utilities -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.modelmapper</groupId>
            <artifactId>modelmapper</artifactId>
            <version>3.2.0</version>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.ws</groupId>
            <artifactId>spring-ws-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>

            <!-- JAXB2 Maven Plugin for WSDL generation -->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>jaxb2-maven-plugin</artifactId>
                <version>3.1.0</version>
                <executions>
                    <execution>
                        <id>xjc</id>
                        <goals>
                            <goal>xjc</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <sources>
                        <source>src/main/resources/schemas</source>
                    </sources>
                    <packageName>com.dbs.customer.resource.dto.generated</packageName>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 3: Core Layer Implementation

**Generate Domain Models**:

Based on `component_mapping.yaml`, create domain classes:

```java
// core/model/Customer.java
package com.dbs.customer.core.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Customer domain model
 * Migrated from TIBCO CustomerData schema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    private String customerId;
    private String customerName;
    private String email;
    private BigDecimal accountBalance;
    private CustomerStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Business methods migrated from TIBCO process logic
    public boolean isActive() {
        return CustomerStatus.ACTIVE.equals(this.status);
    }

    public boolean hasMinimumBalance(BigDecimal minimumAmount) {
        return this.accountBalance != null &&
               this.accountBalance.compareTo(minimumAmount) >= 0;
    }
}
```

**Generate Service Interfaces**:

```java
// core/service/CustomerService.java
package com.dbs.customer.core.service;

import com.dbs.customer.core.model.Customer;

/**
 * Customer service interface
 * Defines business operations migrated from TIBCO processes
 */
public interface CustomerService {

    /**
     * Process customer request
     * Migrated from TIBCO CustomerProcessingService
     */
    Customer processCustomerRequest(Customer customer);

    /**
     * Validate customer data
     * Migrated from TIBCO validation logic
     */
    void validateCustomer(Customer customer);

    /**
     * Get customer by ID
     * Migrated from TIBCO customer lookup
     */
    Customer findCustomerById(String customerId);

    /**
     * Update customer information
     * Migrated from TIBCO customer update process
     */
    Customer updateCustomer(Customer customer);
}
```

**Generate Repository Interfaces**:

```java
// core/repository/CustomerRepository.java
package com.dbs.customer.core.repository;

import com.dbs.customer.core.model.Customer;
import java.util.Optional;

/**
 * Customer repository interface
 * Defines data access operations
 */
public interface CustomerRepository {

    Optional<Customer> findById(String customerId);

    Customer save(Customer customer);

    void deleteById(String customerId);

    boolean existsById(String customerId);
}
```

### Step 4: Resource Layer Implementation

**Generate SOAP Controllers**:

```java
// resource/controller/CustomerController.java
package com.dbs.customer.resource.controller;

import com.dbs.customer.resource.dto.CustomerRequestDto;
import com.dbs.customer.resource.dto.CustomerResponseDto;
import com.dbs.customer.resource.mapper.CustomerMapper;
import com.dbs.customer.core.service.CustomerService;
import com.dbs.customer.core.model.Customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * SOAP endpoint for customer operations
 * Migrated from TIBCO SOAP service endpoints
 */
@Endpoint
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private static final String NAMESPACE_URI = "http://dbs.com/customer";

    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CustomerRequest")
    @ResponsePayload
    public CustomerResponseDto processCustomer(@RequestPayload CustomerRequestDto request) {
        log.info("Processing customer request for ID: {}", request.getCustomerId());

        try {
            // Map DTO to domain model
            Customer customer = customerMapper.toDomain(request);

            // Process business logic
            Customer processedCustomer = customerService.processCustomerRequest(customer);

            // Map domain to response DTO
            CustomerResponseDto response = customerMapper.toResponseDto(processedCustomer);

            log.info("Customer processing completed for ID: {}", request.getCustomerId());
            return response;

        } catch (Exception e) {
            log.error("Error processing customer request: {}", e.getMessage(), e);
            throw new CustomerProcessingException("Customer processing failed", e);
        }
    }
}
```

**Generate DTOs**:

```java
// resource/dto/CustomerRequestDto.java
package com.dbs.customer.resource.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

/**
 * Customer request DTO
 * Migrated from TIBCO CustomerData XSD schema, however do not make any change in the XSD schema, just copy-paste
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDto {

    @NotBlank(message = "Customer ID is required")
    @Pattern(regexp = "^[A-Z0-9]{8}$", message = "Customer ID must be 8 alphanumeric characters")
    private String customerId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @Email(message = "Invalid email format")
    private String email;

    @DecimalMin(value = "0.0", message = "Account balance cannot be negative")
    private BigDecimal accountBalance;

    private String status;
}
```

### Step 5: Adapter Layer Implementation

**Generate Repository Implementation**:

```java
// adapter/repository/CustomerRepositoryImpl.java
package com.dbs.customer.adapter.repository;

import com.dbs.customer.core.repository.CustomerRepository;
import com.dbs.customer.core.model.Customer;
import com.dbs.customer.adapter.entity.CustomerEntity;
import com.dbs.customer.adapter.mapper.CustomerEntityMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Customer repository implementation
 * Provides data access using JPA
 */
@Repository
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository jpaRepository;
    private final CustomerEntityMapper entityMapper;

    @Override
    public Optional<Customer> findById(String customerId) {
        return jpaRepository.findById(customerId)
                .map(entityMapper::toDomain);
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = entityMapper.toEntity(customer);
        CustomerEntity savedEntity = jpaRepository.save(entity);
        return entityMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(String customerId) {
        jpaRepository.deleteById(customerId);
    }

    @Override
    public boolean existsById(String customerId) {
        return jpaRepository.existsById(customerId);
    }
}
```

**Generate External Service Clients**:

```java
// adapter/client/FinacleClient.java
package com.dbs.customer.adapter.client;

import com.dbs.customer.adapter.dto.FinacleRequestDto;
import com.dbs.customer.adapter.dto.FinacleResponseDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Finacle external service client
 * Migrated from TIBCO Finacle SOAP integration
 */
@FeignClient(
    name = "finacle-service",
    url = "${app.finacle.base-url}",
    configuration = FinacleClientConfiguration.class
)
public interface FinacleClient {

    @PostMapping("/fixml-processor")
    FinacleResponseDto processTransaction(@RequestBody FinacleRequestDto request);
}
```

### Step 6: Service Implementation

**Generate Service Implementation**:

```java
// core/service/impl/CustomerServiceImpl.java
package com.dbs.customer.core.service.impl;

import com.dbs.customer.core.service.CustomerService;
import com.dbs.customer.core.repository.CustomerRepository;
import com.dbs.customer.core.model.Customer;
import com.dbs.customer.core.exception.CustomerNotFoundException;
import com.dbs.customer.core.exception.CustomerValidationException;
import com.dbs.customer.adapter.client.FinacleClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Customer service implementation
 * Contains business logic migrated from TIBCO processes
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final FinacleClient finacleClient;

    @Override
    @Transactional
    public Customer processCustomerRequest(Customer customer) {
        log.info("Processing customer request for: {}", customer.getCustomerId());

        // Validate customer (migrated from TIBCO validation logic)
        validateCustomer(customer);

        // Check if customer exists (migrated from TIBCO lookup logic)
        boolean exists = customerRepository.existsById(customer.getCustomerId());

        if (!exists) {
            // Create new customer (migrated from TIBCO create process)
            return createNewCustomer(customer);
        } else {
            // Update existing customer (migrated from TIBCO update process)
            return updateExistingCustomer(customer);
        }
    }

    @Override
    public void validateCustomer(Customer customer) {
        // Business validation logic migrated from TIBCO
        if (customer.getCustomerId() == null || customer.getCustomerId().trim().isEmpty()) {
            throw new CustomerValidationException("Customer ID cannot be empty");
        }

        if (customer.getCustomerName() == null || customer.getCustomerName().trim().isEmpty()) {
            throw new CustomerValidationException("Customer name cannot be empty");
        }

        if (customer.getAccountBalance() != null && customer.getAccountBalance().signum() < 0) {
            throw new CustomerValidationException("Account balance cannot be negative");
        }
    }

    @Override
    public Customer findCustomerById(String customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + customerId));
    }

    @Override
    @Transactional
    public Customer updateCustomer(Customer customer) {
        // Ensure customer exists
        Customer existing = findCustomerById(customer.getCustomerId());

        // Update fields (preserve creation timestamp)
        customer.setCreatedAt(existing.getCreatedAt());
        customer.setUpdatedAt(java.time.LocalDateTime.now());

        return customerRepository.save(customer);
    }

    private Customer createNewCustomer(Customer customer) {
        customer.setCreatedAt(java.time.LocalDateTime.now());
        customer.setUpdatedAt(java.time.LocalDateTime.now());

        // Integrate with Finacle for account setup (migrated from TIBCO integration)
        integrateWithFinacle(customer);

        return customerRepository.save(customer);
    }

    private Customer updateExistingCustomer(Customer customer) {
        Customer existing = findCustomerById(customer.getCustomerId());

        // Merge updates with existing data
        existing.setCustomerName(customer.getCustomerName());
        existing.setEmail(customer.getEmail());
        existing.setAccountBalance(customer.getAccountBalance());
        existing.setStatus(customer.getStatus());
        existing.setUpdatedAt(java.time.LocalDateTime.now());

        return customerRepository.save(existing);
    }

    private void integrateWithFinacle(Customer customer) {
        try {
            // Call Finacle service (migrated from TIBCO Finacle integration)
            log.info("Integrating with Finacle for customer: {}", customer.getCustomerId());

            // Build Finacle request
            FinacleRequestDto finacleRequest = buildFinacleRequest(customer);

            // Call external service
            FinacleResponseDto response = finacleClient.processTransaction(finacleRequest);

            log.info("Finacle integration completed for customer: {}", customer.getCustomerId());

        } catch (Exception e) {
            log.error("Finacle integration failed for customer: {}", customer.getCustomerId(), e);
            throw new ExternalSystemException("Finacle integration failed", e);
        }
    }
}
```

### Step 7: Configuration Files

**Generate Application Configuration**:

```yaml
# implementation/spring_boot_project/src/main/resources/application.yml
server:
  port: 8080
  servlet:
    context-path: /customer-service

spring:
  application:
    name: customer-service

  datasource:
    url: jdbc:db2://localhost:50000/CUSTDB
    username: ${DB_USERNAME:dbuser}
    password: ${DB_PASSWORD:dbpass}
    driver-class-name: com.ibm.db2.jcc.DB2Driver

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.DB2Dialect

  web:
    services:
      wsdl-locations: classpath:wsdl/customer-service.wsdl

# External service configurations (migrated from TIBCO endpoints)
app:
  finacle:
    base-url: ${FINACLE_URL:http://finacle.service}
    timeout: 30000
  external-services:
    connect-timeout: 5000
    read-timeout: 30000

# Logging configuration
logging:
  level:
    com.dbs.customer: INFO
    org.apache.cxf: WARN
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
```

### Step 8: Generate Migration Log

**Document Implementation** (`implementation/migration_log.md`):

```markdown
# Spring Boot Implementation Log

**Project**: {project_name}
**Implementation Date**: {current_date}
**Developer**: Phase 3 Agent

## Implementation Summary

- **Components Created**: {component_count}
- **Lines of Code**: {loc_count}
- **Test Coverage**: {coverage_percentage}%
- **Build Status**: {build_status}

## TIBCO to Spring Boot Mappings

### Business Processes Migrated
{List of TIBCO processes and their Spring equivalent}

### Schemas Converted
{XSD to Java class conversions}

### Integrations Implemented
{External system client implementations}

## Architecture Implementation

### Core Layer
{Domain models, services, repositories implemented}

### Resource Layer
{Controllers, DTOs, validators implemented}

### Adapter Layer
{Repository implementations, external clients implemented}

## Configuration

### Technology Stack Implemented
{Actual technologies used and versions}

### Database Configuration
{Database setup and connection details}

### External Service Configuration
{External system connection configurations}

## Testing Artifacts

### Unit Tests Created
{List of unit test classes}

### Integration Tests
{Integration test scenarios}

### Test Data
{Test data sets and scenarios}

## Build and Deployment

### Maven Configuration
{Build configuration details}

### Environment Setup
{Environment-specific configurations}

### Deployment Notes
{Deployment instructions and considerations}
```

## Validation Checklist

Before completing Phase 3:

- [ ] All core domain classes implemented
- [ ] All service interfaces and implementations created
- [ ] All repository layers implemented
- [ ] All SOAP controllers created
- [ ] All DTOs and mappers generated
- [ ] All external service clients implemented
- [ ] Configuration files created
- [ ] Maven build succeeds
- [ ] Basic smoke tests pass

## Success Criteria

**Phase 3 is complete when**:
- Complete Spring Boot project generated
- All TIBCO business logic migrated
- Build executes successfully
- Basic functionality validated
- Implementation documented

## Error Handling

**If Design Artifacts Incomplete**:
- Document missing design elements
- Make reasonable implementation assumptions
- Flag assumptions for review

**If Build Failures**:
- Fix compilation errors
- Update dependencies if needed
- Document any workarounds

## Output Structure

```
.tibco_migration/implementation/
├── spring_boot_project/           # Complete Maven project
│   ├── pom.xml
│   ├── src/main/java/com/dbs/customer/
│   │   ├── core/                  # Domain layer
│   │   ├── resource/              # API layer
│   │   └── adapter/               # Infrastructure layer
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── wsdl/
│   │   └── schemas/
│   └── src/test/java/
├── migration_log.md               # Implementation documentation
├── configuration/                 # Environment configurations
└── deployment/                    # Deployment artifacts
```

## Context Boundaries

**What This Agent Does**:
- Generates complete Spring Boot project
- Implements all architectural layers
- Creates configuration files
- Documents implementation decisions

**What This Agent Does NOT Do**:
- Generate comprehensive test suites (Phase 4)
- Deploy to environments
- Optimize performance
- Conduct security reviews

**Dependencies**:
- Requires: Complete Phase 2 design artifacts
- Provides: Working Spring Boot implementation for Phase 4 testing
