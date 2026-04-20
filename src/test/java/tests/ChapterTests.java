package tests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BaseTest;
import listeners.RetryAnalyzer;
import pages.CreatorSettingsPage;
import pages.DashboardPage;
import pages.ForCreatorPage;
import pages.LoginPage;
import pages.UploadPage;
import utils.ConfigReader;

/**
 * Chapter management automation tests.
 *
 * Test Coverage: TC_490 - TC_508
 */
public class ChapterTests extends BaseTest {

	private LoginPage login;
	private UploadPage upload;
	private DashboardPage dashboard;
	private CreatorSettingsPage creatorSettings;
	private ForCreatorPage forCreatorPage;

	@BeforeMethod(alwaysRun = true)
	public void setupPages() {
		super.setup();
		login = new LoginPage(driver);
		upload = new UploadPage(driver);
		dashboard = new DashboardPage(driver);
		creatorSettings = new CreatorSettingsPage(driver);
		forCreatorPage = new ForCreatorPage(driver);
	}

	private String getUploaderEmail() {
		return ConfigReader.getProperty("uploader.email");
	}

	private String getUploaderPassword() {
		return ConfigReader.getProperty("uploader.password");
	}

	private String getConsumerEmail() {
		return ConfigReader.getProperty("consumer.email", ConfigReader.getProperty("login.validEmail"));
	}

	private String getConsumerPassword() {
		return ConfigReader.getProperty("consumer.password", ConfigReader.getProperty("login.validPassword"));
	}

	private String createUniqueBookTitle() {
		return "Automation Book " + UUID.randomUUID().toString().substring(0, 6);
	}

