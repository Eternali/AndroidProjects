package com.example.fa11en.syde161proto01

import java.util.*


data class UserEvent (var type: EventTypes = EventTypes.EVENT,
                      var time: Date = Date(),
                      var title: String = "",
                      var desc: String = "") {

}