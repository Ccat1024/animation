package com.example.kotlinstudy.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class MyViewWrite(mContext: Context,attrs: AttributeSet? = null):View(mContext) {
    private var mPath = Path()
    private var mPreX :Float? = null
    private var mPreY :Float? = null


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val paint = Paint()
        paint.color = Color.GREEN
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 20f

        //paint.setShadowLayer(10f,15f,15f,Color.GREEN)//添加阴影 文字有效
        canvas?.drawLine(100f,100f,200f,200f,paint)//绘制直线
        canvas?.drawPoint(0f,0f,paint)//绘制点
        canvas?.drawPath(mPath,paint)

        RectF()//矩形区域

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN->{
                mPath.moveTo(event.x,event.y)
                mPreX = event.x
                mPreY = event.y
                return true
            }
            MotionEvent.ACTION_MOVE->{
                val endX = (mPreX!!+event.x)/2
                val  endY = (mPreY!!+event.y)/2
                mPath.quadTo(mPreX!!,mPreY!!,endX,endY)
                mPreX = event.x
                mPreY =event.y
                postInvalidate()
            }

        }

        return super.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val specSize_width = MeasureSpec.getSize(widthMeasureSpec)
        val specSize_height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(specSize_width,specSize_height)

    }


    fun reset(){
        mPath.reset()
        invalidate()
    }

}