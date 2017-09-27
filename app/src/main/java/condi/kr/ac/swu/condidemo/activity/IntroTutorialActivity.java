package condi.kr.ac.swu.condidemo.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.toolbox.NetworkImageView;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.GlobalApplication;


public class IntroTutorialActivity extends Activity implements View.OnClickListener {

    ImageButton btn_before, btn_next;
    NetworkImageView img_tutorial1, img_tutorial2, img_tutorial3;
    ViewFlipper flipper;
    TextView txt_tutorial_check;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_tutorial);
        flipper = (ViewFlipper) findViewById(R.id.flipper);
        btn_before = (ImageButton) findViewById(R.id.btn_before);
        btn_next = (ImageButton) findViewById(R.id.btn_next);
        txt_tutorial_check = (TextView)findViewById(R.id.txt_tutorial_check);

        btn_before.setOnClickListener(this);
        btn_next.setOnClickListener(this);

        img_tutorial1 = (NetworkImageView) findViewById(R.id.img_tutorial1);
        img_tutorial2 = (NetworkImageView) findViewById(R.id.img_tutorial2);
        img_tutorial3 = (NetworkImageView) findViewById(R.id.img_tutorial3);
        setImageURL(img_tutorial1, "img_tutorial1.png");
        setImageURL(img_tutorial2, "img_tutorial2.png");
        setImageURL(img_tutorial3, "img_tutorial3.png");
    }

    @Override
    public void onClick(View view) {
        if (view == btn_before) {
            if (index == 0) {

            } else if (index == 1) {
                index = 0;
                btn_before.setVisibility(View.INVISIBLE);
                flipper.showPrevious();
            } else if (index == 2) {
                index = 1;
                flipper.showPrevious();
            }
        } else if (view == btn_next) {
            if (index == 0) {
                index = 1;
                btn_before.setVisibility(View.VISIBLE);
                flipper.showNext();
            } else if (index == 1) {
                btn_before.setVisibility(View.VISIBLE);
                txt_tutorial_check.setVisibility(View.VISIBLE);
                index = 2;
                flipper.showNext();
            } else if (index == 2) {
                btn_before.setVisibility(View.VISIBLE);
                startActivity(new Intent(getApplicationContext(), LoadingActivity.class));
                finish();
            }
        }
    }

    public void setImageURL(final NetworkImageView imageView, final String imageURL) {
        if (imageView != null && imageURL != null) {
            Application app = GlobalApplication.getGlobalApplicationContext();
            if (app == null)
                throw new UnsupportedOperationException("needs com.kakao.GlobalApplication in order to use ImageLoader");
            imageView.setImageUrl("http://condi.swu.ac.kr:80/condi2/picture/" + imageURL, ((GlobalApplication) app).getImageLoader());
        }
    }


}


