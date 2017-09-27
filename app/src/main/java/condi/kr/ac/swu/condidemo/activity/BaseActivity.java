package condi.kr.ac.swu.condidemo.activity;

import android.app.Application;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.BackPressCloseHandler;
import condi.kr.ac.swu.condidemo.data.GlobalApplication;
import condi.kr.ac.swu.condidemo.data.NetworkAction;
import condi.kr.ac.swu.condidemo.data.Session;
import condi.kr.ac.swu.condidemo.view.CircularNetworkImageView;
import condi.kr.ac.swu.condidemo.view.adapter.MsgListAdapter;


public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private List<Properties> lists;
    private ListView lv;
    private MsgListAdapter adapter;
    private SlidingMenu slidingMenu;

    private TextView btnGoSetting, see_more_msg;
    private Button btn_about_courses_list;

    private boolean isDialogShow = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMenu();

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(cockReceiver, new IntentFilter("condi.kr.ac.swu.condiproject.cock"));
        registerReceiver(goalReceiver, new IntentFilter("condi.kr.ac.swu.condiproject.success"));
    }

    /*
        * 초기화 메소드
        * */
    private void initMenu() {
        slidingMenu= new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.RIGHT);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW); //Menu view left side.
        slidingMenu.setMenu(R.layout.setting_slide);

        CircularNetworkImageView profile = (CircularNetworkImageView) slidingMenu.findViewById(R.id.slideProfile);
        TextView name = (TextView) slidingMenu.findViewById(R.id.slideName);
        Button btn_about_courses_list = (Button) slidingMenu.findViewById(R.id.btn_about_courses_list);
        btn_about_courses_list.setOnClickListener(this);

        setProfileURL(profile, Session.PROFILE);
        name.setText(Session.NICKNAME);

        new LoadMsg().execute();

        btnGoSetting = (TextView) slidingMenu.findViewById(R.id.btnGoSetting);
        btnGoSetting.setOnClickListener(this);

        see_more_msg = (TextView) slidingMenu.findViewById(R.id.see_more_msg);
        see_more_msg.setOnClickListener(this);

        btn_about_courses_list = (Button) slidingMenu.findViewById(R.id.btn_about_courses_list);
        btn_about_courses_list.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_about_courses_list :
                intent = new Intent(getApplicationContext(), CourseThisActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.see_more_msg :
                intent = new Intent(getApplicationContext(), MyMsgActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.btnGoSetting :
                intent = new Intent(getApplicationContext(), SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }

    protected void initActionBar(String title) {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        ((TextView)findViewById(R.id.titleText)).setText(title);
        ((ImageButton) findViewById(R.id.sidemenu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /*
    * 메시지 읽어오는 내부 클래스
    * */
    private class LoadMsg extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String dml = "select msg.*, mem.nickname as sendername, mem.profile as profile " +
                    "from message msg, member mem " +
                    "where msg.sender=mem.id and " +
                    "msg.receiver='"+ Session.ID+"' limit 4";
            printErrorMsg(dml);
            return NetworkAction.sendDataToServer("mymsg.php", dml);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("success"))
                new ReadMsg().execute();
            else
                printErrorMsg("메시지를 불러올 수 없습니다.");
        }
    }

    private class ReadMsg extends  AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
               lists = NetworkAction.parse("mymsg.xml", "message");
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
            lv = (ListView) slidingMenu.findViewById(R.id.lvMsg);
            adapter = new MsgListAdapter(slidingMenu.getContext().getApplicationContext(), lists);
            lv.setAdapter(adapter);
        }
    }

    public void setProfileURL(final CircularNetworkImageView myProfile, final String profileImageURL) {
        if (myProfile != null && profileImageURL != null) {
            Application app = GlobalApplication.getGlobalApplicationContext();
            if (app == null)
                throw new UnsupportedOperationException("needs com.kakao.GlobalApplication in order to use ImageLoader");
            myProfile.setImageUrl("http://condi.swu.ac.kr:80/condi2/profile/"+profileImageURL, ((GlobalApplication) app).getImageLoader());
        }
    }

    protected void toastErrorMsg(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    protected void printErrorMsg(String msg) {
        System.out.println(String.format("** error!! : %s\n", msg));
    }

    private BroadcastReceiver cockReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showRoomDialog(intent.getStringExtra("message"), intent.getStringExtra("sendername"));
        }
    };

    private BroadcastReceiver goalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showRoomDialog();
        }
    };

    public void showRoomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(850,450);

        TextView dlgDefaultText_big = (TextView) dialog.findViewById(R.id.dlgDefaultText_big);
        TextView dlgDefaultText_small = (TextView) dialog.findViewById(R.id.dlgDefaultText_small);
        Button dlgOk = (Button) dialog.findViewById(R.id.dlgOk);

        dlgDefaultText_big.setText("목 표 달 성");
        dlgDefaultText_small.setText("목표에 도달하셨습니다!");
        dlgOk.setText("확   인");

        dlgOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isDialogShow = false;
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                startActivity(new Intent(getApplicationContext(), GoalActivity.class));
                finish();
            }
        });

        dialog.show();
        isDialogShow = true;
    }

    public void showRoomDialog(String message, String sendername) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(850,450);

        TextView dlgDefaultText_big = (TextView) dialog.findViewById(R.id.dlgDefaultText_big);
        TextView dlgDefaultText_small = (TextView) dialog.findViewById(R.id.dlgDefaultText_small);
        Button dlgOk = (Button) dialog.findViewById(R.id.dlgOk);

        dlgDefaultText_big.setText(sendername+"님으로 부터");
        dlgDefaultText_small.setText(message);
        dlgOk.setText("확   인");

        dlgOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isDialogShow = false;
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

        dialog.show();
        isDialogShow = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(cockReceiver);
        unregisterReceiver(goalReceiver);
    }


    private void setSuccess() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                Properties p = new Properties();
                String dml = "select * from member where id='"+Session.ID+"'";
                p.setProperty("sender", Session.ID);
                p.setProperty("sendername", Session.NICKNAME);
                p.setProperty("type", "9");
                return NetworkAction.sendDataToServer("gcm.php", p, dml);
            }
        }.execute();
    }

}
