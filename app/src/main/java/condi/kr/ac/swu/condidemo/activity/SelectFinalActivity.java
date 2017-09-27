package condi.kr.ac.swu.condidemo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import condi.kr.ac.swu.condidemo.view.CurvTextView;
import condi.kr.ac.swu.condidemo.view.adapter.PushListAdapter;


public class SelectFinalActivity extends RootActivity {

    private List<Properties> list;      // 모든 코스들
    private List<Properties> members;   // 멤버들
    private Course[] courses = new Course[6];
    private String local;

    private ArrayList<String> selected = new ArrayList<String>();   // 선택된 코스들 이름
    private ArrayList<String> selectedMembers = new ArrayList<String>();    //선택한 멤버들 이름

    private List<Properties> memberCourse;
    private PushListAdapter adapter;
    private ListView pushList;

    private Button btnGoBackChoice,btnGoTutorial;

    private ArrayList<ImageButton> imageButtons;
    private ImageButton
            btnSelectedCourseName1, btnSelectedCourseName2, btnSelectedCourseName3,
            btnSelectedCourseName4, btnSelectedCourseName5, btnSelectedCourseName6;

    private CurvTextView curvTextView;

    private ArrayList<TextView> textViews;
    private TextView
            txtSelectedSpeechBox1, txtSelectedSpeechBox2, txtSelectedSpeechBox3,
            txtSelectedSpeechBox4, txtSelectedSpeechBox5, txtSelectedSpeechBox6,
            txtPushUserName1, txtPushCourseName1, txtPushCourseKm1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_final);
        initActionBar("코스선택");
        initView();
    }

    private void checkCourse() {

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                Properties p = new Properties();

                String dml1 = "select count(*) as count from member where groups="+Session.GROUPS;
                String dml2 = "select count(*) as count from member where groups="+ Session.GROUPS+" and course!=0";

                p.setProperty("dml1", dml1);
                p.setProperty("dml2", dml2);


                return NetworkAction.sendDataToServer("isLast.php", p);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(o.equals("yes"))
                    redirectTutorialActivity();
                else
                    toastErrorMsg("모두 코스를 선택할 때가지 기다려주세요.");
            }
        }.execute();

    }

    private void initView() {
        // 내꺼
        txtPushUserName1 = (TextView) findViewById(R.id.txtPushUserName1);
        txtPushCourseName1 = (TextView) findViewById(R.id.txtPushCourseName1);
        txtPushCourseKm1 = (TextView) findViewById(R.id.txtPushCourseKm1);

        // 말풍선
        curvTextView = (CurvTextView) findViewById(R.id.txtSelectedCourseArray);
        txtSelectedSpeechBox1 = (TextView) findViewById(R.id.txtSelectedSpeechBox1);
        txtSelectedSpeechBox2 = (TextView) findViewById(R.id.txtSelectedSpeechBox2);
        txtSelectedSpeechBox3 = (TextView) findViewById(R.id.txtSelectedSpeechBox3);
        txtSelectedSpeechBox4 = (TextView) findViewById(R.id.txtSelectedSpeechBox4);
        txtSelectedSpeechBox5 = (TextView) findViewById(R.id.txtSelectedSpeechBox5);
        txtSelectedSpeechBox6 = (TextView) findViewById(R.id.txtSelectedSpeechBox6);

        txtSelectedSpeechBox1.setVisibility(View.INVISIBLE);
        txtSelectedSpeechBox2.setVisibility(View.INVISIBLE);
        txtSelectedSpeechBox3.setVisibility(View.INVISIBLE);
        txtSelectedSpeechBox4.setVisibility(View.INVISIBLE);
        txtSelectedSpeechBox5.setVisibility(View.INVISIBLE);
        txtSelectedSpeechBox6.setVisibility(View.INVISIBLE);

        textViews = new ArrayList<TextView>();
        textViews.add(txtSelectedSpeechBox1);
        textViews.add(txtSelectedSpeechBox2);
        textViews.add(txtSelectedSpeechBox3);
        textViews.add(txtSelectedSpeechBox4);
        textViews.add(txtSelectedSpeechBox5);
        textViews.add(txtSelectedSpeechBox6);

        // 이미지 버튼
        btnSelectedCourseName1 = (ImageButton) findViewById(R.id.btnSelectedCourseName1);
        btnSelectedCourseName2 = (ImageButton) findViewById(R.id.btnSelectedCourseName2);
        btnSelectedCourseName3 = (ImageButton) findViewById(R.id.btnSelectedCourseName3);
        btnSelectedCourseName4 = (ImageButton) findViewById(R.id.btnSelectedCourseName4);
        btnSelectedCourseName5 = (ImageButton) findViewById(R.id.btnSelectedCourseName5);
        btnSelectedCourseName6 = (ImageButton) findViewById(R.id.btnSelectedCourseName6);

        imageButtons = new ArrayList<ImageButton>();
        imageButtons.add(btnSelectedCourseName1);
        imageButtons.add(btnSelectedCourseName2);
        imageButtons.add(btnSelectedCourseName3);
        imageButtons.add(btnSelectedCourseName4);
        imageButtons.add(btnSelectedCourseName5);
        imageButtons.add(btnSelectedCourseName6);

        //버튼
        btnGoBackChoice = (Button) findViewById(R.id.btnGoBackChoice);
        btnGoTutorial = (Button) findViewById(R.id.btnGoTutorial);
        btnGoTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCourse();
            }
        });

        setCourses();

    }

    private void redirectTutorialActivity() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                String dml = "update groups set startdate=date_format(now(),'%y-%m-%d') ,currentwalk=0 where id="+Session.GROUPS;
                Properties p = new Properties();
                p.setProperty("sender", Session.ID);
                p.setProperty("sendername", Session.NICKNAME);
                p.setProperty("type", "8");

                return NetworkAction.sendDataToServer("gcm.php", p, dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                Intent intent = new Intent(getApplicationContext(), TutorialActivity.class);
                startActivity(intent);
                finish();
            }
        }.execute();

    }

    private void initMySelection() {
        txtPushUserName1.setText(Session.NICKNAME);

        int id = 0;
        for(int i=0; i<courses.length; i++) {
            if(Session.COURSE.equals(courses[i].id)) {
                id = i;
                txtPushCourseName1.setText(courses[i].name);
                txtPushCourseKm1.setText(courses[i].km);
            }
        }

        imageButtons.get(id).setImageResource(R.drawable.course_button_mint);

        textViews.get(id).setVisibility(View.VISIBLE);
        textViews.get(id).setBackgroundResource(R.drawable.speechbubble_green);
        textViews.get(id).setText(Session.NICKNAME + " 님 선택");
        curvTextView.clickedCourse(id);
        curvTextView.invalidate();


    }

    private void initOtherSelection(int position, String selector) {

        imageButtons.get(position).setImageResource(R.drawable.course_button_red);
        textViews.get(position).setVisibility(View.VISIBLE);
        textViews.get(position).setBackgroundResource(R.drawable.speechbubble_pink);
        textViews.get(position).setText(String.format("%s 님 선택", selector));

    }


    /*
    * 코스 로드
    * */
    private void setCourses() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                String dml = "select * " +
                        "from course " +
                        "where local=(" +
                            "select region " +
                            "from groups " +
                            "where id="+Session.GROUPS+"" +
                        ")";
                //String dml = "select * from course where local='"+local+"'";
                return NetworkAction.sendDataToServer("course.php", dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                /*
                * 모든 코스 로드
                * */
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

                            /*
                            * 선택된 코스인지 확인하고 세팅
                            * */
                            new AsyncTask() {
                                @Override
                                protected Object doInBackground(Object[] params) {
                                    String dml = "select * from member where groups=" + Session.GROUPS + " and id!='"+Session.ID+"'";
                                    return NetworkAction.sendDataToServer("member.php", dml);
                                }

                                @Override
                                protected void onPostExecute(Object o) {
                                    super.onPostExecute(o);
                                    new AsyncTask() {


                                        @Override
                                        protected void onPreExecute() {
                                            super.onPreExecute();
                                            /*
                                            * 코스 이름 세팅
                                            * */
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

                                            Properties mc;
                                            String mid = "";
                                            String mname = "";
                                            String cname = "";
                                            String km = "";

                                            /*
                                            * 선택된 코스 얻어오기 성공
                                            * 그 코스를 선택한 사람 얻어오기 성공
                                            * */
                                            memberCourse = new ArrayList<Properties>();
                                            for (Properties p : members) {
                                                mc = new Properties();
                                                cname = "";
                                                km = "";
                                                for (Properties ps : list) {
                                                    if (p.getProperty("course").equals(ps.getProperty("id"))) {
                                                        selected.add(ps.getProperty("name"));
                                                        selectedMembers.add(p.getProperty("nickname"));
                                                        initOtherSelection(list.indexOf(ps), p.getProperty("nickname"));

                                                        cname = ps.getProperty("name");
                                                        km = ps.getProperty("km");
                                                    }

                                                }
                                                mid = p.getProperty("id");
                                                mname = p.getProperty("nickname");

                                                mc.setProperty("mid", mid);
                                                mc.setProperty("mname", mname);
                                                mc.setProperty("cname", cname);
                                                mc.setProperty("ckm", km);

                                                memberCourse.add(mc);
                                            }

                                            initMySelection();

                                            /*
                                            * 말풍선이랑 뷰에 반영
                                            * */
                                            curvTextView.selectedCourse(selected);
                                            curvTextView.invalidate();


                                            /*
                                            * 리스트에 반영
                                            * */
                                            pushList = (ListView) findViewById(R.id.pushList);
                                            adapter = new PushListAdapter(getApplicationContext(), memberCourse);
                                            pushList.setAdapter(adapter);
                                        }
                                    }.execute();
                                }
                            }.execute();


                        }
                    }.execute();

                } else {
                    toastErrorMsg("코스 정보를 로드하지 못했습니다.");
                }
            }
        }.execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(selectReceiver, new IntentFilter("condi.kr.ac.swu.condiproject.course"));
        registerReceiver(startReceiver, new IntentFilter("condi.kr.ac.swu.condiproject.count.walk"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(selectReceiver);
        unregisterReceiver(startReceiver);
    }

    private BroadcastReceiver selectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setCourses();
        }
    };

    private BroadcastReceiver startReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startActivity(new Intent(getApplicationContext(), TutorialActivity.class));
            finish();
        }
    };
}
