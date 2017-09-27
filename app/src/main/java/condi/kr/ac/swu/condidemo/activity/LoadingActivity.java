package condi.kr.ac.swu.condidemo.activity;

import android.app.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.BackPressCloseHandler;
import condi.kr.ac.swu.condidemo.data.NetworkAction;
import condi.kr.ac.swu.condidemo.data.Session;


/*
* 분기  :
*   그룹 ?
*       o :
*       코스 ?
*           o :
*               걸음?
*                   o : main
*                   x : 선택중
*          x : 지역구 선택
*       x :
*          초대 체크
*
* */
public class LoadingActivity extends Activity implements View.OnClickListener {

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        backPressCloseHandler = new BackPressCloseHandler(this);
        new MyPHP().execute();

    }

    private void branchPage() {
        if (hasGroup()) {   // 그룹?
            if (hasCourse()) {      // 코스선택?
                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        String dml = "select * from walk where user='" + Session.ID + "' and groups=" + Session.GROUPS;
                        return NetworkAction.sendDataToServer("checkWalk.php", dml);
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        if (o.equals("ok"))
                            redirectMainActivity();
                        else
                            redirectSelectFinalActivity();
                    }
                }.execute();
            } else {
                redirectSelectRegionActivity();
            }
        } else {
            redirectCheckInviteActivity();
        }
    }

    private boolean hasGroup() {
        return (Session.getPreferences(getApplicationContext(), "groups").equals("") ? false : true);
    }

    private boolean hasCourse() {
        return (Session.getPreferences(getApplicationContext(), "course").equals("0") ? false : true);
    }

    /*
    * redirect
    * */
    private void redirectCheckInviteActivity() {
        Intent intent = new Intent(getApplicationContext(), CheckInviteActivity.class);
        startActivity(intent);
        finish();
    }

    private void redirectSelectCourseActivity() {
        startActivity(new Intent(getApplicationContext(), SelectCourseActivity.class));
        finish();
    }

    private void redirectSelectRegionActivity() {
        startActivity(new Intent(getApplicationContext(), SelectRegionActivity.class));
        finish();
    }

    private void redirectSelectFinalActivity() {
        startActivity(new Intent(getApplicationContext(), SelectFinalActivity.class));
        finish();
    }

    private void redirectMainActivity() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();

    }

    @Override
    public void onClick(View v) {

    }

    /*
    * 나의 최신 정보 로드
    * */
    private class MyPHP extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {
            Properties prop = new Properties();
            prop.setProperty("id", Session.getPreferences(getApplicationContext(), "id"));
            return NetworkAction.sendDataToServer("my.php", prop);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new getMyInfo().execute();
        }
    }

    private class getMyInfo extends AsyncTask<Void, Void, Void> {

        List<Properties> props;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                props = NetworkAction.parse("my.xml", "member");
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Session.removeAllPreferences(getApplicationContext());
            Session.savePreferences(getApplicationContext(), props.get(0));

            branchPage();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }
}

