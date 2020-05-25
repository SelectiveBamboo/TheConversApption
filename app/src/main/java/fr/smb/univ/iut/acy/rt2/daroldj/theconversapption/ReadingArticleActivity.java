package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadingArticleActivity extends AppCompatActivity {

    private String articleUrl;

    private WebView webView;

    private ProgressBar loadProgress;

    private Context context = this;

    private String TAG = this.getClass().getName();

    String articleTitle;

    private String data;

    Boolean shouldShowPB = true;

    final static String REGEX_URL_NOT_ARTICLE_THECONV = "theconversation.com/((fr)|(us)|(ca)|(global)|(africa)|(ca-fr)|(id)|(es)|(nz)|(uk)|(au)/?)";
    final static String REGEX_URL_PROFILE = "theconversation.com/profiles/";
    Pattern patternNotArticleUrlInTheConv = Pattern.compile(REGEX_URL_NOT_ARTICLE_THECONV);
    Pattern patternProfileUrl = Pattern.compile(REGEX_URL_PROFILE);


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_article);

        setBar();

        data = getIntent().getDataString();
        if (data != null && data.length() > 11)
        {
            articleUrl = data;
        }
        else
        {
            articleUrl = getIntent().getStringExtra("url");
        }

        setFloatingAB();

        loadProgress = (ProgressBar)findViewById(R.id.progressBar);

        loadWebViewAndURL();
    }

    private void setFloatingAB()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.downloadArticle_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                final String nameOfArchivedFile = webView.getUrl().substring(28);

                final File archiveFile = new File(context.getFilesDir(), nameOfArchivedFile);
                webView.saveWebArchive(archiveFile.getAbsolutePath());

                Snackbar.make(view, R.string.snackbar_Text_ReadingArticle, Snackbar.LENGTH_LONG)
                        .setAction("Open file", new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                webView.loadUrl(
                                        String.valueOf(archiveFile.toURI())
                                );
                            }
                        })
                        .show();
            }
        });
    }

    private void setBar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarReadingArticle);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

       // ActionBar actionBar = getSupportActionBar();
       // actionBar.setDisplayShowCustomEnabled(true);
        //actionBar.setCustomView(R.layout.appbar_title_layout);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            { finish(); }
        });
    }

    protected void loadWebViewAndURL()
    {
        webView = (WebView)findViewById(R.id.webviewArticle);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                if (shouldShowPB)
                { view.setVisibility(View.INVISIBLE); }

                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onLoadResource(WebView view, String url)
            {
                view.evaluateJavascript("var el = document.querySelector('[itemprop=articleBody]'); " +
                        "var node, nodes = []; " +
                        "do { var parent = el.parentNode; " +
                        "for (var i=0, iLen=parent.childNodes.length; i<iLen; i++) { node = parent.childNodes[i]; " +
                        "if (node.nodeType == 1 && node != el) { nodes.push(node); } } " +
                        "el = parent; } while (el.tagName.toLowerCase() != 'body'); " +
                        "nodes.forEach(function(node){ node.style.display = 'none'; });", null);

                super.onLoadResource(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                if (shouldShowPB)
                {
                    shouldShowPB = false;
                    loadProgress.setVisibility(View.GONE);

                    view.setVisibility(View.VISIBLE);
                   // ReadingArticleActivity.this.getSupportActionBar().setTitle(view.getTitle());
                    ReadingArticleActivity.this.setTitle(view.getTitle());
                    //((TextView) findViewById(R.id.appbar_title)).setText(view.getTitle());
                }

                super.onPageFinished(view, url);
            }

            public boolean shouldOverrideUrlLoading(WebView view, String urlNewString)
            {
                Matcher matcherProfile = patternProfileUrl.matcher(urlNewString);
                Matcher matcherNotArticle = patternNotArticleUrlInTheConv.matcher(urlNewString);

                boolean isTheConversation = Objects.equals(Uri.parse(urlNewString).getHost(),"theconversation.com");

                if(matcherProfile.find())
                { //Whether it should be open as a profile page
                    Intent intent_ViewingProfile = new Intent(context, ViewingProfileActivity.class);
                    intent_ViewingProfile.putExtra("url", urlNewString);
                    context.startActivity(intent_ViewingProfile);

                    return true;
                }

                if(!matcherNotArticle.find() && !urlNewString.equals("https://theconversation.com/") && isTheConversation)
                {  //Whether it should be open as an article
                    return false;
                }

                if (isTheConversation)
                {   //Whether it should be open in the main View
                    Intent intent_MainActivity = new Intent(context, MainActivity.class);
                    intent_MainActivity.putExtra("url", urlNewString);
                    context.startActivity(intent_MainActivity);

                    return true;
                }
                else
                {
                    openLinkElsewhere(urlNewString);
                    return true;
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient());

        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        //for future dark mode availability

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//        {
//            webSettings.setForceDark(WebSettings.FORCE_DARK_ON);
//        }
        webView.loadUrl(articleUrl);

    }

    private void openLinkElsewhere(String urlNewString)
    {
        Log.d(TAG, "Opening link :" + urlNewString);

        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(urlNewString));

        //Intent intentOpenLink = Intent.createChooser(intent, getString(R.string.hint_Chooser_OpenLinkElsewhere));
        startActivity(intent);
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

    public void shareLink(MenuItem item)
    {
        Log.d(TAG, "Sharing link");

        Intent intent = new Intent();

        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.message_Sharing_Article) +  webView.getUrl());
        intent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(intent, getString(R.string.hint_Chooser_SharingLink));
        startActivity(shareIntent);
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
