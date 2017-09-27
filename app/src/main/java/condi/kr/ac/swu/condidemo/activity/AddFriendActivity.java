package condi.kr.ac.swu.condidemo.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.NetworkAction;
import condi.kr.ac.swu.condidemo.data.Session;
import condi.kr.ac.swu.condidemo.view.adapter.AddFriendListAdapter;


public class AddFriendActivity extends RootActivity {

    private String sender = "";

    private boolean[] all;          // 모든 사람들이 선택 or 미선택 여부 판단을 위한 배열
    private int index = 0;              // 현재까지 선택된 사람수
    private ArrayList<String> selects = new ArrayList<String>();        // id가 저장

    private int count;              // 현재 초대된 인원 : 초대가능한 인원 3-count
    private List<Properties> list;
    private AddFriendListAdapter adapter;
    private ListView inviteFriendsList;

    private Button btnInviteFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        initActionBar("초대하기");
        btnInviteFinish = (Button) findViewById(R.id.btnInviteFinish);
        btnInviteFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    invite();
            }
        });

        setCount();
        setSender();
        loadFriend();
    }

    private void setCount() {
        count = getIntent().getIntExtra("count", -1);
        System.out.println("초대 가능한 인원 : "+ (3-count) );
    }

    private void setSender() {
        sender = getIntent().getStringExtra("sender");
    }

    private boolean isSender() {
        return getIntent().getBooleanExtra("isSender", true);
    }


    private void loadFriend() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                String dml = "select * from member where id not in (select receiver from invite) and id!='"+ Session.ID+"'";
                return NetworkAction.sendDataToServer("member.php", dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(o.equals("success"))
                    new AsyncTask() {

                    @Override
                    protected Object doInBackground(Object[] params) {
                        try {
                            list = NetworkAction.parse("member.xml", "member");
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

                        all = new boolean[list.size()];
                        for(int i=0; i<all.length; i++)
                            all[i] = false;

                        inviteFriendsList = (ListView) findViewById(R.id.inviteFriendsList);
                        adapter = new AddFriendListAdapter(AddFriendActivity.this.getApplicationContext(), list);
                        inviteFriendsList.setAdapter(adapter);
                        inviteFriendsList.setOnItemClickListener(new MemberListItemClickListener());

                    }
                }.execute();
                else
                    System.out.println("친구 정보를 불러오기 실패했습니다.");
            }
        }.execute();
    }

    private class MemberListItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if(all[position]) {
                /*
                * 선택 취소
                * 선택된 목록에서 제거
                * */
                selects.remove(list.get(position).getProperty("id"));
                index--;

                all[position] = false;
                view.setBackgroundResource(R.drawable.list_background);
                view.findViewById(R.id.friendCheckBox).setBackgroundResource(R.drawable.checkbox_blank);
            } else {
                /*
                * 선택
                *   선택 가능하면 --> 배경 파란색
                *   선택 불가능 --> 토스트
                * */
                if(index >= 3-count) {
                    // 선택 불가능
                    Toast.makeText(AddFriendActivity.this.getApplicationContext(), "더이상 친구를 선택할 수 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 선택 가능
                    selects.add(list.get(position).getProperty("id"));
                    index++;

                    all[position] = true;
                    view.setBackgroundResource(R.drawable.list_background_hover);
                    view.findViewById(R.id.friendCheckBox).setBackgroundResource(R.drawable.checkbox_filed);
                }
            }

        }
    }

    private void invite() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                Properties p = new Properties();
                p.setProperty("sender", sender);

                for(int i=0; i<index; i++)
                    p.setProperty("receiver"+(i+1), selects.get(i));

                return NetworkAction.sendDataToServer("invite.php", p);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                redirectPreGroupActivity();
            }
        }.execute();
    }

    private void redirectPreGroupActivity() {
        Intent i = new Intent(getApplicationContext(), PreGroupActivity.class);
        i.putExtra("mode", isSender());
        startActivity(i);
        finish();
    }

}
