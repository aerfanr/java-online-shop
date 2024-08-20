package model;

import java.util.ArrayList;

public class Product {
    private String id;
    private ProductStatus status;
    private InventoryStatus inventoryStatus;
    private String name;
    private String brandName;
    private User seller;
    private double price;
    private Category category;
    private ArrayList<Rating> ratings = new ArrayList<>();
    private ArrayList<Comment> comments = new ArrayList<>();
}
