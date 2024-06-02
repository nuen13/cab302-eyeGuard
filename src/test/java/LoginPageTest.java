import com.example.javafxreadingdemo.UserDAO;
import com.example.javafxreadingdemo.LoginPage;
import org.junit.jupiter.api.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginPageTest {
    private LoginPage loginPage;
    private UserDAO userDAO;

    @BeforeAll
    public void setUpOnce() {
        userDAO = mock(UserDAO.class);
        loginPage = new LoginPage();
        loginPage.setVisible(false); // Prevents the login page from actually showing up during tests

// Manually add the userDAO to the loginPage for testing purposes
// Assuming the userDAO can be set via reflection or an accessor method for testing purposes
        try {
            var userDAOField = LoginPage.class.getDeclaredField("userDAO");
            userDAOField.setAccessible(true);
            userDAOField.set(loginPage, userDAO);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public void tearDown() {
        loginPage.dispose();
    }

    @Test
    @Order(1)
    public void testLoginSuccess() {
        when(userDAO.validateUser(anyString(), anyString())).thenReturn(1);

        setTextField(loginPage, "emailField", "test@example.com");
        setTextField(loginPage, "passwordField", "password123");
        clickButton(loginPage, "Login");

// Assuming successful login closes the window
        assertFalse(loginPage.isVisible());
    }

    @Test
    @Order(2)
    public void testLoginFailure() {
        when(userDAO.validateUser(anyString(), anyString())).thenReturn(null);

        setTextField(loginPage, "emailField", "invalid@example.com");
        setTextField(loginPage, "passwordField", "wrongpassword");
        clickButton(loginPage, "Login");

// Assuming an error message dialog is shown
        JOptionPane optionPane = getOptionPane();
        assertNotNull(optionPane);
        assertEquals("Invalid email or password.", optionPane.getMessage());
    }

    @Test
    @Order(3)
    public void testInvalidEmailFormat() {
        setTextField(loginPage, "emailField", "invalid-email");
        setTextField(loginPage, "passwordField", "password123");
        clickButton(loginPage, "Login");

// Assuming an error message dialog is shown for invalid email format
        JOptionPane optionPane = getOptionPane();
        assertNotNull(optionPane);
        assertEquals("Invalid email format.", optionPane.getMessage());
    }

    @Test
    @Order(4)
    public void testShortPassword() {
        setTextField(loginPage, "emailField", "test@example.com");
        setTextField(loginPage, "passwordField", "123");
        clickButton(loginPage, "Login");

// Assuming an error message dialog is shown for short password
        JOptionPane optionPane = getOptionPane();
        assertNotNull(optionPane);
        assertEquals("Password must be at least 6 characters long.", optionPane.getMessage());
    }

    private void setTextField(JFrame frame, String name, String text) {
        for (Component component : frame.getContentPane().getComponents()) {
            if (component instanceof JTextField && ((JTextField) component).getName().equals(name)) {
                ((JTextField) component).setText(text);
            }
        }
    }

    private void clickButton(JFrame frame, String text) {
        for (Component component : frame.getContentPane().getComponents()) {
            if (component instanceof JButton && ((JButton) component).getText().equals(text)) {
                for (ActionListener al : ((JButton) component).getActionListeners()) {
                    al.actionPerformed(new ActionEvent(component, ActionEvent.ACTION_PERFORMED, null));
                }
            }
        }
    }

    private JOptionPane getOptionPane() {
        for (Window window : JOptionPane.getWindows()) {
            if (window instanceof JDialog) {
                for (Component component : ((JDialog) window).getContentPane().getComponents()) {
                    if (component instanceof JOptionPane) {
                        return (JOptionPane) component;
                    }
                }
            }
        }
        return null;
    }
}