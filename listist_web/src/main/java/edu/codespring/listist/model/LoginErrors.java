package edu.codespring.listist.model;

public class LoginErrors {
    private String usernameError;
    private String pswError;

    public LoginErrors() {
        usernameError = null;
        pswError = null;
    }

    public String getUsernameError() {
        return usernameError;
    }

    public void setUsernameError(String usernameError) {
        this.usernameError = usernameError;
    }

    public String getPswError() {
        return pswError;
    }

    public void setPswError(String pswError) {
        this.pswError = pswError;
    }
}
