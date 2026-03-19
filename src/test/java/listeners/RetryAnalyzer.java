package listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

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

		if (count < maxRetry) {
			count++;
			LOGGER.warning("Retrying test: " + result.getMethod().getMethodName() + " | Attempt: " + count);
			return true;
		}

		return false;
	}
}