package condi.kr.ac.swu.condidemo.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import condi.kr.ac.swu.condidemo.R;


/**
 * Created by 8304 on 2015-10-24.
 */
public class PromiseListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String, String>> data;


    public PromiseListAdapter(Context context, List<Properties> data) {
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
            convertView = inflater.inflate(R.layout.promise_list, null);

        TextView txt_sch_show_date = (TextView) convertView.findViewById(R.id.txt_sch_show_date);
        TextView txt_sch_show_time = (TextView) convertView.findViewById(R.id.txt_sch_show_time);
        TextView txt_sch_show_location = (TextView) convertView.findViewById(R.id.txt_sch_show_location);
        TextView txt_shc_show_contents = (TextView) convertView.findViewById(R.id.txt_shc_show_contents);

        Date date;
        String[] dates = new String[2];
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy.MM.dd (E)");
        SimpleDateFormat format2 = new SimpleDateFormat("a HH시 mm분");
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(data.get(position).get("pdate"));
            dates[0] = format1.format(date);
            dates[1] = format2.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        txt_sch_show_date.setText(dates[0]);
        txt_sch_show_time.setText(dates[1]);
        txt_sch_show_location.setText(data.get(position).get("location"));
        txt_shc_show_contents.setText(data.get(position).get("content"));

        return convertView;
    }

}
