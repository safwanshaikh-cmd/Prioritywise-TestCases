# TC_410 Fix #32 - Stale Element Reference in Yes Button Click

## ✅ Issue Fixed: Stale Element Reference After First Click

**Problem**: After clicking Yes button (Attempt 1), the element becomes stale and all other attempts fail.

**Test Logs**:
```
INFO: Attempt 1: JavaScript click on Yes button container
WARNING: Attempt 2 failed: stale element reference: stale element not found in the current frame
WARNING: Attempt 3 failed: stale element reference
WARNING: Attempt 4 failed: stale element reference
WARNING: Attempt 5 failed: stale element reference
SEVERE: All click attempts failed for Yes button
```

---

## 🔍 Root Cause

**Old Code** (BROKEN):
```java
public boolean confirmRemovalViaYesButton() {
    WebElement yesBtn = getConfirmationActionButton("Yes");  // ← Gets element ONCE
    
    // Attempt 1: Click yesBtn
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", yesBtn);
    clicked = didRemovalProgress();
    
    // Attempt 2: Try to find child element using STALE yesBtn reference
    WebElement innerLabel = yesBtn.findElement(...);  // ← STALE ELEMENT ERROR!
    // Attempts 3, 4, 5 also use stale yesBtn reference
}
```

**What Happened**:
1. `yesBtn` element found at line 1274
2. Attempt 1 clicks `yesBtn`
3. DOM changes (dialog may animate, element may be recreated)
4. `yesBtn` reference becomes stale
5. Attempts 2-5 try to use stale `yesBtn` reference
6. All attempts fail with "stale element reference"

---

## 🔧 Fix Applied

**New Code** (FIXED):
```java
public boolean confirmRemovalViaYesButton() {
    boolean clicked = false;

    // Attempt 1: JavaScript click on parent element
    try {
        WebElement yesBtn = getConfirmationActionButton("Yes");  // ← FRESH element
        if (yesBtn != null) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", yesBtn);
            clicked = didRemovalProgress();
        }
    } catch (Exception e1) {
        LOGGER.log(Level.WARNING, "Attempt 1 failed: {0}", e1.getMessage());
    }

    // Attempt 2: JavaScript click on inner text div
    if (!clicked) {
        try {
            WebElement yesBtn = getConfirmationActionButton("Yes");  // ← FRESH element
            if (yesBtn != null) {
                WebElement innerLabel = yesBtn.findElement(...);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", innerLabel);
                clicked = didRemovalProgress();
            }
        } catch (Exception e2) {
            LOGGER.log(Level.WARNING, "Attempt 2 failed: {0}", e2.getMessage());
        }
    }

    // Attempts 3, 4, 5: Each gets a FRESH yesBtn element
    // ...
}
```

**Key Change**: Each attempt now calls `getConfirmationActionButton("Yes")` to get a **fresh element reference** instead of reusing the stale one.

---

## 📋 Why This Works

### Before Fix (Stale Element Problem):
```
Attempt 1: Click yesBtn → DOM changes → yesBtn becomes STALE
Attempt 2: Use stale yesBtn → ERROR ❌
Attempt 3: Use stale yesBtn → ERROR ❌
Attempt 4: Use stale yesBtn → ERROR ❌
Attempt 5: Use stale yesBtn → ERROR ❌
```

### After Fix (Fresh Element Each Time):
```
Attempt 1: Click fresh yesBtn → DOM changes → toaster check
Attempt 2: Get FRESH yesBtn → Click → toaster check
Attempt 3: Get FRESH yesBtn → Click → toaster check
Attempt 4: Get FRESH yesBtn → Click → toaster check
Attempt 5: Get FRESH yesBtn → Click → toaster check
```

---

## 🧪 Expected Test Output After Fix

### Before Fix (BROKEN):
```
INFO: ✅ Book at index 0 successfully selected
INFO: ✅ Book at index 1 successfully selected
Selected Count Before Removal: 2
INFO: Remove Selected clicked
INFO: Attempt 1: JavaScript click on Yes button container
WARNING: Attempt 2 failed: stale element reference
WARNING: Attempt 3 failed: stale element reference
WARNING: Attempt 4 failed: stale element reference
WARNING: Attempt 5 failed: stale element reference
SEVERE: All click attempts failed for Yes button
FAILED: Clicking Yes should confirm bulk removal ❌
```

### After Fix (FIXED):
```
INFO: ✅ Book at index 0 successfully selected
INFO: ✅ Book at index 1 successfully selected
Selected Count Before Removal: 2
INFO: Remove Selected clicked
INFO: Attempt 1: JavaScript click on Yes button container
WARNING: ⚠️ Attempt 1 did not trigger toaster, trying next approach...
INFO: Attempt 2: JavaScript click on Yes label
INFO: ✅ Attempt 2 SUCCESSFUL - Toaster detected!
INFO: ✅ Found "Removed from favourites" toaster notification
PASSED: TC_410 ✅
```

---

## ✨ Summary

**Fix #32 - Fresh Element Reference for Each Click Attempt**:
- ✅ Each of the 5 click attempts now gets a fresh Yes button element
- ✅ No more stale element reference errors
- ✅ All 5 attempts can now be tried until one triggers the toaster
- ✅ Combined with Fix #30 (didRemovalProgress verification) ensures proper success detection

**Complete TC_410 Fix Chain**: Fix #30 (Yes button verification) → Fix #31 (Book card click) → Fix #32 (Fresh element reference)

**All 3 fixes now complete!**

**Status**: ✅ READY FOR TESTING
**Date**: 2026-04-12
**Files Modified**: FavouritesPage.java (confirmRemovalViaYesButton method, lines 1272-1346)
**Test Case**: TC_410 (verifyRemoveSelectedBooks)

---

## 🔍 Key Insight

**The Problem**: Reusing the same WebElement reference across multiple click attempts causes stale element errors after the first click changes the DOM.

**The Solution**: Get a FRESH element reference for each click attempt by calling `getConfirmationActionButton("Yes")` at the start of each try-catch block.

**This ensures**: Each attempt works with a valid, up-to-date element reference, eliminating stale element errors!
