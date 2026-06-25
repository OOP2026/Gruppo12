package implementazioneDao;

import dao.UtenteDAO;
import model.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class UtentePostgresDao extends AbstractPostgresDao implements UtenteDAO {

    private static final String TABLE_UTENTE = "utente";
    private static final String TABLE_MEDICO = "medico";
    private static final String TABLE_AMMINISTRATORE = "amministratore";

	@Override
	public void insertUtente(Utente utente) {
		String sql = "INSERT INTO " + TABLE_UTENTE + " (login, password) VALUES (?, ?)";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, utente.getLogin());
			statement.setString(2, utente.getPassword());
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile inserire l'utente", exception);
		}
	}

	@Override
	public Utente getUtenteById(String login) {
		String sql = "SELECT login, password FROM " + TABLE_UTENTE + " WHERE login = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, login);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new Utente(resultSet.getString("login"), resultSet.getString("password"));
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere l'utente", exception);
		}
		return null;
	}

	@Override
	public List<Utente> getAllUtenti() {
		List<Utente> utenti = new ArrayList<>();
		String sql = "SELECT login, password FROM " + TABLE_UTENTE + " ORDER BY login";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				utenti.add(new Utente(resultSet.getString("login"), resultSet.getString("password")));
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere gli utenti", exception);
		}
		return utenti;
	}

	@Override
	public void updateUtente(Utente utente) {
		String sql = "UPDATE " + TABLE_UTENTE + " SET password = ? WHERE login = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, utente.getPassword());
			statement.setString(2, utente.getLogin());
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
