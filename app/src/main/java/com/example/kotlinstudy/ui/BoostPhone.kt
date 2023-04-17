package com.example.kotlinstudy.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
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


class BoostPhone(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

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


    private var textSize = "0.00"
    private var textUnit = "KB"

    //弧线颜色
    private var mArcColor: Int = Color.parseColor("#EF6833")

    //中心点X、Y坐标
    private var mCenterX = 0f
    private var mCenterY = 0f

    //动画时间
    private var durationAnim = 3 * 1000L

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
    private val rocketBitmap = BitmapFactory.decodeResource(resources, R.mipmap.rocket)
    private val fireBitmap = BitmapFactory.decodeResource(resources, R.mipmap.fire)
    private val lineBitmap = BitmapFactory.decodeResource(resources, R.mipmap.speed_line)
    private val jetBitmap = BitmapFactory.decodeResource(resources, R.mipmap.jet)
    private val smokeBitmap = BitmapFactory.decodeResource(resources, R.mipmap.smoke)

    //rocket Tran
    private var mRocketMove = 0f

    //rocket fire
    private var scaleFire = 0.2f

    //speed line
    private var lineY = 0f

    //jet and smoke
    private var smokeY = 0f

    //jet
    private var scaleJet = 0.2f

    //jet and smoke
    private var isShowSmoke = false

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
        canvas.drawArc(mRect, mAngle, mRadian, false, mArcPaint)


        //绘制 speed line
        val saveLayer = canvas.saveLayer(mRect, mPaint)
        canvas.drawCircle(mCenterX, mCenterY, mRect.right - mCenterX - mArcPaint.strokeWidth, mPaint)
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        drawLine(canvas)
        mPaint.xfermode = null
        canvas.restoreToCount(saveLayer)

        if(isShowSmoke){
            val saveLayer = canvas.saveLayer(mRect, mPaint)
            canvas.drawCircle(
                mCenterX,
                mCenterY,
                mRect.right - mCenterX - mArcPaint.strokeWidth,
                mPaint
            )
            mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            drawSmoke(canvas)
            mPaint.xfermode = null
            canvas.restoreToCount(saveLayer)

            drawJet(canvas)
        }

        //绘制rocket
        drawRocket(canvas)
    }


    private fun drawRocket(canvas: Canvas?) {
        //fire
        val matrix2 = Matrix()
        matrix2.postTranslate(
            width - mWidth / 2 - fireBitmap.width / 2 + 5 * sin(mRocketMove),
            height - mHeight / 2 - fireBitmap.height / 2 + 5 * cos(mRocketMove) + dip2px(8f)
        )
        matrix2.postScale(
            1f,
            scaleFire,
            width - mWidth / 2 - fireBitmap.width / 2 + 5 * sin(mRocketMove),
            height - mHeight / 2 + 5 * cos(mRocketMove) + dip2px(8f)
        )
        canvas?.drawBitmap(fireBitmap, matrix2, null)

        //rocket
        val matrix = Matrix()
        matrix.postTranslate(
            width - mWidth / 2 - rocketBitmap.width / 2 + 5 * sin(mRocketMove),
            height - mHeight / 2 - rocketBitmap.height / 2 + 5 * cos(mRocketMove) - dip2px(30f)
        )
        canvas?.drawBitmap(rocketBitmap, matrix, null)

    }

    private fun drawLine(canvas: Canvas?) {

        //speed line1
        val matrix = Matrix()
        matrix.postTranslate(
            width - mWidth / 2 - lineBitmap.width / 2 - dip2px(80f),
            mRect.top + lineY
        )
        canvas?.drawBitmap(lineBitmap, matrix, mPaint)

        //speed line2
        val matrix2 = Matrix()
        matrix2.postTranslate(
            width - mWidth / 2 - lineBitmap.width / 2 - dip2px(30f),
            mRect.top + lineY - dip2px(20f)
        )
        canvas?.drawBitmap(lineBitmap, matrix2, mPaint)

        //speed line3
        val matrix3 = Matrix()
        matrix3.postTranslate(
            width - mWidth / 2 - lineBitmap.width / 2 + dip2px(80f),
            mRect.top + lineY - dip2px(120f)
        )
        canvas?.drawBitmap(lineBitmap, matrix3, mPaint)

    }


    private fun drawJet(canvas: Canvas?) {
        //jet
        val matrix2 = Matrix()
        matrix2.postTranslate(
            width - mWidth / 2 - jetBitmap.width / 2,
            height - mHeight / 2 + dip2px(10f)
        )
        matrix2.postScale(scaleJet,
            1f,
            width - mWidth / 2 ,
            height - mHeight / 2 - jetBitmap.height / 2
        )
        canvas?.drawBitmap(jetBitmap, matrix2, mPaint)

    }

    private fun drawSmoke(canvas: Canvas?) {
        //smoke
        val matrix = Matrix()
        matrix.postTranslate(
            width - mWidth / 2 - smokeBitmap.width / 2,
            height - mHeight / 2 + dip2px(70f) + smokeY
        )
        canvas?.drawBitmap(smokeBitmap, matrix, mPaint)
    }


    private fun startAnimator() {
        startRocketAnim()
        startSpeedLineAnim()
        //startSmokeAnim()
        startArcAnim()
        startColorAnim()
        startWaveAnim()
    }

    private fun startRocketAnim() {
        //火箭圆周运动
        val animatorRocket = ValueAnimator.ofFloat(0f, -2 * 3.14f)
        animatorRocket.interpolator = LinearInterpolator()
        animatorRocket.duration = 300
        animatorRocket.repeatCount = ValueAnimator.INFINITE
        animatorRocket.repeatMode = ValueAnimator.RESTART
        animatorRocket.addUpdateListener { animation -> //更新旋转角度
            mRocketMove = animation.animatedValue as Float
            postInvalidate()
        }
        animatorRocket.start()

        //fire 拉伸
        val animatorFire = ValueAnimator.ofFloat(0.7f, 1.2f, 0.7f)
        animatorFire.interpolator = LinearInterpolator()
        animatorFire.duration = 600
        animatorFire.repeatCount = ValueAnimator.INFINITE
        animatorFire.repeatMode = ValueAnimator.RESTART
        animatorFire.addUpdateListener { animation -> //更新旋转角度
            scaleFire = animation.animatedValue as Float
            postInvalidate()
        }
        animatorFire.start()


    }

    private fun startSpeedLineAnim() {
        //line 下落 运动
        val animatorRocket = ValueAnimator.ofFloat(0f, mRect.bottom)
        animatorRocket.interpolator = LinearInterpolator()
        animatorRocket.duration = 1000
        animatorRocket.repeatCount = ValueAnimator.INFINITE
        animatorRocket.repeatMode = ValueAnimator.RESTART
        animatorRocket.addUpdateListener { animation -> //更新旋转角度
            lineY = animation.animatedValue as Float
            postInvalidate()
        }
        animatorRocket.start()
    }

    private fun startSmokeAnim() {
        //Smoke 上升 运动
        val animatorRocket = ValueAnimator.ofFloat(dip2px(40f), 0f)
        animatorRocket.interpolator = LinearInterpolator()
        animatorRocket.duration = 600
        animatorRocket.addUpdateListener { animation -> //更新旋转角度
            smokeY = animation.animatedValue as Float
            postInvalidate()
        }
        animatorRocket.start()


        //jet 拉伸
        val animatorJet = ValueAnimator.ofFloat(0.2f, 1f)
        animatorJet.interpolator = LinearInterpolator()
        animatorJet.duration = 500

        animatorJet.addUpdateListener { animation ->
            scaleJet = animation.animatedValue as Float
            postInvalidate()
        }
        animatorJet.start()
    }

    private fun startArcAnim() {
        //弧度旋转
        val animatorRotate = ValueAnimator.ofFloat(90f, 450f)
        animatorRotate.interpolator = LinearInterpolator()
        animatorRotate.duration = 800
        animatorRotate.startDelay = 500
        animatorRotate.repeatCount = ValueAnimator.INFINITE
        animatorRotate.repeatMode = ValueAnimator.RESTART
        animatorRotate.addUpdateListener { animation -> //更新旋转角度
            mAngle = animation.animatedValue as Float
            postInvalidate()
        }
        animatorRotate.start()

        //弧度增长
        val animatorRadian = ValueAnimator.ofFloat(0f, 360f)
        animatorRadian.interpolator = LinearInterpolator()
        animatorRadian.duration = durationAnim
        animatorRadian.addUpdateListener { animation -> //更新弧度
            mRadian = animation.animatedValue as Float
            postInvalidate()
        }
        animatorRadian.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                animatorRotate.cancel()
                startSmokeAnim()
                isShowSmoke = true
            }
        })
        animatorRadian.start()
    }


    private fun startColorAnim() {
        val redAnimator = ValueAnimator.ofInt(255, 25).apply {
            duration = 1000
        }
        val greenAnimator = ValueAnimator.ofInt(89, 217).apply {
            duration = 1000
        }
        val blueAnimator = ValueAnimator.ofInt(89, 255).apply {
            duration = 1000
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
            mRipplePaint.alpha = (255-waveRange/1.3).toInt()
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

    fun setTextSize(size: String) {
        textSize = size
    }

    fun setTextUnit(unit: String) {
        textUnit = unit
    }

}