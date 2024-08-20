package view;

public class CLILogin {
    public String username;
    public String password;

    public CLILogin() {
        CLIView.divider("Login");
        username = CLIView.prompt("Enter username: ");
        password = CLIView.prompt("Enter password: ");
        CLIView.info("Attempting login...");
        CLIView.divider();
    }
}
