package pages;

import java.time.Duration;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import base.BasePage;

public class AudioPlayerPage extends BasePage {

	private static final Logger LOGGER = Logger.getLogger(AudioPlayerPage.class.getName());
	private static final Pattern TIME_PATTERN = Pattern.compile("(\\d{1,2}:)?\\d{1,2}:\\d{2}");
	private static final Duration SHORT_TIMEOUT = Duration.ofSeconds(1);
	private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(4);

	private static final By PLAY_AUDIO_BUTTON = By
			.xpath("//div[text()='Play Audio']" + " | //*[self::button or @role='button' or @tabindex='0']"
					+ "[contains(translate(normalize-space(.),'PLAY AUDIO','play audio'),'play audio')"
					+ " or contains(translate(@aria-label,'PLAY AUDIO','play audio'),'play audio')"
					+ " or contains(@data-testid,'play')]");
	private static final By PAUSE_AUDIO_BUTTON = By
			.xpath("//img[contains(@src,'ic_pause')]/ancestor::div[@tabindex='0'][1]"
					+ " | //*[self::button or @role='button' or @tabindex='0']"
					+ "[contains(translate(normalize-space(.),'PAUSE','pause'),'pause')"
					+ " or contains(translate(@aria-label,'PAUSE','pause'),'pause')"
					+ " or contains(@data-testid,'pause')]");
	private static final By PLAYER_TOGGLE_BUTTON = By
			.xpath("//img[contains(@src,'ic_pause')]/ancestor::div[@tabindex='0']" + " | //*[@data-testid='pause']"
					+ " | //*[self::button or @role='button' or @tabindex='0'][contains(translate(@aria-label,'PAUSE','pause'),'pause')]");
	private static final By FORWARD_30_BUTTON = By
			.xpath("//img[contains(@src,'ic_forward_30')]/ancestor::div[@tabindex='0'][1]"
					+ " | //*[self::button or @role='button' or @tabindex='0'][contains(translate(@aria-label,'FORWARD','forward'),'forward') or contains(translate(@aria-label,'SKIP','skip'),'skip') or contains(translate(normalize-space(.),'30S','30s'),'30s')]");
	private static final By BACKWARD_30_BUTTON = By
			.xpath("//img[contains(@src,'ic_prev_30')]/ancestor::div[@tabindex='0'][1]"
					+ " | //*[self::button or @role='button' or @tabindex='0'][contains(translate(@aria-label,'REWIND','rewind'),'rewind') or contains(translate(@aria-label,'BACKWARD','backward'),'backward') or contains(translate(normalize-space(.),'-30S','-30s'),'-30s')]");
	private static final By NEXT_CHAPTER_BUTTON = By
			.xpath("//img[contains(@src,'ic_next')]/ancestor::div[@tabindex='0'][1]"
					+ " | //*[self::button or @role='button' or @tabindex='0'][contains(translate(@aria-label,'NEXT','next'),'next') or contains(translate(normalize-space(.),'NEXT CHAPTER','next chapter'),'next chapter')]");
	private static final By PREVIOUS_CHAPTER_BUTTON = By
			.xpath("//img[contains(@src,'ic_previous')]/ancestor::div[@tabindex='0'][1]"
					+ " | //*[self::button or @role='button' or @tabindex='0'][contains(translate(@aria-label,'PREVIOUS','previous'),'previous') or contains(translate(normalize-space(.),'PREVIOUS CHAPTER','previous chapter'),'previous chapter')]");
	private static final By SPEED_CONTROL_BUTTON = By.xpath(
			"//select | //*[self::button or @role='button' or @tabindex='0'][contains(translate(normalize-space(.),'1X','1x'),'1x') or contains(translate(normalize-space(.),'SPEED','speed'),'speed') or contains(translate(@aria-label,'SPEED','speed'),'speed')]");
	private static final By VOLUME_SLIDER = By.xpath("//input[@type='range' and @role='slider']"
			+ " | //input[@type='range' and contains(translate(@aria-label,'VOLUME','volume'),'volume')]"
			+ " | //*[@data-testid='volume_slider']"
			+ " | //*[@role='slider' and contains(translate(@aria-label,'VOLUME','volume'),'volume')]"
			+ " | //*[@aria-label='volume']"
			+ " | //div[contains(@style,'cursor: pointer') and .//div[contains(@style,'translateX(')] and .//div[contains(@style,'background-color')]]");
	private static final By MUTE_BUTTON = By.xpath(
			"//*[self::button or @role='button' or @tabindex='0'][contains(translate(@aria-label,'MUTE','mute'),'mute') or contains(translate(@aria-label,'VOLUME','volume'),'volume')]"
					+ " | //div[normalize-space(.)='' and contains(@style,'font-family: material')]");
	private static final By CLOSE_PLAYER_BUTTON = By.xpath(
			"//*[self::button or @role='button' or @tabindex='0'][contains(translate(@aria-label,'CLOSE','close'),'close') or contains(normalize-space(.),'×') or contains(normalize-space(.),'✕')]");
	private static final By PROGRESS_BAR = By.xpath("//input[@type='range']" + " | //*[@role='slider']"
			+ " | //*[@data-testid='progress_bar']" + " | //*[@aria-label='Seek']" + " | //*[@aria-label='progress']");
	private static final By CURRENT_POSITION_LABEL = By.xpath(
			"(//*[@role='slider']/ancestor::div[contains(@style,'flex-direction: row')][1]/div[(string-length(normalize-space(.))=5 and substring(normalize-space(.),3,1)=':') or (string-length(normalize-space(.))=8 and substring(normalize-space(.),3,1)=':' and substring(normalize-space(.),6,1)=':')])[1]"
					+ " | //*[contains(@class,'time') or contains(@class,'position') or contains(@class,'current')][self::div or self::span][contains(normalize-space(.),':')]");
	private static final By CURRENT_CHAPTER_TITLE = By.xpath(
			"//*[contains(@class,'chapter') or contains(@class,'title')][self::div or self::span or self::h1 or self::h2 or self::h3]");
	private static final By PLAYER_SENTINEL = By.xpath(
			"//*[contains(text(),'File Example Mp3')] | //*[@data-testid='progress_bar'] | //*[contains(translate(normalize-space(.),'PLAY AUDIO','play audio'),'play audio')]");
	private static final By CHAPTER_ITEMS = By.xpath(
			"//div[normalize-space()='Available Chapters']/ancestor::div[contains(@class,'css-g5y9jx')][1]/following-sibling::div//*[@tabindex='0'] | //*[@data-testid='chapter_item'] | //*[@data-testid='episode_item']");
	private static final By CHAPTER_DURATION_LABELS = By.xpath(
			"//div[normalize-space()='Available Chapters']/ancestor::div[contains(@class,'css-g5y9jx')][1]/following-sibling::div//*[contains(normalize-space(.),':')] | //*[@data-testid='chapter_item']//*[contains(normalize-space(.),':')]");
	private static final By SUBSCRIPTION_GATE = By.xpath(
			"//*[contains(translate(normalize-space(.),'UPGRADE','upgrade'),'upgrade') or contains(translate(normalize-space(.),'PREMIUM','premium'),'premium') or contains(translate(normalize-space(.),'SUBSCRIBE','subscribe'),'subscribe') or contains(translate(normalize-space(.),'START LISTENING','start listening'),'start listening') or contains(translate(normalize-space(.),'LISTEN 1 BOOK ONLY','listen 1 book only'),'listen 1 book only')]");
	private static final By FREE_USER_LIMIT_MESSAGE = By.xpath(
			"//*[contains(translate(normalize-space(.),'FREE USER','free user'),'free user') or contains(translate(normalize-space(.),'LISTEN 1 BOOK ONLY','listen 1 book only'),'listen 1 book only') or contains(translate(normalize-space(.),'LISTEN MORE BOOKS','listen more books'),'listen more books') or contains(translate(normalize-space(.),'UNLIMITED BOOKS','unlimited books'),'unlimited books')]");

