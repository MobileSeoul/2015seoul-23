package condi.kr.ac.swu.condidemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.BackPressCloseHandler;
import condi.kr.ac.swu.condidemo.data.Session;


public class IntroActivity extends Activity implements View.OnClickListener {

    private BackPressCloseHandler backPressCloseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        backPressCloseHandler = new BackPressCloseHandler(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isLogout())
                    redirectLoadingActivity();
                else
                    redirectLoginActivity();
            }
        }, 2000);
    }

    private boolean isLogout() {
        return (Session.getPreferences(getApplicationContext(), "id").equals(""));
    }

    private void redirectLoginActivity() {
        startActivity(new Intent(IntroActivity.this, LoginActivity.class));
        finish();
    }

    private void redirectLoadingActivity() {
        startActivity(new Intent(IntroActivity.this, LoadingActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }
}


