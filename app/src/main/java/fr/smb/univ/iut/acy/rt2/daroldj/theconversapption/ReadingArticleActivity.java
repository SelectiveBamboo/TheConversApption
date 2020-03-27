package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class ReadingArticleActivity extends AppCompatActivity {

    String articleUrl;

    private WebView webView;

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_article);

        articleUrl = getIntent().getStringExtra("articleUrl");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.downloadArticle_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Log.d(getClass().getName(), articleUrl);

        loadWebViewAndURL(context);
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
}
