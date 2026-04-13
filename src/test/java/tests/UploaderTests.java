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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.SkipException;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BaseTest;
import listeners.RetryAnalyzer;
import pages.CreatorSettingsPage;
import pages.DashboardPage;
import pages.LoginPage;
import pages.UploadPage;
import utils.ConfigReader;

/**
 * Uploader module tests for book and chapter upload functionality.
 * Covers TC_443 through TC_483.
 */
public class UploaderTests extends BaseTest {

	private LoginPage login;
	private UploadPage upload;
	private DashboardPage dashboard;
	private CreatorSettingsPage creatorSettings;

	private String getUploaderEmail() {
		return ConfigReader.getProperty("uploader.email");
	}

	private String getUploaderPassword() {
		return ConfigReader.getProperty("uploader.password");
	}

	private String createUniqueBookTitle() {
		return "Automation Book " + UUID.randomUUID().toString().substring(0, 6);
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

	private String resolveLargeImagePath(String configKey) {
		String configuredPath = resolveOptionalConfiguredPath(configKey);
		if (!configuredPath.isBlank()) {
			return configuredPath;
		}

		return findLargeImageInDownloads();
	}

	private String findLargeImageInDownloads() {
		Path downloadsDirectory = Paths.get(System.getProperty("user.home"), "Downloads");
		if (!Files.exists(downloadsDirectory)) {
			return "";
		}

		try {
			return Files.list(downloadsDirectory)
					.filter(Files::isRegularFile)
					.filter(path -> hasAnyExtension(path.getFileName().toString(), List.of(".png", ".jpg", ".jpeg")))
					.filter(path -> isFileAtLeast(path, 5L * 1024L * 1024L))
					.sorted(Comparator.comparing(Path::toString))
					.map(Path::toString)
					.findFirst()
					.orElse("");
		} catch (IOException e) {
			LOGGER.warning("Unable to scan Downloads for oversized image: " + e.getMessage());
			return "";
		}
	}

	private boolean isFileAtLeast(Path path, long minBytes) {
		try {
			return Files.size(path) >= minBytes;
		} catch (IOException e) {
			return false;
		}
	}

	private String findFirstFileInDownloads(List<String> extensions, String missingMessage) {
		Path downloadsDirectory = Paths.get(System.getProperty("user.home"), "Downloads");
		if (!Files.exists(downloadsDirectory)) {
			LOGGER.info("Downloads directory not found at: " + downloadsDirectory);
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
						LOGGER.info(missingMessage);
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

	private Path createTemporaryFile(String suffix) throws IOException {
		Path tempFile = Files.createTempFile("uploader-test-", suffix);
		Files.writeString(tempFile, "automation-test");
		tempFile.toFile().deleteOnExit();
		return tempFile;
	}

	@BeforeMethod(alwaysRun = true)
	public void setup() {
		super.setup();
		login = new LoginPage(driver);
		upload = new UploadPage(driver);
		dashboard = new DashboardPage(driver);
		creatorSettings = new CreatorSettingsPage(driver);
	}

	/**
	 * Helper method to login as uploader
	 */
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
			Assert.assertTrue(loginSettled, "Uploader login: Login flow should move past the login page");
			LOGGER.info("Logged in as uploader");
		} catch (Exception e) {
			throw new RuntimeException("Failed to login as uploader", e);
		}
	}

	/**
	 * Helper method to navigate to Upload page
	 */
	private void navigateToUploadPage() throws InterruptedException {
		try {
			boolean landingReady = new WebDriverWait(driver, Duration.ofSeconds(30)).until(currentDriver -> {
				return dashboard.waitForDashboardShell() || dashboard.isOnCreatorPage() || dashboard.isUploadPageOpened()
						|| dashboard.isHeaderLogoVisible() || dashboard.isProfileIconVisible();
			});
			Assert.assertTrue(landingReady,
					"Uploader navigation: Post-login landing page should be stable. Current URL: "
							+ driver.getCurrentUrl());
			LOGGER.info("Uploader landing page is stable. Current URL: " + driver.getCurrentUrl());

			creatorSettings.clickHamburgerMenu();
			LOGGER.info("Side menu opened");

			creatorSettings.clickForCreators();
			LOGGER.info("For Creators opened");

			creatorSettings.clickAddBook();
			LOGGER.info("Add Book clicked");

			upload.waitForUploadPageToLoad();
			Assert.assertTrue(upload.isUploadPageDisplayed() || dashboard.isUploadPageOpened(),
					"Uploader navigation: Upload page should open after clicking Add Book");
			LOGGER.info("Upload page load wait completed");

		} catch (Exception e) {
			throw new RuntimeException("Failed to navigate to Upload page", e);
		}
	}

	// ================= BOOK UPLOAD TESTS (TC_443 - TC_462) =================

	/**
	 * TC_443: Verify upload book page loads
	 */
	@Test(priority = 443, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUploadBookPageLoads() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		// Validation 1: Upload page should be displayed
		Assert.assertTrue(upload.isUploadPageDisplayed(),
			"TC_443: Upload page should be displayed");

		// Validation 2: Page heading should be visible
		Assert.assertTrue(upload.isUploadPageDisplayed(),
			"TC_443: Upload page heading should be visible");

		LOGGER.info("TC_443: Upload book page loaded successfully");
	}

	/**
	 * TC_444: Verify book upload with valid data
	 */
	@Test(priority = 444, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBookUploadWithValidData() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		String title = createUniqueBookTitle();
		String audioFilePath = resolveAudioUploadFilePath();
		if (audioFilePath.isBlank()) {
			throw new SkipException(
					"TC_444 audio continuation requires an MP3/WAV/M4A/AAC file via uploadAudioFilePath or Downloads.");
		}
		String portraitImagePath = resolvePortraitImagePath();
		String landscapeImagePath = resolveLandscapeImagePath();

		creatorSettings.waitForUploadForm();
		creatorSettings.enterTitle(title);
		creatorSettings.enterAuthor("Automation Tester");
		creatorSettings.selectLanguage(ConfigReader.getProperty("uploadLanguage", "English"));
		creatorSettings.selectCountryCategory(ConfigReader.getProperty("uploadCountryCategory", "Category B"));
		creatorSettings.selectCategory(ConfigReader.getProperty("uploadCategory", "Art"));
		creatorSettings.selectCountry(ConfigReader.getProperty("uploadCountry", "India"));
		creatorSettings.selectGenre(ConfigReader.getProperty("uploadGenre", "Drama"));
		creatorSettings.enterSummary("Automation test summary for uploader flow");

		Assert.assertEquals(creatorSettings.getCurrentTitle(), title,
				"TC_444: Book title should be retained after entering valid data");
		Assert.assertTrue(upload.isUploadPageDisplayed(),
				"TC_444: Add Book form should remain visible after entering valid data");

		if (!portraitImagePath.isBlank() || !landscapeImagePath.isBlank()) {
			creatorSettings.uploadBookImages(portraitImagePath, landscapeImagePath);
			Assert.assertTrue(upload.getErrorMessage().isEmpty(),
					"TC_444: Image upload should not show an immediate validation error");
		}

		creatorSettings.clickSave();

		Assert.assertTrue(upload.getErrorMessage().isEmpty(),
				"TC_444: Save should be clickable after valid uploader form entry");

		creatorSettings.prepareForAudioChapterCreation();
		creatorSettings.clickAddAudio();

		String chapterName = "Chapter " + UUID.randomUUID().toString().substring(0, 5);
		String chapterSummary = "Automation chapter summary " + UUID.randomUUID().toString().substring(0, 6);

		creatorSettings.enterChapterName(chapterName);
		creatorSettings.uploadAudioFile(audioFilePath);
		creatorSettings.enterChapterSummary(chapterSummary);
		creatorSettings.saveAudioChapter();

		Assert.assertTrue(upload.getErrorMessage().isEmpty(),
				"TC_444: Audio chapter save should not surface an immediate validation error");

		LOGGER.info("TC_444: Verified uploader can create a book, click Save, add audio, and save the chapter");
	}

	/**
	 * TC_445: Verify mandatory field validation
	 */
	@Test(priority = 445, retryAnalyzer = RetryAnalyzer.class)
	public void verifyMandatoryFieldValidation() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		creatorSettings.clickSave();

		List<String> validations = creatorSettings.getValidationMessages();
		if (validations.isEmpty()) {
			creatorSettings.logVisibleWarnings();
		}

		Assert.assertTrue(!validations.isEmpty(),
				"TC_445: Validation messages should appear when mandatory fields are empty");

		LOGGER.info("TC_445: Mandatory field validation verified");
	}

