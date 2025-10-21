package ui;

import models.User;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class LoginWindow extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel cards = new JPanel(cardLayout);

    private JTextField signupNameField = new JTextField(20);
    private JTextField signupUsernameField = new JTextField(20);
    private JPasswordField signupPasswordField = new JPasswordField(20);
    private JPasswordField signupConfirmPasswordField = new JPasswordField(20);
    private JComboBox<String> signupRoleCombo = new JComboBox<>(new String[]{"student", "organizer"});

    private JTextField loginUsernameField = new JTextField(20);
    private JPasswordField loginPasswordField = new JPasswordField(20);
    private JComboBox<String> loginRoleCombo = new JComboBox<>(new String[]{"student", "organizer"});

    public LoginWindow() {
        setTitle("UniEventHub - Login / Signup");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 320);
        setLocationRelativeTo(null);

        cards.add(buildLoginPanel(), "login");
        cards.add(buildSignupPanel(), "signup");

        add(cards);
        cardLayout.show(cards, "login");
    }

    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(5, 10, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(loginUsernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(loginPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Role:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(loginRoleCombo, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 0, 5, 0);
        panel.add(loginButton, gbc);

        JButton goToSignupBtn = new JButton("Go to Signup");
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 0, 15, 0);
        panel.add(goToSignupBtn, gbc);

        loginButton.addActionListener(e -> {
            String username = loginUsernameField.getText().trim();
            String password = new String(loginPasswordField.getPassword());
            String role = (String) loginRoleCombo.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter username and password.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = User.login(username, password, role);
            if (user != null) {
                JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                if ("organizer".equals(role)) {
                    new OrganizerDashboard(user).setVisible(true);
                } else {
                    new StudentDashboard(user).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials or role.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        goToSignupBtn.addActionListener(e -> cardLayout.show(cards, "signup"));

        return panel;
    }

    private JPanel buildSignupPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = new JLabel("Signup");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(5, 10, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(signupNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(signupUsernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(signupPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(signupConfirmPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(signupRoleCombo, gbc);

        JButton signupButton = new JButton("Signup");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 0, 5, 0);
        panel.add(signupButton, gbc);

        JButton goToLoginBtn = new JButton("Go to Login");
        gbc.gridy = 7;
        gbc.insets = new Insets(5, 0, 15, 0);
        panel.add(goToLoginBtn, gbc);

        signupButton.addActionListener(e -> {
            String name = signupNameField.getText().trim();
            String username = signupUsernameField.getText().trim();
            String password = new String(signupPasswordField.getPassword());
            String confirmPassword = new String(signupConfirmPasswordField.getPassword());
            String role = (String) signupRoleCombo.getSelectedItem();

            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = new User(name, username, password, role);
            if (user.save()) {
                JOptionPane.showMessageDialog(this, "Signup successful! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearSignupFields();
                cardLayout.show(cards, "login");
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists or error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        goToLoginBtn.addActionListener(e -> cardLayout.show(cards, "login"));

        return panel;
    }

    private void clearSignupFields() {
        signupNameField.setText("");
        signupUsernameField.setText("");
        signupPasswordField.setText("");
        signupConfirmPasswordField.setText("");
    }
}