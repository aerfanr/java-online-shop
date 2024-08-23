package control;

import model.*;
import view.CLIView;

import java.util.*;
import java.util.function.Consumer;

public class CLIManageProducts {

    private User loggedInUser;
    private Comparator<Product> sortBy = Comparator.comparing(Product::getName);
    private Boolean reverseSorting = false;
    private Boolean running = true;

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
        ADD_PRODUCT("Add new product", CLIManageProducts::addProduct),
        BACK("Back", (CLIManageProducts c) -> c.running = false);

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

    private void addProduct() {
        try {
            String name = CLIView.prompt("Name");
            String brandName = CLIView.prompt("Brand name");
            String description = CLIView.prompt("Description");
            String price = CLIView.prompt("Price");
            Category category = Category.load(Integer.parseInt(CLIView.prompt("Category ID")));

            HashMap<String, String> properties = new HashMap<>();
            for (String key : category.getProperties()) {
                properties.put(key, CLIView.prompt(key));
            }

            User seller;
            if (loggedInUser.getRole() == Role.ADMIN) {
                seller = User.load(CLIView.prompt("Seller"));
            } else {
                seller = loggedInUser;
            }

            Product product = new Product(
                    0,
                    ProductStatus.CREATION_VERIFICATION,
                    InventoryStatus.IN_STOCK,
                    name,
                    brandName,
                    description,
                    seller,
                    Double.parseDouble(price),
                    category,
                    properties
            );

            product.requestInsert();
        } catch (Exception e) {
            CLIView.error("Adding product failed: " + e.getMessage());
        }
    }

    private void viewProduct() {
       int id = Integer.parseInt(CLIView.prompt("Enter product ID"));
       try {
           Product product = Product.load(id);
           if (!Objects.equals(product.getSeller().getUsername(), loggedInUser.getUsername())
                           && loggedInUser.getRole() != Role.ADMIN) {
               throw new Exception("You do not have permission to manage this product");
           }
           new CLIProductData(product);
       } catch (Exception e) {
           CLIView.error("Managing product failed: " + e.getMessage());
       }
    }

    private void changeSorting() {
        this.sortBy = CLIView.select(sortByField.class, "Sort by?").comparator;
    }

    public CLIManageProducts(User user) {
        this.loggedInUser = user;

        while (running) {
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
