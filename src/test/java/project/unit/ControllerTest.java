package project.unit;

import controller.Controller;
import model.*;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class ControllerTest {

    @Test
    public void testRegistraVisitaAndAgenda() {
        Controller c = new Controller();

        Reparto rep = new Reparto("Cardio");
        c.aggiungiReparto(rep);

        Stanza s = new Stanza(1);
        s.setReparto(rep);
        rep.addStanza(s);
        c.aggiungiStanza(s);

        Letto l1 = new Letto("L1");
        l1.setStanza(s);
        s.addLetto(l1);
        c.aggiungiLetto(l1);

        c.aggiungiPaziente("P1","Nome","Cognome");

        Medico m1 = new Medico("u1","p1","M1");
        m1.setReparto(rep);
        rep.addMedico(m1);
        c.aggiungiMedico(m1);

        LocalDateTime inizio = LocalDateTime.of(2026,5,21,9,0);
        LocalDateTime fine = LocalDateTime.of(2026,5,21,17,0);
        TurnoLavorativo t = new TurnoLavorativo("T1", inizio, fine);
        t.addMedico(m1);
        m1.addTurnoLavorativo(t);
        c.aggiungiTurno(t);

        boolean ricoveroOk = c.aggiungiRicovero("R1","P1","L1");
        assertTrue(ricoveroOk);
        System.out.println("testRegistraVisitaAndAgenda - ricoveroOk: " + ricoveroOk);

        LocalDateTime vStart = LocalDateTime.of(2026,5,21,10,0);
        LocalDateTime vEnd = LocalDateTime.of(2026,5,21,10,30);
        boolean visitaOk = c.registraVisita("R1",1,vStart,vEnd,"OK","controllo","M1");
        assertTrue(visitaOk);
        System.out.println("testRegistraVisitaAndAgenda - visitaOk: " + visitaOk);

        List<Prestazione> agenda = c.agendaGiornaliera("M1", LocalDate.of(2026,5,21));
        assertEquals(1, agenda.size());
        System.out.println("testRegistraVisitaAndAgenda - agenda size: " + agenda.size());
        for (Prestazione prestazione : agenda) {
            System.out.println("  prestazione: " + prestazione.getNumPrestazione() +
                    " start=" + prestazione.getDataInizio() +
                    " end=" + prestazione.getDataFine() +
                    " esito=" + prestazione.getEsito());
        }
    }

    @Test
    public void testOverlapPrevention() {
        Controller c = new Controller();

        Reparto rep = new Reparto("Cardio");
        c.aggiungiReparto(rep);

        Medico m1 = new Medico("u1","p1","M1");
        m1.setReparto(rep);
        rep.addMedico(m1);
        c.aggiungiMedico(m1);

        LocalDateTime inizio = LocalDateTime.of(2026,5,21,9,0);
        LocalDateTime fine = LocalDateTime.of(2026,5,21,17,0);
        TurnoLavorativo t = new TurnoLavorativo("T1", inizio, fine);
        t.addMedico(m1);
        m1.addTurnoLavorativo(t);
        c.aggiungiTurno(t);

        // prepare patient and ricovero
        Stanza s = new Stanza(2);
        s.setReparto(rep);
        rep.addStanza(s);
        Letto l = new Letto("L2");
        l.setStanza(s);
        s.addLetto(l);
        c.aggiungiStanza(s);
        c.aggiungiLetto(l);

        c.aggiungiPaziente("P2","N","C");
        boolean ricoveroOk = c.aggiungiRicovero("R2","P2","L2");
        assertTrue(ricoveroOk);

        LocalDateTime aStart = LocalDateTime.of(2026,5,21,11,0);
        LocalDateTime aEnd = LocalDateTime.of(2026,5,21,12,0);
        boolean first = c.registraVisita("R2",10,aStart,aEnd,"OK","tipo","M1");
        assertTrue(first);
        System.out.println("testOverlapPrevention - first visita: " + first);

        // overlapping
        LocalDateTime bStart = LocalDateTime.of(2026,5,21,11,30);
        LocalDateTime bEnd = LocalDateTime.of(2026,5,21,12,30);
        boolean second = c.registraVisita("R2",11,bStart,bEnd,"OK","tipo","M1");
        assertFalse(second);
        System.out.println("testOverlapPrevention - second visita (expected false): " + second);
    }

    @Test
    public void testCercaLettiDisponibiliAndDimissione() {
        Controller c = new Controller();

        Reparto rep = new Reparto("Orto");
        c.aggiungiReparto(rep);
        Stanza s = new Stanza(1);
        s.setReparto(rep);
        rep.addStanza(s);
        Letto l1 = new Letto("LA"); l1.setStanza(s); s.addLetto(l1); c.aggiungiLetto(l1);
        Letto l2 = new Letto("LB"); l2.setStanza(s); s.addLetto(l2); c.aggiungiLetto(l2);
        c.aggiungiStanza(s);

        c.aggiungiPaziente("PX","A","B");
        c.aggiungiPaziente("PY","C","D");
        boolean r1 = c.aggiungiRicovero("R10","PX","LA");
        assertTrue(r1);
        boolean r2 = c.aggiungiRicovero("R11","PY","LB");
        assertTrue(r2);

        List<Letto> tutti = c.cercaLettiPerReparto("Orto");
        assertEquals(2, tutti.size());
        assertTrue(c.lettoOccupato("LA"));
        assertTrue(c.lettoOccupato("LB"));

        // both occupied -> no available
        List<Letto> disponibili = c.cercaLettiDisponibili("Orto");
        assertEquals(0, disponibili.size());
        System.out.println("testCercaLettiDisponibiliAndDimissione - letti disponibili before dimissione: " + disponibili.size());

        // dimetti R10
        boolean dim = c.aggiungiDimissione("R10","PX","LA");
        assertTrue(dim);
        System.out.println("testCercaLettiDisponibiliAndDimissione - dimissione R10: " + dim);

        List<Ricovero> dimissioni = c.dimissioniInData(LocalDate.now());
        // depending on current date, at least R10 should be included if today
        // but we called LocalDate.now() in aggiungiDimissione; assert R10 present
        boolean found = false;
        for (Ricovero r : dimissioni) if (r.getCodiceRicovero().equals("R10")) found = true;
        assertTrue(found);
        System.out.println("testCercaLettiDisponibiliAndDimissione - dimissioni oggi: " + dimissioni.size());

        // now LA should be free
        List<Letto> disp2 = c.cercaLettiDisponibili("Orto");
        assertEquals(1, disp2.size());
        assertFalse(c.lettoOccupato("LA"));
        assertTrue(c.lettoOccupato("LB"));
        System.out.println("testCercaLettiDisponibiliAndDimissione - letti disponibili after dimissione: " + disp2.size());
    }

    @Test
    public void testSuggerisciSostituto() {
        Controller c = new Controller();
        Reparto rep = new Reparto("Chir"); c.aggiungiReparto(rep);
        Medico m1 = new Medico("u1","p1","M1"); m1.setReparto(rep); rep.addMedico(m1); c.aggiungiMedico(m1);
        Medico m2 = new Medico("u2","p2","M2"); m2.setReparto(rep); rep.addMedico(m2); c.aggiungiMedico(m2);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(3);
        boolean assigned = c.assegnaMalattiaMedico("M1","malA", start, end);
        assertTrue(assigned);
        System.out.println("testSuggerisciSostituto - malattia assegnata: " + assigned);

        List<Medico> suggeriti = c.suggerisciSostituto("malA");
        // m2 should be suggested (no turni or prestazioni)
        boolean ok = false; for (Medico m : suggeriti) if (m.getMatricolaMedico().equals("M2")) ok = true;
        assertTrue(ok);
        System.out.println("testSuggerisciSostituto - suggeriti: " + suggeriti.size());
        for (Medico medico : suggeriti) {
            System.out.println("  medico: " + medico.getMatricolaMedico());
        }
    }

    @Test
    public void testLoginPredefinitiPerAdminEMedico() {
        Controller c = new Controller();

        Utente admin = c.effettuaLogin("admin", "admin");
        assertNotNull(admin);
        assertTrue(admin instanceof model.Amministratore);

        Utente medico = c.effettuaLogin("medico", "medico");
        assertNotNull(medico);
        assertTrue(medico instanceof model.Medico);
    }
}
