package gui;

import controller.Controller;
import controller.Controller.MedicoSostitutoView;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/**
 * Dashboard dell'amministratore.
 * Espone le operazioni principali su pazienti, ricoveri, dimissioni e sostituzioni.
 */
public class Amministratore {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Controller controller;
    private JPanel panel;
    private JLabel statusLabel;
    private JTextField pazienteMatricolaField;
    private JTextField pazienteNomeField;
    private JTextField pazienteCognomeField;
    private JButton aggiungiPazienteButton;
    private JTextField ricoveroCodiceField;
    private JTextField ricoveroPazienteField;
    private JTextField ricoveroLettoField;
    private JButton registraRicoveroButton;
    private JButton registraDimissioneButton;
    private JTextField dataDimissioneField;
    private JButton cercaDimissioniButton;
    private JList<String> dimissioniList;
    private JButton apriGestioneLettiButton;
    private JButton assegnaMalattiaButton;
    private JButton suggerisciSostitutoButton;
    private JButton logoutButton;

    private DefaultListModel<String> dimissioniListModel;

    public Amministratore() {
        this(new Controller());
    }

    public Amministratore(Controller controller) {
        this.controller = controller != null ? controller : new Controller();
        this.dimissioniListModel = new DefaultListModel<>();


        configureUi();
    }

    public JComponent getContentPane() {
        return panel;
    }

    private void configureUi() {
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        panel.setBackground(new Color(245, 247, 250));
        statusLabel.setForeground(new Color(96, 96, 96));

        dimissioniList.setModel(dimissioniListModel);
        dataDimissioneField.setText(LocalDate.now().format(DATE_FORMATTER));

        aggiungiPazienteButton.addActionListener(e -> aggiungiPaziente());
        registraRicoveroButton.addActionListener(e -> registraRicovero());
        registraDimissioneButton.addActionListener(e -> registraDimissione());
        cercaDimissioniButton.addActionListener(e -> cercaDimissioni());
        apriGestioneLettiButton.addActionListener(e -> apriGestioneLetti());
        assegnaMalattiaButton.addActionListener(e -> assegnaMalattiaMedico());
        suggerisciSostitutoButton.addActionListener(e -> suggerisciSostituto());
        logoutButton.addActionListener(e -> logout());
    }

    private void assegnaMalattiaMedico() {
        String matricola = JOptionPane.showInputDialog(panel, "Matricola medico:");
        if (matricola == null || matricola.trim().isEmpty()) {
            mostraAvviso("Matricola medico non valida.");
            return;
        }

        String idMalattia = JOptionPane.showInputDialog(panel, "ID malattia:");
        if (idMalattia == null || idMalattia.trim().isEmpty()) {
            mostraAvviso("ID malattia non valido.");
            return;
        }

        String inizioStr = JOptionPane.showInputDialog(panel, "Data inizio (gg/mm/aaaa):", LocalDate.now().format(DATE_FORMATTER));
        String fineStr = JOptionPane.showInputDialog(panel, "Data fine (gg/mm/aaaa):", LocalDate.now().plusDays(7).format(DATE_FORMATTER));

        if (inizioStr == null || fineStr == null) {
            mostraAvviso("Operazione annullata.");
            return;
        }

        try {
            LocalDate inizio = LocalDate.parse(inizioStr, DATE_FORMATTER);
            LocalDate fine = LocalDate.parse(fineStr, DATE_FORMATTER);
            LocalDateTime dtInizio = inizio.atStartOfDay();
            LocalDateTime dtFine = fine.atStartOfDay();

            boolean ok = controller.assegnaMalattiaMedico(matricola.trim(), idMalattia.trim(), dtInizio, dtFine);
            if (ok) {
                aggiornaStato("Malattia " + idMalattia + " assegnata a medico " + matricola + ".");
            } else {
                mostraAvviso("Impossibile assegnare la malattia: verifica matricola medico.");
            }
        } catch (DateTimeParseException ex) {
            mostraAvviso("Data non valida. Usa il formato gg/mm/aaaa.");
        }
    }

