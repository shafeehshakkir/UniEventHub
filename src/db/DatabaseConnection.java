package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/UniEventHub?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // change if needed
    private static final String PASSWORD = "Shafeeh@585"; // change to your MySQL password

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }
}