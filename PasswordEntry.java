
import java.io.Serializable;

public class PasswordEntry implements Serializable {
    
    private String appName;
    private String username;
    private String password;
    private String description;
    private String category;

    public PasswordEntry(String appName, String username, String password, String description, String category) {
        this.appName = appName;
        this.username = username;
        this.password = password;
        this.description = description;
        this.category = category;
    }

    public String getAppName() { return appName; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }

    public void setAppName(String appName) { this.appName = appName; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return appName + " | " + username + " | " + password + " | " + description + " | " + category;
    }

    @Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PasswordEntry)) return false;

    PasswordEntry p = (PasswordEntry) o;
    return appName.equals(p.appName)
            && username.equals(p.username)
            && category.equals(p.category);
}

}
