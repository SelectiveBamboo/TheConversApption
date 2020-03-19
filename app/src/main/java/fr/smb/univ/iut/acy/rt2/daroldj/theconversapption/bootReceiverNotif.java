package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class bootReceiverNotif extends BroadcastReceiver {

    private final static String TAG = bootReceiverNotif.class.getName(); //DEBUG
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, RSSTheConvNotif.class);
            alarmIntent = PendingIntent.getBroadcast(context, 74940, i, 0);
            // With setInexactRepeating(), you have to use one of the AlarmManager interval
            // constants--in this case, AlarmManager.INTERVAL_DAY.
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 60 * 1000,
                    AlarmManager.INTERVAL_DAY, alarmIntent);
        }
    }
}