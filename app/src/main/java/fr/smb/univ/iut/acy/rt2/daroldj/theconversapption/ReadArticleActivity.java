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

public class ReadArticleActivity extends AppCompatActivity {

    String articleUrl;

    private WebView webView;

    private Context context = this;

    String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_article);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "What a nice button, isn't it ? ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        articleUrl = getIntent().getStringExtra("articleUrl");

        Log.d(getClass().getName(), articleUrl);

        loadWebViewAndURL(context);
    }

    protected void loadWebViewAndURL(final Context context)
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
                Log.e(this.getClass().getName(), "onPageFinished");
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

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//        {
//            webSettings.setForceDark(WebSettings.FORCE_DARK_ON);
//        }

        //setContentView(webView);
        webView.loadUrl(articleUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(TAG, "onCreateOptionsMenu");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_reading_article, menu);
        return true;
    }

    public void askFeedback(MenuItem menuItem)
    {
        Log.d(TAG, "askFeedback");

        Intent intent = new Intent(this, askFeedbackActivity.class);

        super.startActivity(intent);
        super.finish();
    }

    public void openSettings(MenuItem menuItem)
    {
        Log.d(TAG, "openSettings");

        Intent intent = new Intent(this, SettingsActivity.class);

        super.startActivity(intent);
        super.finish();
    }

    public void shareLink(MenuItem item)
    {
        Log.d(TAG, "the content of webview.getTitle : " + webView.getTitle());
        Log.d(TAG, "sharing link");

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);

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

}