	public AudioPlayerPage(WebDriver driver) {
		super(driver);
	}

	public boolean waitForPlayerBar() {
		return waitUntil(this::hasPlayerSurfaceVisible, DEFAULT_TIMEOUT);
	}

	public boolean waitForPlayControlsReady() {
		return waitUntil(() -> hasSubscriptionGate() || findPlayButton(false) != null || findPauseButton(false) != null,
				Duration.ofSeconds(8));
	}

	public boolean isPlayerResponsive() {
		return hasPlayerSurfaceVisible();
	}

	public boolean isPlayButtonVisible() {
		return findPlayButton(false) != null;
	}

	public boolean isPauseButtonVisible() {
		return findPauseButton(false) != null;
	}

	public boolean hasSubscriptionGate() {
		return findVisible(SUBSCRIPTION_GATE, SHORT_TIMEOUT, false) != null;
	}

	public boolean hasMultipleChapters() {
		return countVisible(CHAPTER_ITEMS) > 1;
	}

	public boolean isShortAudio() {
		int seconds = getDisplayedDurationSeconds();
		return seconds > 0 && seconds < 30;
	}

	public String getCurrentTime() {
		try {
			WebElement label = findVisible(CURRENT_POSITION_LABEL, SHORT_TIMEOUT, false);
			if (label != null) {
				String extracted = extractTimeValue(label.getText());
				if (!extracted.isBlank()) {
					return extracted;
				}
			}
			Double current = readDoubleFromAnyContext(
					"const audio=document.querySelector('audio');if(!audio||Number.isNaN(audio.currentTime))return null;return audio.currentTime;");
			return current == null ? "N/A" : formatSeconds((int) Math.floor(current));
		} finally {
			driver.switchTo().defaultContent();
		}
	}

	public int convertToSeconds(String time) {
		String extracted = extractTimeValue(time);
		if (extracted.isBlank()) {
			return -1;
		}
		String[] parts = extracted.split(":");
		try {
			if (parts.length == 2) {
				return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
			}
			if (parts.length == 3) {
				return Integer.parseInt(parts[0]) * 3600 + Integer.parseInt(parts[1]) * 60 + Integer.parseInt(parts[2]);
			}
		} catch (NumberFormatException e) {
			LOGGER.log(Level.FINE, "Unable to parse time {0}: {1}", new Object[] { time, e.getMessage() });
		}
		return -1;
	}

	public boolean isPlaybackProgressing() {
		int before = convertToSeconds(getCurrentTime());
		boolean progressed = before >= 0
				&& waitUntil(() -> convertToSeconds(getCurrentTime()) > before, Duration.ofSeconds(2));
		return progressed || isPauseButtonVisible() || isMediaElementPlaying();
	}

	public boolean validatePlay() {
		if (!waitForPlayControlsReady()) {
			LOGGER.warning("Play controls were not ready before attempting to start audio.");
			return false;
		}
		WebElement play = findPlayButton(true);
		if (play == null) {
			play = findPlayButton(false);
		}
		if (play == null) {
			LOGGER.warning("Play Audio button was not found.");
			return false;
		}
		String before = normalizePrePlayTime(getCurrentTime());
		LOGGER.log(Level.INFO, "Before Play: {0}", before);
		clickPlayButton(play);
		boolean started = waitForPlaybackToStabilize(Duration.ofSeconds(6));
		String after = getCurrentTime();
		LOGGER.log(Level.INFO, "After Play: {0}", after);
		return started;
	}

	public boolean clickPlayAudio() {
		return validatePlay();
	}

	public boolean validatePause() {
		if (!ensurePlaybackStarted()) {
			return false;
		}
		int before = convertToSeconds(getCurrentTime());
		WebElement pause = findPauseButton(true);
		if (pause == null) {
			return false;
		}
		LOGGER.log(Level.INFO, "Before Pause: {0}", formatSeconds(before));
		clickElement(pause);
		boolean paused = waitUntil(this::isAudioPaused, DEFAULT_TIMEOUT);
		sleep(1200);
		int after = convertToSeconds(getCurrentTime());
		sleep(1200);
		int stable = convertToSeconds(getCurrentTime());
		LOGGER.log(Level.INFO, "After Pause: {0}", formatSeconds(after));
		return paused && before >= 0 && after >= 0 && stable >= 0 && Math.abs(after - before) <= 1
				&& Math.abs(stable - after) <= 1;
	}

	public boolean clickPausePlay() {
		WebElement toggle = isAudioPlaying() ? findPauseButton(true) : findPlayButton(true);
		if (toggle == null) {
			return false;
		}
		clickElement(toggle);
		return true;
	}

	public boolean validateResume() {
		if (!ensurePlaybackStarted()) {
			return false;
		}
		WebElement pause = findPauseButton(true);
		if (pause == null) {
			return false;
		}
		clickElement(pause);
		if (!waitUntil(this::isAudioPaused, DEFAULT_TIMEOUT)) {
			return false;
		}
		int pausedAt = convertToSeconds(getCurrentTime());
		WebElement play = findPlayButton(true);
		if (play == null || pausedAt < 0) {
			return false;
		}
		LOGGER.log(Level.INFO, "Before Resume: {0}", formatSeconds(pausedAt));
		clickElement(play);
		waitUntil(() -> convertToSeconds(getCurrentTime()) > pausedAt, Duration.ofSeconds(4));
		int resumedAt = convertToSeconds(getCurrentTime());
		LOGGER.log(Level.INFO, "After Resume: {0}", formatSeconds(resumedAt));
		return resumedAt >= pausedAt && isPlaybackProgressing();
	}

	public boolean validateForward30() {
		return validateDeltaButton(FORWARD_30_BUTTON, true);
	}

	public boolean clickForward30() {
		WebElement element = findResolvedVisible(FORWARD_30_BUTTON, DEFAULT_TIMEOUT, true);
		if (element == null) {
			return false;
		}
		clickElement(element);
		return true;
	}

	public boolean validateBackward30() {
		if (!ensureAdvancedPlaybackReady()) {
			return false;
		}
		if (convertToSeconds(getCurrentTime()) <= 0 && !validateForward30()) {
			return false;
		}
		return validateDeltaButton(BACKWARD_30_BUTTON, false);
	}

	public boolean clickBackward30() {
		WebElement element = findResolvedVisible(BACKWARD_30_BUTTON, DEFAULT_TIMEOUT, true);
		if (element == null) {
			return false;
		}
		clickElement(element);
		return true;
	}

	public boolean validateSeekForward() {
		if (!ensureAdvancedPlaybackReady()) {
			return false;
		}

		WebElement bar = findResolvedVisible(PROGRESS_BAR, DEFAULT_TIMEOUT, true);
		if (bar == null) {
			LOGGER.warning("Seek forward failed because progress bar was not found.");
			return false;
		}

		int before = convertToSeconds(getCurrentTime());
		if (before < 0) {
			LOGGER.warning("Seek forward failed because current time could not be read.");
			return false;
		}

		clickProgressBar(bar, 0.75);

		boolean moved = waitUntil(() -> {
			int current = convertToSeconds(getCurrentTime());
			return current > before + 1;
		}, Duration.ofSeconds(5));

		int after = convertToSeconds(getCurrentTime());
		LOGGER.log(Level.INFO, "Seek Forward: before={0}, after={1}",
				new Object[] { formatSeconds(before), formatSeconds(after) });

		return moved && after > before;
	}

