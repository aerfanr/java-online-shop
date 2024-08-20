package control;

import model.Role;
import model.User;
import view.CLILogin;
import view.CLISignUp;
import view.CLIView;

public class CLI {

    public static void main(String[] args) {
        if (!User.adminExists()) {
            while (true) {
                CLIView.warn("No admin account exists. Create one now.");
                CLISignUp signUp = new CLISignUp();
                try {
                    User newAdmin = new User(
                            signUp.username, signUp.firstName, signUp.lastName, signUp.email,
                            signUp.phoneNumber, Role.ADMIN
                    );
                    newAdmin.insert(signUp.password);
                } catch (Exception e) {
                    CLIView.error("Failed to create admin account: " + e.getMessage());
                    continue;
                }

                break;
            }
        }
        assert User.adminExists();

        while (true) {
            CLIView.info("Login to your account.");
            CLILogin login = new CLILogin();
            try {
                User.login(login.username, login.password);
                CLIView.success("Logged in.");
                return;
            } catch (Exception e) {
                CLIView.error("Failed to login: " + e.getMessage());
            }
        }
    }
}
