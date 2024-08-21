package control;

import model.User;

public class CLI {
    private static User loggedInUser = null;

    public static void main(String[] args) {
        while (true) {
            if (loggedInUser == null) {
                CLILoginPhase loginPhase = new CLILoginPhase();
                loggedInUser = loginPhase.getLoggedInUser();
            }

            try {
                CLIProfilePhase profilePhase = new CLIProfilePhase(loggedInUser);
            } catch (LoggedOutExeption e) {
                loggedInUser = null;
            } catch (ExitExeption e) {
                System.exit(0);
            }
        }
    }
}
