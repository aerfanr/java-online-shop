package control;

import model.Role;
import model.User;
import view.CLIView;

import java.util.ArrayList;

public class CLIProfilePhase {

    private enum ProfileChoice {
        USER_DATA("View and manage user data"),
        CART("View and manage shopping cart"),
        TRANSACTIONS("Past transactions"),
        BALANCE("Current balance"),
        MANAGE_USERS("Manage users"),
        MANAGE_CATEGORIES("Manage categories"),
        MANAGE_PRODUCTS("Manage products"),
        LOGOUT("Log out"),
        EXIT("Exit"),
        BACK("Back");

        private final String description;

        @Override
        public String toString() {
            return description;
        }

        ProfileChoice(String desc) {
            this.description = desc;
        }
    }

    public CLIProfilePhase(User user) throws LoggedOutExeption, ExitExeption {
        while (true) {
            CLIView.divider("User profile");
            CLIView.info("Logged in as " + user.getUsername());

            ArrayList<ProfileChoice> disabledOptions = new ArrayList<>();
            if (user.getRole() != Role.ADMIN) {
                disabledOptions.add(ProfileChoice.MANAGE_USERS);
                disabledOptions.add(ProfileChoice.MANAGE_CATEGORIES);
            }
            if (user.getRole() == Role.BUYER) {
                disabledOptions.add(ProfileChoice.MANAGE_PRODUCTS);
            }

            ProfileChoice profileChoice = CLIView.select(
                    ProfileChoice.class,
                    "What do you want to do?",
                    disabledOptions
            );
            switch (profileChoice) {
                case USER_DATA -> new CLIUserData(user);
                case CART -> CLIView.error("Not implemented yet");
                case BALANCE -> CLIView.info("Balance: " + user.getBalanceString());
                case TRANSACTIONS -> CLIView.error("Not implemented yet.");
                case MANAGE_USERS -> new CLIManageUsers();
                case MANAGE_CATEGORIES -> new CLIManageCategories();
                case MANAGE_PRODUCTS -> new CLIManageProducts(user);
                case LOGOUT -> {
                    throw new LoggedOutExeption();
                }
                case EXIT -> {
                    throw new ExitExeption();
                }
                case BACK -> {
                    return;
                }
            }
        }
    }
}
