package edu.berkeley.mims.treasuretrail;

/**
 * Created by Dheean on 4/18/15.
 */
import android.content.BroadcastReceiver;
import android.app.NotificationManager;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;


public class ReminderAlarm extends BroadcastReceiver {
    private NotificationManager mNotificationManager;
    private Notification notification;

    @Override
    public void onReceive(Context context, Intent intent) {

        MainActivity.totalsteps=MainActivity.totalsteps+MainActivity.todayssteps;
        MainActivity.todayssteps = 0;
        Log.i("DHEERA-", "Inside onReceive() of ReminderAlarm...resetting todayssteps to "+MainActivity.todayssteps);

    }
}