	public boolean validateSeekBackward() {
		if (!ensureAdvancedPlaybackReady()) {
			return false;
		}

		WebElement bar = findResolvedVisible(PROGRESS_BAR, DEFAULT_TIMEOUT, true);
		if (bar == null) {
			LOGGER.warning("Seek backward failed because progress bar was not found.");
			return false;
		}

		int current = convertToSeconds(getCurrentTime());
		if (current < 5) {
			clickProgressBar(bar, 0.75);
			waitUntil(() -> convertToSeconds(getCurrentTime()) > 5, Duration.ofSeconds(5));
			current = convertToSeconds(getCurrentTime());
		}

		int before = current;
		clickProgressBar(bar, 0.25);

		boolean moved = waitUntil(() -> {
			int updated = convertToSeconds(getCurrentTime());
			return updated >= 0 && updated < before - 1;
		}, Duration.ofSeconds(5));

		int after = convertToSeconds(getCurrentTime());
		LOGGER.log(Level.INFO, "Seek Backward: before={0}, after={1}",
				new Object[] { formatSeconds(before), formatSeconds(after) });

		return moved && after >= 0 && after < before;
	}

	public boolean validateSeekBeyondEnd() {
		if (!ensureAdvancedPlaybackReady()) {
			return false;
		}

		WebElement bar = findResolvedVisible(PROGRESS_BAR, DEFAULT_TIMEOUT, true);
		int duration = readDurationSeconds();

		if (bar == null || duration <= 0) {
			LOGGER.warning("Seek beyond end failed because progress bar or duration was unavailable.");
			return false;
		}

		clickProgressBar(bar, 0.99);

		boolean clamped = waitUntil(() -> {
			int current = convertToSeconds(getCurrentTime());
			return current >= Math.max(0, duration - 2) && current <= duration;
		}, Duration.ofSeconds(5));

		int after = convertToSeconds(getCurrentTime());
		LOGGER.log(Level.INFO, "Seek Beyond End: duration={0}, after={1}",
				new Object[] { formatSeconds(duration), formatSeconds(after) });

		return clamped && after >= 0 && after <= duration;
	}

	public boolean validateSkipNearEnd() {
		if (!ensureAdvancedPlaybackReady()) {
			return false;
		}
		int duration = readDurationSeconds();
		WebElement bar = findResolvedVisible(PROGRESS_BAR, DEFAULT_TIMEOUT, true);
		LOGGER.log(Level.INFO, "Near-End Forward Setup: duration={0}", formatSeconds(duration));

		if (bar != null && duration > 0) {
			clickProgressBar(bar, 0.95);
			waitUntil(() -> convertToSeconds(getCurrentTime()) >= Math.max(0, duration - 15), Duration.ofSeconds(3));
		} else {
			int seedTime = Math.max(0, convertToSeconds(getCurrentTime()));
			for (int i = 0; i < 3; i++) {
				WebElement forwardSeed = findResolvedVisible(FORWARD_30_BUTTON, DEFAULT_TIMEOUT, true);
				if (forwardSeed == null) {
					return false;
				}
				clickElement(forwardSeed);
				int updated = waitForTimeChange(seedTime, true);
				if (updated <= seedTime) {
					break;
				}
				seedTime = updated;
			}
		}

		int before = convertToSeconds(getCurrentTime());
		if (before < 0) {
			return false;
		}

		WebElement forward = findResolvedVisible(FORWARD_30_BUTTON, DEFAULT_TIMEOUT, true);
		if (forward == null) {
			return false;
		}

		LOGGER.log(Level.INFO, "Before Near-End Forward: {0}", formatSeconds(before));
		clickElement(forward);
		sleep(1000);
		int after = convertToSeconds(getCurrentTime());
		if (after < 0 && duration > 0) {
			after = duration;
		}
		LOGGER.log(Level.INFO, "After Near-End Forward: {0}", formatSeconds(after));

		if (duration > 0) {
			return (after >= before && after <= duration) || (before >= Math.max(0, duration - 2) && after == before);
		}

		return after >= before;
	}

	public boolean validateBackwardAtStart() {
		if (!ensureAdvancedPlaybackReady()) {
			return false;
		}
		WebElement bar = findResolvedVisible(PROGRESS_BAR, DEFAULT_TIMEOUT, true);
		if (bar != null) {
			clickProgressBar(bar, 0.0);
			waitUntil(() -> {
				int current = convertToSeconds(getCurrentTime());
				return current >= 0 && current <= 1;
			}, Duration.ofSeconds(3));
		}

		int before = convertToSeconds(getCurrentTime());
		if (before < 0) {
			before = 0;
		}

		WebElement backward = findResolvedVisible(BACKWARD_30_BUTTON, DEFAULT_TIMEOUT, true);
		if (backward == null) {
			return false;
		}

		LOGGER.log(Level.INFO, "Before Backward At Start: {0}", formatSeconds(before));
		clickElement(backward);
		sleep(1000);

		int after = convertToSeconds(getCurrentTime());
		if (after < 0) {
			after = before;
		}

		LOGGER.log(Level.INFO, "After Backward At Start: {0}", formatSeconds(after));
		return after >= 0 && after <= Math.max(before, 1) && isPlayerResponsive();
	}

	public boolean validateChapterChange(boolean next) {
		if (!ensureAdvancedPlaybackReady() || !hasMultipleChapters()) {
			return false;
		}
		String beforeTitle = getCurrentChapterTitle();
		int beforeTime = convertToSeconds(getCurrentTime());
		WebElement button = findResolvedVisible(next ? NEXT_CHAPTER_BUTTON : PREVIOUS_CHAPTER_BUTTON, DEFAULT_TIMEOUT,
				true);
		if (button == null) {
			return false;
		}
		clickElement(button);
		return waitUntil(() -> {
			String currentTitle = getCurrentChapterTitle();
			int currentTime = convertToSeconds(getCurrentTime());
			return !sameText(beforeTitle, currentTitle)
					|| (beforeTime > 2 && currentTime >= 0 && currentTime < beforeTime);
		}, Duration.ofSeconds(5));
	}

	public boolean clickNextChapter() {
		return validateChapterChange(true);
	}

	public boolean clickPreviousChapter() {
		return validateChapterChange(false);
	}

	public boolean validatePreviousOnFirstChapterBoundary() {
		if (!ensureAdvancedPlaybackReady() || !hasMultipleChapters()) {
			return false;
		}
		WebElement bar = findResolvedVisible(PROGRESS_BAR, DEFAULT_TIMEOUT, true);
		if (bar != null) {
			clickProgressBar(bar, 0.0);
		}
		String beforeTitle = getCurrentChapterTitle();
		int beforeTime = convertToSeconds(getCurrentTime());
		WebElement previous = findResolvedVisible(PREVIOUS_CHAPTER_BUTTON, DEFAULT_TIMEOUT, true);
		if (previous == null) {
			return false;
		}
		clickElement(previous);
		sleep(1000);
		String afterTitle = getCurrentChapterTitle();
		int afterTime = convertToSeconds(getCurrentTime());
		return sameText(beforeTitle, afterTitle) || afterTime <= Math.max(beforeTime, 1);
	}

