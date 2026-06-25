package pages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;

import base.BasePage;
import utils.ConfigReader;
import utils.LoggerUtils;

/**
 * Page Object that orchestrates Chapter-management flows that span
 * multiple underlying pages (Dashboard, CreatorSettings, ForCreator,
 * Upload). Mirrors the conventions used by {@link AudioPlayerPage}:
 * thin wrappers over the underlying page actions, with all multi-step
 * orchestration, file resolution, and wait/synchronization logic kept
 * inside this class so the {@code ChapterTests} test class stays lean.
 *
 * <p>Specifically, this class is the home for:
 * <ul>
 *   <li>Uploader / consumer login helpers and post-login landing
 *       stabilization.</li>
 *   <li>Cross-page navigation to the Upload form (hamburger → For
 *       Creators → Add Book).</li>
 *   <li>Book-detail seeding (title, author, language, country, category,
 *       genre, summary, portrait/landscape images).</li>
 *   <li>Audio-chapter seeding for the chapter list.</li>
 *   <li>The "open an existing book chapter section, or seed a new one"
 *       flow used by every chapter test.</li>
 *   <li>Null-safe URL / window-handle accessors used by the multi-tab
 *       tests (TC_506, TC_507).</li>
 *   <li>File-resolution helpers for portrait, landscape, audio, and
 *       invalid-format uploads (config first, then Downloads, then
 *       synthesized temp file).</li>
 * </ul>
 */
public class ChapterPage extends BasePage {

	private final LoginPage login;
	private final DashboardPage dashboard;
	private final CreatorSettingsPage creatorSettings;
	private final ForCreatorPage forCreatorPage;
	private final UploadPage upload;

	public ChapterPage(WebDriver driver) {
		super(driver);
		this.login = new LoginPage(driver);
		this.dashboard = new DashboardPage(driver);
		this.creatorSettings = new CreatorSettingsPage(driver);
		this.forCreatorPage = new ForCreatorPage(driver);
		this.upload = new UploadPage(driver);
	}

	// ==================== Login helpers ====================

	/**
	 * Login as the configured uploader user and stabilize on the post-login
	 * landing page. Throws {@link SkipException} if login cannot complete.
	 */
	public void loginAsUploader() {
		try {
			login.openLogin();
			login.loginUser(getUploaderEmail(), getUploaderPassword());
			login.clickNextAfterLogin();
			boolean loginSettled = new WebDriverWait(driver, Duration.ofSeconds(30)).until(currentDriver -> {
				if (!login.isOnLoginPage()) {
					return true;
				}
				String currentUrl = safeLower(currentDriver.getCurrentUrl());
				return !currentUrl.contains("/login") && !currentUrl.contains("signin");
			});
			Assert.assertTrue(loginSettled, "Uploader login should move past the login page");
			LoggerUtils.logInfo("Logged in as uploader");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			throw new SkipException("Failed to login as uploader: " + safe(e.getMessage()), e);
		}
	}

	/**
	 * Login as the configured consumer user and wait for the dashboard to
	 * settle. Throws {@link SkipException} if login cannot complete.
	 */
	public void loginAsConsumer() {
		try {
			login.openLogin();
			login.loginUser(getConsumerEmail(), getConsumerPassword());
			login.clickNextAfterLogin();
			dashboard.waitForPageReady();
			LoggerUtils.logInfo("Logged in as consumer");
		} catch (SkipException e) {
			throw e;
		} catch (Exception e) {
			throw new SkipException("Failed to login as consumer: " + safe(e.getMessage()), e);
		}
	}

	// ==================== Credentials ====================

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

	// ==================== Unique book title ====================

	/**
	 * @return a fresh, unique book title of the form
	 *         {@code "Automation Book XXXXXX"} where {@code XXXXXX} is a
	 *         6-character random suffix. Safe to call from any test.
	 */
	public String createUniqueBookTitle() {
		return "Automation Book " + UUID.randomUUID().toString().substring(0, 6);
	}

	// ==================== Cross-page navigation ====================

