package com.rwn.rwnstudy.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.rwn.rwnstudy.R;

public class DocumentViewerActivity extends AppCompatActivity {
    ImageView imageView;
    TextView textViewTopic;
    TextView textViewDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_viewer);

        imageView = findViewById(R.id.ImageView_document)   ;
        textViewTopic = findViewById(R.id.textview_topic_name);
        textViewDescription = findViewById(R.id.textView_topic_description);


        Intent intent = getIntent();
        String topic=intent.getStringExtra("topic");
        String descrption= intent.getStringExtra("description");
        String link =intent.getStringExtra("link");



    }
}
