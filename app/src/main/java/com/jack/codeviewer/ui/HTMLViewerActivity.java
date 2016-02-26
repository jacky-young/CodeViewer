package com.jack.codeviewer.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.jack.codeviewer.WebChrome2;
import com.jack.codeviewer.handler.DocumentHandler;
import com.jack.codeviewer.handler.JavaDocumentHandler;
import com.jack.codeviewer.handler.TextDocumentHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jack on 2/26/16.
 */
public class HTMLViewerActivity extends Activity {

    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieSyncManager.createInstance(this);
        webView = new WebView(this);
        setContentView(webView);

        webView.setWebViewClient(new WebChrome2());

        WebSettings s = webView.getSettings();
        s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        s.setUseWideViewPort(false);
        s.setAllowFileAccess(true);
        s.setBuiltInZoomControls(true);
        s.setLightTouchEnabled(true);
        s.setLoadsImagesAutomatically(true);
        s.setSupportZoom(true);
        s.setSupportMultipleWindows(true);
        s.setJavaScriptEnabled(true);

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            Intent intent = getIntent();
            if (intent.getData() != null) {
                Uri uri = intent.getData();
                if ("file".equals(uri.getScheme())) {
                    loadFile(uri, intent.getType());
                } else {
                    webView.loadUrl(uri.toString());
                }
            } else {
                webView.loadUrl("file:///android_asset/home.html");
            }
        }
    }

    private void loadFile(Uri uri, String mimeType) {
        String path = uri.getPath();
        DocumentHandler handler = getHandlerByExtension(path);

        File f = new File(path);
        final long length = f.length();

        if (!f.exists()) {
            return;
        }
        if (handler == null) {
            return;
        }

        byte[] array = new byte[(int)length];
        try {
            InputStream is = new FileInputStream(f);
            is.read(array);
            is.close();
        } catch (FileNotFoundException e) {
            return;
        } catch (IOException e) {
            return;
        }

        String contentString = "";
        setTitle(path);
        contentString += "<html><head><title>" + path + "</title>";
        contentString += "<link href='file:///android_asset/prettify.css' rel='stylesheet' type='text/css'/> ";
        contentString += "<script src='file:///android_asset/prettify.js' type='text/javascript'></script> ";
        contentString += handler.getFileScriptFiles();
        contentString +=  "</head><body onload='prettyPrint()'><code class='" + handler.getFilePrettifyClass() + "'>";
        String sourceString = new String(array);

        contentString += handler.getFileFormattedString(sourceString);
        contentString += "</code> </html> ";
        webView.getSettings().setUseWideViewPort(true);
        webView.loadDataWithBaseURL("file:///android_asset/", contentString, handler.getFileMimeType(), "", "");
    }

    private DocumentHandler getHandlerByExtension(String filename) {
        DocumentHandler handler = null;
        if (filename.endsWith(".java")) handler = new JavaDocumentHandler();

        if (handler == null) handler = new TextDocumentHandler();
        return handler;
    }

    @Override
    protected void onResume() {
        super.onResume();
        CookieSyncManager.getInstance().startSync();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (webView != null) {
            webView.destroy();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        CookieSyncManager.getInstance().stopSync();
        webView.stopLoading();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
    }
}
