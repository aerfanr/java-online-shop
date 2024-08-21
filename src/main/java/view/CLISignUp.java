package view;

public class CLISignUp {
    public String username;
    public String password;
    public String firstName;
    public String lastName;
    public String email;
    public String phoneNumber;
    public String companyName;

    public boolean isSeller = false;

    public CLISignUp() {
        CLIView.divider("Sign Up");

        username = CLIView.prompt("Enter new username: ");

        while (true) {
            password = CLIView.prompt("Enter new password: ");
            String repeatPassword = CLIView.prompt("Repeat new password: ");

            if (!this.password.equals(repeatPassword)) {
                CLIView.error("Passwords do not match. Please try again.");
                continue;
            }

            if (password.length() < 8) {
                CLIView.error("Password must be at least 8 characters long. Please try again.");
                continue;
            }

            break;
        }

        firstName = CLIView.prompt("Enter first name: ");
        lastName = CLIView.prompt("Enter last name: ");

        email = CLIView.prompt("Enter email: ");
        phoneNumber = CLIView.prompt("Enter phone number: ");

        isSeller = CLIView.prompt("Do you want to request seller account? (y/n): ").equalsIgnoreCase("y");
        if (isSeller) {
            companyName = CLIView.prompt("Enter company name: ");
        }

        CLIView.info("Signing up...");
        CLIView.divider();
    }
}
