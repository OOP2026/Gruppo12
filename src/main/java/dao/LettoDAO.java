package dao;

import model.Letto;

import java.util.List;

public interface LettoDAO {
	void insertLetto(Letto letto);

	Letto getLettoById(String matricolaLetto);

	List<Letto> getAllLetti();

	void updateLetto(Letto letto);

	void deleteLetto(String matricolaLetto);
}