package pages;

import java.time.Duration;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BasePage;

/**
 * Page Object for Audio Player functionality. Handles Play, Pause, Resume,
 * Forward, Backward, Next/Previous Chapter navigation, and player load
 * synchronisation. All player-specific locators, waits, and clicks live
 * here; the test class only orchestrates scenarios.
 */
public class PlayerPage extends BasePage {

	private final WebDriverWait wait;

	// ================= LOCATORS =================

	private static final By PLAY_AUDIO_BTN = By.xpath("//div[text()='Play Audio']");

	private static final By PAUSE_PLAY_BTN = By.xpath("//div[contains(@style,'background-color: rgb(72, 56, 209)')]");

	// Chapter / seek controls. The `[1]` index on the ancestor::div xpath is
	// important: it pins the locator to the IMMEDIATE clickable parent of the
	// icon image. Without `[1]`, the xpath matches any ancestor <div> on the
	// way up to the root, which often contains a share / like / comment
	// button — and a `click()` on that wrapper ends up firing on whichever
	// child is in the center of its bounding box (not the player control).
	private static final By FORWARD_BTN = By
			.xpath("//img[contains(@src,'ic_next')]/ancestor::div[@tabindex='0'][1]");

	private static final By BACKWARD_BTN = By
			.xpath("//img[contains(@src,'ic_previous')]/ancestor::div[@tabindex='0'][1]");

	private static final By BACKWARD_30_BTN = By
			.xpath("//img[contains(@src,'ic_prev_30')]/ancestor::div[@tabindex='0'][1]");

	private static final By FORWARD_30_BTN = By
			.xpath("//img[contains(@src,'ic_forward_30')]/ancestor::div[@tabindex='0'][1]");

	private static final By FILE_EXAMPLE_MP3 = By.xpath("//*[contains(text(),'File Example Mp3')]");

	private static final By PROGRESS_BAR = By.xpath("//input[@type='range'] | //*[@role='slider']");

	// ================= CONSTRUCTOR =================

	public PlayerPage(WebDriver driver) {
		super(driver);
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	}

	// ================= PLAYER LOAD =================

	/**
	 * Wait until the player bar has finished rendering — i.e. the play button,
	 * the pause button, or the {@code <audio>} element is present in the DOM.
	 * Custom lambda (rather than {@code ExpectedConditions.or}) is used to
	 * avoid the {@code <WebElement>} vs {@code <Boolean>} varargs ambiguity.
	 */
	public void waitForPlayerBar() {
		wait.until(driver -> {
			if (!driver.findElements(PLAY_AUDIO_BTN).isEmpty()) {
				return true;
			}
			if (!driver.findElements(PAUSE_PLAY_BTN).isEmpty()) {
				return true;
			}
			if (!driver.findElements(By.xpath("//audio")).isEmpty()) {
				return true;
			}
			return null;
		});
	}

	// ================= PLAY =================

	/**
	 * Click Play Audio button. Waits for the element to be clickable (visible
	 * + enabled), scrolls it into view, then performs a safe click (standard
	 * Selenium click with JavaScript fallback for interception issues).
	 */
	public void clickPlayAudio() {
		WebElement playBtn = waitForClickable(PLAY_AUDIO_BTN);
		scrollIntoView(playBtn);
		click(playBtn);
		// Wait until the <audio> element is present and reports playing. This is
		// the audio-level signal — independent of any UI button toggle lag.
		waitForAudioToStart();
	}

	/**
	 * Check if audio is playing. Reads the {@code <audio>} element's
	 * {@code paused} JS property via {@link JavascriptExecutor}; falls back to
	 * the play/pause button DOM state when the audio element is not yet
	 * available.
	 */
	public boolean isAudioPlaying() {
		try {
			Object result = ((JavascriptExecutor) driver)
					.executeScript(
							"const a = document.querySelector('audio');"
									+ " return a ? !a.paused : null;");
			if (result instanceof Boolean) {
				return (Boolean) result;
			}
		} catch (Exception ignored) {
			// Fall through to button-based check.
		}
		return driver.findElements(PAUSE_PLAY_BTN).size() > 0;
	}

