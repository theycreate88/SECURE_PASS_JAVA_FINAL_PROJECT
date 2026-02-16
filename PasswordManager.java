

import java.util.*;

public class PasswordManager {
    private static PasswordManager instance;
    private List<PasswordEntry> list = new ArrayList<>();
    private String master;

    private PasswordManager(){}

    public static PasswordManager getInstance() {
        if (instance == null) instance = new PasswordManager();
        return instance;
    }

    public void load(String m) {
        master = m;
        list = FileStorage.load(m);
    }

    public void save() {
        FileStorage.save(list);
    }

    public void add(PasswordEntry p) {
        list.add(p);
    }

    public List<PasswordEntry> getAll() {
        return list;
    }
}
