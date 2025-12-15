import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class ViolationEntryForm extends JFrame {

    private JTextField evidencePathField;

    public ViolationEntryForm() {
        setTitle("Violation Entry Form");
        setSize(650, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // DROPDOWN FOR OFFICER ID
        String[] officerIDs = {"Officer001", "Officer002", "Officer003", "Officer004"};
        JComboBox<String> officerDropdown = new JComboBox<>(officerIDs);

        // DROPDOWN FOR VIOLATION TYPE
        String[] violationTypes = {
                "Speeding",
                "No Helmet",
                "Illegal Parking",
                "Reckless Driving",
                "No License",
                "Wrong Turn",
                "Disregarding Traffic Lights"
        };
        JComboBox<String> violationDropdown = new JComboBox<>(violationTypes);

        // TEXT FIELDS
        JTextField violatorNameField = new JTextField();
        JTextField plateField = new JTextField();
        JTextField licenseField = new JTextField();
        JTextArea notesArea = new JTextArea();

        // REMOVE DATE FIELD — auto timestamp is used
        JLabel dateLabel = new JLabel("Date & Time (Auto):");
        JLabel autoDate = new JLabel("Automatically Generated");

        // EVIDENCE FILE
        JButton attachBtn = new JButton("Attach Evidence");
        evidencePathField = new JTextField();
        evidencePathField.setEditable(false);

        attachBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select Evidence File");

            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selected = chooser.getSelectedFile();
                evidencePathField.setText(selected.getAbsolutePath());

                JOptionPane.showMessageDialog(this,
                        "Evidence Attached:\n" + selected.getName(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton saveBtn = new JButton("SAVE & SUBMIT");
        JButton cancelBtn = new JButton("CANCEL");

        cancelBtn.addActionListener(e -> {
            dispose();
            new AdminDashboard().setVisible(true);
        });

        // =======================
        // SAVE TO DATABASE
        // =======================
        saveBtn.addActionListener(e -> {

            String violatorName = violatorNameField.getText();
            String plate = plateField.getText();
            String license = licenseField.getText();
            String officer = officerDropdown.getSelectedItem().toString();
            String violation = violationDropdown.getSelectedItem().toString();
            String notes = notesArea.getText();
            String evidence = evidencePathField.getText();

            // VALIDATION
            if (violatorName.isEmpty() || plate.isEmpty() || license.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Violator Name, Plate Number, and License ID cannot be empty!",
                        "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {

                String sql = "INSERT INTO violations " +
                        "(violator_name, plate_number, license_id, officer_id, violation_type, date_time, notes, evidence_file) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, violatorName);
                ps.setString(2, plate);
                ps.setString(3, license);
                ps.setString(4, officer);
                ps.setString(5, violation);

                // AUTO TIMESTAMP — NO MORE ERROR
                ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

                ps.setString(7, notes);
                ps.setString(8, evidence);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Violation Submitted Successfully!");

                dispose();
                new AdminDashboard().setVisible(true);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
            }
        });

        // FORM UI
        JPanel form = new JPanel(new GridLayout(11, 2, 10, 10));

        form.add(new JLabel("Violator Name:"));
        form.add(violatorNameField);

        form.add(new JLabel("Plate Number:"));
        form.add(plateField);

        form.add(new JLabel("License ID:"));
        form.add(licenseField);

        form.add(new JLabel("Officer ID:"));
        form.add(officerDropdown);

        form.add(new JLabel("Violation Type:"));
        form.add(violationDropdown);

        form.add(dateLabel);
        form.add(autoDate);

        form.add(new JLabel("Notes / Description:"));
        form.add(new JScrollPane(notesArea));

        form.add(new JLabel("Attach Evidence:"));
        form.add(attachBtn);

        form.add(new JLabel("Selected File:"));
        form.add(evidencePathField);

        JPanel bottom = new JPanel(new GridLayout(1, 2, 10, 10));
        bottom.add(saveBtn);
        bottom.add(cancelBtn);

        setLayout(new BorderLayout(10, 10));
        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }
}
