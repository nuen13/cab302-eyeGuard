import com.example.javafxreadingdemo.DatabaseConnection;
import com.example.javafxreadingdemo.User;
import com.example.javafxreadingdemo.UserDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SignUpPageTest {
    private UserDAO userDAO;
    private Connection connection;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
        connection = DatabaseConnection.getInstance();
    }

    @AfterEach
    void tearDown() {
        deleteTestUser("newuser@example.com");
    }

    @Test
    void testUserSignUp() {
        User newUser = new User("newuser@example.com", "password123");
        assertDoesNotThrow(() -> userDAO.insert(newUser));

        User fetchedUser = getUserByEmail("newuser@example.com");
        assertNotNull(fetchedUser);
        assertEquals("newuser@example.com", fetchedUser.getEmail());
    }

    private User getUserByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            var rs = statement.executeQuery();
            if (rs.next()) {
                LocalDate lastAccessDate = null;
                if (rs.getDate("last_access_date") != null) {
                    lastAccessDate = rs.getDate("last_access_date").toLocalDate();
                }
                return new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        lastAccessDate,
                        rs.getInt("day_streak")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void deleteTestUser(String email) {
        String query = "DELETE FROM users WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
