package com.example.urbanin.account;

public class AccountOption {
    private int icon;
    private String title;

    public AccountOption(int icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }
}
