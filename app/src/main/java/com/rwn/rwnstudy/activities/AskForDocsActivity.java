package com.rwn.rwnstudy.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rwn.rwnstudy.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AskForDocsActivity extends AppCompatActivity {
    Spinner spinnerSection;
    Button buttonSubmit;
    String section;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    String CURRENT_USER, userType, sectionforuploaading = null;
    DatabaseReference databaseReference;
    public final static String TAG = "RwnStudy";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_for_docs);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Find Section of your Friends");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        viewIntialization();
        dataBaseLinkUp();
        retriveUserData();
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sectionforuploaading.equals("")) {
                    Toast.makeText(AskForDocsActivity.this, "please select section", Toast.LENGTH_SHORT).show();

                } else {
                    Intent intent = new Intent(AskForDocsActivity.this, OpenclassDetailActivity.class);
                    intent.putExtra("classWithSection",sectionforuploaading);
                    startActivity(intent);
                }
            }
        });
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



    private void viewIntialization() {
        spinnerSection = findViewById(R.id.spinner_section);
        buttonSubmit = findViewById(R.id.button_upload_detial);
        progressDialog= new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

    }

    private void dataBaseLinkUp() {
        mAuth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            CURRENT_USER = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();


    }

    private void retriveUserData() {

        databaseReference.child(getString(R.string.datbase_user_node)).child(CURRENT_USER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    final String sem =dataSnapshot.child("semester").getValue().toString();
                    final String course =dataSnapshot.child("course").getValue().toString();
                    section =dataSnapshot.child("section").getValue().toString();

                    Query query = databaseReference.child(course).orderByValue().equalTo(sem);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot d : dataSnapshot.getChildren()) {
                                    String semsterKey = d.getKey();
                                    loadSectionSpinner(semsterKey, course);

                                }


                            } else {
                                Toast.makeText(AskForDocsActivity.this, "Data Not Found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void loadSectionSpinner(final String semesterName, final String courseName) {

        //semseterName BCaSemester1
///TODO Change it semester to Scection.
        databaseReference.child("Semester").child(semesterName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    final List <String> listSection = new ArrayList <String>();

                    final List <String> listSectionValue = new ArrayList <String>();

                    for (DataSnapshot snap : dataSnapshot.getChildren()) {

                        listSectionValue.add(snap.getValue().toString());
                        listSection.add(snap.getKey());

                    }

                    final ArrayAdapter <String> spinnerArrayAdapter = new ArrayAdapter <String>(
                            AskForDocsActivity.this, android.R.layout.simple_list_item_1, listSectionValue);
                    spinnerSection.setAdapter(spinnerArrayAdapter);

                    progressDialog.dismiss();
                    spinnerSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
                            sectionforuploaading = listSection.get(position);

                        }

                        @Override
                        public void onNothingSelected(AdapterView <?> parent) {

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
