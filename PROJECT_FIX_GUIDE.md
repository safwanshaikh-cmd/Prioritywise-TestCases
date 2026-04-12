# Project Issues - Comprehensive Fix Guide

## 🔍 Diagnostic Steps

### 1. Check for Common Issues

```bash
# Check if FavouritesPage.java exists
ls -la src/test/java/pages/FavouritesPage.java

# Check if FavouritesManagementTests.java exists
ls -la src/test/java/tests/FavouritesManagementTests.java

# Count methods in FavouritesPage
grep -c "public void\|public boolean\|public int\|public String" src/test/java/pages/FavouritesPage.java

# Count test methods in FavouritesManagementTests
grep -c "@Test" src/test/java/tests/FavouritesManagementTests.java
```

### 2. Known Issues and Fixes

#### Issue #1: Missing wait.waitForElementVisible Method
**Problem**: `wait.waitForElementVisible()` might not exist in BasePage
**Fix**: Use explicit WebDriverWait instead

#### Issue #2: Element Locator Issues
**Problem**: XPath selectors might not match actual HTML
**Fix**: Use more flexible locators with fallbacks

#### Issue #3: Thread.sleep Issues
**Problem**: Hard-coded waits can cause flaky tests
**Fix**: Replace with WebDriverWait where possible

#### Issue #4: JavaScriptExecutor Scoping
**Problem**: jsClick() might not handle all element types
**Fix**: Add proper error handling and fallback

## 🛠️ Common Fixes

### Fix #1: Update Element Locators
Current locators might be too specific. Add fallbacks:

```java
// Instead of single XPath, use multiple options
private static final By FAVOURITES_MENU = By.xpath(
    "//div[text()='Favourites'] | " +
    "//div[contains(text(),'Favourites')] | " +
    "//*[contains(@class,'favourite') or contains(@class,'favorite')]"
);
```

### Fix #2: Add Robust Wait Methods
```java
private WebElement waitForElement(By locator, int timeoutSeconds) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    return wait.until(ExpectedConditions.elementToBeClickable(locator));
}
```

### Fix #3: Improve Error Handling
```java
public void clickRemoveIconAtIndex(int index) {
    try {
        // ... existing code ...
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Failed at index " + index + ": " + e.getMessage());
        // Take screenshot for debugging
        takeScreenshot("remove_icon_error_" + index);
        throw e; // Re-throw to fail the test
    }
}
```

## 📋 Quick Fix Checklist

### FavouritesPage.java
- [ ] All locators have fallback XPaths
- [ ] All methods have try-catch blocks
- [ ] All Thread.sleep replaced with WebDriverWait where possible
- [ ] All logging uses proper Level constants
- [ ] All methods return appropriate default values on error

### FavouritesManagementTests.java
- [ ] All tests have @Test annotation with priority
- [ ] All tests have description
- [ ] All tests use retryAnalyzer
- [ ] All assertions have meaningful messages
- [ ] All tests clean up after themselves

## 🚀 Pre-Flight Checks

### 1. Verify Page Object Methods
```bash
# List all public methods in FavouritesPage
grep "public.*(" src/test/java/pages/FavouritesPage.java | grep -v "//"
```

Expected methods:
- Navigation: clickFavouritesMenu, isFavouritesPageDisplayed
- Search: searchBook, searchBookOrAuthor, getSearchResultsCount
- Filter: clickFilterButton, isFilterActionBarDisplayed
- Books: getBookItems, getFavouriteBooksCount, addBookToFavourites, removeBookFromFavourites
- Selection: selectMultipleBooks, clickSelectAll, clickDeselectAll
- Bulk: clickRemoveSelected, clickRemoveSelectedWithConfirmation
- Dialog: isRemoveConfirmationDialogDisplayed, clickYesOnConfirmation, clickNoOnConfirmation
- Empty: isNoFavouritesMessageDisplayed, getNoFavouritesMessage, isBrowseButtonDisplayed

### 2. Verify Test Methods
```bash
# List all test methods
grep "@Test" src/test/java/tests/FavouritesManagementTests.java
```

Expected: 15 tests (TC_404 to TC_418)

### 3. Verify Imports
```bash
# Check imports in both files
head -20 src/test/java/pages/FavouritesPage.java
head -20 src/test/java/tests/FavouritesManagementTests.java
```

## 🔧 Most Likely Issues & Fixes

### Issue: waitForElementVisible Not Found
**Cause**: Method doesn't exist in wait utility
**Fix**: Replace with standard WebDriverWait

```java
// OLD (might not work):
WebElement element = wait.waitForElementVisible(locator, timeout);

// NEW (always works):
WebElement element = new WebDriverWait(driver, Duration.ofSeconds(timeout))
    .until(ExpectedConditions.visibilityOfElementLocated(locator));
```

### Issue: Element Click Interception
**Cause**: Another element blocking the click
**Fix**: Use JavaScriptExecutor with scroll

```java
public void safeClick(By locator) {
    WebElement element = driver.findElement(locator);
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    Thread.sleep(500);
    element.click();
}
```

### Issue: Stale Element Reference
**Cause**: Element updated between find and click
**Fix**: Re-find element before action

```java
public void safeClickByIndex(int index) {
    List<WebElement> elements = driver.findElements(LOCATOR);
    if (index < elements.size()) {
        WebElement element = elements.get(index); // Get fresh reference
        element.click();
    }
}
```

## ✅ Validation Steps

After applying fixes:

1. **Check Syntax**
```bash
# Look for obvious syntax errors
grep -n ";\|{" src/test/java/pages/FavouritesPage.java | tail -20
```

2. **Check Balance**
```bash
# Check braces are balanced
grep -c "{" src/test/java/pages/FavouritesPage.java
grep -c "}" src/test/java/pages/FavouritesPage.java
```

3. **Check Imports**
```bash
# Verify no duplicate imports
sort src/test/java/pages/FavouritesPage.java | uniq -d
```

## 🎯 Quick Fix Command

If you want me to fix specific issues, please provide:

1. **Error message** (copy-paste the exact error)
2. **Stack trace** (if available)
3. **Which test is failing**
4. **Which line number** (if known)

This will help me provide targeted fixes instead of general suggestions.

---

**Next Steps**:
1. Run one specific test to isolate the issue
2. Share the exact error message
3. I'll provide a targeted fix

**Ready to debug! Please provide the error details.** 🚀
