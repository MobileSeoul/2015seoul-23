package condi.kr.ac.swu.condidemo.data;


import android.util.Xml;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * Created by Administrator on 2015-09-10.
 */
public class NetworkAction {

    private static NetworkAction networkAction = new NetworkAction();
    private static final String SERVER_ADDRESS = "http://condi.swu.ac.kr:80/condi2/condi/";
    private static final String ns = null;

    public static NetworkAction getInstance() {
        return networkAction;
    }


    /*----------------------------------------- XML 파서 ---------------------------------------------*/
    public static List parse(String xml, String table) throws XmlPullParserException, IOException {
        InputStream in = null;
        try {
            XmlPullParser parser = Xml.newPullParser();
            in = new URL(SERVER_ADDRESS+xml).openStream();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readTable(parser, table);
        } finally {
            in.close();
        }
    }

    private static List readTable(XmlPullParser parser, String table) throws XmlPullParserException, IOException {
        List rows = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, table+"list");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals(table)) {
                rows.add(readRow(parser, table));
            } else {
                skip(parser);
            }
        }
        return rows;
    }

    private static Properties readRow(XmlPullParser parser, String table) throws XmlPullParserException, IOException {
        Properties prop = new Properties();
        parser.require(XmlPullParser.START_TAG, ns, table);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String field = parser.getName();
            prop.setProperty(field, readValue(parser, field));
        }
        return prop;
    }

    private static String readValue(XmlPullParser parser, String field) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, field);
        String value = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, field);
        return value;
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    /*--------------------------- 데이터 전송 메소드들 -----------------------------------------------*/

    /*
    * 실제 사용할 메소드
    * */
    public static String sendDataToServer(String url, Properties prop) {
        return setDataByPost(postData(url, prop));
    }

    public static String sendDataToServer(String url, Properties prop, String dml) {
        return setDataByPost(postData(url, prop, dml));
    }

    public static String sendDataToServer(String url, String dml) {
        return setDataByPost(postData(url, dml));
    }

    public static String sendDataToServer(String dml) {
        return setDataByPost(postData(dml));
    }

    public static String requestDataToServer(String url) {
        return setDataByPost(requestData(url));
    }


    /*
    * post방식으로 data 전송
    * */
    public static HttpPost postData(String url, Properties prop) {
        HttpPost request = new HttpPost(SERVER_ADDRESS+url);
        Vector<NameValuePair> nameValuePairs = new Vector<NameValuePair>();

        Enumeration keys = prop.propertyNames();
        while(keys.hasMoreElements()) {
            String key = keys.nextElement().toString();
            String value = prop.getProperty(key);
            nameValuePairs.add(new BasicNameValuePair(key, value));
        }

        request.setEntity(setEntity(nameValuePairs));
        return request;
    }

    public static HttpPost postData(String url, Properties prop, String dml) {
        HttpPost request = new HttpPost(SERVER_ADDRESS+url);
        Vector<NameValuePair> nameValuePairs = new Vector<NameValuePair>();

        Enumeration keys = prop.propertyNames();
        while(keys.hasMoreElements()) {
            String key = keys.nextElement().toString();
            String value = prop.getProperty(key);
            nameValuePairs.add(new BasicNameValuePair(key, value));
        }
        nameValuePairs.add(new BasicNameValuePair("dml", dml));

        request.setEntity(setEntity(nameValuePairs));
        return request;
    }

    public static HttpPost postData(String url, String dml) {
        HttpPost request = new HttpPost(SERVER_ADDRESS+url);
        Vector<NameValuePair> nameValuePairs = new Vector<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("dml", dml));

        request.setEntity(setEntity(nameValuePairs));
        return request;
    }

    public static HttpPost postData(String dml) {
        HttpPost request = new HttpPost(SERVER_ADDRESS+"query.php");
        Vector<NameValuePair> nameValuePairs = new Vector<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("dml", dml));

        request.setEntity(setEntity(nameValuePairs));
        return request;
    }

    /*
    * 특별한 결과값을 내주는 쿼리를 전송 시에 이용
    * */
    public static HttpPost requestData(String url) {
        HttpPost request = new HttpPost(SERVER_ADDRESS+url);
        return request;
    }

    /*
    * 데이터 전송에 이용 메소드
    * */
    public static HttpEntity setEntity(Vector<NameValuePair> nameValuePairs) {
        HttpEntity result = null;

        try {
            result = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result;
    }

    /*
    * 결과값을 리턴해주는 메소드
    * */
    public static String setDataByPost(HttpPost request) {
        String result = "";

        HttpClient client = new DefaultHttpClient();
        ResponseHandler<String> responseHandler = new BasicResponseHandler() ;

        try {
            result = client.execute(request, responseHandler);
            System.out.println("==> 결과 :  "+result);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("post방식으로 데이터 로드 실패"+ e.getMessage());
        }

        return result;
    }
}