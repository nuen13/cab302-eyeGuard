package com.example.javafxreadingdemo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.regex.Pattern;

public class SignUpPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    private UserDAO userDAO; // DAO object for database operations

    private CustomSettingDAO CustomSettingDAO;

    public SignUpPage() {
        super("Sign Up");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 500);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(0, 9, 19));

        userDAO = new UserDAO(); // Initialize UserDAO

        userDAO.createTable(); // Ensure table exists


        CustomSettingDAO = new CustomSettingDAO();
        CustomSettingDAO.createCustomSettingTable();

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(10, 20, 10, 20);

        // Logo
        try {
            BufferedImage originalImage = ImageIO.read(getClass().getResourceAsStream("/Logo/Logo.jpg.png"));
            ImageIcon icon = new ImageIcon(originalImage.getScaledInstance(200, 100, Image.SCALE_SMOOTH));
            JLabel logoLabel = new JLabel(icon);
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            constraints.anchor = GridBagConstraints.CENTER;
            panel.add(logoLabel, constraints);
        } catch (IOException e) {
            System.out.println("Logo file not found or error reading the file.");
        }

        emailField = addLabelAndTextField(panel, "EMAIL", 1);
        passwordField = addLabelAndPasswordField(panel, "PASSWORD", 3);
        confirmPasswordField = addLabelAndPasswordField(panel, "CONFIRM PASSWORD", 5);
        addButton(panel, "Sign Up", 7);
        addLoginLink(panel, "already have an account? log in here", 8);

        add(panel);
        setVisible(true);
    }

    private JTextField addLabelAndTextField(JPanel panel, String labelText, int gridy) {
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
        JTextField textField = new JTextField(20);
        textField.setPreferredSize(new Dimension(280, 40));
        textField.setOpaque(false);
        textField.setForeground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.add(textField, constraints);
        return textField;
    }

    private JPasswordField addLabelAndPasswordField(JPanel panel, String labelText, int gridy) {
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
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(280, 40));
        passwordField.setOpaque(false);
        passwordField.setForeground(Color.WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.add(passwordField, constraints);
        return passwordField;
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
                if (validateInput()) {
                    signUpUser();
                }
            }
        });
        panel.add(button, constraints);
    }

    private void addLoginLink(JPanel panel, String linkText, int gridy) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = gridy;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;

        JLabel linkLabel = new JLabel("<html><u>" + linkText + "</u></html>");
        linkLabel.setForeground(Color.CYAN); // Set hyperlink color
        linkLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose(); // Close the SignUpPage
                new LoginPage().setVisible(true); // Open the LoginPage
            }
        });
        panel.add(linkLabel, constraints);
    }

    private boolean validateInput() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (!Pattern.matches("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void signUpUser() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        if (validateInput()) { // If sign-up is successful
            User newUser = new User(email, password); // Create a new user object
            userDAO.insert(newUser); // Insert the new user into the database



            JOptionPane.showMessageDialog(this, "Sign-up successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close the SignUpPage
            new LoginPage().setVisible(true); // Open the LoginPage
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SignUpPage::new);
    }
}
