package com.rwn.rwnstudy.activities;

import android.content.Intent;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.rwn.rwnstudy.R;

import java.util.Objects;

public class OfferPageActivity extends AppCompatActivity {
    FrameLayout frameLayoutOnline, frameLayoutOffline,frameLayoutOthers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_page);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Offers/deals");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewIntializing();
    }

    private void viewIntializing() {
        frameLayoutOffline = findViewById(R.id.framelayout_offline);
        frameLayoutOnline = findViewById(R.id.framelayout_online);
        frameLayoutOthers = findViewById(R.id.framelayout_others);

        clickEvents();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clickEvents() {
        frameLayoutOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.rwn.rwnstudy.action.SHOWSTORE");
                intent.setType("text/plain");
                intent.putExtra(intent.EXTRA_TEXT,"Offline");
                startActivity(intent);
            }
        });
        frameLayoutOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.rwn.rwnstudy.action.SHOWSTORE");
                intent.setType("text/plain");
                intent.putExtra(intent.EXTRA_TEXT,"Online");
                startActivity(intent);
            }
        });
        frameLayoutOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.rwn.rwnstudy.action.SHOWSTORE");
                intent.setType("text/plain");
                intent.putExtra(intent.EXTRA_TEXT,"Others");
                startActivity(intent);
            }
        });
    }
}
