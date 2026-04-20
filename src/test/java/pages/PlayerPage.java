package pages;

import java.time.Duration;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BasePage;

/**
 * Page Object for Audio Player functionality. Handles Play, Pause, Resume,
 * Forward, and Backward actions.
 */
public class PlayerPage extends BasePage {

	private final WebDriverWait wait;

	// ================= LOCATORS =================

	private static final By PLAY_AUDIO_BTN = By.xpath("//div[text()='Play Audio']");

	private static final By PAUSE_PLAY_BTN = By.xpath("//div[contains(@style,'background-color: rgb(72, 56, 209)')]");

	private static final By FORWARD_BTN = By.xpath("//img[contains(@src,'ic_next')]/ancestor::div[@tabindex='0']");

	private static final By BACKWARD_BTN = By.xpath("//img[contains(@src,'ic_previous')]/ancestor::div[@tabindex='0']");

	private static final By BACKWARD_30_BTN = By
			.xpath("//img[contains(@src,'ic_prev_30')]/ancestor::div[@tabindex='0']");

	private static final By FORWARD_30_BTN = By
			.xpath("//img[contains(@src,'ic_forward_30')]/ancestor::div[@tabindex='0']");

	private static final By FILE_EXAMPLE_MP3 = By.xpath("//*[contains(text(),'File Example Mp3')]");

	// ================= CONSTRUCTOR =================

	public PlayerPage(WebDriver driver) {
		super(driver);
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	}

	// ================= PLAYER LOAD =================

	/**
	 * Wait until player is loaded and ready.
	 */
	public void waitForPlayerBar() {
		wait.until(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(PLAY_AUDIO_BTN),
				ExpectedConditions.presenceOfElementLocated(PAUSE_PLAY_BTN),
				ExpectedConditions.presenceOfElementLocated(By.xpath("//audio"))));
	}

	// ================= PLAY =================

	/**
	 * Click Play Audio button.
	 */
	public void clickPlayAudio() {
		WebElement playBtn = wait.until(ExpectedConditions.presenceOfElementLocated(PLAY_AUDIO_BTN));
		clickWithJS(playBtn);
	}

	/**
	 * Check if audio is playing.
	 */
	public boolean isAudioPlaying() {
		return driver.findElements(PAUSE_PLAY_BTN).size() > 0;
	}

	/**
	 * Wait until audio starts playing.
	 */
	public void waitForAudioToStart() {
		wait.until(driver -> isAudioPlaying());
	}

	// ================= PAUSE / RESUME =================

	/**
	 * Toggle Play/Pause button.
	 */
	public boolean clickPausePlay() {
		WebElement pauseBtn = wait.until(ExpectedConditions.presenceOfElementLocated(PAUSE_PLAY_BTN));

		clickWithJS(pauseBtn);
		return isAudioPaused();
	}

	/**
	 * Check if audio is paused.
	 */
	public boolean isAudioPaused() {
		return driver.findElements(PLAY_AUDIO_BTN).size() > 0;
	}

	/**
	 * Wait until audio is paused.
	 */
	public void waitForAudioToPause() {
		wait.until(driver -> isAudioPaused());
	}

	public void waitForPlayControlsReady() {
		wait.until(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(PLAY_AUDIO_BTN),
				ExpectedConditions.presenceOfElementLocated(PAUSE_PLAY_BTN),
				ExpectedConditions.presenceOfElementLocated(FILE_EXAMPLE_MP3)));
	}

	public boolean isPlayerResponsive() {
		return driver.findElements(PLAY_AUDIO_BTN).size() > 0 || driver.findElements(PAUSE_PLAY_BTN).size() > 0
				|| driver.findElements(FILE_EXAMPLE_MP3).size() > 0;
	}

	// ================= FORWARD / BACKWARD =================

	/**
	 * Click Next (Forward Chapter).
	 */
	public void clickForward30() {
		WebElement forward = wait.until(ExpectedConditions.presenceOfElementLocated(FORWARD_BTN));

		scrollIntoView(forward);
		clickWithJS(forward);
	}

	/**
	 * Click Previous (Backward Chapter).
	 */
	public void clickBackward30() {
		WebElement backward = wait.until(ExpectedConditions.presenceOfElementLocated(BACKWARD_BTN));

		scrollIntoView(backward);
		clickWithJS(backward);
	}

	public void clickNextChapter() {
		WebElement nextChapter = wait.until(ExpectedConditions.presenceOfElementLocated(FORWARD_BTN));
		scrollIntoView(nextChapter);
		clickWithJS(nextChapter);
	}

	public void clickPreviousChapter() {
		WebElement previousChapter = wait.until(ExpectedConditions.presenceOfElementLocated(BACKWARD_BTN));
		scrollIntoView(previousChapter);
		clickWithJS(previousChapter);
	}

	/**
	 * Click Forward 30 seconds.
	 */
	public void clickForward30Seconds() {
		WebElement forward30 = wait.until(ExpectedConditions.presenceOfElementLocated(FORWARD_30_BTN));

		scrollIntoView(forward30);
		clickWithJS(forward30);
	}

	/**
	 * Click Backward 30 seconds.
	 */
	public void clickBackward30Seconds() {
		WebElement backward30 = wait.until(ExpectedConditions.presenceOfElementLocated(BACKWARD_30_BTN));

		scrollIntoView(backward30);
		clickWithJS(backward30);
	}

	// ================= HELPER METHODS =================

	/**
	 * Perform click using JavaScript (avoids interception issues).
	 */
	private void clickWithJS(WebElement element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
	}

}
