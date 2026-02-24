#!/bin/bash

# --- Configuration ---
PROJECT_NAME="banking-ledger-validation"
MAIN_CLASS="com.bank.ledger.validation.Application"
DOCKER_IMAGE_NAME="ledger-validation-app"
DOCKER_CONTAINER_NAME="ledger-validation-container"

# --- Colors for better output ---
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}🚀 Starting Validation Framework Demo Setup...${NC}"

# 1. Create Project Directory and navigate into it
if [ -d "$PROJECT_NAME" ]; then
    echo -e "${YELLOW}Directory $PROJECT_NAME already exists. Removing and recreating...${NC}"
    rm -rf "$PROJECT_NAME"
fi
mkdir "$PROJECT_NAME"
cd "$PROJECT_NAME"

echo -e "${GREEN}📁 Created project directory: $(pwd)${NC}"

# 2. Create pom.xml
echo -e "${GREEN}📝 Creating pom.xml...${NC}"
cat <<'POMEOF' > pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.bank.ledger</groupId>
    <artifactId>banking-ledger-validation</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <mainClass>com.bank.ledger.validation.Application</mainClass>
    </properties>

    <dependencies>
        <!-- Jakarta Bean Validation API -->
        <dependency>
            <groupId>jakarta.validation</groupId>
            <artifactId>jakarta.validation-api</artifactId>
            <version>3.0.2</version>
        </dependency>
        <!-- Hibernate Validator (Implementation of JSR 380) -->
        <dependency>
            <groupId>org.hibernate.validator</groupId>
            <artifactId>hibernate-validator</artifactId>
            <version>8.0.1.Final</version>
        </dependency>
        <!-- Jakarta Expression Language (required by Hibernate Validator) -->
        <dependency>
            <groupId>org.glassfish</groupId>
            <artifactId>jakarta.el</artifactId>
            <version>4.0.2</version>
        </dependency>
        <!-- Jackson for JSON processing (for AccountRepository) -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.15.2</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>${maven.compiler.source}</source>
                    <target>${maven.compiler.target}</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <addClasspath>true</addClasspath>
                            <mainClass>${mainClass}</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>3.4.2</version>
                <executions>
                    <execution>
                        <id>make-assembly</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                        <configuration>
                            <archive>
                                <manifest>
                                    <mainClass>${mainClass}</mainClass>
                                </manifest>
                            </archive>
                            <descriptorRefs>
                                <descriptorRef>jar-with-dependencies</descriptorRef>
                            </descriptorRefs>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
POMEOF

# 3. Create Java Source Directories
echo -e "${GREEN}Creating source directories...${NC}"
mkdir -p src/main/java/com/bank/ledger/validation/{model,repository,service,validator}
mkdir -p src/main/resources

# 4. Create Java Source Files
echo -e "${GREEN}Creating Java source files...${NC}"

cat <<EOF > src/main/java/com/bank/ledger/validation/model/TransactionRequest.java
package com.bank.ledger.validation.model;

import com.bank.ledger.validation.validator.SufficientFunds;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

@SufficientFunds(message = "Account does not have sufficient funds for this transaction.")
public class TransactionRequest {

    @NotNull(message = "Transaction ID cannot be null.")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
             message = "Invalid Transaction ID format. Must be a UUID.")
    private String transactionId;

    @NotNull(message = "Account ID cannot be null.")
    @Pattern(regexp = "^ACC-[0-9]{6}$", message = "Invalid Account ID format. Must be ACC-######.")
    private String accountId;

    @NotNull(message = "Amount cannot be null.")
    @DecimalMin(value = "0.01", message = "Amount must be positive.")
    private BigDecimal amount;

    @NotNull(message = "Currency cannot be null.")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code.")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3 uppercase letters.")
    private String currency;

    @NotNull(message = "Transaction type cannot be null.")
    @Pattern(regexp = "DEBIT|CREDIT", message = "Transaction type must be DEBIT or CREDIT.")
    private String transactionType;

    // Constructors, Getters, Setters
    public TransactionRequest() {}

    public TransactionRequest(String transactionId, String accountId, BigDecimal amount, String currency, String transactionType) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.amount = amount;
        this.currency = currency;
        this.transactionType = transactionType;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    @Override
    public String toString() {
        return "TransactionRequest{" +
               "transactionId=" + transactionId +
               ", accountId=" + accountId +
               ", amount=" + amount +
               ", currency=" + currency +
               ", transactionType=" + transactionType +
               "}";
    }
}
EOF

