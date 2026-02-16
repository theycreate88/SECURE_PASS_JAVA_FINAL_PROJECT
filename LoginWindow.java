

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginWindow extends JFrame {

    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private JButton loginBtn;
    private JLabel statusLabel;
    private boolean firstTime;
    private int failedAttempts = 0;

    // Modern professional palette
    private final Color backgroundColor = new Color(240, 242, 245);
    private final Color cardColor = Color.WHITE;
    private final Color primaryColor = new Color(33, 150, 243);
    private final Color hoverColor = new Color(30, 136, 229);
    private final Color textColor = new Color(40, 40, 40);
    private final Color errorColor = new Color(211, 47, 47);
    private final Color borderColor = new Color(220, 220, 220);

    public LoginWindow() {
        super("SecurePass");
        setSize(520, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(backgroundColor);
        setLayout(new GridBagLayout());

        ImageIcon icon = new ImageIcon(getClass().getResource("securepasslogo.png"));
        setIconImage(icon.getImage());

        firstTime = !FileStorage.isMasterSet();

        JPanel card = new JPanel();
        card.setBackground(cardColor);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        JLabel title = new JLabel("SecurePass", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(textColor);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel(
                firstTime ? "Create your secure vault" : "Sign in to continue",
                SwingConstants.CENTER
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(120, 120, 120));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = new JPasswordField(20);
        JPanel pwdPanel = createLabeledField(
                firstTime ? "Master Password" : "Password",
                passwordField
        );

        if (firstTime) {
            confirmField = new JPasswordField(20);
            card.add(pwdPanel);
            card.add(Box.createRigidArea(new Dimension(0, 15)));
            card.add(createLabeledField("Confirm Password", confirmField));
        } else {
            card.add(pwdPanel);
        }

        card.add(Box.createRigidArea(new Dimension(0, 25)));

        loginBtn = new JButton(firstTime ? "Create Account" : "Login");
        styleButton(loginBtn);
        loginBtn.addActionListener(e -> handleLogin());

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(errorColor);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(loginBtn);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(statusLabel);

        card.add(Box.createRigidArea(new Dimension(0, 10)));

        add(card);
    }

    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(cardColor);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(textColor);

        field.setMaximumSize(new Dimension(300, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(field);

        return panel;
    }

    private void styleButton(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(primaryColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(primaryColor);
            }
        });
    }

    /* ===================== LOGIC BELOW (UNCHANGED) ===================== */

    private void handleLogin() {
        String pwd = new String(passwordField.getPassword());

        if (firstTime) {
            if (!isStrongPassword(pwd)) {
                statusLabel.setText(
                        "Password must be ≥5 chars, include 1 uppercase, 1 number & 1 special char"
                );
                return;
            }

            String confirm = new String(confirmField.getPassword());
            if (!pwd.equals(confirm)) {
                statusLabel.setText("Passwords do not match!");
                return;
            }

            FileStorage.saveMaster(pwd);
            showAccountCreatedDialog(pwd);
            return;
        }

        if (!FileStorage.verifyMaster(pwd)) {
            failedAttempts++;
            if (failedAttempts >= 2) {
                showSecurityQuestionResetDialog();
            } else {
                statusLabel.setText("Invalid password!");
            }
            return;
        }

        showWelcomeDialog(pwd);
    }

    // ALL OTHER METHODS BELOW ARE 100% SAME AS YOUR ORIGINAL
    // (Security questions, reset, dialogs, dashboard, password validation)

    // ⬇️ unchanged methods ⬇️

    // ... (keep exactly as you already have them)





private void showSecurityQuestionsDialog(String masterPassword) {
    JDialog dialog = new JDialog(this, "Set Security Questions", true);
    dialog.setSize(400, 300);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));

    String[] questions = {
        "Your Date of Birth",
        "Your Favorite Teacher",
        "Name of Your First Pet"
    };

    JTextField[] answers = new JTextField[3];
    for (int i = 0; i < 3; i++) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(questions[i]);
        answers[i] = new JTextField(20);
        panel.add(label);
        panel.add(answers[i]);
        dialog.add(panel);
        dialog.add(Box.createRigidArea(new Dimension(0,10)));
    }

    JButton saveBtn = new JButton("Save");
    saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
    saveBtn.addActionListener(e -> {
        // Save answers
        FileStorage.saveSecurityQA(
            questions[0], answers[0].getText().trim(),
            questions[1], answers[1].getText().trim(),
            questions[2], answers[2].getText().trim()
        );
        dialog.dispose();
        askUserNameAndProceed(masterPassword);
    });

    dialog.add(saveBtn);
    dialog.setVisible(true);
}

