

import java.io.*;
import java.util.*;

public class FileStorage {

    private static String user = "user.txt";
    private static String passwords = "passwords.txt";
    private static String currentMaster = null;

    public static boolean isMasterSet() {
        File f = new File(user);
        return f.exists() && f.length() > 0;
    }

    public static boolean updateMasterPassword(String oldPassword, String newPassword) {
    // Verify old password
    if (!verifyMaster(oldPassword)) return false;

    // Load all saved password entries
    List<PasswordEntry> entries = load(oldPassword);

    // Save new master password hash
    saveMaster(newPassword);

    // Re-save all password entries encrypted with new master password
    save(entries);

    return true;
}

    public static void saveMaster(String password) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(user))) {
            String hash = HashUtil.hash(password);
            oos.writeObject(hash);
            currentMaster = password;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean verifyMaster(String password) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(user))) {
            String storedHash = (String) ois.readObject();
            boolean ok = storedHash.equals(HashUtil.hash(password));
            if (ok) currentMaster = password;
            return ok;
        } catch (Exception e) {
            return false;
        }
    }

    private static final String SECURITY_FILE = "security.txt";

// Save security questions and answers
public static void saveSecurityQA(String q1, String a1, String q2, String a2, String q3, String a3) {
    try (FileWriter fw = new FileWriter(SECURITY_FILE)) {
        fw.write(q1 + ":" + a1 + "\n");
        fw.write(q2 + ":" + a2 + "\n");
        fw.write(q3 + ":" + a3 + "\n");
    } catch (Exception e) {
        e.printStackTrace();
    }
}

// Retrieve security questions and answers
public static String[][] getSecurityQA() {
    File f = new File(SECURITY_FILE);
    if (!f.exists()) return null;

    String[][] qa = new String[3][2];
    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
        for (int i = 0; i < 3; i++) {
            String line = br.readLine();
            if (line == null) break;
            String[] parts = line.split(":", 2);
            qa[i][0] = parts[0]; // question
            qa[i][1] = parts[1]; // answer
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return qa;
}


    private static final String USERNAME_FILE = "username.txt";

public static void saveUserName(String name) {
    try (FileWriter fw = new FileWriter(USERNAME_FILE)) {
        fw.write(name.trim());
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public static String getUserName() {
    File f = new File(USERNAME_FILE);
    if (!f.exists()) return null;

    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
        return br.readLine();
    } catch (Exception e) {
        return null;
    }
}

    public static void save(List<PasswordEntry> list) {
        if (currentMaster == null) return;
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(passwords))) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream tempOos = new ObjectOutputStream(baos);
            tempOos.writeObject(list);
            tempOos.close();

            String serialized = Base64.getEncoder().encodeToString(baos.toByteArray());
            String encrypted = EncryptionUtil.encrypt(serialized, currentMaster);

            oos.writeObject(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static List<PasswordEntry> load(String masterPassword) {
        File f = new File(passwords);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(passwords))) {
            String encrypted = (String) ois.readObject();
            String decrypted = EncryptionUtil.decrypt(encrypted, masterPassword);

            byte[] data = Base64.getDecoder().decode(decrypted);
            ObjectInputStream tempOis = new ObjectInputStream(new ByteArrayInputStream(data));
            List<PasswordEntry> list = (List<PasswordEntry>) tempOis.readObject();
            tempOis.close();
            currentMaster = masterPassword;
            return list;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void setCurrentMaster(String password) {
        currentMaster = password;
    }
}
