package condi.kr.ac.swu.condidemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Properties;

public class CurvTextView extends View {

    private String[] courseNames;
    private ArrayList<String> selected;
    private RectF rectF;
    private Paint paint;
    private Path path;

    private String color = "#000000";
    private int position=-1;

    public CurvTextView(Context context) {
        super(context);
        courseNames = new String[]{" ", " ", " ", " ", " ", " "};
        selected = new ArrayList<String>();
        paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        rectF = new RectF();
        path = new Path();
    }

    public CurvTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        courseNames = new String[]{" ", " ", " ", " ", " ", " "};
        selected = new ArrayList<String>();
        paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        rectF = new RectF();
        path = new Path();

    }

    @Override
    protected void onDraw(Canvas canvas) {

        paint.setDither(true);

        path.moveTo(0, 0);
        rectF.set(0, 0, getWidth(), getHeight());

        float start = 270;
        float sweep = 60;

        for(int i=0; i<courseNames.length; i++) {

            if(i==position)
                color = "#ffffff";
            else
                color ="#000000";


            // 다른 멤버들이 선택한 것들
            for(int j=0; j<selected.size(); j++) {
                if(courseNames[i].equals(selected.get(j))) color = "#ffffff";
            }

            paint.setColor(Color.parseColor(color));

            paint.setTextSize(70);

            path.addArc(rectF, start, sweep);
            canvas.drawTextOnPath(courseNames[i], path, 30, 55 ,paint);
            path.reset();

            start += sweep;
            if(start>360)
                start = start - 360;

        }
    }

    public void clickedCourse(int position) {
        this.position = position;
    }

    public void courseName(String[] courseNames) {
        this.courseNames = courseNames;
    }

    public void selectedCourse(ArrayList<String> selected) {
        this.selected = selected;
    }
}
