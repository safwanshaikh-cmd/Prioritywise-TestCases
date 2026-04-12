# TC_406 Fix #19 - Yes Button Exact XPath

## ✅ Issue Fixed: Yes Button Not Found

**User Request**: "Please us e this Xpath for Broken heart Icon removal of book //div[@class='css-g5y9jx r-1i6wzkk r-lrvibr r-1loqt21 r-1otgn73 r-y47klf r-qpntkw r-u8s1d r-3mc0re r-1yxedwg']//div[1] for Yes Button (//div[@class='css-g5y9jx r-1i6wzkk r-lrvibr r-1loqt21 r-1otgn73 r-1awozwy r-1f0042m r-13awgt0 r-1777fci r-17q4wm6 r-xyw6el r-14lw9ot r-icoktb'])[1]"

**Issue**: The original YES_BUTTON locator might not find the Yes button in the confirmation dialog

**User Provided**: 2 exact XPath expressions for the Yes button

---

## 🔧 Fix Applied

### Updated clickYesOnConfirmation() Method

**Before** (Single locator method):
```java
public void clickYesOnConfirmation() {
    try {
        WebElement yesBtn = pageWait.until(ExpectedConditions.visibilityOfElementLocated(YES_BUTTON));
        yesBtn.click();
        LOGGER.info("Clicked Yes on removal confirmation");
        Thread.sleep(1000);
    } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Failed to click Yes on confirmation: {0}", e.getMessage());
    }
}
```

**After** (4-method approach with user-provided XPaths):
```java
public void clickYesOnConfirmation() {
    WebElement yesBtn = null;

    // Method 1: Use exact XPath provided by user (PRIMARY)
    try {
        String exactXPath = "//div[@class='css-g5y9jx r-1i6wzkk r-lrvibr r-1loqt21 r-1otgn73 r-y47klf r-qpntkw r-u8s1d r-3mc0re r-1yxedwg']//div[1]";
        yesBtn = driver.findElement(By.xpath(exactXPath));
        LOGGER.info("Found Yes button using exact XPath (Method 1)");
    } catch (Exception e1) {
        LOGGER.log(Level.FINE, "Method 1 failed: " + e1.getMessage());
    }

    // Method 2: Use second exact XPath provided by user
    if (yesBtn == null) {
        try {
            String exactXPath2 = "(//div[@class='css-g5y9jx r-1i6wzkk r-lrvibr r-1loqt21 r-1otgn73 r-1awozwy r-1f0042m r-13awgt0 r-1777fci r-17q4wm6 r-xyw6el r-14lw9ot r-icoktb'])[1]";
            yesBtn = driver.findElement(By.xpath(exactXPath2));
            LOGGER.info("Found Yes button using second exact XPath (Method 2)");
        } catch (Exception e2) {
            LOGGER.log(Level.FINE, "Method 2 failed: " + e2.getMessage());
        }
    }

    // Method 3: Use original YES_BUTTON locator
    if (yesBtn == null) {
        try {
            yesBtn = pageWait.until(ExpectedConditions.visibilityOfElementLocated(YES_BUTTON));
            LOGGER.info("Found Yes button using original locator (Method 3)");
        } catch (Exception e3) {
            LOGGER.log(Level.FINE, "Method 3 failed: " + e3.getMessage());
        }
    }

    // Method 4: Try finding by text content
    if (yesBtn == null) {
        try {
            yesBtn = driver.findElement(By.xpath("//div[contains(text(), 'Yes')]"));
            LOGGER.info("Found Yes button using text content (Method 4)");
        } catch (Exception e4) {
            LOGGER.log(Level.FINE, "Method 4 failed: " + e4.getMessage());
        }
    }

    // Click the button if found
    if (yesBtn != null) {
        try {
            yesBtn.click();
            LOGGER.info("Clicked Yes on removal confirmation");
            Thread.sleep(1000);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to click Yes button: {0}", e.getMessage());
        }
    } else {
        LOGGER.severe("Yes button not found using any method");
    }
}
```

---

## 📋 What the Fix Does

### 4-Method Yes Button Detection:

