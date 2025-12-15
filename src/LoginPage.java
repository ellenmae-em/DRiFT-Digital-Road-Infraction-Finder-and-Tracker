import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginPage extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginPage() {

        setTitle("DRiFT Login");
        setSize(420, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("DRiFT: Digital Road Infraction Filing & Tracking System");
        title.setBounds(10, 5, 400, 20);
        panel.add(title);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(20, 50, 100, 20);
        panel.add(userLabel);

        txtUsername = new JTextField();
        txtUsername.setBounds(20, 70, 360, 25);
        panel.add(txtUsername);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(20, 110, 100, 20);
        panel.add(passLabel);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(20, 130, 360, 25);
        panel.add(txtPassword);

        JButton btnAdmin = new JButton("ADMIN LOGIN");
        btnAdmin.setBounds(20, 170, 360, 25);
        panel.add(btnAdmin);

        JButton btnGuest = new JButton("GUEST MODE");
        btnGuest.setBounds(20, 200, 360, 25);
        panel.add(btnGuest);

        JButton btnForgot = new JButton("Forgot Password?");
        btnForgot.setBounds(20, 230, 360, 25);
        panel.add(btnForgot);

        add(panel);

        // -----------------------------
        // DATABASE-BASED ADMIN LOGIN
        // -----------------------------
        btnAdmin.addActionListener(e -> {
            String user = txtUsername.getText();
            String pass = new String(txtPassword.getPassword());

            if (checkLogin(user, pass)) {
                dispose();
                new AdminDashboard().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Invalid username or password.");
            }
        });

        // GUEST MODE
        btnGuest.addActionListener(e -> {
            new ReportsPage(true).setVisible(true);
            this.dispose();
        });
    }

    // -----------------------------
    // LOGIN CHECK FROM DATABASE
    // -----------------------------
    private boolean checkLogin(String user, String pass) {
        Connection conn = DBConnection.getConnection();

        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Database connection failed.");
            return false;
        }

        try {
            String sql = "SELECT * FROM users WHERE username=? AND password=? AND role='admin'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();

            return rs.next(); // TRUE if a matching row exists

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
