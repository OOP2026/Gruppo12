package implementazioneDao;

import dao.AmministratoreDAO;
import model.Amministratore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class AmministratorePostgresDao extends AbstractPostgresDao implements AmministratoreDAO {

	private static final String TABLE_UTENTE = "utente";
	private static final String TABLE_AMMINISTRATORE = "amministratore";

	@Override
	public void insertAmministratore(Amministratore amministratore) {
		String insertUtente = "INSERT INTO " + TABLE_UTENTE + " (login, password) VALUES (?, ?)";
		String insertAmministratore = "INSERT INTO " + TABLE_AMMINISTRATORE + " (login, matricola_amministratore) VALUES (?, ?)";
		try (Connection connection = getConnection(); PreparedStatement utenteStatement = connection.prepareStatement(insertUtente); PreparedStatement amministratoreStatement = connection.prepareStatement(insertAmministratore)) {
			utenteStatement.setString(1, amministratore.getLogin());
			utenteStatement.setString(2, amministratore.getPassword());
			utenteStatement.executeUpdate();

			amministratoreStatement.setString(1, amministratore.getLogin());
			amministratoreStatement.setString(2, amministratore.getMatricolaAmministratore());
			amministratoreStatement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to insert amministratore", exception);
		}
	}

	@Override
	public Amministratore getAmministratoreById(String matricolaAmministratore) {
		String sql = "SELECT u.login, u.password, a.matricola_amministratore FROM " + TABLE_AMMINISTRATORE + " a JOIN " + TABLE_UTENTE + " u ON u.login = a.login WHERE a.matricola_amministratore = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, matricolaAmministratore);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new Amministratore(resultSet.getString("login"), resultSet.getString("password"), resultSet.getString("matricola_amministratore"));
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to read amministratore", exception);
		}
		return null;
	}

	@Override
	public List<Amministratore> getAllAmministratori() {
		List<Amministratore> amministratori = new ArrayList<>();
		String sql = "SELECT u.login, u.password, a.matricola_amministratore FROM " + TABLE_AMMINISTRATORE + " a JOIN " + TABLE_UTENTE + " u ON u.login = a.login ORDER BY a.matricola_amministratore";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				amministratori.add(new Amministratore(resultSet.getString("login"), resultSet.getString("password"), resultSet.getString("matricola_amministratore")));
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to read amministratori", exception);
		}
		return amministratori;
	}

	@Override
	public void updateAmministratore(Amministratore amministratore) {
		String updateUtente = "UPDATE " + TABLE_UTENTE + " SET password = ? WHERE login = ?";
		String updateAmministratore = "UPDATE " + TABLE_AMMINISTRATORE + " SET matricola_amministratore = ? WHERE login = ?";
		try (Connection connection = getConnection(); PreparedStatement utenteStatement = connection.prepareStatement(updateUtente); PreparedStatement amministratoreStatement = connection.prepareStatement(updateAmministratore)) {
			utenteStatement.setString(1, amministratore.getPassword());
			utenteStatement.setString(2, amministratore.getLogin());
			utenteStatement.executeUpdate();

			amministratoreStatement.setString(1, amministratore.getMatricolaAmministratore());
			amministratoreStatement.setString(2, amministratore.getLogin());
			amministratoreStatement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to update amministratore", exception);
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
			throw new IllegalStateException("Unable to delete amministratore", exception);
		}
	}
}