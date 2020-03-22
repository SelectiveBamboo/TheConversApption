package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

public class createNotifReceiver extends BroadcastReceiver {

    String TAG = createNotifReceiver.class.getName();

    SharedPreferences sharedPrefs;

    public void onReceive(Context context, Intent intent)
    {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        boolean isNotifEnabed = sharedPrefs.getBoolean("switch_allow_notif", true);
        Log.v(TAG, "isNotifEnabled: " + isNotifEnabed);

        if (isNotifEnabed)
        {
            Intent intent1 = new Intent(context, RSSTheConvNotif.class);
            Log.e(this.getClass().getName(), "forNotifreceiver made the intent");
            RSSTheConvNotif.startService(context);
        }
        else
        {
            //cancel the next scheduled notifications
            Intent intentToCancel = new Intent(context, createNotifReceiver.class);
            PendingIntent pendingIntentToCancel= PendingIntent.getBroadcast(context, 0, intentToCancel, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            am.cancel(pendingIntentToCancel);
        }


    }
}
