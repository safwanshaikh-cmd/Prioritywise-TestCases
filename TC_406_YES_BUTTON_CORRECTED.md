# TC_406 Fix #23 - Yes Button Click Correct Element

## ✅ Issue: Clicking Wrong Element

**User Feedback**: "I think it is not clicking on Yes button"

**Root Cause**: We're clicking on the **inner div** (`<div dir="auto" class="css-146c3p1">Yes</div>`) which is NOT clickable!

Looking at your HTML:
```html
<div tabindex="0" class="css-g5y9jx r-1i6wzkk r-lrvibr r-1loqt21 r-1otgn73 r-y47klf r-qpntkw r-u8s1d r-3mc0re r-1yxedwg">
  <div dir="auto" class="css-146c3p1">Yes</div>  ← We're clicking THIS (wrong!)
</div>
```

**The Problem**: The inner `<div dir="auto">` is just text display. The **parent `<div tabindex="0">`** is the clickable button!

---

## 🔧 Fix Applied

### Updated XPaths to Click Parent Element

**Current XPaths** (Wrong - clicking inner div):
```xpath
// Wrong - clicks inner div (not clickable)
//div[@class='css-g5y9jx r-1i6wzkk r-lrvibr r-1loqt21 r-1otgn73 r-y47klf r-qpntkw r-u8s1d r-3mc0re r-1yxedwg']//div[1]
```

**Corrected XPaths** (Right - clicking parent button):
```xpath
// Correct - clicks parent div with tabindex (clickable!)
//div[@class='css-g5y9jx r-1i6wzkk r-lrvibr r-1loqt21 r-1otgn73 r-y47klf r-qpntkw r-u8s1d r-3mc0re r-1yxedwg']
```

Notice: **Removed `//div[1]`** - now clicks the parent button element directly!

---

## 📋 Updated Method

```java
public void clickYesOnConfirmation() {
    WebElement yesBtn = null;

    // Method 1: Click parent element directly (CORRECTED - removed //div[1])
    try {
        String exactXPath = "//div[@class='css-g5y9jx r-1i6wzkk r-lrvibr r-1loqt21 r-1otgn73 r-y47klf r-qpntkw r-u8s1d r-3mc0re r-1yxedwg']";
        yesBtn = driver.findElement(By.xpath(exactXPath));
        LOGGER.info("Found Yes button parent element (Method 1)");
    } catch (Exception e1) {
        LOGGER.log(Level.FINE, "Method 1 failed: " + e1.getMessage());
    }

    // Method 2: Click parent using second exact class
    if (yesBtn == null) {
        try {
            String exactXPath2 = "//div[@class='css-g5y9jx r-1i6wzkk r-lrvibr r-1loqt21 r-1otgn73 r-1awozwy r-1f0042m r-13awgt0 r-1777fci r-17q4wm6 r-xyw6el r-14lw9ot r-icoktb']";
            yesBtn = driver.findElement(By.xpath(exactXPath2));
            LOGGER.info("Found Yes button parent element (Method 2)");
        } catch (Exception e2) {
            LOGGER.log(Level.FINE, "Method 2 failed: " + e2.getMessage());
        }
    }

    // Method 3: Find by tabindex attribute
    if (yesBtn == null) {
        try {
            yesBtn = driver.findElement(By.xpath("//div[@tabindex='0']//div[text()='Yes']/.."));
            LOGGER.info("Found Yes button using tabindex (Method 3)");
        } catch (Exception e3) {
            LOGGER.log(Level.FINE, "Method 3 failed: " + e3.getMessage());
        }
    }

    // Method 4: Find by tabindex and class
    if (yesBtn == null) {
        try {
            yesBtn = driver.findElement(By.xpath("//div[@tabindex='0' and contains(@class, 'r-1i6wzkk')]"));
            LOGGER.info("Found Yes button using tabindex+class (Method 4)");
        } catch (Exception e4) {
            LOGGER.log(Level.FINE, "Method 4 failed: " + e4.getMessage());
        }
    }

    // Click using JavaScript (most reliable)
    if (yesBtn != null) {
        try {
            Thread.sleep(500);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", yesBtn);
            Thread.sleep(300);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", yesBtn);
            LOGGER.info("✅ Successfully clicked Yes button");
            Thread.sleep(2000);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to click Yes button: {0}", e.getMessage());
        }
    } else {
        LOGGER.severe("Yes button not found using any method");
    }
}
```

---

## 🎯 Why This Will Work

**Before** (Wrong):
```xpath
//div[@class='...']//div[1]
```
→ Clicks inner div (text only) ❌

**After** (Correct):
```xpath
//div[@class='...']
```
→ Clicks parent button (with tabindex) ✅

The parent element has:
- `tabindex="0"` - Makes it focusable and clickable
- The actual button functionality
- All the click handlers

---

## 🚀 How to Apply

I need to update the FavouritesPage.java file with the corrected XPaths.

**Status**: ⏳ Ready to apply fix
**Date**: 2026-04-12
**Files to Modify**: FavouritesPage.java (clickYesOnConfirmation method)
