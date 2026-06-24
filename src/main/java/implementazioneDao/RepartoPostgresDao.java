package implementazioneDao;

import dao.RepartoDAO;
import model.Reparto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class RepartoPostgresDao extends AbstractPostgresDao implements RepartoDAO {

	private static final String TABLE_REPARTO = "reparto";

	@Override
	public void insertReparto(Reparto reparto) {
		String sql = "INSERT INTO " + TABLE_REPARTO + " (nome_reparto) VALUES (?)";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, reparto.getNomeReparto());
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to insert reparto", exception);
		}
	}

	@Override
	public Reparto getRepartoById(String nomeReparto) {
		String sql = "SELECT nome_reparto FROM " + TABLE_REPARTO + " WHERE nome_reparto = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, nomeReparto);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new Reparto(resultSet.getString("nome_reparto"));
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to read reparto", exception);
		}
		return null;
	}

	@Override
	public List<Reparto> getAllReparti() {
		List<Reparto> reparti = new ArrayList<>();
		String sql = "SELECT nome_reparto FROM " + TABLE_REPARTO + " ORDER BY nome_reparto";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				reparti.add(new Reparto(resultSet.getString("nome_reparto")));
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to read reparti", exception);
		}
		return reparti;
	}

	@Override
	public void updateReparto(String vecchioNome, Reparto nuovoReparto) {
		String sql = "UPDATE " + TABLE_REPARTO + " SET nome_reparto = ? WHERE nome_reparto = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {


			statement.setString(1, nuovoReparto.getNomeReparto());


			statement.setString(2, vecchioNome);

			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to update reparto", exception);
		}
	}

	@Override
	public void deleteReparto(String nomeReparto) {
		String sql = "DELETE FROM " + TABLE_REPARTO + " WHERE nome_reparto = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, nomeReparto);
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to delete reparto", exception);
		}
	}
}