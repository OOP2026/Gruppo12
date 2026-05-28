package controller;

import model.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private List<Paziente> listaPazienti = new ArrayList<>();
    private List<Utente> listaUtenti = new ArrayList<>();
    private List<Letto> listaLetti = new ArrayList<>();
    private List<Ricovero> listaRicoveri = new ArrayList<>();
    private List<Prestazione> listaPrestazioni = new ArrayList<>();
    private List<Medico> listaMedici = new ArrayList<>();
    private List<Reparto> listaReparti = new ArrayList<>();
    private List<Stanza> listaStanze = new ArrayList<>();
    private List<Malattia> listaMalattie = new ArrayList<>();
    private List<TurnoLavorativo> listaTurniLavorativi = new ArrayList<>();

    public Controller() {
        inizializzaUtentiPredefiniti();
    }

    private void inizializzaUtentiPredefiniti() {
        listaUtenti.add(new Amministratore("admin", "admin", "AMM1"));
        Medico medicoPredefinito = new Medico("medico", "medico", "MED1");
        listaUtenti.add(medicoPredefinito);
        listaMedici.add(medicoPredefinito);
    }

    public boolean assegnaMalattiaMedico(String matricolaMedico, String idMalattia, LocalDateTime dataInizio, LocalDateTime dataFine) {
        if (matricolaMedico == null) {
            return false;
        }

        for (Medico m : listaMedici) {
            if (matricolaMedico.equals(m.getMatricolaMedico())) {
                Malattia malattia = new Malattia(idMalattia, dataInizio, dataFine);
                malattia.setMedico(m);
                m.addMalattia(malattia);
                listaMalattie.add(malattia);

                return true;
            }
        }
        return false;
    }

    public boolean aggiungiPaziente(String matricolaPaziente, String nome, String cognome) {

        for (Paziente p : listaPazienti) {
            if (p.getMatricolaPaziente().equals(matricolaPaziente)) {
                return false;
            }
        }

        Paziente p = new Paziente(matricolaPaziente, nome, cognome);
        listaPazienti.add(p);
        return true;
    }

    public Utente effettuaLogin(String login, String password) {
        if (login == null || password == null) {
            return null;
        }

        if (listaUtenti == null) {
            return null;
        }

        for (Utente utenteLoggato : listaUtenti) {
            if (utenteLoggato != null && utenteLoggato.login(login, password)) {
                return utenteLoggato;
            }
        }
        return null;

    }

    public boolean aggiungiRicovero(String codiceRicovero, String matricolaPaziente, String matricolaLetto) {
        for (Ricovero r : listaRicoveri) {
            if (r.getCodiceRicovero().equals(codiceRicovero)) {
                return false;
            }
        }

        Paziente pazienteTrovato = null;
        Letto lettoTrovato = null;

        for (Paziente paziente : listaPazienti) {
            if (paziente.getMatricolaPaziente().equals(matricolaPaziente)) pazienteTrovato = paziente;
        }
        for (Letto l : listaLetti) {
            if (l.getMatricolaLetto().equals(matricolaLetto)) lettoTrovato = l;
        }

        if (pazienteTrovato == null || lettoTrovato == null) {
            return false;
        }


        LocalDateTime newStart = LocalDateTime.now();
        LocalDateTime newEnd = null; // aperto
        for (Ricovero ricoveroEsistente : lettoTrovato.getRicoveri()) {
            LocalDateTime existStart = ricoveroEsistente.getDataAmmissione();
            LocalDateTime existEnd = ricoveroEsistente.getDataDimissione();


            boolean overlaps;
            if (existEnd == null) {
                overlaps = !newStart.isAfter(existStart);
                overlaps = true;
            } else {

                LocalDateTime aStart = newStart;
                LocalDateTime aEnd = (newEnd == null) ? LocalDateTime.MAX : newEnd;
                LocalDateTime bStart = existStart;
                LocalDateTime bEnd = existEnd;
                overlaps = overlap(aStart, aEnd, bStart, bEnd);
            }

            if (overlaps) {
                Paziente pAssegnato = ricoveroEsistente.getPazienteAssegnato();
                if (pAssegnato == null || !pAssegnato.getMatricolaPaziente().equals(pazienteTrovato.getMatricolaPaziente())) {
                    return false;
                }
            }
        }

        Ricovero r = new Ricovero(LocalDateTime.now(), null, codiceRicovero);
        r.setPaziente(pazienteTrovato);
        r.setLetto(lettoTrovato);
        pazienteTrovato.addRicovero(r);
        lettoTrovato.addRicovero(r);

        listaRicoveri.add(r);
        return true;
    }

    public boolean aggiungiDimissione(String codiceRicovero, String matricolaPaziente, String matricolaLetto) {

        if (codiceRicovero == null) {
            return false;
        }

        for (Ricovero r : listaRicoveri) {
            if (r.getCodiceRicovero().equals(codiceRicovero)) {


                if (r.getPazienteAssegnato().getMatricolaPaziente().equals(matricolaPaziente) &&
                        r.getLettoAssegnato().getMatricolaLetto().equals(matricolaLetto)) {


                    if (r.getDataDimissione() == null) {


                        r.setDataDimissione(LocalDateTime.now());

                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }

        return false;
    }

    public boolean rimuoviPaziente(String matricolaPaziente) {
        for (Paziente p : new ArrayList<>(listaPazienti)) {
            if (p.getMatricolaPaziente().equals(matricolaPaziente)) {
                for (Ricovero r : new ArrayList<>(p.getListaRicoveri())) {
                    if (r.getLettoAssegnato() != null) r.getLettoAssegnato().removeRicovero(r);
                    listaRicoveri.remove(r);
                    p.removeRicovero(r);
                }
                listaPazienti.remove(p);
                return true;
            }
        }
        return false;
    }

    public boolean rimuoviRicovero(String codiceRicovero) {
        for (Ricovero r : new ArrayList<>(listaRicoveri)) {
            if (r.getCodiceRicovero().equals(codiceRicovero)) {
                if (r.getPazienteAssegnato() != null) r.getPazienteAssegnato().removeRicovero(r);
                if (r.getLettoAssegnato() != null) r.getLettoAssegnato().removeRicovero(r);
                listaRicoveri.remove(r);
                return true;
            }
        }
        return false;
    }

    public boolean rimuoviMedico(String matricolaMedico) {
        for (Medico m : new ArrayList<>(listaMedici)) {
            if (m.getMatricolaMedico().equals(matricolaMedico)) {
                for (TurnoLavorativo t : new ArrayList<>(listaTurniLavorativi)) {
                    t.removeMedico(m);
                }
                for (Prestazione p : new ArrayList<>(m.getListaPrestazioni())) {
                    p.removeMedico(m);
                }
                for (Reparto rep : listaReparti) {
                    rep.removeMedico(m);
                }
                listaMedici.remove(m);
                return true;
            }
        }
        return false;
    }


    public boolean rimuoviPrestazione(Integer numPrestazione) {
        for (Ricovero r : listaRicoveri) {
            for (Prestazione p : new ArrayList<>(r.getListaPrestazioni())) {
                if (p.getNumPrestazione().equals(numPrestazione)) {
                    r.removePrestazione(p);
                    listaPrestazioni.remove(p);
                    for (Medico m : new ArrayList<>(p.getMedici())) {
                        p.removeMedico(m);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean rimuoviMalattia(String idMalattia) {
        for (Malattia m : new ArrayList<>(listaMalattie)) {
            if (m.getIdMalattia().equals(idMalattia)) {
                if (m.getMedicoAssegnato() != null) m.getMedicoAssegnato().removeMalattia(m);
                if (m.getAmministratoreAssegnato() != null) m.getAmministratoreAssegnato().removeMalattia(m);
                listaMalattie.remove(m);
                return true;
            }
        }
        return false;
    }

    public boolean rimuoviTurno(String idTurno) {
        for (TurnoLavorativo t : new ArrayList<>(listaTurniLavorativi)) {
            if (t.getIdTurno().equals(idTurno)) {
                for (Medico m : new ArrayList<>(t.getMedici())) {
                    t.removeMedico(m);
                    m.removeTurnoLavorativo(t);
                }
                listaTurniLavorativi.remove(t);
                return true;
            }
        }
        return false;
    }

    public boolean aggiungiMedico(Medico medico) {
        for (Medico m : listaMedici) if (m.getMatricolaMedico().equals(medico.getMatricolaMedico())) return false;
        listaMedici.add(medico);
        return true;
    }

    public boolean aggiungiReparto(Reparto reparto) {
        for (Reparto r : listaReparti) if (r.getNomeReparto().equals(reparto.getNomeReparto())) return false;
        listaReparti.add(reparto);
        return true;
    }

    public boolean aggiungiStanza(Stanza stanza) {
        for (Stanza s : listaStanze) if (s.getNumeroStanza().equals(stanza.getNumeroStanza())) return false;
        listaStanze.add(stanza);
        return true;
    }

    public boolean aggiungiLetto(Letto letto) {
        for (Letto l : listaLetti) if (l.getMatricolaLetto().equals(letto.getMatricolaLetto())) return false;
        listaLetti.add(letto);
        return true;
    }

    public boolean aggiungiTurno(TurnoLavorativo turno) {
        for (TurnoLavorativo t : listaTurniLavorativi) if (t.getIdTurno().equals(turno.getIdTurno())) return false;
        listaTurniLavorativi.add(turno);
        return true;
    }

    private boolean overlap(LocalDateTime aStart, LocalDateTime aEnd, LocalDateTime bStart, LocalDateTime bEnd) {
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    private boolean isLettoOccupato(Letto letto) {
        if (letto == null) {
            return false;
        }

        for (Ricovero ricovero : letto.getRicoveri()) {
            if (ricovero.getDataDimissione() == null) {
                return true;
            }
        }

        return false;
    }

    public boolean registraVisita(String codiceRicovero, Integer numPrestazione, LocalDateTime dataInizio, LocalDateTime dataFine, String esito, String tipoVisita, String matricolaMedico) {
        if (codiceRicovero == null || matricolaMedico == null) return false;
        Ricovero ricovero = null;
        Medico medico = null;
        for (Ricovero r : listaRicoveri) if (r.getCodiceRicovero().equals(codiceRicovero)) ricovero = r;
        for (Medico m : listaMedici) if (m.getMatricolaMedico().equals(matricolaMedico)) medico = m;
        if (ricovero == null || medico == null) return false;

        boolean inTurno = false;
        for (TurnoLavorativo t : medico.getListaTurniLavorativi()) {
            if (!dataInizio.isBefore(t.getInizioTurno()) && !dataFine.isAfter(t.getFineTurno())) {
                inTurno = true;
                break;
            }
        }
        if (!inTurno) return false;

        for (Prestazione p : medico.getListaPrestazioni()) {
            if (overlap(dataInizio, dataFine, p.getDataInizio(), p.getDataFine())) return false;
        }

        Visita v = new Visita(numPrestazione, dataInizio, dataFine, esito, tipoVisita);
        ricovero.addPrestazione(v);
        listaPrestazioni.add(v);
        medico.addPrestazione(v);
        return true;
    }

    public boolean registraIntervento(String codiceRicovero, Integer numPrestazione, LocalDateTime dataInizio, LocalDateTime dataFine, String esito, Integer salaOperatoria, String matricolaMedico) {
        if (codiceRicovero == null || matricolaMedico == null) return false;
        Ricovero ricovero = null;
        Medico medico = null;
        for (Ricovero r : listaRicoveri) if (r.getCodiceRicovero().equals(codiceRicovero)) ricovero = r;
        for (Medico m : listaMedici) if (m.getMatricolaMedico().equals(matricolaMedico)) medico = m;
        if (ricovero == null || medico == null) return false;

        boolean inTurno = false;
        for (TurnoLavorativo t : medico.getListaTurniLavorativi()) {
            if (!dataInizio.isBefore(t.getInizioTurno()) && !dataFine.isAfter(t.getFineTurno())) {
                inTurno = true;
                break;
            }
        }
        if (!inTurno) return false;

        for (Prestazione p : medico.getListaPrestazioni()) {
            if (overlap(dataInizio, dataFine, p.getDataInizio(), p.getDataFine())) return false;
        }

        Intervento it = new Intervento(numPrestazione, dataInizio, dataFine, esito, salaOperatoria);
        ricovero.addPrestazione(it);
        listaPrestazioni.add(it);
        medico.addPrestazione(it);
        return true;
    }

    public boolean modificaEsitoPrestazione(Integer numPrestazione, String esito) {
        for (Prestazione p : listaPrestazioni) {
            if (p.getNumPrestazione().equals(numPrestazione)) {
                p.setEsito(esito);
                return true;
            }
        }
        return false;
    }

    public List<Prestazione> agendaGiornaliera(String matricolaMedico, LocalDate giorno) {
        List<Prestazione> result = new ArrayList<>();
        for (Prestazione p : listaPrestazioni) {
            for (Medico m : p.getMedici()) {
                if (m.getMatricolaMedico().equals(matricolaMedico) && p.getDataInizio().toLocalDate().equals(giorno))
                    result.add(p);
            }
        }
        return result;
    }

    public List<Prestazione> agendaSettimanale(String matricolaMedico, LocalDate inizioSettimana) {
        List<Prestazione> result = new ArrayList<>();
        LocalDate fine = inizioSettimana.plusDays(6);
        for (Prestazione p : listaPrestazioni) {
            for (Medico m : p.getMedici()) {
                LocalDate d = p.getDataInizio().toLocalDate();
                if (m.getMatricolaMedico().equals(matricolaMedico) && (d.isEqual(inizioSettimana) || (d.isAfter(inizioSettimana) && d.isBefore(fine)) || d.isEqual(fine)))
                    result.add(p);
            }
        }
        return result;
    }

    public List<Letto> cercaLettiDisponibili(String nomeReparto) {
        List<Letto> disponibili = new ArrayList<>();
        for (Letto letto : cercaLettiPerReparto(nomeReparto)) {
            if (!isLettoOccupato(letto)) {
                disponibili.add(letto);
            }
        }
        return disponibili;
    }

    public List<Letto> cercaLettiPerReparto(String nomeReparto) {
        List<Letto> letti = new ArrayList<>();
        Reparto target = null;
        for (Reparto r : listaReparti) if (r.getNomeReparto().equals(nomeReparto)) target = r;
        if (target == null) return letti;
        for (Stanza s : target.getStanze()) {
            for (Letto l : s.getLetti()) {
                letti.add(l);
            }
        }
        return letti;
    }

    public boolean lettoOccupato(String matricolaLetto) {
        if (matricolaLetto == null) {
            return false;
        }

        for (Letto letto : listaLetti) {
            if (matricolaLetto.equals(letto.getMatricolaLetto())) {
                return isLettoOccupato(letto);
            }
        }

        return false;
    }

    public List<Ricovero> dimissioniInData(LocalDate data) {
        List<Ricovero> result = new ArrayList<>();
        for (Ricovero r : listaRicoveri) {
            if (r.getDataDimissione() != null && r.getDataDimissione().toLocalDate().equals(data)) result.add(r);
        }
        return result;
    }

    public List<Medico> suggerisciSostituti(String idMalattia) {
        List<Medico> suggeriti = new ArrayList<>();
        Malattia mal = null;
        for (Malattia m : listaMalattie) if (m.getIdMalattia().equals(idMalattia)) mal = m;
        if (mal == null) return suggeriti;
        Medico assente = mal.getMedicoAssegnato();
        if (assente == null) return suggeriti;
        Reparto rep = assente.getReparto();
        LocalDateTime start = mal.getDataInizio();
        LocalDateTime end = mal.getDataFine();
        List<Medico> candidati = new ArrayList<>();
        if (rep != null) candidati.addAll(rep.getMedici());
        else candidati.addAll(listaMedici);
        for (Medico c : candidati) {
            if (c == assente) continue;
            boolean conflitto = false;
            for (TurnoLavorativo t : c.getListaTurniLavorativi()) {
                if (overlap(start, end, t.getInizioTurno(), t.getFineTurno())) {
                    conflitto = true;
                    break;
                }
            }
            if (conflitto) continue;
            for (Prestazione p : c.getListaPrestazioni()) {
                if (overlap(start, end, p.getDataInizio(), p.getDataFine())) {
                    conflitto = true;
                    break;
                }
            }
            if (!conflitto) suggeriti.add(c);
        }
        return suggeriti;
    }


    public List<Reparto> getListaReparti() {
        return listaReparti;
    }
}