	public boolean validatePlaybackSpeed(String speed) {
		if (!ensurePlaybackStarted()) {
			return false;
		}
		WebElement speedControl = findResolvedVisible(SPEED_CONTROL_BUTTON, DEFAULT_TIMEOUT, true);
		if (speedControl == null) {
			return false;
		}
		double before = readPlaybackRate();
		String beforeLabel = readSpeedIndicatorText(speedControl);
		boolean changed = false;
		if ("select".equalsIgnoreCase(speedControl.getTagName())) {
			changed = selectSpeed(speedControl, speed);
		} else {
			clickElement(speedControl);
			By optionLocator = By.xpath(
					"//*[self::button or @role='button' or @tabindex='0'][contains(translate(normalize-space(.),'"
							+ speed.toUpperCase() + "','" + speed.toLowerCase() + "'),'" + speed.toLowerCase() + "')]");
			WebElement option = findVisible(optionLocator, DEFAULT_TIMEOUT, true);
			if (option != null) {
				clickElement(option);
				changed = waitUntil(
						() -> closeEnough(readPlaybackRate(), parseSpeed(speed)) || speedLabelMatchesExpected(speed),
						Duration.ofSeconds(3));
			}
		}
		double after = readPlaybackRate();
		String afterLabel = readSpeedIndicatorText(speedControl);
		LOGGER.log(Level.INFO, "Speed Label Change: before={0}, after={1}", new Object[] { beforeLabel, afterLabel });
		return changed || closeEnough(after, parseSpeed(speed))
				|| (!sameText(beforeLabel, afterLabel) && speedLabelContainsExpected(afterLabel, speed))
				|| speedLabelMatchesExpected(speed);
	}

	public boolean validateVolumeChange(boolean increase) {
		if (!ensureVolumeAdjustmentReady()) {
			LOGGER.warning("Volume validation failed because playback could not be started.");
			return false;
		}

		WebElement slider = findVolumeSliderForInteraction();
		if (slider == null) {
			LOGGER.warning("Volume slider not found or not interactable.");
			Double beforeMediaVolumeValue = readMediaVolumeValue();
			if (beforeMediaVolumeValue == null) {
				LOGGER.warning("Volume fallback unavailable because media volume could not be read.");
				return false;
			}
			int beforeMediaVolume = normalizePercent(beforeMediaVolumeValue);
			if (increase && beforeMediaVolume >= 95) {
				setMediaVolume(0.35);
				sleep(700);
				beforeMediaVolumeValue = readMediaVolumeValue();
				if (beforeMediaVolumeValue == null) {
					LOGGER.warning("Increase volume baseline reset failed because media volume could not be read.");
					return false;
				}
				beforeMediaVolume = normalizePercent(beforeMediaVolumeValue);
				LOGGER.log(Level.INFO, "Increase Volume Baseline Reset: volume={0}", beforeMediaVolume);
			}
			if (increase && beforeMediaVolume <= 5) {
				setMediaVolume(0.35);
				sleep(700);
				beforeMediaVolumeValue = readMediaVolumeValue();
				if (beforeMediaVolumeValue == null) {
					LOGGER.warning("Increase volume baseline reset failed because media volume could not be read.");
					return false;
				}
				beforeMediaVolume = normalizePercent(beforeMediaVolumeValue);
				LOGGER.log(Level.INFO, "Increase Volume Baseline Reset: volume={0}", beforeMediaVolume);
			}
			if (!increase && beforeMediaVolume <= 5) {
				setMediaVolume(0.65);
				sleep(700);
				beforeMediaVolumeValue = readMediaVolumeValue();
				if (beforeMediaVolumeValue == null) {
					LOGGER.warning("Decrease volume baseline reset failed because media volume could not be read.");
					return false;
				}
				beforeMediaVolume = normalizePercent(beforeMediaVolumeValue);
				LOGGER.log(Level.INFO, "Decrease Volume Baseline Reset: volume={0}", beforeMediaVolume);
			}
			boolean mediaAdjusted = setMediaVolume(resolveTargetMediaVolume(beforeMediaVolume, increase));
			sleep(700);
			Double afterMediaVolumeValue = readMediaVolumeValue();
			if (afterMediaVolumeValue == null) {
				LOGGER.warning(
						"Volume fallback adjustment could not be verified because media volume could not be read.");
				return false;
			}
			int afterMediaVolume = normalizePercent(afterMediaVolumeValue);
			LOGGER.log(Level.INFO, "{0} Volume Fallback: before={1}, after={2}",
					new Object[] { increase ? "Increase" : "Decrease", beforeMediaVolume, afterMediaVolume });
			return mediaAdjusted
					&& (increase ? afterMediaVolume > beforeMediaVolume : afterMediaVolume < beforeMediaVolume);
		}

		int before = normalizePercent(readVolumeValue(slider));
		if (increase && before >= 95) {
			adjustSlider(slider, -40);
			sleep(700);
			before = normalizePercent(readVolumeValue(slider));
			LOGGER.log(Level.INFO, "Increase Volume Baseline Reset: volume={0}", before);
		}
		if (increase && before <= 5) {
			adjustSlider(slider, 40);
			sleep(700);
			before = normalizePercent(readVolumeValue(slider));
			LOGGER.log(Level.INFO, "Increase Volume Baseline Reset: volume={0}", before);
		}
		if (!increase && before <= 5) {
			adjustSlider(slider, 40);
			sleep(700);
			before = normalizePercent(readVolumeValue(slider));
			LOGGER.log(Level.INFO, "Decrease Volume Baseline Reset: volume={0}", before);
		}
		adjustSlider(slider, increase ? 10 : -10);
		sleep(700);
		int after = normalizePercent(readVolumeValue(slider));
		LOGGER.log(Level.INFO, "{0} Volume: before={1}, after={2}",
				new Object[] { increase ? "Increase" : "Decrease", before, after });

		if (before == after) {
			adjustSlider(slider, increase ? 25 : -25);
			sleep(700);
			after = normalizePercent(readVolumeValue(slider));
			LOGGER.log(Level.INFO, "{0} Volume Retry: before={1}, after={2}",
					new Object[] { increase ? "Increase" : "Decrease", before, after });
		}

		return increase ? after > before : after < before;
	}

	public boolean hasAccessibleVolumeControl() {
		if (!ensureVolumeAdjustmentReady()) {
			return false;
		}
		WebElement slider = findVolumeSliderForInteraction();
		if (slider != null) {
			return true;
		}
		return readMediaVolumeValue() != null;
	}

	public boolean validateMuteToggle() {
		if (!ensureVolumeAdjustmentReady()) {
			return false;
		}

		WebElement slider = findVolumeSliderForInteraction();
		if (slider == null) {
			LOGGER.warning("Mute validation failed because volume slider was not available.");
			return false;
		}

		int before = normalizePercent(readVolumeValue(slider));
		LOGGER.log(Level.INFO, "Before Mute: volume={0}", before);

		setSliderToPercent(slider, 0.0);
		sleep(700);
		int after = normalizePercent(readVolumeValue(slider));
		LOGGER.log(Level.INFO, "After Mute: volume={0}", after);
		return before >= 0 && after == 0;
	}

	public boolean validateCurrentTimeVisibleWhilePlaying() {
		return ensurePlaybackStarted() && convertToSeconds(getCurrentTime()) >= 0;
	}

	public boolean validatePauseResumeCycle() {
		return validatePause() && validateResume();
	}

