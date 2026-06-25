package implementazioneDao;

import dao.MalattiaDAO;
import model.Amministratore;
import model.Medico;
import model.Malattia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class MalattiaPostgresDao extends AbstractPostgresDao implements MalattiaDAO {

	private static final String TABLE_MALATTIA = "malattia";

	@Override
	public void insertMalattia(Malattia malattia) {
		String sql = "INSERT INTO " + TABLE_MALATTIA + " (id_malattia, data_inizio, data_fine, matricola_medico, matricola_amministratore) VALUES (?, ?, ?, ?, ?)";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, malattia.getIdMalattia());
			statement.setTimestamp(2, toTimestamp(malattia.getDataInizio()));
			statement.setTimestamp(3, toTimestamp(malattia.getDataFine()));
			if (malattia.getMedicoAssegnato() == null) {
				statement.setString(4, null);
			} else {
				statement.setString(4, malattia.getMedicoAssegnato().getMatricolaMedico());
			}
			if (malattia.getAmministratoreAssegnato() == null) {
				statement.setString(5, null);
			} else {
				statement.setString(5, malattia.getAmministratoreAssegnato().getMatricolaAmministratore());
			}
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile inserire la malattia", exception);
		}
	}

	@Override
	public Malattia getMalattiaById(String idMalattia) {
		String sql = "SELECT id_malattia, data_inizio, data_fine, matricola_medico, matricola_amministratore FROM " + TABLE_MALATTIA + " WHERE id_malattia = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, idMalattia);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					Malattia malattia = new Malattia(resultSet.getString("id_malattia"), toLocalDateTime(resultSet.getTimestamp("data_inizio")), toLocalDateTime(resultSet.getTimestamp("data_fine")));
					String matricolaMedico = resultSet.getString("matricola_medico");
					if (matricolaMedico != null) {
						Medico medico = new MedicoPostgresDao().getMedicoById(matricolaMedico);
						malattia.setMedico(medico);
					}
					String matricolaAmministratore = resultSet.getString("matricola_amministratore");
					if (matricolaAmministratore != null) {
						Amministratore amministratore = new AmministratorePostgresDao().getAmministratoreById(matricolaAmministratore);
						malattia.setAmministratore(amministratore);
					}
					return malattia;
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere la malattia", exception);
		}
		return null;
	}

	@Override
	public List<Malattia> getAllMalattie() {
		List<Malattia> malattie = new ArrayList<>();
		String sql = "SELECT id_malattia, data_inizio, data_fine, matricola_medico, matricola_amministratore FROM " + TABLE_MALATTIA + " ORDER BY id_malattia";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				Malattia malattia = new Malattia(resultSet.getString("id_malattia"), toLocalDateTime(resultSet.getTimestamp("data_inizio")), toLocalDateTime(resultSet.getTimestamp("data_fine")));
				String matricolaMedico = resultSet.getString("matricola_medico");
				if (matricolaMedico != null) {
					malattia.setMedico(new MedicoPostgresDao().getMedicoById(matricolaMedico));
				}
				String matricolaAmministratore = resultSet.getString("matricola_amministratore");
				if (matricolaAmministratore != null) {
					malattia.setAmministratore(new AmministratorePostgresDao().getAmministratoreById(matricolaAmministratore));
				}
				malattie.add(malattia);
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere le malattie", exception);
		}
		return malattie;
	}

	@Override
	public void updateMalattia(Malattia malattia) {
		String sql = "UPDATE " + TABLE_MALATTIA + " SET data_inizio = ?, data_fine = ?, matricola_medico = ?, matricola_amministratore = ? WHERE id_malattia = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setTimestamp(1, toTimestamp(malattia.getDataInizio()));
			statement.setTimestamp(2, toTimestamp(malattia.getDataFine()));
			if (malattia.getMedicoAssegnato() == null) {
				statement.setString(3, null);
			} else {
				statement.setString(3, malattia.getMedicoAssegnato().getMatricolaMedico());
			}
			if (malattia.getAmministratoreAssegnato() == null) {
				statement.setString(4, null);
			} else {
				statement.setString(4, malattia.getAmministratoreAssegnato().getMatricolaAmministratore());
			}
			statement.setString(5, malattia.getIdMalattia());
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