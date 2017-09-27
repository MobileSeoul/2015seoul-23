package condi.kr.ac.swu.condidemo.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.BackPressCloseHandler;


public class RootActivity extends ActionBarActivity {

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
        backPressCloseHandler = new BackPressCloseHandler(this);
    }

    protected void initActionBar(String title) {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        ((TextView)findViewById(R.id.titleText)).setText(title);
        ((ImageButton) findViewById(R.id.sidemenu)).setVisibility(View.INVISIBLE);

        ((ImageButton) findViewById(R.id.icon_home)).setVisibility(View.INVISIBLE);
    }

    protected void toastErrorMsg(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    protected void printErrorMsg(String msg) {
        System.out.println(String.format("** error!! : %s\n", msg));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }
}
