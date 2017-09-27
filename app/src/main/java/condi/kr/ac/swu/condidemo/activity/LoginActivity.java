package condi.kr.ac.swu.condidemo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.NetworkAction;
import condi.kr.ac.swu.condidemo.data.Session;
import condi.kr.ac.swu.condidemo.gcm.QuickstartPreferences;
import condi.kr.ac.swu.condidemo.gcm.RegistrationIntentService;


public class LoginActivity extends RootActivity {

    private EditText editLoginPhone;
    private EditText editLoginPassword;
    private Button btnLogin, btnPassword, goToJoin;

    private String phone;
    private String password;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "IntroActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initActionBar("어울림 로그인");
        initView();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    printErrorMsg(sentToken+"success");
                } else {
                    printErrorMsg(sentToken + "fail");
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    private void initView() {
        editLoginPhone = (EditText) findViewById(R.id.editLoginPhone);
        editLoginPassword = (EditText) findViewById(R.id.editLoginPassword);
        btnLogin = (Button) findViewById(R.id.btnLoign);
        btnPassword = (Button) findViewById(R.id.btnPassword);
        goToJoin = (Button) findViewById(R.id.goToJoin);

        btnLogin.setOnClickListener(new LoginEventListener());
        btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectPasswordActivity();
            }
        });
        goToJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectJoinActivity();
            }
        });
    }

    private void redirectJoinActivity() {
        startActivity(new Intent(getApplicationContext(), JoinActivity.class));
        finish();
    }

    private void redirectPasswordActivity() {
        Intent intent = new Intent(LoginActivity.this, PasswordActivity.class);
        startActivity(intent);
        finish();
    }

    private void redirectIntroTutorialActivity() {
        Intent intent = new Intent(LoginActivity.this, IntroTutorialActivity.class);
        startActivity(intent);
        finish();
    }


    private class LoginEventListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            phone = editLoginPhone.getText().toString();
            password = editLoginPassword.getText().toString();

            if (checkValue(phone, password)) {
                new Login().execute();
            }
        }
    }

    private boolean checkValue(String phone, String password) {
        if (TextUtils.isEmpty(phone)) {
            editLoginPhone.setError("전화번호를 입력해주세요.");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            editLoginPassword.setError("비밀번호를 입력해주세요.");
            return false;
        }

        return true;
    }

    private class Login extends AsyncTask<Void, Void, String> {

        Properties prop;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            prop = new Properties();
            prop.setProperty("phone", phone);
            prop.setProperty("password", password);
        }

        @Override
        protected String doInBackground(Void... params) {
            return NetworkAction.sendDataToServer("login.php", prop);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("success")) {
                new getMyInfo().execute();
            } else
                Toast.makeText(getApplicationContext(), "로그인을 할 수 없습니다.", Toast.LENGTH_SHORT).show();
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
            String key = "";
            String value = "";

            System.out.println("============고객 정보============");
            for (Properties p : props) {
                Enumeration keys = p.propertyNames();
                while (keys.hasMoreElements()) {
                    key = keys.nextElement().toString();
                    value = p.getProperty(key);

                    System.out.println(key + " : " + value);
                }
            }
            System.out.println("================================");
            Session.savePreferences(getApplicationContext(), props.get(0));


            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(LoginActivity.this, RegistrationIntentService.class);
                startService(intent);
            }

            //redirectCheckInviteActivity();
            redirectIntroTutorialActivity();
        }
    }

}
