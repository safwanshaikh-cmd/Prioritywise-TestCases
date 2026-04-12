# TC_406 Fix #29 - Add Toaster Verification to Click Attempts

## ✅ Issue Fixed: Click Attempts Not Verified

**Problem**: After clicking Yes button, the code immediately set `clicked = true` without verifying if the click actually triggered the backend removal action. This prevented other click approaches from being tried.

**Evidence from Logs**:
```
INFO: ✅ Clicked Yes button using JavaScript (Attempt 1)
INFO: Clicked Yes on removal confirmation
```
- Only "Attempt 1" appeared in logs
- Attempts 2, 3, 4, 5 were never executed
- Toaster "Removed from favourites" never appeared
- Book remained in favourites

**Root Cause**: Line 989 in the old code:
```java
((JavascriptExecutor) driver).executeScript("arguments[0].click();", yesBtn);
LOGGER.info("✅ Clicked Yes button using JavaScript (Attempt 1)");
clicked = true;  // ← BUG: Sets clicked=true WITHOUT verifying the click worked!
```

The code assumed the click succeeded just because it didn't throw an exception. But the JavaScript click can execute without triggering the actual click event handler!

---

## 🔧 Fix Applied

### Key Changes:

**1. Added isToasterVisible() Helper Method** (NEW):
```java
/**
 * Helper method to check if the "Removed from favourites" toaster is visible
 * Used to verify if a click attempt actually triggered the removal action
 * @return true if toaster is visible, false otherwise
 */
private boolean isToasterVisible() {
    try {
        WebElement toaster = driver.findElement(By.xpath(
            "//div[contains(@data-testid, 'toastText1')][contains(text(), 'Removed from favourites')]"));
        return toaster.isDisplayed();
    } catch (Exception e) {
        return false;
    }
}
```

**2. Modified All 5 Click Attempts to Verify Success**:

**Old Approach** (BROKEN):
```java
// Attempt 1
try {
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", yesBtn);
    LOGGER.info("✅ Clicked Yes button using JavaScript (Attempt 1)");
    clicked = true;  // ← Assumes success without verification!
} catch (Exception e1) {
    // Exception handling
}
```

**New Approach** (FIXED):
```java
// Attempt 1: JavaScript click on parent element
try {
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", yesBtn);
    LOGGER.info("✅ Attempt 1: JavaScript click on parent");

    // Wait briefly and check if toaster appeared
    Thread.sleep(2000);
    if (isToasterVisible()) {
        LOGGER.info("✅ Attempt 1 SUCCESSFUL - Toaster detected!");
        clicked = true;  // ← Only set true if toaster actually appears!
    } else {
        LOGGER.warning("⚠️ Attempt 1 did not trigger toaster, trying next approach...");
        // clicked stays false, so next attempt will run
    }
} catch (Exception e1) {
    LOGGER.log(Level.WARNING, "Attempt 1 failed: " + e1.getMessage());
}
```

---

## 📋 Complete 5-Attempt Strategy

Now all 5 approaches will be tried until one succeeds (toaster appears):

**Attempt 1: JavaScript click on parent element**
- Clicks the `<div tabindex="0">` parent
- Waits 2 seconds
- Checks if toaster appeared
- If yes: SUCCESS! Stop trying other approaches
- If no: Try Attempt 2

**Attempt 2: JavaScript click on inner div** ← KEY NEW APPROACH!
- Clicks the inner `<div dir="auto">Yes</div>` text element
- Sometimes text elements handle the click, not the parent
- Waits 2 seconds and checks for toaster
- If yes: SUCCESS! Stop
- If no: Try Attempt 3

**Attempt 3: Standard Selenium click**
- Uses `yesBtn.click()` method
- Native Selenium click
- Waits 2 seconds and checks for toaster
- If yes: SUCCESS! Stop
- If no: Try Attempt 4

**Attempt 4: Actions class**
- Uses `Actions.moveToElement(yesBtn).click().perform()`
- Simulates real user mouse interaction
- Waits 2 seconds and checks for toaster
- If yes: SUCCESS! Stop
- If no: Try Attempt 5

**Attempt 5: MouseEvent dispatching**
- Creates and dispatches native MouseEvent
- Bypasses framework abstractions
- Waits 2 seconds and checks for toaster
- If yes: SUCCESS!
- If no: All 5 attempts failed

