package com.mostafabor3e.eat_server.Model;

public class User {
    private String name;
    private String password;
    private  String phone;
    private boolean IsStaff;

    public User() {

    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public User(String name, String password, String phone) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.IsStaff=true;
    }

    public boolean isStaff() {
        return IsStaff;
    }

    public void setStaff(boolean staff) {
        IsStaff = staff;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
