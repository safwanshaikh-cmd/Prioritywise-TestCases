package constants;

/**
 * Test-method grouping constants. Mirrors the MTNCGModule
 * {@code tests.TestConstants} so {@code @Test} annotations can declare
 * multiple groups with shared labels instead of inline string literals.
 *
 * <p>Used in the {@code groups} array of {@code @Test} annotations, e.g.
 * <pre>
 * &#64;Test(priority = 514, groups = { TestConstants.GROUP_FUNCTIONAL,
 *         TestConstants.GROUP_UI }, description = "TC_514: ...")
 * </pre>
 *
 * <p>Groups can drive TestNG suite filtering via {@code -Dgroups=...} on
 * the surefire command line.
 */
public final class TestConstants {

	private TestConstants() {
		// Prevent instantiation
	}

	// ==================== User / Role Groups ====================
	public static final String GROUP_CONSUMER = "consumer";
	public static final String GROUP_UPLOADER = "uploader";
	public static final String GROUP_ADMIN = "admin";
	public static final String GROUP_SUPER_ADMIN = "superadmin";

	// ==================== Test Type Groups ====================
	public static final String GROUP_SMOKE = "smoke";
	public static final String GROUP_REGRESSION = "regression";
	public static final String GROUP_FUNCTIONAL = "functional";
	public static final String GROUP_E2E = "e2e";
	public static final String GROUP_INTEGRATION = "integration";

	// ==================== Feature Groups ====================
	public static final String GROUP_UI = "ui";
	public static final String GROUP_API = "api";
	public static final String GROUP_SECURITY = "security";
	public static final String GROUP_PERFORMANCE = "performance";
	public static final String GROUP_ACCESSIBILITY = "accessibility";

	// ==================== Test Priority Groups ====================
	public static final String GROUP_P0 = "p0";
	public static final String GROUP_P1 = "p1";
	public static final String GROUP_P2 = "p2";

	// ==================== Test Phase Groups ====================
	public static final String GROUP_CRITICAL = "critical";
	public static final String GROUP_HIGH = "high";
	public static final String GROUP_MEDIUM = "medium";
	public static final String GROUP_LOW = "low";
}