cat <<EOF > src/main/java/com/bank/ledger/validation/model/Account.java
package com.bank.ledger.validation.model;

import java.math.BigDecimal;

public class Account {
    private String accountId;
    private BigDecimal balance;
    private String currency;
    private boolean active;

    public Account() {}

    public Account(String accountId, BigDecimal balance, String currency, boolean active) {
        this.accountId = accountId;
        this.balance = balance;
        this.currency = currency;
        this.active = active;
    }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "Account{" +
               "accountId=" + accountId +
               ", balance=" + balance +
               ", currency=" + currency +
               ", active=" + active +
               "}";
    }
}
EOF

cat <<EOF > src/main/java/com/bank/ledger/validation/repository/AccountRepository.java
package com.bank.ledger.validation.repository;

import com.bank.ledger.validation.model.Account;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccountRepository {
    private final Map<String, Account> accounts;

    public AccountRepository() {
        this.accounts = loadAccounts();
    }

    private Map<String, Account> loadAccounts() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("accounts.json")) {
            if (is == null) {
                System.err.println("accounts.json not found in classpath.");
                return Collections.emptyMap();
            }
            Map<String, Account> loadedAccounts = mapper.readValue(is, new TypeReference<Map<String, Account>>() {});
            System.out.println("Loaded " + loadedAccounts.size() + " accounts from accounts.json.");
            return loadedAccounts;
        } catch (IOException e) {
            System.err.println("Error loading accounts from JSON: " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    public Optional<Account> findById(String accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }
}
EOF

cat <<EOF > src/main/java/com/bank/ledger/validation/validator/SufficientFunds.java
package com.bank.ledger.validation.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SufficientFundsValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SufficientFunds {
    String message() default "Insufficient funds.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
EOF

cat <<EOF > src/main/java/com/bank/ledger/validation/validator/SufficientFundsValidator.java
package com.bank.ledger.validation.validator;

import com.bank.ledger.validation.model.Account;
import com.bank.ledger.validation.model.TransactionRequest;
import com.bank.ledger.validation.repository.AccountRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;
import java.util.Optional;

public class SufficientFundsValidator implements ConstraintValidator<SufficientFunds, TransactionRequest> {

    private final AccountRepository accountRepository = new AccountRepository();

    @Override
    public void initialize(SufficientFunds constraintAnnotation) {
        // Can retrieve annotation attributes if needed
    }

    @Override
    public boolean isValid(TransactionRequest request, ConstraintValidatorContext context) {
        if (request == null || request.getAccountId() == null || request.getAmount() == null || request.getTransactionType() == null) {
            // Let other @NotNull constraints handle these basic checks
            return true;
        }

        if (request.getTransactionType().equals("DEBIT")) {
            Optional<Account> accountOpt = accountRepository.findById(request.getAccountId());
            if (accountOpt.isEmpty()) {
                // Account not found, let other validators or service logic handle this if it's not a primary validation concern here
                // For this demo, we'll just say it's valid if account is not found, assuming other checks handle existence
                // In a real system, account existence would be a critical early check.
                return true;
            }

            Account account = accountOpt.get();

            // Additional business rule: account must be active for debits
            if (!account.isActive()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Account is not active.")
                       .addPropertyNode("accountId")
                       .addConstraintViolation();
                return false;
            }

            if (account.getBalance().compareTo(request.getAmount()) < 0) {
                // Funds are insufficient
                // The default message for @SufficientFunds will be used, or we can customize it here:
                // context.disableDefaultConstraintViolation();
                // context.buildConstraintViolationWithTemplate("Account " + request.getAccountId() + " has " + account.getBalance() + " but needs " + request.getAmount() + ".")
                //        .addPropertyNode("amount")
                //        .addConstraintViolation();
                return false;
            }
        }
        // For CREDIT transactions, sufficient funds are not a concern.
        return true;
    }
}
EOF

cat <<EOF > src/main/java/com/bank/ledger/validation/service/TransactionValidationService.java
package com.bank.ledger.validation.service;

import com.bank.ledger.validation.model.TransactionRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

public class TransactionValidationService {

    private final Validator validator;

    public TransactionValidationService() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public Set<ConstraintViolation<TransactionRequest>> validate(TransactionRequest request) {
        return validator.validate(request);
    }

    public void printValidationResults(TransactionRequest request, Set<ConstraintViolation<TransactionRequest>> violations) {
        System.out.println("--- Validating Transaction: " + request.getTransactionId() + " ---");
        if (violations.isEmpty()) {
            System.out.println("✅ Transaction is VALID.");
        } else {
            System.out.println("❌ Transaction is INVALID. Found " + violations.size() + " violation(s):");
            for (ConstraintViolation<TransactionRequest> violation : violations) {
                System.out.println("  - " + violation.getPropertyPath() + ": " + violation.getMessage() + " (Invalid value: '" + violation.getInvalidValue() + "')");
            }
        }
        System.out.println("--------------------------------------------------\n");
    }
}
EOF

cat <<EOF > src/main/java/com/bank/ledger/validation/Application.java
package com.bank.ledger.validation;

import com.bank.ledger.validation.model.TransactionRequest;
import com.bank.ledger.validation.service.TransactionValidationService;
import jakarta.validation.ConstraintViolation;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public class Application {
    public static void main(String[] args) {
        System.out.println("🚀 Starting Transaction Validation Service Demo...\n");

        TransactionValidationService validationService = new TransactionValidationService();

        // --- Test Cases ---

        // 1. Valid DEBIT transaction
        TransactionRequest validDebit = new TransactionRequest(
            UUID.randomUUID().toString(), "ACC-100001", new BigDecimal("100.00"), "USD", "DEBIT"
        );
        Set<ConstraintViolation<TransactionRequest>> violations1 = validationService.validate(validDebit);
        validationService.printValidationResults(validDebit, violations1);

        // 2. Invalid DEBIT: Insufficient Funds
        TransactionRequest insufficientFundsDebit = new TransactionRequest(
            UUID.randomUUID().toString(), "ACC-100002", new BigDecimal("600.00"), "USD", "DEBIT"
        );
        Set<ConstraintViolation<TransactionRequest>> violations2 = validationService.validate(insufficientFundsDebit);
        validationService.printValidationResults(insufficientFundsDebit, violations2);

        // 3. Invalid DEBIT: Account not active
        TransactionRequest inactiveAccountDebit = new TransactionRequest(
            UUID.randomUUID().toString(), "ACC-100005", new BigDecimal("100.00"), "USD", "DEBIT"
        );
        Set<ConstraintViolation<TransactionRequest>> violations3 = validationService.validate(inactiveAccountDebit);
        validationService.printValidationResults(inactiveAccountDebit, violations3);

        // 4. Valid CREDIT transaction
        TransactionRequest validCredit = new TransactionRequest(
            UUID.randomUUID().toString(), "ACC-100001", new BigDecimal("200.00"), "USD", "CREDIT"
        );
        Set<ConstraintViolation<TransactionRequest>> violations4 = validationService.validate(validCredit);
        validationService.printValidationResults(validCredit, violations4);

        // 5. Invalid Transaction: Null Account ID
        TransactionRequest nullAccountId = new TransactionRequest(
            UUID.randomUUID().toString(), null, new BigDecimal("50.00"), "USD", "DEBIT"
        );
        Set<ConstraintViolation<TransactionRequest>> violations5 = validationService.validate(nullAccountId);
        validationService.printValidationResults(nullAccountId, violations5);

        // 6. Invalid Transaction: Negative Amount
        TransactionRequest negativeAmount = new TransactionRequest(
            UUID.randomUUID().toString(), "ACC-100001", new BigDecimal("-10.00"), "USD", "DEBIT"
        );
        Set<ConstraintViolation<TransactionRequest>> violations6 = validationService.validate(negativeAmount);
        validationService.printValidationResults(negativeAmount, violations6);

        // 7. Invalid Transaction: Bad Currency Format
        TransactionRequest badCurrency = new TransactionRequest(
            UUID.randomUUID().toString(), "ACC-100001", new BigDecimal("100.00"), "US", "DEBIT"
        );
        Set<ConstraintViolation<TransactionRequest>> violations7 = validationService.validate(badCurrency);
        validationService.printValidationResults(badCurrency, violations7);

        // 8. Invalid Transaction: Non-existent Account (will pass custom validation, caught by service logic later)
        // Note: For this demo, SufficientFundsValidator assumes account existence is handled elsewhere
        TransactionRequest nonExistentAccount = new TransactionRequest(
            UUID.randomUUID().toString(), "ACC-999999", new BigDecimal("100.00"), "USD", "DEBIT"
        );
        Set<ConstraintViolation<TransactionRequest>> violations8 = validationService.validate(nonExistentAccount);
        validationService.printValidationResults(nonExistentAccount, violations8);


        System.out.println("🏁 Transaction Validation Service Demo Finished.");
    }
}
EOF

# 5. Create resources file
echo -e "${GREEN}Creating accounts.json...${NC}"
cat <<EOF > src/main/resources/accounts.json
{
  "ACC-100001": {
    "accountId": "ACC-100001",
    "balance": 1500.00,
    "currency": "USD",
    "active": true
  },
  "ACC-100002": {
    "accountId": "ACC-100002",
    "balance": 500.00,
    "currency": "USD",
    "active": true
  },
  "ACC-100003": {
    "accountId": "ACC-100003",
    "balance": 10000.00,
    "currency": "EUR",
    "active": true
  },
  "ACC-100004": {
    "accountId": "ACC-100004",
    "balance": 0.00,
    "currency": "USD",
    "active": true
  },
  "ACC-100005": {
    "accountId": "ACC-100005",
    "balance": 5000.00,
    "currency": "USD",
    "active": false
  }
}
EOF

# 6. Build the project
echo -e "${GREEN}📦 Building the project with Maven...${NC}"
if ! mvn clean install -DskipTests; then
    echo -e "${RED}❌ Maven build failed! Exiting.${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Project built successfully.${NC}"

# 7. Run the application (without Docker)
echo -e "${GREEN}🚀 Running the application (without Docker)...${NC}"
if ! java -jar target/"${PROJECT_NAME}"-1.0-SNAPSHOT-jar-with-dependencies.jar; then
    echo -e "${RED}❌ Application failed to run!${NC}"
else
    echo -e "${GREEN}✅ Application ran successfully.${NC}"
fi

echo -e "\n${GREEN}--- Docker Setup ---${NC}"

# 8. Create Dockerfile
echo -e "${GREEN}📝 Creating Dockerfile...${NC}"
cat <<EOF > Dockerfile
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/banking-ledger-validation-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
EOF

# 9. Build Docker image
echo -e "${GREEN}🐳 Building Docker image: ${DOCKER_IMAGE_NAME}...${NC}"
if docker build -t "$DOCKER_IMAGE_NAME" . 2>/dev/null; then
  echo -e "${GREEN}✅ Docker image built successfully.${NC}"
  # 10. Run Docker container
  echo -e "${GREEN}▶️ Running Docker container: ${DOCKER_CONTAINER_NAME}...${NC}"
  docker stop "$DOCKER_CONTAINER_NAME" > /dev/null 2>&1 || true
  docker rm "$DOCKER_CONTAINER_NAME" > /dev/null 2>&1 || true
  if docker run --name "$DOCKER_CONTAINER_NAME" --detach "$DOCKER_IMAGE_NAME" 2>/dev/null; then
    echo -e "${GREEN}✅ Docker container started. Fetching logs...${NC}"
    sleep 5
    docker logs "$DOCKER_CONTAINER_NAME" 2>/dev/null || true
  fi
else
  echo -e "${YELLOW}⚠ Docker build skipped or failed (Docker may be unavailable). Run manually if needed.${NC}"
fi

echo -e "\n${GREEN}🎉 Demo finished! You can stop the Docker container using 'docker stop ${DOCKER_CONTAINER_NAME}' or run ./stop.sh${NC}"

cd ..