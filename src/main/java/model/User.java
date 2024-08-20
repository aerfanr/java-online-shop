package model;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class User {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String companyName;

    private Role role;

    private ArrayList<DiscountCode> discountCodes = new ArrayList<>();
    private Double balance = 0.0;

    private ArrayList<Transaction> transactions = new ArrayList<>();

    public User(String username, String firstName, String lastName, String email, String phoneNumber, Role role) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;

        this.role = role;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void insert(String password) {
        if (
                username == null || username.trim().isEmpty()
                        || password == null
                        || firstName == null
                        || lastName == null
                        || email == null
                        || phoneNumber == null
                        || (role == Role.SELLER && companyName == null)
        ) {
            throw new IllegalArgumentException("Invalid user data");
        }

        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        Connection connection = SQLiteConnection.getConnection();

        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                throw new IllegalArgumentException("Username already exists");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        sql = "INSERT INTO users " +
                " (username, password_hash, first_name, last_name, email, phone_number, role) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?)";
        if (role == Role.SELLER) {
            sql = "INSERT INTO users " +
                    " (username, password_hash, first_name, last_name, email, phone_number, role, company_name) " +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        }
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, passwordHash);
            statement.setString(3, firstName);
            statement.setString(4, lastName);
            statement.setString(5, email);
            statement.setString(6, phoneNumber);
            statement.setString(7, role.name());
            if (role == Role.SELLER) {
                statement.setString(8, companyName);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean adminExists() {
        Connection connection = SQLiteConnection.getConnection();
        String sql = "SELECT * FROM users WHERE role = 'ADMIN'";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static void login(String username, String password) {
        Connection connection = SQLiteConnection.getConnection();

        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                throw new IllegalArgumentException("Invalid username or password");
            }

            String passwordHash = rs.getString("password_hash");
            if (!BCrypt.checkpw(password, passwordHash)) {
                throw new IllegalArgumentException("Invalid username or password");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}