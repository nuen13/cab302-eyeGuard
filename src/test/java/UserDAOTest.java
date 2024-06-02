import com.example.javafxreadingdemo.DatabaseConnection;
import com.example.javafxreadingdemo.User;
import com.example.javafxreadingdemo.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {
    private UserDAO userDAO;
    private Connection connection;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
        connection = DatabaseConnection.getInstance();

        // Ensure the table exists
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "email TEXT UNIQUE, " +
                    "password TEXT, " +
                    "day_streak INTEGER DEFAULT 0, " +
                    "last_login BIGINT DEFAULT 0)");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to create users table.");
        }
    }

    @Test
    void testInsertUser() {
        User user = new User("test@example.com", "password123");
        assertDoesNotThrow(() -> userDAO.insert(user));
    }

    @Test
    void testValidateUser() {
        User user = new User("validate@example.com", "password123");
        insertUser(user);

        Integer userId = userDAO.validateUser(user.getEmail(), user.getPassword());
        assertNotNull(userId);
    }

    @Test
    void testUpdateDayStreak() {
        User user = new User("streak@example.com", "password123");
        insertUser(user);

        Integer userId = userDAO.validateUser(user.getEmail(), user.getPassword());
        assertNotNull(userId);

        assertDoesNotThrow(() -> userDAO.updateDayStreak(userId));
    }

    private void insertUser(User user) {
        String query = "INSERT INTO users (email, password) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getPassword());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to insert user for test.");
        }
    }
}
