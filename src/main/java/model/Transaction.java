package model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Transaction {
    private String id;
    private LocalDate date;
    private User buyer;
    private Seller seller;
    private ArrayList<Product> products;
    private double totalPrice;
    private double discountCodePrice;
    private double offerPrice;
    private String total;
    private TransactionStatus status;
}
