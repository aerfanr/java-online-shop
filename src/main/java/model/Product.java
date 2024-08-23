package model;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Product {
    private int id;
    private ProductStatus status;
    private InventoryStatus inventoryStatus;
    private String name;
    private String brandName;
    private User seller;
    private double price;
    private Category category;
//    private ArrayList<Rating> ratings = new ArrayList<>(); // TODO
//    private ArrayList<Comment> comments = new ArrayList<>(); // TODO
    private Map<String, String> properties = Map.of();

    public Product(
            int id,
            ProductStatus status,
            InventoryStatus inventoryStatus,
            String name,
            String brandName,
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
        return new Product(
                rs.getInt("id"),
                ProductStatus.valueOf(rs.getString("status")),
                InventoryStatus.valueOf(rs.getString("inventory")),
                rs.getString("name"),
                rs.getString("brand_name"),
                User.load(rs.getString("seller")),
                rs.getDouble("price"),
                Category.load(rs.getInt("category_id")),
                propertiesMap
        );
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
}
