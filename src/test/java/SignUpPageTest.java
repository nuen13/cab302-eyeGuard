import com.example.javafxreadingdemo.User;
import com.example.javafxreadingdemo.UserDAO;
import com.example.javafxreadingdemo.DatabaseConnection;
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
    private int testUserId = 99; // Start with a unique ID for the test user
    private String testEmail = "testsignupuser99@example.com"; // Ensure a unique email

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
        connection = DatabaseConnection.getInstance();
    }

    @AfterEach
    void tearDown() {
        deleteTestUser(testUserId);
    }

    @Test
    void testUserSignUp() {
        String password = "password123";
        User testUser = new User(testEmail, password);

        // Sign up the test user into the database
        assertDoesNotThrow(() -> userDAO.insert(testUser));

        // Retrieve the inserted user to check if the sign-up was successful
        Integer userId = userDAO.validateUser(testEmail, password);
        assertNotNull(userId, "User should be signed up and valid");

        // Check if the user details match
        User retrievedUser = getUserById(userId);
        assertNotNull(retrievedUser, "User should be retrievable from the database");
        assertEquals(testEmail, retrievedUser.getEmail(), "Email should match");
        assertEquals(password, retrievedUser.getPassword(), "Password should match");
    }

    private User getUserById(int userId) {
        String query = "SELECT id, email, password, last_access_date, day_streak FROM users WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    LocalDate lastAccessDate = null;
                    if (resultSet.getDate("last_access_date") != null) {
                        lastAccessDate = resultSet.getDate("last_access_date").toLocalDate();
                    }
                    return new User(
                            resultSet.getInt("id"),
                            resultSet.getString("email"),
                            resultSet.getString("password"),
                            lastAccessDate,
                            resultSet.getInt("day_streak")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void deleteTestUser(int userId) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error deleting test user: " + ex.getMessage());
        }
    }
}
