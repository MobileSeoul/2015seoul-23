package condi.kr.ac.swu.condidemo.data;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
/**
 * Created by Chang on 10/19/2015.
 */
public class Session {
    public static String ID;
    public static String NICKNAME;
    public static String PROFILE;
    public static String THUMBNAIL;
    public static String GROUPS;
    public static String COURSE;
    public static String HEIGHT;
    public static String LEVEL;
    public static String PHONE;
    public static String PASSWORD;

    public static ArrayList<String> regions = new ArrayList<String>();

    public Session() {
        regions.add("중구");
        regions.add("노원구");
        regions.add("성북구");

    }


    /*----------------------------------------------SharedPreferences----------------------------------------------
    * 유저세션의 정보를 SharedPreferences에 저장
    * */
    public static void savePreferences(Context context, Properties prop){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();

        String key = "";
        String value = "";
        Enumeration keys = prop.propertyNames();
        while(keys.hasMoreElements()) {
            key = keys.nextElement().toString();
            value = prop.getProperty(key);
            editor.putString(key, value);
        }

        System.out.println("============고객 정보============");

         keys = prop.propertyNames();
        while (keys.hasMoreElements()) {
            key = keys.nextElement().toString();
            value = prop.getProperty(key);

            System.out.println(key + " : " + value);
        }

        System.out.println("================================");

        editor.commit();

        setUserSession(context, pref);
    }

    /*
    * 셰어드프리퍼런스의 내용을 삭제함 : 로그아웃이나 갱신시 사용해야함
    * */
    public static void removeAllPreferences(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();

        setUserSession(context, pref);
    }

    /*
    * SharedPreferences의 내용을 Properties로 반환
    * */
    public static Properties getAllPreferences(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Properties prop = new Properties();

        prop.setProperty("id",          pref.getString("id", ""));
        prop.setProperty("groups",      pref.getString("nickname", ""));
        prop.setProperty("nickname",    pref.getString("profile",""));
        prop.setProperty("profile",     pref.getString("thumbnail",""));
        prop.setProperty("thumbnail",   pref.getString("groups", ""));
        prop.setProperty("course",      pref.getString("course", ""));
        prop.setProperty("level",       pref.getString("height", ""));
        prop.setProperty("height",      pref.getString("level", ""));
        prop.setProperty("phone",       pref.getString("phone", ""));
        prop.setProperty("password",    pref.getString("password", ""));




        return prop;

    }

    /*
    * SharedPreferences의 key값을 넘겨주어 String 형태로 반환
    * */
    public static String getPreferences(Context context, String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(key,"");
    }
    /*----------------------------------------------SharedPreferences----------------------------------------------*/


    /*------------------------------------------------UserSession---------------------------------------------------
    * 지정된 SharedPreferences에 저장된 정보를 UserSession에 저장
    * */
    public static void setUserSession(Context context, SharedPreferences pref) {

        ID          = pref.getString("id", "");
        NICKNAME    = pref.getString("nickname", "");
        PROFILE     = pref.getString("profile","");
        THUMBNAIL   = pref.getString("thumbnail","");
        GROUPS      = pref.getString("groups", "");
        COURSE      = pref.getString("course", "");
        HEIGHT      = pref.getString("height", "");
        LEVEL       = pref.getString("level", "");
        PHONE       = pref.getString("phone", "");
        PASSWORD    = pref.getString("password", "");
    }

    /*
    * 현재의 SharedPreferences에 저장된 정보를 UserSession에 저장
    * */
    public static void setUserSession(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        ID          = pref.getString("id", "");
        NICKNAME    = pref.getString("nickname", "");
        PROFILE     = pref.getString("profile","");
        THUMBNAIL   = pref.getString("thumbnail","");
        GROUPS      = pref.getString("groups", "");
        COURSE      = pref.getString("course", "");
        HEIGHT      = pref.getString("height", "");
        LEVEL       = pref.getString("level", "");
        PHONE       = pref.getString("phone", "");
        PASSWORD    = pref.getString("password", "");
    }

    /*
    * UserSession의 필드를 Properties형태로 반환
    * */
    public static Properties getProperties() {
        Properties prop = new Properties();
        prop.setProperty("id",          ID);
        prop.setProperty("groups",      GROUPS);
        prop.setProperty("nickname",    NICKNAME);
        prop.setProperty("profile",     PROFILE);
        prop.setProperty("thumbnail",   THUMBNAIL);
        prop.setProperty("course",      COURSE);
        prop.setProperty("level",       LEVEL);
        prop.setProperty("height",      HEIGHT);
        prop.setProperty("phone",       PHONE);
        prop.setProperty("password",    PASSWORD);

        return prop;
    }
    /*------------------------------------------------UserSession---------------------------------------------------*/
}
