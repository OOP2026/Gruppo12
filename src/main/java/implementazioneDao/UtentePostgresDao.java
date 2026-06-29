package implementazioneDao;

import dao.UtenteDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UtentePostgresDao extends AbstractPostgresDao implements UtenteDAO {

    private static final String TABLE_UTENTE = "utente";
	private static final String TABLE_MEDICO = "medico";
	private static final String TABLE_AMMINISTRATORE = "amministratore";

	@Override
	public void insertUtente(Map<String, Object> utente) {
		String sql = "INSERT INTO " + TABLE_UTENTE + " (login, password) VALUES (?, ?)";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, (String) utente.get("login"));
			statement.setString(2, (String) utente.get("password"));
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile inserire l'utente", exception);
		}
	}

	@Override
	public Map<String, Object> getUtenteById(String login) {
		String sql = "SELECT login, password FROM " + TABLE_UTENTE + " WHERE login = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, login);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					Map<String, Object> utente = new HashMap<>();
					utente.put("login", resultSet.getString("login"));
					utente.put("password", resultSet.getString("password"));
					return utente;
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere l'utente", exception);
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getAllUtenti() {
		List<Map<String, Object>> utenti = new ArrayList<>();
		String sql = "SELECT login, password FROM " + TABLE_UTENTE + " ORDER BY login";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				Map<String, Object> utente = new HashMap<>();
				utente.put("login", resultSet.getString("login"));
				utente.put("password", resultSet.getString("password"));
				utenti.add(utente);
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere gli utenti", exception);
		}
		return utenti;
	}

	@Override
	public void updateUtente(Map<String, Object> utente) {
		String sql = "UPDATE " + TABLE_UTENTE + " SET password = ? WHERE login = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, (String) utente.get("password"));
			statement.setString(2, (String) utente.get("login"));
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile aggiornare l'utente", exception);
		}
	}

	@Override
	public void deleteUtente(String login) {
		String deleteMedico = "DELETE FROM " + TABLE_MEDICO + " WHERE login = ?";
		String deleteAmministratore = "DELETE FROM " + TABLE_AMMINISTRATORE + " WHERE login = ?";
		String deleteUtente = "DELETE FROM " + TABLE_UTENTE + " WHERE login = ?";
		try (Connection connection = getConnection(); PreparedStatement medicoStatement = connection.prepareStatement(deleteMedico); PreparedStatement amministratoreStatement = connection.prepareStatement(deleteAmministratore); PreparedStatement utenteStatement = connection.prepareStatement(deleteUtente)) {
			medicoStatement.setString(1, login);
			medicoStatement.executeUpdate();

			amministratoreStatement.setString(1, login);
			amministratoreStatement.executeUpdate();

			utenteStatement.setString(1, login);
			utenteStatement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile eliminare l'utente", exception);
		}
	}
}
