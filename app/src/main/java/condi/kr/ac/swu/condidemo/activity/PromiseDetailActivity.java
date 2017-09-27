package condi.kr.ac.swu.condidemo.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.NetworkAction;


public class PromiseDetailActivity extends BaseActivity implements View.OnClickListener{

    private int pid;
    private Button btn_delete_promise, btn_edit_promise;
    private TextView txt_sch_detail_date, txt_sch_detail_time, txt_sch_detail_location, txt_shc_detail_contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promise_detail);
        initActionBar("약속보기");

        System.out.println("pid : " + (pid = Integer.parseInt(getIntent().getStringExtra("pid"))));
        initView();
    }

    private void initView() {
        txt_sch_detail_date = (TextView) findViewById(R.id.txt_sch_detail_date);
        txt_sch_detail_time = (TextView) findViewById(R.id.txt_sch_detail_time);
        txt_sch_detail_location = (TextView) findViewById(R.id.txt_sch_detail_location);
        txt_shc_detail_contents = (TextView) findViewById(R.id.txt_shc_detail_contents);

        btn_delete_promise = (Button) findViewById(R.id.btn_delete_promise);
        btn_edit_promise = (Button) findViewById(R.id.btn_edit_promise);

        btn_delete_promise.setOnClickListener(this);
        btn_edit_promise.setOnClickListener(this);

        setInfo();
    }

    private void setInfo() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String dml = "select * from promise where id="+pid;
                return NetworkAction.sendDataToServer("promise.php", dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(o.equals("success")) {
                    new AsyncTask() {

                        List<Properties> list;

                        @Override
                        protected Object doInBackground(Object[] objects) {
                            try {
                                list = NetworkAction.parse("promise.xml", "promise");
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

                            Date date;
                            String[] dates = new String[2];
                            SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd (E)");
                            SimpleDateFormat format2 = new SimpleDateFormat("a HH시 mm분");
                            try {
                                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(list.get(0).get("pdate").toString());
                                dates[0] = format1.format(date);
                                dates[1] = format2.format(date);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            txt_sch_detail_date.setText(dates[0]);
                            txt_sch_detail_time.setText(dates[1]);
                            txt_sch_detail_location.setText(list.get(0).get("location").toString());
                            txt_shc_detail_contents.setText(list.get(0).get("content").toString());
                        }
                    }.execute();
                }
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {
        if(v == btn_delete_promise) {
            new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    String dml = "delete from promise where id="+pid;
                    return NetworkAction.sendDataToServer(dml);
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);

                    if(o.equals("success")) {
                        startActivity(new Intent(getApplicationContext(), PromiseActivity.class));
                        finish();
                    } else {
                        toastErrorMsg("삭제를 실패했습니다.");

                        startActivity(new Intent(getApplicationContext(), PromiseActivity.class));
                        finish();
                    }

                }
            }.execute();

        } else if(v == btn_edit_promise) {
            Intent intent = new Intent(getApplicationContext(), AddPromiseActivity.class);
            intent.putExtra("mode", 0);
            startActivity(intent);
            finish();
        }


    }
}
