package model;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class User implements Requestable {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String companyName;

    private Role role;
    private SellerStatus sellerStatus;

    private ArrayList<DiscountCode> discountCodes = new ArrayList<>();
    private Double balance = 0.0;

    private ArrayList<Transaction> transactions = new ArrayList<>();

    public User(
            String username,
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            Role role
    ) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.balance = 0.0;

        this.role = role;
    }

    public static void deleteByUsername(String username) {
        Connection connection = SQLiteConnection.getConnection();
        String sql = "DELETE FROM users WHERE username = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setSellerStatus(SellerStatus sellerStatus) {
        if (sellerStatus == null) {
            this.sellerStatus = null;
            return;
        }
        assert role != Role.ADMIN;
        this.sellerStatus = sellerStatus;

        if (sellerStatus == SellerStatus.APPROVED) {
            this.role = Role.SELLER;
        } else {
            this.role = Role.BUYER;
        }
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

        String passwordHash = hashPassword(password);

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
                " (username, password_hash, first_name, last_name, email, phone_number, role, company_name, seller_status, balance) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, passwordHash);
            statement.setString(3, firstName);
            statement.setString(4, lastName);
            statement.setString(5, email);
            statement.setString(6, phoneNumber);
            statement.setString(7, role.name());
            statement.setString(8, companyName);
            if (sellerStatus == null) {
                statement.setNull(9, java.sql.Types.VARCHAR);
            } else {
                statement.setString(9, sellerStatus.name());
            }
            statement.setDouble(10, balance);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String hashPassword(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        return BCrypt.hashpw(password, BCrypt.gensalt());
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

    public static User login(String username, String password) {
        Connection connection = SQLiteConnection.getConnection();

        String sql = "SELECT password_hash FROM users WHERE username = ?";
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

        return load(username);
    }

    public static User load(String username) {
        Connection connection = SQLiteConnection.getConnection();

        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                throw new IllegalArgumentException("User not found");
            }
            return fromResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static User fromResultSet(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getString("username"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("phone_number"),
                Role.valueOf(rs.getString("role"))
        );
        user.setCompanyName(rs.getString("company_name"));
        if (rs.getString("seller_status") != null) {
            user.setSellerStatus(SellerStatus.valueOf(rs.getString("seller_status")));
        }
        user.setBalance(rs.getDouble("balance"));
        return user;
    }

    public static ArrayList<String> getAllUsernames() {
        Connection connection = SQLiteConnection.getConnection();
        String sql = "SELECT username FROM users";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            ArrayList<String> usernames = new ArrayList<>();
            while (rs.next()) {
                usernames.add(rs.getString("username"));
            }
            return usernames;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<User> getAllUsersBasic() {
        Connection connection = SQLiteConnection.getConnection();
        String sql = "SELECT * FROM users";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            ArrayList<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(fromResultSet(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update() {
        Connection connection = SQLiteConnection.getConnection();
        String sql = "UPDATE users " +
                "SET first_name = ?, " +
                "last_name = ?, " +
                "email = ?, " +
                "phone_number = ?, " +
                "company_name = ?, " +
                "seller_status = ?, " +
                "balance = ?, " +
                "role = ? " +
                "WHERE username = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            statement.setString(4, phoneNumber);
            statement.setString(5, companyName);
            statement.setString(6, sellerStatus == null ? null : sellerStatus.toString());
            statement.setDouble(7, balance);
            statement.setString(8, role.toString());
            statement.setString(9, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPassword(String password) {
        String passwordHash = hashPassword(password);
        Connection connection = SQLiteConnection.getConnection();
        String sql = "UPDATE users SET password_hash = ? WHERE username = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, passwordHash);
            statement.setString(2, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public String getRoleString() {
        return role.toString();
    }

    public String getCompanyName() {
        return companyName;
    }

    public SellerStatus getSellerStatus() {
        return sellerStatus;
    }

    public String getSellerStatusString() {
        return sellerStatus == null ? "Not requested" : sellerStatus.toString();
    }

    public double getBalance() {
        return balance;
    }

    public String getBalanceString() {
        return String.format("$%.2f", balance);
    }

    public void accept() {
        this.sellerStatus = SellerStatus.APPROVED;
        this.role = Role.SELLER;
        update();
    }

    public void reject() {
        this.sellerStatus = SellerStatus.REJECTED;
        update();
    }

    public static List<User> getRequests() {
        Connection connection = SQLiteConnection.getConnection();
        try {
            String sql = "SELECT * FROM users WHERE seller_status = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, SellerStatus.PENDING.toString());
            ResultSet rs = statement.executeQuery();
            ArrayList<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(fromResultSet(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}