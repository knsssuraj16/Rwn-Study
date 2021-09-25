package com.rwn.rwnstudy.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rwn.rwnstudy.R;
import com.rwn.rwnstudy.utilities.ConstantForApp;
import com.rwn.rwnstudy.utilities.DocumentAdapter;
import com.rwn.rwnstudy.utilities.DocumentGetterSetter;
import com.rwn.rwnstudy.utilities.UserGetterAndSetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//import com.rwn.rwnstudy.utilities.Constant;

public class UnitTopicShowActivity extends AppCompatActivity  {
    private RecyclerView recyclerViewUnitTopic;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String CURRENT_USER,UserId;
    private  String UnitName,SubjectName,Section;
    final  static  String TAG= "RWNStudy";
    private TextView textView;
    private  DocumentAdapter documentAdapter;
    private final List<DocumentGetterSetter> documentGetterSetterslist = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    String PersonalDocument= null;



    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_topic_show);







        getpendingIntent();
        actionBarDesinging();
        dataBaseSetUp();
        testing();



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


    private void getpendingIntent() {
        textView=  findViewById(R.id.textview_units);
        Intent intent = getIntent();
        SubjectName= intent.getStringExtra("SubjectKeyWithSection");
        PersonalDocument= intent.getStringExtra("PersonalDocument");
        UnitName=intent.getStringExtra("Unit");
        UserId=intent.getStringExtra("userId");



    }

    private  void testing(){


        if(PersonalDocument != null)
        {
            Query query =databaseReference.child("Users").child(CURRENT_USER);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String node;
                    UserGetterAndSetter userGetterAndSetter  = dataSnapshot.getValue(UserGetterAndSetter.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        String  userType= Objects.requireNonNull(userGetterAndSetter).getUserType();
                      DatabaseReference dommmm ;
                        if(userType.equals(ConstantForApp.TEACHER)){
                            node = "TeacherUplodedDocument";
                          dommmm=  databaseReference.child(node).child(SubjectName).child(UnitName);

                        }
                        else{
                            node = "StudentUplodedDocument";
                           dommmm= databaseReference.child(node).child(SubjectName).child(UnitName).child(CURRENT_USER);

                        }

                        if(dommmm != null)
                        dommmm.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                if (dataSnapshot.exists()) {
                                    DocumentGetterSetter documentGetterSetter = dataSnapshot.getValue(DocumentGetterSetter.class);
                                    documentGetterSetterslist.add(documentGetterSetter);
                                    documentAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    DocumentGetterSetter documentGetterSetter = dataSnapshot.getValue(DocumentGetterSetter.class);
                                    documentGetterSetterslist.remove(documentGetterSetter);


                                    documentAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
        else{
            databaseReference.child("TeacherUplodedDocument").child(SubjectName).child(UnitName)
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if (dataSnapshot.exists()) {
                                DocumentGetterSetter documentGetterSetter = dataSnapshot.getValue(DocumentGetterSetter.class);
                                documentGetterSetterslist.add(documentGetterSetter);
                                documentAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                DocumentGetterSetter documentGetterSetter = dataSnapshot.getValue(DocumentGetterSetter.class);
                                documentGetterSetterslist.remove(documentGetterSetter);
                                documentAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void actionBarDesinging() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Topics of Unit");
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        viewIntializing();

    }

    private void viewIntializing() {

        recyclerViewUnitTopic = findViewById(R.id.recyclerView_topic);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(UnitTopicShowActivity.this);
        recyclerViewUnitTopic.setLayoutManager(linearLayoutManager);
        documentAdapter = new DocumentAdapter(UnitTopicShowActivity.this,documentGetterSetterslist,SubjectName,UnitName);
        recyclerViewUnitTopic.setAdapter(documentAdapter);

    }
    private void dataBaseSetUp() {
        mAuth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            CURRENT_USER  = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        }
        if(UserId != null){
            CURRENT_USER = UserId;
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();



    }

}
