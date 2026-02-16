

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AddEditDialog extends JDialog {

    private JTextField app;
    private JTextField user;
    private JPasswordField pass;
    private JTextArea desc;
    private JComboBox<String> categoryCombo;

    public AddEditDialog(Dashboard parent, PasswordEntry entry) {
        setModal(true);
        setTitle(entry == null ? "Add New Password" : "Edit Password");
        setSize(380, 400);
        setLocationRelativeTo(parent);

        setLayout(new GridLayout(0, 1, 6, 6));

        app = new JTextField();
        user = new JTextField();
        pass = new JPasswordField();
        desc = new JTextArea(3, 20);

        JScrollPane descScroll = new JScrollPane(desc);

        JComboBox<String> categoryCombo = new JComboBox<>(
                new String[] { "Socials", "Banks", "Entertainment", "Work", "Other" });

        if (entry != null) {
            app.setText(entry.getAppName());
            user.setText(entry.getUsername());
            pass.setText(entry.getPassword());
            desc.setText(entry.getDescription());
            categoryCombo.setSelectedItem(entry.getCategory());
        }

        JButton save = new JButton("Save");

        save.addActionListener(e -> {

            List<PasswordEntry> list = FileStorage.load(parent.getMasterPassword());

            if (entry == null) {

                list.add(new PasswordEntry(
                        app.getText(),
                        user.getText(),
                        new String(pass.getPassword()),
                        desc.getText(),
                        (String) categoryCombo.getSelectedItem()));
            } else {

                for (PasswordEntry pe : list) {
                    if (pe.equals(entry)) {
                        pe.setAppName(app.getText());
                        pe.setUsername(user.getText());
                        pe.setPassword(new String(pass.getPassword()));
                        pe.setDescription(desc.getText());
                        pe.setCategory((String) categoryCombo.getSelectedItem());
                        break;
                    }
                }
            }

            FileStorage.save(list);

            parent.refresh();
            dispose();
        });

        add(new JLabel("App Name:"));
        add(app);
        add(new JLabel("Username:"));
        add(user);
        add(new JLabel("Password:"));
        add(pass);
        add(new JLabel("Description:"));
        add(descScroll);
        add(new JLabel("Category:"));
        add(categoryCombo);
        add(save);
    }

    public void setGeneratedPassword(String pwd) {
        pass.setText(pwd);
    }

}
