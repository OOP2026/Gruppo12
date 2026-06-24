package implementazioneDao;

import dao.PrestazioneDAO;
import model.Intervento;
import model.Medico;
import model.Ricovero;
import model.Prestazione;
import model.Visita;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class PrestazionePostgresDao extends AbstractPostgresDao implements PrestazioneDAO {

	private static final String TABLE_PRESTAZIONE = "prestazione";
	private static final String TABLE_INTERVENTO = "intervento";
	private static final String TABLE_VISITA = "visita";
	private static final String TABLE_MEDICO_PRESTAZIONE = "medico_prestazione";

	@Override
	public void insertPrestazione(Prestazione prestazione) {
		String type = prestazione.getClass().getSimpleName().toUpperCase();
		String insertPrestazione = "INSERT INTO " + TABLE_PRESTAZIONE + " (num_prestazione, data_inizio, data_fine, esito, tipo, codice_ricovero) VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(insertPrestazione)) {
			statement.setInt(1, prestazione.getNumPrestazione());
			statement.setTimestamp(2, toTimestamp(prestazione.getDataInizio()));
			statement.setTimestamp(3, toTimestamp(prestazione.getDataFine()));
			statement.setString(4, prestazione.getEsito());
			statement.setString(5, type);
			if (prestazione.getRicoveroAssegnato() == null) {
				statement.setString(6, null);
			} else {
				statement.setString(6, prestazione.getRicoveroAssegnato().getCodiceRicovero());
			}
			statement.executeUpdate();

			if (prestazione instanceof Intervento) {
				insertInterventoDetails(connection, (Intervento) prestazione);
			} else if (prestazione instanceof Visita) {
				insertVisitaDetails(connection, (Visita) prestazione);
			}

			insertMediciPrestazione(connection, prestazione);
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to insert prestazione", exception);
		}
	}

	@Override
	public Prestazione getPrestazioneById(int numPrestazione) {
		String sql = "SELECT num_prestazione, data_inizio, data_fine, esito, tipo, codice_ricovero FROM " + TABLE_PRESTAZIONE + " WHERE num_prestazione = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, numPrestazione);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return mapPrestazione(connection, resultSet);
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to read prestazione", exception);
		}
		return null;
	}

	@Override
	public List<Prestazione> getAllPrestazioni() {
		List<Prestazione> prestazioni = new ArrayList<>();
		String sql = "SELECT num_prestazione, data_inizio, data_fine, esito, tipo, codice_ricovero FROM " + TABLE_PRESTAZIONE + " ORDER BY num_prestazione";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				prestazioni.add(mapPrestazione(connection, resultSet));
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to read prestazioni", exception);
		}
		return prestazioni;
	}

	@Override
	public void updatePrestazione(Prestazione prestazione) {
		String type = prestazione.getClass().getSimpleName().toUpperCase();
		String updatePrestazione = "UPDATE " + TABLE_PRESTAZIONE + " SET data_inizio = ?, data_fine = ?, esito = ?, tipo = ?, codice_ricovero = ? WHERE num_prestazione = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(updatePrestazione)) {
			statement.setTimestamp(1, toTimestamp(prestazione.getDataInizio()));
			statement.setTimestamp(2, toTimestamp(prestazione.getDataFine()));
			statement.setString(3, prestazione.getEsito());
			statement.setString(4, type);
			if (prestazione.getRicoveroAssegnato() == null) {
				statement.setString(5, null);
			} else {
				statement.setString(5, prestazione.getRicoveroAssegnato().getCodiceRicovero());
			}
			statement.setInt(6, prestazione.getNumPrestazione());
			statement.executeUpdate();

			if (prestazione instanceof Intervento) {
				updateInterventoDetails(connection, (Intervento) prestazione);
			} else if (prestazione instanceof Visita) {
				updateVisitaDetails(connection, (Visita) prestazione);
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to update prestazione", exception);
		}
	}

	@Override
	public void deletePrestazione(int numPrestazione) {
		String deleteMedicoPrestazione = "DELETE FROM " + TABLE_MEDICO_PRESTAZIONE + " WHERE num_prestazione = ?";
		String deleteIntervento = "DELETE FROM " + TABLE_INTERVENTO + " WHERE num_prestazione = ?";
		String deleteVisita = "DELETE FROM " + TABLE_VISITA + " WHERE num_prestazione = ?";
		String deletePrestazione = "DELETE FROM " + TABLE_PRESTAZIONE + " WHERE num_prestazione = ?";
		try (Connection connection = getConnection(); PreparedStatement medicoStatement = connection.prepareStatement(deleteMedicoPrestazione); PreparedStatement interventoStatement = connection.prepareStatement(deleteIntervento); PreparedStatement visitaStatement = connection.prepareStatement(deleteVisita); PreparedStatement prestazioneStatement = connection.prepareStatement(deletePrestazione)) {
			medicoStatement.setInt(1, numPrestazione);
			medicoStatement.executeUpdate();

			interventoStatement.setInt(1, numPrestazione);
			interventoStatement.executeUpdate();

			visitaStatement.setInt(1, numPrestazione);
			visitaStatement.executeUpdate();

			prestazioneStatement.setInt(1, numPrestazione);
			prestazioneStatement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to delete prestazione", exception);
		}
	}

	public void sostituisciMedicoInPrestazione(int numPrestazione, String matricolaAssente, String matricolaSostituto) {
		String sql = "UPDATE medico_prestazione SET matricola_medico = ? WHERE num_prestazione = ? AND matricola_medico = ?";

		try (Connection connection = getConnection();
		     PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setString(1, matricolaSostituto);
			statement.setInt(2, numPrestazione);
			statement.setString(3, matricolaAssente);
			statement.executeUpdate();

		} catch (SQLException exception) {
			throw new IllegalStateException("Errore durante la sostituzione del medico nel DB", exception);
		}
	}

	private Prestazione mapPrestazione(Connection connection, ResultSet resultSet) throws SQLException {
		String type = resultSet.getString("tipo");
		int numPrestazione = resultSet.getInt("num_prestazione");
		Prestazione prestazione;
		if ("INTERVENTO".equalsIgnoreCase(type)) {
			prestazione = loadIntervento(connection, resultSet, numPrestazione);
		} else if ("VISITA".equalsIgnoreCase(type)) {
			prestazione = loadVisita(connection, resultSet, numPrestazione);
		} else {
			prestazione = null;
		}

		if (prestazione != null) {
			String codiceRicovero = resultSet.getString("codice_ricovero");
			if (codiceRicovero != null) {
				Ricovero ricovero = new RicoveroPostgresDao().getRicoveroById(codiceRicovero);
				prestazione.setRicovero(ricovero);
			}

			for (Medico medico : loadMedici(connection, numPrestazione)) {
				prestazione.addMedico(medico);
			}
		}
		return prestazione;
	}

	private List<Medico> loadMedici(Connection connection, int numPrestazione) throws SQLException {
		List<Medico> medici = new ArrayList<>();
		String sql = "SELECT u.login, u.password, m.matricola_medico, m.reparto_nome FROM " + TABLE_MEDICO_PRESTAZIONE + " mp " +
				"JOIN medico m ON m.matricola_medico = mp.matricola_medico " +
				"JOIN utente u ON u.login = m.login " +
				"WHERE mp.num_prestazione = ? ORDER BY m.matricola_medico";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, numPrestazione);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					Medico medico = new Medico(resultSet.getString("login"), resultSet.getString("password"), resultSet.getString("matricola_medico"));
					String repartoNome = resultSet.getString("reparto_nome");
					if (repartoNome != null) {
						medico.setReparto(new model.Reparto(repartoNome));
					}
					medici.add(medico);
				}
			}
		}
		return medici;
	}

	private Prestazione loadIntervento(Connection connection, ResultSet resultSet, int numPrestazione) throws SQLException {
		String sql = "SELECT sala_operatoria FROM " + TABLE_INTERVENTO + " WHERE num_prestazione = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, numPrestazione);
			try (ResultSet subtypeResult = statement.executeQuery()) {
				if (subtypeResult.next()) {
					return new Intervento(resultSet.getInt("num_prestazione"), toLocalDateTime(resultSet.getTimestamp("data_inizio")), toLocalDateTime(resultSet.getTimestamp("data_fine")), resultSet.getString("esito"), subtypeResult.getInt("sala_operatoria"));
				}
			}
		}
		return null;
	}

	private Prestazione loadVisita(Connection connection, ResultSet resultSet, int numPrestazione) throws SQLException {
		String sql = "SELECT tipo_visita FROM " + TABLE_VISITA + " WHERE num_prestazione = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, numPrestazione);
			try (ResultSet subtypeResult = statement.executeQuery()) {
				if (subtypeResult.next()) {
					return new Visita(resultSet.getInt("num_prestazione"), toLocalDateTime(resultSet.getTimestamp("data_inizio")), toLocalDateTime(resultSet.getTimestamp("data_fine")), resultSet.getString("esito"), subtypeResult.getString("tipo_visita"));
				}
			}
		}
		return null;
	}

	private void insertInterventoDetails(Connection connection, Intervento intervento) throws SQLException {
		String sql = "INSERT INTO " + TABLE_INTERVENTO + " (num_prestazione, sala_operatoria) VALUES (?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, intervento.getNumPrestazione());
			statement.setInt(2, intervento.getSalaOperatoria());
			statement.executeUpdate();
		}
	}

	private void insertVisitaDetails(Connection connection, Visita visita) throws SQLException {
		String sql = "INSERT INTO " + TABLE_VISITA + " (num_prestazione, tipo_visita) VALUES (?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, visita.getNumPrestazione());
			statement.setString(2, visita.getTipoVisita());
			statement.executeUpdate();
		}
	}

	private void updateInterventoDetails(Connection connection, Intervento intervento) throws SQLException {
		String update = "UPDATE " + TABLE_INTERVENTO + " SET sala_operatoria = ? WHERE num_prestazione = ?";
		try (PreparedStatement statement = connection.prepareStatement(update)) {
			statement.setInt(1, intervento.getSalaOperatoria());
			statement.setInt(2, intervento.getNumPrestazione());
			statement.executeUpdate();
		}
	}

	private void updateVisitaDetails(Connection connection, Visita visita) throws SQLException {
		String update = "UPDATE " + TABLE_VISITA + " SET tipo_visita = ? WHERE num_prestazione = ?";
		try (PreparedStatement statement = connection.prepareStatement(update)) {
			statement.setString(1, visita.getTipoVisita());
			statement.setInt(2, visita.getNumPrestazione());
			statement.executeUpdate();
		}
	}

	private void insertMediciPrestazione(Connection connection, Prestazione prestazione) throws SQLException {
		if (prestazione.getMedici().isEmpty()) {
			return;
		}

		String sql = "INSERT INTO " + TABLE_MEDICO_PRESTAZIONE + " (matricola_medico, num_prestazione) VALUES (?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			for (Medico medico : prestazione.getMedici()) {
				if (medico.getMatricolaMedico() == null) {
					continue;
				}
				statement.setString(1, medico.getMatricolaMedico());
				statement.setInt(2, prestazione.getNumPrestazione());
				statement.addBatch();
			}
			statement.executeBatch();
		}
	}
}
