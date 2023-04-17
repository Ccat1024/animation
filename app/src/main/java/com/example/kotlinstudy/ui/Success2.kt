package com.example.kotlinstudy.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator

class Success2(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    //画笔
    private val mHookPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#FFFFFF")
            strokeWidth = dip2px(5f)
            style = Paint.Style.STROKE
            //线帽
            strokeCap = Paint.Cap.ROUND
            //连接处
            strokeJoin = Paint.Join.ROUND
        }
    }
    private var centerX = 0f
    private var centerY = 0f
    private var mWidth = 0f
    private var mHeight = 0f

    //circle画笔
    private val mCirclePaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#22dbfe")
            style = Paint.Style.FILL
        }
    }

    //圆弧半径
    private var radius = 0

    private var mHookPath = Path()

    private var offset = 0
    private var offsetWhiteC = dip2px(4f)
    private var maxOffset = 332 //332
    //旋转角度

    private var isShowHook = false

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()
        maxOffset = (mWidth/2-dip2px(45f)).toInt()
        Log.d("maxOffset", "mWidth: $mWidth")
        Log.d("maxOffset", "maxOffset: $maxOffset")
        mHeight = h.toFloat()
        centerX = mWidth / 2
        centerY = mHeight / 2
        setLayerType(LAYER_TYPE_SOFTWARE, null)

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.scale(0.87f, 0.87f, centerX, centerY)

        //绘制圆
        mCirclePaint.color = Color.parseColor("#22dbfe")
        canvas?.drawCircle(centerX,centerY,mWidth/2-offset,mCirclePaint)

        //绘制圆
        mCirclePaint.color = Color.parseColor("#FFFFFF")
        canvas?.drawCircle(centerX,centerY,mWidth/2-offsetWhiteC,mCirclePaint)

        if(isShowHook){
            radius = (centerX-offset).toInt()
            getHookPath()
            canvas?.drawPath(mHookPath,mHookPaint)
        }

    }

    private fun getHookPath(){
        mHookPath.moveTo(centerX - radius/4-radius/8, height / 2f)
        mHookPath.lineTo(centerX-radius/8,centerY + radius/4)
        mHookPath.lineTo(centerX + radius/2-radius/8,centerY - radius/3)
    }

    private fun startAnim(){
        Log.d("maxOffset", "startAnim: $maxOffset")
        //缩小动画
        val offsetAnimator = ValueAnimator.ofInt(0,maxOffset).apply {
            interpolator = LinearInterpolator()
            duration = 300
        }
        offsetAnimator.addUpdateListener {
            offset = it.animatedValue as Int
            postInvalidate()

        }
        offsetAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                isShowHook = true
            }
        })
        offsetAnimator.start()


        val offsetAnimator2 = ValueAnimator.ofFloat(dip2px(4f), dip2px(200f)).apply {
            interpolator = LinearInterpolator()
            duration = 200
        }
        offsetAnimator2.addUpdateListener {
            offsetWhiteC = it.animatedValue as Float
            postInvalidate()

        }
        offsetAnimator2.start()

    }


    fun startPlay(){
        startAnim()
    }


    private fun dip2px(dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }


}