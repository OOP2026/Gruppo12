package database_connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnessioneDatabase {
	private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/postgres";
	private static final String DEFAULT_USERNAME = "postgres";
	private static final String DEFAULT_PASSWORD = "postgres";

	private static final class Holder {
		private static final ConnessioneDatabase INSTANCE = new ConnessioneDatabase();
	}

	private final String url;
	private final String username;
	private final String password;

	private ConnessioneDatabase() {
		this.url = resolveValue("DB_URL", "db.url", DEFAULT_URL);
		this.username = resolveValue("DB_USER", "db.user", DEFAULT_USERNAME);
		this.password = resolveValue("DB_PASSWORD", "db.password", DEFAULT_PASSWORD);
	}

	public static ConnessioneDatabase getInstance() {
		return Holder.INSTANCE;
	}

	public synchronized Connection getConnection() throws SQLException {
		loadDriver();
		return DriverManager.getConnection(url, username, password);
	}

	public synchronized void closeConnection() throws SQLException {
	}

	private void loadDriver() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException exception) {
			throw new IllegalStateException("PostgreSQL driver not found", exception);
		}
	}

	private static String resolveValue(String envName, String propertyName, String defaultValue) {
		String propertyValue = System.getProperty(propertyName);
		if (propertyValue != null && !propertyValue.isBlank()) {
			return propertyValue;
		}

		String envValue = System.getenv(envName);
		if (envValue != null && !envValue.isBlank()) {
			return envValue;
		}

		return defaultValue;
	}
}
