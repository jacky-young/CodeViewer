package com.jack.codeviewer;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by jack on 2/26/16.
 */
public class WebChrome2 extends WebViewClient {
    private ProgressDialog progressDialog;

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        progressDialog = ProgressDialog.show(view.getContext(), "Please wait...", "Opening File...", true);
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        super.onPageFinished(view, url);
    }
}
