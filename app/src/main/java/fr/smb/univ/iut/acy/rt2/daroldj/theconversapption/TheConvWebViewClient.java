package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TheConvWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        if ("theconversation.com".equals(Uri.parse(url).getHost()))
        {
            return false;
        }

        return true;
    }

}
