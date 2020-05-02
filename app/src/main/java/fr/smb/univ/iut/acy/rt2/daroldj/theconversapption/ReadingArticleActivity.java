package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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

    private Context context = this;

    private String TAG = this.getClass().getName();

    private String data;

    final static String REGEX_URL_NOT_ARTICLE_THECONV = "theconversation.com/((fr)|(us)|(ca)|(global)|(africa)|(ca-fr)|(id)|(es)|(nz)|(uk)|(au)/?)";
    final static String REGEX_URL_PROFILE = "theconversation.com/profiles/";
    Pattern patternArticleUrl = Pattern.compile(REGEX_URL_NOT_ARTICLE_THECONV);
    Pattern patternProfileUrl = Pattern.compile(REGEX_URL_PROFILE);


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_article);

        setToolbar();

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

    private void setToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarReadingArticle);
        setSupportActionBar(toolbar);
        setTitle( getString(R.string.title_activity_reading_article));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    protected void loadWebViewAndURL()
    {
        webView = (WebView)findViewById(R.id.webviewArticle);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                // A failed attempt to apply javascript code to a webpage using webView..
                //But keeping hope so keeping it.

                view.loadUrl("javascript:(function hideStuff(){\n" +
                        "  var el = document.querySelector('#article');\n" +
                        "  var node, nodes = [];\n" +
                        "  \n" +
                        "  do {\n" +
                        "    var parent = el.parentNode;\n" +
                        "    \n" +
                        "    // Collect element children\n" +
                        "    for (var i=0, iLen=parent.childNodes.length; i<iLen; i++) {\n" +
                        "      node = parent.childNodes[i];\n" +
                        "\n" +
                        "      // Collect only sibling nodes that are elements and not the current element\n" +
                        "      if (node.nodeType == 1 && node != el) {\n" +
                        "        nodes.push(node);\n" +
                        "      }\n" +
                        "    }\n" +
                        "\n" +
                        "    // Go up to parent\n" +
                        "    el = parent;\n" +
                        "\n" +
                        "  // Stop when processed the body's child nodes\n" +
                        "  } while (el.tagName.toLowerCase() != 'body');\n" +
                        "\n" +
                        "  // Hide the collected nodes\n" +
                        "  nodes.forEach(function(node){\n" +
                        "    node.style.display = 'none';\n" +
                        "  });\n" +
                        "})");
                super.onPageFinished(view, url);
            }

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
                {  //Whether it should be open as an article
                    return false;
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
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        //for future dark mode availability

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//        {
//            webSettings.setForceDark(WebSettings.FORCE_DARK_ON);
//        }

        webView.loadUrl(articleUrl);
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

        Intent sendIntent = new Intent();

        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.message_Sharing_Article) +  webView.getUrl());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.hint_Chooser_SharingLink));
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
