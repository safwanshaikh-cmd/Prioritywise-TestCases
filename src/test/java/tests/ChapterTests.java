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
 * Test Coverage: TC_490 - TC_499
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
			return Files.list(downloadsDirectory)
					.filter(Files::isRegularFile)
					.filter(path -> hasAnyExtension(path.getFileName().toString(), extensions))
					.sorted(Comparator.comparing(Path::toString))
					.map(Path::toString)
					.findFirst()
					.orElseGet(() -> {
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
		Assert.assertTrue(landingReady, "Post-login landing page should be stable. Current URL: " + driver.getCurrentUrl());

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
			throw new SkipException("Valid portrait and landscape JPG/PNG images are required via config or Downloads.");
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

	private void openExistingBookChapterSection(String testCaseId, int minimumChapterCount) {
		LOGGER.info(testCaseId + " - STEP 1: Navigating to existing Pending books");
		openForCreatorsListingPage();
		forCreatorPage.selectPendingFilter();
		if (!forCreatorPage.hasBooks()) {
			throw new SkipException(testCaseId + ": No books found in Pending filter.");
		}

		int bookCount = forCreatorPage.getVisibleBookCount();
		List<String> visibleTitles = forCreatorPage.getVisibleBookTitles();
		for (int index = 0; index < bookCount; index++) {
			openForCreatorsListingPage();
			forCreatorPage.selectPendingFilter();

			String existingBookTitle = index < visibleTitles.size() ? visibleTitles.get(index) : ("Book index " + index);
			LOGGER.info(testCaseId + " - STEP 2: Trying existing book index " + index + " = '" + existingBookTitle + "'");

			forCreatorPage.clickEditBookByIndex(index);
			LOGGER.info(testCaseId + " - STEP 3: Edit book screen opened");
			creatorSettings.clickNext();
			waitForMilliseconds(2000);
			creatorSettings.waitForAudioUploadScreen();
			LOGGER.info(testCaseId + " - STEP 4: Chapter screen opened after clicking Next");

			int chapterCount = creatorSettings.getChapterCount();
			LOGGER.info(testCaseId + " - STEP 5: Existing chapter count for '" + existingBookTitle + "' = " + chapterCount);
			if (chapterCount >= minimumChapterCount) {
				LOGGER.info(testCaseId + " - STEP 6: Using existing book '" + existingBookTitle
						+ "' for chapter operation");
				return;
			}
		}

		throw new SkipException(testCaseId + ": No existing Pending book meets chapter precondition. Required chapters: "
				+ minimumChapterCount);
	}

	@Test(priority = 490, retryAnalyzer = RetryAnalyzer.class)
	puśśśblic void verifyChapterEditScreenLoads() 
	{
		loginAsUploader();
		openExistingBookChapterSection("TC_490", 1);
		creatorSettings.editFirstChapter();

		Assert.assertTrue(creatorSettings.isChapterFormVisible(),
				"TC_490: Chapter edit screen should load after clicking Edit on chapter");
		Assert.assertFalse(creatorSettings.getCurrentChapterName().isBlank(),
				"TC_490: Edit screen should display the chapter title");
		LOGGER.info("TC_490: Chapter edit screen loaded successfully");
	}
śśś
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
		waitForMilliseconds(2000);

		String successMessage = logSuccessToast("TC_491");
		String actualTitle = creatorSettings.getCurrentChapterName();
		LOGGER.info("TC_491 - UPDATED INPUT Title: " + updatedTitle);
		LOGGER.info("TC_491 - ACTUAL SAVED Title: " + actualTitle);

		Assert.assertTrue(!successMessage.isBlank() || actualTitle.equals(updatedTitle) || actualTitle.contains(updatedTitle),
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
		LOGGER.info("TC_493 - ERROR MESSAGE: " + errorMessage);
		Assert.assertTrue(uploadRejected || !errorMessage.isBlank(),
				"TC_493: Invalid chapter file upload should show an error");
		LOGGER.info("TC_493: Invalid chapter file validation verified");
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
		waitForMilliseconds(2000);

		int chapterCountAfter = creatorSettings.getChapterCount();
		LOGGER.info("TC_495 - Chapters AFTER delete: " + chapterCountAfter);
		Assert.assertTrue(chapterCountAfter == chapterCountBefore - 1 || chapterCountAfter == 0,
				"TC_495: Chapter should be deleted successfully");
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
		Assert.assertTrue(chapterCountBefore >= 2, "TC_497: Multiple chapters should exist for boundary delete validation");
		LOGGER.info("TC_497 - Chapters BEFORE boundary delete: " + chapterCountBefore);

		creatorSettings.deleteFirstChapter();
		creatorSettings.confirmChapterDelete();
		waitForMilliseconds(2000);
		int chapterCountAfterFirstDelete = creatorSettings.getChapterCount();
		LOGGER.info("TC_497 - Chapters AFTER first delete: " + chapterCountAfterFirstDelete);
		Assert.assertEquals(chapterCountAfterFirstDelete, chapterCountBefore - 1,
				"TC_497: Deleting the first chapter should reduce the count by one");

		creatorSettings.deleteFirstChapter();
		creatorSettings.confirmChapterDelete();
		waitForMilliseconds(2000);
		int chapterCountAfterLastDelete = creatorSettings.getChapterCount();
		LOGGER.info("TC_497 - Chapters AFTER last delete: " + chapterCountAfterLastDelete);
		Assert.assertEquals(chapterCountAfterLastDelete, 0,
				"TC_497: Deleting the remaining chapter should leave no chapters");
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

	@Test(priority = 499, retryAnalyzer = RetryAnalyzer.class)
	public void verifyChapterDeleteDuringPlayback() {
		loginAsUploader();
		openExistingBookChapterSection("TC_499", 1);

		boolean playButtonVisible = dashboard.isPlayAudioButtonVisible();
		LOGGER.info("TC_499 - Play Audio button visible before delete: " + playButtonVisible);
		if (playButtonVisible) {
			boolean playbackTriggered = dashboard.clickPlayAudioAndVerifyPlayback();
			LOGGER.info("TC_499 - Playback triggered before delete: " + playbackTriggered);
		} else {
			LOGGER.info("TC_499 - Playback controls are not exposed on the current chapter screen; proceeding with delete stability check");
		}

		int chapterCountBefore = creatorSettings.getChapterCount();
		creatorSettings.deleteFirstChapter();
		creatorSettings.confirmChapterDelete();
		waitForMilliseconds(2000);

		int chapterCountAfter = creatorSettings.getChapterCount();
		LOGGER.info("TC_499 - Chapters BEFORE delete during playback: " + chapterCountBefore);
		LOGGER.info("TC_499 - Chapters AFTER delete during playback: " + chapterCountAfter);
		Assert.assertTrue(chapterCountAfter == chapterCountBefore - 1 || chapterCountAfter == 0,
				"TC_499: System should handle chapter delete without crash while audio controls are active");
		LOGGER.info("TC_499: Chapter delete handled properly during playback scenario");
	}
}
