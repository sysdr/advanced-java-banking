#!/bin/bash
set -e

# Define project details
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_NAME="LedgerSystem"
PROJECT_ROOT="$SCRIPT_DIR/$PROJECT_NAME"
MAIN_CLASS="com.mybank.ledger.LedgerApp"
DOCKER_IMAGE_NAME="mybank-ledger-app"
DOCKER_CONTAINER_NAME="${DOCKER_IMAGE_NAME}-instance"

# Function to display a professional-looking message
function display_message() {
    echo "----------------------------------------------------"
    echo "$1"
    echo "----------------------------------------------------"
}

# --- 1. Setup Project Structure and Source Files ---
display_message "1. Setting up project structure and generating source files..."

# Clean up previous runs
rm -rf "$SCRIPT_DIR/$PROJECT_NAME" "$SCRIPT_DIR/target" "$SCRIPT_DIR/Dockerfile"

mkdir -p "$PROJECT_ROOT/src/main/java/com/mybank/ledger"

# Create pom.xml
cat <<EOL > "$PROJECT_ROOT/pom.xml"
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.mybank</groupId>
    <artifactId>ledger-system</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <addClasspath>true</addClasspath>
                            <mainClass>${MAIN_CLASS}</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
EOL

# Create CurrencyUnit.java
cat <<EOL > "$PROJECT_ROOT/src/main/java/com/mybank/ledger/CurrencyUnit.java"
package com.mybank.ledger;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents a specific currency with its ISO code and default fraction digits.
 * Implemented as an immutable class for robustness.
 */
public final class CurrencyUnit {
    private final String code;
    private final int defaultFractionDigits;

    // Pre-defined common currencies
    public static final CurrencyUnit USD = new CurrencyUnit("USD", 2);
    public static final CurrencyUnit EUR = new CurrencyUnit("EUR", 2);
    public static final CurrencyUnit JPY = new CurrencyUnit("JPY", 0);
    public static final CurrencyUnit BTC = new CurrencyUnit("BTC", 8); // Bitcoin, 8 decimal places (Satoshis)

    private static final Map<String, CurrencyUnit> CURRENCIES_BY_CODE = 
        Arrays.asList(USD, EUR, JPY, BTC)
              .stream()
              .collect(Collectors.toMap(CurrencyUnit::getCode, Function.identity()));

    private CurrencyUnit(String code, int defaultFractionDigits) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code cannot be null or empty.");
        }
        if (defaultFractionDigits < 0) {
            throw new IllegalArgumentException("Default fraction digits cannot be negative.");
        }
        this.code = code.toUpperCase();
        this.defaultFractionDigits = defaultFractionDigits;
    }

    /**
     * Factory method to get a CurrencyUnit instance by its ISO code.
     * For simplicity, this only supports pre-defined currencies.
     */
    public static CurrencyUnit of(String code) {
        return Optional.ofNullable(CURRENCIES_BY_CODE.get(code.toUpperCase()))
                       .orElseThrow(() -> new IllegalArgumentException("Unsupported currency code: " + code));
    }

    public String getCode() {
        return code;
    }

    public int getDefaultFractionDigits() {
        return defaultFractionDigits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyUnit that = (CurrencyUnit) o;
        return defaultFractionDigits == that.defaultFractionDigits && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, defaultFractionDigits);
    }

    @Override
    public String toString() {
        return code;
    }
}
EOL

# Create Money.java
cat <<EOL > "$PROJECT_ROOT/src/main/java/com/mybank/ledger/Money.java"
package com.mybank.ledger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Represents an immutable monetary amount, internally stored as a long
 * representing minor units to ensure precision and performance.
 * It's strongly typed with a CurrencyUnit to prevent currency mixing.
 */
public final class Money implements Comparable<Money> {

    private final long minorUnits; // Amount in the smallest indivisible unit (e.g., cents for USD)
    private final CurrencyUnit currencyUnit;

