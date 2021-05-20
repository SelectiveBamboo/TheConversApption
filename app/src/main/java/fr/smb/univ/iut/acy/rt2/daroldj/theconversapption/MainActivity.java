                                                                                                                                                                        package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.Manifest;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mikepenz.materialdrawer.Drawer;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getName(); //DEBUG

    private WebView webView;

    private Context context = this;

    Drawer result;

    private String url = "https://theconversation.com/";
    final static String REGEX_URL_NOT_ARTICLE_THECONV = "theconversation.com/((fr)|(us)|(ca)|(global)|(africa)|(ca-fr)|(id)|(es)|(nz)|(uk)|(au)/?)";
    final static String REGEX_URL_PROFILE = "theconversation.com/profiles/";
    Pattern patternArticleUrl = Pattern.compile(REGEX_URL_NOT_ARTICLE_THECONV);
    Pattern patternProfileUrl = Pattern.compile(REGEX_URL_PROFILE);

    SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        toolbar.setTitle( getString(R.string.toolbarTitle) );
        toolbar.collapseActionView();
        setSupportActionBar(toolbar);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        boolean isNotifEnabled = sharedPrefs.getBoolean("switch_allow_notif", true);
        Log.d(TAG, "isNotifEnabled: " + isNotifEnabled);

        if (isNotifEnabled)
        {
            scheduleNotifService.startService(context);
        }

        if (getIntent().getStringExtra("url") != null)
        {
            url = getIntent().getStringExtra("url");
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

        final SwipeRefreshLayout swipeToRefreshLay = findViewById(R.id.mainActivtiy_swiperefresh);
        swipeToRefreshLay.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        webView.reload();
                        swipeToRefreshLay.setRefreshing(false);
                    }
                }
        );

        result = DrawerCreator.getDrawer(this, toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        result.setSelection(DrawerCreator.MAIN_ACTIVITY_DRAWER_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.v(TAG, "onCreateOptionsMenu");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    public void openSettings(MenuItem menuItem)
    {
        Intent intent = new Intent(this, SettingsActivity.class);

        super.startActivity(intent);
        super.finish();
    }

    public void askFeedback(MenuItem menuItem)
    {
        Intent intent = new Intent(this, askFeedbackActivity.class);

        super.startActivity(intent);
        super.finish();
    }

    protected void loadWebViewAndURL(final Context context)
    {
        webView = (WebView)findViewById(R.id.webviewMain);
        webView.setWebViewClient(new WebViewClient() {

                                    @Override
                                    public void onLoadResource(WebView view, String url)
                                    {
                                        view.evaluateJavascript("document.getElementsByClassName('reader-signin')[0].style.display = 'none';", null);

                                        super.onLoadResource(view, url);
                                    }
                                     @Override
                                     public boolean shouldOverrideUrlLoading(WebView view, String urlNewString)
                                     {
                                         Matcher matcherProfile = patternProfileUrl.matcher(urlNewString);
                                         Matcher matcherNotArticle = patternArticleUrl.matcher(urlNewString);

                                         if(matcherProfile.find())
                                         { //Whether it should be open as a profile page
                                             Intent intent_ViewingProfile = new Intent(context, ViewingProfileActivity.class);
                                             intent_ViewingProfile.putExtra("url", urlNewString);
                                             context.startActivity(intent_ViewingProfile);

                                             return true;
                                         }

                                         if(!matcherNotArticle.find() && !urlNewString.equals("https://theconversation.com/"))
                                         {  //Actually used t determine whether it should open it as an article or not
                                             Intent intent_ReadingArticle = new Intent(context, ReadingArticleActivity.class);
                                             intent_ReadingArticle.putExtra("url", urlNewString);
                                             context.startActivity(intent_ReadingArticle);

                                             return true;
                                         }

                                         if (Objects.equals(Uri.parse(urlNewString).getHost(),"theconversation.com"))
                                         { return false; }
                                         else
                                         { return super.shouldOverrideUrlLoading(view, urlNewString); }

                                     }
                                 });
        
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);

        //for future dark mode availability

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//        {
//            webSettings.setForceDark(WebSettings.FORCE_DARK_ON);
//        }



        webView.loadUrl(url);
    }

    public void refresh(MenuItem item)
    {
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
}