	/**
	 * Navigate from the post-login landing page to the Upload form by
	 * walking the hamburger menu (For Creators → Add Book) and then
	 * stabilizing on the upload form.
	 */
	public void navigateToUploadPage() {
		boolean landingReady = new WebDriverWait(driver, Duration.ofSeconds(30)).until(currentDriver -> dashboard
				.waitForDashboardShell() || dashboard.isOnCreatorPage() || dashboard.isUploadPageOpened()
				|| dashboard.isHeaderLogoVisible() || dashboard.isProfileIconVisible());
		Assert.assertTrue(landingReady,
				"Post-login landing page should be stable. Current URL: " + safeLower(getCurrentUrlSafely()));

		creatorSettings.clickHamburgerMenu();
		creatorSettings.clickForCreators();
		creatorSettings.clickAddBook();

		upload.waitForUploadPageToLoad();
		Assert.assertTrue(upload.isUploadPageDisplayed() || dashboard.isUploadPageOpened(),
				"Upload page should open after clicking Add Book");
	}

	/**
	 * Open the For Creators listing page (show_uploader_books). The URL
	 * is built from the configured base URL so this works against any
	 * environment.
	 */
	public void openForCreatorsListingPage() {
		String baseUrl = ConfigReader.getProperty("url", "https://web-splay.acceses.com/");
		if (!baseUrl.endsWith("/")) {
			baseUrl = baseUrl + "/";
		}
		driver.get(baseUrl + "show_uploader_books");
		forCreatorPage.waitForListingState();
	}

	// ==================== Book-detail seeding ====================

	/**
	 * Fill the upload form's required book-detail fields. Language,
	 * country-category, category, country, and genre are sourced from
	 * config with sane defaults so the call site only needs to provide
	 * the per-test-unique {@code title} and {@code summary}.
	 */
	public void fillValidBookDetails(String title, String summary) {
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

	/**
	 * Resolve the portrait and landscape image paths (config first, then
	 * Downloads) and upload them. Throws {@link SkipException} if no
	 * usable images can be found.
	 */
	public void uploadValidPortraitAndLandscapeImages() {
		String portraitImagePath = resolvePortraitImagePath();
		String landscapeImagePath = resolveLandscapeImagePath();
		if (portraitImagePath.isBlank() || landscapeImagePath.isBlank()) {
			throw new SkipException(
					"Valid portrait and landscape JPG/PNG images are required via config or Downloads.");
		}
		creatorSettings.uploadBookImages(portraitImagePath, landscapeImagePath);
	}

	// ==================== Audio-chapter seeding ====================

	/**
	 * Drive the full "create a fresh Pending book with N audio chapters"
	 * flow: navigate to upload → fill details → upload images → save →
	 * add N audio chapters. Used when no existing book satisfies the
	 * minimum-chapter requirement.
	 */
	public void createPendingBookWithChapters(String testCaseId, int chapterCount) {
		String audioFilePath = resolveAudioUploadFilePath();
		if (audioFilePath.isBlank()) {
			throw new SkipException(testCaseId + ": A valid audio file is required to seed chapter data.");
		}

		LoggerUtils.logInfo(testCaseId + " - STEP 6: No existing Pending book matched. Creating one with "
				+ chapterCount + " chapter(s)");

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
			waitQuietly(1500);
			creatorSettings.prepareForAudioChapterCreation();
		}

		LoggerUtils.logInfo(testCaseId + " - STEP 7: Seeded book '" + seededTitle + "' is ready for chapter edit coverage");
	}

	/**
	 * Walk the existing Pending books list (up to 3) and find one whose
	 * chapter count is at least {@code minimumChapterCount}. If none of
	 * the first 3 satisfy the requirement, fall back to creating a fresh
	 * Pending book with the required chapter count. The test is left
	 * positioned on the chapter screen of the chosen book.
	 */
	public void openExistingBookChapterSection(String testCaseId, int minimumChapterCount) {
		LoggerUtils.logInfo(testCaseId + " - STEP 1: Navigating to existing Pending books");
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
			LoggerUtils.logInfo(
					testCaseId + " - STEP 2: Trying existing book index " + index + " = '" + existingBookTitle + "'");

			forCreatorPage.clickEditBookByIndex(index);
			LoggerUtils.logInfo(testCaseId + " - STEP 3: Edit book screen opened");
			creatorSettings.clickNext();
			waitQuietly(2000);
			creatorSettings.waitForAudioUploadScreen();
			LoggerUtils.logInfo(testCaseId + " - STEP 4: Chapter screen opened after clicking Next");

			int chapterCount = creatorSettings.getChapterCount();
			LoggerUtils.logInfo(testCaseId + " - STEP 5: Existing chapter count for '" + existingBookTitle + "' = "
					+ chapterCount);
			if (chapterCount >= minimumChapterCount) {
				LoggerUtils.logInfo(testCaseId + " - STEP 6: Using existing book '" + existingBookTitle
						+ "' for chapter operation");
				return;
			}
		}

