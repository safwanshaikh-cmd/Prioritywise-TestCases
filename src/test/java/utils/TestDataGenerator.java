package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for generating test data.
 * Handles UUID generation, test file creation, and test data strings.
 */
public class TestDataGenerator {

    private static final Logger LOGGER = Logger.getLogger(TestDataGenerator.class.getName());

    /**
     * Generates a unique test ID using UUID
     * @return unique test ID (8 characters)
     */
    public static String generateTestId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Generates a unique test subject line
     * @param context the context/description of the test
     * @return unique test subject
     */
    public static String generateTestSubject(String context) {
        String testId = generateTestId();
        return "Test Subject - " + context + " [" + testId + "]";
    }

    /**
     * Generates a test message with unique ID
     * @param context the context/description of the test
     * @return unique test message
     */
    public static String generateTestMessage(String context) {
        String testId = generateTestId();
        return "This is a test message for " + context + ". Test ID: " + testId
                + ". This message is being sent to verify Contact Us form functionality.";
    }

    /**
     * Generates a long text string for max length testing
     * @param length the desired length
     * @return string of specified length
     */
    public static String generateLongText(int length) {
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < length; i++) {
            longText.append("A");
        }
        return longText.toString();
    }

    /**
     * Generates special characters string for validation testing
     * @return string with special characters
     */
    public static String generateSpecialCharacters() {
        return "@#$%^&*()_+-=[]{}|;':\",./<>?";
    }

    /**
     * Creates a test file for upload functionality testing
     * @param fileName the desired file name
     * @param content the file content
     * @return absolute path to the created file
     * @throws IOException if file creation fails
     */
    public static String createTestFile(String fileName, String content) throws IOException {
        Path tempDir = Files.createTempDirectory("contact-upload-test-");
        Path testFile = tempDir.resolve(fileName);
        Files.writeString(testFile, content);

        // Mark file for deletion on JVM exit
        testFile.toFile().deleteOnExit();
        tempDir.toFile().deleteOnExit();

        String absolutePath = testFile.toAbsolutePath().toString();
        LOGGER.info("Created test file: " + absolutePath);

        return absolutePath;
    }

    /**
     * Creates a default test file for upload testing
     * @param fileName the desired file name
     * @return absolute path to the created file
     * @throws IOException if file creation fails
     */
    public static String createDefaultTestFile(String fileName) throws IOException {
        String testId = generateTestId();
        String content = "Test file for Contact Us automation testing. Test ID: " + testId + ".";
        return createTestFile(fileName, content);
    }

    /**
     * Checks if a test file exists
     * @param filePath the absolute path to the file
     * @return true if file exists and is readable
     */
    public static boolean testFileExists(String filePath) {
        try {
            java.io.File file = new java.io.File(filePath);
            return file.exists() && file.canRead();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to check if test file exists: {0}", e.getMessage());
            return false;
        }
    }

    /**
     * Gets a unique test identifier that includes timestamp
     * @return unique test identifier
     */
    public static String getUniqueTestIdentifier() {
        return "TEST_" + System.currentTimeMillis() + "_" + generateTestId();
    }

    /**
     * Generates a test subject for network failure testing
     * @return unique test subject for network test
     */
    public static String generateNetworkTestSubject() {
        String testId = generateTestId();
        return "Network Test " + testId;
    }

    /**
     * Generates a test message for network failure testing
     * @return test message for network test
     */
    public static String generateNetworkTestMessage() {
        String testId = generateTestId();
        return "Testing network failure handling. Test ID: " + testId + ".";
    }
}