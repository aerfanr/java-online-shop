package control;

import model.SellerStatus;
import model.User;
import view.CLIView;

import java.util.ArrayList;

public class CLIUserData {
    private enum ProfileChoice {
        EDIT("Edit data"),
        BACK("Back");

        private final String text;

        ProfileChoice(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private enum EditChoice {
        FIRST_NAME("First name"),
        LAST_NAME("Last name"),
        EMAIL("Email"),
        PHONE_NUMBER("Phone number"),
        COMPANY_NAME("Company name"),
        PASSWORD("Password"),
        BACK("Back");

        private final String text;

        EditChoice(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public CLIUserData(User user) {
        while (true) {
            CLIView.divider("User data");
            CLIView.info("Username: " + user.getUsername());
            CLIView.info("First name: " + user.getFirstName());
            CLIView.info("Last name: " + user.getLastName());
            CLIView.info("Email: " + user.getEmail());
            CLIView.info("Phone number: " + user.getPhoneNumber());
            CLIView.info("Balance: " + user.getBalanceString());
            CLIView.info("Role: " + user.getRole());
            if (user.getSellerStatus() != null) {
                CLIView.info("Seller status: " + user.getSellerStatus());
                CLIView.info("Company name: " + user.getCompanyName());
            }
            CLIView.info("Seller status: " + user.getSellerStatus());

            ProfileChoice profileChoice = CLIView.select(
                    ProfileChoice.class,
                    "What do you want to do?"
            );
            switch (profileChoice) {
                case EDIT -> {
                    ArrayList<EditChoice> disabledOptions = new ArrayList<>();
                    if (user.getSellerStatus() == null) {
                        disabledOptions.add(EditChoice.COMPANY_NAME);
                    }
                    EditChoice editChoice = CLIView.select(
                            EditChoice.class,
                            "What do you want to edit?",
                            disabledOptions
                    );
                    switch (editChoice) {
                        case FIRST_NAME -> user.setFirstName(CLIView.prompt("Enter new first name: "));
                        case LAST_NAME -> user.setLastName(CLIView.prompt("Enter new last name: "));
                        case EMAIL -> user.setEmail(CLIView.prompt("Enter new email: "));
                        case PHONE_NUMBER -> user.setPhoneNumber(CLIView.prompt("Enter new phone number: "));
                        case COMPANY_NAME -> user.setCompanyName(CLIView.prompt("Enter new company name: "));
                        case PASSWORD -> user.setPassword(CLIView.newPasswordPrompt("Enter new password: "));

                        case BACK -> {
                            return;
                        }
                    }

                    user.update();
                }
                case BACK -> {
                    return;
                }
            }
        }
    }
}
