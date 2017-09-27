package condi.kr.ac.swu.condidemo.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import condi.kr.ac.swu.condidemo.data.NetworkAction;
import condi.kr.ac.swu.condidemo.data.Session;

public class AccSensor extends Service implements SensorEventListener {

    private SensorManager sm;
    private Sensor accSensor;

    public int MY_WALK_COUNT = 0;
    private Thread th;
    private String user = Session.ID;



    @Override
    public void onCreate() {
        super.onCreate();

        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        accSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        th = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String dml = "update walk set currentwalk = " + MY_WALK_COUNT + " where user='" + user + "' and today=date_add(now(), interval -9 hour)";
                    NetworkAction.sendDataToServer(dml);

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(accSensor != null)
            sm.unregisterListener(this);

        if(th.isAlive())
            th.stop();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(accSensor != null)
            sm.registerListener(this, accSensor,SensorManager.SENSOR_DELAY_GAME);

        if(th != null)
            th.start();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    if ((event.values[0] < -10) || (event.values[0] > 10)
                            || (event.values[1] < -10) || (event.values[1] > 10)
                            || (event.values[2] < -10) || (event.values[2] > 10)) {

                        ++MY_WALK_COUNT;
                        Intent intentResponse = new Intent("condi.kr.ac.swu.condiproject.step");
                        intentResponse.putExtra("walk", Integer.toString(MY_WALK_COUNT));
                        sendBroadcast(intentResponse);
                    }

                    break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }




}
