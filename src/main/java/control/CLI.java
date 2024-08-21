package control;

import model.Role;
import model.SellerStatus;
import model.User;
import view.CLILogin;
import view.CLISignUp;
import view.CLIView;

public class CLI {
    private static User loggedInUser = null;

    private enum AccountChoice {
        SIGN_UP("Sign up"),
        LOGIN("Log in"),
        EXIT("Exit");

        private final String description;
        @Override
        public String toString() {
            return description;
        }
        AccountChoice(String text) {
            this.description = text;
        }
    }

    private enum MainMenuChoice {
        EXIT("Exit");

        private final String description;
        @Override
        public String toString() {
            return description;
        }
        MainMenuChoice(String text) {
            this.description = text;
        }
    }

    public static void main(String[] args) {
        if (!User.adminExists()) {
            while (true) {
                CLIView.warn("No admin account exists. Create one now.");
                CLISignUp signUp = new CLISignUp();
                try {
                    User newAdmin = new User(
                            signUp.username, signUp.firstName, signUp.lastName, signUp.email,
                            signUp.phoneNumber, Role.ADMIN
                    );
                    newAdmin.insert(signUp.password);
                } catch (Exception e) {
                    CLIView.error("Failed to create admin account: " + e.getMessage());
                    continue;
                }

                break;
            }
        }
        assert User.adminExists();

        while (true) {
            while (loggedInUser == null) {
                CLIView.clear();
                handleNoLogin();
                continue;
            }

            CLIView.clear();
            CLIView.info("Logged in as " + loggedInUser.getUsername());
            MainMenuChoice mainMenuChoice = null;
            while (mainMenuChoice == null) {
                try {
                    mainMenuChoice = CLIView.select(
                            MainMenuChoice.class,
                            "What do you want to do?"
                    );
                } catch (IllegalArgumentException e) {
                    CLIView.error(e.getMessage());
                }
            }

            switch (mainMenuChoice) {
                case EXIT -> {
                    System.exit(0);
                }
            }
        }
    }

    private static void handleNoLogin() {
        CLIView.divider("Not logged in");
        AccountChoice accountChoice = null;
        while (accountChoice == null) {
            try {
                accountChoice = CLIView.select(
                        AccountChoice.class,
                        "You need an account. Select an option."
                );
            } catch (IllegalArgumentException e) {
                CLIView.error(e.getMessage());
            }
        }

        switch (accountChoice) {
            case LOGIN -> {
                CLILogin login = new CLILogin();
                try {
                    loggedInUser = User.login(login.username, login.password);
                    CLIView.success("Logged in.");
                } catch (Exception e) {
                    CLIView.error("Failed to login: " + e.getMessage());
                }
            }
            case SIGN_UP -> {
                CLISignUp signUp = new CLISignUp();
                try {
                    User newUser = new User(
                            signUp.username, signUp.firstName, signUp.lastName, signUp.email,
                            signUp.phoneNumber, Role.BUYER
                    );
                    if (signUp.isSeller) {
                        newUser.setSellerStatus(SellerStatus.PENDING);
                        newUser.setCompanyName(signUp.companyName);
                    }
                    newUser.insert(signUp.password);
                } catch (Exception e) {
                    CLIView.error("Failed to create user account: " + e.getMessage());
                }
            }
            case EXIT -> {
                System.exit(0);
            }
        }
    }
}
