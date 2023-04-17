package com.example.kotlinstudy.ui

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class MyViewMeasure(mContext: Context, attrs: AttributeSet? = null) : View(mContext) {


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measureWidth = MeasureSpec.getSize(widthMeasureSpec)
        val measureHeight = MeasureSpec.getSize(heightMeasureSpec)
        val measureWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        val measureHeightMode = MeasureSpec.getMode(widthMeasureSpec)


    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)


    }

}