package condi.kr.ac.swu.condidemo.activity;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.GlobalApplication;
import condi.kr.ac.swu.condidemo.data.NetworkAction;
import condi.kr.ac.swu.condidemo.data.Session;
import condi.kr.ac.swu.condidemo.view.CircularNetworkImageView;
import condi.kr.ac.swu.condidemo.view.adapter.InviteListAdapter;

public class PreGroupActivity extends RootActivity {

    private String senderId = "";
    private InviteListAdapter adapter;
    private ListView inviteList;

    // 프로필 관련 뷰
    private CircularNetworkImageView myProfile;
    private TextView myName, myPhone;

    private Button btnRoomExit, btnRoomStart;
    private ImageButton btn_pre_add_friend;
    private RelativeLayout btn_pre_add_friend_back;

    private static Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_group);
        initActionBar("어울림방 만들기");
        initView();
        start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(memberReceiver);
        unregisterReceiver(startReceiver);
    }

    /*
        * ---------------------------------------------------------------------------------
        * */
    private void initView() {
        // 뷰
        myProfile = (CircularNetworkImageView) findViewById(R.id.myProfile);
        myName = (TextView) findViewById(R.id.myName);
        myPhone = (TextView) findViewById(R.id.myPhone);

        btnRoomExit = (Button) findViewById(R.id.btnRoomExit);
        btnRoomStart = (Button) findViewById(R.id.btnRoomStart);
        btn_pre_add_friend = (ImageButton) findViewById(R.id.btn_pre_add_friend);
        btn_pre_add_friend_back = (RelativeLayout) findViewById(R.id.btn_pre_add_friend_back);

        if(isSender()) {
            btn_pre_add_friend_back.setVisibility(View.VISIBLE);
            btn_pre_add_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), AddFriendActivity.class);
                    i.putExtra("sender", Session.ID);
                    i.putExtra("count", 0);
                    i.putExtra("isSender", isSender());
                    startActivity(i);
                    finish();
                }
            });
        } else {
            btn_pre_add_friend_back.setVisibility(View.INVISIBLE);
            ViewGroup.LayoutParams params = btn_pre_add_friend_back.getLayoutParams();
            params.height = 0;
        }

        initProfile();
        initEvent();
    }

    private void initEvent() {
        btnRoomStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAllOK();

            }
        });
    }

    private void initProfile() {
        setProfileURL(Session.PROFILE);
        myName.setText(Session.NICKNAME);
        myPhone.setText(Session.PHONE);
    }

    public void setProfileURL(final String profileImageURL) {
        Application app = GlobalApplication.getGlobalApplicationContext();
        if (app == null)
            throw new UnsupportedOperationException("needs com.kakao.GlobalApplication in order to use ImageLoader");

        if (myProfile != null && profileImageURL != null) {
            if(profileImageURL.equals(""))
                myProfile.setImageUrl("http://condi.swu.ac.kr:80/condi2/profile/thumb_story.png", ((GlobalApplication) app).getImageLoader());
            else
                myProfile.setImageUrl("http://condi.swu.ac.kr:80/condi2/profile/"+profileImageURL, ((GlobalApplication) app).getImageLoader());
        } else
            myProfile.setImageUrl("http://condi.swu.ac.kr:80/condi2/profile/thumb_story.png", ((GlobalApplication) app).getImageLoader());
    }

    private void start() {
        if(isSender())  // 센더는 나면서 시작
            loadInviteList();
        else    // 센더정보받아온다음에 리스트 뿌려주기
            new Receiver().execute();
    }

    /*
    * true : sender
    * false : receiver
    * */
    private boolean isSender() {
        return getIntent().getBooleanExtra("mode", true);
    }

    private void isAllOK() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                String dml = "select i.*, g.* from (select count(*) as icount from invite where sender='"+senderId+"') i, (select count(*) as gcount from invite where ok=1) g";

                return NetworkAction.sendDataToServer("checkInviting.php", dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(o.equals("same"))
                    insertGroups();
                else
                    toastErrorMsg("모두가 초대에 응해야 시작할 수 있어요!");
            }
        }.execute();
    }


    public void loadInviteList() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                String sender = "";
                if(isSender()) {
                    sender = Session.ID;
                    senderId = sender;
                } else {
                    sender = senderId;
                }
                String dml =  "select m.id, m.nickname, m.profile, m.phone, i.ok " +
                    "from member m, invite i " +
                    "where m.id in (select receiver from invite where sender='"+senderId+"' and receiver != '"+Session.ID+"') and m.id=i.receiver";

                return NetworkAction.sendDataToServer("inviteList.php",dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if(o.equals("success")) {
                    new AsyncTask() {

                        List<Properties> list;
                        @Override
                        protected Object doInBackground(Object[] params) {
                            try {
                                list = NetworkAction.parse("inviteList.xml", "invite");
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
                            final int count = list.size();
                            inviteList = (ListView) findViewById(R.id.inviteList);
                            adapter = new InviteListAdapter(PreGroupActivity.this.getApplicationContext(), list);
                            View footer = getLayoutInflater().inflate(R.layout.invited_list_footer, null, false);

                            if(count > 0) {
                                btn_pre_add_friend_back.setVisibility(View.INVISIBLE);
                                ViewGroup.LayoutParams params = btn_pre_add_friend_back.getLayoutParams();
                                params.height = 0;
                            }

                            if(count>=3) {
                                footer.setVisibility(View.INVISIBLE);
                            } else {
                                footer.setVisibility(View.VISIBLE);
                                footer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                            Intent i = new Intent(PreGroupActivity.this.getApplicationContext(), AddFriendActivity.class);
                                            i.putExtra("sender", senderId);
                                            i.putExtra("count", count);
                                            i.putExtra("isSender", isSender());
                                            startActivity(i);
                                            finish();

                                    }
                                });
                            }

                            inviteList.addFooterView(footer);
                            inviteList.setAdapter(adapter);
                            if(count>=3)
                                inviteList.removeFooterView(footer);
                        }
                    }.execute();
                }
            }
        }.execute();
    }

    private class Receiver extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String dml = "select sender from invite where receiver='"+Session.ID+"'";

            return NetworkAction.sendDataToServer("getSender.php", dml);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("error"))
                Toast.makeText(getApplicationContext(), "sender정보 받아오기 실패", Toast.LENGTH_LONG).show();
            else {
                senderId = s;
                loadInviteList();
            }
        }
    }

    private void insertGroups() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                String dml = "insert into groups(host, region) values('"+senderId+"', '중구')";

                return NetworkAction.sendDataToServer( dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);


                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        String dml = "update member set groups=(select id from groups where host='"+senderId+"') where id in (select receiver from invite where sender='"+senderId+"')";
                        Properties p = new Properties();
                        p.setProperty("sender", Session.ID);
                        p.setProperty("sendername", Session.NICKNAME);
                        p.setProperty("type", "6");
                        return NetworkAction.sendDataToServer("gcm.php",p, dml);
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        new AsyncTask() {
                            @Override
                            protected String doInBackground(Object[] params) {
                                Properties p = new Properties();
                                p.setProperty("id", Session.ID);
                                return NetworkAction.sendDataToServer("my.php", p);
                            }

                            @Override
                            protected void onPostExecute(Object o) {
                                super.onPostExecute(o);
                                new AsyncTask() {
                                    List<Properties> my;
                                    @Override
                                    protected Object doInBackground(Object[] params) {
                                        try {
                                            my = NetworkAction.parse("my.xml", "member");
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
                                        Session.removeAllPreferences(getApplicationContext());
                                        Session.savePreferences(getApplicationContext(), my.get(0));
                                        redirectSelectRegionActivity();
                                    }
                                }.execute();
                            }
                        }.execute();

                    }
                }.execute();


            }
        }.execute();
    }

    private void redirectSelectRegionActivity() {
        startActivity(new Intent(getApplicationContext(), SelectRegionActivity.class));
        finish();
    }


    /*
    * -------------------------------------------------------------------------
    * */

    private void checkStart() {
        registerReceiver(memberReceiver, new IntentFilter("condi.kr.ac.swu.condiproject.invite"));
        registerReceiver(startReceiver, new IntentFilter("condi.kr.ac.swu.condiproject.start.groups"));
    }

    private BroadcastReceiver memberReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(isSender())  // 센더는 나면서 시작
                loadInviteList();
            else    // 센더정보받아온다음에 리스트 뿌려주기
                new Receiver().execute();
        }
    };

    private BroadcastReceiver startReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            new MyPHP().execute();
        }
    };


    /*
    * 사용자 정보 로드
    * */
    private class MyPHP extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            Properties prop = new Properties();
            prop.setProperty("id", Session.ID);
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
        protected void onPreExecute() {
            super.onPreExecute();
        }

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

            redirectSelectRegionActivity();
        }
    }
}
