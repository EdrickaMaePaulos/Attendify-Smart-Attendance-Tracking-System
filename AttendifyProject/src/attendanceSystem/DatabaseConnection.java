package attendanceSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    
    private static final String URL = "jdbc:mysql://localhost:3306/attendifydatabase";  // Update with your DB name
    private static final String USER = "EdrickaMae";  // Your DB username
    private static final String PASSWORD = "100013433675001";  // Your DB password

    // Static method to establish the connection
    public static Connection connect() {
    	final String ITALIC = "\033[3m";
        final String RESET = "\033[0m";
        Connection connection = null;
        try {
            // Try to connect to the database
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println(ITALIC + "\t\t\t\t\t\t\t(Database connected successfully.)" + RESET);
        } catch (SQLException e) {
            System.out.println(ITALIC + "\t\t\t\tError connecting to the database: " + e.getMessage() + RESET);
        }
        return connection;
    }

    // Static method to verify the connection
    public static boolean verifyConnection(Connection connection) {
    	final String ITALIC = "\033[3m";
        final String RESET = "\033[0m";
        try (Statement statement = connection.createStatement()) {
            // Try to execute a simple query to check the connection
            var resultSet = statement.executeQuery("SELECT 1");
            if (resultSet.next()) {
                System.out.println(ITALIC + "\t\t\t\t\t\t\t\t(Database connection is verified.)" + RESET);
                return true;
            }
        } catch (SQLException e) {
            System.out.println(ITALIC + "\t\t\t\tError verifying the database connection: " + e.getMessage() + RESET);
        }
        return false;
    }

    // Static method to close the connection
    public static void closeConnection(Connection connection) {
    	final String ITALIC = "\033[3m";
        final String RESET = "\033[0m";
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println(ITALIC + "\t\t\t\t\t\t\t\t(Database connection closed.)" + RESET);
            }
        } catch (SQLException e) {
            System.out.println(ITALIC + "\t\t\t\tError closing the database connection: " + e.getMessage()+ RESET);
        }
    }
}
