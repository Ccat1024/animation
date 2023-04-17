package com.example.kotlinstudy.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import com.example.kotlinstudy.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class LargeScan(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    //View宽高
    private var mWidth = 0f
    private var mHeight = 0f


    //外圆画笔
    private val mCircleBorderPaint: Paint by lazy {
        Paint().apply {
            //外圆画笔
            style = Paint.Style.STROKE
            color = Color.parseColor("#eef0f5")
            strokeWidth = dip2px(12f)
            isAntiAlias = true
        }
    }

    //波纹画笔
    private val mRipplePaint: Paint by lazy {
        Paint().apply {
            //外圆画笔
            style = Paint.Style.STROKE
            color = Color.parseColor("#eef0f5")
            strokeWidth = dip2px(2f)
            isAntiAlias = true
            alpha = 255
        }
    }

    //弧线画笔
    private val mArcPaint: Paint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            //color = mArcColor
            strokeWidth = dip2px(4f)
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
        }
    }


    //scan 画笔
    private val mScanPaint: Paint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            color = Color.parseColor("#eef0f5")
            isAntiAlias = true
        }
    }


    private var textSize = "0.00"
    private var textUnit = "KB"

    //弧线颜色
    private var mArcColor: Int = Color.parseColor("#19d9ff")

    //中心点X、Y坐标
    private var mCenterX = 0f
    private var mCenterY = 0f

    //动画时间
    private var durationAnim = 4 * 1000L

    //圆半径
    private var mRadius = 0f

    //开始的角度
    private var offsetAngle = -90f

    //旋转角度
    private var scanAngle = 0f

    //圆的弧度
    private var mRadian = 0f

    //绘制范围
    private var mRect = RectF()


    //圆环间距
    private var spacingArc = dip2px(20f)

    //圆环间距
    private var spacingCircle = dip2px(60f)

    //圆环扩散间距
    private var circleRange = 0f

    //圆环扩散间距
    private var waveRange = 0f

    //wave是否绘制
    private var isShowWave = false


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()
        mHeight = h.toFloat()

        //取出padding值
        val paddingLeft = paddingLeft
        val paddingRight = paddingRight
        val paddingTop = paddingTop
        val paddingBottom = paddingBottom

        //绘制范围
        mRect.left = paddingLeft.toFloat() + spacingArc + spacingCircle
        mRect.top = paddingTop.toFloat() + spacingArc + spacingCircle
        mRect.right = mWidth - paddingRight - spacingArc - spacingCircle
        mRect.bottom = mHeight - paddingBottom - spacingArc - spacingCircle
        val diameter = min(mWidth, mHeight)
        mRadius = (diameter / 2 * 0.98).toFloat()


        //计算圆心的坐标
        mCenterX = mWidth / 2
        mCenterY = mHeight / 2
        startAnimator()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measureSpec(widthMeasureSpec), measureSpec(heightMeasureSpec))
    }

    private fun measureSpec(measureSpec: Int): Int {
        val result: Int
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        //默认大小
        val defaultSize = dip2px(30f).toInt()
        //指定宽高则直接返回
        if (mode == MeasureSpec.EXACTLY) {
            result = size
        } else if (mode == MeasureSpec.AT_MOST) {
            //wrap_content的情况
            result = defaultSize.coerceAtMost(size)
        } else {
            //未指定，则使用默认的大小
            result = defaultSize
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.scale(0.87f, 0.87f, mCenterX, mCenterY)

        canvas.drawColor(0)//绘制透明色


        //绘制外圆
        mCircleBorderPaint.strokeWidth = dip2px(12f)
        canvas.drawCircle(
            mCenterX,
            mCenterY,
            mRadius + circleRange - spacingCircle,
            mCircleBorderPaint
        )

        //画内圆
        mCircleBorderPaint.strokeWidth = dip2px(4f)
        canvas.drawCircle(mCenterX, mCenterY, mRect.right - mCenterX, mCircleBorderPaint)

        if (isShowWave) {
            //绘制波纹
            canvas.drawCircle(
                mCenterX,
                mCenterY,
                mRadius + 60f + waveRange - spacingCircle,
                mRipplePaint
            )
        }

        //画弧线
        canvas?.drawArc(mRect, 90f, mRadian, false, mArcPaint)

        //绘制 scan
        drawScan(canvas)

    }


    private fun drawScan(canvas: Canvas?) {
        //绘制范围
        var mRectScan = RectF()
        //绘制范围
        mRectScan.left = mRect.left + spacingArc
        mRectScan.top = mRect.top + spacingArc
        mRectScan.right = mRect.right - spacingArc
        mRectScan.bottom = mRect.bottom - spacingArc

        canvas?.drawArc(mRectScan, offsetAngle, scanAngle, true, mScanPaint)

    }


    private fun startAnimator() {
        startLargeAnim()
        startArcAnim()
        //startColorAnim()
        startWaveAnim()

    }

    private fun startLargeAnim() {
        //scan 面积动画
        val animatorScan = ValueAnimator.ofFloat(0f, 90f, 0f)
        animatorScan.interpolator = LinearInterpolator()
        animatorScan.duration = 1000
        animatorScan.repeatMode = ValueAnimator.RESTART
        animatorScan.repeatCount = ValueAnimator.INFINITE
        animatorScan.addUpdateListener { animation -> //更新旋转角度
            scanAngle = animation.animatedValue as Float
            postInvalidate()
        }

        val animatorScanStart = ValueAnimator.ofFloat(-90f, 270f)
        animatorScanStart.interpolator = LinearInterpolator()
        animatorScanStart.duration = 1000
        animatorScanStart.repeatMode = ValueAnimator.RESTART
        animatorScanStart.repeatCount = ValueAnimator.INFINITE
        animatorScanStart.addUpdateListener { animation -> //更新旋转角度
            offsetAngle = animation.animatedValue as Float
            postInvalidate()
        }

        animatorScanStart.start()
        animatorScan.start()
    }


    private fun startArcAnim() {

        //弧度增长
        val animatorRadian = ValueAnimator.ofFloat(0f, 360f)
        animatorRadian.interpolator = LinearInterpolator()
        animatorRadian.duration = 4000
        animatorRadian.addUpdateListener { animation -> //更新弧度
            mRadian = animation.animatedValue as Float
            postInvalidate()
        }
        animatorRadian.doOnStart {
            startColorAnim()
        }
        animatorRadian.start()

    }


    private fun startColorAnim() {
        val redAnimator = ValueAnimator.ofInt(25, 255).apply {
            duration = 2000
        }
        val greenAnimator = ValueAnimator.ofInt(217, 89).apply {
            duration = 2000
        }
        val blueAnimator = ValueAnimator.ofInt(255, 89).apply {
            duration = 2000
        }
        redAnimator.addUpdateListener {
            var value = it.animatedValue as Int
            //圆弧
            mArcPaint.color = Color.rgb(
                value,
                greenAnimator.animatedValue as Int,
                blueAnimator.animatedValue as Int
            )
            postInvalidate()
        }

        redAnimator.start()
        greenAnimator.start()
        blueAnimator.start()
    }


    private fun startWaveAnim() {
        //波纹扩散
        val animatorWave = ValueAnimator.ofFloat(0f, 300f)
        animatorWave.interpolator = DecelerateInterpolator()
        animatorWave.duration = 1000
        animatorWave.addUpdateListener { animation -> //更新旋转角度
            waveRange = animation.animatedValue as Float
            mRipplePaint.alpha = (255 - waveRange / 1.3).toInt()
            postInvalidate()
        }

        //内圆放大
        val animatorCircle = ValueAnimator.ofFloat(0f, 80f, 0f)
        animatorCircle.interpolator = DecelerateInterpolator()
        animatorCircle.duration = 600
        animatorCircle.startDelay = 1000
        animatorCircle.addUpdateListener { animation -> //更新旋转角度
            circleRange = animation.animatedValue as Float
            postInvalidate()
        }
        animatorCircle.doOnEnd {
            animatorCircle.start()
            animatorWave.start()
            isShowWave = true
        }
        animatorCircle.start()
    }


    private fun dip2px(dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }


}