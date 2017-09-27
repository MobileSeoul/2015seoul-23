package condi.kr.ac.swu.condidemo.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.NetworkAction;

public class PasswordActivity extends RootActivity {

    private EditText editPassPhone;
    private Button btnPass, btnPassGoLogin;
    private TextView txtTempPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        initActionBar("비밀번호 찾기");
        initView();
    }


    private void initView() {
        editPassPhone = (EditText) findViewById(R.id.editPassPhone);
        btnPass = (Button) findViewById(R.id.btnPass);
        btnPassGoLogin = (Button) findViewById(R.id.btnPassGoLogin);
        txtTempPass = (TextView) findViewById(R.id.txtTempPass);

        btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = editPassPhone.getText().toString();

                if(TextUtils.isEmpty(phone))
         editPassPhone.setError("전화번호를 입력해주세요.");
         //           Toast.makeText(getApplicationContext(), "전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                else
                    searchPassword(phone);
            }
        });

        btnPassGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }

    private void searchPassword(final String phone) {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                Properties p = new Properties();
                p.setProperty("phone", phone);
                return NetworkAction.sendDataToServer("password.php", p);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                String tempPass = o.toString();
                if(!tempPass.startsWith("error"))
                    txtTempPass.setText(tempPass);
                else
                    txtTempPass.setText("비밀번호를 찾을 수 없습니다.");
            }
        }.execute();
    }

}
