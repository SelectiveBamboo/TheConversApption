package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.JobIntentService;
import androidx.preference.PreferenceManager;

import java.util.Calendar;

public class scheduleNotifService extends JobIntentService {

    private final static String TAG = scheduleNotifService.class.getName(); //DEBUG

    SharedPreferences sharedPrefs;

    public static void startService(Context context)
    {
        enqueueWork(context, scheduleNotifService.class, 1, new Intent());
    }

    @Override
    protected void onHandleWork(Intent intent)
    {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        scheduleServiceAlarm(this);
    }

    private void scheduleServiceAlarm(Context context)
    {
        String timeNotif = sharedPrefs.getString("set_time_notification", "19:00");
        Log.println(Log.DEBUG, TAG,"timeNotif in service: " + timeNotif);

        String[] splittedTimeNotif = timeNotif.split(":");

        Calendar cal_now = Calendar.getInstance();
        int now_date = cal_now.get(Calendar.DATE);
        int now_month = cal_now.get(Calendar.MONTH);
        int now_year = cal_now.get(Calendar.YEAR);

        int alarm_hour = Integer.parseInt(splittedTimeNotif[0]);
        int alarm_minute = Integer.parseInt(splittedTimeNotif[1]);

        Calendar cal_alarm = Calendar.getInstance();
        cal_alarm.set(now_year, now_month, now_date, alarm_hour, alarm_minute);

        if(cal_alarm.before(cal_now))  //if its in the past increment of a day
            { cal_alarm.add(Calendar.DATE,1); }

        //Setting intent to class where notification will be handled
        Intent intent = new Intent(context, createNotifReceiver.class);

        //Setting pending intent to respond to broadcast sent by AlarmManager
        PendingIntent alarmIntent= PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //getting instance of AlarmManager service
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //Inexact alarm everyday since device is booted up. This is a better choice and
        //scales well when device time settings/locale is changed
        //We're setting alarm to fire notification after 15 minutes, and every 15 minutes there on
        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                cal_alarm.getTimeInMillis(),
                2*AlarmManager.INTERVAL_HALF_DAY, alarmIntent);

       // Toast.makeText(context, "Next notif at: " + cal_now.get(Calendar.HOUR) + ":" + cal_now.get(Calendar.MINUTE), Toast.LENGTH_LONG).show();
    }
}
