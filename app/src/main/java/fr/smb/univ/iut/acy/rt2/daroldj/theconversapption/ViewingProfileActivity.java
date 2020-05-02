package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewingProfileActivity extends AppCompatActivity {
    private final static String TAG = ViewingProfileActivity.class.getName(); //DEBUG

    private WebView webView;

    private Context context = this;

    final static String REGEX_URL_NOT_ARTICLE_THECONV = "theconversation.com/((fr)|(us)|(ca)|(global)|(africa)|(ca-fr)|(id)|(es)|(nz)|(uk)|(au)/?)";
    final static String REGEX_URL_PROFILE = "theconversation.com/profiles/";
    Pattern patternArticleUrl = Pattern.compile(REGEX_URL_NOT_ARTICLE_THECONV);
    Pattern patternProfileUrl = Pattern.compile(REGEX_URL_PROFILE);

    private String profileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_viewing_profile);
        setToolbar();
        setFloatingAB();

        if (getIntent().getStringExtra("url") != null)
        {
            profileUrl = getIntent().getStringExtra("url");
        }
        else
        {
            Toast.makeText(context, R.string.noUrlData, Toast.LENGTH_LONG).show();

            Intent intent_MainActivity = new Intent(context, MainActivity.class);
            context.startActivity(intent_MainActivity);
        }

        loadWebViewAndURL(context);
    }

    private void setFloatingAB()
    {
        FloatingActionButton fab = findViewById(R.id.viewingProfileFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void setToolbar()
    {
        Toolbar toolbar = findViewById(R.id.viewingProfileToolbar);
        setSupportActionBar(toolbar);
        toolbar.collapseActionView();
        toolbar.setTitle( getString(R.string.viewingProfile_barTitle));

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    protected void loadWebViewAndURL(final Context context)
    {
        webView = (WebView)findViewById(R.id.webviewProfile);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String urlNewString)
            {
                Matcher matcherProfile = patternProfileUrl.matcher(urlNewString);
                Matcher matcherNotArticle = patternArticleUrl.matcher(urlNewString);

                if(matcherProfile.find())
                { //Whether it should be open as a profile page
                    return false;
                }

                if(!matcherNotArticle.find() && !urlNewString.equals("https://theconversation.com/"))
                {  //Whether it should be open as an article
                    Intent intent_ReadingArticle = new Intent(context, ReadingArticleActivity.class);
                    intent_ReadingArticle.putExtra("url", urlNewString);
                    context.startActivity(intent_ReadingArticle);

                    return true;
                }

                if (Objects.equals(Uri.parse(urlNewString).getHost(),"theconversation.com"))
                {   //Whether it should be open in the main View
                    Intent intent_MainActivity = new Intent(context, MainActivity.class);
                    intent_MainActivity.putExtra("url", urlNewString);
                    context.startActivity(intent_MainActivity);

                    return true;
                }
                else
                { return super.shouldOverrideUrlLoading(view, urlNewString); }
            }
        });

        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setSupportZoom(true);
        webSettings.setMinimumFontSize(20);

        //for future dark mode availability

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//        {
//            webSettings.setForceDark(WebSettings.FORCE_DARK_ON);
//        }


        webView.loadUrl(profileUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_reading_article, menu);
        return true;
    }

    public void askFeedback(MenuItem menuItem)
    {
        Intent intent = new Intent(this, askFeedbackActivity.class);

        super.startActivity(intent);
        super.finish();
    }

    public void openSettings(MenuItem menuItem)
    {
        Intent intent = new Intent(this, SettingsActivity.class);

        super.startActivity(intent);
        super.finish();
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

    public void shareLink(MenuItem item)
    {
        Log.d(TAG, "Sharing link");

        Intent sendIntent = new Intent();

        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.message_Sharing_Profile) +  webView.getUrl());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.hint_Chooser_SharingLink));
        startActivity(shareIntent);
    }

    public void refresh(MenuItem item)
    {
        webView.reload();
    }

}
