package edu.berkeley.mims.treasuretrail;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.BroadcastReceiver;
import android.os.Message;
import android.os.PowerManager.WakeLock;
import android.os.PowerManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.content.res.Configuration;
import android.view.MenuInflater;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import com.getpebble.android.kit.PebbleKit;
import java.util.Calendar;
import java.util.UUID;
import android.content.Intent;
import android.content.IntentFilter;
import edu.berkeley.mims.treasuretrail.pedometer.StepService;

import edu.berkeley.mims.treasuretrail.pedometer.PedometerSettings;
import edu.berkeley.mims.treasuretrail.pedometer.Settings;
import edu.berkeley.mims.treasuretrail.pedometer.Pedometer;
import edu.berkeley.mims.treasuretrail.pedometer.StepService;
import edu.berkeley.mims.treasuretrail.pedometer.Utils;

public class MainActivity extends ActionBarActivity implements OnClickListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    private FragmentNavigationDrawer dlDrawer;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    //START : ADDED BY DHEERA FOR DATALOGGING, PHONE ACCELEROMETER AND ANIMATION CHANGES
    private static final UUID KIDSENSE_APP_UUID = UUID.fromString("07d87811-510f-48f2-b723-6bcfc4db9a40");
    static int childstepcount = 0;
    static int totalsteps = 0;
    static int todayssteps = 0;
    private TextView textView;
    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;
    int notificationCount;
    private WakeLock mWakeLock = null;
    private PebbleKit.PebbleDataLogReceiver mDataLogReceiver = null;
    public static final int SCREEN_OFF_RECEIVER_DELAY = 500;
    public Button startbutton;
    //END : ADDED BY DHEERA FOR DATALOGGING, PHONE ACCELEROMETER AND ANIMATION CHANGES



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.todayssteps=0;

        startbutton = (Button)findViewById(R.id.startbutton);
        startbutton.setOnClickListener(this);
        // Set a Toolbar to replace the ActionBar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        dlDrawer = (FragmentNavigationDrawer)  findViewById(R.id.drawer_layout);;
        dlDrawer.setupDrawerConfiguration((ListView) findViewById(R.id.lvDrawer), toolbar,
                R.layout.drawer_nav_item, R.id.flContent);
        mTitle = getTitle();

        // Add nav items
        dlDrawer.addNavItem("Home", 1, "Home", FirstFragment.class);
        dlDrawer.addNavItem("Leaderboard", 2, "Leaderboard", SecondFragment.class);
        dlDrawer.addNavItem("Profile", 3,"Profile", ThirdFragment.class);
        dlDrawer.addNavItem("Goals", 4,"Activity", FourthFragment.class);
      //  dlDrawer.addNavItem("Pedometer", 5, "Pedometer", FifthFragment.class);

        // Select default
        if (savedInstanceState == null) {
            dlDrawer.selectDrawerItem(0);
        }


        //START : ADDED BY DHEERA FOR DATALOGGING, PHONE ACCELEROMETER AND ANIMATION CHANGES
        //FOR PHONE"S ACCELEROMETER PART

       /* Log.i("DHEERA-", "Inside onCreate()...todayssteps=" + todayssteps);
        textView = (TextView) findViewById(R.id.count);
        mSensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        PowerManager manager =
                (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Wakelock partial wake lock-");
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        //mStepCounterSensor = mSensorManager .getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        //The following code is for resetting stepcount at 12am everyday
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0 ); //0
        calendar.set(Calendar.MINUTE, 1);//1
        notificationCount = notificationCount + 1;
        AlarmManager mgr = (AlarmManager) getApplicationContext().
                getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(getApplicationContext(),
                ReminderAlarm.class);

        notificationIntent.putExtra("NotifyCount", notificationCount);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),
                notificationCount, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi); */

        //END : ADDED BY DHEERA FOR DATALOGGING, PHONE ACCELEROMETER AND ANIMATION CHANGES


        //Syncs data from Pebble Watch

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.startbutton:
                Intent mIntent = new Intent();
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mIntent.setClassName(getApplicationContext(), "edu.berkeley.mims.treasuretrail.pedometer.Pedometer");
                getApplicationContext().startActivity(mIntent);

                break;

        }
    }

    //onResume() method Added by Dheera for phone's accelerometer data and dataloggin from pebble
    @Override
    protected void onResume() {
        super.onResume();
        final Handler handler = new Handler();
        Log.i("DHEERA-","Inside onResume()");
        System.out.println("Inside onResume()");
        // To receive data logs, Android applications must register a "DataLogReceiver" to receive data.
        //
        // In this example, we're implementing a handler to receive unsigned integer data that was logged by a
        // corresponding watch-app. In the watch-app, three separate logs were created, one per animal. Each log was
        // tagged with a key indicating the animal to which the data corresponds. So, the tag will be used here to
        // look up the animal name when data is received.
        //
        // The data being received contains the seconds since the epoch (a timestamp) of when an ocean faring animal
        // was sighted. The "timestamp" indicates when the log was first created, and will not be used in this example.
        mDataLogReceiver = new PebbleKit.PebbleDataLogReceiver(KIDSENSE_APP_UUID) {
            @Override
            public void receiveData(android.content.Context context, java.util.UUID logUuid, java.lang.Long timestamp, java.lang.Long tag, java.lang.Long data) {
                Log.i("DHEERA", "Inside receiveData()");
                // mDisplayText.append("\n");

                //mDisplayText.append("Footstep ");
                //mDisplayText.append(data);

                childstepcount=data.intValue();
                Log.i("DHEERA","step count="+data.intValue());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateUi();
                    }
                });
            }
        };

        PebbleKit.registerDataLogReceiver(this, mDataLogReceiver);
        PebbleKit.requestDataLogsForApp(this, KIDSENSE_APP_UUID);
        //FOR PHONE"S ACCELEROMETER PART
        //mSensorManager.registerListener(this, mStepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST);


       //COMMENTED AFTER ADDING NEW EXTENDS SENSOR CLASS AccelBgService mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    //Added by Dheera for phone's accelerometer part and datalogging from pebble
    private void updateUi() {
        System.out.print("Inside updateUi()");
        Toast.makeText(MainActivity.this, "TreasureTrail - Data sync in progress.", Toast.LENGTH_LONG).show();

        TextView textView = (TextView) findViewById(R.id.log_data_text_view);
        //textView.setTextColor(Color.GREEN);
        //textView.setText(mDisplayText.toString());
        textView.setText("Pebble steps- "+Integer.toString(childstepcount));
    }

    //Added by Dheera for phone's accelerometer part and datalogging from pebble
       /*
     * Register this as a sensor event listener.
     */
    /*
    private void registerListener() {
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }
*/
    /*
     * Un-register this as a sensor event listener.
     */
    /*
    private void unregisterListener() {
        mSensorManager.unregisterListener(this);
    }
*/
    /*
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Dheera-", "onReceive("+intent+")");

            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                return;
            }

            Runnable runnable = new Runnable() {
                public void run() {
                    Log.i("Dheera -", "Runnable executing.");
                    unregisterListener();
                    registerListener();
                }
            };

            new Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
        }
    };
    */

    /*
    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        unregisterListener();
        mWakeLock.release();
       // stopForeground(true);
    }
*/
    /*

    //ADDED FOR PHONE"S ACCELEROMETER PART
    public void onSensorChanged(SensorEvent event) {
        Log.i("DHEERA","Inside onSensorChanged()");

        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }

        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            todayssteps=todayssteps+value;

            System.out.println("Phone steps-"+todayssteps+" ");
            textView.setText("Phone steps-" + todayssteps);
        } else if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            todayssteps=todayssteps+value;

            Log.i("DHEERA-", "Inside onSensorChanged() ..totalsteps= "+totalsteps);

            textView.setText("Phone steps " + todayssteps+" ");
        }
    }*/
