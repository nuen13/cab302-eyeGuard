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

class UserDAOTest {
    private UserDAO userDAO;
    private Connection connection;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
        connection = DatabaseConnection.getInstance();
    }

    @AfterEach
    void tearDown() {
        deleteTestUser("test@example.com");
    }

    @Test
    void testInsertUser() {
        User user = new User("test@example.com", "password123");
        assertDoesNotThrow(() -> userDAO.insert(user));
        assertNotNull(user.getId());
    }

    @Test
    void testValidateUser() {
        insertUser("test@example.com", "password123");

        Integer userId = userDAO.validateUser("test@example.com", "password123");
        assertNotNull(userId);
    }

    @Test
    void testUpdateDayStreak() {
        insertUser("test@example.com", "password123");

        User user = getUserByEmail("test@example.com");
        assertNotNull(user);

        userDAO.updateDayStreak(user.getId());
        User updatedUser = getUserByEmail("test@example.com");
        assertNotNull(updatedUser);
        assertTrue(updatedUser.getDayStreak() > 0);
    }

    private void insertUser(String email, String password) {
        User user = new User(email, password);
        assertDoesNotThrow(() -> userDAO.insert(user));
        assertNotNull(user.getId());
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