    // Private constructor to enforce creation via static factory methods
    private Money(long minorUnits, CurrencyUnit currencyUnit) {
        Objects.requireNonNull(currencyUnit, "CurrencyUnit cannot be null.");
        this.minorUnits = minorUnits;
        this.currencyUnit = currencyUnit;
    }

    /**
     * Creates a Money instance from a BigDecimal amount and a CurrencyUnit.
     * The BigDecimal is scaled and rounded to the minor units.
     *
     * @param amount       The monetary amount as BigDecimal.
     * @param currencyUnit The currency.
     * @return An immutable Money instance.
     * @throws IllegalArgumentException if currencyUnit is null.
     */
    public static Money of(BigDecimal amount, CurrencyUnit currencyUnit) {
        Objects.requireNonNull(amount, "Amount cannot be null.");
        Objects.requireNonNull(currencyUnit, "CurrencyUnit cannot be null.");

        // Scale the BigDecimal to the currency's default fraction digits and convert to minor units
        BigDecimal scaledAmount = amount.setScale(currencyUnit.getDefaultFractionDigits(), RoundingMode.HALF_EVEN);
        long minorUnits = scaledAmount.movePointRight(currencyUnit.getDefaultFractionDigits()).longValueExact(); // Ensure no fractional minor units
        
        return new Money(minorUnits, currencyUnit);
    }

    /**
     * Creates a zero-value Money instance for a given currency.
     */
    public static Money zero(CurrencyUnit currencyUnit) {
        return new Money(0, currencyUnit);
    }

    public long getMinorUnits() {
        return minorUnits;
    }

    public CurrencyUnit getCurrencyUnit() {
        return currencyUnit;
    }

    /**
     * Converts the internal minor units representation back to a BigDecimal.
     *
     * @return The monetary amount as BigDecimal.
     */
    public BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(minorUnits)
                         .movePointLeft(currencyUnit.getDefaultFractionDigits())
                         .setScale(currencyUnit.getDefaultFractionDigits(), RoundingMode.UNNECESSARY);
    }

    /**
     * Adds another Money instance to this one. Currencies must match.
     *
     * @param other The Money instance to add.
     * @return A new Money instance representing the sum.
     * @throws IllegalArgumentException if currencies do not match or if overflow occurs.
     */
    public Money add(Money other) {
        if (!isSameCurrency(other)) {
            throw new IllegalArgumentException("Cannot add different currencies: " + this.currencyUnit + " and " + other.currencyUnit);
        }
        long resultMinorUnits = Math.addExact(this.minorUnits, other.minorUnits); // Throws ArithmeticException on overflow
        return new Money(resultMinorUnits, this.currencyUnit);
    }

    /**
     * Subtracts another Money instance from this one. Currencies must match.
     *
     * @param other The Money instance to subtract.
     * @return A new Money instance representing the difference.
     * @throws IllegalArgumentException if currencies do not match or if overflow occurs.
     */
    public Money subtract(Money other) {
        if (!isSameCurrency(other)) {
            throw new IllegalArgumentException("Cannot subtract different currencies: " + this.currencyUnit + " and " + other.currencyUnit);
        }
        long resultMinorUnits = Math.subtractExact(this.minorUnits, other.minorUnits); // Throws ArithmeticException on overflow
        return new Money(resultMinorUnits, this.currencyUnit);
    }

    /**
     * Checks if this Money instance has the same currency as another.
     *
     * @param other The other Money instance.
     * @return true if currencies are the same, false otherwise.
     */
    public boolean isSameCurrency(Money other) {
        return this.currencyUnit.equals(other.currencyUnit);
    }

    /**
     * Checks if this Money instance is greater than or equal to another.
     * Currencies must match.
     */
    public boolean greaterThanOrEqual(Money other) {
        if (!isSameCurrency(other)) {
            throw new IllegalArgumentException("Cannot compare different currencies: " + this.currencyUnit + " and " + other.currencyUnit);
        }
        return this.minorUnits >= other.minorUnits;
    }

    /**
     * Checks if this Money instance is strictly greater than another.
     * Currencies must match.
     */
    public boolean greaterThan(Money other) {
        if (!isSameCurrency(other)) {
            throw new IllegalArgumentException("Cannot compare different currencies: " + this.currencyUnit + " and " + other.currencyUnit);
        }
        return this.minorUnits > other.minorUnits;
    }

    @Override
    public int compareTo(Money other) {
        if (!isSameCurrency(other)) {
            throw new IllegalArgumentException("Cannot compare different currencies: " + this.currencyUnit + " and " + other.currencyUnit);
        }
        return Long.compare(this.minorUnits, other.minorUnits);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return minorUnits == money.minorUnits && Objects.equals(currencyUnit, money.currencyUnit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minorUnits, currencyUnit);
    }

    @Override
    public String toString() {
        return String.format("%s %.2f", currencyUnit.getCode(), toBigDecimal());
    }
}
EOL

