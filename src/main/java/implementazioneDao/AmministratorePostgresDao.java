package implementazioneDao;

import dao.AmministratoreDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmministratorePostgresDao extends AbstractPostgresDao implements AmministratoreDAO {

	private static final String TABLE_UTENTE = "utente";
	private static final String TABLE_AMMINISTRATORE = "amministratore";

	@Override
	public void insertAmministratore(Map<String, Object> amministratore) {
		String insertUtente = "INSERT INTO " + TABLE_UTENTE + " (login, password) VALUES (?, ?)";
		String insertAmministratore = "INSERT INTO " + TABLE_AMMINISTRATORE + " (login, matricola_amministratore) VALUES (?, ?)";
		try (Connection connection = getConnection(); PreparedStatement utenteStatement = connection.prepareStatement(insertUtente); PreparedStatement amministratoreStatement = connection.prepareStatement(insertAmministratore)) {
			utenteStatement.setString(1, (String) amministratore.get("login"));
			utenteStatement.setString(2, (String) amministratore.get("password"));
			utenteStatement.executeUpdate();

			amministratoreStatement.setString(1, (String) amministratore.get("login"));
			amministratoreStatement.setString(2, (String) amministratore.get("matricolaAmministratore"));
			amministratoreStatement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile inserire l'amministratore", exception);
		}
	}

	@Override
	public Map<String, Object> getAmministratoreById(String matricolaAmministratore) {
		String sql = "SELECT u.login, u.password, a.matricola_amministratore FROM " + TABLE_AMMINISTRATORE + " a JOIN " + TABLE_UTENTE + " u ON u.login = a.login WHERE a.matricola_amministratore = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, matricolaAmministratore);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					Map<String, Object> amministratore = new HashMap<>();
					amministratore.put("login", resultSet.getString("login"));
					amministratore.put("password", resultSet.getString("password"));
					amministratore.put("matricolaAmministratore", resultSet.getString("matricola_amministratore"));
					return amministratore;
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere l'amministratore", exception);
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getAllAmministratori() {
		List<Map<String, Object>> amministratori = new ArrayList<>();
		String sql = "SELECT u.login, u.password, a.matricola_amministratore FROM " + TABLE_AMMINISTRATORE + " a JOIN " + TABLE_UTENTE + " u ON u.login = a.login ORDER BY a.matricola_amministratore";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				Map<String, Object> amministratore = new HashMap<>();
				amministratore.put("login", resultSet.getString("login"));
				amministratore.put("password", resultSet.getString("password"));
				amministratore.put("matricolaAmministratore", resultSet.getString("matricola_amministratore"));
				amministratori.add(amministratore);
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere gli amministratori", exception);
		}
		return amministratori;
	}

	@Override
	public void updateAmministratore(Map<String, Object> amministratore) {
		String updateUtente = "UPDATE " + TABLE_UTENTE + " SET password = ? WHERE login = ?";
		String updateAmministratore = "UPDATE " + TABLE_AMMINISTRATORE + " SET matricola_amministratore = ? WHERE login = ?";
		try (Connection connection = getConnection(); PreparedStatement utenteStatement = connection.prepareStatement(updateUtente); PreparedStatement amministratoreStatement = connection.prepareStatement(updateAmministratore)) {
			utenteStatement.setString(1, (String) amministratore.get("password"));
			utenteStatement.setString(2, (String) amministratore.get("login"));
			utenteStatement.executeUpdate();

			amministratoreStatement.setString(1, (String) amministratore.get("matricolaAmministratore"));
			amministratoreStatement.setString(2, (String) amministratore.get("login"));
			amministratoreStatement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile aggiornare l'amministratore", exception);
		}
	}

	@Override
	public void deleteAmministratore(String matricolaAmministratore) {
		String selectLogin = "SELECT login FROM " + TABLE_AMMINISTRATORE + " WHERE matricola_amministratore = ?";
		String deleteAmministratore = "DELETE FROM " + TABLE_AMMINISTRATORE + " WHERE matricola_amministratore = ?";
		String deleteUtente = "DELETE FROM " + TABLE_UTENTE + " WHERE login = ?";
		try (Connection connection = getConnection(); PreparedStatement selectStatement = connection.prepareStatement(selectLogin); PreparedStatement deleteAdminStatement = connection.prepareStatement(deleteAmministratore)) {
			selectStatement.setString(1, matricolaAmministratore);
			String login = null;
			try (ResultSet resultSet = selectStatement.executeQuery()) {
				if (resultSet.next()) {
					login = resultSet.getString("login");
				}
			}
			deleteAdminStatement.setString(1, matricolaAmministratore);
			deleteAdminStatement.executeUpdate();

			if (login != null) {
				try (PreparedStatement deleteUtenteStatement = connection.prepareStatement(deleteUtente)) {
					deleteUtenteStatement.setString(1, login);
					deleteUtenteStatement.executeUpdate();
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile eliminare l'amministratore", exception);
		}
	}
}