    private void suggerisciSostituto() {
        String idMalattia = JOptionPane.showInputDialog(panel, "ID malattia:");
        if (idMalattia == null || idMalattia.trim().isEmpty()) {
            mostraAvviso("ID malattia non valido.");
            return;
        }

        List<MedicoSostitutoView> suggeriti = controller.suggerisciSostitutoView(idMalattia.trim());
        if (suggeriti.isEmpty()) {
            mostraAvviso("Nessun sostituto disponibile per la malattia indicata.");
            return;
        }

        DefaultListModel<MedicoSostitutoView> suggerimentiModel = new DefaultListModel<>();
        for (MedicoSostitutoView medico : suggeriti) {
            suggerimentiModel.addElement(medico);
        }

        JList<MedicoSostitutoView> lista = new JList<>(suggerimentiModel);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setVisibleRowCount(Math.min(8, suggerimentiModel.size()));

        JScrollPane scrollPane = new JScrollPane(lista);
        scrollPane.setPreferredSize(new Dimension(380, Math.min(220, 40 + suggerimentiModel.size() * 24)));

        // Mostra solo la lista dei medici disponibili senza effettuare alcuna operazione
        JOptionPane.showMessageDialog(
                panel,
                scrollPane,
                "Medici disponibili per sostituire " + idMalattia.trim(),
                JOptionPane.INFORMATION_MESSAGE
        );

        aggiornaStato("Visualizzati " + suggeriti.size() + " sostituti disponibili per malattia " + idMalattia.trim());
    }
    private void aggiungiPaziente() {
        String matricola = leggi(pazienteMatricolaField);
        String nome = leggi(pazienteNomeField);
        String cognome = leggi(pazienteCognomeField);

        if (matricola.isEmpty() || nome.isEmpty() || cognome.isEmpty()) {
            mostraAvviso("Compila matricola, nome e cognome del paziente.");
            return;
        }

        if (controller.aggiungiPaziente(matricola, nome, cognome)) {
            pazienteMatricolaField.setText("");
            pazienteNomeField.setText("");
            pazienteCognomeField.setText("");
            aggiornaStato("Paziente " + matricola + " aggiunto al sistema.");
        } else {
            mostraAvviso("Matricola paziente gia esistente.");
        }
    }

    private void registraRicovero() {
        String codice = leggi(ricoveroCodiceField);
        String matricolaPaziente = leggi(ricoveroPazienteField);
        String matricolaLetto = leggi(ricoveroLettoField);

        if (codice.isEmpty() || matricolaPaziente.isEmpty() || matricolaLetto.isEmpty()) {
            mostraAvviso("Compila codice ricovero, matricola paziente e matricola letto.");
            return;
        }

        if (controller.aggiungiRicovero(codice, matricolaPaziente, matricolaLetto)) {
            aggiornaStato("Ricovero " + codice + " registrato.");
        } else {
            mostraAvviso("Impossibile registrare il ricovero: verifica paziente e letto.");
        }
    }

    private void registraDimissione() {
        String codice = leggi(ricoveroCodiceField);
        String matricolaPaziente = leggi(ricoveroPazienteField);
        String matricolaLetto = leggi(ricoveroLettoField);

        if (codice.isEmpty() || matricolaPaziente.isEmpty() || matricolaLetto.isEmpty()) {
            mostraAvviso("Compila codice ricovero, matricola paziente e matricola letto.");
            return;
        }

        if (controller.aggiungiDimissione(codice, matricolaPaziente, matricolaLetto)) {
            aggiornaStato("Dimissione registrata per il ricovero " + codice + ".");
        } else {
            mostraAvviso("Impossibile registrare la dimissione.");
        }
    }

    private void cercaDimissioni() {
        String data = leggi(dataDimissioneField);
        if (data.isEmpty()) {
            mostraAvviso("Inserisci una data nel formato gg/mm/aaaa.");
            return;
        }

        dimissioniListModel.clear();

        try {
            LocalDate giorno = LocalDate.parse(data, DATE_FORMATTER);
            List<Controller.RicoveroView> ricoveri = controller.dimissioniInDataView(giorno);

            if (ricoveri.isEmpty()) {
                dimissioniListModel.addElement("Nessuna dimissione prevista per la data indicata.");
            } else {
                for (Controller.RicoveroView ricovero : ricoveri) {
                    dimissioniListModel.addElement(ricovero.toString());
                }
            }
            aggiornaStato("Ricerca dimissioni completata per " + data + ".");
        } catch (DateTimeParseException ex) {
            mostraAvviso("Data non valida. Usa il formato gg/mm/aaaa.");
        }
    }

