package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getName(); //DEBUG

    private WebView webView;

    SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        scheduleServiceAlarm(this.getBaseContext());

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 0);
        }
        else
        {
            loadWebViewAndURL(this.getBaseContext(), "https://theconversation.com/fr");

//            try {
//                fetchXML("https://theconversation.com/fr/articles.atom", this.getApplicationContext());
//            }
//            catch (MalformedURLException e) { Log.e(TAG, e.getMessage()); }
        }
    }

    private void scheduleServiceAlarm(Context context) {
        String timeNotif = sharedPrefs.getString("set_time", "19:00");
       // Toast.makeText(context, "timeNotif : " + timeNotif, Toast.LENGTH_LONG).show();
        Log.println(Log.DEBUG, TAG,"timeNotif : " + timeNotif);

       String[] splittedTimeNotif = timeNotif.split(":");

        Calendar cal_now = Calendar.getInstance();
        int now_date = cal_now.get(Calendar.DATE);
        int now_month = cal_now.get(Calendar.MONTH);
        int now_year = cal_now.get(Calendar.YEAR);

        int alarm_hour = Integer.parseInt(splittedTimeNotif[0]);
        int alarm_minute = Integer.parseInt(splittedTimeNotif[1]);

        Calendar cal_alarm = Calendar.getInstance();
        cal_alarm.set(now_year, now_month, now_date, alarm_hour, alarm_minute);
//        cal_alarm.setTimeInMillis(System.currentTimeMillis());
//        cal_now.setTimeInMillis(System.currentTimeMillis());
//        // cal_now.setTimeInMillis(SystemClock.elapsedRealtime());
//        cal_alarm.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splittedTimeNotif[0]));//set the alarm time
//        cal_alarm.set(Calendar.MINUTE, Integer.parseInt(splittedTimeNotif[1]));
//        cal_alarm.set(Calendar.SECOND,0);
        if(cal_alarm.before(cal_now))  //if its in the past increment
        {
            cal_alarm.add(Calendar.DATE,1);
        }

        Log.println(Log.ERROR, TAG,"alarmcalendar : " + cal_alarm.getTimeInMillis());

        //Setting intent to class where notification will be handled
        Intent intent = new Intent(this, forNotifReceiver.class);

        //Setting pending intent to respond to broadcast sent by AlarmManager everyday at 8am
        PendingIntent alarmIntentElapsed = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //getting instance of AlarmManager service
        AlarmManager alarmManagerElapsed = (AlarmManager)getSystemService(ALARM_SERVICE);
        //Inexact alarm everyday since device is booted up. This is a better choice and
        //scales well when device time settings/locale is changed
        //We're setting alarm to fire notification after 15 minutes, and every 15 minutes there on
        assert alarmManagerElapsed != null;
        alarmManagerElapsed.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis()+5000,
                2*AlarmManager.INTERVAL_HALF_DAY, alarmIntentElapsed);
        Log.println(Log.ERROR, TAG,"alarm : " + alarmManagerElapsed.getNextAlarmClock().getTriggerTime());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(TAG, "onCreateOptionsMenu");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    public void openSettings(MenuItem menuItem)
    {
        Log.d(TAG, "modifyIp");

        Intent intent = new Intent(this, SettingsActivity.class);

        super.startActivity(intent);
        super.finish();
    }
//    protected void onResume()
//    {
//        Log.i(TAG, "onResume"); //DEBUG
//        super.onResume();
//
//        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
//                != PackageManager.PERMISSION_GRANTED)
//        {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 0);
//        }
//        else
//        {
//            loadWebViewAndURL(this.getApplicationContext(), "https://theconversation.com/fr");
//        }
//    }

    protected void loadWebViewAndURL(Context context, String url)
    {
        webView = new WebView(context);
        webView.setWebViewClient(new TheConvWebViewClient());

        setContentView(webView);

        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setDisplayZoomControls(true);
        //webSettings.setForceDark(WebSettings.FORCE_DARK_ON);


        setContentView(webView);
        webView.loadUrl(url);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack())
        {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onPause()    //DEBUG
    {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    protected void onStop() //DEBUG
    {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    protected void onRestart()  //DEBUG
    {
        Log.i(TAG, "onRestart");
        super.onRestart();
    }

    protected void onDestroy() //DEBUG
    {
        Log.i(TAG, "onDestroy");

        super.onDestroy();
    }
}
