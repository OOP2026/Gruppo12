package implementazioneDao;

import dao.TurnoLavorativoDAO;
import model.TurnoLavorativo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class TurnoLavorativoPostgresDao extends AbstractPostgresDao implements TurnoLavorativoDAO {

	private static final String TABLE_TURNO = "turno_lavorativo";

	@Override
	public void insertTurnoLavorativo(TurnoLavorativo turnoLavorativo) {
		String sql = "INSERT INTO " + TABLE_TURNO + " (id_turno, inizio_turno, fine_turno) VALUES (?, ?, ?)";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, turnoLavorativo.getIdTurno());
			statement.setTimestamp(2, toTimestamp(turnoLavorativo.getInizioTurno()));
			statement.setTimestamp(3, toTimestamp(turnoLavorativo.getFineTurno()));
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile inserire il turno lavorativo", exception);
		}
	}

	@Override
	public TurnoLavorativo getTurnoLavorativoById(String idTurno) {
		String sql = "SELECT id_turno, inizio_turno, fine_turno FROM " + TABLE_TURNO + " WHERE id_turno = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, idTurno);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new TurnoLavorativo(resultSet.getString("id_turno"), toLocalDateTime(resultSet.getTimestamp("inizio_turno")), toLocalDateTime(resultSet.getTimestamp("fine_turno")));
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere il turno lavorativo", exception);
		}
		return null;
	}

	@Override
	public List<TurnoLavorativo> getAllTurniLavorativi() {
		List<TurnoLavorativo> turni = new ArrayList<>();
		String sql = "SELECT id_turno, inizio_turno, fine_turno FROM " + TABLE_TURNO + " ORDER BY id_turno";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				turni.add(new TurnoLavorativo(resultSet.getString("id_turno"), toLocalDateTime(resultSet.getTimestamp("inizio_turno")), toLocalDateTime(resultSet.getTimestamp("fine_turno"))));
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere i turni lavorativi", exception);
		}
		return turni;
	}

	@Override
	public void updateTurnoLavorativo(TurnoLavorativo turnoLavorativo) {
		String sql = "UPDATE " + TABLE_TURNO + " SET inizio_turno = ?, fine_turno = ? WHERE id_turno = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setTimestamp(1, toTimestamp(turnoLavorativo.getInizioTurno()));
			statement.setTimestamp(2, toTimestamp(turnoLavorativo.getFineTurno()));
			statement.setString(3, turnoLavorativo.getIdTurno());
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