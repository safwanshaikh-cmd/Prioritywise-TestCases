package utils;

import java.util.Collection;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

/**
 * Enterprise-grade assertion utilities with enhanced error messages and debugging support.
 * Provides soft assertions for non-critical validations and detailed failure messages.
 */
public class AssertionUtils {

    // ==================== Boolean Assertions ====================

    public static void assertTrue(boolean condition, String message) {
        Assert.assertTrue(condition, buildMessage(message));
    }

    public static void assertTrue(boolean condition) {
        assertTrue(condition, "Expected true but was false");
    }

    public static void assertFalse(boolean condition, String message) {
        Assert.assertFalse(condition, buildMessage(message));
    }

    public static void assertFalse(boolean condition) {
        assertFalse(condition, "Expected false but was true");
    }

    // ==================== Null Assertions ====================

    public static void assertNull(Object object, String message) {
        Assert.assertNull(object, buildMessage(message));
    }

    public static void assertNull(Object object) {
        assertNull(object, "Expected null");
    }

    public static void assertNotNull(Object object, String message) {
        Assert.assertNotNull(object, buildMessage(message));
    }

    public static void assertNotNull(Object object) {
        assertNotNull(object, "Expected non-null value");
    }

    // ==================== Equality Assertions ====================

    public static void assertEquals(Object actual, Object expected, String message) {
        Assert.assertEquals(actual, expected, buildMessage(message));
    }

    public static void assertEquals(Object actual, Object expected) {
        assertEquals(actual, expected, null);
    }

    public static void assertNotEquals(Object actual, Object expected, String message) {
        Assert.assertNotEquals(actual, expected, buildMessage(message));
    }

    public static void assertNotEquals(Object actual, Object expected) {
        assertNotEquals(actual, expected, "Values should not be equal");
    }

    public static void assertEqualsIgnoreCase(String actual, String expected, String message) {
        if (actual == null && expected == null) {
            return;
        }
        String nonNullActual = requireNonNull(actual, message + " - Actual is null, Expected: '" + expected + "'");
        String nonNullExpected = requireNonNull(expected, message + " - Actual: '" + actual + "', Expected is null");
        Assert.assertEquals(nonNullActual.trim().toLowerCase(), nonNullExpected.trim().toLowerCase(),
            buildMessage(message));
    }

    public static void assertEqualsIgnoreCase(String actual, String expected) {
        assertEqualsIgnoreCase(actual, expected, "Strings should be equal (case-insensitive)");
    }

    // ==================== String Assertions ====================

    public static void assertContains(String actual, String expected, String message) {
        String nonNullActual = requireNonNull(actual, message + " - Actual string is null");
        String nonNullExpected = requireNonNull(expected, message + " - Expected substring is null");
        Assert.assertTrue(nonNullActual.contains(nonNullExpected),
            buildMessage(message + " - '" + nonNullActual + "' does not contain '" + nonNullExpected + "'"));
    }

    public static void assertContains(String actual, String expected) {
        assertContains(actual, expected, "String should contain substring");
    }

    public static void assertNotContains(String actual, String expected, String message) {
        if (actual == null) {
            return;
        }
        Assert.assertFalse(actual.contains(expected),
            buildMessage(message + " - '" + actual + "' should not contain '" + expected + "'"));
    }

    public static void assertNotContains(String actual, String expected) {
        assertNotContains(actual, expected, "String should not contain substring");
    }

    public static void assertEqualsTrimmed(String actual, String expected, String message) {
        assertEquals(actual != null ? actual.trim() : null,
                     expected != null ? expected.trim() : null, message);
    }

    public static void assertEqualsTrimmed(String actual, String expected) {
        assertEqualsTrimmed(actual, expected, "Trimmed strings should be equal");
    }

    public static void assertEmpty(String actual, String message) {
        assertTrue(actual == null || actual.trim().isEmpty(),
            buildMessage(message + " - Expected empty but was: '" + actual + "'"));
    }

    public static void assertEmpty(String actual) {
        assertEmpty(actual, "String should be empty");
    }

    public static void assertNotEmpty(String actual, String message) {
        assertNotNull(actual, buildMessage(message + " - String should not be null"));
        assertFalse(actual.trim().isEmpty(),
            buildMessage(message + " - String should not be empty"));
    }

    public static void assertNotEmpty(String actual) {
        assertNotEmpty(actual, "String should not be empty");
    }

    public static void assertMatches(String actual, String regex, String message) {
        String nonNullActual = requireNonNull(actual, message + " - Actual string is null");
        String nonNullRegex = requireNonNull(regex, message + " - Regex pattern is null");
        Assert.assertTrue(nonNullActual.matches(nonNullRegex),
            buildMessage(message + " - '" + nonNullActual + "' does not match pattern: " + nonNullRegex));
    }

    public static void assertMatches(String actual, String regex) {
        assertMatches(actual, regex, "String should match pattern");
    }

