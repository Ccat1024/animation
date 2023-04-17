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
import com.example.kotlinstudy.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class BatterySaver(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

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
            color = mArcColor
            strokeWidth = dip2px(4f)
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
        }
    }

    //bitmap画笔
    private val mPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            //color = Color.parseColor("#FFCC44")
        }
    }




    //弧线颜色
    private var mArcColor: Int = Color.parseColor("#EF6833")

    //中心点X、Y坐标
    private var mCenterX = 0f
    private var mCenterY = 0f

    //动画时间
    private var durationAnim = 4 * 1000L

    //圆半径
    private var mRadius = 0f

    //旋转角度
    private var mAngle = 90f


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

    //rocket图片
    private val battery = BitmapFactory.decodeResource(resources, R.mipmap.battery)
    private val light = BitmapFactory.decodeResource(resources, R.mipmap.lightning)
    private val comPower = BitmapFactory.decodeResource(resources, R.mipmap.gezi_com)
    private val unComPower = BitmapFactory.decodeResource(resources, R.mipmap.gezi_uncom)


    //Battery Inc
    private var mBatteryInc = 0


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
        drawMArc(canvas, mRadian)

        //绘制 battery
        drawBattery(canvas)
    }


    private fun drawBattery(canvas: Canvas?) {

        //battery
        val matrix = Matrix()
        matrix.postTranslate(
            width - mWidth / 2 - battery.width / 2,
            height - mHeight / 2 - battery.height / 2
        )
        canvas?.drawBitmap(battery, matrix, null)

        //unComplete  power
        drawPower(canvas, dip2px(40f))
        drawPower(canvas, dip2px(20f))
        drawPower(canvas, 0f)
        drawPower(canvas, dip2px(-20f))
        drawPower(canvas, dip2px(-40f))

        //complete  power
        if (mBatteryInc == 5) drawPower(canvas, dip2px(-40f))
        if (mBatteryInc >= 4) drawPower(canvas, dip2px(-20f))
        if (mBatteryInc >= 3) drawPower(canvas, dip2px(0f))
        if (mBatteryInc >= 2) drawPower(canvas, dip2px(20f))
        if (mBatteryInc >= 1) drawPower(canvas, dip2px(40f))


        if (mBatteryInc == 1) {
            drawPower(canvas, dip2px(40f))
        }

    }

    private fun drawPower(canvas: Canvas?, indexY: Float) {
        //power
        val matrix2 = Matrix()
        matrix2.postTranslate(
            width - mWidth / 2 - unComPower.width / 2,
            height - mHeight / 2 - unComPower.height / 2 + indexY
        )
        canvas?.drawBitmap(unComPower, matrix2, null)

        //battery
        val matrix = Matrix()
        matrix.postTranslate(
            width - mWidth / 2 - battery.width / 2,
            height - mHeight / 2 - battery.height / 2
        )
        canvas?.drawBitmap(battery, matrix, null)

    }

    private fun drawMArc(canvas: Canvas?, percent: Float) {
        val fl = percent * 360
        canvas?.drawArc(mRect, mAngle - fl / 2, fl, false, mArcPaint)
    }


    private fun startAnimator() {
        startBatteryAnim()
        startArcAnim()
        //startColorAnim()
        startWaveAnim()

    }

    private fun startBatteryAnim() {
        //电量增长动画
        val animatorPower = ValueAnimator.ofInt(0, 6)
        animatorPower.interpolator = LinearInterpolator()
        animatorPower.duration = 1000
        animatorPower.repeatMode = ValueAnimator.RESTART
        animatorPower.addUpdateListener { animation -> //更新旋转角度
            mBatteryInc = animation.animatedValue as Int
            postInvalidate()
        }
        animatorPower.doOnEnd {
            mBatteryInc = 0
            animatorPower.startDelay = 1000
            animatorPower.start()
        }
        animatorPower.start()
    }


    private fun startArcAnim() {

        val animatorRadian3 = ValueAnimator.ofFloat(0.8f, 1f)
        animatorRadian3.interpolator = LinearInterpolator()
        animatorRadian3.duration = 1000
        animatorRadian3.startDelay = 1000
        animatorRadian3.addUpdateListener { animation -> //更新弧度
            mRadian = animation.animatedValue as Float
            postInvalidate()
        }

        val animatorRadian2 = ValueAnimator.ofFloat(0.4f, 0.8f)
        animatorRadian2.interpolator = LinearInterpolator()
        animatorRadian2.duration = 1000
        animatorRadian2.startDelay = 1000
        animatorRadian2.addUpdateListener { animation -> //更新弧度
            mRadian = animation.animatedValue as Float
            postInvalidate()
        }
        animatorRadian2.doOnEnd {
            animatorRadian3.start()
        }

        //弧度增长
        val animatorRadian = ValueAnimator.ofFloat(0f, 0.4f)
        animatorRadian.interpolator = LinearInterpolator()
        animatorRadian.duration = 1000
        animatorRadian.addUpdateListener { animation -> //更新弧度
            mRadian = animation.animatedValue as Float
            postInvalidate()
        }
        animatorRadian.doOnEnd {
            animatorRadian2.start()
            startColorAnim()
        }
        animatorRadian.start()

    }


    private fun startColorAnim() {
        val redAnimator = ValueAnimator.ofInt(255, 25).apply {
            duration = 1000
            startDelay = 1000
        }
        val greenAnimator = ValueAnimator.ofInt(89, 217).apply {
            duration = 1000
            startDelay = 1000
        }
        val blueAnimator = ValueAnimator.ofInt(89, 255).apply {
            duration = 1000
            startDelay = 1000
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


    private fun setRadianColor(color: Int) {
        mArcColor = color
    }



}