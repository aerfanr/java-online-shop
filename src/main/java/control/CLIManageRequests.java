package control;

import model.Product;
import model.Requestable;
import model.User;
import view.CLIView;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class CLIManageRequests {
    private Boolean running = true;
    private Requestable selected = null;

    private enum CategoryChoice {
        PRODUCT("Product requests", CLIManageRequests::showProductRequests),
        USER("User requests", CLIManageRequests::showUserRequests),
        BACK("Back", (CLIManageRequests c) -> c.running = false);

        private final String description;
        private final Consumer<CLIManageRequests> action;

        CategoryChoice(String description, Consumer<CLIManageRequests> action) {
            this.description = description;
            this.action = action;
        }

        @Override
        public String toString() {
            return description;
        }

        public void perform(CLIManageRequests c) {
            action.accept(c);
        }
    }

    private enum ActionChoice {
        ACCEPT("Accept", CLIManageRequests::accept),
        REJECT("Reject", CLIManageRequests::reject),
        BACK("Back", (CLIManageRequests c) -> c.selected = null);

        private final String description;
        private final Consumer<CLIManageRequests> action;

        ActionChoice(String description, Consumer<CLIManageRequests> action) {
            this.description = description;
            this.action = action;
        }

        @Override
        public String toString() {
            return description;
        }

        public void perform(CLIManageRequests c) {
            action.accept(c);
        }
    }

    private void accept() {
        try {
            selected.accept();
            selected = null;
            CLIView.info("Request accepted");
        } catch (Exception e) {
            CLIView.error("Request accept failed: " + e.getMessage());
        }
    }

    private void reject() {
        try {
            selected.reject();
            selected = null;
            CLIView.info("Request rejected");
        } catch (Exception e) {
            CLIView.error("Request reject failed: " + e.getMessage());
        }
    }

    private  void showProductRequests() {
        List<Product> requests = Product.getRequests();
        if (requests.isEmpty()) {
            CLIView.info("No product requests");
            return;
        }

        CLIView.info("Product requests:");
        CLIView.sortedList(
                requests,
                Comparator.comparing(Product::getName),
                (Product product) -> String.valueOf(product.getId()),
                Product::getName,
                Product::getPriceString,
                Product::getStatus
        );

        try {
            int id = Integer.parseInt(CLIView.prompt("Enter request ID to manage: "));
            selected = Product.load(id);
        } catch (Exception e) {
            CLIView.error("Managing request failed: " + e.getMessage());
        }
    }

    private void showUserRequests() {
        List<User> requests = User.getRequests();
        if (requests.isEmpty()) {
            CLIView.info("No user requests");
            return;
        }

        CLIView.info("User requests:");
        CLIView.sortedList(
                requests,
                Comparator.comparing(User::getUsername),
                User::getUsername
        );

        try {
            String username = CLIView.prompt("Enter request username to manage: ");
            selected = User.load(username);
        } catch (Exception e) {
            CLIView.error("Managing request failed: " + e.getMessage());
        }
    }

    public CLIManageRequests() {
        while (running) {
            if (selected == null) {
                CLIView.divider("Manage requests");
                CLIView.select(CategoryChoice.class, "Choose a request category: ").perform(this);
            } else {
                CLIView.divider("Manage request");
                CLIView.select(ActionChoice.class, "Choose an action: ").perform(this);
            }
        }
    }
}
