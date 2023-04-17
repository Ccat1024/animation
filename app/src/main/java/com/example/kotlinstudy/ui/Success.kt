package com.example.kotlinstudy.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import com.example.kotlinstudy.R
import kotlin.math.min

class Success(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    //画笔
    private val mHookPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#0397FE")
            strokeWidth = dip2px(2f)
            style = Paint.Style.STROKE
        }
    }
    private var centerX = 0f
    private var centerY = 0f
    private var mWidth = 0f
    private var mHeight = 0f

    //打勾的起点
    private var checkStartX = 0f

    //线1的x轴增量
    private var line1X = 0f

    //线1的y轴增量
    private var line1Y = 0f

    //线2的x轴增量
    private var line2X = 0f

    //线2的y轴增量
    private var line2Y = 0f

    //增量值
    private var step = 6

    //线的宽度
    private val lineThick = 6

    //圆弧半径
    private var radius = 0
    private var secLineInited = false


    private val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.success)

    private var offset = 0
    //旋转角度
    private var mAngle = 0f
    private var isShowHook = false

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()
        mHeight = h.toFloat()
        centerX = mWidth / 2
        centerY = mHeight / 2
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        //startAnim()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.scale(0.87f, 0.87f, centerX, centerY)

        //让画布旋转指定角度
        canvas?.rotate(mAngle, centerX, centerY)

        //绘制圆环图片
        canvas?.drawBitmap(bitmap, null, Rect(offset, offset, mWidth.toInt()-offset, mHeight.toInt()-offset), null)

        if(isShowHook){
            radius = (centerX-offset).toInt()
            checkStartX = centerX - radius/2
            canvas?.rotate(-mAngle, centerX, centerY)
            drawHook(canvas)
        }
    }

    private fun drawHook(canvas: Canvas?) {
        if (line1X < radius / 3) {
            line1X += step
            line1Y += step
        }
        //画第一根线
        canvas?.drawLine(checkStartX, centerX, checkStartX + line1X, centerX + line1Y, mHookPaint)
        if (line1X >= radius / 3) {
            if (!secLineInited) {
                line2X = line1X
                line2Y = line1Y
                secLineInited = true
            }
            line2X += step
            line2Y -= step
            //画第二根线
            canvas?.drawLine(
                checkStartX + line1X - lineThick / 2,
                centerX + line1Y, checkStartX + line2X, centerX + line2Y, mHookPaint
            )
        }
        //每隔6毫秒界面刷新
        if (line2X <= radius)
            postInvalidateDelayed(6)
    }


    private fun startAnim(){
        //旋转动画
        val animatorRotate = ValueAnimator.ofFloat(0f, 360f)
        animatorRotate.interpolator = LinearInterpolator()
        animatorRotate.duration = 600
        animatorRotate.repeatCount = ValueAnimator.INFINITE
        animatorRotate.repeatMode = ValueAnimator.RESTART
        animatorRotate.addUpdateListener { animation -> //更新旋转角度
            mAngle = animation.animatedValue as Float
            postInvalidate()
        }
        animatorRotate.start()


        //缩小动画
        val offsetAnimator = ValueAnimator.ofInt(0,300).apply {
            interpolator = LinearInterpolator()
            duration = 2000
        }
        offsetAnimator.addUpdateListener {
            offset = it.animatedValue as Int
            postInvalidate()

        }
        offsetAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                animatorRotate.cancel()
                isShowHook = true
            }
        })
        offsetAnimator.start()


    }


    fun startPlay(){
        startAnim()
    }


    //第二种画钩
/*    private fun hookAnimation() {
        mHookPath.moveTo(width / 4, height / 2)
        val mAnimatorHook = ValueAnimator.ofFloat(0f, 6f)
        mAnimatorHook.interpolator = LinearInterpolator()
        mAnimatorHook.duration = 1000
        mAnimatorHook.addUpdateListener(AnimatorUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            if (progress < 3) {
                mHookPath.rLineTo(progress, progress)
            } else {
                mHookPath.rLineTo(progress, -progress)
            }
            postInvalidate()
        })
        mAnimatorHook.start()
    }*/


    private fun dip2px(dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }

}