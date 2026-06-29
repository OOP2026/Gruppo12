package implementazioneDao;

import dao.MalattiaDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implementazione PostgreSQL del DAO malattia. */
public class MalattiaPostgresDao extends AbstractPostgresDao implements MalattiaDAO {

	private static final String TABLE_MALATTIA = "malattia";

	@Override
	public void insertMalattia(Map<String, Object> malattia) {
		String sql = "INSERT INTO " + TABLE_MALATTIA + " (id_malattia, data_inizio, data_fine, matricola_medico, matricola_amministratore) VALUES (?, ?, ?, ?, ?)";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, (String) malattia.get("idMalattia"));
			statement.setTimestamp(2, toTimestamp((java.time.LocalDateTime) malattia.get("dataInizio")));
			statement.setTimestamp(3, toTimestamp((java.time.LocalDateTime) malattia.get("dataFine")));
			statement.setString(4, (String) malattia.get("matricolaMedico"));
			statement.setString(5, (String) malattia.get("matricolaAmministratore"));
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile inserire la malattia", exception);
		}
	}

	@Override
	public Map<String, Object> getMalattiaById(String idMalattia) {
		String sql = "SELECT id_malattia, data_inizio, data_fine, matricola_medico, matricola_amministratore FROM " + TABLE_MALATTIA + " WHERE id_malattia = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, idMalattia);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					Map<String, Object> malattia = new HashMap<>();
					malattia.put("idMalattia", resultSet.getString("id_malattia"));
					malattia.put("dataInizio", toLocalDateTime(resultSet.getTimestamp("data_inizio")));
					malattia.put("dataFine", toLocalDateTime(resultSet.getTimestamp("data_fine")));
					malattia.put("matricolaMedico", resultSet.getString("matricola_medico"));
					malattia.put("matricolaAmministratore", resultSet.getString("matricola_amministratore"));
					return malattia;
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere la malattia", exception);
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getAllMalattie() {
		List<Map<String, Object>> malattie = new ArrayList<>();
		String sql = "SELECT id_malattia, data_inizio, data_fine, matricola_medico, matricola_amministratore FROM " + TABLE_MALATTIA + " ORDER BY id_malattia";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				Map<String, Object> malattia = new HashMap<>();
				malattia.put("idMalattia", resultSet.getString("id_malattia"));
				malattia.put("dataInizio", toLocalDateTime(resultSet.getTimestamp("data_inizio")));
				malattia.put("dataFine", toLocalDateTime(resultSet.getTimestamp("data_fine")));
				malattia.put("matricolaMedico", resultSet.getString("matricola_medico"));
				malattia.put("matricolaAmministratore", resultSet.getString("matricola_amministratore"));
				malattie.add(malattia);
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere le malattie", exception);
		}
		return malattie;
	}

	@Override
	public void updateMalattia(Map<String, Object> malattia) {
		String sql = "UPDATE " + TABLE_MALATTIA + " SET data_inizio = ?, data_fine = ?, matricola_medico = ?, matricola_amministratore = ? WHERE id_malattia = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setTimestamp(1, toTimestamp((java.time.LocalDateTime) malattia.get("dataInizio")));
			statement.setTimestamp(2, toTimestamp((java.time.LocalDateTime) malattia.get("dataFine")));
			statement.setString(3, (String) malattia.get("matricolaMedico"));
			statement.setString(4, (String) malattia.get("matricolaAmministratore"));
			statement.setString(5, (String) malattia.get("idMalattia"));
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile aggiornare la malattia", exception);
		}
	}

	@Override
	public void deleteMalattia(String idMalattia) {
		String sql = "DELETE FROM " + TABLE_MALATTIA + " WHERE id_malattia = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, idMalattia);
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile eliminare la malattia", exception);
		}
	}
}
