package com.example.kotlinstudy.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.View


/**
 * range
 *
 */
class MyViewRange(mContext: Context) : View(mContext) {


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //初始画笔
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2F


        val ovalPath = Path()
        val rect = RectF(50F, 50F, 200F, 500F)
        ovalPath.addOval(rect, Path.Direction.CCW)

        val region = Region(50, 50, 200, 200)

        region.setPath(ovalPath,region)

        drawRange(canvas, region, paint)
    }

    private fun drawRange(canvas: Canvas?, rgn: Region, paint: Paint) {
        val iter = RegionIterator(rgn)
        val r = Rect()

        while (iter.next(r)) {
            canvas?.drawRect(r, paint)
        }

    }
}