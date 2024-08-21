package control;

public class LoggedOutExeption extends Exception {

    public LoggedOutExeption() {
        super("You are not logged in.");
    }
}
