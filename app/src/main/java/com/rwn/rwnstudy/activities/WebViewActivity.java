package com.rwn.rwnstudy.activities;

import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.rwn.rwnstudy.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class WebViewActivity extends AppCompatActivity {
    WebView webView;
    String link;

    protected boolean exit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        webView = findViewById(R.id.webView);


        Intent intent = getIntent();
        link = intent.getStringExtra(Intent.EXTRA_TEXT);
        try {
            URL myURL = new URL(link);
            webviewMethod(myURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }



    }
    protected void webviewMethod(URL myURL) {
        webView.loadUrl(myURL.toString());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
    }
    public void onBackPressed() {


        // super.onBackPressed();

        if (webView.canGoBack()) {
            webView.goBack();

        } else {
            if (exit) {
                finish();
            } else {
                Toast.makeText(this, "Double click for exit", Toast.LENGTH_SHORT).show();
            }
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 2000);
            exit = true;
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menuforbrowser, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_open_in_browser) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(link));
            startActivity(i);

        }

        return super.onOptionsItemSelected(item);
    }
}