# Create LedgerApp.java (main application)
cat <<EOL > "$PROJECT_ROOT/src/main/java/com/mybank/ledger/LedgerApp.java"
package com.mybank.ledger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class LedgerApp {

    // Simple in-memory ledger for demonstration
    private static final Map<String, Money> accountBalances = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("----------------------------------------------");
        System.out.println("  MyBank Ledger System - Day 6: Currency & Scaling Units ");
        System.out.println("----------------------------------------------");

        // Initialize accounts
        initializeAccount("Alice", CurrencyUnit.USD, new BigDecimal("1000.00"));
        initializeAccount("Bob", CurrencyUnit.EUR, new BigDecimal("500.50"));
        initializeAccount("Charlie", CurrencyUnit.JPY, new BigDecimal("12345")); // JPY has 0 fraction digits
        initializeAccount("David", CurrencyUnit.BTC, new BigDecimal("0.51234567")); // BTC with 8 fraction digits

        displayBalances();

        // --- Demo Transactions ---
        System.out.println("\\n--- Processing Transactions ---");

        // 1. Valid Deposit (USD)
        processTransaction("Alice", Money.of(new BigDecimal("250.75"), CurrencyUnit.USD), "deposit");

        // 2. Valid Withdrawal (EUR)
        processTransaction("Bob", Money.of(new BigDecimal("100.25"), CurrencyUnit.EUR), "withdraw");

        // 3. Attempt to add different currencies (should fail)
        System.out.println("\\nAttempting invalid transaction: Alice tries to deposit EUR into USD account...");
        try {
            processTransaction("Alice", Money.of(new BigDecimal("50.00"), CurrencyUnit.EUR), "deposit");
        } catch (IllegalArgumentException e) {
            System.out.println("  -> ERROR: " + e.getMessage());
        }

        // 4. Valid Deposit (JPY)
        processTransaction("Charlie", Money.of(new BigDecimal("5000"), CurrencyUnit.JPY), "deposit");

        // 5. Valid Withdrawal (BTC)
        processTransaction("David", Money.of(new BigDecimal("0.12345678"), CurrencyUnit.BTC), "withdraw");
        processTransaction("David", Money.of(new BigDecimal("0.00000001"), CurrencyUnit.BTC), "withdraw"); // Smallest unit

        // 6. Attempt insufficient funds (USD)
        System.out.println("\\nAttempting invalid transaction: Alice tries to withdraw too much USD...");
        try {
            processTransaction("Alice", Money.of(new BigDecimal("2000.00"), CurrencyUnit.USD), "withdraw");
        } catch (IllegalStateException e) {
            System.out.println("  -> ERROR: " + e.getMessage());
        }

        displayBalances();

        System.out.println("\\n----------------------------------------------");
        System.out.println("  Ledger System Demo Complete. ");
        System.out.println("----------------------------------------------");
    }

    private static void initializeAccount(String accountName, CurrencyUnit currency, BigDecimal initialAmount) {
        Money initialMoney = Money.of(initialAmount, currency);
        accountBalances.put(accountName, initialMoney);
        System.out.printf("Initialized account '%s' with %s%n", accountName, initialMoney.toString());
    }

    private static void processTransaction(String accountName, Money transactionAmount, String type) {
        System.out.printf("Processing %s for '%s': %s%n", type, accountName, transactionAmount.toString());
        Money currentBalance = accountBalances.get(accountName);

        if (currentBalance == null) {
            System.out.println("  -> ERROR: Account not found: " + accountName);
            return;
        }

        if (!currentBalance.isSameCurrency(transactionAmount)) {
            throw new IllegalArgumentException(
                String.format("Transaction currency (%s) does not match account currency (%s) for account '%s'.",
                    transactionAmount.getCurrencyUnit().getCode(), currentBalance.getCurrencyUnit().getCode(), accountName));
        }

        Money newBalance;
        if ("deposit".equalsIgnoreCase(type)) {
            newBalance = currentBalance.add(transactionAmount);
        } else if ("withdraw".equalsIgnoreCase(type)) {
            if (!currentBalance.greaterThanOrEqual(transactionAmount)) {
                throw new IllegalStateException(
                    String.format("Insufficient funds for account '%s'. Current: %s, Attempted withdrawal: %s",
                        accountName, currentBalance.toString(), transactionAmount.toString()));
            }
            newBalance = currentBalance.subtract(transactionAmount);
        } else {
            System.out.println("  -> ERROR: Invalid transaction type: " + type);
            return;
        }

        accountBalances.put(accountName, newBalance);
        System.out.printf("  -> Success! New balance for '%s': %s%n", accountName, newBalance.toString());
    }

    private static void displayBalances() {
        System.out.println("\\n--- Current Account Balances ---");
        if (accountBalances.isEmpty()) {
            System.out.println("  No accounts found.");
            return;
        }
        accountBalances.forEach((name, balance) -> System.out.printf("  %-10s: %s%n", name, balance.toString()));
        System.out.println("--------------------------------");
    }
}
EOL

