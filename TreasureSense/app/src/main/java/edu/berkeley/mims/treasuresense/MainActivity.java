package edu.berkeley.mims.treasuresense;
import com.getpebble.android.kit.PebbleKit;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    /* Added by Dheera: Start */
    //private static final UUID KIDSENSE_APP_UUID = UUID.fromString("0A5399d9-5693-4F3E-B768-9C99B5F5DCEA");
    //private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    private final StringBuilder mDisplayText = new StringBuilder();

    private PebbleKit.PebbleDataLogReceiver mDataLogReceiver = null;

    /* Added by Dheera: End */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /* Method Added by Dheera */
    @Override
    protected void onPause() {
        super.onPause();
        if (mDataLogReceiver != null) {
            unregisterReceiver(mDataLogReceiver);
            mDataLogReceiver = null;
        }
    }

    /* Method Added by Dheera */
    @Override
    protected void onResume() {
        super.onResume();
        final Handler handler = new Handler();

        // To receive data logs, Android applications must register a "DataLogReceiver" to receive data.
        //
        // In this example, we're implementing a handler to receive unsigned integer data that was logged by a
        // corresponding watch-app. In the watch-app, three separate logs were created, one per animal. Each log was
        // tagged with a key indicating the animal to which the data corresponds. So, the tag will be used here to
        // look up the animal name when data is received.
        //
        // The data being received contains the seconds since the epoch (a timestamp) of when an ocean faring animal
        // was sighted. The "timestamp" indicates when the log was first created, and will not be used in this example.
        mDataLogReceiver = new PebbleKit.PebbleDataLogReceiver(OCEAN_SURVEY_APP_UUID) {
            @Override
            public void receiveData(Context context, UUID logUuid, UnsignedInteger timestamp, UnsignedInteger tag,
                                    UnsignedInteger secondsSinceEpoch) {
                mDisplayText.append("\n");
                mDisplayText.append(getUintAsTimestamp(secondsSinceEpoch));
                mDisplayText.append(": Made footsteps ");
                mDisplayText.append(AnimalName.fromInt(tag.intValue()).getName());

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateUi();
                    }
                });
            }
        };

        PebbleKit.registerDataLogReceiver(this, mDataLogReceiver);

        PebbleKit.requestDataLogsForApp(this, OCEAN_SURVEY_APP_UUID);
    }

/* Method Added by Dheera */
private void updateUi() {
    TextView textView = (TextView) findViewById(R.id.log_data_text_view);
    textView.setText(mDisplayText.toString());
}

    private String getUintAsTimestamp(UnsignedInteger uint) {
        return DATE_FORMAT.format(new Date(uint.longValue() * 1000L)).toString();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
}
