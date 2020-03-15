package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.app.ActivityCompat;

import com.jakewharton.threetenabp.AndroidThreeTen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getName(); //DEBUG

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        scheduleServiceAlarm( getApplicationContext() );

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 0);
        }
        else
        {
            loadWebViewAndURL(this.getApplicationContext(), "https://theconversation.com/fr");

//            try {
//                fetchXML("https://theconversation.com/fr/articles.atom", this.getApplicationContext());
//            }
//            catch (MalformedURLException e) { Log.e(TAG, e.getMessage()); }
        }
    }

    private void scheduleServiceAlarm(Context context)
    {
        //Setting intent to class where notification will be handled
        Intent intent = new Intent(context, RSSTheConvNotif.class);

        //Setting pending intent to respond to broadcast sent by AlarmManager everyday at 8am
        PendingIntent alarmIntentElapsed = PendingIntent.getBroadcast(context, 74940, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //getting instance of AlarmManager service
        AlarmManager alarmManagerElapsed = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        //Inexact alarm everyday since device is booted up. This is a better choice and
        //scales well when device time settings/locale is changed
        //We're setting alarm to fire notification after 15 minutes, and every 15 minutes there on
        assert alarmManagerElapsed != null;
        alarmManagerElapsed.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HOUR,
                2*AlarmManager.INTERVAL_HALF_DAY, alarmIntentElapsed);
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
