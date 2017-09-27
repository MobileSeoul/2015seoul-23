package condi.kr.ac.swu.condidemo.activity;

import android.app.Application;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.GlobalApplication;
import condi.kr.ac.swu.condidemo.data.NetworkAction;

public class CourseDetailActivity extends BaseActivity {

    private int id;
    private TextView info_each_name, info_each_km, txtCourseInfoDetail1, txtCourseInfoDetail2;
    private NetworkImageView imgCourseDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        initActionBar("코스 정보");
        id = Integer.parseInt(getIntent().getStringExtra("id"));
        initView();
    }

    private void initView() {
        info_each_name = (TextView) findViewById(R.id.info_each_name);
        info_each_km = (TextView) findViewById(R.id.info_each_km);
        txtCourseInfoDetail1 = (TextView) findViewById(R.id.txtCourseInfoDetail1);
        txtCourseInfoDetail2 = (TextView) findViewById(R.id.txtCourseInfoDetail2);
        imgCourseDetail = (NetworkImageView) findViewById(R.id.imgCourseDetail);

        setInfo();
    }

    private void setInfo() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String dml = "select * from course where id="+id;
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

                        info_each_name.setText(list.get(0).getProperty("name"));
                        info_each_km.setText(list.get(0).getProperty("km"));
                        setCourseImageURL(imgCourseDetail,list.get(0).getProperty("picture") );

                        String info1, info2;
                        if(list.get(0).getProperty("info").length()>20) {
                            info1 = list.get(0).getProperty("info").substring(0, 19);
                            if(list.get(0).getProperty("info").length()>40)
                                info2 = list.get(0).getProperty("info").substring(19, 40);
                            else
                                info2 = list.get(0).getProperty("info").substring(19, list.get(0).getProperty("info").length()-1);
                        } else {
                            info1 = list.get(0).getProperty("info");
                            info2 = "";
                        }
                        txtCourseInfoDetail1.setText(info1);
                        txtCourseInfoDetail2.setText(info2);
                    }
                }.execute();
            }
        }.execute();
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
