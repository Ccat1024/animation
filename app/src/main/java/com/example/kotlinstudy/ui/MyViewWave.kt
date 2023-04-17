package com.example.kotlinstudy.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator


class MyViewWave(mContext: Context, attrs: AttributeSet? = null ) : View(mContext) {

    private var mPaint: Paint = Paint()
    private var mPath: Path = Path()
    private var mItemWaveLength: Float = 800F
    private var dx: Float = 0f
    private var dy: Float = 0f

    init {
        mPaint.color = Color.GREEN
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 5f
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)


        val measureWidth = MeasureSpec.getSize(widthMeasureSpec)
        val measureHeight = MeasureSpec.getSize(heightMeasureSpec)
        val measureWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        val measureHeightMode = MeasureSpec.getMode(widthMeasureSpec)



        val height = 0
        val width = 0

    }



    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mPath.reset()
        val originY = 250F
        val halfWaveLen = mItemWaveLength / 2
        mPath.moveTo(-mItemWaveLength + dx, originY + dy)
        var i = -mItemWaveLength
        for (i in -mItemWaveLength.toInt() until width + mItemWaveLength.toInt() step mItemWaveLength.toInt()) {
            mPath.rQuadTo(halfWaveLen / 2, -100F, halfWaveLen, 0F)
            mPath.rQuadTo(halfWaveLen / 2, 100F, halfWaveLen, 0F)
        }
        mPath.lineTo(width.toFloat(), originY+height.toFloat())
        mPath.lineTo(0F, originY+height.toFloat())
        mPath.close()

        //圆形区域
        val cPath = Path()
        cPath.addCircle(250f, originY, 200f, Path.Direction.CCW)
        val cRegion = Region()
        cRegion.setPath(cPath, Region(0, 0, 500, 500))
        mPaint.style = Paint.Style.STROKE
        canvas?.drawCircle(250f,originY,200f,mPaint)
        //drawRange(canvas,cRegion,mPaint)

        //wave区域
        val waveRegion = Region()
        waveRegion.setPath(mPath, Region(0,0, 500, 500))
        //drawRange(canvas, waveRegion, mPaint)

        //取交集绘制
        cRegion.op(waveRegion, Region.Op.INTERSECT)
        mPaint.style = Paint.Style.FILL
        drawRange(canvas,cRegion,mPaint)
        //canvas?.drawCircle(200f,1500f,200f,mPaint)
        //mPaint.style = Paint.Style.STROKE
        //canvas?.drawPath(mPath, mPaint)

    }


    fun startMove() {

        val animatorX = ValueAnimator.ofFloat(0F, mItemWaveLength)
        animatorX.duration = 2000
        animatorX.repeatCount = ValueAnimator.INFINITE
        animatorX.interpolator = LinearInterpolator()
        val animatorY = ValueAnimator.ofFloat(0F, 150F, 0F)
        animatorY.duration = 9000
        animatorY.repeatCount = ValueAnimator.INFINITE
        animatorY.interpolator = LinearInterpolator()


        animatorX.addUpdateListener {
            dx = it?.animatedValue as Float
            dy = animatorY.animatedValue as Float
            postInvalidate()
        }
        animatorX.start()
        animatorY.start()
    }

    private fun drawRange(canvas: Canvas?, rgn: Region, paint: Paint) {
        val iter = RegionIterator(rgn)
        val r = Rect()
        while (iter.next(r)) {
            canvas?.drawRect(r, paint)
        }
    }
}