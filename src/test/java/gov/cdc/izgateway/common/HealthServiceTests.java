package gov.cdc.izgateway.common;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for HealthService, focusing on database URL sanitization
 * to ensure credentials are properly removed before storage and exposure.
 */
class HealthServiceTests {

	/**
	 * Helper method to access the private sanitizeDatabaseUrl method via reflection
	 */
	private String sanitizeDatabaseUrl(String url) throws Exception {
		Method method = HealthService.class.getDeclaredMethod("sanitizeDatabaseUrl", String.class);
		method.setAccessible(true);
		return (String) method.invoke(null, url);
	}

	@Test
	void testSanitizeMySqlUrlWithCredentials() throws Exception {
		String input = "jdbc:mysql://username:password@localhost:3306/mydb";
		String expected = "jdbc:mysql://localhost:3306/mydb";
		String actual = sanitizeDatabaseUrl(input);
		assertEquals(expected, actual, "MySQL URL with credentials should be sanitized");
	}

	@Test
	void testSanitizePostgreSqlUrlWithCredentials() throws Exception {
		String input = "jdbc:postgresql://admin:secret123@db.example.com:5432/production";
		String expected = "jdbc:postgresql://db.example.com:5432/production";
		String actual = sanitizeDatabaseUrl(input);
		assertEquals(expected, actual, "PostgreSQL URL with credentials should be sanitized");
	}

	@Test
	void testSanitizeOracleThinUrlWithCredentials() throws Exception {
		String input = "jdbc:oracle:thin:scott/tiger@localhost:1521:ORCL";
		String expected = "jdbc:oracle:thin:localhost:1521:ORCL";
		String actual = sanitizeDatabaseUrl(input);
		assertEquals(expected, actual, "Oracle thin URL with credentials should be sanitized");
	}

	@Test
	void testSanitizeSqlServerUrlWithParameters() throws Exception {
		String input = "jdbc:sqlserver://localhost:1433;databaseName=TestDB;user=admin;password=secret";
		String expected = "jdbc:sqlserver://localhost:1433;databaseName=TestDB";
		String actual = sanitizeDatabaseUrl(input);
		assertEquals(expected, actual, "SQL Server URL with user/password parameters should be sanitized");
	}

	@Test
	void testSanitizeUrlWithQueryParameters() throws Exception {
		String input = "jdbc:postgresql://localhost:5432/mydb?user=admin&password=secret&ssl=true";
		String expected = "jdbc:postgresql://localhost:5432/mydb?ssl=true";
		String actual = sanitizeDatabaseUrl(input);
		assertEquals(expected, actual, "URL with query string parameters should be sanitized");
	}

	@Test
	void testSanitizeUrlWithUsernameOnly() throws Exception {
		String input = "jdbc:mysql://username@localhost:3306/mydb";
		String expected = "jdbc:mysql://localhost:3306/mydb";
		String actual = sanitizeDatabaseUrl(input);
		assertEquals(expected, actual, "URL with username only should be sanitized");
	}

	@Test
	void testSanitizeUrlWithComplexPassword() throws Exception {
		// Test with special characters in password
		String input = "jdbc:mysql://user:p@ss:w0rd!@#$@localhost:3306/mydb";
		String expected = "jdbc:mysql://localhost:3306/mydb";
		String actual = sanitizeDatabaseUrl(input);
		assertEquals(expected, actual, "URL with complex password containing special chars should be sanitized");
	}

	@Test
	void testSanitizeUrlWithoutCredentials() throws Exception {
		String input = "jdbc:mysql://localhost:3306/mydb";
		String expected = "jdbc:mysql://localhost:3306/mydb";
		String actual = sanitizeDatabaseUrl(input);
		assertEquals(expected, actual, "URL without credentials should remain unchanged");
	}

	@Test
	void testSanitizeNullUrl() throws Exception {
		String actual = sanitizeDatabaseUrl(null);
		assertNull(actual, "Null URL should return null");
	}

	@Test
	void testSanitizeEmptyUrl() throws Exception {
		String input = "";
		String expected = "";
		String actual = sanitizeDatabaseUrl(input);
		assertEquals(expected, actual, "Empty URL should return empty string");
	}

	@Test
	void testSanitizeNonJdbcUrl() throws Exception {
		String input = "https://example.com/api";
		String expected = "https://example.com/api";
		String actual = sanitizeDatabaseUrl(input);
		assertEquals(expected, actual, "Non-JDBC URL should remain unchanged");
	}

	@Test
	void testSanitizeH2InMemoryUrl() throws Exception {
		String input = "jdbc:h2:mem:testdb;MODE=MySQL;user=sa;password=";
		String expected = "jdbc:h2:mem:testdb;MODE=MySQL";
		String actual = sanitizeDatabaseUrl(input);
		assertEquals(expected, actual, "H2 in-memory URL with parameters should be sanitized");
	}

	@Test
	void testSetDatabaseStoresSanitizedUrl() {
		// Test that setDatabase actually sanitizes before storing
		String urlWithCredentials = "jdbc:mysql://root:secret@localhost:3306/testdb";

		// Create a fresh health state (this test assumes no other database was set)
		HealthService.setDatabase(urlWithCredentials);

		String stored = HealthService.getHealth().getDatabase();

		assertNotNull(stored, "Database should be stored");
		assertFalse(stored.contains("root"), "Stored URL should not contain username");
		assertFalse(stored.contains("secret"), "Stored URL should not contain password");
		assertTrue(stored.contains("localhost:3306"), "Stored URL should contain host and port");
		assertTrue(stored.contains("testdb"), "Stored URL should contain database name");
	}

	@Test
	void testSetDatabaseWithMultipleUrls() {
		// Reset health state by setting a clean database
		String url1 = "jdbc:mysql://user1:pass1@host1:3306/db1";
		String url2 = "jdbc:postgresql://user2:pass2@host2:5432/db2";

		HealthService.setDatabase(url1);
		HealthService.setDatabase(url2);

		String stored = HealthService.getHealth().getDatabase();

		assertNotNull(stored, "Database should be stored");
		assertFalse(stored.contains("user1") || stored.contains("pass1"),
			"First URL should not contain credentials");
		assertFalse(stored.contains("user2") || stored.contains("pass2"),
			"Second URL should not contain credentials");
		assertTrue(stored.contains("host1:3306") && stored.contains("host2:5432"),
			"Both host:port combinations should be present");
		assertTrue(stored.contains(", "), "Multiple URLs should be comma-separated");
	}
}
