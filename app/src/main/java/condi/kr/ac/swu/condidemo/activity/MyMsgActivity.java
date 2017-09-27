package condi.kr.ac.swu.condidemo.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.NetworkAction;
import condi.kr.ac.swu.condidemo.data.Session;
import condi.kr.ac.swu.condidemo.view.adapter.MyMsgListAdapter;


public class MyMsgActivity extends BaseActivity {

    private ListView msg_list;
    private MyMsgListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_msg);
        setMsgList();
    }

    private void setMsgList() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                String dml = "select msg.*, mem.nickname as sendername, mem.profile as profile " +
                        "from message msg, member mem " +
                        "where msg.sender=mem.id and " +
                        "msg.receiver='"+ Session.ID+"' limit 20";
                return NetworkAction.sendDataToServer("mymsg.php", dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                if(o.equals("success"))
                    new AsyncTask() {

                    List<Properties> list;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        msg_list = (ListView) findViewById(R.id.msg_list);
                    }

                    @Override
                    protected Object doInBackground(Object[] objects) {
                        try {
                            list = NetworkAction.parse("mymsg.xml","message");
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

                        initActionBar(String.format("메시지함(%s/20)", list.size()));
                        adapter = new MyMsgListAdapter(getApplicationContext(), list);
                        msg_list.setAdapter(adapter);
                    }
                }.execute();
                else
                    toastErrorMsg("메시지를 불러올 수 없습니다.");
            }
        }.execute();
    }
}
