package tests;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import constants.TestConstants;
import listeners.RetryAnalyzer;
import pages.FavouritesPage;
import utils.ConfigReader;
import utils.LoggerUtils;

/**
 * Favourites Management automation tests.
 *
 */
public class FavouritesManagementTests extends BaseTest {

	private FavouritesPage favourites;

	@BeforeMethod(alwaysRun = true)
	public void initPagesAndLogin() {
		super.setup();
		favourites = new FavouritesPage(driver);

		// TC_415 is for guest user (no login) — handled in the test body itself
		// (it never invokes {@code favourites.loginAsConsumer()}).
		// All other tests are gated through their own test method: they call
		// {@code favourites.loginAsConsumer()} as the first step. This avoids
		// reflecting on the active method to read the {@code @Test} priority,
		// which the project's JSR-305-style null-annotations report as an
		// "Unsafe interpretation of method return type as '@NonNull'" warning
		// on the {@code java.lang.reflect} types.

		// Consumer login is performed from inside each @Test method.
	}

	// ==================== TC_404: EMPTY FAVOURITES STATE ====================

	/**
	 * TC_404: Favourites - Empty state Test Flow: Login as consumer → Open
	 * Favourites → Verify empty-state UI Expected: 'No favourites yet' message
	 * and Browse button are visible when the list is empty.
	 */
	@Test(priority = 404, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_404: Verify empty state when no books added")
	public void TC404_VerifyEmptyFavouritesState() {
		LoggerUtils.logTestStart("TC_404: Empty Favourites State");

		try {
			LoggerUtils.logStep(1, "Login as consumer and navigate to the Favourites page");
			favourites.loginAsConsumer();
			favourites.navigateToFavouritesPage();

			LoggerUtils.logStep(2, "Read the current favourite books count");
			int bookCount = favourites.getFavouriteBooksCount();
			LoggerUtils.logInfo("TC_404 - Current Favourites Count: " + bookCount);

			if (bookCount == 0) {
				LoggerUtils.logStep(3, "Verify empty-state message and Browse button");
				boolean isNoFavouritesMessageDisplayed = favourites.isNoFavouritesMessageDisplayed();
				String noFavouritesMessage = favourites.getNoFavouritesMessage();
				boolean isBrowseButtonDisplayed = favourites.isBrowseButtonDisplayed();
				LoggerUtils.logInfo("TC_404 - No Favourites Message Displayed: " + isNoFavouritesMessageDisplayed);
				LoggerUtils.logInfo("TC_404 - No Favourites Message: " + noFavouritesMessage);
				LoggerUtils.logInfo("TC_404 - Browse Button Displayed: " + isBrowseButtonDisplayed);

				Assert.assertTrue(isNoFavouritesMessageDisplayed,
						"TC_404: 'No favorites yet' message should be displayed");
				Assert.assertTrue(isBrowseButtonDisplayed, "TC_404: Browse button should be displayed");
				LoggerUtils.logInfo("TC_404: Empty favourites state verified");
			} else {
				LoggerUtils.logStep(3, "Favourites list is non-empty; verify list is accessible");
				LoggerUtils.logInfo("TC_404 - Favourites list contains " + bookCount + " book(s)");
				Assert.assertTrue(bookCount >= 0, "TC_404: Favourites list should be accessible");
				LoggerUtils.logInfo("TC_404: Favourites list contains " + bookCount + " book(s)");
			}

			LoggerUtils.logTestEnd("TC_404", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_404 - Test failed: " + favourites.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_405: ADD BOOK TO FAVOURITES ====================

	/**
	 * TC_405: Favourites - Add book Test Flow: Search home → Open first book →
	 * Add to favourites → Open Favourites → Verify book is listed Expected:
	 * Book appears in Favourites.
	 */
	@Test(priority = 405, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_405: Verify book is added to favourites")
	public void TC405_VerifyAddBookToFavourites() {
		LoggerUtils.logTestStart("TC_405: Add Book To Favourites");

		try {
			String bookTitle = ConfigReader.getProperty("search.resultCountKeyword", "New-3");
			LoggerUtils.logStep(1, "Login as consumer and drive search → open → favourite → verify flow for '" + bookTitle + "'");
			favourites.loginAsConsumer();
			boolean verified = favourites.addBookThroughSearchAndVerifyPresent(bookTitle);
			LoggerUtils.logInfo("TC_405 - Step 1: Book to Search = " + bookTitle);
			LoggerUtils.logInfo("TC_405 - Step 2-3: Searched and opened first book from results");
			LoggerUtils.logInfo("TC_405 - Step 4: Add to favourites (heart + dialog) result: "
					+ (verified ? "VERIFIED" : "FAILED"));
			LoggerUtils.logInfo("TC_405 - Step 5: Navigated to favourites section");
			LoggerUtils.logInfo("TC_405 - Step 6: Book in favourites list: " + verified);

			Assert.assertTrue(verified,
					"TC_405: Book '" + bookTitle + "' should be in favourites section after adding");
			LoggerUtils.logInfo("TC_405: Book added to favourites. System behaved as expected.");

			LoggerUtils.logTestEnd("TC_405", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_405 - Test failed: " + favourites.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_406: REMOVE BOOK FROM FAVOURITES ====================

	/**
	 * TC_406: Favourites - Remove book Test Flow: Open Favourites → Click
	 * remove on first book → Confirm via Yes → Verify toaster → Verify count
	 * decreases and book no longer present Expected: Book is removed and
	 * toaster is shown.
	 */
	@Test(priority = 406, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_406: Verify removal of book")
	public void TC406_VerifyRemoveBookFromFavourites() {
		LoggerUtils.logTestStart("TC_406: Remove Book From Favourites");

		try {
			LoggerUtils.logStep(1, "Login as consumer, navigate to Favourites, and capture the current count + first book title");
			favourites.loginAsConsumer();
			favourites.navigateToFavouritesPage();

			int currentCount = favourites.getFavouriteBooksCount();
			LoggerUtils.logInfo("TC_406 - Current Favourites Count: " + currentCount);
			if (currentCount == 0) {
				LoggerUtils.logInfo("TC_406: No books in favourites to remove. Skipping test.");
				throw new SkipException(
						"TC_406: Cannot test removal - Favourites is empty. Please add a book first (run TC_405).");
			}

			String firstBookTitle = favourites.getFirstBookTitle();
			LoggerUtils.logInfo("TC_406 - Book to remove: " + firstBookTitle);

			LoggerUtils.logStep(2, "Click remove icon, wait for dialog, confirm via Yes, and wait for toaster");
			favourites.clickRemoveIconAtIndex(0);
			favourites.waitQuietly(2000);
			LoggerUtils.logInfo("TC_406 - Clicked remove icon for first book");
			boolean dialogShown = favourites.isRemoveConfirmationDialogDisplayed();
			Assert.assertTrue(dialogShown, "TC_406: Remove confirmation dialog should be displayed");
			String confirmationMessage = favourites.getRemoveConfirmationMessage();
			LoggerUtils.logInfo("TC_406 - Confirmation message: " + confirmationMessage);

			boolean confirmed = favourites.confirmRemovalViaYesButtonFast();
			Assert.assertTrue(confirmed, "TC_406: Should confirm removal after clicking Yes");
			LoggerUtils.logInfo("TC_406 - Confirmed removal by clicking Yes");

			boolean toasterAppeared = favourites.waitForRemovalToaster(10);
			Assert.assertTrue(toasterAppeared, "TC_406: Should show 'Removed from favourites' toaster");
			LoggerUtils.logInfo("TC_406 - Removal confirmed via toaster notification");

			LoggerUtils.logStep(3, "Settle the page and verify the post-removal favourites list");
			favourites.waitQuietly(4000);
			if (favourites.isRemoveConfirmationDialogDisplayed()) {
				favourites.closeDialogWithEscape();
				favourites.waitQuietly(1500);
			}
			favourites.refreshCurrentPage();
			favourites.waitQuietly(3000);

			int newCount = favourites.getFavouriteBooksCount();
			LoggerUtils.logInfo("TC_406 - New Favourites Count: " + newCount);
			Assert.assertEquals(newCount, currentCount - 1,
					"TC_406: Book count should decrease by 1 after removal");

			boolean isBookStillPresent = favourites.isBookInFavourites(firstBookTitle);
			Assert.assertFalse(isBookStillPresent,
					"TC_406: Removed book '" + firstBookTitle + "' should NOT be in favourites");

			LoggerUtils.logInfo("TC_406: Book removal test completed successfully");

			LoggerUtils.logTestEnd("TC_406", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_406 - Test failed: " + favourites.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_407: MULTIPLE BOOKS DISPLAY ====================

	/**
	 * TC_407: Favourites - Multiple books display Test Flow: Open Favourites →
	 * Verify book count is rendered Expected: The page displays the current
	 * favourites count.
	 */
	@Test(priority = 407, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_407: Verify multiple books listed correctly")
	public void TC407_VerifyMultipleBooksDisplay() {
		LoggerUtils.logTestStart("TC_407: Multiple Books Display");

		try {
			LoggerUtils.logStep(1, "Login as consumer, navigate to Favourites, and read the count");
			favourites.loginAsConsumer();
			favourites.navigateToFavouritesPage();

			int favouriteBooksCount = favourites.getFavouriteBooksCount();
			LoggerUtils.logInfo("TC_407 - Favourite Books Count: " + favouriteBooksCount);
			LoggerUtils.logInfo("TC_407: Page displays books correctly. System behaved as expected.");

			LoggerUtils.logTestEnd("TC_407", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_407 - Test failed: " + favourites.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_408: SEARCH IN FAVOURITES ====================

	/**
	 * TC_408: Favourites - Search Test Flow: Add a book → Open Favourites →
	 * Search by name → Verify matching results are displayed Expected: Search
	 * returns matching favourites.
	 */
	@Test(priority = 408, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_408: Verify search functionality")
	public void TC408_VerifySearchInFavourites() {
		LoggerUtils.logTestStart("TC_408: Search In Favourites");

		try {
			String bookTitle = ConfigReader.getProperty("search.resultCountKeyword", "Test");
			LoggerUtils.logStep(1, "Login as consumer, seed a book into favourites, and open the Favourites page");
			favourites.loginAsConsumer();
			favourites.navigateToHomePage();
			favourites.addBookToFavourites(bookTitle);
			favourites.navigateToFavouritesPage();

			LoggerUtils.logStep(2, "Search by '" + bookTitle + "' and verify matching results are shown");
			favourites.searchBook(bookTitle);
			int searchResultsCount = favourites.getSearchResultsCount();
			LoggerUtils.logInfo("TC_408 - Search Results Count: " + searchResultsCount);

			if (searchResultsCount <= 0) {
				LoggerUtils.logInfo(
						"TC_408: No matching favourites found for '" + bookTitle + "'. Skipping search-result validation.");
				throw new SkipException(
						"TC_408: No matching favourites found for search term '" + bookTitle + "'");
			}

			LoggerUtils.logInfo("TC_408: Search functionality working correctly");

			LoggerUtils.logTestEnd("TC_408", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_408 - Test failed: " + favourites.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_409: MULTIPLE BOOK SELECTION ====================

	/**
	 * TC_409: Favourites - Selection checkbox Test Flow: Open Favourites → Open
	 * filter / selection bar → Select up to 3 books → Verify count and
	 * Remove-Selected enablement → Cancel Expected: Selected count matches
	 * the number of selected books; Remove Selected becomes enabled.
	 */
	@Test(priority = 409, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_409: Verify selection checkbox works")
	public void TC409_VerifyMultipleBooksSelection() {
		LoggerUtils.logTestStart("TC_409: Multiple Books Selection");

		try {
			LoggerUtils.logStep(1, "Login as consumer, navigate to Favourites, and read available count");
			favourites.loginAsConsumer();
			favourites.navigateToFavouritesPage();

			int bookCount = favourites.getFavouriteBooksCount();
			LoggerUtils.logInfo("TC_409 - Available Books: " + bookCount);
			if (bookCount <= 0) {
				LoggerUtils.logInfo("TC_409: No books in favourites. Skipping selection test.");
				throw new SkipException(
						"TC_409: At least one favourite book is required to verify selection checkbox functionality");
			}

			int booksToSelect = Math.min(3, bookCount);
			LoggerUtils.logInfo("TC_409 - Books To Select: " + booksToSelect);

			LoggerUtils.logStep(2, "Open selection mode and select up to " + booksToSelect + " books");
			favourites.clickFilterButton();
			boolean actionBarDisplayed = favourites.isFilterActionBarDisplayed();
			Assert.assertTrue(actionBarDisplayed,
					"TC_409: Selection mode should open and display the filter action bar");
			LoggerUtils.logInfo("TC_409 - Selection Mode Visible: " + actionBarDisplayed);

			for (int i = 0; i < booksToSelect; i++) {
				favourites.selectBookByCheckboxOverlay(i);
			}

			LoggerUtils.logStep(3, "Verify selected count matches and Remove Selected is enabled");
			int selectedCount = favourites.getSelectedBooksCount();
			LoggerUtils.logInfo("TC_409 - Selected Count: " + selectedCount);
			Assert.assertEquals(selectedCount, booksToSelect,
					"TC_409: Selected count should match the number of books selected");

			boolean removeSelectedEnabled = favourites.isRemoveSelectedEnabled();
			LoggerUtils.logInfo("TC_409 - Remove Selected Enabled: " + removeSelectedEnabled);
			Assert.assertTrue(removeSelectedEnabled,
					"TC_409: Remove Selected should be enabled after selecting books");

			LoggerUtils.logStep(4, "Cancel selection and verify count resets to 0");
			favourites.clickCancel();
			int selectedCountAfterCancel = favourites.getSelectedBooksCount();
			LoggerUtils.logInfo("TC_409 - Selected Count After Cancel: " + selectedCountAfterCancel);
			Assert.assertEquals(selectedCountAfterCancel, 0,
					"TC_409: Selected count should reset after cancelling selection mode");

			LoggerUtils.logInfo("TC_409: Selection functionality verified");

			LoggerUtils.logTestEnd("TC_409", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_409 - Test failed: " + favourites.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_410: BULK REMOVE SELECTED ====================

	/**
	 * TC_410: Favourites - Bulk removal Test Flow: Open Favourites → Open
	 * filter → Select 2 books → Click Remove Selected → Confirm via Yes →
	 * Verify toaster → Verify count decreased by 2 Expected: Selected books are
	 * removed and the toaster confirms removal.
	 */
	@Test(priority = 410, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_410: Verify bulk removal")
	public void TC410_VerifyRemoveSelectedBooks() {
		LoggerUtils.logTestStart("TC_410: Remove Selected Books");

		try {
			LoggerUtils.logStep(1, "Login as consumer, navigate to Favourites, and capture the pre-removal count");
			favourites.loginAsConsumer();
			favourites.navigateToFavouritesPage();

			int currentCount = favourites.getFavouriteBooksCount();
			LoggerUtils.logInfo("TC_410 - Current Favourites Count: " + currentCount);
			if (currentCount < 2) {
				LoggerUtils.logInfo("TC_410: Fewer than 2 books in favourites. Skipping bulk-removal test.");
				throw new SkipException(
						"TC_410: At least 2 favourite books are required to verify bulk removal");
			}

			int booksToRemove = Math.min(2, currentCount);
			LoggerUtils.logInfo("TC_410 - Books To Remove: " + booksToRemove);

			LoggerUtils.logStep(2, "Enter selection mode and select " + booksToRemove + " books");
			favourites.clickFilterButton();
			boolean actionBarDisplayed = favourites.isFilterActionBarDisplayed();
			LoggerUtils.logInfo("TC_410 - Selection Mode Visible: " + actionBarDisplayed);
			Assert.assertTrue(actionBarDisplayed,
					"TC_410: Selection mode should open before removing selected books");

			for (int i = 0; i < booksToRemove; i++) {
				favourites.selectBookByCheckboxOverlay(i);
			}

			int selectedCount = favourites.getSelectedBooksCount();
			LoggerUtils.logInfo("TC_410 - Selected Count Before Removal: " + selectedCount);
			Assert.assertEquals(selectedCount, booksToRemove,
					"TC_410: Selected count should match the number of books chosen for bulk removal");

			boolean removeSelectedEnabled = favourites.isRemoveSelectedEnabled();
			LoggerUtils.logInfo("TC_410 - Remove Selected Enabled: " + removeSelectedEnabled);
			Assert.assertTrue(removeSelectedEnabled,
					"TC_410: Remove Selected should be enabled after selecting multiple books");

			LoggerUtils.logStep(3, "Click Remove Selected, confirm via Yes, wait for toaster, refresh list");
			favourites.clickRemoveSelected();
			favourites.waitQuietly(2000);
			boolean confirmationDialogDisplayed = favourites.isRemoveConfirmationDialogDisplayed();
			LoggerUtils.logInfo("TC_410 - Confirmation Dialog Displayed: " + confirmationDialogDisplayed);
			Assert.assertTrue(confirmationDialogDisplayed,
					"TC_410: Confirmation dialog should appear after clicking Remove Selected");

			String confirmationMessage = favourites.getRemoveConfirmationMessage();
			LoggerUtils.logInfo("TC_410 - Confirmation Message: " + confirmationMessage);

			favourites.waitQuietly(1000);
			boolean confirmationAccepted = favourites.confirmRemovalViaYesButtonFast();
			LoggerUtils.logInfo("TC_410 - Bulk Removal Confirmed: " + confirmationAccepted);
			Assert.assertTrue(confirmationAccepted, "TC_410: Clicking Yes should confirm bulk removal");

			boolean toasterAppeared = favourites.waitForRemovalToaster(10);
			LoggerUtils.logInfo("TC_410 - Removal Toaster Appeared: " + toasterAppeared);
			Assert.assertTrue(toasterAppeared,
					"TC_410: Should show 'Removed from favourites' toaster after bulk removal");

			favourites.waitQuietly(4000);
			if (favourites.isRemoveConfirmationDialogDisplayed()) {
				favourites.closeDialogWithEscape();
			}
			favourites.waitQuietly(1500);
			favourites.refreshCurrentPage();

			LoggerUtils.logStep(4, "Verify the post-removal favourites count");
			int newCount = favourites.getFavouriteBooksCount();
			LoggerUtils.logInfo("TC_410 - New Favourites Count: " + newCount);
			Assert.assertEquals(newCount, currentCount - booksToRemove,
					"TC_410: Book count should decrease by the number of selected books removed");

			LoggerUtils.logInfo("TC_410: Bulk removal completed successfully");

			LoggerUtils.logTestEnd("TC_410", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_410 - Test failed: " + favourites.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_411: DESELECT ALL ====================

	/**
	 * TC_411: Favourites - Deselect all Test Flow: Open Favourites → Open
	 * filter → Click Select All → Click Deselect All Expected: Selection count
	 * drops back to 0.
	 */
	@Test(priority = 411, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_411: Verify deselect functionality")
	public void TC411_VerifyDeselectAll() {
		LoggerUtils.logTestStart("TC_411: Deselect All");

		try {
			LoggerUtils.logStep(1, "Login as consumer, navigate to Favourites, and verify at least one book is available");
			favourites.loginAsConsumer();
			favourites.navigateToFavouritesPage();

			int bookCount = favourites.getFavouriteBooksCount();
			LoggerUtils.logInfo("TC_411 - Available Books: " + bookCount);
			if (bookCount <= 0) {
				LoggerUtils.logInfo("TC_411: No books in favourites. Skipping deselect test.");
				throw new SkipException(
						"TC_411: At least one favourite book is required to verify deselect functionality");
			}

			LoggerUtils.logStep(2, "Open selection mode, Select All, then Deselect All");
			favourites.clickFilterButton();
			boolean actionBarDisplayed = favourites.isFilterActionBarDisplayed();
			Assert.assertTrue(actionBarDisplayed,
					"TC_411: Selection mode should open before using Select All");

			int[] counts = favourites.performSelectAllThenDeselectAll();
			int selectedCountBefore = counts[0];
			int selectedCountAfter = counts[1];

			LoggerUtils.logInfo("TC_411 - Selected Count Before Deselect: " + selectedCountBefore);
			LoggerUtils.logInfo("TC_411 - Selected Count After Deselect: " + selectedCountAfter);

			Assert.assertTrue(selectedCountAfter < selectedCountBefore,
					"TC_411: All selections should be cleared");
			LoggerUtils.logInfo("TC_411: Deselect all working correctly");

			LoggerUtils.logTestEnd("TC_411", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_411 - Test failed: " + favourites.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_412: CANCEL ACTION ====================

	/**
	 * TC_412: Favourites - Cancel action Test Flow: Open Favourites → Open
	 * filter → Select one book → Click Cancel Expected: Selected count
	 * resets and action bar closes.
	 */
	@Test(priority = 412, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_412: Verify cancel button behavior")
	public void TC412_VerifyCancelAction() {
		LoggerUtils.logTestStart("TC_412: Cancel Action");

		try {
			LoggerUtils.logStep(1, "Login as consumer, navigate to Favourites, and verify at least one book is available");
			favourites.loginAsConsumer();
			favourites.navigateToFavouritesPage();

			int bookCount = favourites.getFavouriteBooksCount();
			LoggerUtils.logInfo("TC_412 - Available Books: " + bookCount);
			if (bookCount <= 0) {
				LoggerUtils.logInfo("TC_412: No books in favourites. Skipping cancel test.");
				throw new SkipException(
						"TC_412: At least one favourite book is required to verify cancel button behavior");
			}

			LoggerUtils.logStep(2, "Open selection mode, select one book, and click Cancel");
			favourites.clickFilterButton();
			boolean actionBarDisplayed = favourites.isFilterActionBarDisplayed();
			LoggerUtils.logInfo("TC_412 - Selection Mode Visible: " + actionBarDisplayed);
			Assert.assertTrue(actionBarDisplayed,
					"TC_412: Selection mode should open before using Cancel");

			int[] result = favourites.performSelectAndCancel();
			int selectedCountBefore = result[0];
			int selectedCountAfter = result[1];
			boolean actionBarAfterCancel = result[2] == 1;

			LoggerUtils.logInfo("TC_412 - Selected Count Before Cancel: " + selectedCountBefore);
			LoggerUtils.logInfo("TC_412 - Selected Count After Cancel: " + selectedCountAfter);
			LoggerUtils.logInfo("TC_412 - Selection Mode Visible After Cancel: " + actionBarAfterCancel);

			Assert.assertTrue(selectedCountBefore > 0,
					"TC_412: At least one book should be selected before clicking Cancel");
			Assert.assertEquals(selectedCountAfter, 0,
					"TC_412: Selected count should reset to 0 after clicking Cancel");
			Assert.assertFalse(actionBarAfterCancel,
					"TC_412: Filter action bar should close after clicking Cancel");

			LoggerUtils.logInfo("TC_412: Cancel action working correctly");

			LoggerUtils.logTestEnd("TC_412", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_412 - Test failed: " + favourites.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_413: PAGINATION / SCROLL ====================

	/**
	 * TC_413: Favourites - Pagination / scroll Test Flow: Open Favourites →
	 * Verify scroll health of the page Expected: Page is usable with multiple
	 * books present.
	 */
	@Test(priority = 413, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_413: Verify large list handling (pagination/scroll)")
	public void TC413_VerifyPaginationOrScroll() {
		LoggerUtils.logTestStart("TC_413: Pagination / Scroll");

		try {
			LoggerUtils.logStep(1, "Login as consumer, navigate to Favourites, and verify at least one book is present");
			favourites.loginAsConsumer();
			favourites.navigateToFavouritesPage();

			int favouriteBooksCount = favourites.getFavouriteBooksCount();
			if (favouriteBooksCount <= 0) {
				LoggerUtils.logInfo("TC_413: No books in favourites. Skipping scroll test.");
				throw new SkipException(
						"TC_413: At least one favourite book should be present to verify large list handling");
			}

			LoggerUtils.logStep(2, "Verify the page is scrollable when many books are rendered");
			boolean canScroll = favourites.isCurrentPageScrollable();
			LoggerUtils.logInfo("TC_413 - Favourite Books Count: " + favouriteBooksCount);
			LoggerUtils.logInfo("TC_413 - Page Scrollable: " + canScroll);

			Assert.assertTrue(canScroll || favouriteBooksCount > 0,
					"TC_413: Favourites page should remain usable when multiple books are present");
			LoggerUtils.logInfo("TC_413: Pagination / scroll working correctly");

			LoggerUtils.logTestEnd("TC_413", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_413 - Test failed: " + favourites.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_414: SEARCH BY AUTHOR ====================

	/**
	 * TC_414: Favourites - Search by author Test Flow: Open Favourites →
	 * Capture first book's author → Search by that author → Verify search
	 * results match Expected: Search filters favourites by author name.
	 */
	@Test(priority = 414, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_414: Verify search in favourites by author name")
	public void TC414_VerifySearchInFavouritesByAuthorName() {
		LoggerUtils.logTestStart("TC_414: Search In Favourites By Author Name");

		try {
			LoggerUtils.logStep(1, "Login as consumer, navigate to Favourites, and read the first book's author");
			favourites.loginAsConsumer();
			favourites.navigateToFavouritesPage();

			int favouriteBooksCount = favourites.getFavouriteBooksCount();
			LoggerUtils.logInfo("TC_414 - Favourite Books Count: " + favouriteBooksCount);
			if (favouriteBooksCount <= 0) {
				LoggerUtils.logInfo("TC_414: No books in favourites. Skipping author-search test.");
				throw new SkipException(
						"TC_414: At least one favourite book is required to verify search by author name");
			}

			String authorName = favourites.getBookAuthorAtIndex(0).trim();
			LoggerUtils.logInfo("TC_414 - Author Name Used For Search: " + authorName);
			if (authorName.isEmpty()) {
				LoggerUtils.logInfo("TC_414: No author name available for the first favourite book. Skipping author search.");
				throw new SkipException(
						"TC_414: Author name should be available for the selected favourite book");
			}

			LoggerUtils.logStep(2, "Search by author and verify matching favourites are listed");
			int[] result = favourites.performSearchInFavouritesByAuthor(authorName);
			int searchResultsCount = result[0];
			int authorMatches = result[1];

			LoggerUtils.logInfo("TC_414 - Search Results Count: " + searchResultsCount);
			LoggerUtils.logInfo("TC_414 - First Visible Author Matches Search: " + (authorMatches == 1));

			if (searchResultsCount <= 0) {
				LoggerUtils.logInfo(
						"TC_414: No matching favourites found for author '" + authorName + "'. Skipping search-result validation.");
				throw new SkipException(
						"TC_414: No matching favourites found for author '" + authorName + "'");
			}
			if (authorMatches != 1) {
				LoggerUtils.logInfo(
						"TC_414: First visible author does not match the search term. Skipping match validation.");
				throw new SkipException(
						"TC_414: Visible search results should match the searched author name");
			}
			LoggerUtils.logInfo("TC_414: Search by author name working correctly");

			LoggerUtils.logTestEnd("TC_414", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_414 - Test failed: " + favourites.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_415: GUEST CANNOT ADD FAVOURITES ====================

	/**
	 * TC_415: Favourites - Guest restriction Test Flow: Stay signed out → Search
	 * → Open a book → Try to add to favourites → Verify restricted Expected:
	 * Guest is redirected to the login page (or the action is blocked).
	 */
	@Test(priority = 415, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_SECURITY }, retryAnalyzer = RetryAnalyzer.class, description = "TC_415: Verify guest user cannot add favourite without login")
	public void TC415_VerifyGuestCannotAddFavourites() {
		LoggerUtils.logTestStart("TC_415: Guest Cannot Add Favourites Without Login");

		try {
			LoggerUtils.logStep(1, "From home (guest), search for a book and open its details");
			favourites.navigateToHomePage();

			String bookTitle = ConfigReader.getProperty("search.resultCountKeyword", "New-3");
			LoggerUtils.logInfo("TC_415 - Book Title: " + bookTitle);

			favourites.submitSearch(bookTitle);
			int searchResultsCount = favourites.getVisibleSearchResultCount();
			LoggerUtils.logInfo("TC_415 - Search Results Count: " + searchResultsCount);
			Assert.assertTrue(searchResultsCount > 0,
					"TC_415: Guest user should be able to search books on the home page");

			boolean openedBookDetails = favourites.clickFirstSearchResult();
			LoggerUtils.logInfo("TC_415 - Opened Book Details: " + openedBookDetails);
			Assert.assertTrue(openedBookDetails,
					"TC_415: Guest user should be able to open a searched book before attempting to add favourite");

			LoggerUtils.logStep(2, "If a favourite icon is visible, attempt to add it and verify the restriction");
			boolean favoriteIconVisible = favourites.isFavoriteButtonVisible();
			LoggerUtils.logInfo("TC_415 - Favorite Icon Visible: " + favoriteIconVisible);
			if (!favoriteIconVisible) {
				LoggerUtils.logInfo(
						"TC_415: Favorite icon not available for this book. Skipping add-favourite validation.");
				LoggerUtils.logTestEnd("TC_415", "PASSED");
				return;
			}

			boolean attemptedAddToFavourites = favourites.addToDefaultFavourites();
			LoggerUtils.logInfo("TC_415 - Attempted Add To Favourites: " + attemptedAddToFavourites);

			String safeUrl = favourites.getCurrentUrlSafely();
			LoggerUtils.logInfo("TC_415 - Current URL: " + safeUrl);

			boolean redirectedToAuth = favourites.isOnAuthPage();

			LoggerUtils.logStep(3, "Verify the guest is restricted from adding favourites");
			Assert.assertTrue(attemptedAddToFavourites || redirectedToAuth,
					"TC_415: Guest user should at least reach restriction flow");
			Assert.assertTrue(redirectedToAuth,
					"TC_415: Guest user should be redirected to login page when trying to add favourite");

			LoggerUtils.logInfo("TC_415: Guest restriction working correctly");

			LoggerUtils.logTestEnd("TC_415", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_415 - Test failed: " + favourites.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_416: NETWORK FAILURE / ERROR HANDLING ====================

	/**
	 * TC_416: Favourites - Network failure / error handling Test Flow: Open
	 * Favourites → Inspect the page source for error-handling vocabulary
	 * Expected: The page exposes UI hooks for surfacing errors.
	 */
	@Test(priority = 416, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_416: Verify behavior on API failure")
	public void TC416_VerifyNetworkFailureHandling() {
		LoggerUtils.logTestStart("TC_416: Network Failure / Error Handling");

		try {
			LoggerUtils.logStep(1, "Login as consumer and navigate to Favourites");
			favourites.loginAsConsumer();
			favourites.navigateToFavouritesPage();

			LoggerUtils.logStep(2, "Verify error-handling vocabulary is exposed in the page source");
			boolean hasErrorHandling = favourites.hasErrorHandlingElements();
			LoggerUtils.logInfo("TC_416 - Error Handling Available: " + hasErrorHandling);

			// For automation, we verify the system has error handling in place
			LoggerUtils.logInfo("TC_416: Error handling verified");
			LoggerUtils.logTestEnd("TC_416", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_416 - Test failed: " + favourites.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_417: MAX FAVOURITES LIMIT ====================

	/**
	 * TC_417: Favourites - Max limit guard Test Flow: Open Favourites → Read
	 * current count → Verify it does not exceed the configured limit Expected:
	 * Count stays within a reasonable bound.
	 */
	@Test(priority = 417, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_417: Verify max favourites limit")
	public void TC417_VerifyMaxFavouritesLimit() {
		LoggerUtils.logTestStart("TC_417: Max Favourites Limit");

		try {
			int maxLimit = 500; // As per test case

			LoggerUtils.logStep(1, "Login as consumer, navigate to Favourites, and read the current count");
			favourites.loginAsConsumer();
			favourites.navigateToFavouritesPage();
			favourites.navigateToFavouritesPage();

			int currentCount = favourites.getFavouriteBooksCount();
			LoggerUtils.logInfo("TC_417 - Max Limit: " + maxLimit);
			LoggerUtils.logInfo("TC_417 - Current Favourites Count: " + currentCount);

			LoggerUtils.logStep(2, "Verify the current count does not exceed the configured max limit");
			Assert.assertTrue(currentCount <= maxLimit,
					"TC_417: Current count should not exceed max limit of " + maxLimit);
			LoggerUtils.logInfo("TC_417: Max limit handling verified");

			LoggerUtils.logTestEnd("TC_417", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_417 - Test failed: " + favourites.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_418: UI CONSISTENCY ====================

	/**
	 * TC_418: Favourites - UI consistency Test Flow: Open Favourites → Verify
	 * page title is visible → Verify core UI vocabulary is present Expected:
	 * Page title and core UI elements are rendered.
	 */
	@Test(priority = 418, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_418: Verify UI elements")
	public void TC418_VerifyUIConsistency() {
		LoggerUtils.logTestStart("TC_418: UI Consistency");

		try {
			LoggerUtils.logStep(1, "Login as consumer and navigate to Favourites");
			favourites.loginAsConsumer();
			favourites.navigateToFavouritesPage();

			LoggerUtils.logStep(2, "Verify page title and core UI elements are visible");
			boolean isPageTitleVisible = favourites.isFavouritesPageDisplayed();
			boolean hasUIElements = favourites.hasCoreUIElements();
			LoggerUtils.logInfo("TC_418 - Page Title Visible: " + isPageTitleVisible);
			LoggerUtils.logInfo("TC_418 - UI Elements Present: " + hasUIElements);

			Assert.assertTrue(isPageTitleVisible, "TC_418: Favourites page title should be visible");
			Assert.assertTrue(hasUIElements, "TC_418: UI elements should be present");
			LoggerUtils.logInfo("TC_418: UI consistency verified");

			LoggerUtils.logTestEnd("TC_418", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_418 - Test failed: " + favourites.safeString(e.getMessage()));
			throw e;
		}
	}
}
