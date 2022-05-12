package com.cosinum.luke

import android.annotation.SuppressLint
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect
import android.util.AttributeSet;
import android.util.Log
import android.util.Log.d
import android.view.View;
import androidx.annotation.Nullable
import com.cosinum.luke.viewmodel.Coordinate
import kotlin.math.log

class RectOverlay(context: Context?, @Nullable attrs: AttributeSet?) : View(context, attrs) {

    var paint: Paint = Paint()
    var rPaint: Paint = Paint()
    var tPaint: Paint = Paint()
    var lPaint: Paint = Paint()
    var nPaint: Paint = Paint()
    var textPaint: Paint = Paint()

    var head: Coordinate = Coordinate(0.0f, 0.0f)
        set(value) {
            field = value
        }
    var left_ankle: Coordinate = Coordinate(0.0f, 0.0f)
        set(value) {
            field = value
        }
    var left_elbow: Coordinate = Coordinate(0.0f, 0.0f)
        set(value) {
            field = value
        }
    var left_hip: Coordinate = Coordinate(0.0f, 0.0f)
        set(value) {
            field = value
        }
    var left_knee: Coordinate = Coordinate(0.0f, 0.0f)
        set(value) {
            field = value
        }
    var left_shoulder: Coordinate = Coordinate(0.0f, 0.0f)
        set(value) {
            field = value
        }
    var left_wrist: Coordinate = Coordinate(0.0f, 0.0f)
        set(value) {
            field = value
        }
    var neck: Coordinate = Coordinate(0.0f, 0.0f)
        set(value) {
            field = value
        }
    var right_ankle: Coordinate = Coordinate(0.0f, 0.0f)
        set(value) {
            field = value
        }
    var right_elbow: Coordinate = Coordinate(0.0f, 0.0f)
        set(value) {
            field = value
        }
    var right_hip: Coordinate = Coordinate(0.0f, 0.0f)
        set(value) {
            field = value
        }
    var right_knee: Coordinate = Coordinate(0.0f, 0.0f)
        set(value) {
            field = value
        }
    var right_shoulder: Coordinate = Coordinate(0.0f, 0.0f)
        set(value) {
            field = value
        }
    var right_writs: Coordinate = Coordinate(0.0f, 0.0f)
        set(value) {
            field = value
        }
    var torso: Coordinate = Coordinate(0.0f, 0.0f)
        set(value) {
            field = value
        }