1. **Method 1** (PRIMARY): User-provided exact XPath #1
   ```xpath
   //div[@class='css-g5y9jx r-1i6wzkk r-lrvibr r-1loqt21 r-1otgn73 r-y47klf r-qpntkw r-u8s1d r-3mc0re r-1yxedwg']//div[1]
   ```

2. **Method 2**: User-provided exact XPath #2
   ```xpath
   (//div[@class='css-g5y9jx r-1i6wzkk r-lrvibr r-1loqt21 r-1otgn73 r-1awozwy r-1f0042m r-13awgt0 r-1777fci r-17q4wm6 r-xyw6el r-14lw9ot r-icoktb'])[1]
   ```

3. **Method 3**: Original YES_BUTTON locator (fallback)
   ```xpath
   //div[@dir='auto'][text()='Yes']/parent::div[@tabindex='0']
   ```

4. **Method 4**: Text-based search (last resort)
   ```xpath
   //div[contains(text(), 'Yes')]
   ```

---

## 🎯 Why This Will Work

### User's Confirmation Dialog HTML:
```html
<div class="css-g5y9jx r-119rbo0 r-1dzdj1l r-hvns9x r-1pcd2l5 r-6e0ovw">
  <div dir="auto" class="css-146c3p1 r-jwli3a">Remove Favourites</div>
  <div dir="auto" class="css-146c3p1 r-jwli3a">
    Are you sure you want to remove this book from favourites?
  </div>
  <div class="css-g5y9jx r-18u37iz r-1wtj0ep">
    <!-- Yes Button -->
    <div tabindex="0" class="css-g5y9jx r-1i6wzkk r-lrvibr...">
      <div dir="auto" class="css-146c3p1">Yes</div>
    </div>
    <!-- No Button -->
    <div tabindex="0" class="css-g5y9jx r-1i6wzkk r-lrvibr...">
      <div dir="auto" class="css-146c3p1">No</div>
    </div>
  </div>
</div>
```

### Method Breakdown:

**Method 1**: Matches exact parent div class and selects first child div
**Method 2**: Selects first occurrence of the button with complete class string
**Method 3**: Original approach - finds "Yes" text, then gets parent with tabindex
**Method 4**: Simple text search - finds any element containing "Yes"

---

## 🧪 Expected Test Output

### Successful Run:
```
INFO: TC_406: Removing book: New-3 arti (Action)
INFO: Found remove icon using Method 2 (icon character)
INFO: Clicked remove icon for book at index: 0
INFO: TC_406: Clicked remove icon for first book
INFO: Confirmation dialog displayed: true
INFO: Confirmation message: Are you sure you want to remove this book from favourites?
INFO: Found Yes button using exact XPath (Method 1)
INFO: Clicked Yes on removal confirmation
INFO: TC_406: ✅ Book count decreased from 1 to 0
INFO: TC_406: ✅ Book 'New-3 arti (Action)' successfully removed from favourites
PASSED: TC_406 ✅
```

---

## 📝 All TC_406 Fixes Summary

### Complete Fix Chain for TC_406:

1. **Fix #17**: False positive prevention - Added actual removal flow (8 steps)
2. **Fix #18**: Enhanced element finding - getBookTitleAtIndex() and clickRemoveIconAtIndex() with 4-method approach
3. **Fix #19**: Yes button exact XPath - User-provided XPaths as primary methods

---

## 🚀 How to Run

```bash
# Run TC_406 (ensure TC_405 has added a book first)
mvn test -Dtest=FavouritesManagementTests#verifyRemoveBookFromFavourites
```

---

## ✨ Summary

**Fix #19 Applied:**
- ✅ Added user-provided exact XPath #1 as Method 1 (PRIMARY)
- ✅ Added user-provided exact XPath #2 as Method 2
- ✅ Kept original locator as Method 3 (fallback)
- ✅ Added text-based search as Method 4 (last resort)
- ✅ Detailed logging shows which method succeeded
- ✅ Graceful fallback through all methods

**Status**: ✅ READY FOR TESTING
**Date**: 2026-04-12
**Files Modified**: FavouritesPage.java (clickYesOnConfirmation method)
**User XPaths Incorporated**: Both exact XPaths now used as primary detection methods
