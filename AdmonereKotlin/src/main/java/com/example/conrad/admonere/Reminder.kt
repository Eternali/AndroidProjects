package com.example.conrad.admonere

// class used to store the data for each reminder
data class Reminder(val dates: Array<Array<String>>,
                    val time: Array<String>,
                    val name: String,
                    val number: String,
                    val message: String) {

}