package control;

import model.Category;
import view.CLIView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CLIManageCategories {
    private Boolean reverseSorting = false;
    private Boolean readOnly = false;

    private enum Choice {
        REVERSE_SORTING("Reverse sorting"),
        VIEW_CATEGORY("View category"),
        ADD_CATEGORY("Add category"),
        DELETE_CATEGORY("Delete category"),
        EXIT("Exit");

        private final String description;

        Choice(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    public CLIManageCategories(Boolean readOnly) {
        this.readOnly = readOnly;
        while (true) {
            CLIView.divider("Manage categories");
            CLIView.info("Categories list:");
            List<Category> categories = Category.getAll();

            CLIView.sortedList(
                    categories,
                    reverseSorting ? Comparator.comparing(Category::getName).reversed() : Comparator.comparing(Category::getName),
                    (Category category) -> String.valueOf(category.getId()),
                    Category::getName
            );

            Choice choice;
            if (!readOnly) {
                choice = CLIView.select(Choice.class, "What would you like to do?");
            } else {
                ArrayList<Choice> disabledChoices = new ArrayList<>();
                disabledChoices.add(Choice.ADD_CATEGORY);
                disabledChoices.add(Choice.DELETE_CATEGORY);
                choice = CLIView.select(Choice.class, "What would you like to do?", disabledChoices);
            }

            switch (choice) {
                case REVERSE_SORTING -> {
                    reverseSorting = !reverseSorting;
                }
                case VIEW_CATEGORY -> {
                    viewCategory();
                }
                case ADD_CATEGORY -> {
                    addCategory();
                }
                case DELETE_CATEGORY -> {
                    deleteCategory();
                }
                case EXIT -> {
                    return;
                }
            }
        }
    }

    private void deleteCategory() {
        int id = Integer.parseInt(CLIView.prompt("Enter category id: "));
        try {
            Category.deleteById(id);
        } catch (Exception e) {
            CLIView.error("Failed to delete category: " + e.getMessage());
        }
        CLIView.success("Category deleted successfully");
    }

    private void addCategory() {
        String name = CLIView.prompt("Enter new category name: ");
        try {
            Category category = new Category(name);
            category.insert();
        } catch (Exception e) {
            CLIView.error("Failed to add category: " + e.getMessage());
        }
        CLIView.success("Category added successfully");
    }

    private enum EditChoice {
        NAME("Edit name"),
        ADD_PROPERTY("Add property"),
        REMOVE_PROPERTY("Remove property"),
        BACK("Back");

        private final String description;

        EditChoice(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }

    private void viewCategory() {
        int id = Integer.parseInt(CLIView.prompt("Enter category id"));

        while (true) {
            try {
                Category category = Category.load(id);

                CLIView.divider("Category info");
                CLIView.info("Category id: " + category.getId());
                CLIView.info("Category name: " + category.getName());
                CLIView.info("Category properties: ");
                CLIView.sortedList(category.getProperties(), Comparator.comparing(String::toString), String::toString);

                if (readOnly) break;

                EditChoice editChoice = CLIView.select(EditChoice.class, "What do you want to do?");
                switch (editChoice) {
                    case NAME -> {
                        String name = CLIView.prompt("Enter new name: ");
                        category.setName(name);
                        category.update();
                    }
                    case ADD_PROPERTY -> {
                        String property = CLIView.prompt("Enter property: ");
                        category.addProperty(property);
                        category.update();
                    }
                    case REMOVE_PROPERTY -> {
                        String property = CLIView.prompt("Enter property: ");
                        category.removeProperty(property);
                        category.update();
                    }
                    case BACK -> {
                        return;
                    }
                }
            } catch (Exception e) {
                CLIView.error("Failed to edit category: " + e.getMessage());
            }
        }
    }
}