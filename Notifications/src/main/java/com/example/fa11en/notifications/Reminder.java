package com.example.fa11en.notifications;

public class Reminder {

    String[] date;
    String[] time;
    String name;
    String number;
    String message;

    public Reminder (String[] date, String[] time, String name, String number, String message) {
        this.date = date;
        this.time = time;
        this.name = name;
        this.number = number;
        this.message = message;
    }

}
