package condi.kr.ac.swu.condidemo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.BackPressCloseHandler;
import condi.kr.ac.swu.condidemo.data.NetworkAction;
import condi.kr.ac.swu.condidemo.data.Session;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imgCourseBackground;
    private TextView txtCourseWalkKM, txtWalkCount,
            txtCourseName1, txtCourseName2, txtCourseKM, btnCoursePicture;
    private Button btnMyWalk, btnGroup;

    // 비디오 재생
    private VideoView videoCourseBackground;
    private int videoIndex = 0;

    // course
    private ArrayList<Float> courseKM = new ArrayList<Float>();
    private ArrayList<String> courseName = new ArrayList<String>();

    private BackPressCloseHandler backPressCloseHandler;

    int walk = 0;
    float km = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initActionBar("어울림");

        printErrorMsg("Main onCreate");

        initView();
        initVideo();

        backPressCloseHandler = new BackPressCloseHandler(this);
        setMy();
    }

    private void initView() {
        videoCourseBackground = (VideoView) findViewById(R.id.videoCourseBackground);
        imgCourseBackground = (ImageView) findViewById(R.id.imgCourseBackground);

        videoCourseBackground.setVisibility(View.INVISIBLE);
        imgCourseBackground.setVisibility(View.VISIBLE);

        txtCourseWalkKM = (TextView) findViewById(R.id.txtCourseWalkKM);
        txtWalkCount = (TextView) findViewById(R.id.txtWalkCount);
        txtCourseName1 = (TextView) findViewById(R.id.txtCourseName1);
        txtCourseName2 = (TextView) findViewById(R.id.txtCourseName2);
        txtCourseKM = (TextView) findViewById(R.id.txtCourseKM);
        btnMyWalk = (Button) findViewById(R.id.btnMyWalk);
        btnGroup = (Button) findViewById(R.id.btnGroup);
        btnCoursePicture = (TextView) findViewById(R.id.btnCoursePicture);

        btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GroupActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
        });
        btnMyWalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MyActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
        });

    }

    private void initVideo() {
        //videoCourseBackground.setVideoURI(Uri.parse("http://sssol2.esy.es/condi/walkinforest0609.mp4"));

        imgCourseBackground.setVisibility(View.INVISIBLE);
        videoCourseBackground.setVisibility(View.VISIBLE);
        videoCourseBackground.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.walkinforest1_1009));
        videoCourseBackground.requestFocus();
        videoCourseBackground.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoCourseBackground.start();
            }
        });

        videoCourseBackground.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoCourseBackground.start();
            }
        });

    }

    private void setMy() {

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String dml = "select * from course where id in (select course from member where groups="+ Session.GROUPS+")";
                printErrorMsg(dml);
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

                            int cnt = 0;
                            for(Properties p : list) {
                                printErrorMsg(p.getProperty("name"));
                                courseName.add(cnt,p.getProperty("name"));
                                courseKM.add(cnt, Float.parseFloat(p.getProperty("km")));
                                cnt++;
                            }

                        }
                    }.execute();
                } else {
                    printErrorMsg("myView 에서 error 입니다.");
                }
            }
        }.execute();

    }

    @Override
    protected void onStart() {
        super.onStart();
        printErrorMsg("Main onStart");
        registerReceiver(sensorReceiver, new IntentFilter("condi.kr.ac.swu.condiproject.step"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(sensorReceiver);
    }

    private BroadcastReceiver sensorReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            walk = Integer.parseInt(intent.getStringExtra("walk"));
            km = (float) ( Math.round(walk * 0.011559 * 100)/100);

            txtCourseWalkKM.setText(String.format("%s ", km));
            txtWalkCount.setText(String.format("%s",walk));

            new Handler().post(new Runnable() {
                @Override
                public void run() {

                    if (km > courseKM.get(0)) {
                        if (km > courseKM.get(0) + courseKM.get(1)) {
                            if (km > courseKM.get(0) + courseKM.get(1) + courseKM.get(2)) {
                                if (km > courseKM.get(0) + courseKM.get(1) + courseKM.get(2) + courseKM.get(3)) {
                                    toastErrorMsg("목표에 도달하셨습니다.");
                                } else {
                                    txtCourseName1.setText(courseName.get(3));
                                    txtCourseKM.setText(String.format("%s",courseKM.get(3)));
                                }
                            } else {
                                txtCourseName1.setText(courseName.get(2));
                                txtCourseKM.setText(String.format("%s", courseKM.get(2)));
                            }
                        } else {
                            txtCourseName1.setText(courseName.get(1));
                            txtCourseKM.setText(String.format("%s", courseKM.get(1)));
                        }
                    } else {
                        txtCourseName1.setText(courseName.get(0));
                        txtCourseKM.setText(String.format("%s", courseKM.get(0)));
                    }
                }
            });

        }
    };

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }


}