private void showSecurityQuestionResetDialog() {
    String[][] qa = FileStorage.getSecurityQA();
    if (qa == null) return;

    String[] questionOptions = { qa[0][0], qa[1][0], qa[2][0] };
    String question = (String) JOptionPane.showInputDialog(
        this,
        "Select a security question to reset your password:",
        "Security Question",
        JOptionPane.QUESTION_MESSAGE,
        null,
        questionOptions,
        questionOptions[0]
    );

    if (question == null) return;

    // Find the answer index
    int index = -1;
    for (int i = 0; i < 3; i++) {
        if (qa[i][0].equals(question)) {
            index = i;
            break;
        }
    }

    if (index == -1) return;

    String answer = JOptionPane.showInputDialog(
        this,
        question,
        "Answer",
        JOptionPane.PLAIN_MESSAGE
    );

    if (answer == null) return;

    if (answer.trim().equalsIgnoreCase(qa[index][1])) {
        // Correct → allow reset
        resetMasterPassword();
    } else {
        JOptionPane.showMessageDialog(this, "Incorrect answer!", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void resetMasterPassword() {
    String newPwd = JOptionPane.showInputDialog(
        this,
        "Enter new master password:",
        "Reset Password",
        JOptionPane.PLAIN_MESSAGE
    );

    if (newPwd == null || !isStrongPassword(newPwd)) {
        JOptionPane.showMessageDialog(this,
            "Password must be ≥5 chars, include 1 uppercase, 1 number & 1 special char",
            "Error",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    FileStorage.saveMaster(newPwd);
    failedAttempts = 0;
    JOptionPane.showMessageDialog(this,
        "Password reset successfully! Please login again.",
        "Success",
        JOptionPane.INFORMATION_MESSAGE);
}


private void showAccountCreatedDialog(String masterPassword) {
    JDialog dialog = new JDialog(this, "Success", true);
    dialog.setSize(320, 180);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout(10, 10));

    // Success message
    JLabel msg = new JLabel("Account Created Successfully", SwingConstants.CENTER);
    msg.setFont(new Font("Segoe UI", Font.BOLD, 16));

    // Next button
    JButton nextBtn = new JButton("Next");
    nextBtn.addActionListener(e -> {
        dialog.dispose(); // close this dialog
        // Now show security questions dialog
        showSecurityQuestionsDialog(masterPassword);
    });

    JPanel btnPanel = new JPanel();
    btnPanel.add(nextBtn);

    dialog.add(msg, BorderLayout.CENTER);
    dialog.add(btnPanel, BorderLayout.SOUTH);
    dialog.setVisible(true);
}



private void askUserNameAndProceed(String masterPassword) {
    String name = JOptionPane.showInputDialog(
        this,
        "Enter your name:",
        "User Setup",
        JOptionPane.PLAIN_MESSAGE
    );

    if (name == null || name.trim().isEmpty()) {
        JOptionPane.showMessageDialog(
            this,
            "Name cannot be empty!",
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
        return;
    }

    FileStorage.saveUserName(name);
    openDashboard(masterPassword);
}



 private void showWelcomeDialog(String masterPassword) {
    String userName = FileStorage.getUserName();
    if (userName == null) userName = "User";

    JDialog dialog = new JDialog(this, "Welcome", true);
    dialog.setSize(320, 180);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout(10, 10));

    JLabel welcomeLabel = new JLabel(
        "Welcome " + userName,
        SwingConstants.CENTER
    );
    welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

    JButton thankBtn = new JButton("Thank You");
    thankBtn.addActionListener(e -> {
        dialog.dispose();
        openDashboard(masterPassword);
    });

    JPanel btnPanel = new JPanel();
    btnPanel.add(thankBtn);

    dialog.add(welcomeLabel, BorderLayout.CENTER);
    dialog.add(btnPanel, BorderLayout.SOUTH);
    dialog.setVisible(true);
}

private void openDashboard(String masterPassword) {
    Dashboard dash = new Dashboard(masterPassword);
    dash.setVisible(true);
    dispose();
}


    private boolean isStrongPassword(String pwd) {
        if (pwd.length() < 5)
            return false;

        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : pwd.toCharArray()) {
            if (Character.isUpperCase(c))
                hasUpper = true;
            else if (Character.isDigit(c))
                hasDigit = true;
            else if (!Character.isLetterOrDigit(c))
                hasSpecial = true;
        }

        return hasUpper && hasDigit && hasSpecial;
    }

}
