package view;

import java.util.Scanner;

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
    
    public static <E extends Enum<E>> E select(Class<E> enumClass, String message) {
        if (scanner == null) {
            scanner = new Scanner(System.in);
        }

        int choice;
        while (true) {
            info(message);

            int i = 1;
            for (E value : enumClass.getEnumConstants()) {
                System.out.println(i++ + ". " + value);
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
}
