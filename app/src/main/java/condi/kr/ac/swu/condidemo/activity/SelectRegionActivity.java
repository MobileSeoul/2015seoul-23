package condi.kr.ac.swu.condidemo.activity;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.GlobalApplication;
import condi.kr.ac.swu.condidemo.data.NetworkAction;
import condi.kr.ac.swu.condidemo.data.Session;


public class SelectRegionActivity extends RootActivity {

    private Button btnSelectCourse;
    private TextView txtRegionName1, txtRegionName2, txtRegionName3;
    private NetworkImageView imgMap;
    private final String SERVER_ADDRESS = "http://condi.swu.ac.kr:80/condi2/picture/";

    private String local = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_region);
        initActionBar("코스 선택");
        initView();
    }

    private void initView(){
        imgMap = (NetworkImageView) findViewById(R.id.imgMap);
        btnSelectCourse = (Button) findViewById(R.id.btnSelectCourse);
        txtRegionName1 = (TextView) findViewById(R.id.txtRegionName1);
        txtRegionName2 = (TextView) findViewById(R.id.txtRegionName2);
        txtRegionName3 = (TextView) findViewById(R.id.txtRegionName3);

        setMapURL("map1.png");
        setRegionName();

        btnSelectCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectCourseActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void setMapURL(final String mapImageURL) {
        if (imgMap != null && mapImageURL != null) {
            Application app = GlobalApplication.getGlobalApplicationContext();
            if (app == null)
                throw new UnsupportedOperationException("needs com.kakao.GlobalApplication in order to use ImageLoader");
            imgMap.setImageUrl(SERVER_ADDRESS+mapImageURL, ((GlobalApplication) app).getImageLoader());
        }
    }

    private void setRegionName() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                String dml = "select region from groups where id= "+ Session.GROUPS+"";
                printErrorMsg(dml);
                return NetworkAction.sendDataToServer("selectRegion.php", dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                String region = o.toString();
                local = region;
                txtRegionName1.setText(region);
                txtRegionName2.setText(region);
                txtRegionName3.setText(region);
            }
        }.execute();
    }

}
