package control;

import model.Role;
import model.SellerStatus;
import model.User;
import view.CLISignUp;
import view.CLIView;

import java.util.ArrayList;
import java.util.Comparator;

public class CLIManageUsers {
    private Comparator<User> sortBy = Comparator.comparing(User::getUsername);
    private Boolean reverseSorting = false;

    private enum sortByField {
        USERNAME,
        FIRST_NAME,
        LAST_NAME,
        ROLE,
        SELLER_STATUS;
    }

    private enum Choice {
        CHANGE_SORTING("Change sorting"),
        REVERSE_SORTING("Reverse sorting"),
        VIEW_USER("View user"),
        CHANGE_SELLER_STATUS("Change seller status"),
        DELETE_USER("Delete user"),
        ADD_ADMIN("Create admin profile"),
        BACK("Back");

        private final String description;

        Choice(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    public CLIManageUsers() {
        while (true) {
            ArrayList<User> users = User.getAllUsersBasic();

            CLIView.divider("Manage users");
            CLIView.info("Users list:");
            CLIView.sortedList(
                    users,
                    reverseSorting ? sortBy.reversed() : sortBy,
                    User::getUsername,
                    User::getFirstName,
                    User::getLastName,
                    User::getRoleString,
                    User::getSellerStatusString);

            Choice choice = CLIView.select(Choice.class, "What would you like to do?");

            switch (choice) {
                case CHANGE_SORTING -> {
                    changeSorting();
                }
                case REVERSE_SORTING -> {
                    reverseSorting = !reverseSorting;
                }
                case VIEW_USER -> {
                    viewUser();
                }
                case DELETE_USER -> {
                    String username = CLIView.prompt("Enter username");
                    try {
                        User.deleteByUsername(username);
                        CLIView.success("User deleted.");
                    } catch (Exception e) {
                        CLIView.error("Failed to delete user: " + e.getMessage());
                    }
                }
                case ADD_ADMIN -> {
                    CLISignUp signUp = new CLISignUp();
                    try {
                        User newAdmin = new User(
                                signUp.username, signUp.firstName, signUp.lastName, signUp.email,
                                signUp.phoneNumber, Role.ADMIN
                        );
                        newAdmin.insert(signUp.password);
                    } catch (Exception e) {
                        CLIView.error("Failed to create admin account: " + e.getMessage());
                    }
                }
                case CHANGE_SELLER_STATUS -> {
                    changeSellerStatus();
                }
                case BACK -> {
                    return;
                }
            }
        }
    }

    private void changeSellerStatus() {
        String username = CLIView.prompt("Enter username");
        try {
            User user = User.load(username);
            SellerStatus sellerStatus = CLIView.select(SellerStatus.class, "Select seller status");
            user.setSellerStatus(sellerStatus);
            user.update();
            CLIView.success("User updated.");
        } catch (Exception e) {
            CLIView.error("User not found: " + e.getMessage());
        }
    }

    private void viewUser() {
        String username = CLIView.prompt("Enter username");
        try {
            User user = User.load(username);
            CLIView.divider("User profile");
            CLIView.info("Username: " + user.getUsername());
            CLIView.info("First name: " + user.getFirstName());
            CLIView.info("Last name: " + user.getLastName());
            CLIView.info("Email: " + user.getEmail());
            CLIView.info("Phone number: " + user.getPhoneNumber());
            CLIView.info("Role: " + user.getRoleString());
            if (user.getSellerStatus() != null) {
                CLIView.info("Seller status: " + user.getSellerStatus());
                CLIView.info("Company name: " + user.getCompanyName());
            }
            CLIView.info("Balance: " + user.getBalanceString());
        } catch (Exception e) {
            CLIView.error("User not found: " + e.getMessage());
        }
    }

    private void changeSorting() {
        sortByField choice = CLIView.select(sortByField.class, "Sort by?");
        switch (choice) {
            case USERNAME:
                sortBy = Comparator.comparing(User::getUsername);
                break;
            case FIRST_NAME:
                sortBy = Comparator.comparing(User::getFirstName);
                break;
            case LAST_NAME:
                sortBy = Comparator.comparing(User::getLastName);
                break;
            case ROLE:
                sortBy = Comparator.comparing(User::getRoleString);
                break;
            case SELLER_STATUS:
                sortBy = Comparator.comparing(User::getSellerStatusString);
                break;
        }
        CLIView.info("Sorting by " + choice + " now.");
    }
}
