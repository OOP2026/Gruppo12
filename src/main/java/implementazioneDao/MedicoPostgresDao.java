package implementazioneDao;

import dao.MedicoDAO;
import model.Medico;
import model.Reparto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class MedicoPostgresDao extends AbstractPostgresDao implements MedicoDAO {

	private static final String TABLE_UTENTE = "utente";
	private static final String TABLE_MEDICO = "medico";

	@Override
	public void insertMedico(Medico medico) {
		String insertUtente = "INSERT INTO " + TABLE_UTENTE + " (login, password) VALUES (?, ?)";
		String insertMedico = "INSERT INTO " + TABLE_MEDICO + " (login, matricola_medico, reparto_nome) VALUES (?, ?, ?)";
		try (Connection connection = getConnection(); PreparedStatement utenteStatement = connection.prepareStatement(insertUtente); PreparedStatement medicoStatement = connection.prepareStatement(insertMedico)) {
			utenteStatement.setString(1, medico.getLogin());
			utenteStatement.setString(2, medico.getPassword());
			utenteStatement.executeUpdate();

			medicoStatement.setString(1, medico.getLogin());
			medicoStatement.setString(2, medico.getMatricolaMedico());
			if (medico.getReparto() == null) {
				medicoStatement.setString(3, null);
			} else {
				medicoStatement.setString(3, medico.getReparto().getNomeReparto());
			}
			medicoStatement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile inserire il medico", exception);
		}
	}

	@Override
	public Medico getMedicoById(String matricolaMedico) {
		String sql = "SELECT u.login, u.password, m.matricola_medico, m.reparto_nome FROM " + TABLE_MEDICO + " m JOIN " + TABLE_UTENTE + " u ON u.login = m.login WHERE m.matricola_medico = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, matricolaMedico);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					Medico medico = new Medico(resultSet.getString("login"), resultSet.getString("password"), resultSet.getString("matricola_medico"));
					String repartoNome = resultSet.getString("reparto_nome");
					if (repartoNome != null) {
						medico.setReparto(new Reparto(repartoNome));
					}
					return medico;
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere il medico", exception);
		}
		return null;
	}

	@Override
	public List<Medico> getAllMedici() {
		List<Medico> medici = new ArrayList<>();
		String sql = "SELECT u.login, u.password, m.matricola_medico, m.reparto_nome FROM " + TABLE_MEDICO + " m JOIN " + TABLE_UTENTE + " u ON u.login = m.login ORDER BY m.matricola_medico";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				Medico medico = new Medico(resultSet.getString("login"), resultSet.getString("password"), resultSet.getString("matricola_medico"));
				String repartoNome = resultSet.getString("reparto_nome");
				if (repartoNome != null) {
					medico.setReparto(new Reparto(repartoNome));
				}
				medici.add(medico);
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere i medici", exception);
		}
		return medici;
	}

	@Override
	public void updateMedico(Medico medico) {
		String updateUtente = "UPDATE " + TABLE_UTENTE + " SET password = ? WHERE login = ?";
		String updateMedico = "UPDATE " + TABLE_MEDICO + " SET matricola_medico = ?, reparto_nome = ? WHERE login = ?";
		try (Connection connection = getConnection(); PreparedStatement utenteStatement = connection.prepareStatement(updateUtente); PreparedStatement medicoStatement = connection.prepareStatement(updateMedico)) {
			utenteStatement.setString(1, medico.getPassword());
			utenteStatement.setString(2, medico.getLogin());
			utenteStatement.executeUpdate();

			medicoStatement.setString(1, medico.getMatricolaMedico());
			if (medico.getReparto() == null) {
				medicoStatement.setString(2, null);
			} else {
				medicoStatement.setString(2, medico.getReparto().getNomeReparto());
			}
			medicoStatement.setString(3, medico.getLogin());
			medicoStatement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile aggiornare il medico", exception);
		}
	}

	@Override
	public void deleteMedico(String matricolaMedico) {
		String selectLogin = "SELECT login FROM " + TABLE_MEDICO + " WHERE matricola_medico = ?";
		String deleteMedico = "DELETE FROM " + TABLE_MEDICO + " WHERE matricola_medico = ?";
		String deleteUtente = "DELETE FROM " + TABLE_UTENTE + " WHERE login = ?";
		try (Connection connection = getConnection(); PreparedStatement selectStatement = connection.prepareStatement(selectLogin); PreparedStatement deleteMedicoStatement = connection.prepareStatement(deleteMedico)) {
			selectStatement.setString(1, matricolaMedico);
			String login = null;
			try (ResultSet resultSet = selectStatement.executeQuery()) {
				if (resultSet.next()) {
					login = resultSet.getString("login");
				}
			}
			deleteMedicoStatement.setString(1, matricolaMedico);
			deleteMedicoStatement.executeUpdate();

			if (login != null) {
				try (PreparedStatement deleteUtenteStatement = connection.prepareStatement(deleteUtente)) {
					deleteUtenteStatement.setString(1, login);
					deleteUtenteStatement.executeUpdate();
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile eliminare il medico", exception);
		}
	}
}