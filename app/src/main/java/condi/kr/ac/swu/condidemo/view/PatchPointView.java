package condi.kr.ac.swu.condidemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class PatchPointView extends View  {

    Paint mPaint;
    RectF box;
    List<Integer> percent;

    public PatchPointView(Context context) {
        super(context);
        mPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        box = new RectF();
        percent=new ArrayList<Integer>();
        percent.add(0);
    }

    public PatchPointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        box = new RectF();
        percent=new ArrayList<Integer>();
        percent.add(0);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int radius = 270;
        mPaint.setColor(Color.WHITE);

        box.set(getWidth() / 2 - radius, getHeight() / 2 - radius, getWidth() / 2 + radius, getHeight() / 2 + radius);
        int s_radius = 15;
        float sweep;
        double x,y;

        for(Integer i : percent) {
            sweep = (float) ((2.0*Math.PI) * i * 0.01f);
            x = getWidth() / 2 + radius*Math.cos(sweep+(float)(3.0/2*Math.PI));
            y = getHeight() / 2 + radius * Math.sin(sweep+(float)(3.0/2*Math.PI));
            canvas.drawCircle((int)x, (int)y, s_radius, mPaint);
        }
    }

    public void setPercentToPoint(List<Integer> percent) {
        this.percent = percent;
    }
}
