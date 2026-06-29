package dao;

import java.util.List;
import java.util.Map;

public interface StanzaDAO {
	void insertStanza(Map<String, Object> stanza);

	Map<String, Object> getStanzaById(int numeroStanza);

	List<Map<String, Object>> getAllStanze();

	void updateStanza(Map<String, Object> stanza);

	void deleteStanza(int numeroStanza);
}
