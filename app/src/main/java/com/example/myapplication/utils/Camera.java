package com.example.myapplication.utils;

import java.io.Serializable;

public class Camera implements Serializable {
    public String[] getName() {
        return name;
    }

    public void setName(String[] name) {
        this.name = name;
    }

    private String[] name;

    public int[] getIcon() {
        return icon;
    }

    public void setIcon(int[] icon) {
        this.icon = icon;
    }

    private int[] icon;


}