	/**
	 * Wait until audio starts playing (audio element reports not paused).
	 */
	public void waitForAudioToStart() {
		wait.until(driver -> isAudioPlaying());
	}

	// ================= PAUSE / RESUME =================

	/**
	 * Click the pause / resume toggle. Waits for the toggle to be clickable,
	 * then performs a safe click (Selenium click with JS fallback). After the
	 * click, waits for the destination state on the {@code <audio>} element.
	 * The returned boolean is {@code true} when audio is paused after the
	 * click, {@code false} when it is playing — matching the original "did
	 * we pause?" contract.
	 */
	public boolean clickPausePlay() {
		// Determine whether we are about to pause (currently playing) or
		// resume (currently paused), so we can wait for the correct end state.
		boolean wasPlayingBeforeClick = isAudioPlaying();

		// Wait for whichever toggle is currently rendered to be clickable.
		// Custom lambda because ExpectedConditions.or(<WebElement>, <WebElement>)
		// is ambiguous with the <Boolean> varargs overload.
		WebElement toggle = wait.until(driver -> {
			try {
				if (driver.findElements(PAUSE_PLAY_BTN).size() > 0) {
					return waitForClickable(PAUSE_PLAY_BTN);
				}
				if (driver.findElements(PLAY_AUDIO_BTN).size() > 0) {
					return waitForClickable(PLAY_AUDIO_BTN);
				}
			} catch (Exception ignored) {
				// Element not yet clickable — keep polling.
			}
			return null;
		});
		scrollIntoView(toggle);
		click(toggle);

		if (wasPlayingBeforeClick) {
			// We are pausing — wait until audio reports paused.
			waitForAudioToPause();
			return isAudioPaused();
		}
		// We are resuming — wait until audio reports playing.
		waitForAudioToStart();
		return isAudioPaused();
	}

	/**
	 * Check if audio is paused. Reads the {@code <audio>} element's
	 * {@code paused} JS property; falls back to the play-button DOM state when
	 * the audio element is not yet available.
	 */
	public boolean isAudioPaused() {
		try {
			Object result = ((JavascriptExecutor) driver)
					.executeScript(
							"const a = document.querySelector('audio');"
									+ " return a ? a.paused : null;");
			if (result instanceof Boolean) {
				return (Boolean) result;
			}
		} catch (Exception ignored) {
			// Fall through to button-based check.
		}
		return driver.findElements(PLAY_AUDIO_BTN).size() > 0;
	}

	/**
	 * Wait until audio is paused (audio element reports paused).
	 */
	public void waitForAudioToPause() {
		wait.until(driver -> isAudioPaused());
	}

	public void waitForPlayControlsReady() {
		wait.until(driver -> {
			if (!driver.findElements(PLAY_AUDIO_BTN).isEmpty()) {
				return true;
			}
			if (!driver.findElements(PAUSE_PLAY_BTN).isEmpty()) {
				return true;
			}
			if (!driver.findElements(FILE_EXAMPLE_MP3).isEmpty()) {
				return true;
			}
			return null;
		});
	}

	/**
	 * Wait until the advanced player controls (forward 30s / backward 30s /
	 * next-chapter / previous-chapter / progress bar) are visible in the DOM.
	 * The app only reveals these controls after audio has actually started
	 * playing and advanced past t=0, so callers of {@link #clickForward30()},
	 * {@link #clickBackward30()}, {@link #clickNextChapter()},
	 * {@link #clickPreviousChapter()}, {@link #clickForward30Seconds()}, and
	 * {@link #clickBackward30Seconds()} must invoke this gate first or the
	 * click will time out waiting for the button to become clickable.
	 */
	public void waitForAdvancedControlsVisible() {
		wait.until(driver -> {
			if (isDisplayed(FILE_EXAMPLE_MP3)) {
				return true;
			}
			if (isDisplayed(FORWARD_30_BTN) || isDisplayed(BACKWARD_30_BTN)) {
				return true;
			}
			if (isDisplayed(FORWARD_BTN) || isDisplayed(BACKWARD_BTN)) {
				return true;
			}
			if (isDisplayed(PROGRESS_BAR)) {
				return true;
			}
			return null;
		});
	}

