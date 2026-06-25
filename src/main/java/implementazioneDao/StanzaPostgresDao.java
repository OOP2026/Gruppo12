package implementazioneDao;

import dao.StanzaDAO;
import model.Stanza;
import model.Reparto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class StanzaPostgresDao extends AbstractPostgresDao implements StanzaDAO {

	private static final String TABLE_STANZA = "stanza";

	@Override
	public void insertStanza(Stanza stanza) {
		String sql = "INSERT INTO " + TABLE_STANZA + " (numero_stanza, reparto_nome) VALUES (?, ?)";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, stanza.getNumeroStanza());
			if (stanza.getReparto() == null) {
				statement.setString(2, null);
			} else {
				statement.setString(2, stanza.getReparto().getNomeReparto());
			}
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile inserire la stanza", exception);
		}
	}

	@Override
	public Stanza getStanzaById(int numeroStanza) {
		String sql = "SELECT numero_stanza, reparto_nome FROM " + TABLE_STANZA + " WHERE numero_stanza = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, numeroStanza);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					Stanza stanza = new Stanza(resultSet.getInt("numero_stanza"));
					String repartoNome = resultSet.getString("reparto_nome");
					if (repartoNome != null) {
						stanza.setReparto(new Reparto(repartoNome));
					}
					return stanza;
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere la stanza", exception);
		}
		return null;
	}

	@Override
	public List<Stanza> getAllStanze() {
		List<Stanza> stanze = new ArrayList<>();
		String sql = "SELECT numero_stanza, reparto_nome FROM " + TABLE_STANZA + " ORDER BY numero_stanza";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				Stanza stanza = new Stanza(resultSet.getInt("numero_stanza"));
				String repartoNome = resultSet.getString("reparto_nome");
				if (repartoNome != null) {
					stanza.setReparto(new Reparto(repartoNome));
				}
				stanze.add(stanza);
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile leggere le stanze", exception);
		}
		return stanze;
	}

	@Override
	public void updateStanza(Stanza stanza) {
		String sql = "UPDATE " + TABLE_STANZA + " SET reparto_nome = ? WHERE numero_stanza = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			if (stanza.getReparto() == null) {
				statement.setString(1, null);
			} else {
				statement.setString(1, stanza.getReparto().getNomeReparto());
			}
			statement.setInt(2, stanza.getNumeroStanza());
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile aggiornare la stanza", exception);
		}
	}

	@Override
	public void deleteStanza(int numeroStanza) {
		String sql = "DELETE FROM " + TABLE_STANZA + " WHERE numero_stanza = ?";
		try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, numeroStanza);
			statement.executeUpdate();
		} catch (SQLException exception) {
			throw new IllegalStateException("Impossibile eliminare la stanza", exception);
		}
	}
}