package com.example.kotlinstudy.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.fonts.FontFamily
import android.util.AttributeSet
import android.util.Property
import android.view.View
import android.view.animation.LinearInterpolator
import com.example.kotlinstudy.R
import kotlin.math.min


/**
 * 水波纹控件
 */
class WaterView : View {

    private var mWidth = 0

    private var mHeight = 0

    private var mColor = Color.parseColor("#19d9ff")
    private var circleColor = Color.parseColor("#3319d9ff")

    //水波纹画笔
    private val mPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            strokeWidth = dp2px(2.5f)
            style = Paint.Style.STROKE
            color = mColor
            maskFilter = BlurMaskFilter(20f,BlurMaskFilter.Blur.SOLID)
        }
    }

    //圆弧画笔
    private val arcPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            strokeWidth = dp2px(3.5f)
            style = Paint.Style.STROKE
        }
    }

    //圆画笔
    private val circlePaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }

    //圆弧小球画笔
    private val ballPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }

    private var isShowBall = true

    private  var radius = 0f

    //水波纹的大小
    private lateinit var mRect: RectF

    //圆圈数量
    private var circleCount = 2

    //存储圆半径的大小
    private var circleRadius: FloatArray = FloatArray(circleCount)

    private var circleSpacing = dp2px(100f)

    //图片
    private val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.clean)

    //文字
    private val tPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            textSize = dp2px(52f)
            color = Color.parseColor("#333333")
            typeface = Typeface.createFromAsset(context.assets, "fonts/DINPro-Medium_13936.ttf")
        }
    }

    private val tPaint2: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            textSize = dp2px(12f)
            textAlign = Paint.Align.CENTER
            color = Color.parseColor("#666666")
            typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
        }
    }

    private var mSweepGradient: SweepGradient? = null

    private var textSize = "1.03"
    private var textUnit = "KB"
    private var textTitle = "Junk  Files"



    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        mRect = RectF(0f, 0f, min(mWidth, mHeight) * 1.2f, min(mWidth, mHeight) * 1.2f)
        radius = mWidth/2f - circleSpacing
        startAnimation()
        updateArcPaint()
    }

    private fun updateArcPaint() {
        // 设置渐变
        val mGradientColors = intArrayOf(
            0,
            mColor,
        )
        val positions = floatArrayOf(90f / 360f, 270f / 360f)
        mSweepGradient = SweepGradient(mWidth / 2f, mHeight / 2f, mGradientColors, positions)
        arcPaint.shader = mSweepGradient

        //圆环向中心变淡
        val mRadialColors = intArrayOf(
            0,
            circleColor
        )
        val positions2 = floatArrayOf(0.8f, 1f)
        val mRadialGradient = RadialGradient(
            mWidth / 2f, mHeight / 2f, radius,
            mRadialColors, positions2, Shader.TileMode.REPEAT
        )
        circlePaint.shader = mRadialGradient
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //绘制中间控件
        //drawBitmap(canvas)

        //绘制圆环
        canvas?.drawCircle(width / 2f, height / 2f, radius, circlePaint)


        if(isShowBall){
            //绘制尾拖
            drawBallTail(canvas)
            //绘制圆弧前的小球
            drawBall(canvas)
        }

        //文字
        drawText(canvas)

        //水波纹
        val avgAlpha = 255 / circleSpacing//(mRect.height() / 2f)
        repeat(circleCount) {
            if (circleRadius[it] > 0) {
                mPaint.alpha = 205 - (avgAlpha * circleRadius[it]).toInt()
                canvas?.drawCircle(mWidth / 2f, mHeight / 2f, radius+circleRadius[it], mPaint)
            }
        }


    }

    private fun drawBallTail(canvas: Canvas?){
        //旋转渐变 90
        val matrix = Matrix()
        matrix.setRotate(90f + mCurrentSingle, width / 2f, height / 2f)
        mSweepGradient!!.setLocalMatrix(matrix)
        //绘制渐变圆弧
        val circleRectF = RectF(
            width / 2f - radius,
            height / 2f - radius,
            width / 2f + radius,
            height / 2f + radius
        )
        canvas?.drawArc(circleRectF, 90f + mCurrentSingle, -270f, false, arcPaint)

    }

    //绘制圆弧前的小球
    private fun drawBall(canvas: Canvas?){
        canvas?.save()
        canvas?.rotate(mCurrentSingle,width / 2f, height / 2f)
        //绘制圆弧前面的小球
        ballPaint.color = mColor
        canvas?.drawCircle(width / 2f, height / 2f + radius, 35f, ballPaint)
        ballPaint.color = Color.WHITE
        canvas?.drawCircle(width / 2f, height / 2f + radius, 25f, ballPaint)
        canvas?.restore()
    }


    //绘制bitmap背景
    private fun drawBitmap(canvas: Canvas?) {
        //外圈旋转
        val matrix = Matrix()
        matrix.postTranslate(
            (width - mWidth / 2 - bitmap.width / 2).toFloat(),
            (height - mHeight / 2 - bitmap.height / 2).toFloat()
        )
        matrix.postRotate(
            mCurrentSingle,
            (width - mWidth / 2 - bitmap.width / 2 + bitmap.width / 2).toFloat(),
            (height - mHeight / 2 - bitmap.height / 2 + bitmap.height / 2).toFloat()
        )
        canvas?.drawBitmap(bitmap, matrix, null)
        //canvas?.drawBitmap(bitmap, null, Rect(0, 0, mWidth, mHeight),null)
    }

    //文字
    private fun drawText(canvas: Canvas?) {

        //计算textSize的width,gb的width
        val rect = Rect()
        tPaint.getTextBounds(textSize, 0, textSize.length, rect)
        val w = rect.width()
        tPaint2.getTextBounds("GB", 0, "GB".length, rect)
        val w2 = rect.width()
        val offset = (w + w2) / 2f + 5
        //第二种测量方式
        val w11 = tPaint.measureText(textSize)

        canvas?.drawText(textSize, mWidth / 2f - (offset - w / 2f), mHeight / 2f, tPaint)
        canvas?.drawText(textUnit, mWidth / 2f + (offset - w2 / 2f), mHeight / 2f, tPaint2)

        canvas?.drawText(textTitle, mWidth / 2f, mHeight / 2f + dp2px(25f), tPaint2)
    }


    //rgb(25,217,255) -> rgb(255,89,89)
    private fun startColorAnim(){
        val redAnimator = ValueAnimator.ofInt(25,255).apply {
            duration = 4000
        }
        val greenAnimator = ValueAnimator.ofInt(217,89).apply {
            duration = 4000
        }
        val blueAnimator = ValueAnimator.ofInt(255,89).apply {
            duration = 4000
        }
        redAnimator.addUpdateListener {
            var value = it.animatedValue as Int
            //波纹
            mPaint.color = Color.rgb(value,greenAnimator.animatedValue as Int,blueAnimator.animatedValue as Int)
            //圆弧
            mColor = Color.rgb(value,greenAnimator.animatedValue as Int,blueAnimator.animatedValue as Int)
            //圆环
            circleColor = Color.argb(51,value,greenAnimator.animatedValue as Int,blueAnimator.animatedValue as Int)
            if(radius != 0f){
                updateArcPaint()
            }
            postInvalidate()
        }

        redAnimator.start()
        greenAnimator.start()
        blueAnimator.start()
    }

    private var va: ValueAnimator? = null
    private var mCurrentSingle = 0f

    //圆弧旋转
    private fun startSplashCircle() {
        //设置属性动画，让圆转起来
        va = ObjectAnimator.ofFloat(0f, 360f)
        va?.duration = 1000
        va?.repeatCount = ValueAnimator.INFINITE
        va?.repeatMode = ValueAnimator.RESTART
        va?.addUpdateListener { animation ->
            mCurrentSingle = animation.animatedValue as Float
            invalidate()
        }
        va?.interpolator = LinearInterpolator()
        va?.start()
    }

    fun startRotate() {
        startSplashCircle()
        //startColorAnim()
    }

    fun startChangeColor() {
        startColorAnim()
    }

    fun stopRotate() {
        va?.cancel()
        isShowBall = false
    }

    fun setTextSize(size: String) {
        textSize = size
    }

    fun setTextTitle(title: String) {
        textTitle = title
    }


    private fun dp2px(dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }

    /**
     * 当前圆的半径
     */
    private var currentCircleRadius = 0f

    private var animation: ObjectAnimator? = null

    /**
     * 开始动画，水波纹
     */
    @SuppressLint("WrongConstant")
    fun startAnimation() {
        if (animation?.isRunning == true) {
            return
        }

        animation = ObjectAnimator.ofFloat(this, property, 0f,circleSpacing ).apply {//mRect.height() / 2f
            addUpdateListener {
                val avgSize = circleSpacing  / circleCount //平均大小
                repeat(circleCount) {
                    val radius = currentCircleRadius - (it * avgSize)
                    if (radius > 0) {
                        circleRadius[it] = radius
                    } else if (circleRadius[it] > 0) {
                        circleRadius[it] = circleSpacing + radius  //后面圈的继续扩散
                    }

                    if (circleRadius[it] > mRect.height()) {
                        circleRadius[it] = 0f
                    }
                }
                invalidate()
            }
            repeatCount = ValueAnimator.INFINITE;//无限循环
            repeatMode = ValueAnimator.INFINITE;//
        }
        animation?.duration = 3000
        animation?.interpolator = LinearInterpolator()
        animation?.start()

    }




    /**
     * 停止动画
     */
    fun stopAnimation() {
        animation?.removeAllListeners()
        animation?.cancel()
        circleCount = 0

    }

    /**
     * 设置波浪颜色
     */
    fun setColor(color: String) {
        //波纹
        mPaint.color = Color.parseColor("#$color")
        //圆弧
        mColor = Color.parseColor("#$color")
        //圆环
        circleColor = Color.parseColor("#33$color")

        updateArcPaint()
    }

    /**
     * 自定义属性动画值
     */
    private var property =
        object : Property<WaterView, Float>(Float::class.java, "currentCircleRadius") {

            override fun get(waterView: WaterView): Float {
                return waterView.currentCircleRadius
            }

            override fun set(waterView: WaterView, value: Float) {
                waterView.currentCircleRadius = value
            }
        }
}
