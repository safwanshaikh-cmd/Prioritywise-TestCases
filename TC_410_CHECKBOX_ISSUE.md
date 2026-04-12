# TC_410 Checkbox Issue - Selection Mode Not Working

## ❌ Issue: Checkboxes Not Appearing on Books

**User Feedback**: "It is not displaying the Selected Books and Checkbox on those books"

**Test Logs**:
```
INFO: Filter button clicked
Selection Mode Visible: true
INFO: Found 5 book items
INFO: Selected book at index: 0  ← Says "Selected" but checkbox didn't appear!
INFO: Selected book at index: 1
Selected Count Before Removal: 2  ← Thinks 2 are selected
```

---

## 🔍 Root Cause Analysis

### What's Happening:

The `selectBookByIndex()` method (FavouritesPage.java, line 194-214) tries to find a checkbox within each book:

```java
public void selectBookByIndex(int index) {
    WebElement book = books.get(index);
    try {
        // Try to find checkbox
        WebElement checkbox = book.findElement(By.xpath(
            ".//*[@role='checkbox' or @aria-checked or (@tabindex='0' and contains(@class,'css-g5y9jx'))][1]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
    } catch (Exception e) {
        book.click(); // ← FALLBACK: Clicks book card instead!
    }
    LOGGER.info("Selected book at index: " + index);
}
```

### The Problem:

1. Filter button clicked → Selection mode appears ✅
2. `selectBookByIndex(0)` called → Tries to find checkbox ❌
3. Checkbox NOT found (XPath doesn't match) ❌
4. Falls back to `book.click()` → Clicks book card instead ❌
5. Logs "Selected book at index: 0" → But no checkbox appeared! ❌
6. Test thinks book is selected, but it's not ❌

---

## 🎯 Why Checkboxes Aren't Found

### Possible Reasons:

**1. Checkboxes Take Time to Appear**
- When filter button is clicked, checkboxes might need time to render
- No explicit wait after entering selection mode
- Code tries to find checkboxes immediately (line 196)

**2. Checkbox XPath Doesn't Match**
```java
".//*[@role='checkbox' or @aria-checked or (@tabindex='0' and contains(@class,'css-g5y9jx'))][1]"
```
- This XPath might not match the actual checkbox elements
- Checkboxes might have different attributes or structure
- Need to inspect actual HTML to find correct locator

**3. Checkboxes Are Outside Book Element**
- Checkboxes might be siblings of book elements, not children
- `book.findElement()` only searches within book element
- Should search entire page or use different approach

**4. Selection Mode Works Differently**
- Application might use a different selection mechanism
- Maybe clicking the book card IS how you select it
- But visual feedback (checkbox) is missing

---

## 🔧 Potential Fixes

### Fix #1: Add Explicit Wait for Checkboxes (Low Priority)

Before selecting books, wait for checkboxes to appear:

```java
public void selectBookByIndex(int index) {
    try {
        List<WebElement> books = getBookItems();

        // NEW: Wait for checkboxes to appear after entering selection mode
        Thread.sleep(2000);  // Or use WebDriverWait

        if (index < books.size()) {
            WebElement book = books.get(index);
            // ... rest of method
        }
    }
}
```

### Fix #2: Enhanced Checkbox Finding (Medium Priority)

Try multiple methods to find checkboxes:

```java
WebElement checkbox = null;

// Method 1: Original XPath
try {
    checkbox = book.findElement(By.xpath(
        ".//*[@role='checkbox' or @aria-checked or (@tabindex='0' and contains(@class,'css-g5y9jx'))][1]"));
} catch (Exception e1) {
    LOGGER.log(Level.FINE, "Method 1 failed: " + e1.getMessage());
}

// Method 2: Look for input[type="checkbox"]
if (checkbox == null) {
    try {
        checkbox = book.findElement(By.xpath(".//input[@type='checkbox']"));
    } catch (Exception e2) {
        LOGGER.log(Level.FINE, "Method 2 failed: " + e2.getMessage());
    }
}

// Method 3: Look for checkbox class patterns
if (checkbox == null) {
    try {
        checkbox = book.findElement(By.xpath(".//*[contains(@class, 'checkbox')]"));
    } catch (Exception e3) {
        LOGGER.log(Level.FINE, "Method 3 failed: " + e3.getMessage());
    }
}

if (checkbox != null) {
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
} else {
    LOGGER.warning("⚠️ Checkbox not found for book at index " + index);
    // Don't fall back to book.click() - it's not selecting!
}
```

### Fix #3: Verify Selection Actually Worked (High Priority)

After trying to select, verify the book is actually selected:

```java
public void selectBookByIndex(int index) {
    // ... selection code ...

    // NEW: Verify selection worked
    boolean isSelected = isBookSelectedAtIndex(index);
    if (!isSelected) {
        LOGGER.warning("⚠️ Book at index " + index + " was NOT selected!");
        // Retry or fail
    } else {
        LOGGER.info("✅ Book at index " + index + " successfully selected");
    }
}

private boolean isBookSelectedAtIndex(int index) {
    try {
        WebElement book = getBookItems().get(index);
        // Check for selected class, aria-checked="true", or visible checkbox
        String ariaChecked = book.getAttribute("aria-checked");
        return "true".equals(ariaChecked);
    } catch (Exception e) {
        return false;
    }
}
```

---

## 🚨 Immediate Action Required

**User Needs to Inspect HTML** to understand how checkboxes work:

1. Run application and click Filter button to enter selection mode
2. Open browser DevTools (F12)
3. Inspect a book element to see:
   - Do checkboxes appear in the DOM?
   - What attributes do they have?
   - Are they inside the book element or outside?
   - What changes when a book is selected?

**Example HTML to look for**:
```html
<!-- Scenario 1: Checkbox inside book -->
<div class="book-item">
  <input type="checkbox" role="checkbox" aria-checked="false" />
  <img src="..." />
  <div class="title">Book Title</div>
</div>

<!-- Scenario 2: Checkbox outside book (sibling) -->
<input type="checkbox" data-book-id="123" />
<div class="book-item" data-book-id="123">
  <img src="..." />
  <div class="title">Book Title</div>
</div>

<!-- Scenario 3: Selection via class on book -->
<div class="book-item selected" aria-selected="false">
  <img src="..." />
  <div class="title">Book Title</div>
</div>
```

---

## ✅ What Has Been Fixed So Far

**Fixed** ✅:
- TC_410 Yes button click verification (Fix #30 - `didRemovalProgress()` method)
- Now all 5 click attempts will be tried until toaster appears

**Not Fixed** ❌:
- Checkboxes not appearing in selection mode
- Books not actually being selected
- Need user to inspect HTML and provide correct checkbox locator

---

## 📝 Next Steps

1. **User**: Please inspect the HTML when selection mode is active and provide:
   - Screenshot of DevTools showing book element structure
   - HTML of a book element with checkbox (if visible)
   - HTML before and after selecting a book (to see what changes)

2. **Once HTML is provided**: I can update the checkbox finding logic with correct locators

3. **For now**: The Yes button fix will ensure that if books ARE somehow selected, the removal will work properly

---

**Status**: ⏸️ WAITING FOR USER INPUT
**Date**: 2026-04-12
**Test Case**: TC_410 (verifyRemoveSelectedBooks)
**Priority**: Medium (Yes button fixed, checkbox issue needs HTML inspection)
