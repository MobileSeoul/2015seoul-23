package condi.kr.ac.swu.condidemo.view.adapter;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;
import condi.kr.ac.swu.condidemo.data.GlobalApplication;
import condi.kr.ac.swu.condidemo.view.CircularNetworkImageView;

/**
 * Created by Chang on 10/20/2015.
 */
public class AddFriendListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> data;

    public AddFriendListAdapter(Context context, List<Properties> data) {
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
            convertView = inflater.inflate(R.layout.add_friend_list, null);

        // 리소스와 연결
        TextView friendName = (TextView) convertView.findViewById(R.id.friendName);     // 장미희
        CircularNetworkImageView friendProfile = (CircularNetworkImageView) convertView.findViewById(R.id.friendProfile);   //프로필사진
        ImageView friendCheckBox = (ImageView) convertView.findViewById(R.id.friendCheckBox);


        //텍스트 세팅
        friendName.setText(data.get(position).get("nickname"));

        //이미지세팅
        setProfileURL(friendProfile, data.get(position).get("profile"));

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

}
