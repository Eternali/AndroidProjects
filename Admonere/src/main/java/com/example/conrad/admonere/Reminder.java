package com.example.conrad.admonere;

// Class used to store the data for each reminder
public class Reminder {

    // Data variables
    String[] date;
    String[] time;
    String name;
    String number;
    String message;

    /**
     * Constructor for class to store the data passed into it
     * pre: the parameters are of the proper form
     * post: the class variables will be saved
     * @param date
     * @param time
     * @param name
     * @param number
     * @param message
     */
    public Reminder (String[] date, String[] time, String name, String number, String message) {
        this.date = date;
        this.time = time;
        this.name = name;
        this.number = number;
        this.message = message;
    }

}
