package com.rwn.rwnstudy.activities;

import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rwn.rwnstudy.R;
import com.rwn.rwnstudy.utilities.UserGetterAndSetter;

import java.util.Objects;

public class FacultiesDocumentActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSubject;
    private DatabaseReference databaseReference;
    private String CURRENT_USER,section;
    private String semsterKey;
    final  static  String TAG= "RWNStudy";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculties_document);



        actionBarDesinging();
        dataBaseSetUp();



    }

    private void dataBaseSetUp() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        CURRENT_USER  = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        retriveUserData();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void retriveUserData() {

        databaseReference.child(getString(R.string.datbase_user_node)).child(CURRENT_USER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    UserGetterAndSetter userGetterAndSetter = dataSnapshot.getValue(UserGetterAndSetter.class);

                    String sem= null;
                    if (userGetterAndSetter != null) {
                        sem = userGetterAndSetter.getSemester();

                        String course = userGetterAndSetter.getCourse();
                        section = userGetterAndSetter.getSection();
                        Query query = databaseReference.child(course).orderByValue().equalTo(sem);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                                        semsterKey = d.getKey();
                                    }
                                    displayAllSubjects();


                                } else {
                                    Toast.makeText(FacultiesDocumentActivity.this, "Data Not Found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


    private void displayAllSubjects() {



        Query sortPostinDecendingOrder = databaseReference.child("Subject").child(semsterKey);
        FirebaseRecyclerOptions<String> options =
                new FirebaseRecyclerOptions.Builder <String>()
                        .setQuery(sortPostinDecendingOrder, String.class)
                        .build();
        FirebaseRecyclerAdapter<String, SubjectViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter <String, SubjectViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final SubjectViewHolder holder, final int position, @NonNull final String model) {

                        final String postKey = getRef(position).getKey();

                        databaseReference.child("Subject").child(semsterKey).child(postKey)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            final String buttonName=  Objects.requireNonNull(dataSnapshot.getValue()).toString();
                                            holder.button.setText(buttonName);
                                            Log.d(TAG, "onDataChange: "+buttonName);
                                            holder.button.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent= new  Intent( FacultiesDocumentActivity.this,SubjectUnitsActivity.class);
                                                    intent.putExtra("SubjectKey",postKey);
                                                    intent.putExtra("SubjectName",buttonName);
                                                    intent.putExtra("Section",section);
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });



                    }

                    @NonNull
                    @Override
                    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.subject_layout, parent, false);
                        return new SubjectViewHolder(view);
                    }
                };
        recyclerViewSubject.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        Button button;


        SubjectViewHolder(View itemView) {

            super(itemView);

            button = itemView.findViewById(R.id.button_subject);


        }

    }

    private void actionBarDesinging() {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Faculty Notes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewIntializing();

    }

    private void viewIntializing() {
        recyclerViewSubject = findViewById(R.id.recyclerView_Subject);
        recyclerViewSubject.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FacultiesDocumentActivity.this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        recyclerViewSubject.setLayoutManager(linearLayoutManager);

    }
}
