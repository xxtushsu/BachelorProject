package com.example.myapplication10;

public class UsersInfo {
    String username;
    String isBackgroundLocationGranted, isForegroundLocationGranted, isCameraGranted;

    public UsersInfo(){

    }

    public UsersInfo(String username, String isBackgroundLocationGranted, String isForegroundLocationGranted, String isCameraGranted){
        this.username = username;
        this.isBackgroundLocationGranted = isBackgroundLocationGranted;
        this.isForegroundLocationGranted = isForegroundLocationGranted;
        this.isCameraGranted = isCameraGranted;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    public String getUsername() {
        return username;
    }
}
