package implementazioneDao;

import dao.StanzaDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implementazione PostgreSQL del DAO stanza. */
public class StanzaPostgresDao extends AbstractPostgresDao implements StanzaDAO {

	private static final String TABLE_STANZA = "stanza";

	@Override
	public void insertStanza(Map<String, Object> stanza) {
		String sql = "INSERT INTO " + TABLE_STANZA + " (numero_stanza, reparto_nome) VALUES (?, ?)";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, (Integer) stanza.get("numeroStanza"));
			statement.setString(2, (String) stanza.get("nomeReparto"));
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile inserire la stanza", exception);
		}
	}

	@Override
	public Map<String, Object> getStanzaById(int numeroStanza) {
		String sql = "SELECT numero_stanza, reparto_nome FROM " + TABLE_STANZA + " WHERE numero_stanza = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, numeroStanza);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					Map<String, Object> stanza = new HashMap<>();
					stanza.put("numeroStanza", resultSet.getInt("numero_stanza"));
					stanza.put("nomeReparto", resultSet.getString("reparto_nome"));
					return stanza;
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere la stanza", exception);
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getAllStanze() {
		List<Map<String, Object>> stanze = new ArrayList<>();
		String sql = "SELECT numero_stanza, reparto_nome FROM " + TABLE_STANZA + " ORDER BY numero_stanza";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				Map<String, Object> stanza = new HashMap<>();
				stanza.put("numeroStanza", resultSet.getInt("numero_stanza"));
				stanza.put("nomeReparto", resultSet.getString("reparto_nome"));
				stanze.add(stanza);
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere le stanze", exception);
		}
		return stanze;
	}

	@Override
	public void updateStanza(Map<String, Object> stanza) {
		String sql = "UPDATE " + TABLE_STANZA + " SET reparto_nome = ? WHERE numero_stanza = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, (String) stanza.get("nomeReparto"));
			statement.setInt(2, (Integer) stanza.get("numeroStanza"));
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile aggiornare la stanza", exception);
		}
	}

	@Override
	public void deleteStanza(int numeroStanza) {
		String sql = "DELETE FROM " + TABLE_STANZA + " WHERE numero_stanza = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, numeroStanza);
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile eliminare la stanza", exception);
		}
	}
}
