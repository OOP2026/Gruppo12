package implementazioneDao;

import dao.RepartoDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implementazione PostgreSQL del DAO reparto. */
public class RepartoPostgresDao extends AbstractPostgresDao implements RepartoDAO {

	private static final String TABLE_REPARTO = "reparto";

	@Override
	public void insertReparto(Map<String, Object> reparto) {
		String sql = "INSERT INTO " + TABLE_REPARTO + " (nome_reparto) VALUES (?)";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, (String) reparto.get("nomeReparto"));
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile inserire il reparto", exception);
		}
	}

	@Override
	public Map<String, Object> getRepartoById(String nomeReparto) {
		String sql = "SELECT nome_reparto FROM " + TABLE_REPARTO + " WHERE nome_reparto = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, nomeReparto);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					Map<String, Object> reparto = new HashMap<>();
					reparto.put("nomeReparto", resultSet.getString("nome_reparto"));
					return reparto;
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere il reparto", exception);
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getAllReparti() {
		List<Map<String, Object>> reparti = new ArrayList<>();
		String sql = "SELECT nome_reparto FROM " + TABLE_REPARTO + " ORDER BY nome_reparto";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				Map<String, Object> reparto = new HashMap<>();
				reparto.put("nomeReparto", resultSet.getString("nome_reparto"));
				reparti.add(reparto);
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere i reparti", exception);
		}
		return reparti;
	}

	@Override
	public void updateReparto(String vecchioNome, Map<String, Object> nuovoReparto) {
		String sql = "UPDATE " + TABLE_REPARTO + " SET nome_reparto = ? WHERE nome_reparto = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, (String) nuovoReparto.get("nomeReparto"));
			statement.setString(2, vecchioNome);
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile aggiornare il reparto", exception);
		}
	}

	@Override
	public void deleteReparto(String nomeReparto) {
		String sql = "DELETE FROM " + TABLE_REPARTO + " WHERE nome_reparto = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, nomeReparto);
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile eliminare il reparto", exception);
		}
	}
}
