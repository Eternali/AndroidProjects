package com.example.conrad.admonerekt

// Class used to store the data for each reminder
class Reminder
/**
 * Constructor for class to store the data passed into it
 * pre: the parameters are of the proper form
 * post: the class variables will be saved
 * @param date
 * *
 * @param time
 * *
 * @param name
 * *
 * @param number
 * *
 * @param message
 */
(// Data variables
        internal var date: Array<String>,
        internal var time: Array<String>,
        internal var name: String,
        internal var number: String,
        internal var message: String)