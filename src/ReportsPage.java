import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ReportsPage extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public ReportsPage(boolean isGuest) {

        setTitle("DRIFT - Violation Reports");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // -------------------------------------------------------
        // TABLE MODEL
        // -------------------------------------------------------
        model = new DefaultTableModel();

        model.addColumn("ID");                 // 🔒 Hidden real ID
        model.addColumn("No.");                // 👁 Display number
        model.addColumn("Violator Name");
        model.addColumn("Plate Number");
        model.addColumn("License ID");
        model.addColumn("Officer ID");
        model.addColumn("Violation Type");
        model.addColumn("Date & Time");
        model.addColumn("Notes");
        model.addColumn("Evidence File");
        model.addColumn("Status");

        table = new JTable(model);
        table.setRowHeight(25);

        // 🔒 Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);

        // -------------------------------------------------------
        // BUTTONS PANEL
        // -------------------------------------------------------
        JPanel bottomPanel = new JPanel();

        JButton backBtn;
        JButton deleteBtn = new JButton("DELETE SELECTED");
        JButton updateStatusBtn = new JButton("UPDATE STATUS");

        if (isGuest) {
            backBtn = new JButton("Back to Login");
            backBtn.addActionListener(e -> {
                dispose();
                new LoginPage().setVisible(true);
            });
            bottomPanel.add(backBtn);

        } else {
            deleteBtn.addActionListener(e -> deleteViolation());
            updateStatusBtn.addActionListener(e -> updateStatus());

            backBtn = new JButton("Back to Dashboard");
            backBtn.addActionListener(e -> {
                dispose();
                new AdminDashboard().setVisible(true);
            });

            bottomPanel.add(deleteBtn);
            bottomPanel.add(updateStatusBtn);
            bottomPanel.add(backBtn);
        }

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadViolations();
    }

    // -------------------------------------------------------
    // LOAD FROM DATABASE
    // -------------------------------------------------------
    private void loadViolations() {

        model.setRowCount(0);
        int rowNumber = 1;

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "SELECT * FROM violations ORDER BY date_time DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),           // 🔒 hidden ID
                        rowNumber++,               // 👁 1,2,3...
                        rs.getString("violator_name"),
                        rs.getString("plate_number"),
                        rs.getString("license_id"),
                        rs.getString("officer_id"),
                        rs.getString("violation_type"),
                        rs.getString("date_time"),
                        rs.getString("notes"),
                        rs.getString("evidence_file"),
                        rs.getString("status")
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data!");
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------
    // DELETE SELECTED
    // -------------------------------------------------------
    private void deleteViolation() {

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0); // 🔒 real ID

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this record?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "DELETE FROM violations WHERE id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            pst.executeUpdate();

            loadViolations();
            JOptionPane.showMessageDialog(this, "Record deleted successfully!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting record!");
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------
    // UPDATE STATUS
    // -------------------------------------------------------
    private void updateStatus() {

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0); // 🔒 real ID

        String[] statuses = {"PAID", "PENDING"};
        JComboBox<String> statusDropdown = new JComboBox<>(statuses);

        int option = JOptionPane.showConfirmDialog(
                this,
                statusDropdown,
                "Select new status:",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option != JOptionPane.OK_OPTION) return;

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "UPDATE violations SET status=? WHERE id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, statusDropdown.getSelectedItem().toString());
            pst.setInt(2, id);
            pst.executeUpdate();

            loadViolations();
            JOptionPane.showMessageDialog(this, "Status updated!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating status!");
            e.printStackTrace();
        }
    }
}
