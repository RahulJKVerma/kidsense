package edu.berkeley.mims.treasuretrail;

import android.app.Service;
import android.hardware.SensorEventListener;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Dheean on 4/25/15.
 */
public class AccelBgService extends Service implements SensorEventListener {

    private static final String DEBUG_TAG = "AccelerometerLoggerService";

    private SensorManager sensorManager = null;
    private Sensor sensor = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("Dheera-","Inside onStartCommand of AccelBgService");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, sensor,
                SensorManager.SENSOR_DELAY_FASTEST);


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.i("DHEERA","Inside onSensorChanged()..which is inside AccelBgService");

        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }

        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            MainActivity.todayssteps=MainActivity.todayssteps+value;

            System.out.println("Phone steps-"+MainActivity.todayssteps+" ");
          //  textView.setText("Phone steps-" + MainActivity.todayssteps);
        } else if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            MainActivity.todayssteps=MainActivity.todayssteps+value;

            Log.i("DHEERA-", "Inside onSensorChanged() ..totalsteps= "+MainActivity.totalsteps);

         //   textView.setText("Phone steps " + MainActivity.todayssteps+" ");
        }
        sensorManager.unregisterListener(this);
        stopSelf();
    }

    private class SensorEventLoggerTask extends
            AsyncTask<SensorEvent, Integer, Void> {
        @Override
        protected Void doInBackground(SensorEvent... events) {
            SensorEvent event = events[0];

            return null;
            // log the value
        }
    }

}
