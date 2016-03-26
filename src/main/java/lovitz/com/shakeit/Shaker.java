package lovitz.com.shakeit;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class Shaker extends FragmentActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private Initialize inner;

    private float delta = 0;
    private float acceleration = 0;
    private float accelerationLast = 0;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        //Intent intent = new Intent(this, Initialize.class);
        //inner = new Initialize();
        //delta = acceleration = accelerationLast = 0;


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent ev){
        float x = ev.values[0];
        float y = ev.values[1];
        float z = ev.values[2];

        accelerationLast = acceleration;
        acceleration = Math.abs(x)+Math.abs(y)+Math.abs(z);
        delta = acceleration - accelerationLast;

        if(delta > 1){
            //Initialize init = new Initialize();


            InvalidX error = new InvalidX();
            error.show(getFragmentManager(), "x error");

            //inner.onResume();
        }

    }



    @Override
    public void onResume(){
        super.onResume();
    }

}
