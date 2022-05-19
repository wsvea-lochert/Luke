package com.cosinum.luke.util

import com.cosinum.luke.viewmodel.Coordinate
import java.lang.Math.atan
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt

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
            if ((leftHip.x * 224 < 5 && leftHip.y * 224 < 5) || (rightHip.x * 224 < 5 && rightHip.y * 224 < 5)) {
                return "stop"
            }
            else if ((leftElbow.x * 224 < 5 && leftElbow.y * 224 < 5) || (rightElbow.x * 224 < 5 && rightElbow.y * 224 < 5)) {
                return "stop"
            }
            else if ((leftWrist.x * 224 < 5 && leftWrist.y * 224 < 5) || (rightWrist.x * 224 < 5 && rightWrist.y * 224 < 5)) {
                return "stop"
            }
            else{
                val leftAngle = angleCalc(leftHip, leftShoulder, leftElbow)
                val rightAngle = angleCalc(rightHip, rightShoulder, rightElbow)

                if (isFacing) {
                    if (leftAngle in 50.0..110.0 && rightAngle in 50.0..110.0) {
                        return "stop"
                    }
                    else if (leftAngle in 50.0..110.0 && rightAngle !in 50.0..110.0) {
                        return "right, throttle:${throttleInput(angleCalc(leftShoulder, leftElbow, leftWrist))}"
                    }
                    else if (leftAngle !in 50.0..110.0 && rightAngle in 50.0..110.0) {
                        return "left, throttle:${throttleInput(angleCalc(rightShoulder, rightElbow, rightWrist))}"
                    }
                    else if (leftAngle in 115.0..180.0 && rightAngle in 115.0..180.0) {
                        return "reverse, throttle: 10%"
                    }
                    else if (leftAngle < 30 || rightAngle < 30) {
                        return "forward, throttle: 10%"
                    }
//                    else if (leftAngle in 115.0..180.0 && rightAngle in 115.0..180.0) {
//                        return "reverse"
//                    }
//                    else if (leftAngle in 115.0..190.0 || rightAngle in 115.0..190.0) {
//                        return "forward"
//                    }
                    else {
                        return "stop"
                    }
                }
                else {
                    if (leftAngle in 50.0..110.0 && rightAngle in 50.0..110.0) {
                        return "stop"
                    }
                    else if (leftAngle in 50.0..110.0 && rightAngle !in 50.0..110.0) {
                        return "left, throttle:${throttleInput(angleCalc(leftShoulder, leftElbow, leftWrist))}"
                    }
                    else if (leftAngle !in 50.0..110.0 && rightAngle in 50.0..110.0) {
                        return "right, throttle:${throttleInput(angleCalc(rightShoulder, rightElbow, rightWrist))}"
                    }
                    else if (leftAngle in 115.0..180.0 && rightAngle in 115.0..180.0) {
                        return "reverse, throttle: 10%"
                    }
                    else if (leftAngle in 115.0..180.0 || rightAngle in 115.0..180.0) {
                        return "forward, throttle: 10%"
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

    private fun throttleInput(angle: Double): String{
        return when (angle) {
            in 140.0..180.0 -> {
                "10%"
            }
            in 135.0..100.0 -> {
                "25%"
            }
            in 90.0..40.0 -> {
                "50%"
            }
            else -> {
                "10%"
            }
        }
    }

    private fun angleCalc(a: Coordinate, b: Coordinate, c: Coordinate): Double{
        // calculate the angle between a b and c
        val ab = sqrt((a.x.toDouble()*224 - b.x.toDouble()*224).pow(2) + (a.y.toDouble()*224 - b.y.toDouble()*224).pow(2))
        val bc = sqrt((b.x.toDouble()*224 - c.x.toDouble()*224).pow(2) + (b.y.toDouble()*224 - c.y.toDouble()*224).pow(2))
        val ac = sqrt((a.x.toDouble()*224 - c.x.toDouble()*224).pow(2) + (a.y.toDouble()*224 - c.y.toDouble()*224).pow(2))
        val angle = acos((ab.pow(2) + bc.pow(2) - ac.pow(2)) / (2 * ab * bc))
        // Convert angle to degrees
        val angleDeg = angle * 180 / Math.PI
        return angleDeg
    }

    private fun calculateAngle(a: Coordinate, b: Coordinate, c: Coordinate): Double {
        var ab = b.x * 500 - a.x * 500  // x2 - x1
        var bc = c.x * 500 - b.x * 500  // x3 - x2
        var ac = c.x * 500 - a.x * 500  // x3 - x1
        var ad = c.y * 500 - a.y * 500  // y3 - y1
        var bd = c.y * 500 - b.y * 500  // y3 - y2
        var cd = c.y * 500 - c.y * 500  // y3 - y3
        var abc = Math.sqrt(Math.pow(ab.toDouble(), 2.0) + Math.pow(ad.toDouble(), 2.0))  // sqrt(x2 - x1)^2 + (y2 - y1)^2
        var bcd = Math.sqrt(Math.pow(bc.toDouble(), 2.0) + Math.pow(bd.toDouble(), 2.0))  // sqrt(x3 - x2)^2 + (y3 - y2)^2
        var acd = Math.sqrt(Math.pow(ac.toDouble(), 2.0) + Math.pow(cd.toDouble(), 2.0))  // sqrt(x3 - x1)^2 + (y3 - y1)^2
        var angle = Math.acos((Math.pow(abc, 2.0) + Math.pow(bcd, 2.0) - Math.pow(acd, 2.0)) / (2 * abc * bcd)) * (180.0 / Math.PI)
        return if (angle < 0){
            180+angle
        } else{
            angle
        }

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