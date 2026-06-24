package dao;

import model.Amministratore;

import java.util.List;

public interface AmministratoreDAO {
	void insertAmministratore(Amministratore amministratore);

	Amministratore getAmministratoreById(String matricolaAmministratore);

	List<Amministratore> getAllAmministratori();

	void updateAmministratore(Amministratore amministratore);

	void deleteAmministratore(String matricolaAmministratore);
}