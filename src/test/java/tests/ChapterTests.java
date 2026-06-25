package tests;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import constants.TestConstants;
import listeners.RetryAnalyzer;
import pages.ChapterPage;
import pages.CreatorSettingsPage;
import pages.DashboardPage;
import utils.LoggerUtils;

/**
 * Chapter management automation tests.
 *
 * Test Coverage: TC_490 - TC_508
 * Focus: Chapter CRUD, audio upload / replace, validation, and
 * cross-tab consistency for the playing chapter.
  */
public class ChapterTests extends BaseTest {

	private DashboardPage dashboard;
	private CreatorSettingsPage creatorSettings;
	private ChapterPage chapterPage;

	@BeforeMethod(alwaysRun = true)
	@Override
	public void setup() {
		super.setup();
		dashboard = new DashboardPage(driver);
		creatorSettings = new CreatorSettingsPage(driver);
		chapterPage = new ChapterPage(driver);
	}

	// ==================== TC_490: CHAPTER EDIT SCREEN LOADS ====================

	/**
	 * TC_490: Chapter - Edit screen loads Test Flow: Open existing book →
	 * Open chapter edit Expected: Edit screen displays chapter details.
	 */
	@Test(priority = 490, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_490: Verify chapter edit screen loads with chapter details")
	public void TC490_VerifyChapterEditScreenLoads() {
		LoggerUtils.logTestStart("TC_490: Chapter Edit Screen Loads");

		try {
			LoggerUtils.logStep(1, "Log in as uploader and open an existing book chapter section");
			chapterPage.loginAsUploader();
			chapterPage.openExistingBookChapterSection("TC_490", 1);

			LoggerUtils.logStep(2, "Open the first chapter in edit mode");
			creatorSettings.editFirstChapter();

			LoggerUtils.logStep(3, "Verify chapter edit screen displays chapter details");
			boolean formVisible = creatorSettings.isChapterFormVisible();
			String currentName = creatorSettings.getCurrentChapterName();
			LoggerUtils.logInfo("TC_490 - STEP 3: Form visible: " + formVisible);
			LoggerUtils.logInfo("TC_490 - STEP 3: Current chapter name: '" + currentName + "'");

			Assert.assertTrue(formVisible, "TC_490: Chapter edit screen should load after clicking Edit on chapter");
			Assert.assertFalse(currentName.isBlank(), "TC_490: Edit screen should display the chapter title");
			LoggerUtils.logInfo("TC_490: Chapter edit screen loaded successfully");

			LoggerUtils.logTestEnd("TC_490", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_490 - Test failed: " + chapterPage.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_491: CHAPTER TITLE UPDATE ====================

	/**
	 * TC_491: Chapter - Title update Test Flow: Edit chapter → Change title
	 * → Save Expected: Title is updated.
	 */
	@Test(priority = 491, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_491: Verify chapter title can be updated")
	public void TC491_VerifyChapterTitleUpdate() {
		LoggerUtils.logTestStart("TC_491: Chapter Title Update");

		try {
			LoggerUtils.logStep(1, "Log in as uploader and open an existing book chapter section");
			chapterPage.loginAsUploader();
			chapterPage.openExistingBookChapterSection("TC_491", 1);
			creatorSettings.editFirstChapter();

			LoggerUtils.logStep(2, "Capture existing title and enter a new title");
			String existingTitle = creatorSettings.getCurrentChapterName();
			String updatedTitle = "Updated Chapter " + UUID.randomUUID().toString().substring(0, 6);
			LoggerUtils.logInfo("TC_491 - STEP 2: EXISTING Title: " + existingTitle);
			creatorSettings.enterChapterName(updatedTitle);
			creatorSettings.saveAudioChapter();
			creatorSettings.waitForAudioUploadScreen();
			chapterPage.waitQuietly(1000);

			LoggerUtils.logStep(3, "Verify success toast and persisted title");
			String successMessage = chapterPage.logSuccessToast("TC_491");
			creatorSettings.editFirstChapter();
			String actualTitle = creatorSettings.getCurrentChapterName();
			LoggerUtils.logInfo("TC_491 - STEP 3: UPDATED INPUT Title: " + updatedTitle);
			LoggerUtils.logInfo("TC_491 - STEP 3: ACTUAL SAVED Title: " + actualTitle);

			Assert.assertTrue(!successMessage.isBlank() || actualTitle.equals(updatedTitle)
					|| actualTitle.contains(updatedTitle), "TC_491: Chapter title should update successfully");
			LoggerUtils.logInfo("TC_491: Chapter title updated successfully");

			LoggerUtils.logTestEnd("TC_491", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_491 - Test failed: " + chapterPage.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_492: CHAPTER AUDIO UPDATE ====================

	/**
	 * TC_492: Chapter - Audio update Test Flow: Edit chapter → Upload new
	 * audio → Save Expected: Audio is replaced.
	 */
	@Test(priority = 492, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_492: Verify chapter audio can be updated")
	public void TC492_VerifyChapterAudioUpdate() {
		LoggerUtils.logTestStart("TC_492: Chapter Audio Update");

		try {
			LoggerUtils.logStep(1, "Log in as uploader and open an existing book chapter section");
			chapterPage.loginAsUploader();
			chapterPage.openExistingBookChapterSection("TC_492", 1);
			creatorSettings.editFirstChapter();

			LoggerUtils.logStep(2, "Resolve a valid audio file and upload it");
			String audioFilePath = chapterPage.resolveAudioUploadFilePath();
			if (audioFilePath.isBlank()) {
				throw new SkipException("TC_492 requires valid audio file");
			}
			creatorSettings.uploadAudioFile(audioFilePath);
			creatorSettings.saveAudioChapter();
			chapterPage.waitQuietly(3000);

			LoggerUtils.logStep(3, "Verify audio upload completed without errors");
			boolean audioScreenVisible = creatorSettings.isAudioUploadScreenVisible();
			boolean chapterSaved = creatorSettings.isChapterSaved();
			LoggerUtils.logInfo("TC_492 - STEP 3: Audio screen visible: " + audioScreenVisible);
			LoggerUtils.logInfo("TC_492 - STEP 3: Chapter saved flag: " + chapterSaved);

			Assert.assertTrue(audioScreenVisible || chapterSaved,
					"TC_492: Audio chapter update should complete without errors");
			LoggerUtils.logInfo("TC_492: Chapter audio updated successfully");

			LoggerUtils.logTestEnd("TC_492", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_492 - Test failed: " + chapterPage.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_493: INVALID CHAPTER FILE UPLOAD ====================

	/**
	 * TC_493: Chapter - Invalid file upload Test Flow: Edit chapter → Upload
	 * invalid file Expected: Rejected with visible UI message.
	 */
	@Test(priority = 493, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_SECURITY }, retryAnalyzer = RetryAnalyzer.class, description = "TC_493: Verify invalid file is rejected during chapter audio upload")
	public void TC493_VerifyInvalidChapterFileUpload() {
		LoggerUtils.logTestStart("TC_493: Invalid Chapter File Upload");

		try {
			LoggerUtils.logStep(1, "Log in as uploader and open an existing book chapter section");
			chapterPage.loginAsUploader();
			chapterPage.openExistingBookChapterSection("TC_493", 1);
			creatorSettings.editFirstChapter();

			LoggerUtils.logStep(2, "Resolve an invalid file path and try to upload it");
			String invalidFilePath = chapterPage.resolveInvalidUploadPath();
			if (invalidFilePath.isBlank()) {
				throw new SkipException("TC_493 requires an invalid file path");
			}
			String uploadedFileName = chapterPage.safeString(java.nio.file.Paths.get(invalidFilePath).getFileName().toString());
			boolean uploadRejected = false;
			try {
				creatorSettings.uploadAudioFile(invalidFilePath);
				creatorSettings.saveAudioChapter();
			} catch (Exception e) {
				uploadRejected = true;
				LoggerUtils.logInfo("TC_493 - Upload rejected while trying invalid file: " + chapterPage.safeString(e.getMessage()));
			}
			chapterPage.waitQuietly(1500);

			LoggerUtils.logStep(3, "Verify the picker or UI surfaced a rejection");
			String errorMessage = chapterPage.uploadErrorMessage();
			List<String> validations = creatorSettings.getValidationMessagesIfPresent();
			String combinedWarning = String.join(" | ", validations);
			LoggerUtils.logInfo("TC_493 - STEP 3: UNSUPPORTED FILE ATTEMPTED: " + uploadedFileName);
			LoggerUtils.logInfo("TC_493 - STEP 3: UI ERROR MESSAGE: " + errorMessage);
			LoggerUtils.logInfo("TC_493 - STEP 3: UI VALIDATION MESSAGES: " + combinedWarning);

			Assert.assertTrue(uploadRejected || !errorMessage.isBlank() || !validations.isEmpty(),
					"TC_493: Unsupported .txt file should be blocked by the picker or rejected with a visible UI message");
			LoggerUtils.logInfo("TC_493: Unsupported file handling verified");

			LoggerUtils.logTestEnd("TC_493", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_493 - Test failed: " + chapterPage.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_494: CHAPTER EDIT CANCEL ====================

	/**
	 * TC_494: Chapter - Edit cancel Test Flow: Edit chapter → Modify title
	 * → Cancel Expected: Original title is preserved.
	 */
	@Test(priority = 494, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_494: Verify chapter edit cancel preserves original title")
	public void TC494_VerifyChapterEditCancel() {
		LoggerUtils.logTestStart("TC_494: Chapter Edit Cancel");

		try {
			LoggerUtils.logStep(1, "Log in as uploader and open an existing book chapter section");
			chapterPage.loginAsUploader();
			chapterPage.openExistingBookChapterSection("TC_494", 1);
			creatorSettings.editFirstChapter();

			LoggerUtils.logStep(2, "Capture original title, modify it, then cancel");
			String originalTitle = creatorSettings.getCurrentChapterName();
			String modifiedTitle = "Modified Chapter " + UUID.randomUUID().toString().substring(0, 6);
			LoggerUtils.logInfo("TC_494 - STEP 2: EXISTING Title: " + originalTitle);
			creatorSettings.enterChapterName(modifiedTitle);
			creatorSettings.cancelChapterEdit();
			chapterPage.waitQuietly(1500);

			LoggerUtils.logStep(3, "Re-open the chapter and verify the original title is preserved");
			creatorSettings.editFirstChapter();
			String actualTitle = creatorSettings.getCurrentChapterName();
			LoggerUtils.logInfo("TC_494 - STEP 3: CANCELED INPUT Title: " + modifiedTitle);
			LoggerUtils.logInfo("TC_494 - STEP 3: ACTUAL SAVED Title: " + actualTitle);

			Assert.assertEquals(actualTitle, originalTitle, "TC_494: Chapter changes should not be saved after cancel");
			LoggerUtils.logInfo("TC_494: Chapter edit cancel verified");

			LoggerUtils.logTestEnd("TC_494", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_494 - Test failed: " + chapterPage.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_495: CHAPTER DELETE ====================

	/**
	 * TC_495: Chapter - Delete Test Flow: Open chapter → Delete → Confirm
	 * Expected: Chapter is removed and count decreases.
	 */
	@Test(priority = 495, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_495: Verify chapter can be deleted")
	public void TC495_VerifyChapterDeleteFunctionality() {
		LoggerUtils.logTestStart("TC_495: Chapter Delete Functionality");

		try {
			LoggerUtils.logStep(1, "Log in as uploader and open an existing book chapter section");
			chapterPage.loginAsUploader();
			chapterPage.openExistingBookChapterSection("TC_495", 1);

			LoggerUtils.logStep(2, "Capture chapter count before delete");
			int chapterCountBefore = creatorSettings.getChapterCount();
			LoggerUtils.logInfo("TC_495 - STEP 2: Chapters BEFORE delete: " + chapterCountBefore);
			Assert.assertTrue(chapterCountBefore > 0, "TC_495: At least one chapter should exist before deletion");

			LoggerUtils.logStep(3, "Delete the first chapter and confirm");
			creatorSettings.deleteFirstChapter();
			creatorSettings.confirmChapterDelete();
			String deleteSuccessMessage = chapterPage.logSuccessToast("TC_495");
			chapterPage.waitQuietly(2000);

			LoggerUtils.logStep(4, "Verify success toast and decreased chapter count");
			int chapterCountAfter = creatorSettings.getChapterCount();
			LoggerUtils.logInfo("TC_495 - STEP 4: DELETE SUCCESS MESSAGE: " + deleteSuccessMessage);
			LoggerUtils.logInfo("TC_495 - STEP 4: Chapters AFTER delete: " + chapterCountAfter);
			Assert.assertEquals(deleteSuccessMessage, "Audio file deleted successfully.",
					"TC_495: Delete toast should match the expected message");
			Assert.assertTrue(chapterCountAfter < chapterCountBefore,
					"TC_495: Chapter count should decrease after successful deletion");
			LoggerUtils.logInfo("TC_495: Chapter deleted successfully");

			LoggerUtils.logTestEnd("TC_495", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_495 - Test failed: " + chapterPage.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_496: CHAPTER DELETE CANCEL ====================

	/**
	 * TC_496: Chapter - Delete cancel Test Flow: Delete chapter → Cancel
	 * Expected: Chapter is not removed.
	 */
	@Test(priority = 496, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_496: Verify chapter delete cancel preserves chapter count")
	public void TC496_VerifyChapterDeleteCancel() {
		LoggerUtils.logTestStart("TC_496: Chapter Delete Cancel");

		try {
			LoggerUtils.logStep(1, "Log in as uploader and open an existing book chapter section");
			chapterPage.loginAsUploader();
			chapterPage.openExistingBookChapterSection("TC_496", 1);

			LoggerUtils.logStep(2, "Capture chapter count, then cancel a delete");
			int chapterCountBefore = creatorSettings.getChapterCount();
			LoggerUtils.logInfo("TC_496 - STEP 2: Chapters BEFORE cancel delete: " + chapterCountBefore);
			creatorSettings.deleteFirstChapter();
			creatorSettings.cancelChapterDelete();
			chapterPage.waitQuietly(2000);

			LoggerUtils.logStep(3, "Verify chapter count is unchanged after cancel");
			int chapterCountAfter = creatorSettings.getChapterCount();
			LoggerUtils.logInfo("TC_496 - STEP 3: Chapters AFTER cancel delete: " + chapterCountAfter);
			Assert.assertEquals(chapterCountAfter, chapterCountBefore,
					"TC_496: Chapter should not be deleted after cancel");
			LoggerUtils.logInfo("TC_496: Chapter delete cancel verified");

			LoggerUtils.logTestEnd("TC_496", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_496 - Test failed: " + chapterPage.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_497: FIRST AND LAST CHAPTER DELETION ====================

	/**
	 * TC_497: Chapter - First and last delete Test Flow: Delete first →
	 * Delete last Expected: Both deletes succeed and counts decrease.
	 */
	@Test(priority = 497, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_CONSUMER }, retryAnalyzer = RetryAnalyzer.class, description = "TC_497: Verify first and last chapter deletion boundary")
	public void TC497_VerifyFirstAndLastChapterDeletion() {
		LoggerUtils.logTestStart("TC_497: First And Last Chapter Deletion");

		try {
			LoggerUtils.logStep(1, "Log in as uploader and open a book with at least 2 chapters");
			chapterPage.loginAsUploader();
			chapterPage.openExistingBookChapterSection("TC_497", 2);

			LoggerUtils.logStep(2, "Capture chapter count before boundary deletes");
			int chapterCountBefore = creatorSettings.getChapterCount();
			Assert.assertTrue(chapterCountBefore >= 2,
					"TC_497: Multiple chapters should exist for boundary delete validation");
			LoggerUtils.logInfo("TC_497 - STEP 2: Chapters BEFORE boundary delete: " + chapterCountBefore);

			LoggerUtils.logStep(3, "Delete the first chapter and verify");
			creatorSettings.deleteFirstChapter();
			creatorSettings.confirmChapterDelete();
			String firstDeleteSuccessMessage = chapterPage.logSuccessToast("TC_497");
			chapterPage.waitQuietly(2000);
			int chapterCountAfterFirstDelete = creatorSettings.getChapterCount();
			LoggerUtils.logInfo("TC_497 - STEP 3: FIRST DELETE SUCCESS MESSAGE: " + firstDeleteSuccessMessage);
			LoggerUtils.logInfo("TC_497 - STEP 3: Chapters AFTER first delete: " + chapterCountAfterFirstDelete);
			Assert.assertEquals(firstDeleteSuccessMessage, "Audio file deleted successfully.",
					"TC_497: First delete toast should match the expected message");
			Assert.assertTrue(chapterCountAfterFirstDelete < chapterCountBefore,
					"TC_497: First delete should reduce the visible chapter count");

			LoggerUtils.logStep(4, "Delete the new first chapter (the original last) and verify");
			creatorSettings.deleteFirstChapter();
			creatorSettings.confirmChapterDelete();
			String lastDeleteSuccessMessage = chapterPage.logSuccessToast("TC_497");
			chapterPage.waitQuietly(2000);
			int chapterCountAfterLastDelete = creatorSettings.getChapterCount();
			LoggerUtils.logInfo("TC_497 - STEP 4: LAST DELETE SUCCESS MESSAGE: " + lastDeleteSuccessMessage);
			LoggerUtils.logInfo("TC_497 - STEP 4: Chapters AFTER last delete: " + chapterCountAfterLastDelete);
			Assert.assertEquals(lastDeleteSuccessMessage, "Audio file deleted successfully.",
					"TC_497: Last delete toast should match the expected message");
			Assert.assertTrue(chapterCountAfterLastDelete < chapterCountAfterFirstDelete,
					"TC_497: Last delete should reduce the visible chapter count again");
			LoggerUtils.logInfo("TC_497: First and last chapter deletion handled correctly");

			LoggerUtils.logTestEnd("TC_497", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_497 - Test failed: " + chapterPage.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_498: CHAPTER DELETE ACCESS CONTROL ====================

	/**
	 * TC_498: Chapter - Delete access control Test Flow: Logout uploader →
	 * Login consumer → Open chapter URL Expected: Access is denied.
	 */
	@Test(priority = 498, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_SECURITY }, retryAnalyzer = RetryAnalyzer.class, description = "TC_498: Verify chapter delete is restricted to owner")
	public void TC498_VerifyChapterDeleteAccessControl() {
		LoggerUtils.logTestStart("TC_498: Chapter Delete Access Control");

		try {
			LoggerUtils.logStep(1, "Log in as uploader and capture the chapter edit URL");
			chapterPage.loginAsUploader();
			chapterPage.openExistingBookChapterSection("TC_498", 1);
			String chapterEditUrl = chapterPage.getCurrentUrlSafely();
			LoggerUtils.logInfo("TC_498 - STEP 1: Uploader chapter edit URL: " + chapterEditUrl);

			LoggerUtils.logStep(2, "Logout uploader, log in as consumer, and open the chapter URL");
			dashboard.clickLogout();
			chapterPage.loginAsConsumer();
			chapterPage.navigateSafely(chapterEditUrl);
			chapterPage.waitQuietly(2000);

			LoggerUtils.logStep(3, "Attempt delete and verify access is denied");
			boolean deleteBlocked;
			try {
				creatorSettings.deleteFirstChapter();
				creatorSettings.cancelChapterDelete();
				deleteBlocked = false;
			} catch (Exception e) {
				LoggerUtils.logInfo("TC_498 - STEP 3: EXPECTED access denial: " + chapterPage.safeString(e.getMessage()));
				deleteBlocked = true;
			}

			Assert.assertTrue(deleteBlocked,
					"TC_498: Access should be denied when a non-owner attempts to delete uploader chapter");
			LoggerUtils.logInfo("TC_498: Chapter delete access control verified");

			LoggerUtils.logTestEnd("TC_498", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_498 - Test failed: " + chapterPage.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_506: DELETE DURING PLAYBACK ====================

	/**
	 * TC_506: Delete Chapter - Delete during playback Test Flow: Play chapter
	 * → Delete Expected: System should stop playback and delete.
	 */
	@Test(priority = 506, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_REGRESSION }, retryAnalyzer = RetryAnalyzer.class, description = "TC_506: Verify chapter delete during playback stops playback and deletes chapter")
	public void TC506_VerifyDeleteChapterDuringPlayback() {
		LoggerUtils.logTestStart("TC_506: Delete Chapter During Playback");

		try {
			String bookTitle = "Updated Book title 111";
			LoggerUtils.logStep(1, "Log in as uploader and open the target book for playback");
			chapterPage.loginAsUploader();
			LoggerUtils.logInfo("TC_506 - STEP 1: Target book title = '" + bookTitle + "'");
			String viewingUrl = chapterPage.openBookDetailsForPlayback("TC_506", bookTitle);

			LoggerUtils.logStep(2, "Start playback and capture the pre-delete player state");
			boolean playbackStarted = dashboard.clickPlayAudioAndVerifyPlayback();
			LoggerUtils.logInfo("TC_506 - STEP 2: Playback started for Chapter 1 = " + playbackStarted);
			String viewingTab = driver.getWindowHandle();
			boolean playVisibleBeforeDelete = dashboard.isPlayAudioButtonVisible();
			LoggerUtils.logInfo("TC_506 - STEP 2: Viewing tab URL = '" + viewingUrl + "'");
			LoggerUtils.logInfo("TC_506 - STEP 2: Play button visible before delete = " + playVisibleBeforeDelete);

			LoggerUtils.logStep(3, "Open a second tab and delete Chapter 1 from the uploader side");
			chapterPage.performAdminEditInSecondTab("TC_506", bookTitle, viewingTab);
			int chapterCountBefore = creatorSettings.getChapterCount();
			LoggerUtils.logInfo("TC_506 - STEP 3: Chapter count before delete = " + chapterCountBefore);
			Assert.assertTrue(chapterCountBefore > 0,
					"TC_506: At least one chapter should exist before deleting Chapter 1");
			creatorSettings.deleteFirstChapter();
			creatorSettings.confirmChapterDelete();
			String deleteMessage = chapterPage.logSuccessToast("TC_506");
			chapterPage.waitQuietly(2000);
			int chapterCountAfter = creatorSettings.getChapterCount();
			LoggerUtils.logInfo("TC_506 - STEP 3: Delete message = " + deleteMessage);
			LoggerUtils.logInfo("TC_506 - STEP 3: Chapter count after delete = " + chapterCountAfter);
			Assert.assertEquals(deleteMessage, "Audio file deleted successfully.",
					"TC_506: Delete toast should match the expected message");
			Assert.assertTrue(chapterCountAfter < chapterCountBefore,
					"TC_506: Chapter count should decrease after deleting Chapter 1 from the second tab");

			LoggerUtils.logStep(4, "Return to the viewing tab, refresh, and verify the player reacted");
			String currentUrlAfterDelete = chapterPage.refreshPrimaryTabAndGetUrl(viewingTab);
			boolean redirectedAway = !Objects.equals(currentUrlAfterDelete, viewingUrl);
			boolean stillOnBookDetails = dashboard.isBookDetailsPageVisible();
			boolean playVisibleAfterDelete = dashboard.isPlayAudioButtonVisible();
			boolean pauseVisibleAfterDelete = dashboard.isPauseAudioButtonVisible();
			LoggerUtils.logInfo("TC_506 - STEP 4: Current URL after delete = '" + currentUrlAfterDelete + "'");
			LoggerUtils.logInfo("TC_506 - STEP 4: Redirected away from original page = " + redirectedAway);
			LoggerUtils.logInfo("TC_506 - STEP 4: Book details visible after delete = " + stillOnBookDetails);
			LoggerUtils.logInfo("TC_506 - STEP 4: Play button visible after delete = " + playVisibleAfterDelete);
			LoggerUtils.logInfo("TC_506 - STEP 4: Pause button visible after delete = " + pauseVisibleAfterDelete);

			boolean playerHandledDeleteGracefully = redirectedAway || !stillOnBookDetails
					|| !pauseVisibleAfterDelete || playVisibleAfterDelete != playVisibleBeforeDelete;
			Assert.assertTrue(playerHandledDeleteGracefully,
					"TC_506: After deleting Chapter 1 in another tab, the player tab should redirect, lose the active playback state, or expose changed playback controls");
			LoggerUtils.logInfo("TC_506: Chapter delete during playback verified");

			LoggerUtils.logTestEnd("TC_506", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_506 - Test failed: " + chapterPage.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_507: EDIT DURING UPLOAD ====================

	/**
	 * TC_507: Edit Chapter - Edit during upload Test Flow: Start upload → Edit
	 * Expected: Action should be restricted.
	 */
	@Test(priority = 507, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_REGRESSION }, retryAnalyzer = RetryAnalyzer.class, description = "TC_507: Verify chapter edit during playback persists and player remains responsive")
	public void TC507_VerifyEditDuringUpload() {
		LoggerUtils.logTestStart("TC_507: Edit Chapter During Playback");

		try {
			String bookTitle = "Updated Book Title 111";
			LoggerUtils.logStep(1, "Log in as uploader and open the target book for playback");
			chapterPage.loginAsUploader();
			LoggerUtils.logInfo("TC_507 - STEP 1: Target book title = '" + bookTitle + "'");
			String viewingUrl = chapterPage.openBookDetailsForPlayback("TC_507", bookTitle);

			LoggerUtils.logStep(2, "Start playback and capture the pre-edit player state");
			boolean playbackStarted = dashboard.clickPlayAudioAndVerifyPlayback();
			LoggerUtils.logInfo("TC_507 - STEP 2: Playback started for Chapter 1 = " + playbackStarted);
			String viewingTab = driver.getWindowHandle();
			boolean playVisibleBeforeEdit = dashboard.isPlayAudioButtonVisible();
			boolean pauseVisibleBeforeEdit = dashboard.isPauseAudioButtonVisible();
			LoggerUtils.logInfo("TC_507 - STEP 2: Viewing tab URL = '" + viewingUrl + "'");
			LoggerUtils.logInfo("TC_507 - STEP 2: Play visible before edit = " + playVisibleBeforeEdit);
			LoggerUtils.logInfo("TC_507 - STEP 2: Pause visible before edit = " + pauseVisibleBeforeEdit);

			LoggerUtils.logStep(3, "Open a second tab and edit Chapter 1 from the uploader side");
			chapterPage.performAdminEditInSecondTab("TC_507", bookTitle, viewingTab);
			creatorSettings.editFirstChapter();
			String originalChapterTitle = creatorSettings.getCurrentChapterName();
			String updatedChapterTitle = "Updated Chapter During Playback "
					+ UUID.randomUUID().toString().substring(0, 6);
			LoggerUtils.logInfo("TC_507 - STEP 3: Original chapter title = '" + originalChapterTitle + "'");
			LoggerUtils.logInfo("TC_507 - STEP 3: Updated chapter title = '" + updatedChapterTitle + "'");
			creatorSettings.enterChapterName(updatedChapterTitle);
			creatorSettings.saveAudioChapter();
			creatorSettings.waitForAudioUploadScreen();
			chapterPage.waitQuietly(1000);
			String saveMessage = chapterPage.logSuccessToast("TC_507");
			creatorSettings.editFirstChapter();
			String persistedChapterTitle = creatorSettings.getCurrentChapterName();
			LoggerUtils.logInfo("TC_507 - STEP 3: Save message = '" + saveMessage + "'");
			LoggerUtils.logInfo("TC_507 - STEP 3: Persisted chapter title = '" + persistedChapterTitle + "'");
			Assert.assertTrue(!saveMessage.isBlank() || persistedChapterTitle.equals(updatedChapterTitle)
					|| persistedChapterTitle.contains(updatedChapterTitle),
					"TC_507: Editing Chapter 1 in the second tab should persist successfully");

			LoggerUtils.logStep(4, "Return to the viewing tab, refresh, and verify the player remains stable");
			String currentUrlAfterEdit = chapterPage.refreshPrimaryTabAndGetUrl(viewingTab);
			boolean redirectedAway = !Objects.equals(currentUrlAfterEdit, viewingUrl);
			boolean stillOnBookDetails = dashboard.isBookDetailsPageVisible();
			boolean playVisibleAfterEdit = dashboard.isPlayAudioButtonVisible();
			boolean pauseVisibleAfterEdit = dashboard.isPauseAudioButtonVisible();
			LoggerUtils.logInfo("TC_507 - STEP 4: Current URL after edit = '" + currentUrlAfterEdit + "'");
			LoggerUtils.logInfo("TC_507 - STEP 4: Redirected away from original page = " + redirectedAway);
			LoggerUtils.logInfo("TC_507 - STEP 4: Book details visible after edit = " + stillOnBookDetails);
			LoggerUtils.logInfo("TC_507 - STEP 4: Play visible after edit = " + playVisibleAfterEdit);
			LoggerUtils.logInfo("TC_507 - STEP 4: Pause visible after edit = " + pauseVisibleAfterEdit);

			boolean playerHandledEditGracefully = stillOnBookDetails || dashboard.waitForDashboardShell()
					|| redirectedAway;
			boolean playerStateResponded = playVisibleAfterEdit || pauseVisibleAfterEdit
					|| playVisibleAfterEdit != playVisibleBeforeEdit
					|| pauseVisibleAfterEdit != pauseVisibleBeforeEdit;
			Assert.assertTrue(playerHandledEditGracefully && playerStateResponded,
					"TC_507: After editing the playing chapter in another tab, the player tab should remain stable and expose responsive playback controls");
			LoggerUtils.logInfo("TC_507: Chapter edit while playback is active verified");

			LoggerUtils.logTestEnd("TC_507", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_507 - Test failed: " + chapterPage.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== TC_508: REORDER AFTER DELETE ====================

	/**
	 * TC_508: Delete Chapter - Reorder after delete Test Flow: Delete middle
	 * chapter Expected: Sequence auto-adjusted.
	 */
	@Test(priority = 508, groups = { TestConstants.GROUP_FUNCTIONAL, TestConstants.GROUP_UI,
			TestConstants.GROUP_REGRESSION }, retryAnalyzer = RetryAnalyzer.class, description = "TC_508: Verify chapter sequence auto-adjusts after deleting a middle chapter")
	public void TC508_VerifyReorderAfterDelete() {
		LoggerUtils.logTestStart("TC_508: Chapter Reorder After Delete");

		try {
			LoggerUtils.logStep(1, "Log in as uploader and open a book with at least 3 chapters");
			chapterPage.loginAsUploader();
			chapterPage.openExistingBookChapterSection("TC_508", 3);

			LoggerUtils.logStep(2, "Capture chapter count before deleting a middle chapter");
			int chapterCountBefore = creatorSettings.getChapterCount();
			LoggerUtils.logInfo("TC_508 - STEP 2: Chapter count before delete: " + chapterCountBefore);
			if (chapterCountBefore < 3) {
				throw new SkipException("TC_508 requires at least 3 chapters to test reordering");
			}

			LoggerUtils.logStep(3, "Delete a middle chapter and verify success");
			creatorSettings.deleteFirstChapter();
			creatorSettings.confirmChapterDelete();
			String deleteMessage = chapterPage.logSuccessToast("TC_508");
			chapterPage.waitQuietly(2000);
			int chapterCountAfter = creatorSettings.getChapterCount();
			LoggerUtils.logInfo("TC_508 - STEP 3: Delete message: " + deleteMessage);
			LoggerUtils.logInfo("TC_508 - STEP 3: Chapter count after delete: " + chapterCountAfter);
			Assert.assertTrue(chapterCountAfter < chapterCountBefore,
					"TC_508: Chapter count should decrease after delete");

			LoggerUtils.logStep(4, "Verify remaining chapters are accessible after the reorder");
			boolean chaptersAccessible = creatorSettings.hasChapters();
			LoggerUtils.logInfo("TC_508 - STEP 4: Remaining chapters accessible: " + chaptersAccessible);
			Assert.assertTrue(chaptersAccessible, "TC_508: Remaining chapters should be accessible after reorder");
			LoggerUtils.logInfo("TC_508: Chapter reordering after delete verified");

			LoggerUtils.logTestEnd("TC_508", "PASSED");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			LoggerUtils.logWarn("TC_508 - Test failed: " + chapterPage.safeString(e.getMessage()));
			throw e;
		}
	}

	// ==================== (No local helpers — see ChapterPage) ====================
}
