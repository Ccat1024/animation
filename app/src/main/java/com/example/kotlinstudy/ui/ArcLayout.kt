package com.example.kotlinstudy.ui


import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt


class ArcLayout : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    /**
     * 矩形高度占的比例
     */
    private var topRectWeight = 5
    /**
     * 圆弧高度占的比例
     */
    private var bottomArcWeight = 1
    /**
     * 圆弧高度占的比例
     */
    private var topRectF = RectF()
    private val mPath: Path = Path()
    private var mColor = Color.rgb(0,0,255)
    private val mPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = mColor
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        startAnim()
    }


    override fun onDraw(canvas: Canvas) {
        // 获取 view 宽度
        val width = width.toFloat()
        // 获取 view 高度
        val height = height.toFloat()

        // 全部高度 所占的比例 顶部矩形+底部圆弧
        val allWeight = (topRectWeight + bottomArcWeight).toFloat()

        // 矩形 的 上下左右
        topRectF.left = 0f
        topRectF.top = 0f
        topRectF.right = width
        topRectF.bottom = height / allWeight * topRectWeight

        // 移动 到起点  根据比例计算 相当于 矩形底部 ，为了合理优化计算误差 贴合紧密 特意 -1 也就是向上移动一点点
        mPath.moveTo(0f, height / allWeight * topRectWeight - 1)
        mPath.quadTo(width / 2, height, width, height / allWeight * topRectWeight - 1)

        // 画顶部矩形
        canvas.drawRect(topRectF, mPaint)
        // 画弧形
        //canvas.drawPath(mPath, mPaint)
    }
    //rgb(31,203,251) -> rgb(255,127,102)
    private fun startAnim(){
        val redAnimator = ValueAnimator.ofInt(31,255).apply {
            duration = 1000
        }
        val greenAnimator = ValueAnimator.ofInt(203,127).apply {
            duration = 1000
        }
        val blueAnimator = ValueAnimator.ofInt(251,102).apply {
            duration = 1000
        }
        redAnimator.addUpdateListener {
            var value = it.animatedValue as Int
            mPaint.color = Color.rgb(value,greenAnimator.animatedValue as Int,blueAnimator.animatedValue as Int)
            postInvalidate()
        }

        redAnimator.start()
        greenAnimator.start()
        blueAnimator.start()
    }

    /**
     * 代码设置 相关 UI
     *
     * @param topRectFWeight  顶部 矩形高度 比例
     * @param bottomArcWeight 底部 弧形高度 比例
     * @param color           整体颜色
     */
    fun resetArcView(topRectFWeight: Int, bottomArcWeight: Int, @ColorInt color: Int) {
        topRectWeight = topRectFWeight
        this.bottomArcWeight = bottomArcWeight
        mColor = color
        if (mPaint != null) {
            mPaint.color = color
            invalidate()
        }
    }

    /**
     * 可设置背景颜色 跟随一定的规则 变化 重绘
     *
     * @param color 颜色
     */
    fun setPaintColor(@ColorInt color: Int) {
        if (mPaint != null) {
            mPaint.color = color
            invalidate()
        }
    }





}