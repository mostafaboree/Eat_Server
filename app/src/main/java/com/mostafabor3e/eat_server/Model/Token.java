package com.mostafabor3e.eat_server.Model;

public class Token {
    private String tekon;
    private boolean istoken;

    public Token(String tekon, boolean istoken) {
        this.tekon = tekon;
        this.istoken = istoken;
    }

    public Token() {
    }

    public String getTekon() {
        return tekon;
    }

    public void setTekon(String tekon) {
        this.tekon = tekon;
    }

    public boolean isIstoken() {
        return istoken;
    }

    public void setIstoken(boolean istoken) {
        this.istoken = istoken;
    }
}
