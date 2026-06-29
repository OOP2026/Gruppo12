package implementazioneDao;

import dao.TurnoLavorativoDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TurnoLavorativoPostgresDao extends AbstractPostgresDao implements TurnoLavorativoDAO {

	private static final String TABLE_TURNO = "turno_lavorativo";

	@Override
	public void insertTurnoLavorativo(Map<String, Object> turnoLavorativo) {
		String sql = "INSERT INTO " + TABLE_TURNO + " (id_turno, inizio_turno, fine_turno) VALUES (?, ?, ?)";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, (String) turnoLavorativo.get("idTurno"));
			statement.setTimestamp(2, toTimestamp((java.time.LocalDateTime) turnoLavorativo.get("inizioTurno")));
			statement.setTimestamp(3, toTimestamp((java.time.LocalDateTime) turnoLavorativo.get("fineTurno")));
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile inserire il turno lavorativo", exception);
		}
	}

	@Override
	public Map<String, Object> getTurnoLavorativoById(String idTurno) {
		String sql = "SELECT id_turno, inizio_turno, fine_turno FROM " + TABLE_TURNO + " WHERE id_turno = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, idTurno);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					Map<String, Object> turno = new HashMap<>();
					turno.put("idTurno", resultSet.getString("id_turno"));
					turno.put("inizioTurno", toLocalDateTime(resultSet.getTimestamp("inizio_turno")));
					turno.put("fineTurno", toLocalDateTime(resultSet.getTimestamp("fine_turno")));
					return turno;
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere il turno lavorativo", exception);
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getAllTurniLavorativi() {
		List<Map<String, Object>> turni = new ArrayList<>();
		String sql = "SELECT id_turno, inizio_turno, fine_turno FROM " + TABLE_TURNO + " ORDER BY id_turno";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				Map<String, Object> turno = new HashMap<>();
				turno.put("idTurno", resultSet.getString("id_turno"));
				turno.put("inizioTurno", toLocalDateTime(resultSet.getTimestamp("inizio_turno")));
				turno.put("fineTurno", toLocalDateTime(resultSet.getTimestamp("fine_turno")));
				turni.add(turno);
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere i turni lavorativi", exception);
		}
		return turni;
	}

	@Override
	public void updateTurnoLavorativo(Map<String, Object> turnoLavorativo) {
		String sql = "UPDATE " + TABLE_TURNO + " SET inizio_turno = ?, fine_turno = ? WHERE id_turno = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setTimestamp(1, toTimestamp((java.time.LocalDateTime) turnoLavorativo.get("inizioTurno")));
			statement.setTimestamp(2, toTimestamp((java.time.LocalDateTime) turnoLavorativo.get("fineTurno")));
			statement.setString(3, (String) turnoLavorativo.get("idTurno"));
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile aggiornare il turno lavorativo", exception);
		}
	}

	@Override
	public void deleteTurnoLavorativo(String idTurno) {
		String sql = "DELETE FROM " + TABLE_TURNO + " WHERE id_turno = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, idTurno);
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile eliminare il turno lavorativo", exception);
		}
	}
}
