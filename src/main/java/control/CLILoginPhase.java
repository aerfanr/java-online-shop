package control;

import model.Role;
import model.SellerStatus;
import model.User;
import view.CLILogin;
import view.CLISignUp;
import view.CLIView;

public class CLILoginPhase {
    private User loggedInUser = null;

    public User getLoggedInUser() {
        return loggedInUser;
    }

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

    public CLILoginPhase() {
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

        while (this.loggedInUser == null) {
            handleNoLogin();
        }
    }

    private void handleNoLogin() {
        CLIView.divider("Not logged in");
        AccountChoice accountChoice = CLIView.select(
                AccountChoice.class,
                "You need an account. Select an option."
        );

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
