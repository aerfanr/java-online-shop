package model;

import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Product implements Requestable {
    private int id;
    private ProductStatus status;
    private InventoryStatus inventoryStatus;
    private String name;
    private String brandName;
    private String description;
    private User seller;
    private double price;
    private Category category;
//    private ArrayList<Rating> ratings = new ArrayList<>(); // TODO
//    private ArrayList<Comment> comments = new ArrayList<>(); // TODO
    private Map<String, String> properties = Map.of();
    private int ref;

    public Product(
            int id,
            ProductStatus status,
            InventoryStatus inventoryStatus,
            String name,
            String brandName,
            String description,
            User seller,
            double price,
            Category category,
            Map<String, String> properties
    ) {
        this.id = id;
        this.status = status;
        this.inventoryStatus = inventoryStatus;
        this.name = name;
        this.brandName = brandName;
        this.description = description;
        this.seller = seller;
        this.price = price;
        this.category = category;
        this.properties = properties;
    }

    public static Product load(int id) {
        Connection connection = SQLiteConnection.getConnection();
        String sql = "SELECT * FROM products WHERE id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, String.valueOf(id));
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                throw new IllegalArgumentException("Product not found");
            }
            return fromResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Product fromResultSet(ResultSet rs) throws SQLException {
        JSONObject properties = new JSONObject(rs.getString("properties"));
        Map<String, String> propertiesMap = new HashMap<>();
        for (String key : properties.keySet()) {
            propertiesMap.put(key, properties.getString(key));
        }
        Product product = new Product(
                rs.getInt("id"),
                ProductStatus.valueOf(rs.getString("status")),
                InventoryStatus.valueOf(rs.getString("inventory")),
                rs.getString("name"),
                rs.getString("brand_name"),
                rs.getString("description"),
                User.load(rs.getString("seller")),
                rs.getDouble("price"),
                Category.load(rs.getInt("category_id")),
                propertiesMap
        );
        product.ref = rs.getInt("ref");
        return product;
    }

    public static List<Product> getAll() {
        Connection connection = SQLiteConnection.getConnection();
        String sql = "SELECT * FROM products";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                products.add(fromResultSet(rs));
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Product> getAll(User seller) {
        Connection connection = SQLiteConnection.getConnection();
        String sql = "SELECT * FROM products WHERE seller = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, seller.getUsername());
            ResultSet rs = statement.executeQuery();
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                products.add(fromResultSet(rs));
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Product> getRequests() {
        Connection connection = SQLiteConnection.getConnection();
        try {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM products WHERE status = ? OR status = ?");
            statement.setString(1, ProductStatus.CREATION_VERIFICATION.toString());
            statement.setString(2, ProductStatus.MODIFICATION_VERIFICATION.toString());
            ResultSet rs = statement.executeQuery();
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                products.add(fromResultSet(rs));
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status.toString();
    }

    public Double getPrice() {
        return price;
    }

    public String getPriceString() {
        return String.format("$%.2f", price);
    }

    public String getInventoryStatus() {
        return inventoryStatus.toString();
    }

    public String getBrandName() {
        return brandName;
    }

    public Category getCategory() {
        return category;
    }

    public String getCategoryName() {
        return category.getName();
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public Map<String, String> getValidPropertiesAsMap() {
        Map<String, String> validProperties = new HashMap<>();
        for (String key : category.getProperties()) {
            validProperties.put(key, properties.getOrDefault(key, null));
        }
        return validProperties;
    }

    public User getSeller() {
        return seller;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setProperty(String key, String value) {
        if (category.getProperties().contains(key)) {
            properties.put(key, value);
        } else {
            throw new IllegalArgumentException("Invalid property");
        }
    }

    public void delete() {
        Connection connection = SQLiteConnection.getConnection();
        try {
            String sql = "DELETE FROM products WHERE ref = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, String.valueOf(id));
            statement.executeUpdate();

            sql = "DELETE FROM products WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, String.valueOf(id));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert() {
        Connection connection = SQLiteConnection.getConnection();

        try {
            String sql = "INSERT INTO products " +
                    "(status, inventory, name, brand_name, seller, price, category_id, properties, ref, description) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, status.toString());
            statement.setString(2, inventoryStatus.toString());
            statement.setString(3, name);
            statement.setString(4, brandName);
            statement.setString(5, seller.getUsername());
            statement.setDouble(6, price);
            statement.setInt(7, category.getId());
            statement.setString(8, new JSONObject(properties).toString());
            if (ref == 0) {
                statement.setNull(9, Types.INTEGER);
            } else {
                statement.setInt(9, ref);
            }
            statement.setString(10, description);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void requestEdit() {
        ref = id;
        status = ProductStatus.MODIFICATION_VERIFICATION;
        this.insert();
    }

    public void requestInsert() {
        ref = 0;
        status = ProductStatus.CREATION_VERIFICATION;
        this.insert();
    }

    public void accept() {
        if (status == ProductStatus.VERIFIED) {
            throw new IllegalArgumentException("Product already verified");
        }

        if (status == ProductStatus.MODIFICATION_VERIFICATION) {
            try {
                String sql = "UPDATE products SET " +
                        "status = ?, " +
                        "inventory = ?," +
                        "name = ?, " +
                        "brand_name = ?, " +
                        "seller = ?, " +
                        "price = ?, " +
                        "category_id = ?, " +
                        "properties = ?, " +
                        "ref = ?, " +
                        "description = ? " +
                        "WHERE id = ?";

                Connection connection = SQLiteConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, ProductStatus.VERIFIED.toString());
                statement.setString(2, inventoryStatus.toString());
                statement.setString(3, name);
                statement.setString(4, brandName);
                statement.setString(5, seller.getUsername());
                statement.setDouble(6, price);
                statement.setInt(7, category.getId());
                statement.setString(8, new JSONObject(properties).toString());
                statement.setNull(9, Types.INTEGER);
                statement.setString(10, description);
                statement.setInt(11, ref);
                statement.executeUpdate();

                delete();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            status = ProductStatus.VERIFIED;
            try {
                String sql = "UPDATE products SET status = ? WHERE id = ?";
                Connection connection = SQLiteConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, status.toString());
                statement.setInt(2, id);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void reject() {
        if (status == ProductStatus.VERIFIED) {
            throw new IllegalArgumentException("Product already verified");
        }

        delete();
    }
}
