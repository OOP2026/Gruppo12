package gui;

import controller.Controller;
import model.Letto;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

public class GestioneLetto {
    private final Controller controller;
    private final JPanel rootPanel;
    private final JTextField repartoField;
    private final DefaultListModel<Letto> lettoListModel;
    private final JList<Letto> lettoList;
    private final JLabel countLabel;
    private final JLabel statusLabel;

    public GestioneLetto() {
        this(new Controller());
    }

    public GestioneLetto(Controller controller) {
        this.controller = controller != null ? controller : new Controller();
        this.rootPanel = new JPanel(new BorderLayout(0, 16));
        this.repartoField = new JTextField();
        this.lettoListModel = new DefaultListModel<>();
        this.lettoList = new JList<>(lettoListModel);
        this.countLabel = new JLabel("0 letti disponibili");
        this.statusLabel = new JLabel("Inserisci il nome di un reparto per avviare la ricerca.");

        configureUi();
        refreshBedList();
    }

    public JComponent getContentPane() {
        return rootPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Ricerca letti per reparto");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                GestioneLetto home = new GestioneLetto();
                frame.setContentPane(home.getContentPane());
                frame.setMinimumSize(new Dimension(760, 480));
                frame.setLocationRelativeTo(null);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    private void configureUi() {
        rootPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        rootPanel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout(0, 6));
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Ricerca rapida letti disponibili");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 22f));

        JLabel subtitleLabel = new JLabel("I letti occupati vengono mostrati in rosso, in tempo reale.");
        subtitleLabel.setForeground(new Color(96, 96, 96));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new BorderLayout(12, 12));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 226, 226)),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));

        JPanel controlsPanel = new JPanel(new GridBagLayout());
        controlsPanel.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 0, 0, 10);
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;

        JLabel repartoLabel = new JLabel("Reparto");
        repartoLabel.setFont(repartoLabel.getFont().deriveFont(Font.BOLD));
        constraints.gridx = 0;
        controlsPanel.add(repartoLabel, constraints);

        repartoField.setColumns(24);
        constraints.gridx = 1;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        controlsPanel.add(repartoField, constraints);

        JButton refreshButton = new JButton("Aggiorna");
        constraints.gridx = 2;
        constraints.weightx = 0.0;
        constraints.fill = GridBagConstraints.NONE;
        controlsPanel.add(refreshButton, constraints);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        infoPanel.setOpaque(false);
        countLabel.setFont(countLabel.getFont().deriveFont(Font.BOLD));
        statusLabel.setForeground(new Color(96, 96, 96));
        infoPanel.add(countLabel);
        infoPanel.add(statusLabel);

        searchPanel.add(controlsPanel, BorderLayout.NORTH);
        searchPanel.add(infoPanel, BorderLayout.SOUTH);

        lettoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lettoList.setVisibleRowCount(10);
        lettoList.setCellRenderer(new ListCellRenderer<Letto>() {
            private final DefaultListCellRenderer delegate = new DefaultListCellRenderer();

            @Override
            public Component getListCellRendererComponent(JList<? extends Letto> list, Letto value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    label.setText("");
                    return label;
                }

                boolean occupato = controller.lettoOccupato(value.getMatricolaLetto());
                String stanza = value.getStanza() != null ? String.valueOf(value.getStanza().getNumeroStanza()) : "?";
                label.setText("Letto: " + value.getMatricolaLetto() + " - stanza " + stanza);

                if (!isSelected) {
                    label.setForeground(occupato ? new Color(180, 0, 0) : new Color(0, 110, 60));
                }

                return label;
            }
        });

        JScrollPane listScrollPane = new JScrollPane(lettoList);
        listScrollPane.setBorder(BorderFactory.createTitledBorder("Letti del reparto"));

        searchPanel.add(listScrollPane, BorderLayout.CENTER);

        rootPanel.add(searchPanel, BorderLayout.CENTER);

        DocumentListener liveSearchListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshBedList();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshBedList();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshBedList();
            }
        };

        repartoField.getDocument().addDocumentListener(liveSearchListener);
        refreshButton.addActionListener(e -> refreshBedList());
    }

    private void refreshBedList() {
        String nomeReparto = repartoField.getText() != null ? repartoField.getText().trim() : "";

        lettoListModel.clear();

        if (nomeReparto.isEmpty()) {
            countLabel.setText("0 letti disponibili");
            statusLabel.setText("Inserisci il nome di un reparto per avviare la ricerca.");
            return;
        }

        List<Letto> letti = controller.cercaLettiPerReparto(nomeReparto);
        for (Letto letto : letti) {
            lettoListModel.addElement(letto);
        }

        List<Letto> disponibili = controller.cercaLettiDisponibili(nomeReparto);
        countLabel.setText(disponibili.size() + " letti disponibili su " + letti.size());

        if (letti.isEmpty()) {
            statusLabel.setText("Nessun letto trovato per il reparto indicato.");
        } else if (disponibili.isEmpty()) {
            statusLabel.setText("Tutti i letti del reparto risultano occupati.");
        } else {
            statusLabel.setText("Aggiornamento automatico attivo.");
        }
    }
}