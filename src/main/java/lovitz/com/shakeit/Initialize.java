package lovitz.com.shakeit;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;


public class Initialize extends FragmentActivity implements SensorEventListener { //implements ActionBar.TabListener {
    //Shaker Commands
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private Initialize inner;

    private float delta = 0;
    private float acceleration = 0;
    private float accelerationLast = 0;

    //Initialize Commands
    AppSectionsPagerAdapter pagerAdapter;
    ViewPager mViewPager;

    //User-set global for battery loss
    static double x = 2.5;

    //Initial battery level
    static float init;

    //Global battery intent
    static IntentFilter ifilter;
    static Intent batteryStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Intent intent = getIntent();
        super.onCreate(savedInstanceState);

        //Accelerometer Starter
     //   Shaker shaker = new Shaker();

        //Necessary?
        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = this.registerReceiver(null, ifilter);

        setContentView(R.layout.activity_initialize);


        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new AppSectionsPagerAdapter(getSupportFragmentManager()));

        //Shaker

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
/*
    @Override
    public void onResume(){
        //What?
    }
  */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {
        public AppSectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int i) {
            if (i == 0) {
                Fragment setsFrag = new Settings();
                Bundle args = new Bundle();
                //args.putInt(setsFrag.ARG_SECTION_NUMBER, i + 1);
                //setsFrag.setArguments(args);
                return setsFrag;
            } else {
                Fragment task = new Task();
                Bundle args = new Bundle();
                //args.putInt(task.ARG_SECTION_NUMBER, i + 1);
                //setsFrag.setArguments(args);
                return task;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Settings";
            }
            return "Task";
        }
    }

    public static class Settings extends Fragment {

        //@Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.settings, container, false);


            ((TextView) rootView.findViewById(R.id.currentBat)).setText(getBatteryPctString());
            ((TextView) rootView.findViewById(R.id.currentX)).setText(getThresholdString());
            /*
            View rootView = inflater.inflate(R.layout.settings, container, false);

            Bundle args = getArguments();
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                    name);
            */
            return rootView;
        }
    }


    public static class Task extends Fragment {
        //public static final String name = "Task";
        /*
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.task, container, false);
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                    name);
            ((TextView) rootView.findViewById(android.R.id.text2)).setText(
                    "banana");
            Button button = (Button) rootView.findViewById(android.R.id.button1);
            /*
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText(
                            "eek");
                }

            });
            */

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.task2, container, false);
            /*
            ScrollView rootView = new ScrollView(getActivity());
            TextView text = new TextView(getActivity());

            text.setText("hi");
            rootView.addView(text);
            */

            return rootView;
        }
        /*
        public void taskClick(View view){
            view.findViewById(view.getId().te
        }
        */


    }

    public void setX(View setsView) {
        EditText rawX = (EditText) findViewById(R.id.setNum);
        try {
            double newX = Double.parseDouble(rawX.getText().toString());
            if (newX < getBatteryPct()) {
                x = newX;
                TextView charge = ((TextView) findViewById(R.id.currentBat));
                charge.setText(getBatteryPctString());
                TextView threshold = ((TextView) findViewById(R.id.currentX));
                threshold.setText(getThresholdString());
            }
            else{
                InvalidX error = new InvalidX();
                error.show(getFragmentManager(), "x error");
            }
        }

        catch (NumberFormatException|NullPointerException y){
            InvalidX error = new InvalidX();
            error.show(getFragmentManager(), "x error");
        }
    }

    public double getX() {
        return x;
    }

    public static float getBatteryPct() {
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return (level * 100)/ (float)scale;
    }

    public static String getBatteryPctString(){
        float current = getBatteryPct();
        return ("The current battery level is " + Float.toString(current) + "%.");
    }
    public static String getThresholdString(){
        float min = getBatteryPct() - (float)x;
        return ("Wake functions will halt when battery drops to " + Float.toString(min) + "% if the task is started now.");
    }

    public static void startTask(View view) {
        //Set initial battery level
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        init = level / (float)scale;
    }

    public static float batteryElapsed(){
        return init-getBatteryPct();
    }


    //Shaker
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent ev) {
  /*
        float x = ev.values[0];
        float y = ev.values[1];
        float z = ev.values[2];

        accelerationLast = acceleration;
        acceleration = Math.abs(x) + Math.abs(y) + Math.abs(z);
        delta = acceleration - accelerationLast;

        if (delta > 4) {
            //Initialize init = new Initialize();


            InvalidX error = new InvalidX();
            error.show(getFragmentManager(), "x error");

            //inner.onResume();
        }
  */
    }
}