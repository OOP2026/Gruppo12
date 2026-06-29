package implementazioneDao;

import dao.VisitaDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Adapter PostgreSQL per le visite, basato sul DAO delle prestazioni. */
public class VisitaPostgresDao extends AbstractPostgresDao implements VisitaDAO {

	@Override
	public void insertVisita(Map<String, Object> visita) {
		new PrestazionePostgresDao().insertPrestazione(visita);
	}

	@Override
	public Map<String, Object> getVisitaById(int numPrestazione) {
		Map<String, Object> prestazione = new PrestazionePostgresDao().getPrestazioneById(numPrestazione);
		if (prestazione != null && "VISITA".equalsIgnoreCase((String) prestazione.get("tipo"))) {
			return prestazione;
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getAllVisite() {
		List<Map<String, Object>> visite = new ArrayList<>();
		for (Map<String, Object> prestazione : new PrestazionePostgresDao().getAllPrestazioni()) {
			if ("VISITA".equalsIgnoreCase((String) prestazione.get("tipo"))) {
				visite.add(prestazione);
			}
		}
		return visite;
	}

	@Override
	public void updateVisita(Map<String, Object> visita) {
		new PrestazionePostgresDao().updatePrestazione(visita);
	}

	@Override
	public void deleteVisita(int numPrestazione) {
		new PrestazionePostgresDao().deletePrestazione(numPrestazione);
	}
}
