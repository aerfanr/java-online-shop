import model.Role;
import model.SellerStatus;
import model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserTest {
    private User user = null;
    private final String username = "test" + System.currentTimeMillis();

    @BeforeAll
    public void create() {
        user = new User(
                username,
                "test",
                "test",
                "test",
                "test",
                Role.BUYER
        );
        user.insert("Test1234");
    }

    @Test
    public void createWorks() {
        // No duplicate username
        assertThrows(IllegalArgumentException.class, () -> user.insert("Test12345"));

        // No wrong password
        assertThrows(IllegalArgumentException.class, () -> User.login("test", "Test12345"));

        // Correct login
        User userRead = User.login(username, "Test1234");
        assertEquals(user.getUsername(), userRead.getUsername());
        assertEquals(user.getEmail(), userRead.getEmail());
    }

    @Test
    public void sellerStatus() {
        assertNull(user.getSellerStatus());
        assertEquals(user.getRole(), Role.BUYER);

        user.setSellerStatus(SellerStatus.APPROVED);
        assertEquals(user.getSellerStatus(), SellerStatus.APPROVED);
        assertEquals(user.getRole(), Role.SELLER);

        user.setSellerStatus(SellerStatus.REJECTED);
        assertEquals(user.getSellerStatus(), SellerStatus.REJECTED);
        assertEquals(user.getRole(), Role.BUYER);

        user.setSellerStatus(SellerStatus.PENDING);
        assertEquals(user.getSellerStatus(), SellerStatus.PENDING);
        assertEquals(user.getRole(), Role.BUYER);

    }

    public void requests() {
        user.setSellerStatus(SellerStatus.PENDING);

        assertTrue(User.getRequests()
                .stream()
                .anyMatch(u -> u.getUsername().equals(username)));

        user.accept();
        assertEquals(user.getSellerStatus(), SellerStatus.APPROVED);
        assertEquals(user.getRole(), Role.SELLER);
        assertFalse(User.getRequests()
                .stream()
                .anyMatch(u -> u.getUsername().equals(username)));

        user.setSellerStatus(SellerStatus.PENDING);

        user.reject();
        assertEquals(user.getSellerStatus(), SellerStatus.REJECTED);
        assertEquals(user.getRole(), Role.BUYER);
        assertFalse(User.getRequests()
                .stream()
                .anyMatch(u -> u.getUsername().equals(username)));
    }

    @AfterAll
    public void delete() {
        User.deleteByUsername(username);
        assertThrows(IllegalArgumentException.class, () -> User.load(username));
    }
}
