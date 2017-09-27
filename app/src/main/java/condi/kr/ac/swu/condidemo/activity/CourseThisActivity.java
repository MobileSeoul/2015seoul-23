package condi.kr.ac.swu.condidemo.activity;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.GlobalApplication;
import condi.kr.ac.swu.condidemo.data.NetworkAction;
import condi.kr.ac.swu.condidemo.data.Session;


public class CourseThisActivity extends BaseActivity implements View.OnClickListener{

    private NetworkImageView[] courses = new NetworkImageView[4];
    private TextView[] names = new TextView[4];
    private TextView[] kms = new TextView[4];

    private ImageView[] regions = new ImageView[6];

    private int local;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_this);
        initActionBar("코스 정보");
        initView();
        setInfo();
    }

    private void setInfo() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String dml = "select * from course where id in (select course from member where groups="+ Session.GROUPS+")";
                return NetworkAction.sendDataToServer("course.php", dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                if(!o.equals("error")) {
                    new AsyncTask() {
                        List<Properties> list;

                        @Override
                        protected Object doInBackground(Object[] objects) {
                            try {
                                list = NetworkAction.parse("course.xml", "course");
                            } catch (XmlPullParserException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);

                            int count=0;
                            for(Properties p : list) {
                                setCourseImageURL(courses[count], p.getProperty("picture"));
                                names[count].setText(p.getProperty("name"));
                                kms[count].setText(String.format("%s KM", p.getProperty("km")));

                                setCourseEvent(p, courses[count]);

                                count++;
                            }
                        }
                    }.execute();
                } else
                    printErrorMsg("코스 정보를 로드할 수 없습니다.");
            }
        }.execute();
    }

    private void initView() {
        courses[0] = (NetworkImageView) findViewById(R.id.course1);
        courses[1] = (NetworkImageView) findViewById(R.id.course2);
        courses[2] = (NetworkImageView) findViewById(R.id.course3);
        courses[3] = (NetworkImageView) findViewById(R.id.course4);

        names[0] = (TextView) findViewById(R.id.info_name_course1);
        names[1] = (TextView) findViewById(R.id.info_name_course2);
        names[2] = (TextView) findViewById(R.id.info_name_course3);
        names[3] = (TextView) findViewById(R.id.info_name_course4);

        kms[0] = (TextView) findViewById(R.id.info_km_course1);
        kms[1] = (TextView) findViewById(R.id.info_km_course2);
        kms[2] = (TextView) findViewById(R.id.info_km_course3);
        kms[3] = (TextView) findViewById(R.id.info_km_course4);

        regions[0] = (ImageView) findViewById(R.id.course_this_other1);
        regions[1] = (ImageView) findViewById(R.id.course_this_other2);
        regions[2] = (ImageView) findViewById(R.id.course_this_other3);
        regions[3] = (ImageView) findViewById(R.id.course_this_other4);
        regions[4] = (ImageView) findViewById(R.id.course_this_other5);
        regions[5] = (ImageView) findViewById(R.id.course_this_other6);

        for(ImageView i : regions) {
            i.setOnClickListener(this);
        }
    }

    public void setCourseImageURL(final NetworkImageView imageView, final String imageURL) {
        if (imageView != null && imageURL != null) {
            Application app = GlobalApplication.getGlobalApplicationContext();
            if (app == null)
                throw new UnsupportedOperationException("needs com.kakao.GlobalApplication in order to use ImageLoader");
            imageView.setImageUrl("http://condi.swu.ac.kr:80/condi2/course/"+imageURL, ((GlobalApplication) app).getImageLoader());
        }
    }

    private void setCourseEvent(final Properties p, NetworkImageView networkImageView) {
        networkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CourseDetailActivity.class);
                intent.putExtra("id", p.getProperty("id"));
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.course_this_other1 :
                local = 0;
                break;
            case R.id.course_this_other2 :
                local = 1;
                break;
            case R.id.course_this_other3 :
                local = 2;
                break;
            case R.id.course_this_other4:
                local = 3;
                break;
            case R.id.course_this_other5 :
                local = 4;
                break;
            case R.id.course_this_other6 :
                local = 5;
                break;
        }

        redirectEtcCourseListActivity(local);
    }

    private void redirectEtcCourseListActivity(int local) {
        Intent i = new Intent(getApplicationContext(), CourseEtcActivity.class);
        i.putExtra("local", local);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
}