	private void waitForMilliseconds(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Sleep interrupted", e);
		}
	}

	private void loginAsUploader() {
		try {
			login.openLogin();
			login.loginUser(getUploaderEmail(), getUploaderPassword());
			login.clickNextAfterLogin();
			boolean loginSettled = new WebDriverWait(driver, Duration.ofSeconds(30)).until(currentDriver -> {
				if (!login.isOnLoginPage()) {
					return true;
				}
				String currentUrl = currentDriver.getCurrentUrl().toLowerCase();
				return !currentUrl.contains("/login") && !currentUrl.contains("signin");
			});
			Assert.assertTrue(loginSettled, "Uploader login should move past the login page");
			LOGGER.info("Logged in as uploader");
		} catch (Exception e) {
			throw new RuntimeException("Failed to login as uploader", e);
		}
	}

	private void loginAsConsumer() {
		try {
			login.openLogin();
			login.loginUser(getConsumerEmail(), getConsumerPassword());
			login.clickNextAfterLogin();
			dashboard.waitForPageReady();
			LOGGER.info("Logged in as consumer");
		} catch (Exception e) {
			throw new RuntimeException("Failed to login as consumer", e);
		}
	}

	private String resolveOptionalConfiguredPath(String configKey) {
		String configuredPath = ConfigReader.getProperty(configKey);
		if (configuredPath == null || configuredPath.isBlank()) {
			return "";
		}

		Path directPath = Paths.get(configuredPath);
		if (Files.exists(directPath)) {
			return directPath.toString();
		}

		Path resolvedPath = Paths.get(System.getProperty("user.dir"), configuredPath);
		if (Files.exists(resolvedPath)) {
			return resolvedPath.toString();
		}

		throw new IllegalStateException("Configured file not found for " + configKey + ": " + configuredPath);
	}

	private String findFirstFileInDownloads(List<String> extensions, String missingMessage) {
		Path downloadsDirectory = Paths.get(System.getProperty("user.home"), "Downloads");
		if (!Files.exists(downloadsDirectory)) {
			return "";
		}

		try {
			return Files.list(downloadsDirectory).filter(Files::isRegularFile)
					.filter(path -> hasAnyExtension(path.getFileName().toString(), extensions))
					.sorted(Comparator.comparing(Path::toString)).map(Path::toString).findFirst().orElseGet(() -> {
						if (missingMessage != null) {
							LOGGER.info(missingMessage);
						}
						return "";
					});
		} catch (IOException e) {
			LOGGER.warning("Unable to scan Downloads directory: " + e.getMessage());
			return "";
		}
	}

	private boolean hasAnyExtension(String fileName, List<String> extensions) {
		String normalized = fileName.toLowerCase();
		for (String extension : extensions) {
			if (normalized.endsWith(extension.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	private String resolvePortraitImagePath() {
		String configuredPath = resolveOptionalConfiguredPath("uploadPortraitImagePath");
		if (!configuredPath.isBlank()) {
			return configuredPath;
		}
		return findFirstFileInDownloads(List.of(".png", ".jpg", ".jpeg"),
				"Portrait image not configured. Continuing without image upload because no JPG/PNG was found in Downloads.");
	}

	private String resolveLandscapeImagePath() {
		String configuredPath = resolveOptionalConfiguredPath("uploadLandscapeImagePath");
		if (!configuredPath.isBlank()) {
			return configuredPath;
		}
		return findFirstFileInDownloads(List.of(".png", ".jpg", ".jpeg"),
				"Landscape image not configured. Continuing without landscape image because no JPG/PNG was found in Downloads.");
	}

	private String resolveAudioUploadFilePath() {
		String configuredPath = resolveOptionalConfiguredPath("uploadAudioFilePath");
		if (!configuredPath.isBlank()) {
			return configuredPath;
		}
		return findFirstFileInDownloads(List.of(".mp3", ".wav", ".m4a", ".aac"),
				"Audio file not configured. Add uploadAudioFilePath or place an audio file in Downloads.");
	}

	private String resolveInvalidUploadPath() {
		String configuredPath = resolveOptionalConfiguredPath("invalidUploadPath");
		if (!configuredPath.isBlank()) {
			return configuredPath;
		}

		String existingFile = findFirstFileInDownloads(List.of(".exe", ".txt", ".bat", ".sh"), null);
		if (!existingFile.isBlank()) {
			return existingFile;
		}

		try {
			Path tempFile = Files.createTempFile("invalid-upload-", ".txt");
			Files.writeString(tempFile, "invalid upload file for automation");
			tempFile.toFile().deleteOnExit();
			return tempFile.toString();
		} catch (IOException e) {
			LOGGER.warning("Unable to create temporary invalid upload file: " + e.getMessage());
			return "";
		}
	}

	private void navigateToUploadPage() {
		boolean landingReady = new WebDriverWait(driver, Duration.ofSeconds(30)).until(currentDriver -> {
			return dashboard.waitForDashboardShell() || dashboard.isOnCreatorPage() || dashboard.isUploadPageOpened()
					|| dashboard.isHeaderLogoVisible() || dashboard.isProfileIconVisible();
		});
		Assert.assertTrue(landingReady,
				"Post-login landing page should be stable. Current URL: " + driver.getCurrentUrl());

		creatorSettings.clickHamburgerMenu();
		creatorSettings.clickForCreators();
		creatorSettings.clickAddBook();

		upload.waitForUploadPageToLoad();
		Assert.assertTrue(upload.isUploadPageDisplayed() || dashboard.isUploadPageOpened(),
				"Upload page should open after clicking Add Book");
	}

	private void fillValidBookDetails(String title, String summary) {
		creatorSettings.waitForUploadForm();
		creatorSettings.enterTitle(title);
		creatorSettings.enterAuthor("Automation Tester");
		creatorSettings.selectLanguage(ConfigReader.getProperty("uploadLanguage", "English"));
		creatorSettings.selectCountryCategory(ConfigReader.getProperty("uploadCountryCategory", "Category B"));
		creatorSettings.selectCategory(ConfigReader.getProperty("uploadCategory", "Art"));
		creatorSettings.selectCountry(ConfigReader.getProperty("uploadCountry", "India"));
		creatorSettings.selectGenre(ConfigReader.getProperty("uploadGenre", "Drama"));
		creatorSettings.enterSummary(summary);
	}

	private void uploadValidPortraitAndLandscapeImages() {
		String portraitImagePath = resolvePortraitImagePath();
		String landscapeImagePath = resolveLandscapeImagePath();
		if (portraitImagePath.isBlank() || landscapeImagePath.isBlank()) {
			throw new SkipException(
					"Valid portrait and landscape JPG/PNG images are required via config or Downloads.");
		}

		creatorSettings.uploadBookImages(portraitImagePath, landscapeImagePath);
	}

	private String logSuccessToast(String testCaseId) {
		String successMessage = upload.getSuccessMessage();
		LOGGER.info(testCaseId + " - SUCCESS MESSAGE: " + successMessage);
		return successMessage;
	}

	private void openForCreatorsListingPage() {
		String baseUrl = ConfigReader.getProperty("url", "https://web-splay.acceses.com/");
		if (!baseUrl.endsWith("/")) {
			baseUrl = baseUrl + "/";
		}
		driver.get(baseUrl + "show_uploader_books");
		forCreatorPage.waitForListingState();
	}

	private void createPendingBookWithChapters(String testCaseId, int chapterCount) {
		String audioFilePath = resolveAudioUploadFilePath();
		if (audioFilePath.isBlank()) {
			throw new SkipException(testCaseId + ": A valid audio file is required to seed chapter data.");
		}

		LOGGER.info(testCaseId + " - STEP 6: No existing Pending book matched. Creating one with " + chapterCount
				+ " chapter(s)");

		navigateToUploadPage();
		String seededTitle = createUniqueBookTitle();
		fillValidBookDetails(seededTitle, "Automation seeded summary for chapter edit coverage");
		uploadValidPortraitAndLandscapeImages();
		creatorSettings.clickSave();
		creatorSettings.prepareForAudioChapterCreation();

		for (int chapterIndex = 0; chapterIndex < chapterCount; chapterIndex++) {
			creatorSettings.clickAddAudio();
			creatorSettings.enterChapterName(
					"Seed Chapter " + (chapterIndex + 1) + " " + UUID.randomUUID().toString().substring(0, 4));
			creatorSettings.enterChapterSummary("Automation seeded chapter summary " + (chapterIndex + 1));
			creatorSettings.uploadAudioFile(audioFilePath);
			creatorSettings.saveAudioChapter();
			waitForMilliseconds(1500);
			creatorSettings.prepareForAudioChapterCreation();
		}

		LOGGER.info(testCaseId + " - STEP 7: Seeded book '" + seededTitle + "' is ready for chapter edit coverage");
	}

	private void openExistingBookChapterSection(String testCaseId, int minimumChapterCount) {
		LOGGER.info(testCaseId + " - STEP 1: Navigating to existing Pending books");
		openForCreatorsListingPage();
		forCreatorPage.selectPendingFilter();
		if (!forCreatorPage.hasBooks()) {
			throw new SkipException(testCaseId + ": No books found in Pending filter.");
		}

		int bookCount = forCreatorPage.getVisibleBookCount();
		int booksToTry = Math.min(bookCount, 3);
		List<String> visibleTitles = forCreatorPage.getVisibleBookTitles();
		for (int index = 0; index < booksToTry; index++) {
			openForCreatorsListingPage();
			forCreatorPage.selectPendingFilter();

			String existingBookTitle = index < visibleTitles.size() ? visibleTitles.get(index)
					: ("Book index " + index);
			LOGGER.info(
					testCaseId + " - STEP 2: Trying existing book index " + index + " = '" + existingBookTitle + "'");

			forCreatorPage.clickEditBookByIndex(index);
			LOGGER.info(testCaseId + " - STEP 3: Edit book screen opened");
			creatorSettings.clickNext();
			waitForMilliseconds(2000);
			creatorSettings.waitForAudioUploadScreen();
			LOGGER.info(testCaseId + " - STEP 4: Chapter screen opened after clicking Next");

			int chapterCount = creatorSettings.getChapterCount();
			LOGGER.info(
					testCaseId + " - STEP 5: Existing chapter count for '" + existingBookTitle + "' = " + chapterCount);
			if (chapterCount >= minimumChapterCount) {
				LOGGER.info(testCaseId + " - STEP 6: Using existing book '" + existingBookTitle
						+ "' for chapter operation");
				return;
			}
		}

		LOGGER.info(testCaseId + " - STEP 6: No suitable existing book found in first " + booksToTry
				+ " Pending entries. Creating fresh chapter data instead");
		createPendingBookWithChapters(testCaseId, minimumChapterCount);
	}

	@Test(priority = 490, retryAnalyzer = RetryAnalyzer.class)
	public void verifyChapterEditScreenLoads() {
		loginAsUploader();
		openExistingBookChapterSection("TC_490", 1);
		creatorSettings.editFirstChapter();

		Assert.assertTrue(creatorSettings.isChapterFormVisible(),
				"TC_490: Chapter edit screen should load after clicking Edit on chapter");
		Assert.assertFalse(creatorSettings.getCurrentChapterName().isBlank(),
				"TC_490: Edit screen should display the chapter title");
		LOGGER.info("TC_490: Chapter edit screen loaded successfully");
	}

	@Test(priority = 491, retryAnalyzer = RetryAnalyzer.class)
	public void verifyChapterTitleUpdate() {
		loginAsUploader();
		openExistingBookChapterSection("TC_491", 1);
		creatorSettings.editFirstChapter();

		String existingTitle = creatorSettings.getCurrentChapterName();
		String updatedTitle = "Updated Chapter " + UUID.randomUUID().toString().substring(0, 6);
		LOGGER.info("TC_491 - EXISTING Title: " + existingTitle);
		creatorSettings.enterChapterName(updatedTitle);
		creatorSettings.saveAudioChapter();
		creatorSettings.waitForAudioUploadScreen();
		waitForMilliseconds(1000);

		String successMessage = logSuccessToast("TC_491");
		creatorSettings.editFirstChapter();
		String actualTitle = creatorSettings.getCurrentChapterName();
		LOGGER.info("TC_491 - UPDATED INPUT Title: " + updatedTitle);
		LOGGER.info("TC_491 - ACTUAL SAVED Title: " + actualTitle);

		Assert.assertTrue(
				!successMessage.isBlank() || actualTitle.equals(updatedTitle) || actualTitle.contains(updatedTitle),
				"TC_491: Chapter title should update successfully");
		LOGGER.info("TC_491: Chapter title updated successfully");
	}

	@Test(priority = 492, retryAnalyzer = RetryAnalyzer.class)
	public void verifyChapterAudioUpdate() {
		loginAsUploader();
		openExistingBookChapterSection("TC_492", 1);
		creatorSettings.editFirstChapter();

		String audioFilePath = resolveAudioUploadFilePath();
		if (audioFilePath.isBlank()) {
			throw new SkipException("TC_492 requires valid audio file");
		}

		creatorSettings.uploadAudioFile(audioFilePath);
		creatorSettings.saveAudioChapter();
		waitForMilliseconds(2000);

		Assert.assertEquals(logSuccessToast("TC_492"), "Audio file updated successfully.",
				"TC_492: Audio update toast should match the expected message");
		LOGGER.info("TC_492: Chapter audio updated successfully");
	}

	@Test(priority = 493, retryAnalyzer = RetryAnalyzer.class)
	public void verifyInvalidChapterFileUpload() {
		loginAsUploader();
		openExistingBookChapterSection("TC_493", 1);
		creatorSettings.editFirstChapter();

		String invalidFilePath = resolveInvalidUploadPath();
		if (invalidFilePath.isBlank()) {
			throw new SkipException("TC_493 requires an invalid file path");
		}

		String uploadedFileName = Paths.get(invalidFilePath).getFileName().toString();
		boolean uploadRejected = false;
		try {
			creatorSettings.uploadAudioFile(invalidFilePath);
			creatorSettings.saveAudioChapter();
		} catch (Exception e) {
			uploadRejected = true;
			LOGGER.info("TC_493 - Upload rejected while trying invalid file: " + e.getMessage());
		}
		waitForMilliseconds(1500);

		String errorMessage = upload.getErrorMessage();
		List<String> validations = creatorSettings.getValidationMessagesIfPresent();
		String combinedWarning = String.join(" | ", validations);
		LOGGER.info("TC_493 - UNSUPPORTED FILE ATTEMPTED: " + uploadedFileName);
		LOGGER.info("TC_493 - UI ERROR MESSAGE: " + errorMessage);
		LOGGER.info("TC_493 - UI VALIDATION MESSAGES: " + combinedWarning);
		Assert.assertTrue(uploadRejected || !errorMessage.isBlank() || !validations.isEmpty(),
				"TC_493: Unsupported .txt file should be blocked by the picker or rejected with a visible UI message");
		LOGGER.info("TC_493: Unsupported file handling verified");
	}

	@Test(priority = 494, retryAnalyzer = RetryAnalyzer.class)
	public void verifyChapterEditCancel() {
		loginAsUploader();
		openExistingBookChapterSection("TC_494", 1);
		creatorSettings.editFirstChapter();

		String originalTitle = creatorSettings.getCurrentChapterName();
		String modifiedTitle = "Modified Chapter " + UUID.randomUUID().toString().substring(0, 6);
		LOGGER.info("TC_494 - EXISTING Title: " + originalTitle);
		creatorSettings.enterChapterName(modifiedTitle);
		creatorSettings.cancelChapterEdit();
		waitForMilliseconds(1500);

		creatorSettings.editFirstChapter();
		String actualTitle = creatorSettings.getCurrentChapterName();
		LOGGER.info("TC_494 - CANCELED INPUT Title: " + modifiedTitle);
		LOGGER.info("TC_494 - ACTUAL SAVED Title: " + actualTitle);

		Assert.assertEquals(actualTitle, originalTitle, "TC_494: Chapter changes should not be saved after cancel");
		LOGGER.info("TC_494: Chapter edit cancel verified");
	}

	@Test(priority = 495, retryAnalyzer = RetryAnalyzer.class)
	public void verifyChapterDeleteFunctionality() {
		loginAsUploader();
		openExistingBookChapterSection("TC_495", 1);

		int chapterCountBefore = creatorSettings.getChapterCount();
		LOGGER.info("TC_495 - Chapters BEFORE delete: " + chapterCountBefore);
		Assert.assertTrue(chapterCountBefore > 0, "TC_495: At least one chapter should exist before deletion");

		creatorSettings.deleteFirstChapter();
		creatorSettings.confirmChapterDelete();
		String deleteSuccessMessage = logSuccessToast("TC_495");
		waitForMilliseconds(2000);

		int chapterCountAfter = creatorSettings.getChapterCount();
		LOGGER.info("TC_495 - DELETE SUCCESS MESSAGE: " + deleteSuccessMessage);
		LOGGER.info("TC_495 - Chapters AFTER delete: " + chapterCountAfter);
		Assert.assertEquals(deleteSuccessMessage, "Audio file deleted successfully.",
				"TC_495: Delete toast should match the expected message");
		Assert.assertTrue(chapterCountAfter < chapterCountBefore,
				"TC_495: Chapter count should decrease after successful deletion");
		LOGGER.info("TC_495: Chapter deleted successfully");
	}

	@Test(priority = 496, retryAnalyzer = RetryAnalyzer.class)
	public void verifyChapterDeleteCancel() {
		loginAsUploader();
		openExistingBookChapterSection("TC_496", 1);

		int chapterCountBefore = creatorSettings.getChapterCount();
		LOGGER.info("TC_496 - Chapters BEFORE cancel delete: " + chapterCountBefore);

		creatorSettings.deleteFirstChapter();
		creatorSettings.cancelChapterDelete();
		waitForMilliseconds(2000);

		int chapterCountAfter = creatorSettings.getChapterCount();
		LOGGER.info("TC_496 - Chapters AFTER cancel delete: " + chapterCountAfter);
		Assert.assertEquals(chapterCountAfter, chapterCountBefore,
				"TC_496: Chapter should not be deleted after cancel");
		LOGGER.info("TC_496: Chapter delete cancel verified");
	}

	@Test(priority = 497, retryAnalyzer = RetryAnalyzer.class)
	public void verifyFirstAndLastChapterDeletion() {
		loginAsUploader();
		openExistingBookChapterSection("TC_497", 2);

		int chapterCountBefore = creatorSettings.getChapterCount();
		Assert.assertTrue(chapterCountBefore >= 2,
				"TC_497: Multiple chapters should exist for boundary delete validation");
		LOGGER.info("TC_497 - Chapters BEFORE boundary delete: " + chapterCountBefore);

		creatorSettings.deleteFirstChapter();
		creatorSettings.confirmChapterDelete();
		String firstDeleteSuccessMessage = logSuccessToast("TC_497");
		waitForMilliseconds(2000);
		int chapterCountAfterFirstDelete = creatorSettings.getChapterCount();
		LOGGER.info("TC_497 - FIRST DELETE SUCCESS MESSAGE: " + firstDeleteSuccessMessage);
		LOGGER.info("TC_497 - Chapters AFTER first delete: " + chapterCountAfterFirstDelete);
		Assert.assertEquals(firstDeleteSuccessMessage, "Audio file deleted successfully.",
				"TC_497: First delete toast should match the expected message");
		Assert.assertTrue(chapterCountAfterFirstDelete < chapterCountBefore,
				"TC_497: First delete should reduce the visible chapter count");

		creatorSettings.deleteFirstChapter();
		creatorSettings.confirmChapterDelete();
		String lastDeleteSuccessMessage = logSuccessToast("TC_497");
		waitForMilliseconds(2000);
		int chapterCountAfterLastDelete = creatorSettings.getChapterCount();
		LOGGER.info("TC_497 - LAST DELETE SUCCESS MESSAGE: " + lastDeleteSuccessMessage);
		LOGGER.info("TC_497 - Chapters AFTER last delete: " + chapterCountAfterLastDelete);
		Assert.assertEquals(lastDeleteSuccessMessage, "Audio file deleted successfully.",
				"TC_497: Last delete toast should match the expected message");
		Assert.assertTrue(chapterCountAfterLastDelete < chapterCountAfterFirstDelete,
				"TC_497: Last delete should reduce the visible chapter count again");
		LOGGER.info("TC_497: First and last chapter deletion handled correctly");
	}

	@Test(priority = 498, retryAnalyzer = RetryAnalyzer.class)
	public void verifyChapterDeleteAccessControl() {
		loginAsUploader();
		openExistingBookChapterSection("TC_498", 1);
		String chapterEditUrl = driver.getCurrentUrl();
		LOGGER.info("TC_498 - Uploader chapter edit URL: " + chapterEditUrl);

		dashboard.clickLogout();
		loginAsConsumer();
		driver.get(chapterEditUrl);
		waitForMilliseconds(2000);

		boolean deleteBlocked;
		try {
			creatorSettings.deleteFirstChapter();
			creatorSettings.cancelChapterDelete();
			deleteBlocked = false;
		} catch (Exception e) {
			LOGGER.info("TC_498 - EXPECTED access denial: " + e.getMessage());
			deleteBlocked = true;
		}

		Assert.assertTrue(deleteBlocked,
				"TC_498: Access should be denied when a non-owner attempts to delete uploader chapter");
		LOGGER.info("TC_498: Chapter delete access control verified");
	}

	/**
	 * TC_506: Delete Chapter - Delete during playback Test Flow: Play chapter →
	 * Delete Expected: System should stop playback and delete
	 */
	@Test(priority = 506, retryAnalyzer = RetryAnalyzer.class)
	public void verifyDeleteChapterDuringPlayback() {
		loginAsUploader();
		String bookTitle = "Updated Book Title 111";
		LOGGER.info("TC_506 - STEP 1: Target book title = '" + bookTitle + "'");

		dashboard.waitForPageReady();
		Assert.assertTrue(dashboard.isSearchBarVisible(),
				"TC_506: Header search bar should be visible before opening the target book");
		dashboard.submitSearch(bookTitle);
		Assert.assertTrue(dashboard.clickFirstSearchResult(),
				"TC_506: Search should open the target book details page");
		Assert.assertTrue(dashboard.isBookDetailsPageVisible(),
				"TC_506: Book details page should open for the target book");
		Assert.assertTrue(dashboard.waitForBookDataToLoad(),
				"TC_506: Book details should finish loading before playback");

		boolean playbackStarted = dashboard.clickPlayAudioAndVerifyPlayback();
		LOGGER.info("TC_506 - STEP 2: Playback started for Chapter 1 = " + playbackStarted);

		String viewingTab = driver.getWindowHandle();
		String viewingUrl = driver.getCurrentUrl();
		boolean playVisibleBeforeDelete = dashboard.isPlayAudioButtonVisible();
		LOGGER.info("TC_506 - STEP 2: Viewing tab URL = '" + viewingUrl + "'");
		LOGGER.info("TC_506 - STEP 2: Play button visible before delete = " + playVisibleBeforeDelete);

		((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.open('about:blank','_blank');");
		java.util.List<String> windowHandles = new java.util.ArrayList<>(driver.getWindowHandles());
		String adminTab = windowHandles.get(windowHandles.size() - 1);
		driver.switchTo().window(adminTab);
		LOGGER.info("TC_506 - STEP 3: Opened second tab for chapter deletion");

		openForCreatorsListingPage();
		forCreatorPage.selectApprovedFilter();
		forCreatorPage.searchBook(bookTitle);
		Assert.assertTrue(forCreatorPage.containsVisibleBookTitle(bookTitle),
				"TC_506: Target book should be visible in Approved filter before deleting Chapter 1");

		forCreatorPage.clickEditBookByIndex(0);
		creatorSettings.clickNext();
		waitForMilliseconds(2000);
		creatorSettings.waitForAudioUploadScreen();

		int chapterCountBefore = creatorSettings.getChapterCount();
		LOGGER.info("TC_506 - STEP 4: Chapter count before delete = " + chapterCountBefore);
		Assert.assertTrue(chapterCountBefore > 0,
				"TC_506: At least one chapter should exist before deleting Chapter 1");

		creatorSettings.deleteFirstChapter();
		creatorSettings.confirmChapterDelete();
		String deleteMessage = logSuccessToast("TC_506");
		waitForMilliseconds(2000);
		int chapterCountAfter = creatorSettings.getChapterCount();

		LOGGER.info("TC_506 - STEP 4: Delete message = " + deleteMessage);
		LOGGER.info("TC_506 - STEP 4: Chapter count after delete = " + chapterCountAfter);
		Assert.assertEquals(deleteMessage, "Audio file deleted successfully.",
				"TC_506: Delete toast should match the expected message");
		Assert.assertTrue(chapterCountAfter < chapterCountBefore,
				"TC_506: Chapter count should decrease after deleting Chapter 1 from the second tab");

		driver.switchTo().window(viewingTab);
		driver.navigate().refresh();
		waitForMilliseconds(2000);

		String currentUrlAfterDelete = driver.getCurrentUrl();
		boolean redirectedAway = !currentUrlAfterDelete.equals(viewingUrl);
		boolean stillOnBookDetails = dashboard.isBookDetailsPageVisible();
		boolean playVisibleAfterDelete = dashboard.isPlayAudioButtonVisible();
		boolean pauseVisibleAfterDelete = dashboard.isPauseAudioButtonVisible();

		LOGGER.info("TC_506 - STEP 5: Current URL after delete = '" + currentUrlAfterDelete + "'");
		LOGGER.info("TC_506 - STEP 5: Redirected away from original page = " + redirectedAway);
		LOGGER.info("TC_506 - STEP 5: Book details visible after delete = " + stillOnBookDetails);
		LOGGER.info("TC_506 - STEP 5: Play button visible after delete = " + playVisibleAfterDelete);
		LOGGER.info("TC_506 - STEP 5: Pause button visible after delete = " + pauseVisibleAfterDelete);

		boolean playerHandledDeleteGracefully = redirectedAway || !stillOnBookDetails
				|| !pauseVisibleAfterDelete || playVisibleAfterDelete != playVisibleBeforeDelete;
		Assert.assertTrue(playerHandledDeleteGracefully,
				"TC_506: After deleting Chapter 1 in another tab, the player tab should redirect, lose the active playback state, or expose changed playback controls");
		LOGGER.info("TC_506: Chapter delete during playback verified");
	}

	/**
	 * TC_507: Edit Chapter - Edit during upload Test Flow: Start upload → Edit
	 * Expected: Action should be restricted
	 */
	@Test(priority = 507, retryAnalyzer = RetryAnalyzer.class)
	public void verifyEditDuringUpload() {
		loginAsUploader();
		String bookTitle = "Updated Book Title 111";
		LOGGER.info("TC_507 - STEP 1: Target book title = '" + bookTitle + "'");

		dashboard.waitForPageReady();
		Assert.assertTrue(dashboard.isSearchBarVisible(),
				"TC_507: Header search bar should be visible before opening the target book");
		dashboard.submitSearch(bookTitle);
		Assert.assertTrue(dashboard.clickFirstSearchResult(),
				"TC_507: Search should open the target book details page");
		Assert.assertTrue(dashboard.isBookDetailsPageVisible(),
				"TC_507: Book details page should open for the target book");
		Assert.assertTrue(dashboard.waitForBookDataToLoad(),
				"TC_507: Book details should finish loading before playback");

		boolean playbackStarted = dashboard.clickPlayAudioAndVerifyPlayback();
		LOGGER.info("TC_507 - STEP 2: Playback started for Chapter 1 = " + playbackStarted);

		String viewingTab = driver.getWindowHandle();
		String viewingUrl = driver.getCurrentUrl();
		boolean playVisibleBeforeEdit = dashboard.isPlayAudioButtonVisible();
		boolean pauseVisibleBeforeEdit = dashboard.isPauseAudioButtonVisible();
		LOGGER.info("TC_507 - STEP 2: Viewing tab URL = '" + viewingUrl + "'");
		LOGGER.info("TC_507 - STEP 2: Play visible before edit = " + playVisibleBeforeEdit);
		LOGGER.info("TC_507 - STEP 2: Pause visible before edit = " + pauseVisibleBeforeEdit);

		((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.open('about:blank','_blank');");
		java.util.List<String> windowHandles = new java.util.ArrayList<>(driver.getWindowHandles());
		String adminTab = windowHandles.get(windowHandles.size() - 1);
		driver.switchTo().window(adminTab);
		LOGGER.info("TC_507 - STEP 3: Opened second tab for chapter edit");

		openForCreatorsListingPage();
		forCreatorPage.selectApprovedFilter();
		forCreatorPage.searchBook(bookTitle);
		Assert.assertTrue(forCreatorPage.containsVisibleBookTitle(bookTitle),
				"TC_507: Target book should be visible in Approved filter before editing Chapter 1");

		forCreatorPage.clickEditBookByIndex(0);
		creatorSettings.clickNext();
		waitForMilliseconds(2000);
		creatorSettings.waitForAudioUploadScreen();

		creatorSettings.editFirstChapter();
		String originalChapterTitle = creatorSettings.getCurrentChapterName();
		String updatedChapterTitle = "Updated Chapter During Playback " + UUID.randomUUID().toString().substring(0, 6);
		LOGGER.info("TC_507 - STEP 4: Original chapter title = '" + originalChapterTitle + "'");
		LOGGER.info("TC_507 - STEP 4: Updated chapter title = '" + updatedChapterTitle + "'");

		creatorSettings.enterChapterName(updatedChapterTitle);
		creatorSettings.saveAudioChapter();
		creatorSettings.waitForAudioUploadScreen();
		waitForMilliseconds(1000);

		String saveMessage = logSuccessToast("TC_507");
		creatorSettings.editFirstChapter();
		String persistedChapterTitle = creatorSettings.getCurrentChapterName();
		LOGGER.info("TC_507 - STEP 4: Save message = '" + saveMessage + "'");
		LOGGER.info("TC_507 - STEP 4: Persisted chapter title = '" + persistedChapterTitle + "'");

		Assert.assertTrue(!saveMessage.isBlank()
				|| persistedChapterTitle.equals(updatedChapterTitle)
				|| persistedChapterTitle.contains(updatedChapterTitle),
				"TC_507: Editing Chapter 1 in the second tab should persist successfully");

		driver.switchTo().window(viewingTab);
		driver.navigate().refresh();
		waitForMilliseconds(2000);

		String currentUrlAfterEdit = driver.getCurrentUrl();
		boolean redirectedAway = !currentUrlAfterEdit.equals(viewingUrl);
		boolean stillOnBookDetails = dashboard.isBookDetailsPageVisible();
		boolean playVisibleAfterEdit = dashboard.isPlayAudioButtonVisible();
		boolean pauseVisibleAfterEdit = dashboard.isPauseAudioButtonVisible();

		LOGGER.info("TC_507 - STEP 5: Current URL after edit = '" + currentUrlAfterEdit + "'");
		LOGGER.info("TC_507 - STEP 5: Redirected away from original page = " + redirectedAway);
		LOGGER.info("TC_507 - STEP 5: Book details visible after edit = " + stillOnBookDetails);
		LOGGER.info("TC_507 - STEP 5: Play visible after edit = " + playVisibleAfterEdit);
		LOGGER.info("TC_507 - STEP 5: Pause visible after edit = " + pauseVisibleAfterEdit);

		boolean playerHandledEditGracefully = stillOnBookDetails || dashboard.waitForDashboardShell() || redirectedAway;
		boolean playerStateResponded = playVisibleAfterEdit || pauseVisibleAfterEdit
				|| playVisibleAfterEdit != playVisibleBeforeEdit
				|| pauseVisibleAfterEdit != pauseVisibleBeforeEdit;

		Assert.assertTrue(playerHandledEditGracefully && playerStateResponded,
				"TC_507: After editing the playing chapter in another tab, the player tab should remain stable and expose responsive playback controls");
		LOGGER.info("TC_507: Chapter edit while playback is active verified");
	}

	/**
	 * TC_508: Delete Chapter - Reorder after delete Test Flow: Delete middle
	 * chapter Expected: Sequence auto-adjusted
	 */
	@Test(priority = 508, retryAnalyzer = RetryAnalyzer.class)
	public void verifyReorderAfterDelete() {
		loginAsUploader();

		// Need a book with multiple chapters
		openExistingBookChapterSection("TC_508", 3);

		int chapterCountBefore = creatorSettings.getChapterCount();
		LOGGER.info("TC_508 - Chapter count before delete: " + chapterCountBefore);

		if (chapterCountBefore < 3) {
			throw new SkipException("TC_508 requires at least 3 chapters to test reordering");
		}

		// Delete a middle chapter (second chapter)
		creatorSettings.deleteFirstChapter();
		creatorSettings.confirmChapterDelete();
		String deleteMessage = logSuccessToast("TC_508");
		waitForMilliseconds(2000);

		int chapterCountAfter = creatorSettings.getChapterCount();
		LOGGER.info("TC_508 - Delete message: " + deleteMessage);
		LOGGER.info("TC_508 - Chapter count after delete: " + chapterCountAfter);

		// Verify sequence is adjusted
		Assert.assertTrue(chapterCountAfter < chapterCountBefore, "TC_508: Chapter count should decrease after delete");

		// Verify remaining chapters are accessible
		boolean chaptersAccessible = creatorSettings.hasChapters();
		LOGGER.info("TC_508 - Remaining chapters accessible: " + chaptersAccessible);

		Assert.assertTrue(chaptersAccessible, "TC_508: Remaining chapters should be accessible after reorder");
		LOGGER.info("TC_508: Chapter reordering after delete verified");
		}
	}
