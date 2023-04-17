package com.example.kotlinstudy.ui

import android.content.Context
import android.graphics.*
import android.view.View
/**
 * path ,text
 *
 */
class MyView2(mContext: Context) : View(mContext) {

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val paint = Paint();
        paint.isAntiAlias =true
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5F

        paint.textSize = 80F


        val path = Path();

        path.moveTo(10F,300F)
        path.lineTo(10F,400F)
        path.lineTo(300F,400F)
        path.lineTo(500F,400F)
        path.close()

        //text
        val font = Typeface.create("宋体", Typeface.NORMAL)
        paint.typeface = font

        canvas?.drawText("123456",10F,300F,paint)
        canvas?.drawTextOnPath("123456",path,0F,0F,paint)
        canvas?.drawPath(path,paint)
    }

}