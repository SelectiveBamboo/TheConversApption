package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getName(); //DEBUG

    private WebView webView;

    private Context context = this;

    private String url = "https://theconversation.com/fr";
    final static String REGEX_URL_NOT_ARTICLE_THECONV = "theconversation.com/((fr/)|(us/)|(ca/)|(global/)|(africa/)|(ca-fr/)|(id/)|(es/)|(nz/)|(uk/)|(au/))";
    Pattern patternArticleUrl = Pattern.compile(REGEX_URL_NOT_ARTICLE_THECONV);


    SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        boolean isNotifEnabled = sharedPrefs.getBoolean("switch_allow_notif", true);
        Log.d(TAG, "isNotifEnabled: " + isNotifEnabled);

        if (isNotifEnabled)
        {
            //scheduleServiceAlarm(this.getBaseContext());

            Intent scheduleNotifIntent = new Intent(getApplicationContext(), scheduleNotifService.class);
            scheduleNotifService.startService(context);
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 0);
        }
        else
        {
            loadWebViewAndURL(context);
        }
    }

    public void scheduleServiceAlarm(Context context)
    {
        String timeNotif = sharedPrefs.getString("set_time_notification", "19:00");
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

        if(cal_alarm.before(cal_now))  //if its in the past increment of a day
            { cal_alarm.add(Calendar.DATE,1); }

        Log.println(Log.ERROR, TAG,"alarmcalendar : " + cal_alarm.getTimeInMillis());
        Log.println(Log.ERROR, TAG,"nowCalendar : " + cal_now.getTimeInMillis());

        //Setting intent to class where notification will be handled
        Intent intent = new Intent(this, createNotifReceiver.class);

        //Setting pending intent to respond to broadcast sent by AlarmManager
        PendingIntent alarmIntent= PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //getting instance of AlarmManager service
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        //Inexact alarm everyday since device is booted up. This is a better choice and
        //scales well when device time settings/locale is changed
        //We're setting alarm to fire notification after 15 minutes, and every 15 minutes there on
        assert alarmManager != null;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                cal_alarm.getTimeInMillis(),
                2*AlarmManager.INTERVAL_HALF_DAY, alarmIntent);

        Log.println(Log.ERROR, TAG,"next alarm : " + alarmManager.getNextAlarmClock().getTriggerTime());
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
        Log.d(TAG, "openSettings");

        Intent intent = new Intent(this, SettingsActivity.class);

        super.startActivity(intent);
        super.finish();
    }

    public void askFeedback(MenuItem menuItem)
    {
        Log.d(TAG, "askFeedback");

        Intent intent = new Intent(this, askFeedbackActivity.class);

        super.startActivity(intent);
        super.finish();
    }

    protected void loadWebViewAndURL(final Context context)
    {
        webView = (WebView)findViewById(R.id.webviewMain);
        webView.setWebViewClient(new WebViewClient() {
                                     @Override
                                     public boolean shouldOverrideUrlLoading(WebView view, String urlNewString)
                                     {
                                         Matcher matcher = patternArticleUrl.matcher(urlNewString);
                                         if(!matcher.find())
                                         {
                                             Intent i = new Intent(context, ReadingArticleActivity.class);
                                             i.putExtra("articleUrl", urlNewString);
                                             context.startActivity(i);
                                             return true;
                                         }

                                         /*
                                         PROBLEM HEEEEEEEEERE
                                          */


                                         if (Uri.parse(urlNewString).getHost().equals("theconversation.com"))
                                         { return false; }
                                         else
                                         { return super.shouldOverrideUrlLoading(view, urlNewString); }
                                     }
                                 });
        
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setDisplayZoomControls(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//        {
//            webSettings.setForceDark(WebSettings.FORCE_DARK_ON);
//        }

        //setContentView(webView);
        webView.loadUrl(url);
    }

    public void onRefresh()
    {
        Log.d(TAG, "on refresh");
        webView.reload();
    }

    public void refresh(MenuItem item)
    {
        Log.d(TAG, "refresh from button");
        webView.reload();
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
