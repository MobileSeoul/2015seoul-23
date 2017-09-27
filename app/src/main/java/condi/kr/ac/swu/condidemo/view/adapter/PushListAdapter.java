package condi.kr.ac.swu.condidemo.view.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.NetworkAction;
import condi.kr.ac.swu.condidemo.data.Session;

/**
 * Created by 8304 on 2015-10-23.
 */
public class PushListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> data;


    public PushListAdapter(Context context, List<Properties> data) {
        this.context = context;

        ArrayList<HashMap<String, String>> maps = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;
        String key = "";

        for(Properties p : data) {
            map = new HashMap<String, String>();
            Enumeration keys = p.propertyNames();
            while(keys.hasMoreElements()) {
                key = keys.nextElement().toString();
                map.put(key, p.getProperty(key));
            }
            maps.add(map);
        }

        this.data = maps;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null)
            convertView = inflater.inflate(R.layout.push_list, null);


        TextView txtPushUserName = (TextView) convertView.findViewById(R.id.txtPushUserName);
        TextView txtPushCourseName = (TextView) convertView.findViewById(R.id.txtPushCourseName);
        TextView txtPushCourseKm = (TextView) convertView.findViewById(R.id.txtPushCourseKm);   // km
        Button btnPush = (Button) convertView.findViewById(R.id.btnPush);

        txtPushUserName.setText(data.get(position).get("mname"));
        txtPushCourseKm.setText(data.get(position).get("ckm"));

        setCourseName(txtPushCourseName, data.get(position).get("cname"), btnPush);
        setButton(btnPush, data.get(position).get("mid"), data.get(position).get("mname"));

        return convertView;
    }

    public void setCourseName(final TextView courseName, final String course, final Button btnPush) {
        if(course.equals("")) {
            courseName.setText("선택 중입니다.");
            btnPush.setVisibility(View.VISIBLE);
        } else {
            courseName.setText(course);
            btnPush.setVisibility(View.INVISIBLE);
        }
    }

    private void setButton(Button button, final String receiver, final String name) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        Properties p = new Properties();
                        p.setProperty("sender", Session.ID);
                        p.setProperty("receiver", receiver);
                        p.setProperty("sendername", Session.NICKNAME);
                        p.setProperty("type", "1");

                        return NetworkAction.sendDataToServer("gcmp.php", p);
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);

                        Toast.makeText(context, name+"님 에게 푸시를 보냅니다.", Toast.LENGTH_SHORT).show();
                    }
                }.execute();
            }
        });
    }

}
