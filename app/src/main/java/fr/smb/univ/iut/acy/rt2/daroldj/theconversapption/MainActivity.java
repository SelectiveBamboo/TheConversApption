package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getName(); //DEBUG

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 0);
        }
        else
        {
            loadWebViewAndURL(this.getApplicationContext(), "https://theconversation.com/fr");
        }
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
