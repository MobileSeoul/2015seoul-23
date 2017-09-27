package condi.kr.ac.swu.condidemo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.Course;
import condi.kr.ac.swu.condidemo.data.NetworkAction;
import condi.kr.ac.swu.condidemo.data.Session;
import condi.kr.ac.swu.condidemo.service.AccSensor;
import condi.kr.ac.swu.condidemo.service.SuccessService;
import condi.kr.ac.swu.condidemo.view.CurvTextView;


public class TutorialActivity extends RootActivity {

    private Course[] courses = new Course[6];

    private ImageView imgTotalGoalShot;
    private TextView txtTutorialCourseSum, txtTutorialDaysSum, txtTutorialName1, txtTutorialName2, txtTutorialName3, txtTutorialName4;
    private Button btnMain;

    private List<Properties> list;
    private List<Properties> members;

    private ArrayList<String> selected = new ArrayList<String>();
    private ArrayList<String> selectedMembers = new ArrayList<String>();

    private ArrayList<ImageButton> imageButtons;
    private ImageButton
            btnTutorialCourseName1, btnTutorialCourseName2, btnTutorialCourseName3,
            btnTutorialCourseName4, btnTutorialCourseName5, btnTutorialCourseName6;

    private CurvTextView curvTextView;

    private ArrayList<TextView> textViews;
    private TextView
            txtTutorialSpeechBox1, txtTutorialSpeechBox2, txtTutorialSpeechBox3,
            txtTutorialSpeechBox4, txtTutorialSpeechBox5, txtTutorialSpeechBox6;

