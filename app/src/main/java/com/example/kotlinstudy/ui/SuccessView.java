package com.example.kotlinstudy.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.example.kotlinstudy.util.DensityUtil;


/**
 * 完成动画，路径，画圈加对钩
 */
public class SuccessView extends View {

    private Paint paint;
    private Path dstPath;
    private Path circlePath;

    private int centerX = 0;
    private int centerY = 0;
    private int radius = 0;
    private PathMeasure pathMeasure;
    private float currentAnimValue;
    private boolean switchLine;

    public SuccessView(Context context) {
        super(context);
        init();
    }

    public SuccessView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SuccessView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        centerX = DensityUtil.getScreenWidth(getContext()) / 2;
        centerY = DensityUtil.getScreenHeight(getContext()) / 2;
        radius = DensityUtil.dip2px(getContext(), 60);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#0397FE"));
        paint.setStrokeWidth(DensityUtil.dip2px(getContext(), 6));
        paint.setStyle(Paint.Style.STROKE);
        dstPath = new Path();
        circlePath = new Path();
        circlePath.addCircle(centerX, centerY, radius, Path.Direction.CW);
        circlePath.moveTo(centerX - radius / 2, centerY);
        circlePath.lineTo(centerX, centerY + radius / 2);
        circlePath.lineTo(centerX + radius / 2, centerY - radius / 3);
        pathMeasure = new PathMeasure(circlePath, false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        showAnim();
    }



    public void showAnim() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 2);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAnimValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.setDuration(2000);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (currentAnimValue < 1) {
            float stop = pathMeasure.getLength() * currentAnimValue;
            pathMeasure.getSegment(0, stop, dstPath, true);
        } else if (currentAnimValue > 1 && !switchLine) {
            pathMeasure.getSegment(0, pathMeasure.getLength(), dstPath, true);
            switchLine = true;
            pathMeasure.nextContour();
        } else {
            float stop = pathMeasure.getLength() * (currentAnimValue - 1);
            pathMeasure.getSegment(0, stop, dstPath, true);
        }
        canvas.drawPath(dstPath, paint);
    }
}
