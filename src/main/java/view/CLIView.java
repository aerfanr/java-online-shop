package view;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class CLIView {
    private static Scanner scanner = null;

    private static final String RESET = "\033[0m";
    private static final String RED = "\033[31m";
    private static final String GREEN = "\033[32m";
    private static final String YELLOW = "\033[33m";
    private static final String BLUE = "\033[34m";

    public static void warn(String message) {
        System.out.println(YELLOW + "WARNING: " + message + RESET);
    }

    public static void error(String message) {
        System.out.println(RED + "ERROR: " + message + RESET);
    }

    public static void success(String message) {
        System.out.println(GREEN + "SUCCESS: " + message + RESET);
    }

    public static void info(String message) {
        System.out.println(BLUE + message + RESET);
    }

    public static String prompt(String message) {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        info(message);
        return scanner.nextLine();
    }

    public static String newPasswordPrompt(String message) {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }
        info(message);
        String newPassword = scanner.nextLine();
        info("Confirm password: ");
        String confirmPassword = scanner.nextLine();
        if (!newPassword.equals(confirmPassword)) {
            error("Passwords do not match. Please try again.");
            return newPasswordPrompt(message);
        }

        if (newPassword.length() < 8) {
            error("Password must be at least 8 characters long. Please try again.");
            return newPasswordPrompt(message);
        }

        return newPassword;
    }

    public static <E extends Enum<E>> E select(Class<E> enumClass, String message) {
        return select(enumClass, message, new ArrayList<>());
    }

    public static <E extends Enum<E>> E select(Class<E> enumClass, String message, ArrayList<E> disabledOptions) {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }

        int choice;
        while (true) {
            info(message);

            int i = 0;
            for (E value : enumClass.getEnumConstants()) {
                i++;
                if (disabledOptions.contains(value)) {
                    continue;
                }

                System.out.println(i + ". " + value);
            }

            newline();
            choice = scanner.nextInt();
            scanner.nextLine();
            if (choice < 1 || choice > enumClass.getEnumConstants().length) {
                error("Invalid choice. Please try again.");
                newline();
                continue;
            }

            break;
        }

        return enumClass.getEnumConstants()[choice - 1];
    }

    public static void newline() {
        System.out.println();
    }

    public static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static final int dividerSize = 40;
    public static void divider(String title) {
        newline();
        if (title.length() > dividerSize) {
            System.out.println(title);
        } else {
            System.out.println("-".repeat((dividerSize - title.length()) / 2 + title.length() % 2)
                            + title
                            + "-".repeat((dividerSize - title.length()) / 2)
            );
        }
    }
    public static void divider() {
        System.out.println("-".repeat(dividerSize));
    }

    @SafeVarargs
    public static <E> void sortedList(List<E> list, Comparator<E> comparator, Function<E, String>... fields) {
        list.sort(comparator);

        int[] lengths = new int[fields.length];
        for (E e : list) {
            for (int i = 0; i < fields.length; i++) {
                int valueLength = fields[i].apply(e).length();
                if (lengths[i] < valueLength) {
                    lengths[i] = valueLength;
                }
            }
        }

        for (E e : list) {
            System.out.print("  ");
            for (int i = 0; i < fields.length; i++) {
                String value = fields[i].apply(e);
                System.out.print(" " + fields[i].apply(e) + " ".repeat(lengths[i] - value.length() + 4));
                if (i != fields.length - 1) {
                    System.out.print("|");
                }
            }
            System.out.println();
        }
        newline();
    }
}
