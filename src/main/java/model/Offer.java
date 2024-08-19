package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Offer {
    private String id;
    private ArrayList<Product> products = new ArrayList<>();
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private double value;
}
