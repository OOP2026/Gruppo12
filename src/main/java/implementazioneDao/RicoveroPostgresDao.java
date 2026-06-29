package implementazioneDao;

import dao.RicoveroDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implementazione PostgreSQL del DAO ricovero. */
public class RicoveroPostgresDao extends AbstractPostgresDao implements RicoveroDAO {

	private static final String TABLE_RICOVERO = "ricovero";

	@Override
	public void insertRicovero(Map<String, Object> ricovero) {
		String sql = "INSERT INTO " + TABLE_RICOVERO + " (codice_ricovero, data_ammissione, data_dimissione, matricola_paziente, matricola_letto) VALUES (?, ?, ?, ?, ?)";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, (String) ricovero.get("codiceRicovero"));
			statement.setTimestamp(2, toTimestamp((java.time.LocalDateTime) ricovero.get("dataAmmissione")));
			statement.setTimestamp(3, toTimestamp((java.time.LocalDateTime) ricovero.get("dataDimissione")));
			statement.setString(4, (String) ricovero.get("matricolaPaziente"));
			statement.setString(5, (String) ricovero.get("matricolaLetto"));
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile inserire il ricovero", exception);
		}
	}

	@Override
	public Map<String, Object> getRicoveroById(String codiceRicovero) {
		String sql = "SELECT codice_ricovero, data_ammissione, data_dimissione, matricola_paziente, matricola_letto FROM " + TABLE_RICOVERO + " WHERE codice_ricovero = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, codiceRicovero);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					Map<String, Object> ricovero = new HashMap<>();
					ricovero.put("codiceRicovero", resultSet.getString("codice_ricovero"));
					ricovero.put("dataAmmissione", toLocalDateTime(resultSet.getTimestamp("data_ammissione")));
					ricovero.put("dataDimissione", toLocalDateTime(resultSet.getTimestamp("data_dimissione")));
					ricovero.put("matricolaPaziente", resultSet.getString("matricola_paziente"));
					ricovero.put("matricolaLetto", resultSet.getString("matricola_letto"));
					return ricovero;
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere il ricovero", exception);
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getAllRicoveri() {
		List<Map<String, Object>> ricoveri = new ArrayList<>();
		String sql = "SELECT codice_ricovero, data_ammissione, data_dimissione, matricola_paziente, matricola_letto FROM " + TABLE_RICOVERO + " ORDER BY codice_ricovero";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				Map<String, Object> ricovero = new HashMap<>();
				ricovero.put("codiceRicovero", resultSet.getString("codice_ricovero"));
				ricovero.put("dataAmmissione", toLocalDateTime(resultSet.getTimestamp("data_ammissione")));
				ricovero.put("dataDimissione", toLocalDateTime(resultSet.getTimestamp("data_dimissione")));
				ricovero.put("matricolaPaziente", resultSet.getString("matricola_paziente"));
				ricovero.put("matricolaLetto", resultSet.getString("matricola_letto"));
				ricoveri.add(ricovero);
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere i ricoveri", exception);
		}
		return ricoveri;
	}

	@Override
	public void updateRicovero(Map<String, Object> ricovero) {
		String sql = "UPDATE " + TABLE_RICOVERO + " SET data_ammissione = ?, data_dimissione = ?, matricola_paziente = ?, matricola_letto = ? WHERE codice_ricovero = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setTimestamp(1, toTimestamp((java.time.LocalDateTime) ricovero.get("dataAmmissione")));
			statement.setTimestamp(2, toTimestamp((java.time.LocalDateTime) ricovero.get("dataDimissione")));
			statement.setString(3, (String) ricovero.get("matricolaPaziente"));
			statement.setString(4, (String) ricovero.get("matricolaLetto"));
			statement.setString(5, (String) ricovero.get("codiceRicovero"));
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile aggiornare il ricovero", exception);
		}
	}

	@Override
	public void deleteRicovero(String codiceRicovero) {
		String sql = "DELETE FROM " + TABLE_RICOVERO + " WHERE codice_ricovero = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, codiceRicovero);
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile cancellare il ricovero", exception);
		}
	}
}
