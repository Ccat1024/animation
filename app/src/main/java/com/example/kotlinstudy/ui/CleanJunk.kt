package com.example.kotlinstudy.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import com.example.kotlinstudy.R
import kotlin.math.min


class CleanJunk(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    //View宽高
    private var mWidth = 0f
    private var mHeight = 0f

    //外圆画笔
    private val mCircleBorderPaint: Paint by lazy {
        Paint().apply {
            //外圆画笔
            style = Paint.Style.STROKE
            color = Color.parseColor("#eef0f5")
            strokeWidth = dip2px(13f)
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

    //文字画笔
    private val tPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            textSize = dip2px(90f)
            color = Color.parseColor("#333333")
            typeface = Typeface.createFromAsset(context.assets, "fonts/DINPro-Medium_13936.ttf")
        }
    }

    private val tPaint2: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            textSize = dip2px(16f)
            textAlign = Paint.Align.CENTER
            color = Color.parseColor("#666666")
            typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
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
    private var durationAnim = 6*1000L

    //圆半径
    private var mRadius = 0f

    //旋转角度
    private var mAngle = 90f

    //旋转角度
    private var mFanAngle = 0f

    //圆的弧度
    private var mRadian = 0f

    //绘制范围
    private var mRect = RectF()


    //圆环间距
    private var spacingArc = dip2px(20f)

    //圆环间距
    private var spacingCircle = dip2px(40f)

    //圆环扩散间距
    private var circleRange = 0f

    //圆环扩散间距
    private var waveRange = 0f

    //wave是否绘制
    private var isShowWave = false

    //图片
    private val fanBitmap = BitmapFactory.decodeResource(resources, R.mipmap.fan)


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

        //让画布旋转指定角度
        //canvas.rotate(mAngle, mCenterX, mCenterY)

        //绘制背景灰色圆
        //canvas.drawBitmap(innerCircleBitmap, null, Rect(0, 0, width, height), null)
        //canvas.drawBitmap(outerCircleBitmap,null, Rect(0, 0, width, height), null)

        //绘制外圆
        canvas.drawCircle(mCenterX, mCenterY, mRadius + circleRange -spacingCircle, mCircleBorderPaint)

        if(isShowWave){
            //绘制波纹
            canvas.drawCircle(mCenterX, mCenterY, mRadius + 60f + waveRange -spacingCircle, mRipplePaint)
        }


        //画弧线
        canvas.drawArc(mRect, mAngle, mRadian, false, mArcPaint)

        //绘制风扇
        drawFan(canvas)

        drawText(canvas)
        //canvas.drawText("94.2MB", mWidth / 2, mHeight / 2, tPaint)
    }


    private fun drawFan(canvas: Canvas?) {
        val matrix = Matrix()
        matrix.postTranslate(
            width - mWidth / 2 - fanBitmap.width / 2,
            height - mHeight / 2 - fanBitmap.height / 2
        )
        matrix.postRotate(
            mFanAngle,
            width - mWidth / 2 - fanBitmap.width / 2 + fanBitmap.width / 2,
            height - mHeight / 2 - fanBitmap.height / 2 + fanBitmap.height / 2
        )
        canvas?.drawBitmap(fanBitmap, matrix, null)
    }


    //文字
    private fun drawText(canvas: Canvas?) {
        //计算textSize的width,gb的width
        val rect = Rect()
        tPaint.getTextBounds(textSize, 0, textSize.length, rect)
        val h = rect.height()
        tPaint2.getTextBounds("GB", 0, "GB".length, rect)
        val h2 = rect.height()
        val offset = (h + h2) / 2f + 5
        //第二种测量方式
        val w11 = tPaint.measureText(textSize)

        canvas?.drawText(textSize, mWidth / 2f, mHeight / 2f, tPaint)
        canvas?.drawText(textUnit, mWidth / 2f, mHeight / 2f + offset, tPaint2)

    }


    private fun startAnimator() {
        startFanAnim()
        startArcAnim()
        startColorAnim()
        startWaveAnim()
    }

    private fun startFanAnim() {
        //风扇旋转变化
        val animatorFan = ValueAnimator.ofFloat(0f, 10 * 360f)
        animatorFan.interpolator = AccelerateDecelerateInterpolator()
        animatorFan.duration = durationAnim
        animatorFan.addUpdateListener { animation -> //更新旋转角度
            mFanAngle = animation.animatedValue as Float
            postInvalidate()
        }
        animatorFan.start()

    }

    private fun startArcAnim() {
        //弧度旋转
        val animatorRotate = ValueAnimator.ofFloat(90f, 450f)
        animatorRotate.interpolator = LinearInterpolator()
        animatorRotate.duration = 800
        animatorRotate.startDelay = 1000
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
        animatorRadian.start()
    }


    private fun startColorAnim() {
        val redAnimator = ValueAnimator.ofInt(255, 25).apply {
            duration = 2000
        }
        val greenAnimator = ValueAnimator.ofInt(89, 217).apply {
            duration = 2000
        }
        val blueAnimator = ValueAnimator.ofInt(89, 255).apply {
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