package edu.codespring.listist.backend.model;

public class User extends BaseEntity {
    private String username;
    private String password;
    private String email;

    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public void setEmail(String email) { this.email = email; }

    public String getEmail() { return email; }

    @Override
    public String toString() {
        return "User{" +
                "username= '" + username + '\'' +
                ", password= '" + password + '\'' +
                ", email= '" + email + '\'' +
                ", id= '" + getId() + '\'' +
                ", uuid= '" + getUuid() + '\'' +
                '}';
    }
}
