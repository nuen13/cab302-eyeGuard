package com.example.javafxreadingdemo;

import javafx.application.Platform;
import javafx.stage.Stage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class LoginPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    public LoginPage() {
        super("EYEGUARD Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 500);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(32, 34, 37));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(10, 20, 10, 20);

        try {
            BufferedImage originalImage = ImageIO.read(getClass().getResourceAsStream("/Logo/Logo.jpg.png"));
            ImageIcon icon = new ImageIcon(resizeImage(originalImage, 200, 100));
            JLabel logoLabel = new JLabel(icon);
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            constraints.anchor = GridBagConstraints.CENTER;
            panel.add(logoLabel, constraints);
        } catch (IOException e) {
            System.out.println("Logo file not found or error reading the file.");
        }

        addLabelAndTextField(panel, "EMAIL", 1);
        addLabelAndTextField(panel, "PASSWORD", 3);
        addButton(panel, "Login", 5);
        addButton(panel, "Forgot Password?", 6);
        addSignUpButton(panel, "New to EyeGuard? Signup!", 7); // Modify this part to include redirection functionality

        add(panel);
        setVisible(true);
    }

    private Image resizeImage(BufferedImage originalImage, int width, int height) {
        Image resultingImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return resultingImage;
    }

    private void addLabelAndTextField(JPanel panel, String labelText, int gridy) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = gridy;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(5, 20, 5, 20);
        constraints.anchor = GridBagConstraints.CENTER;

        JLabel label = new JLabel(labelText);
        label.setForeground(Color.LIGHT_GRAY);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(label, constraints);

        constraints.gridy += 1;
        if ("EMAIL".equals(labelText)) {
            emailField = new JTextField(20);
            emailField.setPreferredSize(new Dimension(280, 40));
            emailField.setOpaque(false);
            emailField.setForeground(Color.WHITE);
            emailField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
            panel.add(emailField, constraints);
        } else if ("PASSWORD".equals(labelText)) {
            // Assuming similar handling for passwordField
            passwordField = new JPasswordField(20);
            passwordField.setPreferredSize(new Dimension(280, 40));
            passwordField.setOpaque(false);
            passwordField.setForeground(Color.WHITE);
            passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
            panel.add(passwordField, constraints);
        }
    }

    private void addButton(JPanel panel, String buttonText, int gridy) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = gridy;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(10, 20, 10, 20);
        constraints.anchor = GridBagConstraints.CENTER;

        JButton button = new JButton(buttonText);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(114, 137, 218));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(280, 40));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Conditional to differentiate the button functionality based on the button text
                if ("Login".equals(buttonText)) {
                    if (validateInput()) { // Validate input fields before logging in
                        dispose(); // Close the login window if input is valid
                        // Initialize JavaFX Platform and launch the JavaFX application
                        Platform.startup(() -> {
                            TimerManagementApplication app = new TimerManagementApplication();
                            try {
                                app.start(new Stage()); // Start the JavaFX application
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });
                    }
                } else if ("Forgot Password?".equals(buttonText)) {
                    // Handle forgot password functionality here
                    JOptionPane.showMessageDialog(LoginPage.this, "Password reset feature is not implemented yet.");
                }
            }
        });
        panel.add(button, constraints);
    }

    // New method to add a SignUp button with action listener
    private void addSignUpButton(JPanel panel, String buttonText, int gridy) {
        JButton signUpButton = new JButton(buttonText);
        signUpButton.setFont(new Font("Arial", Font.BOLD, 12));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setBackground(new Color(114, 137, 218));
        signUpButton.setBorderPainted(false);
        signUpButton.setFocusPainted(false);
        signUpButton.setPreferredSize(new Dimension(280, 40));
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the login window
                new SignUpPage().setVisible(true); // Open the sign-up window
            }
        });

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = gridy;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(10, 20, 10, 20);
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(signUpButton, constraints);
    }

    private boolean validateInput() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (!Pattern.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        Connection connection = DatabaseConnection.getInstance();
        SwingUtilities.invokeLater(LoginPage::new);
    }
}
