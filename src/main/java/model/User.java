package model;

import java.util.ArrayList;

public class User {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private String password;

    private Role role;

    private ArrayList<DiscountCode> discountCodes = new ArrayList<>();
    private Double balance = 0.0;

    private ArrayList<Transaction> transactions = new ArrayList<>();
}

