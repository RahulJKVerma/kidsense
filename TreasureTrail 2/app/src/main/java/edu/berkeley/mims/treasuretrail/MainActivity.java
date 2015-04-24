package edu.berkeley.mims.treasuretrail;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
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


public class MainActivity extends ActionBarActivity implements SensorEventListener {

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
    private PebbleKit.PebbleDataLogReceiver mDataLogReceiver = null;
    //END : ADDED BY DHEERA FOR DATALOGGING, PHONE ACCELEROMETER AND ANIMATION CHANGES



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.todayssteps=0;

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
        // Select default
        if (savedInstanceState == null) {
            dlDrawer.selectDrawerItem(0);
        }


        //START : ADDED BY DHEERA FOR DATALOGGING, PHONE ACCELEROMETER AND ANIMATION CHANGES
        //FOR PHONE"S ACCELEROMETER PART
        Log.i("DHEERA-", "Inside onCreate()...todayssteps=" + todayssteps);
        textView = (TextView) findViewById(R.id.count);
        mSensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
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
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);

        //END : ADDED BY DHEERA FOR DATALOGGING, PHONE ACCELEROMETER AND ANIMATION CHANGES


        //Syncs data from Pebble Watch

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
        mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    //Added by Dheera for phone's accelerometer part and datalogging from pebble
    private void updateUi() {
        System.out.print("Inside updateUi()");
        Toast.makeText(MainActivity.this, "Syncing data from your Pebble watch.", Toast.LENGTH_LONG).show();

        TextView textView = (TextView) findViewById(R.id.log_data_text_view);
        //textView.setTextColor(Color.GREEN);
        //textView.setText(mDisplayText.toString());
        textView.setText("Pebble steps- "+Integer.toString(childstepcount));
    }

    //Added by Dheera for phone's accelerometer part and datalogging from pebble

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
    }

    //Added by Dheera for phone's accelerometer part and datalogging from pebble
    //ADDED FOR PHONE"S ACCELEROMETER PART
    protected void onStop() {
        Log.i("DHEERA", "Inside onStop()");
        super.onStop();
        //mSensorManager.unregisterListener(this, mStepCounterSensor);
        mSensorManager.unregisterListener(this, mStepDetectorSensor);
    }

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

}