# --- Verify all expected files were generated ---
display_message "Verifying generated files..."
EXPECTED_FILES=(
  "$PROJECT_ROOT/pom.xml"
  "$PROJECT_ROOT/src/main/java/com/mybank/ledger/CurrencyUnit.java"
  "$PROJECT_ROOT/src/main/java/com/mybank/ledger/Money.java"
  "$PROJECT_ROOT/src/main/java/com/mybank/ledger/LedgerApp.java"
)
MISSING=0
for f in "${EXPECTED_FILES[@]}"; do
  if [[ -f "$f" ]]; then
    echo "  OK: $f"
  else
    echo "  MISSING: $f"
    MISSING=1
  fi
done
if [[ $MISSING -ne 0 ]]; then
  display_message "ERROR: Some expected files were not generated."
  exit 1
fi
echo "All expected files present."

# --- 2. Build the Project ---
display_message "2. Building the project with Maven..."
cd "$PROJECT_ROOT" || exit
mvn clean install
if [ $? -ne 0 ]; then
    display_message "ERROR: Maven build failed!"
    exit 1
fi
cd "$SCRIPT_DIR" || exit

# Find the generated JAR file
JAR_FILE=$(find "$PROJECT_ROOT/target" -name "ledger-system-*.jar" | grep -v "javadoc" | grep -v "sources" | head -n 1)
if [ -z "$JAR_FILE" ]; then
    display_message "ERROR: JAR file not found after build!"
    exit 1
fi
# Use path relative to SCRIPT_DIR for Docker build context
JAR_RELATIVE="${PROJECT_NAME}/target/$(basename "$JAR_FILE")"

# --- 3. Run and Verify Functionality (Without Docker) ---
display_message "3. Running the application without Docker..."
java -jar "$JAR_FILE"
if [ $? -ne 0 ]; then
    display_message "ERROR: Application run failed!"
    exit 1
fi