	public boolean isPlayerResponsive() {
		return driver.findElements(PLAY_AUDIO_BTN).size() > 0 || driver.findElements(PAUSE_PLAY_BTN).size() > 0
				|| driver.findElements(FILE_EXAMPLE_MP3).size() > 0;
	}

	// ================= FORWARD / BACKWARD =================

	/**
	 * Click the next-chapter (forward) button. First waits for the advanced
	 * player controls to become visible (they are only rendered after audio
	 * has started playing and advanced past t=0), then waits for the button
	 * to be clickable, scrolls it into view, and performs a safe click.
	 */
	public void clickForward30() {
		waitForAdvancedControlsVisible();
		WebElement forward = waitForClickable(FORWARD_BTN);
		scrollIntoView(forward);
		click(forward);
	}

	/**
	 * Click the previous-chapter (backward) button. First waits for the
	 * advanced player controls to become visible, then waits for the button
	 * to be clickable, scrolls it into view, and performs a safe click.
	 */
	public void clickBackward30() {
		waitForAdvancedControlsVisible();
		WebElement backward = waitForClickable(BACKWARD_BTN);
		scrollIntoView(backward);
		click(backward);
	}

	/**
	 * Alias of {@link #clickForward30()} for clarity at the call site when the
	 * intent is "navigate to next chapter" rather than "skip 30s".
	 */
	public void clickNextChapter() {
		waitForAdvancedControlsVisible();
		WebElement nextChapter = waitForClickable(FORWARD_BTN);
		scrollIntoView(nextChapter);
		click(nextChapter);
	}

	/**
	 * Alias of {@link #clickBackward30()} for clarity at the call site when
	 * the intent is "navigate to previous chapter" rather than "rewind 30s".
	 */
	public void clickPreviousChapter() {
		waitForAdvancedControlsVisible();
		WebElement previousChapter = waitForClickable(BACKWARD_BTN);
		scrollIntoView(previousChapter);
		click(previousChapter);
	}

	/**
	 * Click the forward-30s button. First waits for the advanced player
	 * controls to become visible, then waits for the button to be clickable,
	 * scrolls it into view, and performs a safe click.
	 */
	public void clickForward30Seconds() {
		waitForAdvancedControlsVisible();
		WebElement forward30 = waitForClickable(FORWARD_30_BTN);
		scrollIntoView(forward30);
		click(forward30);
	}

	/**
	 * Click the backward-30s button. First waits for the advanced player
	 * controls to become visible, then waits for the button to be clickable,
	 * scrolls it into view, and performs a safe click.
	 */
	public void clickBackward30Seconds() {
		waitForAdvancedControlsVisible();
		WebElement backward30 = waitForClickable(BACKWARD_30_BTN);
		scrollIntoView(backward30);
		click(backward30);
	}

	// ================= HELPER METHODS =================
	// Click handling is delegated to BasePage.click(WebElement), which performs
	// a standard Selenium click and falls back to a JavaScript click on
	// interception. No local click helper is required.

	// ==================== Null-safe accessors ====================

	/**
	 * Null-safe string accessor used by player tests when logging exception
	 * messages. Returns an empty string when {@code value} is {@code null}.
	 *
	 * @param value raw value (may be {@code null})
	 * @return the value, or empty string if {@code null}
	 */
	public String safeString(String value) {
		return value == null ? "" : value;
	}

}
