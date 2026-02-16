

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Dashboard extends JFrame {

    private List<PasswordEntry> entries;
    private JPanel listPanel;
    private JComboBox<String> filterCombo;
    private String masterPassword;
    private Timer autoLogoutTimer;

    // ===== Modern UI =====
    private final Color BG = new Color(245, 247, 250);
    private final Color CARD = Color.WHITE;
    private final Color PRIMARY = new Color(33, 150, 243);
    private final Color BORDER = new Color(220, 220, 220);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public String getMasterPassword() {
        return masterPassword;
    }

    public Dashboard(String masterPassword) {
        super("SecurePass - Dashboard");
        this.masterPassword = masterPassword;

        entries = FileStorage.load(masterPassword);

        setSize(700, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);

        // ===== TOP PANEL =====
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(BG);
        topPanel.setBorder(new EmptyBorder(8, 12, 8, 12));

        JButton settingsBtn = createButton("⚙ Settings");
        JPopupMenu settingsMenu = new JPopupMenu();

        JMenuItem resetPwdItem = new JMenuItem("Reset Master Password");
        resetPwdItem.addActionListener(e -> resetMasterPassword());
        settingsMenu.add(resetPwdItem);

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Do you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                logout();
            }
        });
        settingsMenu.add(logoutItem);

        settingsBtn.addActionListener(
                e -> settingsMenu.show(settingsBtn, 0, settingsBtn.getHeight())
        );

        filterCombo = new JComboBox<>(new String[]{
                "All", "Socials", "Banks", "Entertainment", "Work", "Other"
        });
        filterCombo.setFont(TEXT_FONT);
        filterCombo.addActionListener(e -> refresh());

        JButton addBtn = createButton("+ Add Password");
        addBtn.addActionListener(e -> openAddDialog());

        JButton genPwdBtn = createButton("🔐 Generate");
        genPwdBtn.addActionListener(e -> generatePasswordFlow());

        topPanel.add(settingsBtn);
        topPanel.add(genPwdBtn);
        topPanel.add(new JLabel("Filter:"));
        topPanel.add(filterCombo);
        topPanel.add(addBtn);

        add(topPanel, BorderLayout.NORTH);

        // ===== LIST PANEL =====
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(BG);
        listPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);

        refresh();
        startAutoLogoutTimer();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopTimer();
            }
        });
    }

    // ===== AUTO LOGOUT =====
    private void startAutoLogoutTimer() {
        autoLogoutTimer = new Timer(30_000, e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Session expired. Do you want to logout?",
                    "Auto Logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                logout();
            } else {
                autoLogoutTimer.restart();
            }
        });
        autoLogoutTimer.setRepeats(false);
        autoLogoutTimer.start();
    }

    private void stopTimer() {
        if (autoLogoutTimer != null) {
            autoLogoutTimer.stop();
        }
    }

    private void logout() {
        stopTimer();
        dispose();
        new LoginWindow().setVisible(true);
    }

    // ===== RESET MASTER PASSWORD (UNCHANGED) =====
    private void resetMasterPassword() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        JPasswordField oldPwdField = new JPasswordField();
        JPasswordField newPwdField = new JPasswordField();

        panel.add(new JLabel("Old Master Password:"));
        panel.add(oldPwdField);
        panel.add(new JLabel("New Master Password:"));
        panel.add(newPwdField);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Reset Master Password",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String oldPwd = new String(oldPwdField.getPassword());
            String newPwd = new String(newPwdField.getPassword());

            if (!oldPwd.equals(masterPassword)) {
                JOptionPane.showMessageDialog(this,
                        "Old master password is incorrect!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isValidPassword(newPwd)) {
                JOptionPane.showMessageDialog(this,
                        "Password must be ≥5 chars, include 1 uppercase, 1 number & 1 special char",
                        "Invalid Password",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (FileStorage.updateMasterPassword(masterPassword, newPwd)) {
                masterPassword = newPwd;
                JOptionPane.showMessageDialog(this,
                        "Master password updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private boolean isValidPassword(String pwd) {
        return pwd.length() >= 5 &&
                pwd.matches(".*[A-Z].*") &&
                pwd.matches(".*\\d.*") &&
                pwd.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }

    // ===== UI METHODS =====
    public void refresh() {
        entries = FileStorage.load(masterPassword);
        listPanel.removeAll();

        String selected = (String) filterCombo.getSelectedItem();
        if (selected == null) selected = "All";

        for (PasswordEntry e : entries) {
            if (selected.equals("All") || e.getCategory().equals(selected)) {
                listPanel.add(createBlock(e));
                listPanel.add(Box.createVerticalStrut(12));
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createBlock(PasswordEntry e) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(12, 14, 12, 14)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(CARD);

        content.add(createLabel("App: " + e.getAppName(), true));
        content.add(createLabel("Username: " + e.getUsername(), false));
        content.add(createLabel("Password: " + e.getPassword(), false));
        content.add(createLabel("Description: " + e.getDescription(), false));
        content.add(createLabel("Category: " + e.getCategory(), false));

        JButton editBtn = createButton("Edit");
        editBtn.addActionListener(a -> editEntry(e));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(CARD);
        btnPanel.add(editBtn);

        card.add(content, BorderLayout.CENTER);
        card.add(btnPanel, BorderLayout.SOUTH);

        return card;
    }

    private JLabel createLabel(String text, boolean title) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(title ? TITLE_FONT : TEXT_FONT);
        lbl.setBorder(new EmptyBorder(2, 0, 2, 0));
        return lbl;
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(TEXT_FONT);
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        return btn;
    }

    private void openAddDialog() {
        new AddEditDialog(this, null).setVisible(true);
    }

    private void editEntry(PasswordEntry entry) {
        new AddEditDialog(this, entry).setVisible(true);
    }

    // ===== PASSWORD GENERATION (UNCHANGED) =====
    private void generatePasswordFlow() {

        JTextField lengthField = new JTextField();
        JTextField upperField = new JTextField();
        JTextField digitField = new JTextField();
        JTextField specialField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(4, 2, 8, 8));
        panel.add(new JLabel("Password Length:"));
        panel.add(lengthField);
        panel.add(new JLabel("Capital Letters:"));
        panel.add(upperField);
        panel.add(new JLabel("Digits:"));
        panel.add(digitField);
        panel.add(new JLabel("Special Characters:"));
        panel.add(specialField);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Generate Password", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return;

        try {
            int length = Integer.parseInt(lengthField.getText());
            int uppers = Integer.parseInt(upperField.getText());
            int digits = Integer.parseInt(digitField.getText());
            int specials = Integer.parseInt(specialField.getText());

            if (uppers + digits + specials > length) {
                JOptionPane.showMessageDialog(this,
                        "Sum of characters exceeds password length!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String generated = generatePassword(length, uppers, digits, specials);

            int save = JOptionPane.showConfirmDialog(
                    this,
                    "Generated Password:\n\n" + generated + "\n\nDo you want to save it?",
                    "Password Generated",
                    JOptionPane.YES_NO_OPTION);

            if (save == JOptionPane.YES_OPTION) {
                AddEditDialog dialog = new AddEditDialog(this, null);
                dialog.setGeneratedPassword(generated);
                dialog.setVisible(true);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generatePassword(int length, int uppers, int digits, int specials) {

        String lower = "abcdefghijklmnopqrstuvwxyz";
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digit = "0123456789";
        String special = "!@#$%^&*()_+-={}[]<>?";

        List<Character> chars = new ArrayList<>();
        Random r = new Random();

        for (int i = 0; i < uppers; i++)
            chars.add(upper.charAt(r.nextInt(upper.length())));

        for (int i = 0; i < digits; i++)
            chars.add(digit.charAt(r.nextInt(digit.length())));

        for (int i = 0; i < specials; i++)
            chars.add(special.charAt(r.nextInt(special.length())));

        while (chars.size() < length)
            chars.add(lower.charAt(r.nextInt(lower.length())));

        Collections.shuffle(chars);

        StringBuilder sb = new StringBuilder();
        for (char c : chars) sb.append(c);

        return sb.toString();
    }
}
