package edu.codespring.listist.model;

public class RegisterErrors {
    String usernameError;
    String emailError;
    String success;

    public RegisterErrors() {
        usernameError = null;
        emailError = null;
    }

    public String getUsernameError() {
        return usernameError;
    }

    public void setUsernameError(String usernameError) {
        this.usernameError = usernameError;
    }

    public String getEmailError() {
        return emailError;
    }

    public void setEmailError(String emailError) { this.emailError = emailError; }
}