		LoggerUtils.logInfo(testCaseId + " - STEP 6: No suitable existing book found in first " + booksToTry
				+ " Pending entries. Creating fresh chapter data instead");
		createPendingBookWithChapters(testCaseId, minimumChapterCount);
	}

	// ==================== Toast capture helper ====================

	/**
	 * Fetch the most recent success message from the upload form and
	 * log it with the test case id. Returns the message (or blank) so
	 * callers can also assert on it.
	 */
	public String logSuccessToast(String testCaseId) {
		String successMessage = upload.getSuccessMessage();
		LoggerUtils.logInfo(testCaseId + " - SUCCESS MESSAGE: " + successMessage);
		return successMessage;
	}

	/**
	 * @return the most recent error message surfaced by the upload form,
	 *         or an empty string if no error is shown.
	 */
	public String uploadErrorMessage() {
		try {
			return upload.getErrorMessage();
		} catch (Exception e) {
			return "";
		}
	}

	// ==================== Multi-tab helpers (TC_506 / TC_507) ====================

	/**
	 * Open a fresh browser tab (about:blank) and switch the driver to it.
	 *
	 * @return the new tab's window handle.
	 */
	public String openSecondaryTab() {
		Objects.requireNonNull((JavascriptExecutor) driver).executeScript("window.open('about:blank','_blank');");
		List<String> windowHandles = new ArrayList<>(driver.getWindowHandles());
		String newTab = windowHandles.get(windowHandles.size() - 1);
		driver.switchTo().window(newTab);
		return newTab;
	}

	/**
	 * Open a search result by title and verify the resulting book-details
	 * page is ready for playback. Used by TC_506 / TC_507.
	 *
	 * @return the viewing tab's URL after the page is ready.
	 */
	public String openBookDetailsForPlayback(String testCaseId, String bookTitle) {
		dashboard.waitForPageReady();
		Assert.assertTrue(dashboard.isSearchBarVisible(),
				testCaseId + ": Header search bar should be visible before opening the target book");
		dashboard.submitSearch(bookTitle);
		Assert.assertTrue(dashboard.clickFirstSearchResult(),
				testCaseId + ": Search should open the target book details page");
		Assert.assertTrue(dashboard.isBookDetailsPageVisible(),
				testCaseId + ": Book details page should open for the target book");
		Assert.assertTrue(dashboard.waitForBookDataToLoad(),
				testCaseId + ": Book details should finish loading before playback");
		return safeLower(getCurrentUrlSafely());
	}

	/**
	 * Drive the multi-tab "playing chapter in tab A, edit/delete in tab B,
	 * switch back to A and refresh" pattern used by TC_506 and TC_507.
	 *
	 * @param testCaseId       TC id used in error messages.
	 * @param bookTitle        title to look up in the Approved filter.
	 * @param primaryTabHandle the window handle of the tab that started
	 *                         playback (will be switched back to).
	 */
	public void performAdminEditInSecondTab(String testCaseId, String bookTitle, String primaryTabHandle) {
		openSecondaryTab();
		LoggerUtils.logInfo(testCaseId + " - STEP 3: Opened second tab for chapter operation");

		openForCreatorsListingPage();
		forCreatorPage.selectApprovedFilter();
		forCreatorPage.searchBook(bookTitle);
		Assert.assertTrue(forCreatorPage.containsVisibleBookTitle(bookTitle),
				testCaseId + ": Target book should be visible in Approved filter before operating on Chapter 1");

		forCreatorPage.clickEditBookByIndex(0);
		creatorSettings.clickNext();
		waitQuietly(2000);
		creatorSettings.waitForAudioUploadScreen();
	}

	/**
	 * Refresh the primary (viewing) tab after a cross-tab mutation and
	 * return the post-refresh URL (null-safe).
	 */
	public String refreshPrimaryTabAndGetUrl(String primaryTabHandle) {
		driver.switchTo().window(primaryTabHandle);
		driver.navigate().refresh();
		waitQuietly(2000);
		return safeLower(getCurrentUrlSafely());
	}

	// ==================== Null-safe accessors ====================

	/**
	 * @return the current URL, lower-cased, or empty string if the driver
	 *         cannot be queried.
	 */
	public String getCurrentUrlSafely() {
		try {
			return driver.getCurrentUrl();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Open a URL in the current driver, tolerating a null argument. If
	 * the argument is blank, no navigation is performed and the test
	 * will continue without an NPE.
	 */
	public void navigateSafely(String url) {
		if (url == null || url.isBlank()) {
			return;
		}
		driver.get(url);
	}

	// ==================== File-resolution helpers ====================

	/**
	 * Resolve a path configured under {@code configKey}. Tries the literal
	 * value first, then {@code user.dir/configuredPath}, then throws an
	 * {@link IllegalStateException} with a descriptive message. Returns
	 * an empty string if the value is blank/missing.
	 */
	public String resolveOptionalConfiguredPath(String configKey) {
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

	/**
	 * Find the first file in {@code ~/Downloads} whose name ends with any
	 * of the given extensions. Returns "" if no Downloads directory, no
	 * matching file, or an I/O error occurs.
	 */
	public String findFirstFileInDownloads(List<String> extensions, String missingMessage) {
		Path downloadsDirectory = Paths.get(System.getProperty("user.home"), "Downloads");
		if (!Files.exists(downloadsDirectory)) {
			return "";
		}

		try {
			return Files.list(downloadsDirectory).filter(Files::isRegularFile)
					.filter(path -> hasAnyExtension(path.getFileName().toString(), extensions))
					.sorted(Comparator.comparing(Path::toString)).map(Path::toString).findFirst().orElseGet(() -> {
						if (missingMessage != null) {
							LoggerUtils.logInfo(missingMessage);
						}
						return "";
					});
		} catch (IOException e) {
			LoggerUtils.logInfo("Unable to scan Downloads directory: " + safe(e.getMessage()));
			return "";
		}
	}

	private boolean hasAnyExtension(String fileName, List<String> extensions) {
		String normalized = safe(fileName).toLowerCase();
		for (String extension : extensions) {
			if (!extension.isBlank() && normalized.endsWith(extension.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/** @return the configured portrait image path, or a Downloads fallback, or "". */
	public String resolvePortraitImagePath() {
		String configuredPath = resolveOptionalConfiguredPath("uploadPortraitImagePath");
		if (!configuredPath.isBlank()) {
			return configuredPath;
		}
		return findFirstFileInDownloads(List.of(".png", ".jpg", ".jpeg"),
				"Portrait image not configured. Continuing without image upload because no JPG/PNG was found in Downloads.");
	}

	/** @return the configured landscape image path, or a Downloads fallback, or "". */
	public String resolveLandscapeImagePath() {
		String configuredPath = resolveOptionalConfiguredPath("uploadLandscapeImagePath");
		if (!configuredPath.isBlank()) {
			return configuredPath;
		}
		return findFirstFileInDownloads(List.of(".png", ".jpg", ".jpeg"),
				"Landscape image not configured. Continuing without landscape image because no JPG/PNG was found in Downloads.");
	}

	/** @return the configured audio upload path, or a Downloads fallback, or "". */
	public String resolveAudioUploadFilePath() {
		String configuredPath = resolveOptionalConfiguredPath("uploadAudioFilePath");
		if (!configuredPath.isBlank()) {
			return configuredPath;
		}
		return findFirstFileInDownloads(List.of(".mp3", ".wav", ".m4a", ".aac"),
				"Audio file not configured. Add uploadAudioFilePath or place an audio file in Downloads.");
	}

	/**
	 * @return the configured invalid-format path, or the first .exe/.txt
	 *         /etc. file in Downloads, or a freshly-created temp .txt
	 *         file (guaranteed non-blank when system I/O works).
	 */
	public String resolveInvalidUploadPath() {
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
			LoggerUtils.logInfo("Unable to create temporary invalid upload file: " + safe(e.getMessage()));
			return "";
		}
	}

	// ==================== Local helpers (public for test use) ====================

	/**
	 * Wait for the given number of milliseconds. Throws
	 * {@link RuntimeException} if the wait is interrupted so the test
	 * surfaces a clear failure rather than silently swallowing the
	 * interrupt.
	 */
	public void waitQuietly(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Sleep interrupted", e);
		}
	}

	/**
	 * @return the value, or empty string if it is {@code null}. Useful
	 *         for null-safe concatenation when logging exception
	 *         messages in the test.
	 */
	public String safeString(String value) {
		return value == null ? "" : value;
	}

	/**
	 * @return the value (or empty) lower-cased using {@link Locale#ROOT}.
	 *         Used by the test for null-safe URL comparison.
	 */
	public String safeLowerUrl(String value) {
		return safeString(value).toLowerCase(Locale.ROOT);
	}

	private static String safe(String value) {
		return value == null ? "" : value;
	}

	private static String safeLower(String value) {
		return safe(value).toLowerCase(Locale.ROOT);
	}
}