/*
    //Added by Dheera for phone's accelerometer part and datalogging from pebble
    //ADDED FOR PHONE"S ACCELEROMETER PART
    protected void onStop() {
        Log.i("DHEERA", "Inside onStop()");
        super.onStop();
        //mSensorManager.unregisterListener(this, mStepCounterSensor);
        mSensorManager.unregisterListener(this, mStepDetectorSensor);
    } */

    //Added by Dheera for phone's accelerometer part and datalogging from pebble
    //Need to override this method for the implementing SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }
    //Added by Dheera for phone's accelerometer part and datalogging from pebble
    @Override
    protected void onPause() {
        super.onPause();
        if (mDataLogReceiver != null) {
            unregisterReceiver(mDataLogReceiver);
            mDataLogReceiver = null;
        }

        Log.i("DHEERA","Inside onPause()");

    }

    /* COMMENTED TO REMOVE ERROR */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content
        if (dlDrawer.isDrawerOpen()) {
            // Uncomment to hide menu items
            // menu.findItem(R.id.mi_test).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Uncomment to inflate menu items to Action Bar
        // inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (dlDrawer.getDrawerToggle().onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        dlDrawer.getDrawerToggle().syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        dlDrawer.getDrawerToggle().onConfigurationChanged(newConfig);
    }