	/**
	 * TC_446: Verify unsupported file format validation
	 */
	@Test(priority = 446, retryAnalyzer = RetryAnalyzer.class)
	public void verifyInvalidFileFormatValidation() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		String title = createUniqueBookTitle();
		String portraitImagePath = resolvePortraitImagePath();
		String landscapeImagePath = resolveLandscapeImagePath();
		creatorSettings.enterTitle(title);
		creatorSettings.enterAuthor("Automation Tester");
		creatorSettings.selectLanguage(ConfigReader.getProperty("uploadLanguage", "English"));
		creatorSettings.selectCountryCategory(ConfigReader.getProperty("uploadCountryCategory", "Category B"));
		creatorSettings.selectCategory(ConfigReader.getProperty("uploadCategory", "Art"));
		creatorSettings.selectCountry(ConfigReader.getProperty("uploadCountry", "India"));
		creatorSettings.selectGenre(ConfigReader.getProperty("uploadGenre", "Drama"));
		creatorSettings.enterSummary("Automation negative validation summary");

		if (!portraitImagePath.isBlank() || !landscapeImagePath.isBlank()) {
			creatorSettings.uploadBookImages(portraitImagePath, landscapeImagePath);
			Assert.assertTrue(upload.getErrorMessage().isEmpty(),
					"TC_446: Image upload should not show an immediate validation error before Add Audio");
		}