    init {
        paint.color = Color.BLUE
        paint.strokeWidth = 10.0f
        paint.style = Paint.Style.STROKE

        lPaint.color = Color.GREEN
        lPaint.strokeWidth = 10.0f
        lPaint.style = Paint.Style.STROKE

        rPaint.color = Color.RED
        rPaint.strokeWidth = 10.0f
        rPaint.style = Paint.Style.STROKE

        tPaint.color = Color.YELLOW
        tPaint.strokeWidth = 10.0f
        tPaint.style = Paint.Style.STROKE

        nPaint.color = Color.MAGENTA
        nPaint.strokeWidth = 10.0f
        nPaint.style = Paint.Style.STROKE

        textPaint.color = Color.WHITE
        textPaint.textSize = 30.0f
        textPaint.style = Paint.Style.FILL
        textPaint.strokeWidth = 10.0f

    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // DRAW STUFF HERE

        //draw text at the top of the screen

        if (head.xValue > 1.0f && head.yValue-140 > 1.0f) {
            canvas?.drawCircle(head.xValue,head.yValue, 10.0f, nPaint)
        }
        if (neck.xValue > 1.0f && neck.yValue-120 > 1.0f) {
            canvas?.drawCircle(neck.xValue,neck.yValue, 10.0f, paint)
        }
        if (left_ankle.xValue > 1.0f && left_ankle.yValue-110 > 1.0f) {
            canvas?.drawCircle(left_ankle.xValue,left_ankle.yValue, 10.0f, lPaint)
        }
        if (right_ankle.xValue > 1.0f && right_ankle.yValue-110 > 1.0f) {
            canvas?.drawCircle(right_ankle.xValue,right_ankle.yValue, 10.0f, rPaint)
        }
        if (left_elbow.xValue > 1.0f && left_elbow.yValue > 1.0f) {
            canvas?.drawCircle(left_elbow.xValue,left_elbow.yValue, 10.0f, lPaint)
        }
        if (right_elbow.xValue > 1.0f && right_elbow.yValue > 1.0f) {
            canvas?.drawCircle(right_elbow.xValue,right_elbow.yValue, 10.0f, rPaint)
        }
        if (left_hip.xValue > 1.0f && left_hip.yValue > 1.0f) {
            canvas?.drawCircle(left_hip.xValue,left_hip.yValue, 10.0f, lPaint)
        }
        if (right_hip.xValue > 1.0f && right_hip.yValue > 1.0f) {
            canvas?.drawCircle(right_hip.xValue,right_hip.yValue, 10.0f, rPaint)
        }
        if (left_knee.xValue > 1.0f && left_knee.yValue+60 > 1.0f) {
            canvas?.drawCircle(left_knee.xValue,left_knee.yValue, 10.0f, lPaint)
        }
        if (right_knee.xValue > 1.0f && right_knee.yValue+60 > 1.0f) {
            canvas?.drawCircle(right_knee.xValue,right_knee.yValue, 10.0f, rPaint)
        }
        if (left_shoulder.xValue > 1.0f && left_shoulder.yValue-100 > 1.0f) {
            canvas?.drawCircle(left_shoulder.xValue,left_shoulder.yValue, 10.0f, lPaint)
        }
        if (right_shoulder.xValue > 1.0f && right_shoulder.yValue-100 > 1.0f) {
            canvas?.drawCircle(right_shoulder.xValue,right_shoulder.yValue, 10.0f, rPaint)
        }
        if (left_wrist.xValue > 1.0f && left_wrist.yValue > 1.0f) {
            canvas?.drawCircle(left_wrist.xValue,left_wrist.yValue, 10.0f, lPaint)
        }
        if (right_writs.xValue > 1.0f && right_writs.yValue > 1.0f) {
            canvas?.drawCircle(right_writs.xValue,right_writs.yValue, 10.0f, rPaint)
        }
        if (torso.xValue > 1.0f && torso.yValue > 1.0f) {
            canvas?.drawCircle(torso.xValue,torso.yValue, 10.0f, tPaint)
        }

/*
     //canvas?.drawCircle(neck.xValue, neck.yValue, 10.0f, paint)
     canvas?.drawCircle(left_ankle.xValue,left_ankle.yValue, 10.0f, lPaint)
     canvas?.drawCircle(left_elbow.xValue,left_elbow.yValue, 10.0f, lPaint)
     canvas?.drawCircle(left_hip.xValue,left_hip.yValue, 10.0f, lPaint)
     canvas?.drawCircle(left_knee.xValue,left_knee.yValue, 10.0f, lPaint)
     canvas?.drawCircle(left_shoulder.xValue,left_shoulder.yValue, 10.0f, lPaint)
     canvas?.drawCircle(left_wrist.xValue,left_wrist.yValue, 10.0f, lPaint)
     canvas?.drawCircle(right_ankle.xValue,right_ankle.yValue, 10.0f, rPaint)
     canvas?.drawCircle(right_elbow.xValue,right_elbow.yValue, 10.0f, rPaint)
     canvas?.drawCircle(right_hip.xValue,right_hip.yValue, 10.0f, rPaint)
     canvas?.drawCircle(right_knee.xValue,right_knee.yValue, 10.0f, rPaint)
     canvas?.drawCircle(right_shoulder.xValue,right_shoulder.yValue, 10.0f, rPaint)
     canvas?.drawCircle(right_writs.xValue,right_writs.yValue, 10.0f, rPaint)
     canvas?.drawCircle(torso.xValue,torso.yValue, 10.0f, tPaint)*/
     invalidate()
 /*
     head?.let {
         canvas?.drawCircck.xValue, neck.yValue, 10.0f, paint)
         canvas?.drawCircle(left_ankle.xValue,left_ankle.yValue, 10.0f, lPaint)
         canvas?.drawCircle(left_elbow.xValue,left_elbow.yValue, 10.0f, lPaint)
         canvas?.drawCircle(left_hip.xValue,left_hip.yValue, 10.0f, lPaint)
         canvas?.drawCircle(left_knee.xValue,left_knee.yValue, 10.0f, lPaint)
         canvas?.drawCircle(left_shoulder.xValue,left_shoulder.yValue, 10.0f, lPaint)
         canvas?.drawCircle(left_wrist.xValue,left_wrist.yValue, 10.0f, lPaint)
         canvas?.drawCircle(neck.xValue,neck.yValue, 10.0f, nPaint)
         canvas?.drawCircle(right_ankle.xValue,right_ankle.yValue, 10.0f, rPaint)
         canvas?.drawCircle(right_elbow.xValue,right_elbow.yValue, 10.0f, rPaint)
         canvas?.drawCircle(right_hip.xValue,right_hip.yValue, 10.0f, rPaint)
         canvas?.drawCircle(right_knee.xValue,right_knee.yValue, 10.0f, rPaint)
         canvas?.drawCircle(right_shoulder.xValue,right_shoulder.yValue, 10.0f, rPaint)
         canvas?.drawCircle(right_writs.xValue,right_writs.yValue, 10.0f, rPaint)
         canvas?.drawCircle(torso.xValue,torso.yValue, 10.0f, tPaint)
         // canvas?.drawCircle(1080.0f/2, 2097.0f/2, 5.0f, paint)
         invalidate()
     }
     //invalidate()
*/

 }
}