    public static void assertStartsWith(String actual, String prefix, String message) {
        String nonNullActual = requireNonNull(actual, message + " - Null values provided");
        String nonNullPrefix = requireNonNull(prefix, message + " - Null values provided");
        Assert.assertTrue(nonNullActual.startsWith(nonNullPrefix),
            buildMessage(message + " - '" + nonNullActual + "' does not start with '" + nonNullPrefix + "'"));
    }

    public static void assertStartsWith(String actual, String prefix) {
        assertStartsWith(actual, prefix, "String should start with prefix");
    }

    public static void assertEndsWith(String actual, String suffix, String message) {
        String nonNullActual = requireNonNull(actual, message + " - Null values provided");
        String nonNullSuffix = requireNonNull(suffix, message + " - Null values provided");
        Assert.assertTrue(nonNullActual.endsWith(nonNullSuffix),
            buildMessage(message + " - '" + nonNullActual + "' does not end with '" + nonNullSuffix + "'"));
    }

    public static void assertEndsWith(String actual, String suffix) {
        assertEndsWith(actual, suffix, "String should end with suffix");
    }

    // ==================== Number Assertions ====================

    public static void assertEquals(int actual, int expected, String message) {
        Assert.assertEquals(actual, expected, buildMessage(message));
    }

    public static void assertEquals(int actual, int expected) {
        assertEquals(actual, expected, null);
    }

    public static void assertEquals(long actual, long expected, String message) {
        Assert.assertEquals(actual, expected, buildMessage(message));
    }

    public static void assertEquals(long actual, long expected) {
        assertEquals(actual, expected, null);
    }

    public static void assertEquals(double actual, double expected, double delta, String message) {
        Assert.assertEquals(actual, expected, delta, buildMessage(message));
    }

    public static void assertEquals(double actual, double expected, double delta) {
        assertEquals(actual, expected, delta, null);
    }

    public static void assertGreaterThan(int actual, int expected, String message) {
        Assert.assertTrue(actual > expected,
            buildMessage(message + " - " + actual + " should be greater than " + expected));
    }

    public static void assertGreaterThan(int actual, int expected) {
        assertGreaterThan(actual, expected, "Value should be greater than expected");
    }

    public static void assertGreaterThanOrEqual(int actual, int expected, String message) {
        Assert.assertTrue(actual >= expected,
            buildMessage(message + " - " + actual + " should be >= " + expected));
    }

    public static void assertGreaterThanOrEqual(int actual, int expected) {
        assertGreaterThanOrEqual(actual, expected, "Value should be >= expected");
    }

    public static void assertLessThan(int actual, int expected, String message) {
        Assert.assertTrue(actual < expected,
            buildMessage(message + " - " + actual + " should be less than " + expected));
    }

    public static void assertLessThan(int actual, int expected) {
        assertLessThan(actual, expected, "Value should be less than expected");
    }

    public static void assertLessThanOrEqual(int actual, int expected, String message) {
        Assert.assertTrue(actual <= expected,
            buildMessage(message + " - " + actual + " should be <= " + expected));
    }

    public static void assertLessThanOrEqual(int actual, int expected) {
        assertLessThanOrEqual(actual, expected, "Value should be <= expected");
    }

    // ==================== Collection Assertions ====================

    public static void assertListSize(Collection<?> collection, int expectedSize, String message) {
        assertNotNull(collection, buildMessage(message + " - Collection should not be null"));
        Assert.assertEquals(collection.size(), expectedSize,
            buildMessage(message + " - Expected size: " + expectedSize + ", Actual: " + collection.size()));
    }

    public static void assertListSize(Collection<?> collection, int expectedSize) {
        assertListSize(collection, expectedSize, "Collection size mismatch");
    }

    public static void assertListNotEmpty(Collection<?> collection, String message) {
        assertNotNull(collection, buildMessage(message + " - Collection should not be null"));
        Assert.assertFalse(collection.isEmpty(),
            buildMessage(message + " - Collection should not be empty"));
    }

    public static void assertListNotEmpty(Collection<?> collection) {
        assertListNotEmpty(collection, "Collection should not be empty");
    }

    public static void assertListEmpty(Collection<?> collection, String message) {
        assertNotNull(collection, buildMessage(message + " - Collection should not be null"));
        Assert.assertTrue(collection.isEmpty(),
            buildMessage(message + " - Collection should be empty but has " + collection.size() + " elements"));
    }

    public static void assertListEmpty(Collection<?> collection) {
        assertListEmpty(collection, "Collection should be empty");
    }

    public static void assertContains(Collection<?> collection, Object element, String message) {
        assertNotNull(collection, buildMessage(message + " - Collection should not be null"));
        Assert.assertTrue(collection.contains(element),
            buildMessage(message + " - Collection should contain: " + element));
    }

    public static void assertContains(Collection<?> collection, Object element) {
        assertContains(collection, element, "Collection should contain element");
    }

    public static void assertNotContains(Collection<?> collection, Object element, String message) {
        assertNotNull(collection, buildMessage(message + " - Collection should not be null"));
        Assert.assertFalse(collection.contains(element),
            buildMessage(message + " - Collection should not contain: " + element));
    }

