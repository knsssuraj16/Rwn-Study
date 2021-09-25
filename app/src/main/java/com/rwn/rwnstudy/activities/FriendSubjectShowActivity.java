package com.rwn.rwnstudy.activities;

import android.content.Intent;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rwn.rwnstudy.R;

import java.util.Objects;

public class FriendSubjectShowActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView textView;
    String UserIdByIntent;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_subject_show);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Friend Subjects");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        intializingViwe();
        getPendingIntent();
        databaseLinkUp();

    }

    private void databaseLinkUp() {

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(UserIdByIntent);

           }

    private void getPendingIntent() {

        Intent intent = getIntent();
        UserIdByIntent=intent.getStringExtra("Userid");
        textView.setText("");
    }

    private void intializingViwe() {

        textView= findViewById(R.id.textView_add_new_class);
        recyclerView = findViewById(R.id.recyclerView_Subject);

    }
}
