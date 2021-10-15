package org.unimelb.BirdMigration;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//from youtuber Richvale Consulting
public class Gyroscope {
    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorEventListener;

    public interface Listener
    {
        void onRotation(float rx,float ry,float rz);
    }

    private Listener listener;
    public void setListener(Listener l)
    {
        listener =l;
    }

    Gyroscope(Context context)
    {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor =sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (listener != null)
                {
                    listener.onRotation(sensorEvent.values[0],sensorEvent.values[1],sensorEvent.values[2]);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    //register and unregister method
    public void register()
    {
        sensorManager.registerListener(sensorEventListener,sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister()
    {
        sensorManager.unregisterListener(sensorEventListener);
    }
}
