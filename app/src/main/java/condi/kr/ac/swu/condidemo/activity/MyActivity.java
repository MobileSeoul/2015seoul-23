package condi.kr.ac.swu.condidemo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.NetworkAction;
import condi.kr.ac.swu.condidemo.data.Session;
import condi.kr.ac.swu.condidemo.view.CustomCircularRingView2;
import condi.kr.ac.swu.condidemo.view.PatchPointView;

public class MyActivity extends BaseActivity {

    private CustomCircularRingView2 myView;
    private PatchPointView patchPointView;

    private TextView txtTotalDate, txtTotalKM ; // 전체 일수, km
    private TextView txtPercent, txtCurrentDate, txtCurrentKM;

    private TextView myStep;

    // 경로
    private TextView txtCourseName1, txtCourseName2, txtCourseName3, txtCourseName4;   // 나머지 코스 이름
    private float courseKm1, courseKm2, courseKm3, courseKm4;

    private static String GRAY = "#FF777777";
    private static String BLUE = "#ff30c9ff";
    private static String WHITE = "#ffffff";

    private List<Properties> walks;
    private LinearLayout barChart, barDate;

    private int walk = 0;
    private int period = 0;
    private float totalKM;

    // thread
    private Handler graphHandler = new Handler();
    private Thread th;

