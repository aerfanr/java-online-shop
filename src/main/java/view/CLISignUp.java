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

        password = CLIView.newPasswordPrompt("Choose a password: ");

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
