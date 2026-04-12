# TC_405 Method 4 Optimization & Method 5 Debug

## ✅ Optimization Applied

### Issue: Method 4 Too Verbose
**Problem**: Found 651 elements with `css-g5y9jx` class and logged ALL of them!

**Impact**:
- Massive log output (hundreds of lines)
- Slow test execution
- Difficult to find relevant information

**Fix Applied**:
```java
// BEFORE:
for (int i = 0; i < cssElements.size(); i++)  // Checks all 651 elements

// AFTER:
for (int i = 0; i < Math.min(cssElements.size(), 20); i++)  // Only first 20
```

### Method 5 Enhancements
Added better logging to see if Method 5 executes:
```java
LOGGER.info("Method 5: Trying exact XPath approach...");
String exactXPath = "(//div[@class='css-g5y9jx r-1awozwy r-z2wwpe r-d045u9 r-1472mwg r-1777fci r-lrsllp'])[1]";
```

Changed error level from FINE to WARNING to see errors:
```java
LOGGER.log(Level.WARNING, "Exact XPath lookup failed: " + e.getMessage());
```

## 🔍 What to Look For in Next Test Run

### Expected Output If Method 5 Works:
```
Heart icon clicked, favorites dialog opened
DEBUG: Dialog elements: [...]
Found 20 elements with css-g5y9jx class (limited from 651)
Element 0-19: [checking...]
Method 5: Trying exact XPath approach...
Found Favourites checkbox using exact XPath (Method 5) ← LOOK FOR THIS!
Favourites checkbox clicked - waiting for loading to complete
✅ Checkbox successfully checked after loading
```

### If Method 5 Fails, You'll See:
```
Method 5: Trying exact XPath approach...
WARNING: Exact XPath lookup failed: <error message> ← LOOK FOR THIS
Favourites checkbox not found in dialog using any method
```

## 📋 Debug Information Needed

If Method 5 fails, please share:
1. The exact error message from "Exact XPath lookup failed"
2. A screenshot of the favorites dialog
3. The HTML of the checkbox element (right-click → Inspect)

## 🎯 Next Steps

### Run Test:
```bash
mvn test -Dtest=FavouritesManagementTests#verifyAddBookToFavourites
```

### If Still Failing:
We may need to adjust the XPath or try a different approach based on the actual error message.

---

**Optimized**: 2026-04-12
**Changes**: 
- Method 4 limited to 20 elements instead of 651
- Method 5 added info/warning logging
- Both methods now execute faster with clearer output
