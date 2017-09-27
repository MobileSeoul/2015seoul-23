package condi.kr.ac.swu.condidemo.view.adapter;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.GlobalApplication;
import condi.kr.ac.swu.condidemo.data.NetworkAction;
import condi.kr.ac.swu.condidemo.data.Session;
import condi.kr.ac.swu.condidemo.view.CircularNetworkImageView;

/**
 * Created by Chang on 10/20/2015.
 */
public class InviteListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> data;

    public InviteListAdapter(Context context, List<Properties> data) {
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

    /*
    * 오버라이드
    * */
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
            convertView = inflater.inflate(R.layout.invited_list, null);

        // 리소스와 연결
        TextView itemPhone = (TextView) convertView.findViewById(R.id.itemPhone);     //산보
        TextView itemName = (TextView) convertView.findViewById(R.id.itemName);     // 장미희
        CircularNetworkImageView itemProfile = (CircularNetworkImageView) convertView.findViewById(R.id.itemProfile);   //프로필사진
        Button itemButton = (Button) convertView.findViewById(R.id.itemButton);


        //텍스트 세팅
        itemPhone.setText(data.get(position).get("phone"));
        itemName.setText(data.get(position).get("nickname"));

        //이미지세팅
        setProfileURL(itemProfile, data.get(position).get("profile"));

        //버튼세팅
        if(data.get(position).get("ok").equals("1"))
            itemButton.setVisibility(View.INVISIBLE);
        else {
            itemButton.setVisibility(View.VISIBLE);
            setButton(itemButton, data.get(position).get("id"), data.get(position).get("nickname"));
        }

        return convertView;
    }

    /*
    * 세팅 메소트
    * */
    public void setProfileURL(final CircularNetworkImageView profile, final String profileImageURL) {

        Application app = GlobalApplication.getGlobalApplicationContext();
        if (app == null)
            throw new UnsupportedOperationException("needs com.kakao.GlobalApplication in order to use ImageLoader");

        if (profile != null && profileImageURL != null) {
            if(profileImageURL.equals(""))
                profile.setImageUrl("http://condi.swu.ac.kr:80/condi2/profile/thumb_story.png", ((GlobalApplication) app).getImageLoader());
            else
                profile.setImageUrl("http://condi.swu.ac.kr:80/condi2/profile/"+profileImageURL, ((GlobalApplication) app).getImageLoader());
        } else  {
            profile.setImageUrl("http://condi.swu.ac.kr:80/condi2/profile/thumb_story.png", ((GlobalApplication) app).getImageLoader());
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
                        String dml = "delete from invite where receiver = '"+receiver+"'";
                        p.setProperty("dml", dml);
                        p.setProperty("sender", Session.ID);
                        p.setProperty("sendername", Session.NICKNAME);
                        p.setProperty("type", "10");
                        return NetworkAction.sendDataToServer("gcm.php", p);
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);

                        Toast.makeText(context, name + "님을 초대 취소합니다.", Toast.LENGTH_SHORT).show();
                    }
                }.execute();
            }
        });
    }

}