/*
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, FirstFragment.newInstance(position + 1))
                .commit();
    }*/

    public void onSectionAttached(int number) {
        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = new PlusOneFragment();


        switch (number) {
            case 1:
               // fragment = new PlusOneFragment();
               // fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                mTitle = getString(R.string.title_section0);
                break;
            case 2:
                mTitle = getString(R.string.title_section1);
                break;
            case 3:
                mTitle = getString(R.string.title_section2);
                break;
            case 4:
                mTitle = getString(R.string.title_section3);
                break;

        }

    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class FirstFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static FirstFragment newInstance(int sectionNumber) {
            FirstFragment fragment = new FirstFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public FirstFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.leaderboardfragment, container, false);

            return rootView;
        }

//        @Override
//        public void onAttach(Activity activity) {
//            super.onAttach(activity);
//            ((MainActivity) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
//        }
    }

    public static class SecondFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static SecondFragment newInstance(int sectionNumber) {
            SecondFragment fragment = new SecondFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public SecondFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.leaderboardlayout, container, false);
            Button dailyBtn = (Button)rootView.findViewById(R.id.dailyBtn);
            dailyBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v)
                {
                    Toast.makeText(getActivity(), "Daily Button Clicked", Toast.LENGTH_LONG).show();
                }
            });

            //Weekly button
            Button weeklyBtn = (Button)rootView.findViewById(R.id.weeklyBtn);
            weeklyBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v)
                {
                    Toast.makeText(getActivity(), "Weekly Button Clicked", Toast.LENGTH_LONG).show();
                }
            });


            return rootView;

        }

//        @Override
//        public void onAttach(Activity activity) {
//            super.onAttach(activity);
//            ((MainActivity) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
//        }
    }


    public static class ThirdFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ThirdFragment newInstance(int sectionNumber) {
            ThirdFragment fragment = new ThirdFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ThirdFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            View rootView = inflater.inflate(R.layout.profilelayout, container, false);
            return rootView;
        }

//        @Override
//        public void onAttach(Activity activity) {
//            super.onAttach(activity);
//            ((MainActivity) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
//        }
    }



    public static class FourthFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static FourthFragment newInstance(int sectionNumber) {
            FourthFragment fragment = new FourthFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public FourthFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            View rootView = inflater.inflate(R.layout.activityfragment, container, false);
            return rootView;
        }

