public class TestConnection {
    public static void main(String[] args) {
        if (DBConnection.getConnection() != null) {
            System.out.println("Connected to database successfully!");
        } else {
            System.out.println("Connection failed.");
        }
    }
}
