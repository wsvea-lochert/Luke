package com.cosinum.luke.util

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.cosinum.luke.viewmodel.Coordinate

class JamesAngles(private val joints: List<Coordinate>) {
    // private var person = person()
    private var leftShoulder: Coordinate = joints[5]
    private var rightShoulder: Coordinate = joints[12]
    private var leftElbow: Coordinate = joints[2]
    private var rightElbow: Coordinate = joints[9]
    private var leftWrist: Coordinate = joints[6]
    private var rightWrist: Coordinate = joints[13]
    private var head: Coordinate = joints[0]
    private var torso: Coordinate = joints[14]
    private var neck: Coordinate = joints[7]
    private var leftHip: Coordinate = joints[3]
    private var rightHip: Coordinate = joints[10]
    private var person: Boolean = person()
    private var isFacing: Boolean = isFacing()

    fun getSignal(): String {
        if (person) {
            if ((leftHip.x * 224 < 20 && leftHip.y * 224 < 20) || (rightHip.x * 224 < 20 && rightHip.y * 224 < 20)) {
                return "stop"
            }
            else if ((leftElbow.x * 224 > 20 && leftElbow.y * 224 < 20) || (rightElbow.x * 224 > 20 && rightElbow.y * 224 < 20)) {
                return "stop"
            }
            else if ((leftWrist.x * 224 > 20 && leftWrist.y * 224 < 20) || (rightWrist.x * 224 > 20 && rightWrist.y * 224 < 20)) {
                return "stop"
            }
            else{
                val leftAngle = calculateAngle(leftHip, leftShoulder, leftElbow)
                val rightAngle = calculateAngle(rightHip, rightShoulder, rightElbow)

                if (isFacing) {
                    if (leftAngle in 50.0..110.0 && rightAngle in 50.0..110.0) {
                        return "stop"
                    }
                    else if (leftAngle in 50.0..110.0 && rightAngle !in 50.0..110.0) {
                        return "right"
                    }
                    else if (leftAngle !in 50.0..110.0 && rightAngle in 50.0..110.0) {
                        return "left"
                    }
                    else if (leftAngle in 120.0..180.0 && rightAngle in 120.0..180.0) {
                        return "reverse"
                    }
                    else if (leftAngle in 120.0..180.0 || rightAngle in 120.0..180.0) {
                        return "forward"
                    }
                    else {
                        return "stop"
                    }
                }
                else {
                    if (leftAngle in 50.0..110.0 && rightAngle in 50.0..110.0) {
                        return "stop"
                    }
                    else if (leftAngle in 50.0..110.0 && rightAngle !in 50.0..110.0) {
                        return "left"
                    }
                    else if (leftAngle !in 50.0..110.0 && rightAngle in 50.0..110.0) {
                        return "right"
                    }
                    else if (leftAngle in 120.0..180.0 && rightAngle in 120.0..180.0) {
                        return "reverse"
                    }
                    else if (leftAngle in 120.0..180.0 || rightAngle in 120.0..180.0) {
                        return "forward"
                    }
                    else {
                        return "stop"
                    }
                }
            }
        }
        return "stop"
    }

    private fun isFacing(): Boolean {
        return leftShoulder.x > rightShoulder.x
    }

    private fun calculateAngle(a: Coordinate, b: Coordinate, c: Coordinate): Double {
        var ab = b.x * 224 - a.x * 224  // x2 - x1
        var bc = c.x * 224 - b.x * 224  // x3 - x2
        var ac = c.x * 224 - a.x * 224  // x3 - x1
        var ad = c.y * 224 - a.y * 224  // y3 - y1
        var bd = c.y * 224 - b.y * 224  // y3 - y2
        var cd = c.y * 224 - c.y * 224  // y3 - y3
        var abc = Math.sqrt(Math.pow(ab.toDouble(), 2.0) + Math.pow(ad.toDouble(), 2.0))  // sqrt(x2 - x1)^2 + (y2 - y1)^2
        var bcd = Math.sqrt(Math.pow(bc.toDouble(), 2.0) + Math.pow(bd.toDouble(), 2.0))  // sqrt(x3 - x2)^2 + (y3 - y2)^2
        var acd = Math.sqrt(Math.pow(ac.toDouble(), 2.0) + Math.pow(cd.toDouble(), 2.0))  // sqrt(x3 - x1)^2 + (y3 - y1)^2
        var angle = Math.acos((Math.pow(abc, 2.0) + Math.pow(bcd, 2.0) - Math.pow(acd, 2.0)) / (2 * abc * bcd)) * (180.0 / Math.PI)
        return angle
    }

    private fun person(): Boolean {
        var counter = 0
        for (joint in joints) {
            if (joint.x < 20 && joint.y < 20) {
                counter++
            }
        }

        return counter > 4
    }

}