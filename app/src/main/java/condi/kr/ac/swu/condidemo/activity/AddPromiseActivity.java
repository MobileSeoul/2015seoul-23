package condi.kr.ac.swu.condidemo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.NetworkAction;
import condi.kr.ac.swu.condidemo.data.Session;


public class AddPromiseActivity extends AppCompatActivity
        implements OnDateSelectedListener, OnMonthChangedListener,
                    TimePicker.OnTimeChangedListener, AdapterView.OnItemSelectedListener, View.OnClickListener {

    private int mode;
    private final DateFormat FORMATTER = new SimpleDateFormat("yyyy.MM.dd (E)");

    private Button cancelPromise, addPromise;
    private MaterialCalendarView calendarView;
    private TextView newSchDate, newSchTime;
    private TimePicker timePicker;
    private Spinner spinner;
    private EditText schContentsNew;

    private ArrayList<String> courseNames;

    private String total, promise_date, promise_time, content, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promise);
        mode = getIntent().getIntExtra("mode", -1);
        initActionBar();
        initView();
    }

    private void initView() {
        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        newSchDate = (TextView) findViewById(R.id.new_sch_date);

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        newSchTime = (TextView) findViewById(R.id.new_sch_time);
        timePicker.setOnTimeChangedListener(this);

        calendarView.setOnDateChangedListener(this);
        calendarView.setOnMonthChangedListener(this);

        spinner = (Spinner) findViewById(R.id.spinner_location);
        setCourseNames();

        schContentsNew = (EditText) findViewById(R.id.sch_contents_new);

        addPromise = (Button) findViewById(R.id.addPromise);
        cancelPromise = (Button) findViewById(R.id.cancelPromise);

        addPromise.setOnClickListener(this);
        cancelPromise.setOnClickListener(this);
    }


    protected void initActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        if(mode==1)
            ((TextView)findViewById(R.id.titleText)).setText("새로운 약속잡기");
        else
            ((TextView)findViewById(R.id.titleText)).setText("약속 수정하기");


    }

    private void setCourseNames() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String dml = "select * from course where local=(select region from groups where id="+ Session.GROUPS+")";
                return NetworkAction.sendDataToServer("course.php", dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(!o.equals("error")) {
                    new AsyncTask() {

                        List<Properties> list;

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            courseNames = new ArrayList<String>();
                        }

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

                            for(Properties p : list) {
                                courseNames.add(p.getProperty("name"));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddPromiseActivity.this, R.layout.spinner_list, R.id.spinner_text, courseNames);
                            spinner.setAdapter(adapter);
                            spinner.setOnItemSelectedListener(AddPromiseActivity.this);
                        }
                    }.execute();
                } else {
                    System.out.println("코스 정보를 로드할 수 없습니다.");
                }
            }
        }.execute();
    }

    private String getSelectedDatesString() {
        CalendarDay date = calendarView.getSelectedDate();
        if (date == null) {
            return FORMATTER.format(new Date());
        }
        return FORMATTER.format(date.getDate());
    }

    private void setTotal() {
        CalendarDay date = calendarView.getSelectedDate();
        promise_date = new SimpleDateFormat("yyMMdd").format(date.getDate()).toString();
        total = promise_date+promise_time; /* format : '%y%m%d%I%i' */
    }

    /*
    * ============================================== Event =====================================================
    * */
    @Override
    public void onDateSelected(MaterialCalendarView materialCalendarView, CalendarDay calendarDay, boolean b) {
        newSchDate.setText(getSelectedDatesString());
    }

    @Override
    public void onMonthChanged(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
        Toast.makeText(getApplicationContext(), FORMATTER.format(calendarDay.getDate()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
        String ampm = "";
        String hour = "";
        String min = "";

        if(hourOfDay>12)
            ampm = "오후";
        else
            ampm = "오전";

        if(hourOfDay < 10)
            hour = String.format("0%s",hourOfDay);
        else
            hour = Integer.toString(hourOfDay);

        if(minute < 10)
            min = String.format("0%s",minute);
        else
            min = Integer.toString(minute);

        promise_time = hour+min;
        newSchTime.setText(String.format("%s %s시 %s분", ampm, hour, min));
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        location = courseNames.get(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        if(view == addPromise) {
            setTotal();
            content = schContentsNew.getText().toString();

            new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    String dml = "";
                    if(mode == 1 /* new */)
                        dml = "insert into promise(pdate, location, content, writer, groups) values( str_to_date("+total+",'%y%m%d%I%i'), '"+location+"', '"+content+"', '"+Session.ID+"', "+Session.GROUPS+")";
                    else if(mode == 0 /* modify */)
                        dml = "update promise set pdate=str_to_date("+total+",'%y%m%d%I%i'), location='"+location+"', content='"+content+"' where writer='"+Session.ID+"' and groups="+Session.GROUPS;

                    System.out.println("dml : "+dml);
                    return NetworkAction.sendDataToServer(dml);
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    if(o.equals("success")) {
                        startActivity(new Intent(getApplicationContext(), PromiseActivity.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "약속 잡기를 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        } else if (view == cancelPromise) {
            startActivity(new Intent(getApplicationContext(), PromiseActivity.class));
            finish();
        }
    }
}
