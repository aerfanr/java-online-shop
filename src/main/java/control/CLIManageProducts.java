package control;

import model.Product;
import model.Role;
import model.User;
import view.CLIView;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CLIManageProducts {

    private Comparator<Product> sortBy = Comparator.comparing(Product::getName);
    private Boolean reverseSorting = false;
    private int menuLevel = 1;

    private enum sortByField {
        ID(Comparator.comparing(Product::getId)),
        NAME(Comparator.comparing(Product::getName)),
        PRICE(Comparator.comparing(Product::getPrice)),
        STATUS(Comparator.comparing(Product::getStatus));

        private final Comparator<Product> comparator;

        sortByField(Comparator<Product> comparator) {
            this.comparator = comparator;
        }

        public Comparator<Product> getComparator() {
            return comparator;
        }
    }

    private enum Choice {
        SORT_BY("Change sorting", CLIManageProducts::changeSorting),
        REVERSE_SORTING("Reverse sorting", (CLIManageProducts c) -> c.reverseSorting = !c.reverseSorting),
        VIEW_PRODUCT("View and manage product", CLIManageProducts::viewProduct),
        BACK("Back", (CLIManageProducts c) -> c.menuLevel = 0);

        private final String text;
        private final Consumer<CLIManageProducts> action;

        Choice(String text, Consumer<CLIManageProducts> action) {
            this.text = text;
            this.action = action;
        }

        @Override
        public String toString() {
            return text;
        }

        public void perform(CLIManageProducts c) {
            action.accept(c);
        }

    }

    private void viewProduct() {
        String id = CLIView.prompt("Enter product ID");
        int currentMenuLevel = ++menuLevel;

        while (menuLevel == currentMenuLevel) {
            try {
                Product product = Product.load(Integer.parseInt(id));

                CLIView.divider("View product");
                CLIView.info("ID: " + product.getId());
                CLIView.info("Name: " + product.getName());
                CLIView.info("Price: " + product.getPriceString());
                CLIView.info("Status: " + product.getStatus());
                CLIView.info("Category: " + product.getCategoryName());
                CLIView.info("Seller: " + product.getSeller().getUsername());
                CLIView.info("Inventory status: " + product.getInventoryStatus());
                CLIView.info("Properties: ");
                if (product.getValidPropertiesAsMap().isEmpty()) {
                    CLIView.sortedList(
                            product.getValidPropertiesAsMap().entrySet().stream().toList(),
                            Comparator.comparing(Map.Entry<String, String>::getKey),
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    );
                }
            } catch (Exception e) {
                CLIView.error("Product not found: " + e.getMessage());
                menuLevel = currentMenuLevel - 1;
            }

            menuLevel = currentMenuLevel - 1;
        }
    }

    private void changeSorting() {
        this.sortBy = CLIView.select(sortByField.class, "Sort by?").comparator;
    }

    public CLIManageProducts(User user) {
        while (menuLevel == 1) {
            CLIView.divider("Manage products");

            List<Product> products = null;
            if (user.getRole() == Role.ADMIN) {
                products = Product.getAll();
            } else {
                products = Product.getAll(user);
            }
            CLIView.info("Products list:");

            CLIView.sortedList(
                    products,
                    reverseSorting ? sortBy.reversed() : sortBy,
                    (Product product) -> String.valueOf(product.getId()),
                    Product::getName,
                    Product::getPriceString,
                    Product::getStatus
            );

            CLIView.select(Choice.class, "What would you like to do?").perform(this);
        }
    }
}
