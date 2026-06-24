package dao;

import model.Stanza;

import java.util.List;

public interface StanzaDAO {
	void insertStanza(Stanza stanza);

	Stanza getStanzaById(int numeroStanza);

	List<Stanza> getAllStanze();

	void updateStanza(Stanza stanza);

	void deleteStanza(int numeroStanza);
}