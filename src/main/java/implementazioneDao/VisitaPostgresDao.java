package implementazioneDao;

import dao.VisitaDAO;
import model.Visita;

import java.util.ArrayList;
import java.util.List;

public class VisitaPostgresDao extends AbstractPostgresDao implements VisitaDAO {

	@Override
	public void insertVisita(Visita visita) {
		new PrestazionePostgresDao().insertPrestazione(visita);
	}

	@Override
	public Visita getVisitaById(int numPrestazione) {

		model.Prestazione prestazione = new PrestazionePostgresDao().getPrestazioneById(numPrestazione);


		if (prestazione instanceof model.Visita) {
			return (model.Visita) prestazione;
		}


		return null;
	}

	@Override
	public List<Visita> getAllVisite() {
		List<Visita> visite = new ArrayList<>();
		for (model.Prestazione prestazione : new PrestazionePostgresDao().getAllPrestazioni()) {
			if (prestazione instanceof Visita) {
				visite.add((Visita) prestazione);
			}
		}
		return visite;
	}

	@Override
	public void updateVisita(Visita visita) {
		new PrestazionePostgresDao().updatePrestazione(visita);
	}

	@Override
	public void deleteVisita(int numPrestazione) {
		new PrestazionePostgresDao().deletePrestazione(numPrestazione);
	}
}