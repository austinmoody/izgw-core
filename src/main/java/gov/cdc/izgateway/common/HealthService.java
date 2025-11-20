package gov.cdc.izgateway.common;

import gov.cdc.izgateway.logging.event.Health;
import gov.cdc.izgateway.logging.markers.Markers2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HealthService {
	private HealthService() {}
	private static Health health = new Health();

   //overloaded setHealthy method  that is invoked when there is an exception
    public static void setHealthy(Throwable ex) {
        health.setLastException(ex);
        setHealthy(false, ex.getMessage());
    }

    public static void setHealthy(boolean b, String message) {
    	health.setHealthy(b);
    	health.setLastChangeReason(message);
        log.info(Markers2.append("health", health), "Server health changed to {}", health.isHealthy() ? "healthy" : "not healthy");
    }

	public static Health getHealth() {
        return health.copy();
    }
    
	public static void incrementVolumes(boolean hasProcessError) {
		health.incrementRequestVolume();
		if (!hasProcessError) {
			health.incrementSuccessVolume();
		}
	}
	public static void setServerName(String serverName) {
		health.setServerName(serverName);
	}
	public static void setBuildName(String build) {
		health.setBuildName(build);
	}

	public static void setDatabase(String url) {
		String sanitizedUrl = sanitizeDatabaseUrl(url);
		if (health.getDatabase() == null) {
			health.setDatabase(sanitizedUrl);
		} else {
			health.setDatabase(health.getDatabase() + ", " + sanitizedUrl);
		}
	}

	/**
	 * Sanitize a database URL by removing embedded credentials.
	 * Handles common JDBC URL formats including MySQL, PostgreSQL, Oracle, SQL Server, etc.
	 *
	 * @param url The database URL that may contain credentials
	 * @return The sanitized URL with credentials removed, or the original if not a recognized format
	 */
	private static String sanitizeDatabaseUrl(String url) {
		if (url == null || url.isEmpty()) {
			return url;
		}

		String sanitized = url;

		// Handle JDBC URLs with format: jdbc:dbtype://credentials@host:port/database
		// Look for the pattern and find the rightmost @ before the host (which shouldn't contain @)
		// This handles usernames and passwords with special chars by matching everything up to the last @
		// before a valid hostname pattern
		if (sanitized.matches("jdbc:[^:]+://.*@.*")) {
			// Match up to and including the last @ that's followed by a hostname pattern
			sanitized = sanitized.replaceFirst(
				"(jdbc:[^:]+://).*@(?=[a-zA-Z0-9])",
				"$1"
			);
		}

		// Handle Oracle thin format: jdbc:oracle:thin:credentials@host:port:SID
		if (sanitized.matches("jdbc:oracle:thin:.*@.*")) {
			sanitized = sanitized.replaceFirst(
				"(jdbc:oracle:thin:).*@(?=[a-zA-Z0-9])",
				"$1"
			);
		}

		// Handle connection string parameters: user=username;password=password or ?user=username&password=password
		// Examples: jdbc:sqlserver://localhost:1433;user=admin;password=secret
		//           jdbc:postgresql://localhost:5432/db?user=admin&password=secret
		sanitized = sanitized.replaceAll(
			"[;&?]user=[^;&]*",
			""
		);
		sanitized = sanitized.replaceAll(
			"[;&?]password=[^;&]*",
			""
		);

		// Clean up any resulting issues with separators
		// If we removed the first query parameter, the next one starts with & instead of ?
		// Convert patterns like "mydb&param" to "mydb?param" (where there's no ? before &)
		if (!sanitized.contains("?") && sanitized.contains("&")) {
			sanitized = sanitized.replaceFirst("&", "?");
		}
		// Fix consecutive separators
		sanitized = sanitized.replaceAll("\\?&", "?");
		sanitized = sanitized.replaceAll("\\?;", "?");
		// Remove double semicolons or ampersands
		sanitized = sanitized.replaceAll("[;&]+", ";");
		// Remove trailing separators
		sanitized = sanitized.replaceAll("[;?&]+$", "");

		return sanitized;
	}
	
	/**
	 * Set the egress DNS address for this server
	 * @param address	The egress DNS address
	 */
	public static void setEgressDnsAddress(String address) {
		health.setEgressDnsAddress(address);
	}
	
	/**
	 * Set the ingress DNS address for this server
	 * @param address	The ingress DNS address
	 */
	public static void setIngressDnsAddress(String[] address) {
		health.setIngressDnsAddress(address);
	}
	
}