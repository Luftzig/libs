package me.libs.utils;

/**
 * Represents user login. Given password should be the plaintext and it will be encrypted.
 * Created by luftzug on 2015-4-3.
 */
public class UserLogin {
    private String username;
    private String email;
    private String password; // TODO: Use real encrypted password

    public UserLogin(String username, String password) {
        this(username, null, password);
    }

    public UserLogin(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.setPassword(password);  // Encrypts
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    // TODO: encrypt password on saving
    public void setPassword(String password) {
        this.password = password;
    }

    // TODO: return the encrypted password
    public String getPassword() {
        return password;
    }
}