---

## 🧪 Expected Test Output

### Before Fix #29 (BROKEN):
```
INFO: Found Yes button within dialog context (Method 0)
INFO: ✅ Clicked Yes button using JavaScript (Attempt 1)
INFO: Clicked Yes on removal confirmation
INFO: Waiting for "Removed from favourites" toaster (max 10 seconds)...
WARNING: ⚠️ "Removed from favourites" toaster not found after 10 seconds
SEVERE: Yes button not found using any method
FAILED: Should show 'Removed from favourites' toaster
```
- Only Attempt 1 logged
- No further attempts made
- Toaster never appeared
- Test failed

### After Fix #29 (FIXED):
```
INFO: Found Yes button within dialog context (Method 0)
INFO: ✅ Attempt 1: JavaScript click on parent
WARNING: ⚠️ Attempt 1 did not trigger toaster, trying next approach...
INFO: ✅ Attempt 2: JavaScript click on inner div
WARNING: ⚠️ Attempt 2 did not trigger toaster, trying next approach...
INFO: ✅ Attempt 3: Standard click
INFO: ✅ Attempt 3 SUCCESSFUL - Toaster detected!
INFO: Clicked Yes on removal confirmation
INFO: ✅ Found "Removed from favourites" toaster notification ✅
PASSED: TC_406 ✅
```
- All 5 attempts available
- Attempts 1, 2 tried and logged
- Attempt 3 succeeded
- Toaster appeared
- Test passed

---

## 🎯 Why This Will Work

**The Problem Chain**:
```
JavaScript click() executed
    ↓
No exception thrown (doesn't mean it worked!)
    ↓
clicked = true (WRONG - assumed success)
    ↓
Attempts 2-5 skipped (if (!clicked) is false)
    ↓
Wait for toaster (max 10 seconds)
    ↓
Toaster never appears
    ↓
Test FAILS
```

**The Fixed Chain**:
```
JavaScript click() executed
    ↓
Wait 2 seconds
    ↓
Check: isToasterVisible()?
    ↓ NO → clicked stays false, try Attempt 2
JavaScript on inner div
    ↓
Wait 2 seconds
    ↓
Check: isToasterVisible()?
    ↓ NO → clicked stays false, try Attempt 3
Standard click()
    ↓
Wait 2 seconds
    ↓
Check: isToasterVisible()?
    ↓ YES → clicked = true, stop trying
    ↓
Test PASSES
```

---

## 🚀 How to Run

```bash
# Compile project
mvn compile

# Run TC_406
mvn test -Dtest=FavouritesManagementTests#verifyRemoveBookFromFavourites
```

---

## ✨ Summary

**Fix #29 - Click Verification with Toaster Detection**:
- ✅ Added `isToasterVisible()` helper method
- ✅ Modified all 5 click attempts to verify success
- ✅ Each attempt now checks for toaster after clicking
- ✅ Only sets `clicked = true` if toaster appears
- ✅ Ensures all 5 approaches are tried until one works
- ✅ Detailed logging shows which attempt succeeded
- ✅ Fixes the bug where only Attempt 1 was being tried

**Complete TC_406 Fix Chain**: Fix #17 → #18 → #19 → #20 → #21 → #22 → #23 → #24 → #25 → #26 → #27 → #28 → #29

**All 13 fixes complete!**

**Status**: ✅ READY FOR TESTING
**Date**: 2026-04-12
**Files Modified**: FavouritesPage.java (clickYesOnConfirmation, added isToasterVisible helper)
**Compilation**: Need to verify with mvn compile (Java not available in WSL environment)

---

## 🔍 Key Insight

**The Bug**: We were assuming that because the JavaScript click didn't throw an exception, it must have worked. But JavaScript can execute `click()` without triggering the actual event handler!

**The Fix**: After each click attempt, wait 2 seconds and check if the toaster notification appeared. Only consider the click successful if the toaster is visible!

**This ensures all 5 click approaches are actually tried until one triggers the backend removal!**

**Expected Behavior**: One of the 5 approaches will work (most likely Attempt 2 or 3), the toaster will appear, and TC_406 will finally pass!
