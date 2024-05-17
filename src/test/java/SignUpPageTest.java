/*package com.example.javafxreadingdemo;

import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class SignUpPageTest {

    @Test
    public void testValidInput() {
        SignUpPage signUpPage = new SignUpPage();

        signUpPage.emailField.setText("test@example.com");
        signUpPage.passwordField.setText("password");
        signUpPage.confirmPasswordField.setText("password");

        assertTrue(signUpPage.validateInput());
    }

    @Test
    public void testInvalidEmailFormat() {
        SignUpPage signUpPage = new SignUpPage();

        signUpPage.emailField.setText("invalidemail");
        signUpPage.passwordField.setText("password");
        signUpPage.confirmPasswordField.setText("password");

        assertFalse(signUpPage.validateInput());
    }

    @Test
    public void testPasswordsMismatch() {
        SignUpPage signUpPage = new SignUpPage();

        signUpPage.emailField.setText("test@example.com");
        signUpPage.passwordField.setText("password1");
        signUpPage.confirmPasswordField.setText("password2");

        assertFalse(signUpPage.validateInput());
    }

    @Test
    public void testShortPassword() {
        SignUpPage signUpPage = new SignUpPage();

        signUpPage.emailField.setText("test@example.com");
        signUpPage.passwordField.setText("pass");
        signUpPage.confirmPasswordField.setText("pass");

        assertFalse(signUpPage.validateInput());
    }

    @Test
    public void testSignUpUser() {
        SignUpPage signUpPage = new SignUpPage();

        signUpPage.emailField.setText("test@example.com");
        signUpPage.passwordField.setText("password");
        signUpPage.confirmPasswordField.setText("password");

        signUpPage.signUpUser();

        // Assuming sign-up is successful, LoginPage should be opened
        Component[] components = signUpPage.getContentPane().getComponents();
        boolean loginPageOpened = false;
        for (Component component : components) {
            if (component instanceof LoginPage) {
                loginPageOpened = true;
                break;
            }
        }

        assertTrue(loginPageOpened);
    }
}
*/