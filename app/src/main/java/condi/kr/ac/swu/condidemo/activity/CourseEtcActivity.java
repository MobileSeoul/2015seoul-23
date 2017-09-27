package condi.kr.ac.swu.condidemo.activity;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.GlobalApplication;
import condi.kr.ac.swu.condidemo.data.NetworkAction;

public class CourseEtcActivity extends BaseActivity {

    private int local;

    private TextView etc_local;
    private NetworkImageView[] etc_course = new NetworkImageView[6];
    private TextView[] info_name_etc_course = new TextView[6];
    private TextView[] info_km_etc_course = new TextView[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_etc);
        initActionBar("코스 정보");
        initView();
    }

    private void initView() {
        local = getIntent().getIntExtra("local", -1);
        etc_local = (TextView) findViewById(R.id.etc_local);

        etc_course[0] = (NetworkImageView) findViewById(R.id.etc_course1);
        etc_course[1] = (NetworkImageView) findViewById(R.id.etc_course2);
        etc_course[2] = (NetworkImageView) findViewById(R.id.etc_course3);
        etc_course[3] = (NetworkImageView) findViewById(R.id.etc_course4);
        etc_course[4] = (NetworkImageView) findViewById(R.id.etc_course5);
        etc_course[5] = (NetworkImageView) findViewById(R.id.etc_course6);

        info_name_etc_course[0] = (TextView) findViewById(R.id.info_name_etc_course1);
        info_name_etc_course[1] = (TextView) findViewById(R.id.info_name_etc_course2);
        info_name_etc_course[2] = (TextView) findViewById(R.id.info_name_etc_course3);
        info_name_etc_course[3] = (TextView) findViewById(R.id.info_name_etc_course4);
        info_name_etc_course[4] = (TextView) findViewById(R.id.info_name_etc_course5);
        info_name_etc_course[5] = (TextView) findViewById(R.id.info_name_etc_course6);

        info_km_etc_course[0] = (TextView) findViewById(R.id.info_km_etc_course1);
        info_km_etc_course[1] = (TextView) findViewById(R.id.info_km_etc_course2);
        info_km_etc_course[2] = (TextView) findViewById(R.id.info_km_etc_course3);
        info_km_etc_course[3] = (TextView) findViewById(R.id.info_km_etc_course4);
        info_km_etc_course[4] = (TextView) findViewById(R.id.info_km_etc_course5);
        info_km_etc_course[5] = (TextView) findViewById(R.id.info_km_etc_course6);

        setInfo();
    }

    private void setInfo() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String dml = "select * from course where local="+local;
                return NetworkAction.sendDataToServer("course.php", dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

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


                        int count = 0;
                        for(Properties p : list) {
                            setCourseImageURL(etc_course[count], p.getProperty("picture"));
                            info_name_etc_course[count].setText(p.getProperty("name"));
                            info_km_etc_course[count].setText(p.getProperty("km")+" KM");
                            setCourseEvent(p.getProperty("id"), etc_course[count]);

                            count++;
                        }

                    }
                }.execute();
            }
        }.execute();
    }

    private void setCourseEvent(final String id, NetworkImageView networkImageView) {
        networkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CourseDetailActivity.class);
                intent.putExtra("id", id);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }

    public void setCourseImageURL(final NetworkImageView imageView, final String imageURL) {
        if (imageView != null && imageURL != null) {
            Application app = GlobalApplication.getGlobalApplicationContext();
            if (app == null)
                throw new UnsupportedOperationException("needs com.kakao.GlobalApplication in order to use ImageLoader");
            imageView.setImageUrl("http://condi.swu.ac.kr:80/condi2/course/"+imageURL, ((GlobalApplication) app).getImageLoader());
        }
    }

}
