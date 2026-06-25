package implementazioneDao;

import dao.PazienteDAO;
import model.Paziente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class PazientePostgresDao extends AbstractPostgresDao implements PazienteDAO {

	private static final String TABLE_PAZIENTE = "paziente";

	@Override
	public void insertPaziente(Paziente paziente) {
		String sql = "INSERT INTO " + TABLE_PAZIENTE + " (matricola_paziente, nome, cognome) VALUES (?, ?, ?)";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, paziente.getMatricolaPaziente());
			statement.setString(2, paziente.getNome());
			statement.setString(3, paziente.getCognome());
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile inserire il paziente", exception);
		}
	}

	@Override
	public Paziente getPazienteById(String matricolaPaziente) {
		String sql = "SELECT matricola_paziente, nome, cognome FROM " + TABLE_PAZIENTE + " WHERE matricola_paziente = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, matricolaPaziente);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new Paziente(resultSet.getString("matricola_paziente"), resultSet.getString("nome"), resultSet.getString("cognome"));
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile inserie il paziente", exception);
		}
		return null;
	}

	@Override
	public List<Paziente> getAllPazienti() {
		List<Paziente> pazienti = new ArrayList<>();
		String sql = "SELECT matricola_paziente, nome, cognome FROM " + TABLE_PAZIENTE + " ORDER BY matricola_paziente";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				pazienti.add(new Paziente(resultSet.getString("matricola_paziente"), resultSet.getString("nome"), resultSet.getString("cognome")));
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere i pazienti", exception);
		}
		return pazienti;
	}

	@Override
	public void updatePaziente(Paziente paziente) {
		String sql = "UPDATE " + TABLE_PAZIENTE + " SET nome = ?, cognome = ? WHERE matricola_paziente = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, paziente.getNome());
			statement.setString(2, paziente.getCognome());
			statement.setString(3, paziente.getMatricolaPaziente());
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