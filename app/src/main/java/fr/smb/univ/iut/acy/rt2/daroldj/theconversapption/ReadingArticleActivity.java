package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

public class ReadingArticleActivity extends AppCompatActivity {

    private String articleUrl;

    private WebView webView;

    private Context context = this;

    private String TAG = this.getClass().getName();

    private String data;

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
            articleUrl = getIntent().getStringExtra("articleUrl");
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.downloadArticle_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                final String nameOfArchivedFile = webView.getUrl().substring(28);

                final File archiveFile = new File(context.getFilesDir(), nameOfArchivedFile);
                webView.saveWebArchive(archiveFile.getAbsolutePath());

                Snackbar.make(view, "Stop annoying the Panic Button, it's just a virus bro. Internet will still be.", Snackbar.LENGTH_LONG)
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

        loadWebViewAndURL();
    }

    private void setToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarReadingArticle);
        setSupportActionBar(toolbar);
        setTitle("");
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
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);
            }
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
        });

        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setDisplayZoomControls(true);
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
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Have a look at this, great articles deserve great audience ! :) \n\n" +  webView.getUrl());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "To choose how you share");
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