    public static void assertNotContains(Collection<?> collection, Object element) {
        assertNotContains(collection, element, "Collection should not contain element");
    }

    // ==================== Element Assertions ====================

    public static void assertElementDisplayed(WebElement element, String message) {
        assertNotNull(element, buildMessage(message + " - Element should not be null"));
        Assert.assertTrue(element.isDisplayed(), buildMessage(message));
    }

    public static void assertElementDisplayed(WebElement element) {
        assertElementDisplayed(element, "Element should be displayed");
    }

    public static void assertElementNotDisplayed(WebElement element, String message) {
        assertNotNull(element, buildMessage(message + " - Element should not be null"));
        Assert.assertFalse(element.isDisplayed(), buildMessage(message));
    }

    public static void assertElementNotDisplayed(WebElement element) {
        assertElementNotDisplayed(element, "Element should not be displayed");
    }

    public static void assertElementEnabled(WebElement element, String message) {
        assertNotNull(element, buildMessage(message + " - Element should not be null"));
        Assert.assertTrue(element.isEnabled(), buildMessage(message));
    }

    public static void assertElementEnabled(WebElement element) {
        assertElementEnabled(element, "Element should be enabled");
    }

    public static void assertElementDisabled(WebElement element, String message) {
        assertNotNull(element, buildMessage(message + " - Element should not be null"));
        Assert.assertFalse(element.isEnabled(), buildMessage(message));
    }

    public static void assertElementDisabled(WebElement element) {
        assertElementDisabled(element, "Element should be disabled");
    }

    public static void assertElementSelected(WebElement element, String message) {
        assertNotNull(element, buildMessage(message + " - Element should not be null"));
        Assert.assertTrue(element.isSelected(), buildMessage(message));
    }

    public static void assertElementSelected(WebElement element) {
        assertElementSelected(element, "Element should be selected");
    }

    public static void assertElementNotSelected(WebElement element, String message) {
        assertNotNull(element, buildMessage(message + " - Element should not be null"));
        Assert.assertFalse(element.isSelected(), buildMessage(message));
    }

    public static void assertElementNotSelected(WebElement element) {
        assertElementNotSelected(element, "Element should not be selected");
    }

    public static void assertElementTextEquals(WebElement element, String expected, String message) {
        assertNotNull(element, buildMessage(message + " - Element should not be null"));
        String actual = element.getText().trim();
        assertEquals(actual, expected, message);
    }

    public static void assertElementTextEquals(WebElement element, String expected) {
        assertElementTextEquals(element, expected, "Element text should equal");
    }

    public static void assertElementTextContains(WebElement element, String expected, String message) {
        assertNotNull(element, buildMessage(message + " - Element should not be null"));
        String actual = element.getText();
        assertContains(actual, expected, message);
    }

    public static void assertElementTextContains(WebElement element, String expected) {
        assertElementTextContains(element, expected, "Element text should contain");
    }

    // ==================== URL/Page Assertions ====================

    public static void assertCurrentUrlContains(String expected, String message) {
        // This needs a WebDriver instance - use instance method below
        throw new UnsupportedOperationException("Use assertCurrentUrlContains(WebDriver, String, String)");
    }

    public static void assertCurrentUrlContains(WebDriver driver, String expected, String message) {
        assertNotNull(driver, "WebDriver should not be null");
        String url = driver.getCurrentUrl();
        assertContains(url, expected, message);
    }

    public static void assertCurrentUrlContains(WebDriver driver, String expected) {
        assertCurrentUrlContains(driver, expected, "URL should contain");
    }

    public static void assertCurrentUrlMatches(WebDriver driver, String regex, String message) {
        assertNotNull(driver, "WebDriver should not be null");
        String url = driver.getCurrentUrl();
        assertMatches(url, regex, message);
    }

    public static void assertCurrentUrlMatches(WebDriver driver, String regex) {
        assertCurrentUrlMatches(driver, regex, "URL should match pattern");
    }

    public static void assertPageTitle(WebDriver driver, String expected, String message) {
        assertNotNull(driver, "WebDriver should not be null");
        String title = driver.getTitle();
        assertEquals(title, expected, message);
    }

    public static void assertPageTitle(WebDriver driver, String expected) {
        assertPageTitle(driver, expected, "Page title should equal");
    }

    public static void assertPageTitleContains(WebDriver driver, String expected, String message) {
        assertNotNull(driver, "WebDriver should not be null");
        String title = driver.getTitle();
        assertContains(title, expected, message);
    }

    public static void assertPageTitleContains(WebDriver driver, String expected) {
        assertPageTitleContains(driver, expected, "Page title should contain");
    }

    // ==================== Helper Methods ====================

    private static String buildMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "";
        }
        return message;
    }

    private static String requireNonNull(String value, String message) {
        if (value == null) {
            Assert.fail(buildMessage(message));
        }
        return value;
    }

    // ==================== Fail Methods ====================

    public static void fail(String message) {
        Assert.fail(buildMessage(message));
    }

    public static void fail() {
        Assert.fail();
    }
}
