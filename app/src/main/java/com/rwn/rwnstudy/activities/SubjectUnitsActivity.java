package com.rwn.rwnstudy.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rwn.rwnstudy.R;
//import com.rwn.rwnstudy.utilities.Constant;
import com.rwn.rwnstudy.utilities.ConstantForApp;
import com.rwn.rwnstudy.utilities.DocumentGetterSetter;
import com.rwn.rwnstudy.utilities.UserGetterAndSetter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class SubjectUnitsActivity extends AppCompatActivity {
    private RecyclerView recyclerViewSubjectsUnit;
    private FirebaseAuth mAuth;
    String user;
    private DatabaseReference databaseReference;
    private String CURRENT_USER,userID;
    private  String SubjectKey,SubjectName,Section;
    final  static  String TAG= "RWNStudy";
    private TextView textView;
    String PersonalDocument;
    Calendar calendar;
    String node;
    ArrayList<? extends String> list;
    String oldSubject;
    String oldUnit;
    String oldNode;
    UserGetterAndSetter userGetterAndSetter;


    String saveCurrentDate,saveCurrenttime;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_units);


        calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrenttime = currentTime.format(calendar.getTime());
        getpendingIntent();
        actionBarDesinging();
        dataBaseSetUp();
    }




    private void getpendingIntent() {
        textView=  findViewById(R.id.textview_units);
        Intent intent = getIntent();


        list = intent.getParcelableArrayListExtra("ListOfTopic");
        oldSubject =intent.getStringExtra("OldSubjectKey");
        oldUnit =intent.getStringExtra("OldUnitKey");
        oldNode =intent.getStringExtra("node");



        SubjectKey= intent.getStringExtra("SubjectKey");
        user= intent.getStringExtra("CurrentUser");
        SubjectName=intent.getStringExtra("SubjectName");
        PersonalDocument=intent.getStringExtra("PersonalDocument");
        Section=intent.getStringExtra("Section");
        userID=intent.getStringExtra("userID");
        textView.setText("Choose "+SubjectName+" Unit");



    }

    private void actionBarDesinging() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Subject units");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewIntializing();

    }
    private void viewIntializing() {
        recyclerViewSubjectsUnit = findViewById(R.id.recyclerView_Subject);
        recyclerViewSubjectsUnit.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SubjectUnitsActivity.this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        recyclerViewSubjectsUnit.setLayoutManager(linearLayoutManager);

    }
    private void dataBaseSetUp() {
        mAuth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            CURRENT_USER  = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        }

        if(userID != null)
        {
            CURRENT_USER = userID;
        }
        if(user != null){
            CURRENT_USER = user;
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();

        displayAllSubjectsSePahlay();

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
    private void displayAllSubjectsSePahlay() {
        databaseReference.child("Users").child(CURRENT_USER).addListenerForSingleValueEvent(new ValueEventListener() {

            String s;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userGetterAndSetter = dataSnapshot.getValue(UserGetterAndSetter.class);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });



        if(Section != null) {
            String   s= SubjectKey +Section;
            displayAllSubjects(s, "Teachers");
        }
        else {
            databaseReference.child("Users").child(CURRENT_USER).addListenerForSingleValueEvent(new ValueEventListener() {

                String s;
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userGetterAndSetter = dataSnapshot.getValue(UserGetterAndSetter.class);
                    String usertype=userGetterAndSetter.getUserType();
                    String sss;
                    if(usertype.equals(ConstantForApp.STUDENT)){
                        s=  SubjectKey + userGetterAndSetter.getSection();
                        sss= "Students";}
                    else {
                        s=SubjectKey;
                        sss = "Teachers";
                    }
                    displayAllSubjects(s,sss);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }


            });

        }
    }


    private void displayAllSubjects(String s, final String usersType) {

        Query sortPostinDecendingOrder = databaseReference.child("UnitsByTeachers").child(s);
        FirebaseRecyclerOptions<String> options =
                new FirebaseRecyclerOptions.Builder <String>()
                        .setQuery(sortPostinDecendingOrder, String.class)
                        .build();
        final String finalS = s;
        FirebaseRecyclerAdapter<String, SubjectViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter <String, SubjectViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final SubjectViewHolder holder, final int position, @NonNull final String model) {


                        final String postKey = getRef(position).getKey();
                        databaseReference.child("UnitsByTeachers").child(finalS).child(postKey)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                final String buttonName = Objects.requireNonNull(dataSnapshot.getValue()).toString();



                                                if (usersType.equals("Teachers")) {
                                                    node = "TeacherUplodedDocument";
                                                } else {
                                                    node = "StudentUplodedDocument";
                                                }



                                                if(node.equals("TeacherUplodedDocument") && userGetterAndSetter.getUserType().equals("Student"))
                                                {
                                                    databaseReference.child(node).child(finalS).child(postKey).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists()) {
                                                                int coount = (int) dataSnapshot.getChildrenCount();

                                                                if (coount >= 1)
                                                                    holder.button.setText(buttonName + " (" + coount + ")");
                                                                else
                                                                    holder.button.setText(buttonName);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                                else {
                                                    databaseReference.child(node).child(finalS).child(postKey).child(CURRENT_USER).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if(dataSnapshot.exists()){
                                                                int coount  = (int) dataSnapshot.getChildrenCount();

                                                                if(coount >=1)
                                                                    holder.button.setText(buttonName+  " ("+coount+")");
                                                                else
                                                                    holder.button.setText(buttonName);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });




                                                }








                                                holder.button.setText(buttonName);
                                                Log.d(TAG, "onDataChange: "+buttonName);
                                                holder.button.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {


                                                        if(list!= null && oldSubject!= null && oldUnit != null) {

                                                            CharSequence options[] = new CharSequence[]{
                                                                    "Yes"};
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(SubjectUnitsActivity.this);

                                                            builder.setTitle("Send Selected Document here");
                                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                    if (which == 0) {
                                                                        ProgressDialog progressDialog = new ProgressDialog(SubjectUnitsActivity.this);
                                                                        progressDialog.show();

                                                                        DatabaseReference d;

                                                                        final String Node;
                                                                        if (oldNode.equals(ConstantForApp.TEACHER)) {
                                                                            Node = "TeacherUplodedDocument";
                                                                            d=databaseReference.child(Node).child(oldSubject).child(oldUnit);
                                                                        }
                                                                        else{
                                                                            Node = "StudentUplodedDocument";
                                                                            d= databaseReference.child(Node).child(oldSubject).child(oldUnit).child(CURRENT_USER);
                                                                        }

                                                                        for (String s : list) {
                                                                            d.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                    if(dataSnapshot.exists()) {
                                                                                        DocumentGetterSetter documentGetterSetter = dataSnapshot.getValue(DocumentGetterSetter.class);
                                                                                        documentGetterSetter.setUnit(postKey);
                                                                                        documentGetterSetter.setSubject(finalS);
                                                                                        documentGetterSetter.setUploadingTime(saveCurrenttime);
                                                                                        documentGetterSetter.setUploadingDate(saveCurrentDate);
                                                                                        DatabaseReference du;
                                                                                        if (oldNode.equals(ConstantForApp.TEACHER)) {
                                                                                            du = databaseReference.child(Node).child(finalS).child(postKey);

                                                                                        }
                                                                                        else{
                                                                                            du = databaseReference.child(Node).child(finalS).child(postKey).child(mAuth.getCurrentUser().getUid());


                                                                                        }
                                                                                        du.child(documentGetterSetter.getKey())
                                                                                                .setValue(documentGetterSetter).addOnCompleteListener(new OnCompleteListener <Void>() {
                                                                                                                                                          @Override
                                                                                                                                                          public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                              if(task.isSuccessful()){

                                                                                                                                                                  Log.d("LOGDVER", "onDataChange: task succesful");
                                                                                                                                                              }
                                                                                                                                                              else{

                                                                                                                                                                  Log.d("LOGDVER", "onDataChange: "+task.getException());
                                                                                                                                                              }
                                                                                                                                                          }
                                                                                                                                                      }
                                                                                        );
                                                                                        Log.d("LOGDVER", "onDataChange: Work done");
                                                                                    }

                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                }
                                                                            });
                                                                        }
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(SubjectUnitsActivity.this, "Share Document Succesfully", Toast.LENGTH_SHORT).show();

                                                                        finish();
                                                                    }
                                                                }

                                                            });

                                                            builder.show();




                                                        }

                                                        else {
                                                            Intent intent = new Intent(SubjectUnitsActivity.this, UnitTopicShowActivity.class);
                                                            intent.putExtra("Unit", postKey);
                                                            intent.putExtra("SubjectKeyWithSection", finalS);
                                                            intent.putExtra("PersonalDocument", PersonalDocument);
                                                            if (userID != null) {

                                                                intent.putExtra("userId", userID);
                                                            }  if (user != null) {

                                                                intent.putExtra("userId", user);
                                                            }
                                                            startActivity(intent);
                                                        }
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

                    @NonNull
                    @Override
                    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.subject_layout, parent, false);
                        SubjectViewHolder subjectViewHolder = new SubjectViewHolder(view);
                        return subjectViewHolder;
                    }
                };
        recyclerViewSubjectsUnit.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
    @Override
    public void onStop()
    {
        super.onStop();
        this.setResult(0);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        this.setResult(0);
    }
    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        Button button;


        public SubjectViewHolder(View itemView) {

            super(itemView);

            button = itemView.findViewById(R.id.button_subject);


        }

    }
}

