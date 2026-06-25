package implementazioneDao;

import dao.RicoveroDAO;
import model.Ricovero;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class RicoveroPostgresDao extends AbstractPostgresDao implements RicoveroDAO {

	private static final String TABLE_RICOVERO = "ricovero";

	@Override
	public void insertRicovero(Ricovero ricovero) {
		String sql = "INSERT INTO " + TABLE_RICOVERO + " (codice_ricovero, data_ammissione, data_dimissione, matricola_paziente, matricola_letto) VALUES (?, ?, ?, ?, ?)";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, ricovero.getCodiceRicovero());
			statement.setTimestamp(2, toTimestamp(ricovero.getDataAmmissione()));
			statement.setTimestamp(3, toTimestamp(ricovero.getDataDimissione()));
			if (ricovero.getPazienteAssegnato() == null) {
				statement.setString(4, null);
			} else {
				statement.setString(4, ricovero.getPazienteAssegnato().getMatricolaPaziente());
			}
			if (ricovero.getLettoAssegnato() == null) {
				statement.setString(5, null);
			} else {
				statement.setString(5, ricovero.getLettoAssegnato().getMatricolaLetto());
			}
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile inserire il ricovero", exception);
		}
	}

	@Override
	public Ricovero getRicoveroById(String codiceRicovero) {
		String sql = "SELECT codice_ricovero, data_ammissione, data_dimissione, matricola_paziente, matricola_letto FROM " + TABLE_RICOVERO + " WHERE codice_ricovero = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, codiceRicovero);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					Ricovero ricovero = new Ricovero(toLocalDateTime(resultSet.getTimestamp("data_ammissione")), toLocalDateTime(resultSet.getTimestamp("data_dimissione")), resultSet.getString("codice_ricovero"));
					String matricolaPaziente = resultSet.getString("matricola_paziente");
					if (matricolaPaziente != null) {
						ricovero.setPaziente(new PazientePostgresDao().getPazienteById(matricolaPaziente));
					}
					String matricolaLetto = resultSet.getString("matricola_letto");
					if (matricolaLetto != null) {
						ricovero.setLetto(new LettoPostgresDao().getLettoById(matricolaLetto));
					}
					return ricovero;
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere il ricovero", exception);
		}
		return null;
	}

	@Override
	public List<Ricovero> getAllRicoveri() {
		List<Ricovero> ricoveri = new ArrayList<>();
		String sql = "SELECT codice_ricovero, data_ammissione, data_dimissione, matricola_paziente, matricola_letto FROM " + TABLE_RICOVERO + " ORDER BY codice_ricovero";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				Ricovero ricovero = new Ricovero(toLocalDateTime(resultSet.getTimestamp("data_ammissione")), toLocalDateTime(resultSet.getTimestamp("data_dimissione")), resultSet.getString("codice_ricovero"));
				String matricolaPaziente = resultSet.getString("matricola_paziente");
				if (matricolaPaziente != null) {
					ricovero.setPaziente(new PazientePostgresDao().getPazienteById(matricolaPaziente));
				}
				String matricolaLetto = resultSet.getString("matricola_letto");
				if (matricolaLetto != null) {
					ricovero.setLetto(new LettoPostgresDao().getLettoById(matricolaLetto));
				}
				ricoveri.add(ricovero);
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere i ricoveri", exception);
		}
		return ricoveri;
	}

	@Override
	public void updateRicovero(Ricovero ricovero) {
		String sql = "UPDATE " + TABLE_RICOVERO + " SET data_ammissione = ?, data_dimissione = ?, matricola_paziente = ?, matricola_letto = ? WHERE codice_ricovero = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setTimestamp(1, toTimestamp(ricovero.getDataAmmissione()));
			statement.setTimestamp(2, toTimestamp(ricovero.getDataDimissione()));
			if (ricovero.getPazienteAssegnato() == null) {
				statement.setString(3, null);
			} else {
				statement.setString(3, ricovero.getPazienteAssegnato().getMatricolaPaziente());
			}
			if (ricovero.getLettoAssegnato() == null) {
				statement.setString(4, null);
			} else {
				statement.setString(4, ricovero.getLettoAssegnato().getMatricolaLetto());
			}
			statement.setString(5, ricovero.getCodiceRicovero());
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