package condi.kr.ac.swu.condidemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


public class CustomCircularRingView2 extends View {

    Paint mPaint;
    RectF box;
    int actualPercent = 0;

    public CustomCircularRingView2(Context context) {
        super(context);
        mPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        box = new RectF();
    }

    public CustomCircularRingView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        box = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mPaint.setDither(true);
        mPaint.setColor(Color.parseColor("#dadada"));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(15);

        int width = getWidth();
        int height = getHeight();
        int radius = 270;

        // Thin circle
        canvas.drawCircle(width / 2, height / 2, radius, mPaint);

        // Arc
        mPaint.setColor(Color.parseColor("#00c6c1"));
        mPaint.setStrokeWidth(40);

        int percent = actualPercent;

        box.set(getWidth() / 2 - radius, getHeight() / 2 - radius, getWidth() / 2 + radius, getHeight() / 2 + radius);

        float sweep = 360 * percent * 0.01f;
        canvas.drawArc(box, 270, sweep, false, mPaint);
    }

    // percentage from activity
    public void changePercentage(int value) {
        actualPercent = value;
    }
}