package implementazioneDao;

import database_connection.ConnessioneDatabase;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

abstract class AbstractPostgresDao {
    protected Connection getConnection() {
        try {
            return ConnessioneDatabase.getInstance().getConnection();
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to access PostgreSQL connection", exception);
        }
    }

    protected Timestamp toTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    protected LocalDateTime toLocalDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }
}