package condi.kr.ac.swu.condidemo.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.NetworkAction;
import condi.kr.ac.swu.condidemo.data.Session;
import condi.kr.ac.swu.condidemo.view.adapter.PromiseListAdapter;

public class PromiseActivity extends BaseActivity {

    private ListView promise_list;
    private ImageView btn_add_promise;
    private RelativeLayout addpromise;
    private TextView promise_tutorial;

    private List<Properties> promiseList;
    private PromiseListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promise);
        initActionBar("약속 잡기");
        initView();
    }

    private void initView() {
        btn_add_promise = (ImageView) findViewById(R.id.btn_add_promise);
        promise_tutorial = (TextView) findViewById(R.id.promise_tutorial);
        addpromise = (RelativeLayout) findViewById(R.id.addpromise);

        btn_add_promise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddPromiseActivity.class);
                i.putExtra("mode", 1);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }
        });
        setPromiseList();
    }

    private void setPromiseList() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String dml = "select * from promise where groups="+ Session.GROUPS+" order by pdate desc";
                return NetworkAction.sendDataToServer("promise.php", dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(o.equals("success")) {
                    new AsyncTask() {
                        @Override
                        protected Object doInBackground(Object[] objects) {
                            try {
                                promiseList = NetworkAction.parse("promise.xml", "promise");
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

                            printErrorMsg(Integer.toString(promiseList.size()));
                            if(promiseList.size() <= 0) {
                                promise_tutorial.setVisibility(View.VISIBLE);
                            } else {
                                /*
                                * set listView
                                * */
                                addpromise.setVisibility(View.INVISIBLE);
                                promise_tutorial.setVisibility(View.INVISIBLE);
                                View header = getLayoutInflater().inflate(R.layout.promise_list_footer, null, false);
                                promise_list = (ListView) findViewById(R.id.promise_list);
                                adapter = new PromiseListAdapter(getApplicationContext(), promiseList);
                                promise_list.addFooterView(header);
                                promise_list.setAdapter(adapter);

                                promise_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        printErrorMsg("i => " + i);
                                        Intent intent = new Intent(getApplicationContext(), PromiseDetailActivity.class);
                                        intent.putExtra("pid", promiseList.get(i - 1).getProperty("id"));
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                    }
                                });

                                header.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(getApplicationContext(), AddPromiseActivity.class);
                                        i.putExtra("mode", 1);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);
                                    }
                                });
                            }
                        }
                    }.execute();
                }
            }
        }.execute();
    }
}
