package com.example.conrad.admonere

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent

/**
 * This is a class meant to detect basic touch gestures like horizontal or vertical swipes.
 */
class GestureDetector : GestureDetector.SimpleOnGestureListener () {

    // override the onFling method to implement swipe motions
    override fun onFling(e1 : MotionEvent?, e2 : MotionEvent?, velocityX : Float, velocityY : Float) : Boolean {
        try {
            // check if the input is valid and if the motion is within tolerances
            if (e1 !is MotionEvent || e2 !is MotionEvent) return false
            if (Math.abs(e1.y - e2.y) > SWIPE_MAX_OFF_PATH) return false

            // if its a right to left swipe then call onRightSwipe()
            if (e1.x - e2.x > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) onLeftSwipe()
            // if its a left to right swipe then call onLeftSwipe()
            else if (e2.x - e1.x > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)  onRightSwipe()
        } catch (e : Exception) { Log.e("Gesture Error", e.toString()) }

        return false
    }

    // method to call when the user swipes left
    private fun onLeftSwipe() {

    }

    // method to call when the user swipes right
    private fun onRightSwipe() {

    }

}