    int percent = 0;
    int currentStep = 0;
    long currentKM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        initActionBar("나의 걸음");
        initView();
        initGraph();
        setSensor();
    }

    /*
    * ============================================================================================================================================
    * */
    private void initView() {
        // graph
        myView = (CustomCircularRingView2) findViewById(R.id.customCircularRingView2);
        myView.changePercentage(0);
        myView.invalidate();

        patchPointView = (PatchPointView) findViewById(R.id.patchPointView2);
        txtPercent = (TextView) findViewById(R.id.txtPercent2);
        txtCurrentKM = (TextView) findViewById(R.id.txtCurrentKM2);

        myStep = (TextView) findViewById(R.id.myStep);

        // 경로
        txtCourseName1 = (TextView) findViewById(R.id.txtCourseName12);
        txtCourseName2 = (TextView) findViewById(R.id.txtCourseName22);
        txtCourseName3 = (TextView) findViewById(R.id.txtCourseName32);
        txtCourseName4 = (TextView) findViewById(R.id.txtCourseName42);

        // date
        txtTotalDate = (TextView) findViewById(R.id.txtTotalDate2);
        txtTotalKM = (TextView) findViewById(R.id.txtTotalKM2);
        txtCurrentDate = (TextView) findViewById(R.id.txtCurrentDate2);
        setDateKM();
        setMy();
        setMyView();
    }

    private void setDateKM () {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String dml = "select goaldays, goalkm, date_format(startdate,'%y%m%d') as startdate " +
                        "from groups " +
                        "where id="+ Session.GROUPS;
                return NetworkAction.sendDataToServer("goaldayskm.php",dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                String[] results;
                if(o.equals("error"))
                    printErrorMsg("error : cannot select goaldays, goalkm");
                else {
                    results = o.toString().split("/");
                    txtTotalDate.setText(results[0]);
                    txtTotalKM.setText(results[1]);
                    totalKM = Float.parseFloat(results[1]);

                    printErrorMsg("totalKM : " + totalKM);
                    /*
                    * current date
                    * */
                    try {
                        Date startDate = new SimpleDateFormat("yyMMdd").parse(results[2]);       // startdate
                        Date today = new Date();

                        period = startDate.compareTo(today);
                        txtCurrentDate.setText(Integer.toString(period));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    private void setMy() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String dml = "select * from course where id in (select course from member where groups="+Session.GROUPS+")";
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

                            double[] km = new double[list.size()];
                            int cnt = 0;
                            int sum = 0;
                            List<Integer> points = new ArrayList<Integer>();
                            for(Properties p : list) {
                                points.add(sum);
                                km[cnt] = Double.parseDouble(p.getProperty("km"));
                                sum +=(int)(km[cnt]/totalKM*100);

                                switch (cnt) {
                                    case 0 :
                                        txtCourseName1.setText(p.getProperty("name"));
                                        courseKm1 = Float.parseFloat(p.getProperty("km"));
                                        break;
                                    case 1 :
                                        txtCourseName2.setText(p.getProperty("name"));
                                        courseKm2 = Float.parseFloat(p.getProperty("km"));
                                        break;
                                    case 2 :
                                        txtCourseName3.setText(p.getProperty("name"));
                                        courseKm3 = Float.parseFloat(p.getProperty("km"));
                                        break;
                                    case 3 :
                                        txtCourseName4.setText(p.getProperty("name"));
                                        courseKm4 = Float.parseFloat(p.getProperty("km"));
                                        break;
                                }
                                cnt++;
                            }
                            patchPointView.setPercentToPoint(points);
                            patchPointView.invalidate();

                        }
                    }.execute();
                } else {
                    printErrorMsg("myView 에서 error 입니다.");
                }
            }
        }.execute();

    }

    private void setMyView() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (percent < 100) {
                    String dml = "select sum(currentwalk) as count " +
                            "from walk " +
                            "where user='"+Session.ID+"'";
                    currentStep = Integer.parseInt(NetworkAction.sendDataToServer("sum.php", dml));
                    currentKM = Math.round(currentStep * 0.011559 * 100)/100;
                    percent = (int)((float) currentKM / totalKM *100);

                    graphHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            myView.changePercentage(percent);
                            myView.invalidate();

                            txtPercent.setText(Integer.toString(percent));
                            txtCurrentKM.setText(Float.toString(currentKM));

                            if(currentKM > courseKm1) {
                                if(currentKM > courseKm1+courseKm2) {
                                    if(currentKM > courseKm1+courseKm2+courseKm3) {
                                        if(currentKM > courseKm1+courseKm2+courseKm3+courseKm4) {
                                            toastErrorMsg("목표에 도달하셨습니다.");
                                        } else {
                                            txtCourseName1.setBackgroundResource(R.drawable.route_blank);
                                            txtCourseName2.setBackgroundResource(R.drawable.route_blank);
                                            txtCourseName3.setBackgroundResource(R.drawable.route_blank);
                                            txtCourseName4.setBackgroundResource(R.drawable.road_nametag_on);
                                        }
                                    } else {
                                        txtCourseName1.setBackgroundResource(R.drawable.route_blank);
                                        txtCourseName2.setBackgroundResource(R.drawable.route_blank);
                                        txtCourseName3.setBackgroundResource(R.drawable.road_nametag_on);
                                    }
                                } else {
                                    txtCourseName1.setBackgroundResource(R.drawable.route_blank);
                                    txtCourseName2.setBackgroundResource(R.drawable.road_nametag_on);
                                }
                            } else {
                                txtCourseName1.setBackgroundResource(R.drawable.road_nametag_on);
                            }
                        }
                    });

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    /*
    * ==============================================================================================================================================
    * */

    /*
    * init메소드
    * */
    private void initGraph() {
        barChart = (LinearLayout) findViewById(R.id.barChart);
        barDate = (LinearLayout) findViewById(R.id.barDate);
        new loadingWalk().execute();
    }

    /*
    * 걸음수 로드 내부 클래스
    * */
    private class loadingWalk extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
           String dml = "select * from walk where user='"+ Session.ID+"'";
            return NetworkAction.sendDataToServer("mywalk.php", dml);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equals("success")) {
                new readWalk().execute();
            } else {
                System.out.println("데이터 가져오기 오류 : walk");
            }
        }
    }

    private class readWalk extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                walks = NetworkAction.parse("mywalk.xml", "walk");
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

            if(!walks.isEmpty()) {
                drawGragh(walks);
            } else {
                System.out.println("걸음 기록이 없습니다.");
            }
        }
    }

    /*
    * 그래프 그리기
    *   1. drawChart : 막대그래프
    *   2. drawDate : 날짜
    * */
    public void drawGragh(List<Properties> walks) {
        drawChart(walks);
        drawDate(walks);
    }

    public void drawChart(List<Properties> walks) {
        View view;
        TextView km;
        float scaleFactor;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        scaleFactor = metrics.density;
        System.out.println("scaleFactor : "+scaleFactor);

        float maxValue = 0;
        for(Properties p : walks) {
            if(toKMFloat(p) > maxValue)
                maxValue = toKMFloat(p);
        }

        int barWidth = (int) (30*scaleFactor);
        int barHeight;
        System.out.println("barWidth : " + barWidth);

        String color = GRAY;
        int count = 0;
        float normalWalk = 3.5f;
        for(Properties p : walks) {
            view = new View(this);
            km = new TextView(this);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.BOTTOM);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));


            LinearLayout.LayoutParams linearLayoutlp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            if(toKMFloat(p) > normalWalk)
                color = BLUE;
            else
                color = GRAY;
            view.setBackgroundColor(Color.parseColor(color));
            km.setTextColor(Color.parseColor(color));
            km.setText(toKMString(p));

            barHeight = (int) (toKMFloat(p)/maxValue * 100 * scaleFactor);
            if(barHeight == 0)
                barHeight = (int)scaleFactor;
            view.setLayoutParams(new LinearLayout.LayoutParams(barWidth, barHeight));
            km.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            km.setGravity(Gravity.CENTER_HORIZONTAL);

            linearLayout.addView(km, linearLayoutlp);
            linearLayout.addView(view);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            params.setMargins(5*(int)scaleFactor, 5*(int)scaleFactor, 0, 0);
            linearLayout.setLayoutParams(params);
            barChart.addView(linearLayout);

            count++;
        }
    }

    public void drawDate(List<Properties> walks) {
        TextView view1, view2;

        float scaleFactor;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        scaleFactor = metrics.density;

        int barWidth = (int) (30*scaleFactor);

        String[] dates = new String[2];
        String color1 = GRAY;
        String color2= GRAY;

        int count = 0;
        for(Properties p : walks) {
            view1 = new TextView(this);
            view2 = new TextView(this);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            LinearLayout.LayoutParams linearLayoutlp = new LinearLayout.LayoutParams(
                    barWidth,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            dates = getToday(p);

            if(count == walks.size()-1) {
                color1 = WHITE;
                color2 = BLUE;

                dates[0] = dates[0].substring(3,dates[0].length());
                view1.setBackgroundResource(R.drawable.date_oval);
            } else if(count==0) {
                color1 = GRAY;
                color2 = GRAY;

                dates[0] = dates[0].substring(1,dates[0].length());
            } else {
                color1 = GRAY;
                color2 = GRAY;

                dates[0] = dates[0].substring(3,dates[0].length());
            }
            view1.setTextColor(Color.parseColor(color1));
            view1.setText(dates[0]);
            view1.setLayoutParams(new LinearLayout.LayoutParams(barWidth, barWidth));
            view1.setGravity(Gravity.CENTER);

            view2.setTextColor(Color.parseColor(color2));
            view2.setText(dates[1]);
            view2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            view2.setGravity(Gravity.CENTER_HORIZONTAL);

            linearLayout.addView(view1);
            linearLayout.addView(view2, linearLayoutlp);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            params.setMargins(5*(int)scaleFactor, 5*(int)scaleFactor, 0, 0);
            linearLayout.setLayoutParams(params);
            barDate.addView(linearLayout);

            count++;
        }
    }

    /*
    * 날짜와 걸음수 변환 메소드
    * */
    private String toKMString(Properties p) {
        return (isWalkEmpty(getCurrentWalk(p)) ?
                "0"
                : Double.toString(Math.round(Integer.parseInt(getCurrentWalk(p)) * 0.011559 * 100)/100));
    }

    private float toKMFloat(Properties p) {
        return (!isWalkEmpty(getCurrentWalk(p)) ?
                Math.round(Integer.parseInt(getCurrentWalk(p)) * 0.011559 * 100)/100
                : 0.0f);
    }

    private String getCurrentWalk(Properties p) {
        return p.getProperty("currentwalk");
    }

    private String[] getToday(Properties p) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat f1 = new SimpleDateFormat("MM/dd");
        SimpleDateFormat f2 = new SimpleDateFormat("E");

        Date date;
        String[] dates = new String[2];
        try {
            date = f.parse(p.getProperty("today"));
            String d1 = f1.format(date);
            String d2 = f2.format(date);

            dates[0] = d1;
            dates[1] = d2;
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("날짜 변환 오류!!");
        }
        return dates;
    }

    private boolean isWalkEmpty(String walk) {
        return ((walk.equals("")) ? true  : false);
    }

    /*----------------------------------------------------- senser -------------------------------------------------------------*/
    private void setSensor() {
        registerReceiver(broadcastReceiver, new IntentFilter("condi.kr.ac.swu.condiproject.step"));
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            walk = Integer.parseInt(intent.getStringExtra("walk"));
            myStep.setText(String.format("%s",walk));

            th = new Thread(new Runnable() {
                @Override
                public void run() {
                    DateFormat df = new SimpleDateFormat("yyMMdd");
                    df.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

                    String today = df.format(new Date());

                    String dml = "update walk set currentwalk="+ walk +" where user='"+ Session.ID+"' and date_format(today,'%y%m%d')=str_to_date('"+ today +"','%y%m%d')";
                    System.out.println("걸음 : "+ dml + "\n ==> "+NetworkAction.sendDataToServer(dml)+" : "+walk);

                }
            });
            th.start();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }
}
