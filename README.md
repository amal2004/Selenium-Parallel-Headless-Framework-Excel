# 🚀 Data Driven Java Selenium Automation Framework 

A lightweight and scalable **Java Selenium Test Automation Framework** built using industry-standard design patterns and best practices.

This framework provides:

- ✅ Selenium WebDriver
- ✅ TestNG
- ✅ Page Object Model (POM)
- ✅ Thread-Safe Driver Management
- ✅ Allure Reporting
- ✅ SLF4J + Logback Logging
- ✅ Excel Data-Driven Testing
- ✅ Screenshot Capture on Failure
- ✅ WebDriverManager Integration
- ✅ Cross Browser Execution
- ✅ Headless Execution Support
- ✅ Configuration Management
- ✅ Parallel Test Execution Ready

---

# 📌 Tech Stack

| Technology | Purpose |
|------------|----------|
| Java | Programming Language |
| Selenium WebDriver | Browser Automation |
| TestNG | Test Execution |
| Maven | Dependency Management |
| Allure | Reporting |
| SLF4J + Logback | Logging |
| Apache POI | Excel Data Handling |
| WebDriverManager | Driver Management |

---

# 🏗 Framework Architecture

```text
src
├── main
│   └── java
│       └── com.amalw
│           ├── config
│           ├── driver
│           ├── enums
│           ├── exceptions
│           ├── logging
│           ├── model
│           ├── pages
│           └── utils
│
├── test
│   └── java
│       └── com.amalw
│           ├── base
│           ├── dataproviders
│           ├── listeners
│           └── tests
│
└── resources
    ├── config.properties
    ├── testdata
    └── logback.xml
```

---

# ⚙ Framework Features

## Configuration Management

The framework uses a centralized `ConfigManager` to load and manage application properties.

### Supported Features

- Read properties from `config.properties`
- System Property Override Support
- Default Value Support
- Boolean Configuration Support
- Integer Configuration Support
- Configuration Validation
- Detailed Logging

### Example

```java
String browser = ConfigManager.get("browser");

boolean headless =
        ConfigManager.getBoolean("headless", false);

int timeout =
        ConfigManager.getInt("pageLoadTimeout", 30);
```

---

## Driver Management

The framework uses:

- ThreadLocal WebDriver
- Browser Factory Pattern
- Browser Enum Validation
- Automatic Driver Setup

### Supported Browsers

```java
CHROME
FIREFOX
EDGE
```

### Example

```java
DriverFactory.initDriver("chrome");
```

---

## Thread-Safe Execution

Each test thread receives its own WebDriver instance.

```java
private static final ThreadLocal<WebDriver> TLDRIVER
        = new ThreadLocal<>();
```

Benefits:

- Parallel Execution Ready
- No Driver Collision
- Scalable Test Execution

---

# 📄 Page Object Model (POM)

Framework follows the Page Object Model design pattern.

### BasePage

Provides reusable methods:

```java
click()
type()
getText()
navigateTo()
waitForVisibility()
```

### RegisterPage

Encapsulates registration page actions:

```java
open()
selectGender()
fillForm()
submit()
isRegistrationSuccessful()
```

---

# 👤 Test Data Management

The framework supports Excel-driven testing.

### Excel File

```text
registration-data.xlsx
```

### Sample Structure

| RunMode | FirstName | LastName | Gender | Company | Password | ConfirmPassword |
|----------|------------|------------|---------|----------|------------|------------------|
| Y | John | Smith | Male | ABC Corp | Test123 | Test123 |
| Y | David | Miller | Male | XYZ Ltd | Test123 | Test123 |

Only rows with:

```text
RunMode = Y
```

will be executed.

---

# 🏭 Test Data Factory

Dynamic test users are generated using the `UserFactory`.

### Example

```java
User user =
    UserFactory.buildUser(
        "John",
        "Smith",
        "ABC",
        "Password123",
        "Password123"
    );
```

Generated email:

