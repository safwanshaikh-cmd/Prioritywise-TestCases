package listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.SkipException;

import java.util.logging.Logger;

/**
 * Retry failed test cases automatically.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

	private static final Logger LOGGER = Logger.getLogger(RetryAnalyzer.class.getName());

	private int count = 0;
	private final int maxRetry = 2;

	@Override
	public boolean retry(ITestResult result) {
		if (containsSkipException(result.getThrowable())) {
			LOGGER.info("Skipping retry for test: " + result.getMethod().getMethodName()
					+ " because it was explicitly skipped.");
			return false;
		}

		if (count < maxRetry) {
			count++;
			LOGGER.warning("Retrying test: " + result.getMethod().getMethodName() + " | Attempt: " + count);
			return true;
		}

		return false;
	}

	private boolean containsSkipException(Throwable throwable) {
		while (throwable != null) {
			if (throwable instanceof SkipException) {
				return true;
			}
			throwable = throwable.getCause();
		}
		return false;
	}
}