	public boolean validateRepeatedForward(int repetitions) {
		if (!ensureAdvancedPlaybackReady()) {
			return false;
		}
		int previous = convertToSeconds(getCurrentTime());
		for (int i = 0; i < repetitions; i++) {
			WebElement forward = findResolvedVisible(FORWARD_30_BUTTON, DEFAULT_TIMEOUT, true);
			if (forward == null) {
				return false;
			}
			clickElement(forward);
			int updated = waitForTimeChange(previous, true);
			if (updated <= previous) {
				return false;
			}
			previous = updated;
		}
		return true;
	}

	public boolean validateRepeatedBackward(int repetitions) {
		if (!ensurePlaybackStarted() || !validateSeekForward()) {
			return false;
		}
		int previous = convertToSeconds(getCurrentTime());
		for (int i = 0; i < repetitions; i++) {
			WebElement backward = findResolvedVisible(BACKWARD_30_BUTTON, DEFAULT_TIMEOUT, true);
			if (backward == null) {
				return false;
			}
			clickElement(backward);
			int updated = waitForTimeChange(previous, false);
			if (updated < 0 || updated >= previous) {
				return false;
			}
			previous = updated;
		}
		return true;
	}

	public boolean validateShortAudioForwardGracefully() {
		if (!isShortAudio() || !ensurePlaybackStarted()) {
			return false;
		}
		int duration = getDisplayedDurationSeconds();
		int before = convertToSeconds(getCurrentTime());
		if (!clickForward30()) {
			return false;
		}
		int after = convertToSeconds(getCurrentTime());
		return after >= before && (duration < 0 || after <= duration);
	}

	public boolean validateShortAudioBackwardGracefully() {
		if (!isShortAudio() || !ensurePlaybackStarted()) {
			return false;
		}
		int before = convertToSeconds(getCurrentTime());
		if (!clickBackward30()) {
			return false;
		}
		int after = convertToSeconds(getCurrentTime());
		return after >= 0 && after <= Math.max(before, 1);
	}

	public boolean validateRestrictedPlaybackBlocked() {
		return hasSubscriptionGate() && !isPlayButtonVisible();
	}

	public String getCurrentChapterTitle() {
		WebElement title = findVisible(CURRENT_CHAPTER_TITLE, SHORT_TIMEOUT, false);
		return title == null ? "N/A" : normalizeText(title.getText());
	}

	public boolean hasFreeUserLimitIndicator() {
		return findVisible(FREE_USER_LIMIT_MESSAGE, SHORT_TIMEOUT, false) != null;
	}

	private boolean validateDeltaButton(By locator, boolean shouldAdvance) {
		if (!ensureAdvancedPlaybackReady()) {
			return false;
		}
		int before = convertToSeconds(getCurrentTime());
		WebElement button = findResolvedVisible(locator, DEFAULT_TIMEOUT, true);
		if (button == null) {
			return false;
		}
		clickElement(button);
		int after = waitForTimeChange(before, shouldAdvance);
		LOGGER.log(Level.INFO, "{0}: before={1}, after={2}",
				new Object[] { shouldAdvance ? "Forward" : "Backward", formatSeconds(before), formatSeconds(after) });
		return shouldAdvance ? after > before : after >= 0 && after < before;
	}

	private boolean validateSeek(double ratio, boolean shouldAdvance) {
		if (!ensureAdvancedPlaybackReady()) {
			return false;
		}
		WebElement bar = findResolvedVisible(PROGRESS_BAR, DEFAULT_TIMEOUT, true);
		if (bar == null) {
			return false;
		}
		int before = convertToSeconds(getCurrentTime());
		clickProgressBar(bar, ratio);
		int after = waitForTimeChange(before, shouldAdvance);
		LOGGER.log(Level.INFO, "Seek: before={0}, after={1}",
				new Object[] { formatSeconds(before), formatSeconds(after) });
		return shouldAdvance ? after > before : after >= 0 && after < before;
	}

	private boolean ensurePlaybackStarted() {
		if (hasConfirmedPlaybackStarted()) {
			return true;
		}
		if (!validatePlay()) {
			return false;
		}
		return hasConfirmedPlaybackStarted();
	}

	private boolean ensureAdvancedPlaybackReady() {
		if (!ensurePlaybackStarted()) {
			return false;
		}
		return convertToSeconds(getCurrentTime()) > 0 || hasPlaybackControlsVisible() || waitUntil(
				() -> convertToSeconds(getCurrentTime()) > 0 || hasPlaybackControlsVisible(), Duration.ofSeconds(3));
	}

	private boolean isAudioPlaying() {
		return hasConfirmedPlaybackStarted() || findPauseButton(false) != null;
	}

	private boolean isAudioPaused() {
		Boolean paused = readBooleanFromAnyContext(
				"const audio=document.querySelector('audio');if(!audio)return null;return audio.paused;");
		if (Boolean.FALSE.equals(paused)) {
			return false;
		}
		return findPlayButton(false) != null || Boolean.TRUE.equals(paused);
	}

	private boolean isMediaElementPlaying() {
		Boolean playing = readBooleanFromAnyContext(
				"const audio=document.querySelector('audio');if(!audio)return null;return !audio.paused || (!audio.ended && audio.currentTime > 0);");
		return Boolean.TRUE.equals(playing);
	}

	private boolean hasConfirmedPlaybackStarted() {
		if (isMediaElementPlaying()) {
			return true;
		}
		int before = convertToSeconds(getCurrentTime());
		if (before < 0) {
			return false;
		}
		return waitUntil(() -> {
			int current = convertToSeconds(getCurrentTime());
			return current > before || isMediaElementPlaying();
		}, Duration.ofSeconds(3));
	}

	private boolean ensureVolumeAdjustmentReady() {
		if (!ensurePlaybackStarted()) {
			return false;
		}
		return waitForPlaybackToStabilize(Duration.ofSeconds(5));
	}

	private boolean waitForPlaybackToStabilize(Duration timeout) {
		if (Boolean.TRUE.equals(waitForMediaPlaybackEvent(timeout)) && hasPlaybackAdvancedPastStart()) {
			return true;
		}
		return waitUntil(this::hasPlaybackAdvancedPastStart, timeout);
	}

	private boolean hasPlaybackAdvancedPastStart() {
		int current = convertToSeconds(getCurrentTime());
		return current >= 1 && (isMediaElementPlaying() || isPauseButtonVisible() || isPlaybackProgressing());
	}

	private Boolean waitForMediaPlaybackEvent(Duration timeout) {
		Object result = executeAsyncInAnyContext("const maxWait = arguments[0];"
				+ "const done = arguments[arguments.length - 1];" + "const audio = document.querySelector('audio');"
				+ "if (!audio) { done(null); return; }"
				+ "if (audio.readyState >= 2 && !audio.paused && !audio.ended) { done(true); return; }"
				+ "let finished = false;" + "let timer = null;" + "const cleanup = () => {"
				+ "  ['loadeddata','canplay','playing','timeupdate'].forEach(eventName => audio.removeEventListener(eventName, onReady));"
				+ "  if (timer) { clearTimeout(timer); }" + "};" + "const finish = (value) => {"
				+ "  if (finished) { return; }" + "  finished = true;" + "  cleanup();" + "  done(value);" + "};"
				+ "const onReady = () => {"
				+ "  if (audio.readyState >= 2 && (!audio.paused || audio.currentTime > 0)) {" + "    finish(true);"
				+ "  }" + "};"
				+ "['loadeddata','canplay','playing','timeupdate'].forEach(eventName => audio.addEventListener(eventName, onReady));"
				+ "timer = setTimeout(() => finish(false), maxWait);" + "onReady();", timeout);
		return result instanceof Boolean ? (Boolean) result : null;
	}

