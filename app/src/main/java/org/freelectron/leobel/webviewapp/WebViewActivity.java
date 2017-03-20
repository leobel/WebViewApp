package org.freelectron.leobel.webviewapp;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebViewActivity extends AppCompatActivity {

    private WebView mWebView;
    private Pattern pattern = Pattern.compile("^whatsapp://send\\?text=(.*)|^sms:\\?body=(.*)|^mailto:\\?&body=(.*)");
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mWebView.canGoBack()){
                    mWebView.goBack();
                }else{
                    finish();
                }
            }
        });

        mDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.invitation_dialog_title)
                .setPositiveButton(R.string.invitation_dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDialog.dismiss();
                    }
                })
                .create();

        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleUrl(url);
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return handleUrl(request.getUrl().toString());
            }
        });

        mWebView.loadUrl(getString(R.string.site_url));
    }

    private boolean handleUrl(String url) {
        Matcher matcher = pattern.matcher(url);
        if(matcher.matches()){
            if(matcher.group(1) != null) { // whastapp invitation
                mDialog.setMessage(getString(R.string.whatsapp_not_available));
            }
            else if(matcher.group(2) != null){ // sms invitation
                mDialog.setMessage(getString(R.string.sms_not_available));
            }
            else{ // email invitation
                mDialog.setMessage(getString(R.string.email_not_available));
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if(intent.resolveActivity(getPackageManager()) != null){
                startActivity(intent);
            }
            else{
                mDialog.show();
            }
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()){
            mWebView.goBack();
        }
        else{
            super.onBackPressed();
        }
    }
}
