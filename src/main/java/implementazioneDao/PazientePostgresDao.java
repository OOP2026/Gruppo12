package implementazioneDao;

import dao.PazienteDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PazientePostgresDao extends AbstractPostgresDao implements PazienteDAO {

	private static final String TABLE_PAZIENTE = "paziente";

	@Override
	public void insertPaziente(Map<String, Object> paziente) {
		String sql = "INSERT INTO " + TABLE_PAZIENTE + " (matricola_paziente, nome, cognome) VALUES (?, ?, ?)";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, (String) paziente.get("matricolaPaziente"));
			statement.setString(2, (String) paziente.get("nome"));
			statement.setString(3, (String) paziente.get("cognome"));
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile inserire il paziente", exception);
		}
	}

	@Override
	public Map<String, Object> getPazienteById(String matricolaPaziente) {
		String sql = "SELECT matricola_paziente, nome, cognome FROM " + TABLE_PAZIENTE + " WHERE matricola_paziente = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, matricolaPaziente);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					Map<String, Object> paziente = new HashMap<>();
					paziente.put("matricolaPaziente", resultSet.getString("matricola_paziente"));
					paziente.put("nome", resultSet.getString("nome"));
					paziente.put("cognome", resultSet.getString("cognome"));
					return paziente;
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile inserie il paziente", exception);
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getAllPazienti() {
		List<Map<String, Object>> pazienti = new ArrayList<>();
		String sql = "SELECT matricola_paziente, nome, cognome FROM " + TABLE_PAZIENTE + " ORDER BY matricola_paziente";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				Map<String, Object> paziente = new HashMap<>();
				paziente.put("matricolaPaziente", resultSet.getString("matricola_paziente"));
				paziente.put("nome", resultSet.getString("nome"));
				paziente.put("cognome", resultSet.getString("cognome"));
				pazienti.add(paziente);
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere i pazienti", exception);
		}
		return pazienti;
	}

	@Override
	public void updatePaziente(Map<String, Object> paziente) {
		String sql = "UPDATE " + TABLE_PAZIENTE + " SET nome = ?, cognome = ? WHERE matricola_paziente = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, (String) paziente.get("nome"));
			statement.setString(2, (String) paziente.get("cognome"));
			statement.setString(3, (String) paziente.get("matricolaPaziente"));
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile aggiornare il paziente", exception);
		}
	}

	@Override
	public void deletePaziente(String matricolaPaziente) {
		String sql = "DELETE FROM " + TABLE_PAZIENTE + " WHERE matricola_paziente = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, matricolaPaziente);
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile eliminare il paziente", exception);
		}
	}
}
