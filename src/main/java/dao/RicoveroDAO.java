package dao;

import java.util.List;
import java.util.Map;

/** Operazioni CRUD sui ricoveri persistiti nel database. */
public interface RicoveroDAO {
	void insertRicovero(Map<String, Object> ricovero);

	Map<String, Object> getRicoveroById(String codiceRicovero);

	List<Map<String, Object>> getAllRicoveri();

	void updateRicovero(Map<String, Object> ricovero);

	void deleteRicovero(String codiceRicovero);
}
