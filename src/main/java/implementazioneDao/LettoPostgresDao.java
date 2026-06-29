package implementazioneDao;

import dao.LettoDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LettoPostgresDao extends AbstractPostgresDao implements LettoDAO {

	private static final String TABLE_LETTO = "letto";

	@Override
	public void insertLetto(Map<String, Object> letto) {
		String sql = "INSERT INTO " + TABLE_LETTO + " (matricola_letto, numero_stanza) VALUES (?, ?)";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, (String) letto.get("matricolaLetto"));
			statement.setObject(2, letto.get("numeroStanza"));
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile inserire il letto", exception);
		}
	}

	@Override
	public Map<String, Object> getLettoById(String matricolaLetto) {
		String sql = "SELECT matricola_letto, numero_stanza FROM " + TABLE_LETTO + " WHERE matricola_letto = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, matricolaLetto);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					Map<String, Object> letto = new HashMap<>();
					letto.put("matricolaLetto", resultSet.getString("matricola_letto"));
					letto.put("numeroStanza", resultSet.getObject("numero_stanza"));
					return letto;
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere il letto", exception);
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getAllLetti() {
		List<Map<String, Object>> letti = new ArrayList<>();
		String sql = "SELECT matricola_letto, numero_stanza FROM " + TABLE_LETTO + " ORDER BY matricola_letto";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				Map<String, Object> letto = new HashMap<>();
				letto.put("matricolaLetto", resultSet.getString("matricola_letto"));
				letto.put("numeroStanza", resultSet.getObject("numero_stanza"));
				letti.add(letto);
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere i letti", exception);
		}
		return letti;
	}

	@Override
	public void updateLetto(Map<String, Object> letto) {
		String sql = "UPDATE " + TABLE_LETTO + " SET numero_stanza = ? WHERE matricola_letto = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setObject(1, letto.get("numeroStanza"));
			statement.setString(2, (String) letto.get("matricolaLetto"));
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile aggiornare il letto", exception);
		}
	}

	@Override
	public void deleteLetto(String matricolaLetto) {
		String sql = "DELETE FROM " + TABLE_LETTO + " WHERE matricola_letto = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, matricolaLetto);
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile eliminare il letto", exception);
		}
	}
}
