

import javax.swing.*;

public class MasterPasswordPrompt {

    public static String ask() {
        JPasswordField field = new JPasswordField();
        int res = JOptionPane.showConfirmDialog(
                null,
                field,
                "Enter Master Password",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (res == JOptionPane.OK_OPTION) {
            return new String(field.getPassword());
        }
        return null;
    }
}
