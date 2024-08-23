package control;

import model.Category;
import model.Product;
import view.CLIView;

import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;

public class CLIProductData {
    private Product product; 
    private Boolean running = true;
    private Boolean editing = false;
    private Boolean changed = false;

    private enum ProductChoice {
        EDIT_PRODUCT("Edit product", CLIProductData::editProduct),
        DELETE_PRODUCT("Delete product", CLIProductData::deleteProduct),
        BACK("Back", (CLIProductData c) -> c.running = false);

        private final String text;
        private final Consumer<CLIProductData> action;

        ProductChoice(String text, Consumer<CLIProductData> action) {
            this.text = text;
            this.action = action;
        }

        @Override
        public String toString() {
            return text;
        }

        public void perform(CLIProductData c) {
            action.accept(c);
        }
    }

    private enum EditChoice {
        NAME("Change name", CLIProductData::editName),
        PRICE("Change price", CLIProductData::editPrice),
        CATEGORY("Change category", CLIProductData::editCategory),
        SET_PROPERTY("Set property", CLIProductData::setProperty),
        BACK("Back / Save", (CLIProductData c) -> c.editing = false);

        private final String text;
        private final Consumer<CLIProductData> action;

        EditChoice(String text, Consumer<CLIProductData> action) {
            this.text = text;
            this.action = action;
        }

        @Override
        public String toString() {
            return text;
        }

        public void perform(CLIProductData c) {
            action.accept(c);
        }
    }

    private void editProduct() {
        editing = true;
        while (editing) {
            CLIView.select(EditChoice.class, "What do you want to change?").perform(this);
        }

        if (changed) {
            product.requestEdit();
        }
    }

    private void editName() {
        product.setName(CLIView.prompt("Enter new name: "));
        changed = true;
    }

    private void editPrice() {
        product.setPrice(Double.valueOf(CLIView.prompt("Enter new price: ")));
        changed = true;
    }

    private void editCategory() {
        product.setCategory(Category.load(Integer.parseInt(CLIView.prompt("Enter new category ID: ")))); // TODO
        changed = true;
    }

    private void setProperty() {
        product.setProperty(CLIView.prompt("Enter property name: "), CLIView.prompt("Enter property value: ")); // TODO
        changed = true;
    }

    private void deleteProduct() {
        product.delete();
        running = false;
        changed = false;
    }

    public CLIProductData(Product product) {
        this.product = product;

        while (running) {
            try {
                CLIView.divider("View product");
                CLIView.info("ID: " + product.getId());
                CLIView.info("Name: " + product.getName());
                CLIView.info("Price: " + product.getPriceString());
                CLIView.info("Status: " + product.getStatus());
                CLIView.info("Category: " + product.getCategoryName());
                CLIView.info("Seller: " + product.getSeller().getUsername());
                CLIView.info("Inventory status: (IN_STOCK / OUT_OF_STOCK)" + product.getInventoryStatus());
                CLIView.info("Properties: ");
                if (this.product.getValidPropertiesAsMap().isEmpty()) {
                    CLIView.sortedList(
                            this.product.getValidPropertiesAsMap().entrySet().stream().toList(),
                            Comparator.comparing(Map.Entry<String, String>::getKey),
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    );
                }

                CLIView.select(ProductChoice.class, "What do you want to do?").perform(this);
            } catch (Exception e) {
                CLIView.error("Product not found: " + e.getMessage());
            }
        }
    }
}
