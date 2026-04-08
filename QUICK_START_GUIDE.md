# Quick Start Guide - New Test Automation

## Overview

This guide helps you quickly run the newly automated test cases (TC_361 to TC_403).

---

## 🚀 Quick Setup (5 Minutes)

### 1. Update Configuration

Edit `src/test/resources/config.properties` and add:

```properties
# ================= ROLE-BASED ACCESS =================
consumer.email=your_consumer@email.com
consumer.password=YourPassword123

uploader.email=your_uploader@email.com
uploader.password=YourPassword123

# ================= SUBSCRIPTION (Optional) =================
subscription.activeEmail=your_active_subscription@email.com
subscription.activePassword=YourPassword123
```

### 2. Verify Prerequisites

```bash
# Check Java version (11+)
java -version

# Check Maven
mvn -version

# Verify tests compile
mvn clean compile test-compile
```

### 3. Run Your First Test

```bash
# Run a simple smoke test
mvn test -Dtest=RoleAccessTests#verifyCreatorMenuNotVisibleForConsumer
```

---

## 📋 Test Classes Overview

### 1. RoleAccessTests (TC_361-364)
Tests role-based access to Creator functionality.

```bash
# Run all role access tests
mvn test -Dtest=RoleAccessTests
```

**Tests:**
- Consumer cannot see "For Creators" menu
- Uploader can see "For Creators" menu
- Uploader can access Creator dashboard
- Consumer blocked from Creator URL

### 2. OfferTests (TC_372-381)
Tests offer page and subscription entry points.

```bash
# Run all offer tests
mvn test -Dtest=OfferTests
```

**Tests:**
- Navigate to "Get 80% Off" page
- CTA button visibility
- Payment flow navigation
- Double-click handling

### 3. SubscriptionManagementTests (TC_388-403)
Tests subscription lifecycle and cancellation.

```bash
# Run all subscription tests
mvn test -Dtest=SubscriptionManagementTests
```

**Tests:**
- Active plan display
- Plan cancellation flow
- Access until expiry
- State persistence

### 4. HamburgerMenuBehaviorTests (TC_365, 371)
Tests menu interaction behavior.

```bash
# Run all hamburger menu tests
mvn test -Dtest=HamburgerMenuBehaviorTests
```

**Tests:**
- Menu closes on outside click
- Hamburger visible on all pages

---

## 🎯 Common Use Cases

### Run All New Tests

```bash
mvn test -Dtest=RoleAccessTests,OfferTests,SubscriptionManagementTests,HamburgerMenuBehaviorTests
```

### Run Specific Priority Range

```bash
# Run role access tests (TC_361-364)
mvn test -Dtest=RoleAccessTests

# Run subscription tests (TC_388-403)
mvn test -Dtest=SubscriptionManagementTests
```

### Run in Parallel (Faster)

```bash
# Run all tests in parallel (requires testng.xml configuration)
mvn test -Dparallel=all
```

### Skip Tests Requiring Special Accounts

Tests will automatically skip if required credentials are missing:

```bash
# Will skip if uploader.email not configured
mvn test -Dtest=RoleAccessTests#verifyCreatorMenuVisibleForUploader
```

---

## 🔧 Troubleshooting

### Test Skipped: "Set consumer.email in config.properties"

**Solution:** Add the required credentials to `config.properties`:
```properties
consumer.email=your_email@example.com
consumer.password=YourPassword123
```

### Element Not Found Errors

**Cause:** UI may have changed or locator needs update.

**Solution:**
1. Check if the element exists in the current UI
2. Update locator in the appropriate Page class
3. Run test again

### Timeout Errors

**Cause:** Slow network or page load.

**Solution:**
1. Increase timeout in `config.properties`:
```properties
explicitWait=20  # Increase from 15
```
2. Or check if the application is running slowly

### Tests Pass Locally but Fail in CI

**Cause:** CI environment may have different screen size, speed, or configuration.

**Solution:**
1. Check CI logs for specific errors
2. Ensure CI has required test data accounts
3. Verify CI is using correct config.properties

---

## 📊 Test Reports

### View HTML Reports

After running tests, open:

```bash
# Default location
target/surefire-reports/index.html

# Or with ExtentReports (if configured)
target/extent-reports/*.html
```

### View Console Output

```bash
# Run with verbose output
mvn test -Dtest=RoleAccessTests -X
```

---

## 🔐 Security Best Practices

### Don't Commit Credentials

**Always use environment variables or separate config files:**

```bash
# Option 1: Environment variables
export CONSUMER_EMAIL="your_email@example.com"
export CONSUMER_PASSWORD="YourPassword123"
mvn test

# Option 2: Separate config file
cp config.properties.example config.local.properties
# Add config.local.properties to .gitignore
```

### Update .gitignore

```
# Local configuration
config.local.properties
*.local.properties

# Test reports
target/test-classes/config.properties
```

---

## 📝 Next Steps

1. ✅ Run tests locally to verify setup
2. ✅ Configure CI/CD pipeline
3. ✅ Set up test data accounts
4. ✅ Review and update locators if UI has changed
5. ✅ Add new tests following the established patterns

---

## 🆘 Need Help?

### Check Documentation

- Full details: `TEST_AUTOMATION_DELIVERABLE.md`
- Existing tests: `src/test/java/tests/`
- Page objects: `src/test/java/pages/`

### Common Patterns

**Login as specific role:**
```java
private void loginAsConsumer() {
    String email = ConfigReader.getProperty("consumer.email");
    String password = ConfigReader.getProperty("consumer.password");
    login.openLogin();
    login.loginUser(email, password);
    login.clickNextAfterLogin();
    dashboard.waitForPageReady();
}
```

**Navigate menu:**
```java
dashboard.openSideMenu();
dashboard.clickSideMenuItemAndCaptureUrl("home");
```

**Verify subscription:**
```java
String status = subscription.getPlanStatus();
boolean isActive = subscription.isPlanActive();
```

---

## ✨ Framework Features

All new tests leverage existing framework capabilities:

- ✅ Reusable BaseTest/BasePage classes
- ✅ Explicit waits (no Thread.sleep)
- ✅ Automatic retry on failure
- ✅ Comprehensive logging
- ✅ Screenshot capture on failure
- ✅ Multiple locator fallbacks
- ✅ JS click for stubborn elements

---

**Version:** 1.0
**Last Updated:** 2025-04-08
