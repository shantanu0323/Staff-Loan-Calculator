package com.shaan.tnclerksfraternity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    WebView webView;
    ProgressBar progressBar;
    TextView tvLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViews();
        loadDataFromServer();

    }

    private void loadDataFromServer() {
        if (isNetworkAvailable()) {
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    view.loadUrl(request.getUrl().toString());
                    return false;
                }
            });
            webView.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView webView, int progress) {
                    progressBar.setProgress(progress);
                    tvLoading.setText("Loading...");
                    tvLoading.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);

                    if (progress == 100) {
                        tvLoading.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                    super.onProgressChanged(webView, progress);
                }
            });

            webView.getSettings().setJavaScriptEnabled(true);
            webView.setVerticalScrollBarEnabled(true);
            webView.loadUrl("http://tnclerksfraternity.000webhostapp.com/index.php");
            progressBar.setProgress(0);
        } else {
            tvLoading.setText("There is some problem with the Internet Connectivity. Please try again later");
            tvLoading.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void findViews() {
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);
        tvLoading = findViewById(R.id.tvLoading);
    }

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                    loadDataFromServer();
                }
            }
            tvLoading.setText("There is some problem with the Internet Connectivity. Please try again later");
            tvLoading.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialog));
        builder.setTitle("Sure to exit ?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        HomeActivity.super.onBackPressed();
                        finish();
                    }
                })
                .setNegativeButton("No", null);
        AlertDialog dialog = builder.show();
    }
}