    private int days = 0;
    private float km = 0.00f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        initActionBar("어울림 시작하기");
        initView();
        setCourses();
    }

    private void initView() {
        curvTextView = (CurvTextView) findViewById(R.id.txtTutorialCourseArray);

        txtTutorialSpeechBox1 = (TextView) findViewById(R.id.txtTutorialSpeechBox1);
        txtTutorialSpeechBox2 = (TextView) findViewById(R.id.txtTutorialSpeechBox2);
        txtTutorialSpeechBox3 = (TextView) findViewById(R.id.txtTutorialSpeechBox3);
        txtTutorialSpeechBox4 = (TextView) findViewById(R.id.txtTutorialSpeechBox4);
        txtTutorialSpeechBox5 = (TextView) findViewById(R.id.txtTutorialSpeechBox5);
        txtTutorialSpeechBox6 = (TextView) findViewById(R.id.txtTutorialSpeechBox6);

        txtTutorialSpeechBox1.setVisibility(View.INVISIBLE);
        txtTutorialSpeechBox2.setVisibility(View.INVISIBLE);
        txtTutorialSpeechBox3.setVisibility(View.INVISIBLE);
        txtTutorialSpeechBox4.setVisibility(View.INVISIBLE);
        txtTutorialSpeechBox5.setVisibility(View.INVISIBLE);
        txtTutorialSpeechBox6.setVisibility(View.INVISIBLE);

        textViews = new ArrayList<TextView>();
        textViews.add(txtTutorialSpeechBox1);
        textViews.add(txtTutorialSpeechBox2);
        textViews.add(txtTutorialSpeechBox3);
        textViews.add(txtTutorialSpeechBox4);
        textViews.add(txtTutorialSpeechBox5);
        textViews.add(txtTutorialSpeechBox6);

        btnTutorialCourseName1 = (ImageButton) findViewById(R.id.btnTutorialCourseName1);
        btnTutorialCourseName2 = (ImageButton) findViewById(R.id.btnTutorialCourseName2);
        btnTutorialCourseName3 = (ImageButton) findViewById(R.id.btnTutorialCourseName3);
        btnTutorialCourseName4 = (ImageButton) findViewById(R.id.btnTutorialCourseName4);
        btnTutorialCourseName5 = (ImageButton) findViewById(R.id.btnTutorialCourseName5);
        btnTutorialCourseName6 = (ImageButton) findViewById(R.id.btnTutorialCourseName6);

        imageButtons = new ArrayList<ImageButton>();
        imageButtons.add(btnTutorialCourseName1);
        imageButtons.add(btnTutorialCourseName2);
        imageButtons.add(btnTutorialCourseName3);
        imageButtons.add(btnTutorialCourseName4);
        imageButtons.add(btnTutorialCourseName5);
        imageButtons.add(btnTutorialCourseName6);

        btnMain = (Button) findViewById(R.id.btnMain);
        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectMainActivity();
            }
        });

        txtTutorialCourseSum = (TextView) findViewById(R.id.txtTutorialCourseSum);
        txtTutorialDaysSum = (TextView) findViewById(R.id.txtTutorialDaysSum);
        txtTutorialName1 = (TextView) findViewById(R.id.txtTutorialName1);
        txtTutorialName2 = (TextView) findViewById(R.id.txtTutorialName2);
        txtTutorialName3 = (TextView) findViewById(R.id.txtTutorialName3);
        txtTutorialName4 = (TextView) findViewById(R.id.txtTutorialName4);

    }

    private void initSelection(int position, String selector) {

        textViews.get(position).setVisibility(View.VISIBLE);

        if(selector.equals(Session.NICKNAME)) {
            imageButtons.get(position).setImageResource(R.drawable.course_button_mint);
            textViews.get(position).setBackgroundResource(R.drawable.speechbubble_green);
        } else {
            imageButtons.get(position).setImageResource(R.drawable.course_button_red);
            textViews.get(position).setBackgroundResource(R.drawable.speechbubble_pink);
        }

        textViews.get(position).setText(String.format("%s 님 선택", selector));

    }

    private void setCourses() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                String dml = "select * " +
                        "from course " +
                        "where local=(" +
                        "select region " +
                        "from groups " +
                        "where id="+ Session.GROUPS+"" +
                        ")";
                return NetworkAction.sendDataToServer("course.php", dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (!o.equals("error")) {

                    new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] params) {
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

                            new AsyncTask() {
                                @Override
                                protected Object doInBackground(Object[] params) {
                                    String dml = "select * from member where groups=" + Session.GROUPS;
                                    return NetworkAction.sendDataToServer("member.php", dml);
                                }

                                @Override
                                protected void onPostExecute(Object o) {
                                    super.onPostExecute(o);
                                    new AsyncTask() {


                                        @Override
                                        protected void onPreExecute() {
                                            super.onPreExecute();
                                            String[] names = new String[6];

                                            int i = 0;
                                            for (Properties p : list) {
                                                courses[i] = new Course(p);
                                                names[i] = courses[i].name;
                                                i++;
                                            }
                                            curvTextView.courseName(names);
                                            curvTextView.invalidate();

                                        }

                                        @Override
                                        protected Object doInBackground(Object[] params) {
                                            try {
                                                members = NetworkAction.parse("member.xml", "member");
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

                                            for (Properties p : members) {

                                                for (Properties ps : list) {
                                                    if (p.getProperty("course").equals(ps.getProperty("id"))) {
                                                        selected.add(ps.getProperty("name"));
                                                        selectedMembers.add(p.getProperty("nickname"));
                                                        initSelection(list.indexOf(ps), p.getProperty("nickname"));

                                                        km += Double.parseDouble(ps.getProperty("km"));
                                                        days += (int) Math.ceil(Double.parseDouble(ps.getProperty("km"))/3.5);

                                                        switch (cnt) {
                                                            case 0 :
                                                                txtTutorialName1.setText(ps.getProperty("name"));
                                                                break;
                                                            case 1 :
                                                                txtTutorialName2.setText(ps.getProperty("name"));
                                                                break;
                                                            case 2 :
                                                                txtTutorialName3.setText(ps.getProperty("name"));
                                                                break;
                                                            case 3 :
                                                                txtTutorialName4.setText(ps.getProperty("name"));
                                                                break;
                                                        }
                                                        cnt++;
                                                    }
                                                }
                                            }

                                            curvTextView.selectedCourse(selected);
                                            curvTextView.invalidate();

                                            txtTutorialDaysSum.setText(Integer.toString(days));
                                            txtTutorialCourseSum.setText(String.format("%.1f KM" , km));

                                            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                            SharedPreferences.Editor editor = pref.edit();
                                            editor.putString("goalkm", String.format("%s",km));
                                            editor.putString("goaldays", String.format("%s",days));
                                            editor.commit();
                                        }
                                    }.execute();
                                }
                            }.execute();


                        }
                    }.execute();

                } else {
                    toastErrorMsg("안녕안녕");
                }
            }
        }.execute();
    }


    /*
    * redirect
    * */
    private void redirectMainActivity() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                String dml = "update groups set goalkm="+km+", goaldays="+days+" where id="+Session.GROUPS;
                return NetworkAction.sendDataToServer(dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(o.equals("success")) {
                    toastErrorMsg("어울림을 시작합니다.");
                    startSensor();
                    startService(new Intent(getApplicationContext(), SuccessService.class));
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    toastErrorMsg("메인으로 넘어갈 수 없습니다.");
                }
            }
        }.execute();

    }

    /*
    * sensor
    * */
    private void startSensor() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String dml = String.format("insert into walk values('%s',%s, date_add(now(), interval -9 hour), %s)", Session.ID, 0, Session.GROUPS);

                return NetworkAction.sendDataToServer(dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(o.equals("success")) {
                    startService(new Intent(getApplicationContext(), AccSensor.class));
                } else {
                    toastErrorMsg("error : cannot start step!");
                }
            }
        }.execute();

    }

}
