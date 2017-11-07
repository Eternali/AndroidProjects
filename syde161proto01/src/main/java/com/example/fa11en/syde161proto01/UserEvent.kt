package com.example.fa11en.syde161proto01

import android.location.Location
import java.util.Date
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class UserEvent (val type: EventType) {

    var params: HashMap<ParameterTypes, Any> = hashMapOf()

    fun setParam (key: ParameterTypes, value: Any) {
        if (key in type.parameters && checkParamType(key, value))
            this.params.set(key, value)
    }

    companion object {
        fun checkParamType (key: ParameterTypes, value: Any): Boolean {
            when (key) {
                ParameterTypes.TITLE -> return value is String
                ParameterTypes.DESCRIPTION -> return value is String
                ParameterTypes.DATETIME -> return value is Date
                ParameterTypes.LOCATION -> return value is Location
                ParameterTypes.ENTITIES -> return value is Person
                ParameterTypes.REPEAT -> return value is ArrayList<*>
            }
        }
    }

}