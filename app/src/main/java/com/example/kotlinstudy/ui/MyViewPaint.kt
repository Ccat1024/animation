package com.example.kotlinstudy.ui

import android.content.Context
import android.graphics.*
import android.view.View

class MyViewPaint(mContext: Context) : View(mContext){



    private fun getPaint() : Paint{
        var mPaint = Paint()
        mPaint.color = Color.GREEN
        mPaint.isAntiAlias = true  //抗锯齿
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 4f
        //设置线冒样式，取值有Cap.ROUND(圆形线冒)、Cap.SQUARE(方形线冒)、Paint.Cap.BUTT(无线冒)
        mPaint.strokeCap = Paint.Cap.BUTT
        //设置线段连接处样式，取值有：Join.MITER（结合处为锐角）、Join.Round(结合处为圆弧)、Join.BEVEL(结合处为直线)
        mPaint.strokeJoin = Paint.Join.ROUND
        //mPaint.pathEffect = DiscretePathEffect(6f,5f)//离散路径效果
        //mPaint.pathEffect = CornerPathEffect(50f)//圆弧路径效果
        //mPaint.pathEffect = DashPathEffect(floatArrayOf(20f, 10f, 50f, 20f),15f)//虚线路径效果
        val dashPathEffect = DashPathEffect(floatArrayOf(20f, 10f, 50f, 20f), 15f)
        val cornerPathEffect = CornerPathEffect(50f)
        mPaint.pathEffect =  ComposePathEffect(dashPathEffect,cornerPathEffect)//合并路径效果
        return mPaint
    }


    private fun getPath(): Path{
        val path = Path()
        path.moveTo(0f,850f)

        for (i in 0..40){
            path.lineTo(i*35f, 850f+(Math.random()*150f).toFloat())
        }
        return path
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val paint = getPaint()
        val path = getPath()

        canvas?.drawPath(path,paint)
    }


}