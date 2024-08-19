package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class DiscountCode {
    private String code;
    private double value;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private double percentage;
    private double maxDiscount;
    private int maxUsage;
    private ArrayList<User> users = new ArrayList<>();
}
