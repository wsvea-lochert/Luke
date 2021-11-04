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

    var head: Coordinate = Coordinate(0.0f, 0.0f)
    /*var left_ankle: Coordinate = Coordinate(0.0f, 0.0f)
    var left_elbow: Coordinate = Coordinate(0.0f, 0.0f)
    var left_hip: Coordinate = Coordinate(0.0f, 0.0f)
    var left_knee: Coordinate = Coordinate(0.0f, 0.0f)
    var left_shoulder: Coordinate = Coordinate(0.0f, 0.0f)
    var left_wrist: Coordinate = Coordinate(0.0f, 0.0f)
    var neck: Coordinate = Coordinate(0.0f, 0.0f)
    var right_ankle: Coordinate = Coordinate(0.0f, 0.0f)
    var right_elbow: Coordinate = Coordinate(0.0f, 0.0f)
    var right_hip: Coordinate = Coordinate(0.0f, 0.0f)
    var right_knee: Coordinate = Coordinate(0.0f, 0.0f)
    var right_shoulder: Coordinate = Coordinate(0.0f, 0.0f)
    var right_writs: Coordinate = Coordinate(0.0f, 0.0f)
    var torso: Coordinate = Coordinate(0.0f, 0.0f)*/


    init {
        paint.color = Color.BLUE
        paint.strokeWidth = 10.0f
        paint.style = Paint.Style.STROKE
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // DRAW STUFF HERE
        //Log.d("ADebugTagFromDraw", head.x.toString())
        head?.let { canvas?.drawCircle(this.head.x, this.head.y, 20.0f, paint)
            invalidate()
        }
        invalidate()
        /*let {

        //    canvas?.drawCircle((head?.x*22.4f), head?.y*22, 100.0f, paint)
            //rectBox?.let { canvas?.drawRect(it, paint)
        //    invalidate()

        //}
       // invalidate()
*/
    }
}
