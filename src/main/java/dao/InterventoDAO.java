package dao;

import model.Intervento;

import java.util.List;

public interface InterventoDAO {
	void insertIntervento(Intervento intervento);

	Intervento getInterventoById(int numPrestazione);

	List<Intervento> getAllInterventi();

	void updateIntervento(Intervento intervento);

	void deleteIntervento(int numPrestazione);
}