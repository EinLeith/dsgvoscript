import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DSGVO_GUI extends JFrame {

    private JTextField idField;
    private JTextArea outputArea;

    private JTextField vornameField;
    private JTextField nachnameField;
    private JTextField strasseField;
    private JTextField nummerField;
    private JTextField plzField;
    private JTextField ortField;

    public DSGVO_GUI() {
        setTitle("DSGVO Kundenverwaltung");
        setSize(500, 420);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false); // kann man rausmachen aber sieht kacke aus wenns groß ist

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Kundensuche", createSearchPanel());
        tabs.add("Neuer Kunde", createAddPanel());

        add(tabs);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.add(new JLabel("Kunden-ID:"));
        idField = new JTextField(10);
        top.add(idField);



        JButton anzeigenBtn = new JButton("Anzeigen");
        JButton loeschenBtn = new JButton("Löschen");

        top.add(anzeigenBtn);
        top.add(loeschenBtn);

        panel.add(top, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        outputArea.setBorder(BorderFactory.createTitledBorder("Ausgabe"));

        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        anzeigenBtn.addActionListener(e -> kundenDatenAnzeigenBabaProMethode());
        loeschenBtn.addActionListener(e -> kundeYallahLöschen());

        return panel;
    }

    private JPanel createAddPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Neuer Kunde"));

        form.add(new JLabel("Vorname:"));
        vornameField = new JTextField();
        form.add(vornameField);

        form.add(new JLabel("Nachname:"));
        nachnameField = new JTextField();
        form.add(nachnameField);

        form.add(new JLabel("Straße:"));
        strasseField = new JTextField();
        form.add(strasseField);

        form.add(new JLabel("Nr:"));
        nummerField = new JTextField();
        form.add(nummerField);

        form.add(new JLabel("PLZ:"));
        plzField = new JTextField();
        form.add(plzField);

        form.add(new JLabel("Ort:"));
        ortField = new JTextField();
        form.add(ortField);

        panel.add(form, BorderLayout.CENTER);

        JButton addBtn = new JButton("Kunde hinzufügen");
        addBtn.setPreferredSize(new Dimension(180, 40));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(addBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> kundeHinzufuegen());

        return panel;
    }

    private Connection getConnection() throws Exception { // sql mariadb connection
        Class.forName("org.mariadb.jdbc.Driver");
        return DriverManager.getConnection(
                "jdbc:mariadb://localhost:3306/mysql",
                "root",
                "root"
        );
    }

    private void kundenDatenAnzeigenBabaProMethode() {
        try {
            int id = Integer.parseInt(idField.getText().trim());

            String sql = "SELECT * FROM kundendaten WHERE id = ?";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    outputArea.setText(
                            "ID: " + rs.getInt("id") + "\n" +
                                    "Name: " + rs.getString("vorname") + " " +
                                    rs.getString("nachname") + "\n" +
                                    "Adresse: " +
                                    rs.getString("strasse") + " " +
                                    rs.getString("strassennummer") + ", " +
                                    rs.getString("plz") + " " +
                                    rs.getString("ort")
                    );
                } else {
                    outputArea.setText("wallah kunde gibt es nicht");
                }
            }
        } catch (Exception ex) {
            outputArea.setText("Fehler: " + ex.getMessage());
        }
    }

    private void kundeYallahLöschen() {
        try {
            int id = Integer.parseInt(idField.getText().trim());

            String sql = "DELETE FROM kundendaten WHERE id = ?";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);
                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    outputArea.setText("Kunde gelöscht.");
                } else {
                    outputArea.setText("wallah kunde gibts nicht.");
                }
            }
        } catch (Exception ex) {
            outputArea.setText("Fehler: " + ex.getMessage());
        }
    }

    private void kundeHinzufuegen() {
        try {
            String sql = "INSERT INTO kundendaten " +
                    "(vorname, nachname, strasse, strassennummer, plz, ort) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, vornameField.getText().trim());
                stmt.setString(2, nachnameField.getText().trim());
                stmt.setString(3, strasseField.getText().trim());
                stmt.setString(4, nummerField.getText().trim());
                stmt.setString(5, plzField.getText().trim());
                stmt.setString(6, ortField.getText().trim());

                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int neueId = rs.getInt(1);
                    JOptionPane.showMessageDialog(this,
                            "Kunde hinzugefügt.\nNeue ID: " + neueId,
                            "Erfolg",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Fehler: " + ex.getMessage(),
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            DSGVO_GUI app = new DSGVO_GUI();
            app.setVisible(true);
        });
    }
}
