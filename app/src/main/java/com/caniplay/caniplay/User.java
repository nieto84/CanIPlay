package com.caniplay.caniplay;

/**
 * Created by A on 17/11/2017.
 */

public class User {

    private String id;
    private String password;
    private String userName;
    private String fullName;
    private String email;
    private String roles[];


    public User(String id, String password, String userName, String fullName, String email, String[] roles) {
        this.id = id;
        this.password = password;
        this.userName = userName;
        this.fullName = fullName;
        this.email = email;
        this.roles = roles;
    }

    public User(String password, String userName, String fullName) {
        this.password = password;
        this.userName = userName;
        this.fullName = fullName;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }
}
