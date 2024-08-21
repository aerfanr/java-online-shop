package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteConnection {
    public static Connection connection = null;

    private static final String URL = "jdbc:sqlite:database.db";

    private SQLiteConnection() {}

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL);
            } catch (Exception e) {
                System.err.println("Connection to database failed: " + e.getMessage());
                System.exit(1);
            }
        }

        updateSchema();

        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                System.err.println("Closing connection failed: " + e.getMessage());
            }
        }
    }

    private static void updateSchema() {
        try {
            ResultSet rs = connection.createStatement().executeQuery("PRAGMA user_version");
            if (!rs.next()) {
                throw new SQLException("User version not found");
            }
            int currentVersion = rs.getInt(1);

            if (currentVersion < 1) {
                connection.createStatement().executeUpdate(
                        "CREATE TABLE IF NOT EXISTS users"
                        + " (id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + " username TEXT NOT NULL UNIQUE,"
                        + " password_hash TEXT NOT NULL,"
                        + " first_name TEXT NOT NULL,"
                        + " last_name TEXT NOT NULL,"
                        + " email TEXT NOT NULL,"
                        + " phone_number TEXT NOT NULL,"
                        + " role TEXT NOT NULL,"
                        + " company_name TEXT, "
                        + " balance DOUBLE NOT NULL DEFAULT 0.0)"
                );
            }
            if (currentVersion < 2) {
                connection.createStatement().executeUpdate(
                        "ALTER TABLE users ADD COLUMN seller_status TEXT"
                );
            }

            connection.createStatement().executeUpdate("PRAGMA user_version = " + 2);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
