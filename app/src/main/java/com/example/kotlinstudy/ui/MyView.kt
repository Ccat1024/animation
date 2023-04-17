package com.example.kotlinstudy.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

/**
 * canvas paint
 *
 */
class MyView(mContext: Context) : View(mContext) {

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val paint = Paint();
        paint.isAntiAlias =true
        paint.color = Color.RED
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 5F
        paint.setShadowLayer(10F, 15F, 15F, Color.GREEN)

        canvas?.drawRGB(255, 255,255)
        canvas?.drawCircle(150F, 200F, 150F, paint)
    }

}