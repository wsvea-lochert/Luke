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
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // DRAW STUFF HERE

        head?.let {
            canvas?.drawCircle(head.xValue,head.yValue, 10.0f, paint)
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


    }
}