//        @Override
//        public void onAttach(Activity activity) {
//            super.onAttach(activity);
//            ((MainActivity) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
//        }
    }


    public static class FifthFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String TAG = "Pedometer";
        private SharedPreferences mSettings;
        private PedometerSettings mPedometerSettings;
        private Utils mUtils;

        private TextView mStepValueView;
        private TextView mPaceValueView;
        private TextView mDistanceValueView;
        private TextView mSpeedValueView;
        private TextView mCaloriesValueView;
        TextView mDesiredPaceView;
        private int mStepValue;
        private int mPaceValue;
        private float mDistanceValue;
        private float mSpeedValue;
        private int mCaloriesValue;
        private float mDesiredPaceOrSpeed;
        private int mMaintain;
        private boolean mIsMetric;
        private float mMaintainInc;
        private boolean mQuitting = false; // Set when user selected Quit from menu, can be used by onPause, onStop, onDestroy
        View rootView;
        /**
         * True, when service is running.
         */
        private boolean mIsRunning;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static FifthFragment newInstance(int sectionNumber) {
            FifthFragment fragment = new FifthFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public FifthFragment() {
        }

        @Override
        public void onStart() {
            Log.i("Dheera-", "onStart of FifthFragment");
            super.onStart();
        }



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mStepValue = 0;
            mPaceValue = 0;
            mUtils = Utils.getInstance();
            Log.v("Dheera-", "Initializing fifth fragment for pedometer...");
            rootView = inflater.inflate(R.layout.pedometerlayout, container, false);


            return rootView;

        }


        @Override
        public void onResume() {
            Log.i("Dheera-", " onResume of FifthFragment");
            super.onResume();

            mSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());
            mPedometerSettings = new PedometerSettings(mSettings);

            mUtils.setSpeak(mSettings.getBoolean("speak", false));

            // Read from preferences if the service was running on the last onPause
            mIsRunning = mPedometerSettings.isServiceRunning();

            // Start the service if this is considered to be an application start (last onPause was long ago)
            if (!mIsRunning && mPedometerSettings.isNewStart()) {
                startStepService();
                bindStepService();
            }
            else if (mIsRunning) {
                bindStepService();
            }

            mPedometerSettings.clearServiceRunning();

            mStepValueView     = (TextView) rootView.findViewById(R.id.step_value);
            mPaceValueView     = (TextView) rootView.findViewById(R.id.pace_value);
            mDistanceValueView = (TextView) rootView.findViewById(R.id.distance_value);
            mSpeedValueView    = (TextView) rootView.findViewById(R.id.speed_value);
            mCaloriesValueView = (TextView) rootView.findViewById(R.id.calories_value);
            mDesiredPaceView   = (TextView) rootView.findViewById(R.id.desired_pace_value);

            mIsMetric = mPedometerSettings.isMetric();
            ((TextView) rootView.findViewById(R.id.distance_units)).setText(getString(
                    mIsMetric
                            ? R.string.kilometers
                            : R.string.miles
            ));
            ((TextView) rootView.findViewById(R.id.speed_units)).setText(getString(
                    mIsMetric
                            ? R.string.kilometers_per_hour
                            : R.string.miles_per_hour
            ));

            mMaintain = mPedometerSettings.getMaintainOption();
            ((LinearLayout) rootView.findViewById(R.id.desired_pace_control)).setVisibility(
                    mMaintain != PedometerSettings.M_NONE
                            ? View.VISIBLE
                            : View.GONE
            );
            if (mMaintain == PedometerSettings.M_PACE) {
                mMaintainInc = 5f;
                mDesiredPaceOrSpeed = (float)mPedometerSettings.getDesiredPace();
            }
            else
            if (mMaintain == PedometerSettings.M_SPEED) {
                mDesiredPaceOrSpeed = mPedometerSettings.getDesiredSpeed();
                mMaintainInc = 0.1f;
            }
            Button button1 = (Button) rootView.findViewById(R.id.button_desired_pace_lower);
            button1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mDesiredPaceOrSpeed -= mMaintainInc;
                    mDesiredPaceOrSpeed = Math.round(mDesiredPaceOrSpeed * 10) / 10f;
                    displayDesiredPaceOrSpeed();
                    setDesiredPaceOrSpeed(mDesiredPaceOrSpeed);
                }
            });
            Button button2 = (Button) rootView.findViewById(R.id.button_desired_pace_raise);
            button2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mDesiredPaceOrSpeed += mMaintainInc;
                    mDesiredPaceOrSpeed = Math.round(mDesiredPaceOrSpeed * 10) / 10f;
                    displayDesiredPaceOrSpeed();
                    setDesiredPaceOrSpeed(mDesiredPaceOrSpeed);
                }
            });
            if (mMaintain != PedometerSettings.M_NONE) {
                ((TextView) rootView.findViewById(R.id.desired_pace_label)).setText(
                        mMaintain == PedometerSettings.M_PACE
                                ? R.string.desired_pace
                                : R.string.desired_speed
                );
            }


            displayDesiredPaceOrSpeed();
        }

        public void displayDesiredPaceOrSpeed() {
            if (mMaintain == PedometerSettings.M_PACE) {
                mDesiredPaceView.setText("" + (int)mDesiredPaceOrSpeed);
            }
            else {
                mDesiredPaceView.setText("" + mDesiredPaceOrSpeed);
            }
        }
        @Override
        public void onPause() {
            Log.i(TAG, "[ACTIVITY] onPause");
            if (mIsRunning) {
                unbindStepService();
            }
            if (mQuitting) {
                mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
            }
            else {
                mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);
            }

            super.onPause();
            savePaceSetting();
        }

        @Override
        public void onStop() {
            Log.i(TAG, "[ACTIVITY] onStop");
            super.onStop();
        }

        public void onDestroy() {
            Log.i(TAG, "[ACTIVITY] onDestroy");
            super.onDestroy();
        }

        public void onRestart() {
            Log.i(TAG, "[ACTIVITY] onRestart");
            super.onDestroy();
        }

        public void setDesiredPaceOrSpeed(float desiredPaceOrSpeed) {
            if (mService != null) {
                if (mMaintain == PedometerSettings.M_PACE) {
                    mService.setDesiredPace((int)desiredPaceOrSpeed);
                }
                else
                if (mMaintain == PedometerSettings.M_SPEED) {
                    mService.setDesiredSpeed(desiredPaceOrSpeed);
                }
            }
        }

        public void savePaceSetting() {
            mPedometerSettings.savePaceOrSpeedSetting(mMaintain, mDesiredPaceOrSpeed);
        }
        private StepService mService;

        private ServiceConnection mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                mService = ((StepService.StepBinder)service).getService();

                mService.registerCallback(mCallback);
                mService.reloadSettings();

            }

            public void onServiceDisconnected(ComponentName className) {
                mService = null;
            }
        };


        private void startStepService() {
            if (! mIsRunning) {
                Log.i(TAG, "[SERVICE] Start");
                mIsRunning = true;
                getActivity().startService(new Intent(getActivity(),
                        StepService.class));
            }
        }

        private void bindStepService() {
            Log.i(TAG, "[SERVICE] Bind");
            getActivity().bindService(new Intent(getActivity(),
                    StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
        }

        private void unbindStepService() {
            Log.i(TAG, "[SERVICE] Unbind");
            getActivity().unbindService(mConnection);
        }

        private void stopStepService() {
            Log.i(TAG, "[SERVICE] Stop");
            if (mService != null) {
                Log.i(TAG, "[SERVICE] stopService");
                getActivity().stopService(new Intent(getActivity(),
                        StepService.class));
            }
            mIsRunning = false;
        }

        private void resetValues(boolean updateDisplay) {
            if (mService != null && mIsRunning) {
                mService.resetValues();
            }
            else {
                mStepValueView.setText("0");
                mPaceValueView.setText("0");
                mDistanceValueView.setText("0");
                mSpeedValueView.setText("0");
                mCaloriesValueView.setText("0");
                SharedPreferences state = getActivity().getSharedPreferences("state", 0);
                SharedPreferences.Editor stateEditor = state.edit();
                if (updateDisplay) {
                    stateEditor.putInt("steps", 0);
                    stateEditor.putInt("pace", 0);
                    stateEditor.putFloat("distance", 0);
                    stateEditor.putFloat("speed", 0);
                    stateEditor.putFloat("calories", 0);
                    stateEditor.commit();
                }
            }
        }

        private static final int MENU_SETTINGS = 8;
        private static final int MENU_QUIT     = 9;

        private static final int MENU_PAUSE = 1;
        private static final int MENU_RESUME = 2;
        private static final int MENU_RESET = 3;

        /* Creates the menu items */
        public void onPrepareOptionsMenu(Menu menu) {
            menu.clear();
            if (mIsRunning) {
                menu.add(0, MENU_PAUSE, 0, R.string.pause)
                        .setIcon(android.R.drawable.ic_media_pause)
                        .setShortcut('1', 'p');
            }
            else {
                menu.add(0, MENU_RESUME, 0, R.string.resume)
                        .setIcon(android.R.drawable.ic_media_play)
                        .setShortcut('1', 'p');
            }
            menu.add(0, MENU_RESET, 0, R.string.reset)
                    .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                    .setShortcut('2', 'r');
            menu.add(0, MENU_SETTINGS, 0, R.string.settings)
                    .setIcon(android.R.drawable.ic_menu_preferences)
                    .setShortcut('8', 's')
                    .setIntent(new Intent(getActivity(), Settings.class));
            menu.add(0, MENU_QUIT, 0, R.string.quit)
                    .setIcon(android.R.drawable.ic_lock_power_off)
                    .setShortcut('9', 'q');

        }

        /* Handles item selections */
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case MENU_PAUSE:
                    unbindStepService();
                    stopStepService();
                    return true;
                case MENU_RESUME:
                    startStepService();
                    bindStepService();
                    return true;
                case MENU_RESET:
                    resetValues(true);
                    return true;
                case MENU_QUIT:
                    resetValues(false);
                    unbindStepService();
                    stopStepService();
                    mQuitting = true;
                    getActivity().finish();
                    return true;
            }
            return false;
        }

        // TODO: unite all into 1 type of message
        private StepService.ICallback mCallback = new StepService.ICallback() {
            public void stepsChanged(int value) {
                mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
            }
            public void paceChanged(int value) {
                mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
            }
            public void distanceChanged(float value) {
                mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), 0));
            }
            public void speedChanged(float value) {
                mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int)(value*1000), 0));
            }
            public void caloriesChanged(float value) {
                mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int)(value), 0));
            }
        };

        private static final int STEPS_MSG = 1;
        private static final int PACE_MSG = 2;
        private static final int DISTANCE_MSG = 3;
        private static final int SPEED_MSG = 4;
        private static final int CALORIES_MSG = 5;

        private Handler mHandler = new Handler() {
            @Override public void handleMessage(Message msg) {
                switch (msg.what) {
                    case STEPS_MSG:
                        mStepValue = (int)msg.arg1;
                        mStepValueView.setText("" + mStepValue);
                        break;
                    case PACE_MSG:
                        mPaceValue = msg.arg1;
                        if (mPaceValue <= 0) {
                            mPaceValueView.setText("0");
                        }
                        else {
                            mPaceValueView.setText("" + (int)mPaceValue);
                        }
                        break;
                    case DISTANCE_MSG:
                        mDistanceValue = ((int)msg.arg1)/1000f;
                        if (mDistanceValue <= 0) {
                            mDistanceValueView.setText("0");
                        }
                        else {
                            mDistanceValueView.setText(
                                    ("" + (mDistanceValue + 0.000001f)).substring(0, 5)
                            );
                        }
                        break;
                    case SPEED_MSG:
                        mSpeedValue = ((int)msg.arg1)/1000f;
                        if (mSpeedValue <= 0) {
                            mSpeedValueView.setText("0");
                        }
                        else {
                            mSpeedValueView.setText(
                                    ("" + (mSpeedValue + 0.000001f)).substring(0, 4)
                            );
                        }
                        break;
                    case CALORIES_MSG:
                        mCaloriesValue = msg.arg1;
                        if (mCaloriesValue <= 0) {
                            mCaloriesValueView.setText("0");
                        }
                        else {
                            mCaloriesValueView.setText("" + (int)mCaloriesValue);
                        }
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }

        };




    }

}
