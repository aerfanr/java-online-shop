package control;

import model.User;
import view.CLIView;

public class CLIProfilePhase {

    private enum ProfileChoice {
        USER_DATA("View and manage user data"),
        CART("View and manage shopping cart"),
        TRANSACTIONS("Past transactions"),
        BALANCE("Current balance"),
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
            ProfileChoice profileChoice = CLIView.select(
                    ProfileChoice.class,
                    "What do you want to do?"
            );
            switch (profileChoice) {
                case USER_DATA -> new CLIUserData(user);
                case CART -> CLIView.error("Not implemented yet");
                case BALANCE -> CLIView.info("Balance: " + user.getBalanceString());
                case TRANSACTIONS -> CLIView.error("Not implemented yet.");
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
