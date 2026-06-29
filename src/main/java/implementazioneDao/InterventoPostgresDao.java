package implementazioneDao;

import dao.InterventoDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Adapter PostgreSQL per gli interventi, basato sul DAO delle prestazioni. */
public class InterventoPostgresDao extends AbstractPostgresDao implements InterventoDAO {

	@Override
	public void insertIntervento(Map<String, Object> intervento) {
		new PrestazionePostgresDao().insertPrestazione(intervento);
	}


	@Override
	public Map<String, Object> getInterventoById(int numPrestazione) {
		Map<String, Object> prestazione = new PrestazionePostgresDao().getPrestazioneById(numPrestazione);
		if (prestazione != null && "INTERVENTO".equalsIgnoreCase((String) prestazione.get("tipo"))) {
			return prestazione;
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getAllInterventi() {
		List<Map<String, Object>> interventi = new ArrayList<>();
		for (Map<String, Object> prestazione : new PrestazionePostgresDao().getAllPrestazioni()) {
			if ("INTERVENTO".equalsIgnoreCase((String) prestazione.get("tipo"))) {
				interventi.add(prestazione);
			}
		}
		return interventi;
	}

	@Override
	public void updateIntervento(Map<String, Object> intervento) {
		new PrestazionePostgresDao().updatePrestazione(intervento);
	}

	@Override
	public void deleteIntervento(int numPrestazione) {
		new PrestazionePostgresDao().deletePrestazione(numPrestazione);
	}
}
