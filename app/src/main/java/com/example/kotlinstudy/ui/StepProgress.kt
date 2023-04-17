package com.example.kotlinstudy.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import kotlin.math.min

class StepProgress(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    //View宽高
    private var mWidth = 0f
    private var mHeight = 0f

    //中心点X、Y坐标
    private var mCenterX = 0f
    private var mCenterY = 0f

    //弧线画笔
    private val mArcPaint: Paint by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            color = Color.parseColor("#eef0f5")
            strokeWidth = dip2px(14f)
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
        }
    }

    //文字
    private val tPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            textSize = dip2px(14f)
            color = Color.parseColor("#333333")
        }
    }

    //外圈 4个 绘制范围
    private var mRect = RectF()

    //圆半径
    private var mRadius = 0f

    //刻度旋转角度
    private var scaleValue = 0f

    //刻度最大角度
    private var maxScaleValue = 50f


    //外圈圆弧旋转角度
    private var scanAngle = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()
        mHeight = h.toFloat() - dip2px(20f)
        //计算圆心的坐标
        mCenterX = mWidth / 2
        mCenterY = mHeight

        //取出padding值
        val paddingLeft = paddingLeft
        val paddingRight = paddingRight
        val paddingTop = paddingTop
        val paddingBottom = paddingBottom

        //绘制范围
        mRect.left = paddingLeft.toFloat() + dip2px(12f)
        mRect.top = paddingTop.toFloat() + dip2px(12f)
        mRect.right = mWidth - paddingRight - dip2px(12f)
        mRect.bottom = mHeight * 2 - paddingBottom - dip2px(12f)


        val diameter = min(mWidth, mHeight)
        mRadius = (diameter / 2 * 0.98).toFloat()
        mRadius -= dip2px(43f)

        startAnimator()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //绘制刻度弧
        drawScaleArc(canvas)

    }

    private fun drawScaleArc(canvas: Canvas?) {
        //绘制静态的弧形刻度表盘
        mArcPaint.color = Color.parseColor("#eef0f5")
        mArcPaint.strokeWidth = dip2px(14f)
        canvas?.drawArc(mRect, 180f, 180f, false, mArcPaint)
        drawCalibration(canvas, 4, true)


        //绘制动态的弧形进度
        mArcPaint.color = Color.parseColor("#ff5959")
        mArcPaint.strokeWidth = dip2px(14f)
        canvas?.drawArc(mRect, 180f, scaleValue, false, mArcPaint)
        drawCalibration(canvas, (scaleValue / (180 / 4)).toInt(), false)

    }


    /**
     * 绘制内圆弧上的大小刻度线
     * 通过旋转画布到水平 再画相对于圆心的水平线
     */
    private fun drawCalibration(canvas: Canvas?, max: Int, isText: Boolean) {
        val starLineX = mWidth - dip2px(12f) - mArcPaint.strokeWidth
        val endLineX = mWidth - dip2px(12f) - mArcPaint.strokeWidth - dip2px(10f)
        for (i in 0..max) {
            canvas?.save()
            canvas?.rotate((180f + 180 / 4 * i), mCenterX, mCenterY)
            mArcPaint.strokeWidth = dip2px(2f)
            //绘制大刻度
            canvas?.drawLine(starLineX, mCenterY, endLineX, mCenterY, mArcPaint)
            if (isText) {
                val text = "${i * 1500}"
                val rect = Rect()
                tPaint.getTextBounds(text, 0, text.length, rect)
                val w = rect.width()
                val h = rect.height()
                if ( i == 0) {
                    canvas?.drawText(text, endLineX - w , mCenterY + h / 2, tPaint)
                }
                if (i == max ) {
                    canvas?.drawText(text, endLineX - w +dip2px(10f), mCenterY + h / 2, tPaint)
                }
                if (i == 2)  {
                    canvas?.rotate(90f, endLineX, mCenterY)
                    canvas?.drawText(text, endLineX, mCenterY + h + dip2px(5f), tPaint)
                    canvas?.rotate(-90f, endLineX, mCenterY)
                }
            }


            canvas?.restore()
        }

    }


    /**
     * 动画部分
     */

    private fun startAnimator() {
        startScaleAnim()
    }

    //刻度增长动画
    private fun startScaleAnim() {
        val animatorScale = ValueAnimator.ofFloat(0f, maxScaleValue)
        animatorScale.duration = 3000
        animatorScale.interpolator = AccelerateDecelerateInterpolator()
        animatorScale.addUpdateListener { animation -> //更新旋转角度
            scaleValue = animation.animatedValue as Float
            postInvalidate()
        }
        animatorScale.start()

    }


    /**
     *  开放的api
     */
    fun setScaleValue(present: Float) {
        maxScaleValue = present * 300F
    }


    private fun dip2px(dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }

}