```text
e2c64f11-74ff@example.com
```

Each execution creates a unique email address.

---

# 🎯 Registration Test Flow

```text
Open Registration Page
        ↓
Select Gender
        ↓
Fill Form
        ↓
Submit Form
        ↓
Validate Success Message
```

---

# 🧪 Sample Test

```java
@Test(dataProvider = "registrationData")
public void testRegistration(
        User user,
        Gender gender) {

    RegisterPage registerPage =
            new RegisterPage();

    registerPage.open()
                .selectGender(gender)
                .fillForm(user)
                .submit();

    Assert.assertTrue(
            registerPage.isRegistrationSuccessful());
}
```

---

# 📸 Screenshot Capture

Screenshots are automatically captured when a test fails.

### Storage Structure

```text
screenshots
│
├── RegistrationTest
│   ├── testRegistration_20260501.png
│   ├── testRegistration_20260502.png
```

### Benefits

- Faster Failure Analysis
- Automatically Attached to Allure
- Timestamp-Based Naming

---

# 📊 Allure Reporting

The framework integrates with Allure for rich reporting.

### Supported Features

- Test Steps
- Test Status
- Screenshots
- Severity Levels
- Stories
- Features
- Epics

### Example

```java
@Epic("User Management")
@Feature("Registration")
@Story("Successful Registration")
@Severity(SeverityLevel.CRITICAL)
```

---

# 📝 Logging Framework

Uses:

```text
SLF4J
+
Logback
```

### Features

- Dynamic Log Levels
- Thread Information
- Browser Information
- Execution Tracking
- Failure Logging

### Example Log

```text
INFO  Initializing WebDriver
INFO  Browser launched successfully
INFO  Registration successful
ERROR Failed to capture screenshot
```

---

# ⚡ Headless Execution

Enable headless mode using:

```properties
headless=true
```

Supported Browsers:

- Chrome
- Firefox
- Edge

---

# 🔧 Configuration File

### config.properties

```properties
base.url=http://localhost:5000
browser=firefox
pageLoadTimeout=30
headless=true
screenshot.dir=./screenshots
logLevel=DEBUG
```

---

# ▶ Running Tests

## Run All Tests

```bash
mvn clean test
```

---

## Run Specific Browser

```bash
mvn clean test -Dbrowser=chrome
```

```bash
mvn clean test -Dbrowser=firefox
```

```bash
mvn clean test -Dbrowser=edge
```

---

## Run Headless

```bash
mvn clean test -Dheadless=true
```

---

## Override Log Level

```bash
mvn clean test -DlogLevel=INFO
```

---

# 📈 Generate Allure Report

### Execute Tests

```bash
mvn clean test
```

### Generate Report

```bash
allure serve allure-results
```

Or

```bash
allure generate allure-results --clean
```

---

# 🛡 Framework Design Principles

- Single Responsibility Principle (SRP)
- Encapsulation
- Factory Pattern
- Page Object Model
- Thread Safety
- Reusability
- Scalability
- Maintainability

---

# 🔥 Current Framework Capabilities

✅ Selenium WebDriver Automation

✅ Page Object Model

✅ Allure Reporting

✅ Screenshot on Failure

✅ Excel Data-Driven Testing

✅ Cross Browser Support

✅ ThreadLocal Driver Management

✅ Dynamic Test Data

✅ Configurable Execution

✅ SLF4J Logging

✅ Maven Integration

✅ Headless Execution

---

# 🚀 Future Enhancements

- JSON Data Providers
- Faker Data Generation
- Selenium Grid Support
- Docker Integration
- Jenkins Pipeline
- GitHub Actions CI/CD
- REST Assured API Layer
- Retry Analyzer
- Environment Profiles
- Parallel Suite Execution

---

# 👨‍💻 Author

**Amal W**

QA Automation Engineer

Java | Selenium | TestNG | Allure | Maven | Automation Framework Design

---

# ⭐ If you found this framework useful

Give the repository a ⭐ and support the project.
