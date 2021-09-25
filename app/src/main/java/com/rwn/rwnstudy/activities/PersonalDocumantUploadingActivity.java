package com.rwn.rwnstudy.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.rwn.rwnstudy.utilities.TeacherSubjectGetterSetter;
import com.rwn.rwnstudy.utilities.UserGetterAndSetter;

import java.util.ArrayList;
import java.util.Objects;

public class PersonalDocumantUploadingActivity extends AppCompatActivity {

    Button buttonCreateNewDocuments;
    private RecyclerView recyclerViewSubject;
    private TextView textViewName;
    private DatabaseReference databaseReference, databaseReferenceUserRef;
    public static final String TAG = "PersonalDocument";
    private FirebaseAuth mAuth;
    String userid;
    private String CURRENT_USER;
    String userType = null;
    ArrayList <? extends String> list;
    String UnitName;
    String SubjectName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_documant_uploading);

        actionBarDesinging();

        Intent intent = getIntent();
        userid= intent.getStringExtra("Userid");
        SubjectName =intent.getStringExtra("SubjectName");
        UnitName =intent.getStringExtra("Unitname");

        list = intent.getParcelableArrayListExtra("listdata");
        Log.d("CHECKFOR", "onCreate: "+list+"\n"+SubjectName+"\n"+UnitName);



        viewIntializing();

        clickAction();
   }



    private void viewIntializing() {

        buttonCreateNewDocuments = findViewById(R.id.button_create_a_document);
        recyclerViewSubject = findViewById(R.id.recyclerView_Subject);
        recyclerViewSubject.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PersonalDocumantUploadingActivity.this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        recyclerViewSubject.setLayoutManager(linearLayoutManager);
        mAuth = FirebaseAuth.getInstance();
        textViewName = findViewById(R.id.textView_name);
        CURRENT_USER = mAuth.getCurrentUser().getUid();
        if( userid != null )
        {
            CURRENT_USER = userid;
            buttonCreateNewDocuments.setVisibility(View.GONE);
            buttonCreateNewDocuments.setEnabled(false);
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();
        datbaseRetriveData();

    }

    private void datbaseRetriveData() {

        databaseReference.child(getString(R.string.datbase_user_node)).child(CURRENT_USER).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    UserGetterAndSetter userGetterAndSetter = dataSnapshot.getValue(UserGetterAndSetter.class);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        userType = Objects.requireNonNull(userGetterAndSetter).getUserType();
                    }
                    String sem = Objects.requireNonNull(userGetterAndSetter).getSemester();
                    String name = Objects.requireNonNull(userGetterAndSetter).getName();
                    if(userid != null){

                        textViewName.setText(name+" Document , Subject");

                    }
                    final String Course = userGetterAndSetter.getCourse();
                    ///in future when ever we go for expand nodes can be conflicts
                    String section = userGetterAndSetter.getSection();
                    if (userType.equals(ConstantForApp.TEACHER)) {
                        String Node = "TeachersSubject";

                        displayAllSubjects(Node, CURRENT_USER);

                    } else {

                        Query query = databaseReference.child(Course).orderByValue().equalTo(sem);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String SemesterCode = null;
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    SemesterCode = ds.getKey();
                                }

                                String CourseCode = SemesterCode;
                                /// college code and also semesterCode;
                                String Node = "Subject";
                                displayAllSubjects(Node, CourseCode);

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void displayAllSubjects(final String node, final String courseCode) {

        if (userType.equals(ConstantForApp.TEACHER)) {


            Query sortPostinDecendingOrder = databaseReference.child(node).child(courseCode);
            FirebaseRecyclerOptions <TeacherSubjectGetterSetter> options =
                    new FirebaseRecyclerOptions.Builder <TeacherSubjectGetterSetter>()
                            .setQuery(sortPostinDecendingOrder, TeacherSubjectGetterSetter.class)
                            .build();
            FirebaseRecyclerAdapter <TeacherSubjectGetterSetter, SubjectViewHolder> firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter <TeacherSubjectGetterSetter, SubjectViewHolder>(options) {


                        @Override
                        protected void onBindViewHolder(@NonNull final SubjectViewHolder holder, int position, @NonNull TeacherSubjectGetterSetter model) {


                            final String postKey = getRef(position).getKey();
                            databaseReference.child(node).child(courseCode).child(postKey)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {


                                                final TeacherSubjectGetterSetter teacherSubjectGetterSetter = dataSnapshot.getValue(TeacherSubjectGetterSetter.class);


                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                    final String buttonName;
                                                    if (teacherSubjectGetterSetter != null) {
                                                        buttonName = Objects.requireNonNull(teacherSubjectGetterSetter.getSubjectCode());

                                                        holder.button.setText(buttonName);

                                                        holder.button.setOnLongClickListener(new View.OnLongClickListener() {
                                                            @Override
                                                            public boolean onLongClick(View v) {

                                                                final String postKey = getRef(holder.getAdapterPosition()).getKey();


                                                                CharSequence options[] = new CharSequence[]{

                                                                        "Yes"
                                                                };
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(PersonalDocumantUploadingActivity.this)
                                                                        .setTitle("Select Option");

                                                                builder.setTitle("Remove this Subject");
                                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {

                                                                        if (which == 0) {

                                                                            removeTeacherDocument(postKey);

                                                                        }

                                                                    }

                                                                });

                                                                builder.show();


                                                                Log.d("TESTTEACH", "onLongClick: "+ postKey);
                                                                return true;
                                                            }
                                                        });

                                                        holder.button.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                Intent intent = new Intent(PersonalDocumantUploadingActivity.this, SubjectUnitsActivity.class);



                                                                if(SubjectName != null && UnitName != null && list != null){
                                                                    intent.putExtra("OldSubjectKey", SubjectName);
                                                                    intent.putExtra("OldUnitKey", UnitName);
                                                                    intent.putExtra("node", userType);
                                                                    intent.putStringArrayListExtra("ListOfTopic", (ArrayList <String>)list);
                                                                    intent.putExtra("SubjectKey", buttonName);
                                                                    intent.putExtra("SubjectName", teacherSubjectGetterSetter.getSubject());
                                                                    intent.putExtra("PersonalDocument", "PersonalDocument");
                                                                    intent.putExtra("userID", CURRENT_USER);
                                                                    startActivityForResult(intent,23);
                                                                }
                                                                else {
                                                                    //intent.putExtra("userID", CURRENT_USER);

                                                                    intent.putExtra("SubjectKey", buttonName);
                                                                    intent.putExtra("SubjectName", teacherSubjectGetterSetter.getSubject());
                                                                    intent.putExtra("PersonalDocument", "PersonalDocument");
                                                                    if (userid != null)
                                                                        intent.putExtra("userID", userid);
                                                                    startActivity(intent);
                                                                }
                                                            }
                                                        });
                                                    }
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
                            return new SubjectViewHolder(view);
                        }
                    };
            recyclerViewSubject.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();
        } else {

            Query sortPostinDecendingOrder = databaseReference.child(node).child(courseCode);
            FirebaseRecyclerOptions <String> options =
                    new FirebaseRecyclerOptions.Builder <String>()
                            .setQuery(sortPostinDecendingOrder, String.class)
                            .build();
            FirebaseRecyclerAdapter <String, SubjectViewHolder> firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter <String, SubjectViewHolder>(options) {


                        @Override
                        protected void onBindViewHolder(@NonNull final SubjectViewHolder holder, int position, @NonNull String model) {

                            final String postKey = getRef(position).getKey();
                            databaseReference.child(node).child(courseCode).child(postKey)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {

                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                    final String buttonName = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                                                    final String key =dataSnapshot.getKey();
                                                    holder.button.setText(buttonName);
                                                    Log.d(TAG, "onDataChange: " + buttonName);
                                                    holder.button.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {


                                                            Intent intent = new Intent(PersonalDocumantUploadingActivity.this, SubjectUnitsActivity.class);
                                                            if(SubjectName != null && UnitName != null && list != null){
                                                                intent.putExtra("OldSubjectKey", SubjectName);
                                                                intent.putExtra("OldUnitKey", UnitName);
                                                                intent.putStringArrayListExtra("ListOfTopic", (ArrayList <String>)list);
                                                                intent.putExtra("SubjectKey", key);
                                                                intent.putExtra("SubjectName", buttonName);
                                                                intent.putExtra("PersonalDocument", "PersonalDocument");
                                                                intent.putExtra("CurrentUser", CURRENT_USER);

                                                                intent.putExtra("node", userType);

                                                                startActivityForResult(intent,23);
                                                            }
                                                            else {
                                                                intent.putExtra("CurrentUser", CURRENT_USER);

                                                                intent.putExtra("SubjectKey", key);
                                                                intent.putExtra("SubjectName", buttonName);
                                                                intent.putExtra("PersonalDocument", "PersonalDocument");
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
            recyclerViewSubject.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();
        }
    }

    private void removeTeacherDocument(String postKey) {
        databaseReference.child("TeachersSubject").child(CURRENT_USER).child(postKey).removeValue().addOnCompleteListener(new OnCompleteListener <Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Log.d("TESTTEACH", "onComplete: ");

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode== 23){
            if(resultCode == 0){
                PersonalDocumantUploadingActivity.this.finish();
            }
        }
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        Button button;


        public SubjectViewHolder(View itemView) {

            super(itemView);

            button = itemView.findViewById(R.id.button_subject);


        }

    }

    private void clickAction() {
        buttonCreateNewDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(PersonalDocumantUploadingActivity.this, UploadDocumentActivity.class));
            }
        });
    }

    private void actionBarDesinging() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Personal Documents");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
    }
}