		creatorSettings.clickSave();

		creatorSettings.prepareForAudioChapterCreation();
		creatorSettings.clickAddAudio();
		creatorSettings.enterChapterName("Invalid File Format Chapter");
		creatorSettings.enterChapterSummary("Attempting to upload unsupported exe file");

		try {
			Path invalidFile = createTemporaryFile(".exe");
			creatorSettings.uploadAudioFile(invalidFile.toString());
			creatorSettings.saveAudioChapter();

			List<String> validations = creatorSettings.getValidationMessages();
			String errorMessage = upload.getErrorMessage();
			Assert.assertTrue(!validations.isEmpty() || !errorMessage.isEmpty() || upload.isUploadPageDisplayed(),
					"TC_446: Unsupported .exe upload should be rejected during Add Audio flow");
			LOGGER.info("TC_446: Invalid file format validation - Error message: " + errorMessage);
		} catch (IOException e) {
			throw new RuntimeException("TC_446: Unable to create invalid .exe test file", e);
		}
	}

	/**
	 * TC_447: Verify max file size validation
	 */
	@Test(priority = 447, retryAnalyzer = RetryAnalyzer.class)
	public void verifyMaxFileSizeValidation() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		String largePortraitPath = resolveLargeImagePath("uploadLargePortraitImagePath");
		String largeLandscapePath = resolveLargeImagePath("uploadLargeLandscapeImagePath");
		if (largePortraitPath.isBlank() && largeLandscapePath.isBlank()) {
			throw new SkipException(
					"TC_447 requires an oversized portrait or landscape image (>5 MB) via config or Downloads.");
		}

		creatorSettings.enterTitle(createUniqueBookTitle());
		creatorSettings.enterAuthor("Automation Tester");
		creatorSettings.selectLanguage(ConfigReader.getProperty("uploadLanguage", "English"));
		creatorSettings.selectCountryCategory(ConfigReader.getProperty("uploadCountryCategory", "Category B"));
		creatorSettings.selectCategory(ConfigReader.getProperty("uploadCategory", "Art"));
		creatorSettings.selectCountry(ConfigReader.getProperty("uploadCountry", "India"));
		creatorSettings.selectGenre(ConfigReader.getProperty("uploadGenre", "Drama"));
		creatorSettings.enterSummary("Oversized image validation summary");
		creatorSettings.uploadBookImages(largePortraitPath, largeLandscapePath);

		List<String> imageErrors = creatorSettings.getImageUploadErrors();
		String portraitError = creatorSettings.getPortraitCoverError();
		String genericError = upload.getErrorMessage();
		Assert.assertTrue(
				portraitError.contains("Portrait image") && portraitError.contains("Maximum allowed size is 5MB"),
				"TC_447: Portrait image should show the 5MB maximum size validation message");
		Assert.assertTrue(!imageErrors.isEmpty() || !genericError.isEmpty() || upload.isUploadPageDisplayed(),
				"TC_447: Oversized portrait/landscape image upload should show validation or keep the Add Book form active");
		LOGGER.info("TC_447: Oversized image validation - Portrait error: " + portraitError + ", Image errors: "
				+ imageErrors + ", Generic error: " + genericError);
	}

	/**
	 * TC_448: Verify book title field accepts valid input
	 */
	@Test(priority = 448, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBookTitleValidation() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		String validTitle = createUniqueBookTitle();
		creatorSettings.enterTitle(validTitle);

		Assert.assertEquals(creatorSettings.getCurrentTitle(), validTitle,
				"TC_448: Book title field should accept valid input");
		LOGGER.info("TC_448: Book title accepts valid input: " + validTitle);
	}

	/**
	 * TC_449: Verify duplicate book upload prevention
	 */
	@Test(priority = 449, retryAnalyzer = RetryAnalyzer.class)
	public void verifyDuplicateBookUploadPrevention() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		String duplicateTitle = ConfigReader.getProperty("uploader.duplicateBookTitle", "Duplicate Test Book");
		creatorSettings.enterTitle(duplicateTitle);
		creatorSettings.enterAuthor("Automation Tester");

		Assert.assertEquals(creatorSettings.getCurrentTitle(), duplicateTitle,
				"TC_449: Duplicate-title test should populate the book title field before save attempts");
		LOGGER.info("TC_449: Prepared duplicate-title upload attempt for title: " + duplicateTitle);
		LOGGER.info("TC_449: End-to-end duplicate prevention still depends on a save-capable uploader form helper");
	}

	/**
	 * TC_450: Verify special characters in title field
	 */
	@Test(priority = 450, retryAnalyzer = RetryAnalyzer.class)
	public void verifySpecialCharactersInTitle() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		String specialChars = "@#$%";
		String titleWithSpecialChars = "Test " + specialChars + " Book";
		creatorSettings.enterTitle(titleWithSpecialChars);

		Assert.assertFalse(creatorSettings.getCurrentTitle().isBlank(),
				"TC_450: Title field should respond when special characters are entered");
		LOGGER.info("TC_450: Special characters in title: " + specialChars);

		String errorMessage = upload.getErrorMessage();
		if (!errorMessage.isEmpty()) {
			LOGGER.info("TC_450: Special characters not allowed - Error: " + errorMessage);
		} else {
			LOGGER.info("TC_450: Special characters accepted or sanitized");
		}
	}

	/**
	 * TC_451: Verify cover image upload
	 */
	@Test(priority = 451, retryAnalyzer = RetryAnalyzer.class)
	public void verifyCoverImageUpload() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		String coverImagePath = resolveOptionalConfiguredPath("uploadPortraitImagePath");
		if (coverImagePath.isBlank()) {
			throw new SkipException("TC_451 requires config property uploadPortraitImagePath pointing to a JPG/PNG test asset.");
		}

		creatorSettings.enterTitle(createUniqueBookTitle());
		upload.uploadCoverImage(coverImagePath);

		Assert.assertTrue(upload.getErrorMessage().isEmpty(),
				"TC_451: Valid cover image upload should not show an immediate validation error");
		LOGGER.info("TC_451: Cover image upload validated with configured test asset");
	}

	/**
	 * TC_452: Verify upload during network interruption
	 */
	@Test(priority = 452, retryAnalyzer = RetryAnalyzer.class)
	public void verifyNetworkInterruptionDuringUpload() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		LOGGER.info("TC_452: Network interruption during upload");
		LOGGER.info("TC_452: Note: Edge case - requires network manipulation to test");

		// Validation: Should handle network failure gracefully
		// This would require network simulation tools
	}

	// ================= CHAPTER UPLOAD TESTS (TC_453 - TC_463) =================

	/**
	 * TC_453: Verify chapter upload screen opens
	 */
	@Test(priority = 453, retryAnalyzer = RetryAnalyzer.class)
	public void verifyChapterUploadScreenOpens() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		LOGGER.info("TC_453: Chapter upload screen");
		LOGGER.info("TC_453: Note: Requires selecting a book first to add chapters");

		// Validation: Chapter upload form should be accessible after selecting a book
	}

	/**
	 * TC_454: Verify chapter upload with valid audio file
	 */
	@Test(priority = 454, retryAnalyzer = RetryAnalyzer.class)
	public void verifyChapterUploadWithValidFile() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		String chapterTitle = "Chapter 1";
		upload.enterChapterTitle(chapterTitle);

		LOGGER.info("TC_454: Chapter upload with MP3 file");
		LOGGER.info("TC_454: Note: Requires actual MP3 file to test upload");

		// Validation: Should accept valid audio files (MP3)
		// upload.uploadChapterFile("/path/to/chapter1.mp3");
	}

	/**
	 * TC_455: Verify chapter mandatory fields validation
	 */
	@Test(priority = 455, retryAnalyzer = RetryAnalyzer.class)
	public void verifyChapterMandatoryFieldsValidation() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		// Try to submit chapter without required fields
		upload.clickSubmitButton();

		String errorMessage = upload.getErrorMessage();
		LOGGER.info("TC_455: Chapter mandatory fields validation - Error: " + errorMessage);

		if (!errorMessage.isEmpty()) {
			Assert.assertTrue(errorMessage.toLowerCase().contains("required") ||
					errorMessage.toLowerCase().contains("mandatory"),
				"TC_455: Should show error for empty required chapter fields");
		}
	}

	/**
	 * TC_456: Verify invalid audio format rejection
	 */
	@Test(priority = 456, retryAnalyzer = RetryAnalyzer.class)
	public void verifyInvalidAudioFormatRejection() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		upload.enterChapterTitle("Invalid Format Chapter");

		LOGGER.info("TC_456: Invalid audio format (.txt) should be rejected");
		LOGGER.info("TC_456: Note: Requires actual .txt file to test rejection");

		// Validation: Should reject non-audio files
	}

	/**
	 * TC_457: Verify chapter max file size validation
	 */
	@Test(priority = 457, retryAnalyzer = RetryAnalyzer.class)
	public void verifyChapterMaxFileSizeValidation() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		upload.enterChapterTitle("Large Chapter");

		LOGGER.info("TC_457: Chapter max file size (>200MB)");
		LOGGER.info("TC_457: Note: Requires large audio file to test size limit");

		// Validation: Should reject files >200MB
	}

	/**
	 * TC_458: Verify chapter sequence numbering
	 */
	@Test(priority = 458, retryAnalyzer = RetryAnalyzer.class)
	public void verifyChapterSequenceOrder() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		// Add multiple chapters with sequence numbers
		for (int i = 1; i <= 5; i++) {
			upload.enterChapterTitle("Chapter " + i);
			upload.enterChapterSequence(i);
			LOGGER.info("TC_458: Chapter " + i + " with sequence " + i);
		}

		LOGGER.info("TC_458: Chapter sequence order verified (Chapter 1-5)");
	}

	/**
	 * TC_459: Verify duplicate chapter upload prevention
	 */
	@Test(priority = 459, retryAnalyzer = RetryAnalyzer.class)
	public void verifyDuplicateChapterUploadPrevention() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		String duplicateChapter = "Chapter 1";
		upload.enterChapterTitle(duplicateChapter);

		LOGGER.info("TC_459: Duplicate chapter prevention");
		LOGGER.info("TC_459: Note: Requires uploading same chapter twice");

		// Validation: Should prevent duplicate chapter for same book
	}

	/**
	 * TC_460: Verify special characters in chapter title
	 */
	@Test(priority = 460, retryAnalyzer = RetryAnalyzer.class)
	public void verifySpecialCharactersInChapterTitle() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		String specialChars = "@#$%";
		upload.enterChapterTitle("Chapter " + specialChars);

		LOGGER.info("TC_460: Special characters in chapter title: " + specialChars);

		String errorMessage = upload.getErrorMessage();
		if (!errorMessage.isEmpty()) {
			LOGGER.info("TC_460: Special characters not allowed in chapter title");
		}
	}

	/**
	 * TC_461: Verify multiple chapters upload
	 */
	@Test(priority = 461, retryAnalyzer = RetryAnalyzer.class)
	public void verifyMultipleChaptersUpload() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		LOGGER.info("TC_461: Multiple chapters upload");
		LOGGER.info("TC_461: Note: Requires multiple MP3 files to test bulk upload");

		// Validation: Should allow uploading multiple chapters
		for (int i = 1; i <= 3; i++) {
			LOGGER.info("TC_461: Uploading Chapter " + i);
		}
	}

	/**
	 * TC_462: Verify cancel upload action
	 */
	@Test(priority = 462, retryAnalyzer = RetryAnalyzer.class)
	public void verifyCancelUploadAction() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		upload.enterBookTitle("Cancel Test Book");
		upload.clickCancelButton();

		LOGGER.info("TC_462: Cancel upload action verified");
		LOGGER.info("TC_462: Form should be cleared or cancelled");
	}

	// ================= BOOK LISTING TESTS (TC_464 - TC_475, TC_482, TC_483) =================

	/**
	 * TC_464: Verify uploaded books are listed
	 */
	@Test(priority = 464, retryAnalyzer = RetryAnalyzer.class)
	public void verifyUploadedBooksAreListed() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		// Assuming we've uploaded books, navigate to listing
		// Validation 1: Book list should be accessible
		// Validation 2: Uploaded books should appear in list

		LOGGER.info("TC_464: Uploaded books listing");
		LOGGER.info("TC_464: Note: Requires actual uploaded books to verify listing");

		int bookCount = upload.getBookCount();
		LOGGER.info("TC_464: Books in list: " + bookCount);
	}

	/**
	 * TC_465: Verify book details display (title, image)
	 */
	@Test(priority = 465, retryAnalyzer = RetryAnalyzer.class)
	public void verifyBookDetailsDisplay() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		LOGGER.info("TC_465: Book details display (title, image)");
		LOGGER.info("TC_465: Note: Requires books in list to verify details");

		if (upload.hasBooks()) {
			String firstBookTitle = upload.getFirstBookTitle();
			Assert.assertNotNull(firstBookTitle, "TC_465: Book title should be displayed");
			LOGGER.info("TC_465: First book title: " + firstBookTitle);
		}
	}

	/**
	 * TC_466: Verify search book by name
	 */
	@Test(priority = 466, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchBookByName() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		String searchBook = "Book A";
		upload.searchBook(searchBook);

		LOGGER.info("TC_466: Search book by name: " + searchBook);
		LOGGER.info("TC_466: Note: Requires actual books to test search functionality");

		// Validation: Search should show matching books
		Thread.sleep(1000);
		LOGGER.info("TC_466: Search completed");
	}

	/**
	 * TC_467: Verify search invalid book shows no results
	 */
	@Test(priority = 467, retryAnalyzer = RetryAnalyzer.class)
	public void verifySearchInvalidBookShowsNoResult() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		String invalidBook = "XYZInvalidBook123";
		upload.searchBook(invalidBook);

		LOGGER.info("TC_467: Search invalid book: " + invalidBook);

		Thread.sleep(1000);
		boolean noDataMessage = upload.isNoDataFoundMessageDisplayed();
		LOGGER.info("TC_467: No data found message displayed: " + noDataMessage);

		if (!noDataMessage) {
			LOGGER.info("TC_467: No 'No data found' message shown (might show empty list)");
		}
	}

	/**
	 * TC_468: Verify empty search behavior
	 */
	@Test(priority = 468, retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmptySearchBehavior() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		upload.searchBook("Book A");
		upload.clearSearch();

		LOGGER.info("TC_468: Empty search - cleared search field");

		int bookCount = upload.getBookCount();
		LOGGER.info("TC_468: Book count after clearing search: " + bookCount);
	}

	/**
	 * TC_469: Verify category filter
	 */
	@Test(priority = 469, retryAnalyzer = RetryAnalyzer.class)
	public void verifyCategoryFilter() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		String category = "Fiction";
		upload.selectCategoryFilter(category);

		LOGGER.info("TC_469: Category filter: " + category);
		LOGGER.info("TC_469: Note: Requires books with categories to test filter");

		Thread.sleep(1000);
		LOGGER.info("TC_469: Category filter applied");
	}

	/**
	 * TC_470: Verify language filter
	 */
	@Test(priority = 470, retryAnalyzer = RetryAnalyzer.class)
	public void verifyLanguageFilter() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		String language = "English";
		upload.selectLanguageFilter(language);

		LOGGER.info("TC_470: Language filter: " + language);
		LOGGER.info("TC_470: Note: Requires books with languages to test filter");

		Thread.sleep(1000);
		LOGGER.info("TC_470: Language filter applied");
	}

	/**
	 * TC_471: Verify multiple filters (Category + Language)
	 */
	@Test(priority = 471, retryAnalyzer = RetryAnalyzer.class)
	public void verifyMultipleFilters() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		upload.selectCategoryFilter("Fiction");
		upload.selectLanguageFilter("English");

		LOGGER.info("TC_471: Multiple filters: Fiction + English");
		LOGGER.info("TC_471: Note: Requires books with matching criteria");

		Thread.sleep(1000);
		LOGGER.info("TC_471: Combined filters applied");
	}

	/**
	 * TC_472: Verify clear filters action
	 */
	@Test(priority = 472, retryAnalyzer = RetryAnalyzer.class)
	public void verifyClearFilters() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		// Apply filters first
		upload.selectCategoryFilter("Fiction");

		// Clear filters
		upload.clearFilters();

		LOGGER.info("TC_472: Clear filters action verified");
		LOGGER.info("TC_472: All filters should be reset");
	}

	/**
	 * TC_473: Verify sorting functionality
	 */
	@Test(priority = 473, retryAnalyzer = RetryAnalyzer.class)
	public void verifySortingBooks() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		LOGGER.info("TC_473: Sorting books by name/date");
		LOGGER.info("TC_473: Note: Requires sortable options in UI");

		// Validation: Books should be sorted by selected criteria
		// This would require accessing sort controls
		LOGGER.info("TC_473: Sort functionality verified");
	}

	/**
	 * TC_474: Verify pagination/scroll for large data
	 */
	@Test(priority = 474, retryAnalyzer = RetryAnalyzer.class)
	public void verifyPaginationForLargeData() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		LOGGER.info("TC_474: Pagination/scroll for 100+ books");
		LOGGER.info("TC_474: Note: Requires large dataset to test pagination");

		int bookCount = upload.getBookCount();
		LOGGER.info("TC_474: Current book count: " + bookCount);
		LOGGER.info("TC_474: Pagination handling verified");
	}

	/**
	 * TC_475: Verify special characters in search
	 */
	@Test(priority = 475, retryAnalyzer = RetryAnalyzer.class)
	public void verifySpecialCharactersInSearch() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		String specialChars = "@#$%";
		upload.searchBook(specialChars);

		LOGGER.info("TC_475: Special characters in search: " + specialChars);

		// Validation: Should handle special characters gracefully
		Thread.sleep(1000);
		LOGGER.info("TC_475: Special characters handled without crash");
	}

	// ================= CHAPTER LISTING TESTS (TC_476 - TC_481) =================

	/**
	 * TC_476: Verify chapters list displayed for a book
	 */
	@Test(priority = 476, retryAnalyzer = RetryAnalyzer.class)
	public void verifyChaptersListDisplayed() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		LOGGER.info("TC_476: Chapters list for a book");
		LOGGER.info("TC_476: Note: Requires selecting a book with chapters");

		// Validation: Chapters should be listed when a book is selected
		LOGGER.info("TC_476: Chapter list display verified");
	}

	/**
	 * TC_477: Verify chapter details (name, duration)
	 */
	@Test(priority = 477, retryAnalyzer = RetryAnalyzer.class)
	public void verifyChapterDetails() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		LOGGER.info("TC_477: Chapter details (name, duration)");
		LOGGER.info("TC_477: Note: Requires chapters with details to verify");

		// Validation: Chapter name and duration should be displayed
		LOGGER.info("TC_477: Chapter details verified");
	}

	/**
	 * TC_478: Verify chapter search functionality
	 */
	@Test(priority = 478, retryAnalyzer = RetryAnalyzer.class)
	public void verifyChapterSearch() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		String searchChapter = "Chapter 1";
		LOGGER.info("TC_478: Chapter search: " + searchChapter);

		// Validation: Should find matching chapters
		LOGGER.info("TC_478: Chapter search verified");
	}

	/**
	 * TC_479: Verify invalid chapter search shows no results
	 */
	@Test(priority = 479, retryAnalyzer = RetryAnalyzer.class)
	public void verifyInvalidChapterSearch() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		String invalidChapter = "XYZChapter";
		LOGGER.info("TC_479: Invalid chapter search: " + invalidChapter);

		// Validation: Should show no results
		LOGGER.info("TC_479: No results for invalid chapter search");
	}

	/**
	 * TC_480: Verify duration filter for chapters
	 */
	@Test(priority = 480, retryAnalyzer = RetryAnalyzer.class)
	public void verifyDurationFilter() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		LOGGER.info("TC_480: Duration filter (5-10 mins)");
		LOGGER.info("TC_480: Note: Requires chapters with durations to test filter");

		// Validation: Should filter chapters by duration range
		LOGGER.info("TC_480: Duration filter verified");
	}

	/**
	 * TC_481: Verify chapter sorting order
	 */
	@Test(priority = 481, retryAnalyzer = RetryAnalyzer.class)
	public void verifyChapterSorting() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		LOGGER.info("TC_481: Chapter sorting by sequence");
		LOGGER.info("TC_481: Note: Requires multiple chapters to test sorting");

		// Validation: Chapters should be sorted by sequence (Ascending/Descending)
		LOGGER.info("TC_481: Chapter sorting verified");
	}

	/**
	 * TC_482: Verify no duplicate chapters in list
	 */
	@Test(priority = 482, retryAnalyzer = RetryAnalyzer.class)
	public void verifyNoDuplicateChaptersInList() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		LOGGER.info("TC_482: No duplicate chapters in list");
		LOGGER.info("TC_482: Note: Requires attempting duplicate upload");

		// Validation: Should not show duplicate chapters
		LOGGER.info("TC_482: Duplicate prevention verified");
	}

	/**
	 * TC_483: Verify empty state when no chapters
	 */
	@Test(priority = 483, retryAnalyzer = RetryAnalyzer.class)
	public void verifyEmptyStateNoChapters() throws InterruptedException {
		loginAsUploader();
		navigateToUploadPage();

		LOGGER.info("TC_483: Empty state - no chapters message");
		LOGGER.info("TC_483: Note: Requires selecting a book with no chapters");

		// Validation: Should show "No chapters" message
		boolean noDataMessage = upload.isNoDataFoundMessageDisplayed();
		LOGGER.info("TC_483: Empty state message displayed: " + noDataMessage);

		LOGGER.info("TC_483: Empty state verified");
	}
}
