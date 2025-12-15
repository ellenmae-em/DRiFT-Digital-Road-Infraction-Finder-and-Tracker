public class Launcher {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true);
        });
    }
}