    private void apriGestioneLetti() {
        GestioneLetto gestioneLetto = new GestioneLetto(controller);
        JFrame frame = new JFrame("Ricerca letti per reparto");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(gestioneLetto.getContentPane());
        frame.setMinimumSize(new Dimension(760, 480));
        frame.pack();
        frame.setLocationRelativeTo(panel);
        frame.setVisible(true);
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(panel, "Tornare alla pagina di login?", "Logout", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        Window currentWindow = SwingUtilities.getWindowAncestor(panel);
        if (currentWindow != null) {
            currentWindow.dispose();
        }

        login.showLogin(controller);
    }

    private String leggi(JTextField field) {
        return field.getText() != null ? field.getText().trim() : "";
    }

    private void aggiornaStato(String messaggio) {
        statusLabel.setText(messaggio);
    }

    private void mostraAvviso(String messaggio) {
        JOptionPane.showMessageDialog(panel, messaggio, "Dashboard amministratore", JOptionPane.WARNING_MESSAGE);
        aggiornaStato(messaggio);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Dashboard amministratore");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                Amministratore amministratoreView = new Amministratore();
                frame.setContentPane(amministratoreView.getContentPane());
                frame.setMinimumSize(new Dimension(860, 560));
                frame.setLocationRelativeTo(null);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), 12, 12));
        panel.setBackground(new Color(-657414));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$("Dialog", Font.BOLD, 22, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Dashboard amministratore");
        panel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        statusLabel = new JLabel();
        statusLabel.setText("Gestisci pazienti, ricoveri, dimissioni, disponibilita letti e sostituti.");
        panel.add(statusLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(10, 10, 10, 10), 8, 8));
        panel.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-1907998)), "Gestione pazienti"));
        final JLabel label2 = new JLabel();
        label2.setText("Matricola");
        panel1.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pazienteMatricolaField = new JTextField();
        panel1.add(pazienteMatricolaField, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(160, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Nome");
        panel1.add(label3, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pazienteNomeField = new JTextField();
        panel1.add(pazienteNomeField, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(160, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Cognome");
        panel1.add(label4, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pazienteCognomeField = new JTextField();
        panel1.add(pazienteCognomeField, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(160, -1), null, 0, false));
        aggiungiPazienteButton = new JButton();
        aggiungiPazienteButton.setText("Aggiungi paziente");
        panel1.add(aggiungiPazienteButton, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(10, 10, 10, 10), 8, 8));
        panel.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-1907998)), "Ricoveri e dimissioni"));
        final JLabel label5 = new JLabel();
        label5.setText("Codice ricovero");
        panel2.add(label5, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ricoveroCodiceField = new JTextField();
        panel2.add(ricoveroCodiceField, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(160, -1), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Matricola paziente");
        panel2.add(label6, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ricoveroPazienteField = new JTextField();
        panel2.add(ricoveroPazienteField, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(160, -1), null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Matricola letto");
        panel2.add(label7, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ricoveroLettoField = new JTextField();
        panel2.add(ricoveroLettoField, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(160, -1), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 8, -1));
        panel2.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        registraRicoveroButton = new JButton();
        registraRicoveroButton.setText("Registra ricovero");
        panel3.add(registraRicoveroButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        registraDimissioneButton = new JButton();
        registraDimissioneButton.setText("Registra dimissione");
        panel3.add(registraDimissioneButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 2, new Insets(10, 10, 10, 10), 8, 8));
        panel.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        panel4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-1907998)), "Dimissioni per data"));
        final JLabel label8 = new JLabel();
        label8.setText("Data");
        panel4.add(label8, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dataDimissioneField = new JTextField();
        panel4.add(dataDimissioneField, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(160, -1), null, 0, false));
        cercaDimissioniButton = new JButton();
        cercaDimissioniButton.setText("Cerca dimissioni");
        panel4.add(cercaDimissioniButton, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dimissioniList = new JList<>();
        panel4.add(dimissioniList, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(240, 100), null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 1, new Insets(10, 10, 10, 10), 8, 8));
        panel.add(panel5, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel5.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-1907998)), "Strumenti"));
        apriGestioneLettiButton = new JButton();
        apriGestioneLettiButton.setText("Ricerca letti disponibili");
        panel5.add(apriGestioneLettiButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        assegnaMalattiaButton = new JButton();
        assegnaMalattiaButton.setText("Assegna malattia a medico");
        panel5.add(assegnaMalattiaButton, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        suggerisciSostitutoButton = new JButton();
        suggerisciSostitutoButton.setText("Suggerisci sostituto");
        panel5.add(suggerisciSostitutoButton, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logoutButton = new JButton();
        logoutButton.setText("Logout");
        panel5.add(logoutButton, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
