package dao;

import model.Ricovero;

import java.util.List;

public interface RicoveroDAO {
	void insertRicovero(Ricovero ricovero);

	Ricovero getRicoveroById(String codiceRicovero);

	List<Ricovero> getAllRicoveri();

	void updateRicovero(Ricovero ricovero);

	void deleteRicovero(String codiceRicovero);
}