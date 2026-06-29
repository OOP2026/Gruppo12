package implementazioneDao;

import dao.PrestazioneDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implementazione PostgreSQL del DAO prestazione. */
public class PrestazionePostgresDao extends AbstractPostgresDao implements PrestazioneDAO {

	private static final String TABLE_PRESTAZIONE = "prestazione";
	private static final String TABLE_INTERVENTO = "intervento";
	private static final String TABLE_VISITA = "visita";
	private static final String TABLE_MEDICO_PRESTAZIONE = "medico_prestazione";

	@Override
	public void insertPrestazione(Map<String, Object> prestazione) {
		String type = (String) prestazione.get("tipo");
		String insertPrestazione = "INSERT INTO " + TABLE_PRESTAZIONE + " (num_prestazione, data_inizio, data_fine, esito, tipo, codice_ricovero) VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(insertPrestazione)) {
			statement.setInt(1, (Integer) prestazione.get("numPrestazione"));
			statement.setTimestamp(2, toTimestamp((java.time.LocalDateTime) prestazione.get("dataInizio")));
			statement.setTimestamp(3, toTimestamp((java.time.LocalDateTime) prestazione.get("dataFine")));
			statement.setString(4, (String) prestazione.get("esito"));
			statement.setString(5, type);
			statement.setString(6, (String) prestazione.get("codiceRicovero"));
			statement.executeUpdate();

			if ("INTERVENTO".equalsIgnoreCase(type)) {
				insertInterventoDetails(connection, prestazione);
			} else if ("VISITA".equalsIgnoreCase(type)) {
				insertVisitaDetails(connection, prestazione);
			}

			insertMediciPrestazione(connection, prestazione);
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile inserire la prestazione", exception);
		}
	}

	@Override
	public Map<String, Object> getPrestazioneById(int numPrestazione) {
		String sql = "SELECT num_prestazione, data_inizio, data_fine, esito, tipo, codice_ricovero FROM " + TABLE_PRESTAZIONE + " WHERE num_prestazione = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, numPrestazione);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return mapPrestazione(connection, resultSet);
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere la prestazione", exception);
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getAllPrestazioni() {
		List<Map<String, Object>> prestazioni = new ArrayList<>();
		String sql = "SELECT num_prestazione, data_inizio, data_fine, esito, tipo, codice_ricovero FROM " + TABLE_PRESTAZIONE + " ORDER BY num_prestazione";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				prestazioni.add(mapPrestazione(connection, resultSet));
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere le prestazioni", exception);
		}
		return prestazioni;
	}

	@Override
	public void updatePrestazione(Map<String, Object> prestazione) {
		String type = (String) prestazione.get("tipo");
		String updatePrestazione = "UPDATE " + TABLE_PRESTAZIONE + " SET data_inizio = ?, data_fine = ?, esito = ?, tipo = ?, codice_ricovero = ? WHERE num_prestazione = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(updatePrestazione)) {
			statement.setTimestamp(1, toTimestamp((java.time.LocalDateTime) prestazione.get("dataInizio")));
			statement.setTimestamp(2, toTimestamp((java.time.LocalDateTime) prestazione.get("dataFine")));
			statement.setString(3, (String) prestazione.get("esito"));
			statement.setString(4, type);
			statement.setString(5, (String) prestazione.get("codiceRicovero"));
			statement.setInt(6, (Integer) prestazione.get("numPrestazione"));
			statement.executeUpdate();

			if ("INTERVENTO".equalsIgnoreCase(type)) {
				updateInterventoDetails(connection, prestazione);
			} else if ("VISITA".equalsIgnoreCase(type)) {
				updateVisitaDetails(connection, prestazione);
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile aggiornare la prestazione", exception);
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
			throw new IllegalStateException("Impossibile eliminare la prestazione", exception);
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

	private Map<String, Object> mapPrestazione(Connection connection, ResultSet resultSet) throws SQLException {
		String type = resultSet.getString("tipo");
		int numPrestazione = resultSet.getInt("num_prestazione");
		Map<String, Object> prestazione = new HashMap<>();
		prestazione.put("numPrestazione", numPrestazione);
		prestazione.put("dataInizio", toLocalDateTime(resultSet.getTimestamp("data_inizio")));
		prestazione.put("dataFine", toLocalDateTime(resultSet.getTimestamp("data_fine")));
		prestazione.put("esito", resultSet.getString("esito"));
		prestazione.put("tipo", type);
		prestazione.put("codiceRicovero", resultSet.getString("codice_ricovero"));
		prestazione.putAll(loadSubtypeDetails(connection, type, numPrestazione));
		prestazione.put("medici", loadMedici(connection, numPrestazione));
		return prestazione;
	}

	private List<String> loadMedici(Connection connection, int numPrestazione) throws SQLException {
		List<String> medici = new ArrayList<>();
		String sql = "SELECT u.login, u.password, m.matricola_medico, m.reparto_nome FROM " + TABLE_MEDICO_PRESTAZIONE + " mp " +
				"JOIN medico m ON m.matricola_medico = mp.matricola_medico " +
				"JOIN utente u ON u.login = m.login " +
				"WHERE mp.num_prestazione = ? ORDER BY m.matricola_medico";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, numPrestazione);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					medici.add(resultSet.getString("matricola_medico"));
				}
			}
		}
		return medici;
	}

	private Map<String, Object> loadSubtypeDetails(Connection connection, String type, int numPrestazione) throws SQLException {
		Map<String, Object> details = new HashMap<>();
		if ("INTERVENTO".equalsIgnoreCase(type)) {
			String sql = "SELECT sala_operatoria FROM " + TABLE_INTERVENTO + " WHERE num_prestazione = ?";
			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				statement.setInt(1, numPrestazione);
				try (ResultSet subtypeResult = statement.executeQuery()) {
					if (subtypeResult.next()) {
						details.put("salaOperatoria", subtypeResult.getInt("sala_operatoria"));
					}
				}
			}
		} else if ("VISITA".equalsIgnoreCase(type)) {
			String sql = "SELECT tipo_visita FROM " + TABLE_VISITA + " WHERE num_prestazione = ?";
			try (PreparedStatement statement = connection.prepareStatement(sql)) {
				statement.setInt(1, numPrestazione);
				try (ResultSet subtypeResult = statement.executeQuery()) {
					if (subtypeResult.next()) {
						details.put("tipoVisita", subtypeResult.getString("tipo_visita"));
					}
				}
			}
		}
		return details;
	}

	private void insertInterventoDetails(Connection connection, Map<String, Object> prestazione) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + TABLE_INTERVENTO + " (num_prestazione, sala_operatoria) VALUES (?, ?)")) {
			statement.setInt(1, (Integer) prestazione.get("numPrestazione"));
			statement.setInt(2, (Integer) prestazione.get("salaOperatoria"));
			statement.executeUpdate();
		}
	}

	private void insertVisitaDetails(Connection connection, Map<String, Object> prestazione) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + TABLE_VISITA + " (num_prestazione, tipo_visita) VALUES (?, ?)")) {
			statement.setInt(1, (Integer) prestazione.get("numPrestazione"));
			statement.setString(2, (String) prestazione.get("tipoVisita"));
			statement.executeUpdate();
		}
	}

	private void updateInterventoDetails(Connection connection, Map<String, Object> prestazione) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement("UPDATE " + TABLE_INTERVENTO + " SET sala_operatoria = ? WHERE num_prestazione = ?")) {
			statement.setInt(1, (Integer) prestazione.get("salaOperatoria"));
			statement.setInt(2, (Integer) prestazione.get("numPrestazione"));
			statement.executeUpdate();
		}
	}

	private void updateVisitaDetails(Connection connection, Map<String, Object> prestazione) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement("UPDATE " + TABLE_VISITA + " SET tipo_visita = ? WHERE num_prestazione = ?")) {
			statement.setString(1, (String) prestazione.get("tipoVisita"));
			statement.setInt(2, (Integer) prestazione.get("numPrestazione"));
			statement.executeUpdate();
		}
	}

	private void insertMediciPrestazione(Connection connection, Map<String, Object> prestazione) throws SQLException {
		Object mediciObj = prestazione.get("medici");
		if (!(mediciObj instanceof List)) {
			return;
		}
		List<?> medici = (List<?>) mediciObj;
		if (medici.isEmpty()) {
			return;
		}
		try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + TABLE_MEDICO_PRESTAZIONE + " (matricola_medico, num_prestazione) VALUES (?, ?)")) {
			for (Object matricola : medici) {
				statement.setString(1, (String) matricola);
				statement.setInt(2, (Integer) prestazione.get("numPrestazione"));
				statement.addBatch();
			}
			statement.executeBatch();
		}
	}
}