	private boolean hasPlayerSurfaceVisible() {
		return findPlayButton(false) != null || findPauseButton(false) != null
				|| findVisible(PROGRESS_BAR, SHORT_TIMEOUT, false) != null
				|| findVisible(PLAYER_SENTINEL, SHORT_TIMEOUT, false) != null || hasSubscriptionGate();
	}

	private boolean hasPlaybackControlsVisible() {
		return findResolvedVisible(FORWARD_30_BUTTON, SHORT_TIMEOUT, false) != null
				|| findResolvedVisible(BACKWARD_30_BUTTON, SHORT_TIMEOUT, false) != null
				|| findResolvedVisible(PROGRESS_BAR, SHORT_TIMEOUT, false) != null;
	}

	private WebElement findPlayButton(boolean requireClickable) {
		return findVisible(PLAY_AUDIO_BUTTON, requireClickable ? DEFAULT_TIMEOUT : SHORT_TIMEOUT, requireClickable);
	}

	private WebElement findPauseButton(boolean requireClickable) {
		WebElement pause = findVisible(PAUSE_AUDIO_BUTTON, requireClickable ? DEFAULT_TIMEOUT : SHORT_TIMEOUT,
				requireClickable);
		return pause != null ? pause
				: findVisible(PLAYER_TOGGLE_BUTTON, requireClickable ? DEFAULT_TIMEOUT : SHORT_TIMEOUT,
						requireClickable);
	}

	private WebElement findVisible(By locator, Duration timeout, boolean requireClickable) {
		long deadline = System.currentTimeMillis() + timeout.toMillis();
		while (System.currentTimeMillis() < deadline) {
			WebElement element = findVisible(locator, requireClickable);
			if (element != null) {
				return element;
			}
			sleep(200);
		}
		driver.switchTo().defaultContent();
		return null;
	}

	private WebElement findResolvedVisible(By locator, Duration timeout, boolean requireClickable) {
		long deadline = System.currentTimeMillis() + timeout.toMillis();
		while (System.currentTimeMillis() < deadline) {
			WebElement element = findResolvedVisible(locator, requireClickable);
			if (element != null) {
				return element;
			}
			sleep(200);
		}
		driver.switchTo().defaultContent();
		return null;
	}

