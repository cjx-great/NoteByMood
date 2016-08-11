package com.cjx.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.cjx.R;


/**
 * Created by CJX on 2016/8/6.
 */
public class ProgressView extends FrameLayout {
    private Bitmap mScaleBitmap = null;
    private Bitmap mBitmap = null;
    private Paint mPaint = new Paint();

    private int mRepeatCount;
    private int mSpeed = 15;
    private float mLeft;
    private float mPercent;       //记录圈数

    private static final int mTextColor = 0xFF000000 ;
    private static final int mTextSize = 100;


    public ProgressView(Context context,AttributeSet attrs) {
        super(context,attrs);
    }

    /**
     * 设置进度
     * */
    public void setPercent(float percent) {
        mPercent = percent;
        postInvalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        //裁剪为圆形
        Path path = new Path();
        canvas.save();
        path.reset();
        canvas.clipPath(path);
        path.addCircle(width / 2, height / 2, width / 2, Path.Direction.CCW);
        canvas.clipPath(path, Region.Op.REPLACE);

        if(mScaleBitmap == null){
            mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.green);
            mScaleBitmap = Bitmap.createScaledBitmap(mBitmap, mBitmap.getWidth(), getHeight(), false);
            mBitmap.recycle();
            mBitmap = null;
            mRepeatCount = (int) Math.ceil(getWidth() / mScaleBitmap.getWidth() + 0.5) + 1;
        }
        for (int i = 0; i < mRepeatCount; i++) {
            canvas.drawBitmap(mScaleBitmap, mLeft + (i - 1) * mScaleBitmap.getWidth(), (1 - mPercent) * getHeight(), null);
        }

        String text = (int) (mPercent * 100) + "%";
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(text, (getWidth() - mPaint.measureText(text)) / 2, getHeight() / 2 + mTextSize / 2, mPaint);
        mLeft += mSpeed;
        if (mLeft >= mScaleBitmap.getWidth()) {
            mLeft = 0;
        }

        //绘制外圆环
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.rgb(33, 211, 39));
        canvas.drawCircle(width / 2, height / 2, width / 2 - 2, mPaint);
        postInvalidateDelayed(20);
        canvas.restore();

    }
}
