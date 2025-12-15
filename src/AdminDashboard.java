import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {

    private JLabel lblTotal, lblPending, lblPaid;
    private JTable activityTable;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        // SIDE MENU
        JButton btnDashboard = new JButton("ADMIN DASHBOARD");
        btnDashboard.setBounds(20, 40, 150, 40);
        add(btnDashboard);

        JButton btnViolationForm = new JButton("VIOLATION ENTRY FORM");
        btnViolationForm.setBounds(20, 100, 150, 40);
        add(btnViolationForm);

        JButton btnReports = new JButton("REPORTS");
        btnReports.setBounds(20, 160, 150, 40);
        add(btnReports);

        JButton btnLogin = new JButton("LOGIN PAGE");
        btnLogin.setBounds(20, 220, 150, 40);
        add(btnLogin);

        // TITLE
        JLabel title = new JLabel("ADMIN DASHBOARD");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setBounds(350, 20, 400, 40);
        add(title);

        // STAT BOXES
        lblTotal = createStatBox("TOTAL VIOLATIONS", 50);
        lblPending = createStatBox("PENDING", 280);
        lblPaid = createStatBox("PAID", 510);

        add(lblTotal);
        add(lblPending);
        add(lblPaid);

        // TABLE (UPDATED COLUMN NAME)
        String[] colNames = {"Date/Time", "Violator Name", "Plate Number", "Violation", "Status"};
        DefaultTableModel model = new DefaultTableModel(colNames, 0);
        activityTable = new JTable(model);
        activityTable.setRowHeight(26);

        JScrollPane scroll = new JScrollPane(activityTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Recent Activity"));
        scroll.setBounds(220, 200, 820, 380);
        add(scroll);

        // LOAD DATA
        loadStats();
        loadRecentActivity();

        // BUTTON ACTIONS
        btnViolationForm.addActionListener(e -> {
            dispose();
            new ViolationEntryForm().setVisible(true);
        });

        btnReports.addActionListener(e -> {
            dispose();
            new ReportsPage(false).setVisible(true);
        });

        btnLogin.addActionListener(e -> {
            dispose();
            new LoginPage().setVisible(true);
        });
    }

    // Create stat box
    private JLabel createStatBox(String title, int xPos) {
        JLabel lbl = new JLabel("<html><center>" + title + "<br>0</center></html>", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        lbl.setOpaque(true);
        lbl.setBackground(new Color(240, 240, 240));
        lbl.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        lbl.setBounds(220 + xPos, 100, 200, 80);
        return lbl;
    }

    // Load counters from DB
    private void loadStats() {
        try (Connection con = DBConnection.getConnection()) {

            Statement st = con.createStatement();

            ResultSet total = st.executeQuery("SELECT COUNT(*) FROM violations");
            total.next();
            lblTotal.setText("<html><center>TOTAL VIOLATIONS<br>" + total.getInt(1) + "</center></html>");

            ResultSet pending = st.executeQuery("SELECT COUNT(*) FROM violations WHERE status='Pending'");
            pending.next();
            lblPending.setText("<html><center>PENDING<br>" + pending.getInt(1) + "</center></html>");

            ResultSet paid = st.executeQuery("SELECT COUNT(*) FROM violations WHERE status='Paid'");
            paid.next();
            lblPaid.setText("<html><center>PAID<br>" + paid.getInt(1) + "</center></html>");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load latest activity
    private void loadRecentActivity() {
        DefaultTableModel model = (DefaultTableModel) activityTable.getModel();
        model.setRowCount(0);

        try (Connection con = DBConnection.getConnection()) {

            String sql = """
                    SELECT date_time, violator_name, plate_number, violation_type, status
                    FROM violations
                    ORDER BY date_time DESC
                    LIMIT 20
                """;

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("date_time"),
                        rs.getString("violator_name"),
                        rs.getString("plate_number"),
                        rs.getString("violation_type"),
                        rs.getString("status")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new AdminDashboard().setVisible(true);
    }
}
