package model;

import org.json.JSONArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Category {

    private int id;
    private String name;
    private List<String> properties;
    private ArrayList<Product> products = new ArrayList<>();

    public Category(String name) {
        this.name = name;
        this.properties = new ArrayList<>();
    }

    public static Category load(int id) {
        Connection connection = SQLiteConnection.getConnection();

        try {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM categories WHERE id = ?");
            statement.setString(1, String.valueOf(id));

            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                throw new IllegalArgumentException("Category not found");
            }

            return fromResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Category fromResultSet(ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        Category category = new Category(name);
        category.id = rs.getInt("id");
        JSONArray properties = new JSONArray(rs.getString("properties"));

        for (int i = 0; i < properties.length(); i++) {
            String property = properties.getString(i);
            category.properties.add(property);
        }

        return category;
    }

    public static List<Category> getAll() {
        Connection connection = SQLiteConnection.getConnection();
        List<Category> categories = new ArrayList<>();

        try {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM categories");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                categories.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return categories;
    }

    public static void deleteById(int id) {
        Connection connection = SQLiteConnection.getConnection();
        Category category = load(id);

        try {
            PreparedStatement statement = connection
                    .prepareStatement("DELETE FROM products WHERE category_id = ?");
            statement.setString(1, String.valueOf(category.id));
            statement.executeUpdate();

            statement = connection
                    .prepareStatement("DELETE FROM categories WHERE id = ?");
            statement.setString(1, String.valueOf(category.id));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void update() {
        Connection connection = SQLiteConnection.getConnection();

        try {
            PreparedStatement statement = connection
                    .prepareStatement("UPDATE categories SET name = ?, properties = ? WHERE id = ?");
            statement.setString(1, name);
            statement.setString(2, new JSONArray(properties).toString());
            statement.setString(3, String.valueOf(id));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addProperty(String property) {
        if (!properties.contains(property)) {
            properties.add(property);
        }
    }

    public void removeProperty(String property) {
        if (!properties.contains(property)) {
            throw new IllegalArgumentException("Property not found");
        }
        properties.remove(property);
    }

    public void insert() {
        Connection connection = SQLiteConnection.getConnection();

        try {
            PreparedStatement statement = connection
                    .prepareStatement("INSERT INTO categories (name, properties) VALUES (?, ?)");
            statement.setString(1, name);
            statement.setString(2, new JSONArray(properties).toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
