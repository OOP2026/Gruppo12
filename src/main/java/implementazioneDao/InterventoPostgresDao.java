package implementazioneDao;

import dao.InterventoDAO;
import model.Intervento;

import java.util.ArrayList;
import java.util.List;

public class InterventoPostgresDao extends AbstractPostgresDao implements InterventoDAO {

	@Override
	public void insertIntervento(Intervento intervento) {
		new PrestazionePostgresDao().insertPrestazione(intervento);
	}


	@Override
	public Intervento getInterventoById(int numPrestazione) {

		model.Prestazione prestazione = new PrestazionePostgresDao().getPrestazioneById(numPrestazione);


		if (prestazione instanceof model.Intervento) {
			return (model.Intervento) prestazione;
		}


		return null;
	}

	@Override
	public List<Intervento> getAllInterventi() {
		List<Intervento> interventi = new ArrayList<>();
		for (model.Prestazione prestazione : new PrestazionePostgresDao().getAllPrestazioni()) {
			if (prestazione instanceof Intervento) {
				interventi.add((Intervento) prestazione);
			}
		}
		return interventi;
	}

	@Override
	public void updateIntervento(Intervento intervento) {
		new PrestazionePostgresDao().updatePrestazione(intervento);
	}

	@Override
	public void deleteIntervento(int numPrestazione) {
		new PrestazionePostgresDao().deletePrestazione(numPrestazione);
	}
}