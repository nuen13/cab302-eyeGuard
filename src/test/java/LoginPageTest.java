/*import com.example.javafxreadingdemo.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoginPageTest {

    @Mock
    private UserDAO userDAO;

    private LoginPage loginPage;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        loginPage = new LoginPage();
        loginPage.userDAO = userDAO;
    }

    @Test
    public void testValidateInput_ValidInputs() {
        loginPage.emailField = new JTextField("test@example.com");
        loginPage.passwordField = new JPasswordField("validPassword123");

        assertTrue(loginPage.validateInput());
    }

    @Test
    public void testValidateInput_InvalidEmailFormat() {
        loginPage.emailField = new JTextField("invalid-email");
        loginPage.passwordField = new JPasswordField("validPassword123");

        assertFalse(loginPage.validateInput());
    }

    @Test
    public void testValidateInput_ShortPassword() {
        loginPage.emailField = new JTextField("test@example.com");
        loginPage.passwordField = new JPasswordField("short");

        assertFalse(loginPage.validateInput());
    }

    @Test
    public void testLoginButtonAction_ValidCredentials() {
        // Mock DAO behavior
        when(userDAO.validateUser("test@example.com", "validPassword123")).thenReturn(1);

        // Simulate button click
        loginPage.emailField = new JTextField("test@example.com");
        loginPage.passwordField = new JPasswordField("validPassword123");
        JButton loginButton = new JButton("Login");
        for (ActionListener listener : loginButton.getActionListeners()) {
            listener.actionPerformed(null);
        }

        // Verify behavior
        verify(userDAO, times(1)).validateUser("test@example.com", "validPassword123");
    }

    @Test
    public void testLoginButtonAction_InvalidCredentials() {
        // Mock DAO behavior
        when(userDAO.validateUser(anyString(), anyString())).thenReturn(null);

        // Simulate button click
        loginPage.emailField = new JTextField("test@example.com");
        loginPage.passwordField = new JPasswordField("invalidPassword");
        JButton loginButton = new JButton("Login");
        for (ActionListener listener : loginButton.getActionListeners()) {
            listener.actionPerformed(null);
        }

        // Verify behavior
        verify(userDAO, times(1)).validateUser("test@example.com", "invalidPassword");
        assertEquals(1, JOptionPane.showOptionDialog(null, "Invalid email or password.", "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null));
    }

    @Test
    public void testForgotPasswordButtonAction() {
        JButton forgotPasswordButton = new JButton("Forgot Password?");
        for (ActionListener listener : forgotPasswordButton.getActionListeners()) {
            listener.actionPerformed(null);
        }

        assertEquals(1, JOptionPane.showOptionDialog(null, "Password reset feature is not implemented yet.", "Message", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null));
    }

    @Test
    public void testSignUpButtonAction() {
        JButton signUpButton = new JButton("New to EyeGuard? Signup!");
        for (ActionListener listener : signUpButton.getActionListeners()) {
            listener.actionPerformed(null);
        }

        assertFalse(loginPage.isVisible());
    }
}
*/