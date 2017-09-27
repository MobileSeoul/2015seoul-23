package condi.kr.ac.swu.condidemo.activity;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.GlobalApplication;

public class GoalActivity extends BaseActivity {

    private NetworkImageView imgMap;
    private final String SERVER_ADDRESS = "http://condi.swu.ac.kr:80/condi2/picture/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);
        initActionBar("목표 완성");
        initView();
    }

    private void initView(){
        imgMap = (NetworkImageView) findViewById(R.id.imgMapNext);
        setMapURL("map2.png");
    }

    public void setMapURL(final String mapImageURL) {
        if (imgMap != null && mapImageURL != null) {
            Application app = GlobalApplication.getGlobalApplicationContext();
            if (app == null)
                throw new UnsupportedOperationException("needs com.kakao.GlobalApplication in order to use ImageLoader");
            imgMap.setImageUrl(SERVER_ADDRESS+mapImageURL, ((GlobalApplication) app).getImageLoader());
        }
    }

}
