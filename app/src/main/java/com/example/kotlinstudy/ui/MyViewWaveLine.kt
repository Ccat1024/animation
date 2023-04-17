package com.example.kotlinstudy.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class MyViewWaveLine(mContext: Context, attrs: AttributeSet? = null) : View(mContext, attrs) {

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.color = Color.RED

        val path = Path()
        path.moveTo(100f, 300f)
        path.quadTo(200f, 200f, 300f, 300f)
        path.quadTo(400f, 400f, 500f, 300f)

        canvas?.drawPath(path, paint)

    }
}