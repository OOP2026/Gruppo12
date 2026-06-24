package implementazioneDao;

import dao.LettoDAO;
import model.Letto;
import model.Stanza;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class LettoPostgresDao extends AbstractPostgresDao implements LettoDAO {

	private static final String TABLE_LETTO = "letto";

	@Override
	public void insertLetto(Letto letto) {
		String sql = "INSERT INTO " + TABLE_LETTO + " (matricola_letto, numero_stanza) VALUES (?, ?)";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, letto.getMatricolaLetto());
			if (letto.getStanza() == null) {
				statement.setObject(2, null);
			} else {
				statement.setInt(2, letto.getStanza().getNumeroStanza());
			}
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to insert letto", exception);
		}
	}

	@Override
	public Letto getLettoById(String matricolaLetto) {
		String sql = "SELECT matricola_letto, numero_stanza FROM " + TABLE_LETTO + " WHERE matricola_letto = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, matricolaLetto);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					Letto letto = new Letto(resultSet.getString("matricola_letto"));
					int numeroStanza = resultSet.getInt("numero_stanza");
					if (!resultSet.wasNull()) {
						letto.setStanza(new Stanza(numeroStanza));
					}
					return letto;
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to read letto", exception);
		}
		return null;
	}

	@Override
	public List<Letto> getAllLetti() {
		List<Letto> letti = new ArrayList<>();
		String sql = "SELECT matricola_letto, numero_stanza FROM " + TABLE_LETTO + " ORDER BY matricola_letto";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				Letto letto = new Letto(resultSet.getString("matricola_letto"));
				int numeroStanza = resultSet.getInt("numero_stanza");
				if (!resultSet.wasNull()) {
					letto.setStanza(new Stanza(numeroStanza));
				}
				letti.add(letto);
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to read letti", exception);
		}
		return letti;
	}

	@Override
	public void updateLetto(Letto letto) {
		String sql = "UPDATE " + TABLE_LETTO + " SET numero_stanza = ? WHERE matricola_letto = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			if (letto.getStanza() == null) {
				statement.setObject(1, null);
			} else {
				statement.setInt(1, letto.getStanza().getNumeroStanza());
			}
			statement.setString(2, letto.getMatricolaLetto());
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to update letto", exception);
		}
	}

	@Override
	public void deleteLetto(String matricolaLetto) {
		String sql = "DELETE FROM " + TABLE_LETTO + " WHERE matricola_letto = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, matricolaLetto);
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Unable to delete letto", exception);
		}
	}
}