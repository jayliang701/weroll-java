package com.magicfish.weroll.model;

public class User {

    private Long id;

    private String username;

    private String password;

    private String JS;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUserame(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJS() {
        return JS;
    }

    public void setJS(String JS) {
        this.JS = JS;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + ", JS='" + JS + '\'' +
                '}';
    }
}
