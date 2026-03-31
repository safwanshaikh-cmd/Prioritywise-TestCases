package tests;

import org.testng.annotations.Test;

import utils.ConfigReader;

/**
 * Quick test to verify config.properties is being read correctly
 */
public class ConfigTest {

	@Test
	public void verifyConfigValues() {
		System.out.println("=== Config Reader Test ===");

		System.out.println("Consumer Email: " + ConfigReader.getProperty("consumer.email"));
		System.out.println("Consumer Password: " + ConfigReader.getProperty("consumer.password"));

		System.out.println("Uploader Email: " + ConfigReader.getProperty("uploader.email"));
		System.out.println("Uploader Password: " + ConfigReader.getProperty("uploader.password"));

		System.out.println("Admin Email: " + ConfigReader.getProperty("admin.email"));
		System.out.println("Admin Password: " + ConfigReader.getProperty("admin.password"));

		System.out.println("Default Email: " + ConfigReader.getProperty("login.validEmail"));
		System.out.println("========================");
	}
}