# --- 4. Create startup scripts (run with full path or from project dir) ---
display_message "4. Creating startup scripts..."
JAR_NAME="ledger-system-1.0-SNAPSHOT.jar"
cat <<STARTOF > "$SCRIPT_DIR/start.sh"
#!/bin/bash
set -e
SCRIPT_DIR="\$(cd "\$(dirname "\${BASH_SOURCE[0]}")" && pwd)"
cd "\$SCRIPT_DIR/$PROJECT_NAME"
if [[ ! -f target/$JAR_NAME ]]; then
  echo "[ERROR] target/$JAR_NAME not found. Run setup.sh first." >&2
  exit 1
fi
exec java -jar "target/$JAR_NAME" "\$@"
STARTOF
cat <<BUILDOF > "$SCRIPT_DIR/build.sh"
#!/bin/bash
set -e
SCRIPT_DIR="\$(cd "\$(dirname "\${BASH_SOURCE[0]}")" && pwd)"
if [[ ! -d "\$SCRIPT_DIR/$PROJECT_NAME" ]] || [[ ! -f "\$SCRIPT_DIR/$PROJECT_NAME/pom.xml" ]]; then
  echo "[ERROR] Project not found. Run setup.sh first." >&2
  exit 1
fi
cd "\$SCRIPT_DIR/$PROJECT_NAME"
mvn clean install
echo "[SUCCESS] Build complete."
BUILDOF
cat <<STOPOF > "$SCRIPT_DIR/stop.sh"
#!/bin/bash
SCRIPT_DIR="\$(cd "\$(dirname "\${BASH_SOURCE[0]}")" && pwd)"
for c in $DOCKER_CONTAINER_NAME; do
  if docker ps -a --format '{{.Names}}' 2>/dev/null | grep -qx "\$c"; then
    echo "Stopping and removing: \$c"
    docker rm -f "\$c" 2>/dev/null || true
  fi
done
echo "Containers cleaned up."
STOPOF
chmod +x "$SCRIPT_DIR/start.sh" "$SCRIPT_DIR/build.sh" "$SCRIPT_DIR/stop.sh"
echo "Startup scripts created: start.sh, build.sh, stop.sh"

# --- Run startup script with full path to verify ---
display_message "Running startup script (full path)..."
if [[ ! -x "$SCRIPT_DIR/start.sh" ]]; then
  display_message "ERROR: start.sh not executable or missing."
  exit 1
fi
"$SCRIPT_DIR/start.sh"

# --- 5. Check for duplicate services (Docker) ---
display_message "5. Checking for duplicate Docker containers..."
if command -v docker &>/dev/null && docker info &>/dev/null 2>&1; then
  if docker ps -a --format '{{.Names}}' 2>/dev/null | grep -qx "$DOCKER_CONTAINER_NAME"; then
    echo "Removing existing container: $DOCKER_CONTAINER_NAME"
    docker rm -f "$DOCKER_CONTAINER_NAME" 2>/dev/null || true
  fi
fi

# --- 6. Build and Run with Docker ---
display_message "6. Building Docker image..."
cat <<EOL > "$SCRIPT_DIR/Dockerfile"
# Use a slim OpenJDK base image
FROM eclipse-temurin:11-jre

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the Maven build output (relative to build context = day6)
COPY $JAR_RELATIVE /app/app.jar

# Define the command to run your application
CMD ["java", "-jar", "app.jar"]
EOL

cd "$SCRIPT_DIR" || exit
docker build -t "$DOCKER_IMAGE_NAME" .
if [ $? -ne 0 ]; then
    display_message "ERROR: Docker image build failed!"
    exit 1
fi

display_message "7. Running the application with Docker..."
docker run --name "$DOCKER_CONTAINER_NAME" "$DOCKER_IMAGE_NAME"
if [ $? -ne 0 ]; then
    display_message "ERROR: Docker container run failed!"
    exit 1
fi

display_message "8. Demo and Verification Complete!"
echo "To run again (full path): $SCRIPT_DIR/start.sh"
echo "To stop Docker: $SCRIPT_DIR/stop.sh"