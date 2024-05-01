package com.example.javafxreadingdemo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class LoginPage extends JFrame {
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
        JTextField textField = new JTextField(20);
        textField.setPreferredSize(new Dimension(280, 40));
        textField.setOpaque(false);
        textField.setForeground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.add(textField, constraints);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPage::new);
    }
}