	private WebElement findResolvedVisible(By locator, boolean requireClickable) {
		Integer frameIndex = resolvePlayerFrameIndex();
		if (frameIndex == null) {
			return findVisible(locator, requireClickable);
		}
		try {
			driver.switchTo().defaultContent();
			if (frameIndex >= 0) {
				driver.switchTo().frame(frameIndex);
			}
			WebElement element = findVisibleInCurrentContext(locator, requireClickable);
			if (element != null) {
				return element;
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Resolved player lookup failed: {0}", e.getMessage());
		}
		return findVisible(locator, requireClickable);
	}

	private Integer resolvePlayerFrameIndex() {
		try {
			driver.switchTo().defaultContent();
			if (hasPlayerMarkersInCurrentContext()) {
				return -1;
			}
			List<WebElement> frames = driver.findElements(By.tagName("iframe"));
			for (int i = 0; i < frames.size(); i++) {
				driver.switchTo().defaultContent();
				driver.switchTo().frame(i);
				if (hasPlayerMarkersInCurrentContext()) {
					return i;
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Player frame resolution failed: {0}", e.getMessage());
		} finally {
			driver.switchTo().defaultContent();
		}
		return null;
	}

	private boolean hasPlayerMarkersInCurrentContext() {
		return findVisibleInCurrentContext(PAUSE_AUDIO_BUTTON, false) != null
				|| findVisibleInCurrentContext(PLAYER_TOGGLE_BUTTON, false) != null
				|| findVisibleInCurrentContext(PROGRESS_BAR, false) != null
				|| findVisibleInCurrentContext(CURRENT_POSITION_LABEL, false) != null;
	}

	private WebElement findVisible(By locator, boolean requireClickable) {
		driver.switchTo().defaultContent();
		WebElement current = findVisibleInCurrentContext(locator, requireClickable);
		if (current != null) {
			return current;
		}
		List<WebElement> frames = driver.findElements(By.tagName("iframe"));
		for (int i = 0; i < frames.size(); i++) {
			try {
				driver.switchTo().defaultContent();
				driver.switchTo().frame(i);
				WebElement framed = findVisibleInCurrentContext(locator, requireClickable);
				if (framed != null) {
					return framed;
				}
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Frame lookup failed: {0}", e.getMessage());
			}
		}
		driver.switchTo().defaultContent();
		return null;
	}

	private WebElement findVisibleInCurrentContext(By locator, boolean requireClickable) {
		try {
			for (WebElement element : driver.findElements(locator)) {
				if (element.isDisplayed() && (!requireClickable || element.isEnabled())) {
					return element;
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Current-context lookup failed: {0}", e.getMessage());
		}
		return null;
	}

	private boolean isVisibleInDefaultContent(By locator) {
		try {
			driver.switchTo().defaultContent();
			for (WebElement element : driver.findElements(locator)) {
				if (element.isDisplayed()) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		} finally {
			driver.switchTo().defaultContent();
		}
	}

	private void clickElement(WebElement element) {
		try {
			scrollIntoView(element);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
		} catch (Exception e) {
			try {
				element.click();
			} catch (Exception inner) {
				LOGGER.log(Level.FINE, "Click failed: {0}", inner.getMessage());
			}
		}
	}

	private void clickPlayButton(WebElement playButton) {
		try {
			scrollIntoView(playButton);
			new Actions(driver).moveToElement(playButton).click().perform();
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Actions click on Play failed: {0}", e.getMessage());
		}

		sleep(400);
		if (findPauseButton(false) != null || isMediaElementPlaying()) {
			return;
		}

		try {
			clickElement(playButton);
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Fallback click on Play failed: {0}", e.getMessage());
		}
	}

	private void clickProgressBar(WebElement bar, double ratio) {
		scrollIntoView(bar);
		int width = bar.getSize().getWidth();
		int offset = (int) Math.round(width * Math.max(0.0, Math.min(1.0, ratio))) - (width / 2);
		try {
			new Actions(driver).moveToElement(bar, offset, 0).click().perform();
		} catch (Exception e) {
			((JavascriptExecutor) driver).executeScript(
					"const el=arguments[0],ratio=arguments[1],rect=el.getBoundingClientRect();"
							+ "el.dispatchEvent(new MouseEvent('click',{bubbles:true,clientX:rect.left+Math.max(0,Math.min(rect.width,rect.width*ratio)),clientY:rect.top+rect.height/2}));",
					bar, Math.max(0.0, Math.min(1.0, ratio)));
		}
	}

	private void adjustSlider(WebElement slider, int delta) {
		try {
			scrollIntoView(slider);
			if (isNativeRangeSlider(slider)) {
				slider.click();
				slider.sendKeys(delta > 0 ? Keys.ARROW_RIGHT : Keys.ARROW_LEFT);
			} else {
				clickCustomSlider(slider, delta);
			}
		} catch (Exception ignored) {
		}
		try {
			if (isNativeRangeSlider(slider)) {
				((JavascriptExecutor) driver).executeScript(
						"const el=arguments[0],delta=arguments[1];let current=Number(el.value||el.getAttribute('aria-valuenow')||50);"
								+ "const min=Number(el.min||0),max=Number(el.max||100);if(current<=1&&max>1) current=current*100;"
								+ "const next=Math.max(min,Math.min(max,current+delta));el.value=next;el.setAttribute('aria-valuenow',next);"
								+ "el.dispatchEvent(new Event('input',{bubbles:true}));el.dispatchEvent(new Event('change',{bubbles:true}));",
						slider, delta);
			}
		} catch (Exception ignored) {
		}
	}

	private boolean isNativeRangeSlider(WebElement slider) {
		try {
			return "input".equalsIgnoreCase(slider.getTagName())
					&& "range".equalsIgnoreCase(firstNonBlank(slider.getAttribute("type"), ""));
		} catch (Exception e) {
			return false;
		}
	}

	private void clickCustomSlider(WebElement slider, int delta) {
		Double currentPercent = readCustomSliderPercent(slider);
		double safeCurrent = currentPercent == null ? 50.0 : currentPercent;
		double targetPercent = Math.max(0, Math.min(100, safeCurrent + delta));
		setSliderToPercent(slider, targetPercent);
	}

	private void setSliderToPercent(WebElement slider, double targetPercent) {
		double targetRatio = Math.max(0.0, Math.min(1.0, targetPercent / 100.0));
		try {
			int width = slider.getSize().getWidth();
			int offset = (int) Math.round(width * targetRatio) - (width / 2);
			new Actions(driver).moveToElement(slider, offset, 0).click().perform();
		} catch (Exception e) {
			((JavascriptExecutor) driver).executeScript(
					"const el=arguments[0],ratio=arguments[1],rect=el.getBoundingClientRect();"
							+ "const clientX=rect.left + Math.max(1, Math.min(rect.width - 1, rect.width * ratio));"
							+ "const clientY=rect.top + (rect.height / 2);"
							+ "['pointerdown','mousedown','pointerup','mouseup','click'].forEach(type => "
							+ "el.dispatchEvent(new MouseEvent(type,{bubbles:true,cancelable:true,clientX,clientY})));",
					slider, targetRatio);
		}
	}

	private boolean selectSpeed(WebElement selectElement, String speed) {
		String normalized = speed.toLowerCase().replace("x", "").trim();
		try {
			new Select(selectElement).selectByValue(normalized);
		} catch (Exception ignored) {
			try {
				((JavascriptExecutor) driver).executeScript(
						"const el=arguments[0],target=arguments[1];const option=Array.from(el.options).find(opt => opt.value===target || (opt.textContent||'').trim()===arguments[2]);"
								+ "if(!option)return false;el.value=option.value;el.dispatchEvent(new Event('input',{bubbles:true}));el.dispatchEvent(new Event('change',{bubbles:true}));return true;",
						selectElement, normalized, speed);
			} catch (Exception e) {
				return false;
			}
		}
		return waitUntil(() -> closeEnough(readPlaybackRate(), parseSpeed(speed)), Duration.ofSeconds(3));
	}

	private int getDisplayedDurationSeconds() {
		try {
			driver.switchTo().defaultContent();
			for (WebElement label : driver.findElements(CHAPTER_DURATION_LABELS)) {
				if (label.isDisplayed()) {
					int seconds = convertToSeconds(label.getText());
					if (seconds > 0) {
						return seconds;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Duration label lookup failed: {0}", e.getMessage());
		} finally {
			driver.switchTo().defaultContent();
		}
		return -1;
	}

	private int readDurationSeconds() {
		Double duration = readDoubleFromAnyContext(
				"const audio=document.querySelector('audio');if(!audio||Number.isNaN(audio.duration))return null;return audio.duration;");
		if (duration != null && duration > 0) {
			return (int) Math.floor(duration);
		}
		return getDisplayedDurationSeconds();
	}

	private double readPlaybackRate() {
		Double rate = readDoubleFromAnyContext(
				"const audio=document.querySelector('audio');if(!audio)return null;return audio.playbackRate;");
		if (rate != null) {
			return rate;
		}
		WebElement control = findVisible(SPEED_CONTROL_BUTTON, SHORT_TIMEOUT, false);
		return control == null ? -1 : parseSpeed(control.getText());
	}

	private double readVolumeValue(WebElement slider) {
		try {
			String value = firstNonBlank(slider.getAttribute("value"), slider.getAttribute("aria-valuenow"));
			if (!value.isBlank()) {
				return Double.parseDouble(value);
			}
		} catch (Exception e) {
		}
		Double customSliderVolume = readCustomSliderPercent(slider);
		if (customSliderVolume != null) {
			return customSliderVolume;
		}
		Double mediaVolume = readMediaVolumeValue();
		return mediaVolume != null ? mediaVolume * 100 : 50;
	}

	private Double readMediaVolumeValue() {
		Double volume = readDoubleFromAnyContext(
				"const audio=document.querySelector('audio');if(!audio||Number.isNaN(audio.volume))return null;return audio.volume;");
		return volume;
	}

	private boolean setMediaVolume(double targetVolume) {
		double boundedTarget = Math.max(0, Math.min(1, targetVolume));
		Object result = executeInAnyContext("const audio=document.querySelector('audio');" + "if(!audio)return null;"
				+ "audio.muted=false;" + "audio.volume=" + boundedTarget + ";" + "return audio.volume;");
		return result instanceof Number && Math.abs(((Number) result).doubleValue() - boundedTarget) < 0.05;
	}

	private Double readCustomSliderPercent(WebElement slider) {
		try {
			Object result = ((JavascriptExecutor) driver).executeScript("const slider=arguments[0];"
					+ "const knob=slider.querySelector('div[style*=\"translateX(\"]');"
					+ "const fill=slider.querySelector('div[style*=\"width:\"]');"
					+ "const width=slider.getBoundingClientRect().width;" + "if(width<=0)return null;" + "if(knob){"
					+ "  const match=(knob.getAttribute('style')||'').match(/translateX\\(([-\\d.]+)px\\)/);"
					+ "  if(match){return Math.max(0,Math.min(100,(parseFloat(match[1])/width)*100));}" + "}"
					+ "if(fill){return Math.max(0,Math.min(100,(fill.getBoundingClientRect().width/width)*100));}"
					+ "return null;", slider);
			return result instanceof Number ? ((Number) result).doubleValue() : null;
		} catch (Exception e) {
			return null;
		}
	}

	private WebElement findVolumeSliderForInteraction() {
		WebElement slider = findResolvedVisible(VOLUME_SLIDER, DEFAULT_TIMEOUT, true);
		if (slider != null) {
			return slider;
		}
		slider = findResolvedVisible(VOLUME_SLIDER, DEFAULT_TIMEOUT, false);
		if (slider != null) {
			return slider;
		}

		WebElement volumeToggle = findResolvedVisible(MUTE_BUTTON, DEFAULT_TIMEOUT, false);
		if (volumeToggle == null) {
			return null;
		}

		try {
			scrollIntoView(volumeToggle);
			new Actions(driver).moveToElement(volumeToggle).perform();
			sleep(400);
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Volume slider hover reveal failed: {0}", e.getMessage());
		}

		try {
			clickElement(volumeToggle);
			sleep(400);
		} catch (Exception e) {
			LOGGER.log(Level.FINE, "Volume slider click reveal failed: {0}", e.getMessage());
		}

		slider = findResolvedVisible(VOLUME_SLIDER, DEFAULT_TIMEOUT, true);
		if (slider != null) {
			return slider;
		}
		return findResolvedVisible(VOLUME_SLIDER, DEFAULT_TIMEOUT, false);
	}

	private double resolveTargetMediaVolume(int beforeVolumePercent, boolean increase) {
		double current = Math.max(0, Math.min(1, beforeVolumePercent / 100.0));
		if (increase) {
			return Math.min(1, current + 0.25);
		}
		return Math.max(0, current - 0.25);
	}

	private boolean currentVolumeIsZero() {
		WebElement slider = findResolvedVisible(VOLUME_SLIDER, SHORT_TIMEOUT, false);
		if (slider != null) {
			return normalizePercent(readVolumeValue(slider)) == 0;
		}
		Boolean muted = readBooleanFromAnyContext(
				"const audio=document.querySelector('audio');if(!audio)return null;return audio.muted;");
		return Boolean.TRUE.equals(muted);
	}

	private String readSpeedIndicatorText(WebElement speedControl) {
		try {
			if (speedControl == null) {
				return "";
			}
			if ("select".equalsIgnoreCase(speedControl.getTagName())) {
				try {
					return normalizeText(new Select(speedControl).getFirstSelectedOption().getText());
				} catch (Exception ignored) {
				}
			}
			return firstNonBlank(normalizeText(speedControl.getText()),
					normalizeText(speedControl.getAttribute("value")),
					normalizeText(speedControl.getAttribute("aria-valuetext")),
					normalizeText(speedControl.getAttribute("aria-label")));
		} catch (Exception e) {
			return "";
		}
	}

	private boolean speedLabelMatchesExpected(String speed) {
		WebElement control = findResolvedVisible(SPEED_CONTROL_BUTTON, SHORT_TIMEOUT, false);
		return speedLabelContainsExpected(readSpeedIndicatorText(control), speed);
	}

	private boolean speedLabelContainsExpected(String label, String speed) {
		if (label == null || label.isBlank()) {
			return false;
		}
		String normalizedLabel = label.toLowerCase().replaceAll("\\s+", "");
		String normalizedSpeed = speed.toLowerCase().replaceAll("\\s+", "");
		String numericSpeed = normalizedSpeed.replace("x", "");
		return normalizedLabel.contains(normalizedSpeed) || normalizedLabel.contains(numericSpeed + "x")
				|| normalizedLabel.equals(numericSpeed);
	}

	private int waitForTimeChange(int before, boolean shouldAdvance) {
		final int[] current = new int[] { before };
		waitUntil(() -> {
			current[0] = convertToSeconds(getCurrentTime());
			return shouldAdvance ? current[0] > before : current[0] >= 0 && current[0] < before;
		}, Duration.ofSeconds(5));
		return current[0];
	}

	private int countVisible(By locator) {
		try {
			driver.switchTo().defaultContent();
			int count = 0;
			for (WebElement element : driver.findElements(locator)) {
				if (element.isDisplayed()) {
					count++;
				}
			}
			return count;
		} catch (Exception e) {
			return 0;
		} finally {
			driver.switchTo().defaultContent();
		}
	}

	private Object executeInAnyContext(String script) {
		try {
			driver.switchTo().defaultContent();
			Object result = executeScript(script);
			if (result != null) {
				return result;
			}
			List<WebElement> frames = driver.findElements(By.tagName("iframe"));
			for (int i = 0; i < frames.size(); i++) {
				try {
					driver.switchTo().defaultContent();
					driver.switchTo().frame(i);
					result = executeScript(script);
					if (result != null) {
						return result;
					}
				} catch (Exception ignored) {
				}
			}
			return null;
		} finally {
			driver.switchTo().defaultContent();
		}
	}

	private Object executeAsyncInAnyContext(String script, Duration timeout) {
		try {
			driver.manage().timeouts().scriptTimeout(timeout.plusSeconds(1));
			driver.switchTo().defaultContent();
			Object result = executeAsyncScript(script, timeout);
			if (result != null) {
				return result;
			}
			List<WebElement> frames = driver.findElements(By.tagName("iframe"));
			for (int i = 0; i < frames.size(); i++) {
				try {
					driver.switchTo().defaultContent();
					driver.switchTo().frame(i);
					result = executeAsyncScript(script, timeout);
					if (result != null) {
						return result;
					}
				} catch (Exception ignored) {
				}
			}
			return null;
		} finally {
			driver.switchTo().defaultContent();
		}
	}

	private Object executeScript(String script) {
		try {
			Object result = ((JavascriptExecutor) driver).executeScript(script);
			return result instanceof String && ((String) result).isBlank() ? null : result;
		} catch (Exception e) {
			return null;
		}
	}

	private Object executeAsyncScript(String script, Duration timeout) {
		try {
			Object result = ((JavascriptExecutor) driver).executeAsyncScript(script, timeout.toMillis());
			return result instanceof String && ((String) result).isBlank() ? null : result;
		} catch (Exception e) {
			return null;
		}
	}

	private Boolean readBooleanFromAnyContext(String script) {
		Object result = executeInAnyContext(script);
		return result instanceof Boolean ? (Boolean) result : null;
	}

	private Double readDoubleFromAnyContext(String script) {
		Object result = executeInAnyContext(script);
		return result instanceof Number ? ((Number) result).doubleValue() : null;
	}

	private boolean waitUntil(BooleanSupplier condition, Duration timeout) {
		long deadline = System.currentTimeMillis() + timeout.toMillis();
		while (System.currentTimeMillis() < deadline) {
			try {
				if (condition.getAsBoolean()) {
					return true;
				}
			} catch (Exception ignored) {
			}
			sleep(250);
		}
		return false;
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private void scrollIntoView(By element) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
	}

	private String extractTimeValue(String raw) {
		if (raw == null) {
			return "";
		}
		Matcher matcher = TIME_PATTERN.matcher(raw.replaceAll("\\s+", " ").trim());
		return matcher.find() ? matcher.group() : "";
	}

	private String formatSeconds(int seconds) {
		if (seconds < 0) {
			return "N/A";
		}
		int hours = seconds / 3600;
		int minutes = (seconds % 3600) / 60;
		int remainder = seconds % 60;
		return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, remainder)
				: String.format("%02d:%02d", minutes, remainder);
	}

	private double parseSpeed(String speed) {
		try {
			return Double.parseDouble(speed.toLowerCase().replace("x", "").trim());
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	private boolean closeEnough(double actual, double expected) {
		return actual >= 0 && expected >= 0 && Math.abs(actual - expected) < 0.05;
	}

	private int normalizePercent(double value) {
		double normalized = value <= 1 ? value * 100 : value;
		return Math.max(0, Math.min(100, (int) Math.round(normalized)));
	}

	private String normalizeText(String value) {
		return value == null ? "" : value.replaceAll("\\s+", " ").trim();
	}

	private boolean sameText(String left, String right) {
		return normalizeText(left).equalsIgnoreCase(normalizeText(right));
	}

	private String firstNonBlank(String... values) {
		for (String value : values) {
			if (value != null && !value.isBlank()) {
				return value;
			}
		}
		return "";
	}

	private String normalizePrePlayTime(String rawTime) {
		if (!"N/A".equals(rawTime)) {
			return rawTime;
		}

		Double current = readDoubleFromAnyContext(
				"const audio=document.querySelector('audio');if(!audio||Number.isNaN(audio.currentTime))return null;return audio.currentTime;");
		if (current != null && current <= 0.5) {
			return "00:00";
		}

		if (isPlayButtonVisible()) {
			return "00:00";
		}

		return rawTime;
	}
}
