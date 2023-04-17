package com.example.kotlinstudy.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import com.example.kotlinstudy.R
import kotlin.math.min

class ArcScale(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

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
            strokeWidth = dip2px(4f)
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
        }
    }

    private val mCirclePaint: Paint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            color = Color.parseColor("#eef0f5")
            isAntiAlias = true
             alpha = 100
        }
    }

    private val mScanPaint: Paint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            color = Color.parseColor("#eef0f5")
            isAntiAlias = true
        }
    }

    //外圈 4个 绘制范围
    private var mRect = RectF()

    //圆弧  绘制范围
    private var mScaleRect = RectF()

    //圆半径
    private var mRadius = 0f

    //外圈圆弧旋转角度
    private var outArcAngle = 0f

    //刻度旋转角度
    private var scaleValue = 0f

    //刻度最大角度
    private var maxScaleValue = 0f

    private var scanBitmap = BitmapFactory.decodeResource(resources,R.mipmap.scan)

    //外圈圆弧旋转角度
    private var scanAngle = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()
        mHeight = h.toFloat()
        //计算圆心的坐标
        mCenterX = mWidth / 2
        mCenterY = mHeight / 2

        //取出padding值
        val paddingLeft = paddingLeft
        val paddingRight = paddingRight
        val paddingTop = paddingTop
        val paddingBottom = paddingBottom

        //绘制范围
        mRect.left = paddingLeft.toFloat() + dip2px(2f)
        mRect.top = paddingTop.toFloat() + dip2px(2f)
        mRect.right = mWidth - paddingRight - dip2px(2f)
        mRect.bottom = mHeight - paddingBottom - dip2px(2f)

        mScaleRect.left = mRect.left + dip2px(12f)
        mScaleRect.top = mRect.top + dip2px(12f)
        mScaleRect.right = mRect.right - dip2px(12f)
        mScaleRect.bottom = mRect.bottom - dip2px(12f)

        val diameter = min(mWidth, mHeight)
        mRadius = (diameter / 2 * 0.98).toFloat()
        mRadius-=dip2px(43f)
        startAnimator()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //绘制外圈的旋转圆弧
        drawRotateArc(canvas)

        //绘制刻度弧
        drawScaleArc(canvas)

        Log.d("mRadius", "onDraw: $mRadius")
        mArcPaint.color = Color.parseColor("#eef0f5")
        mArcPaint.strokeWidth = dip2px(2f)
        canvas?.drawCircle(mCenterX,mCenterY,mRadius,mArcPaint)
        canvas?.drawLine(mCenterX-mRadius-dip2px(5f),mCenterY,mCenterX+mRadius+dip2px(5f),mCenterY,mArcPaint)
        canvas?.drawLine(mCenterX,mCenterY-mRadius-dip2px(5f),mCenterX,mCenterY+mRadius+dip2px(5f),mArcPaint)
        canvas?.drawCircle(mCenterX,mCenterY,dip2px(40f),mCirclePaint)

        val matrix = Matrix()
        matrix.postTranslate(
            width - mWidth / 2 - scanBitmap.width / 2,
            height - mHeight / 2 - scanBitmap.height / 2
        )
        matrix.postRotate(
            scanAngle,
            width - mWidth / 2 - scanBitmap.width / 2 + scanBitmap.width / 2,
            height - mHeight / 2 - scanBitmap.height / 2 + scanBitmap.height / 2
        )
        canvas?.drawBitmap(scanBitmap, matrix, null)
    }





    private fun drawRotateArc(canvas: Canvas?) {
        mArcPaint.color = Color.parseColor("#eef0f5")
        mArcPaint.strokeWidth = dip2px(2f)
        canvas?.drawArc(mRect, 90f - 20f + outArcAngle, 40f, false, mArcPaint)
        canvas?.drawArc(mRect, 180f - 20f + outArcAngle, 40f, false, mArcPaint)
        canvas?.drawArc(mRect, 270f - 20f + outArcAngle, 40f, false, mArcPaint)
        canvas?.drawArc(mRect, -20f + outArcAngle, 40f, false, mArcPaint)
    }


    private fun drawScaleArc(canvas: Canvas?){
        //绘制静态的弧形刻度表盘
        mArcPaint.color = Color.parseColor("#eef0f5")
        mArcPaint.strokeWidth = dip2px(4f)
        canvas?.drawArc(mScaleRect, 120f, 300f, false, mArcPaint)
        drawCalibration(canvas,20)


        //绘制动态的弧形进度
        mArcPaint.color = Color.parseColor("#ff5959")
        mArcPaint.strokeWidth = dip2px(4f)
        canvas?.drawArc(mScaleRect, 120f, scaleValue, false, mArcPaint)
        drawCalibration(canvas, (scaleValue/15).toInt())
    }


    private fun drawScan(canvas: Canvas?){

    }




    /**
     * 绘制内圆弧上的大小刻度线
     * 通过旋转画布到水平 再画相对于圆心的水平线
     */
    private fun drawCalibration(canvas: Canvas?,max:Int) {
        val starLineX = mWidth-dip2px(12f)-mArcPaint.strokeWidth
        val  endLineX = mWidth-dip2px(12f)-mArcPaint.strokeWidth-dip2px(10f)
        for (i in 0..max) {
            canvas?.save()
            canvas?.rotate((120f + 15 * i), mCenterX, mCenterY)
            if (i % 2 == 0) {
                mArcPaint.strokeWidth = dip2px(2f)
                //绘制大刻度
                canvas?.drawLine(starLineX, mCenterY, endLineX, mCenterY, mArcPaint)
            } else {
                //小刻度
                mArcPaint.strokeWidth = dip2px(1f)
                canvas?.drawLine(starLineX, mCenterY, endLineX+dip2px(3f), mCenterY, mArcPaint)
            }
            canvas?.restore()
        }
    }




    /**
     * 动画部分
     */

    private fun startAnimator() {

        startOutArcAnim()
        startScaleAnim()
        startScanAnim()
    }


    //外圈 圆弧 旋转变化
    private fun startOutArcAnim() {
        val animatorOut = ValueAnimator.ofFloat(0f,-360f)
        animatorOut.duration = 2000
        animatorOut.interpolator = LinearInterpolator()
        animatorOut.repeatCount = ValueAnimator.INFINITE
        animatorOut.repeatMode = ValueAnimator.RESTART
        animatorOut.addUpdateListener { animation -> //更新旋转角度
            outArcAngle = animation.animatedValue as Float
            postInvalidate()
        }
        animatorOut.start()

    }

    //刻度增长动画
    private fun startScaleAnim() {
        val animatorScale = ValueAnimator.ofFloat(0f,maxScaleValue)
        animatorScale.duration = 3000
        animatorScale.interpolator = AccelerateDecelerateInterpolator()
        animatorScale.addUpdateListener { animation -> //更新旋转角度
            scaleValue = animation.animatedValue as Float
            postInvalidate()
        }
        animatorScale.start()

    }

    //scan 旋转变化
    private fun startScanAnim() {
        val animatorOut = ValueAnimator.ofFloat(0f,360f)
        animatorOut.duration = 500
        animatorOut.interpolator = LinearInterpolator()
        animatorOut.repeatCount = ValueAnimator.INFINITE
        animatorOut.repeatMode = ValueAnimator.RESTART
        animatorOut.addUpdateListener { animation -> //更新旋转角度
            scanAngle = animation.animatedValue as Float
            postInvalidate()
        }
        animatorOut.start()

    }




    /**
     *  开放的api
     */

    fun setScaleValue(present: Float){
        maxScaleValue = present*300F
        //startScaleAnim()
    }


    private fun dip2px(dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f)
    }

}