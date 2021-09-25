package com.rwn.rwnstudy.activities;

import android.app.ProgressDialog;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rwn.rwnstudy.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class UpdateWhatClassTeacherStudied extends AppCompatActivity {

    private Spinner spinnerCourse, spinnerSubject, spinnerCollege, spinnerSection, spinnerBranch, spinnerSemester;
    Button buttonSubmit;
    TextView textViewSucees, textViewAddSubject;
    FirebaseAuth mAuth;
    String CURRENT_USER;
    DatabaseReference databaseReference;
    public final static  String TAG = "RwnStudy";
    ProgressDialog progressDialog;
    boolean checking= true;
    String SubjectCode;
    String sectionforuploaading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_what_class_teacher_studied);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Update your Class detail");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }



        viewIntialization();

        dataBaseLinkUp();

        clickActionEvent();

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




    private void clickActionEvent() {

       buttonSubmit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               String College = "";
               String semester= "" ;

               String Branch= "" ;
               String Section= "" ;

               String Course = "";
               String Subject = "";
               try {
                    College = spinnerCollege.getSelectedItem().toString();
                    semester = spinnerSemester.getSelectedItem().toString();

                    Branch = spinnerBranch.getSelectedItem().toString();
                    Section = spinnerSection.getSelectedItem().toString();

                    Course = spinnerCourse.getSelectedItem().toString();
                    Subject = spinnerSubject.getSelectedItem().toString();
               }catch (Exception e){}
               finally {

               if (TextUtils.isEmpty(College)) {
                   spinnerCollege.requestFocus();
                   Toast.makeText(UpdateWhatClassTeacherStudied.this, "Please select College name", Toast.LENGTH_SHORT).show();
               } else if (semester.equals("") || spinnerSemester == null) {
                   spinnerSemester.requestFocus();
                   Toast.makeText(UpdateWhatClassTeacherStudied.this, "Please select semester name", Toast.LENGTH_SHORT).show();
               } else if (Branch.equals("")|| spinnerBranch == null) {
                   spinnerBranch.requestFocus();
                   Toast.makeText(UpdateWhatClassTeacherStudied.this, "Please select Branch name", Toast.LENGTH_SHORT).show();
               } else if (Section.equals("")|| spinnerSection == null) {
                   spinnerCourse.requestFocus();
                   Toast.makeText(UpdateWhatClassTeacherStudied.this, "Please select Course name", Toast.LENGTH_SHORT).show();
               } else if (Course.equals("")  || spinnerCourse == null) {
                   spinnerSection.requestFocus();
                   Toast.makeText(UpdateWhatClassTeacherStudied.this, "Please select Section name", Toast.LENGTH_SHORT).show();
               } else if (Subject.equals("")|| spinnerSubject == null) {
                   spinnerSubject.requestFocus();
                   Toast.makeText(UpdateWhatClassTeacherStudied.this, "Please select Subject name", Toast.LENGTH_SHORT).show();
               } else {
                   uplodeDataIntoDatabase(College,semester,Branch,Section,Course,Subject);
               }
           }}
       });

       textViewAddSubject.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               spinnerBranch.setVisibility(View.VISIBLE);
               spinnerCollege.setVisibility(View.VISIBLE);
               spinnerCourse.setVisibility(View.VISIBLE);
               spinnerSection.setVisibility(View.VISIBLE);
               spinnerSemester.setVisibility(View.VISIBLE);
               spinnerSubject.setVisibility(View.VISIBLE);
               buttonSubmit.setVisibility(View.VISIBLE);
               textViewAddSubject.setVisibility(View.GONE);
               textViewSucees.setVisibility(View.GONE);
           }
       });

    }

    private void uplodeDataIntoDatabase(String college, String semester, String branch, String section, String course, String subject) {

        progressDialog.setTitle("Updating detail");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        final HashMap hashMap= new HashMap();
        hashMap.put(getString(R.string.user_college_name),college);
        hashMap.put(getString(R.string.user_department),branch);
        hashMap.put(getString(R.string.user_section),section);
        hashMap.put(getString(R.string.user_course),course);
        hashMap.put(getString(R.string.user_subject),subject);
        hashMap.put(getString(R.string.user_semester),semester);
        hashMap.put(getString(R.string.user_uid),CURRENT_USER);
        hashMap.put(getString(R.string.subject_Code), SubjectCode);


        databaseReference.child("TeachersSubject").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    checking = true;

                    for (DataSnapshot d:dataSnapshot.getChildren()) {

                        if(d.hasChild(SubjectCode)){
                           checking= false;
                        }

                        Log.d("TESTTEACH", "Its for loop");
                    }

                   if(checking){
                       databaseReference.child("TeachersSubject").child(CURRENT_USER).child(SubjectCode).updateChildren(hashMap).
                               addOnCompleteListener(new OnCompleteListener() {
                                   @Override
                                   public void onComplete(@NonNull Task task) {

                                       if (task.isSuccessful()) {


                                           spinnerBranch.setVisibility(View.GONE);
                                           spinnerCollege.setVisibility(View.GONE);
                                           spinnerCourse.setVisibility(View.GONE);
                                           spinnerSection.setVisibility(View.GONE);
                                           spinnerSemester.setVisibility(View.GONE);
                                           spinnerSubject.setVisibility(View.GONE);
                                           buttonSubmit.setVisibility(View.GONE);
                                           textViewAddSubject.setVisibility(View.VISIBLE);
                                           textViewSucees.setVisibility(View.VISIBLE);
                                           progressDialog.dismiss();
                                       }
                                       else
                                       { Toast.makeText(UpdateWhatClassTeacherStudied.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                               //       Log.d(TAG, "onComplete: "+Objects.requireNonNull(task.getException()).getMessage());
                                           }
                                           progressDialog.dismiss();
                                       }
                                   }
                               });
                   }
                   else {
                       progressDialog.dismiss();

                       Log.d("TESTTEACH", "onChildAdded: ");
                       Toast.makeText(UpdateWhatClassTeacherStudied.this, "Sorry you can't Access another teacher Subjects", Toast.LENGTH_SHORT).show();
                   }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    private void dataBaseLinkUp() {
        mAuth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            CURRENT_USER = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();
        spinnerSetValue();
    }

    private void spinnerSetValue() {

        databaseReference.child("CollegeName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    final List <String> CollegesNamelist = new ArrayList <String>();
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {

                        CollegesNamelist.add(snap.getKey().toString());
                    }

                    final ArrayAdapter <String> spinnerArrayAdapter = new ArrayAdapter <String>(
                            UpdateWhatClassTeacherStudied.this, android.R.layout.simple_list_item_1, CollegesNamelist);
                    spinnerCollege.setAdapter(spinnerArrayAdapter);


                    spinnerCollege.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
                            String collegeName = CollegesNamelist.get(position);
                            loadBranchSpinner(collegeName);
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

    private void loadBranchSpinner(String collegeName) {
        databaseReference.child("College").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    final List <String> listBranch = new ArrayList <String>();
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {

                        listBranch.add(snap.getKey());
                    }

                    final ArrayAdapter <String> spinnerArrayAdapter = new ArrayAdapter <String>(
                            UpdateWhatClassTeacherStudied.this, android.R.layout.simple_list_item_1, listBranch);
                    spinnerBranch.setAdapter(spinnerArrayAdapter);

                    spinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
                            String branchName = listBranch.get(position);
                            loadCourseSpinner(branchName);
                            //enginearing pass or mgmt
                        }

                        @Override
                        public void onNothingSelected(AdapterView <?> parent) {

                        }
                    });
                }
                else{
                    spinnerBranch.setAdapter(null);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadCourseSpinner(String branchName) {
        databaseReference.child(branchName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    final List <String> listCourse = new ArrayList <String>();
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {

                        listCourse.add(snap.getKey());
                    }

                    final ArrayAdapter <String> spinnerArrayAdapter = new ArrayAdapter <String>(
                            UpdateWhatClassTeacherStudied.this, android.R.layout.simple_list_item_1, listCourse);
                    spinnerCourse.setAdapter(spinnerArrayAdapter);

                    spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
                            String CourseName = listCourse.get(position);
                            loadSemesterSpinner(CourseName);
                            //BCA PAss //mca
                        }

                        @Override
                        public void onNothingSelected(AdapterView <?> parent) {

                        }
                    });
                }else{
                    spinnerCourse.setAdapter(null);
                    spinnerSemester.setAdapter(null);
                    spinnerSection.setAdapter(null);
                    spinnerSubject.setAdapter(null);


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadSemesterSpinner(final String CourseName) {
        databaseReference.child(CourseName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    final List <String> listSemseter = new ArrayList <String>();
                    final List <String> listSemseterValue = new ArrayList <String>();
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {

                        listSemseter.add(snap.getKey());
                        listSemseterValue.add(snap.getValue().toString());
                    }

                    final ArrayAdapter <String> spinnerArrayAdapter = new ArrayAdapter <String>(
                            UpdateWhatClassTeacherStudied.this, android.R.layout.simple_list_item_1, listSemseterValue);
                    spinnerSemester.setAdapter(spinnerArrayAdapter);

                    spinnerSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {

                            int sem =spinnerSemester.getSelectedItemPosition();
                            String semesterName = listSemseter.get(sem);
                            loadSectionSpinner(semesterName, CourseName);
                            ///section passs like Semester1st ,
                        }

                        @Override
                        public void onNothingSelected(AdapterView <?> parent) {

                        }
                    });
                }else{
                    spinnerSemester.setAdapter(null);
                    spinnerSection.setAdapter(null);
                    spinnerSubject.setAdapter(null);

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
                            UpdateWhatClassTeacherStudied.this, android.R.layout.simple_list_item_1, listSectionValue);
                    spinnerSection.setAdapter(spinnerArrayAdapter);

                    spinnerSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
                             sectionforuploaading = listSectionValue.get(position);

                            loadSubjectSpinner(semesterName);
                            //loadSubjectSpinner(semesterName);
                        }

                        @Override
                        public void onNothingSelected(AdapterView <?> parent) {

                        }
                    });
                }else{
                    spinnerSection.setAdapter(null);

                    spinnerSubject.setAdapter(null);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadSubjectSpinner(String semesterName) {
        databaseReference.child("Subject").child(semesterName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    final List <String> listSubject = new ArrayList <String>();
                    final List <String> listSubjectValue = new ArrayList <String>();

                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        listSubjectValue.add(snap.getValue().toString());
                        listSubject.add(snap.getKey());
                    }

                    final ArrayAdapter <String> spinnerArrayAdapter = new ArrayAdapter <String>(
                            UpdateWhatClassTeacherStudied.this, android.R.layout.simple_list_item_1, listSubjectValue);
                    spinnerSubject.setAdapter(spinnerArrayAdapter);

                    spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {

                            String Section = spinnerSection.getSelectedItem().toString();
                            SubjectCode = listSubject.get(position)+Section;
                            //loadSubjectSpinner(semesterName);
                        }

                        @Override
                        public void onNothingSelected(AdapterView <?> parent) {

                        }
                    });
                } else{
                    spinnerSubject.setAdapter(null);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void viewIntialization() {
        spinnerCourse = findViewById(R.id.spinner_course);
        spinnerSubject = findViewById(R.id.spinner_subject);
        spinnerSection = findViewById(R.id.spinner_section);
        spinnerCollege = findViewById(R.id.spinner_college);
        spinnerBranch = findViewById(R.id.spinner_branch);
        buttonSubmit = findViewById(R.id.button_upload_detial);
        textViewAddSubject = findViewById(R.id.textView_add_new_class);
        textViewSucees = findViewById(R.id.edit_text_success);
        spinnerSemester = findViewById(R.id.spinner_semester);
        progressDialog = new ProgressDialog(this);
    }
}
