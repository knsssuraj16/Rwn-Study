package com.rwn.rwnstudy.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rwn.rwnstudy.R;
import com.rwn.rwnstudy.utilities.ConstantForApp;
import com.rwn.rwnstudy.utilities.UserGetterAndSetter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class PersonalDetailActivity extends AppCompatActivity {

    private String User, Fullname, EmailId, CollegeName, AdmisionNumber,Gender="Male";
    int date, month, year;
    Calendar calendar;
    private EditText editTextNumber, editTextDOB, editTextRollNumber;
    private Button buttonSubmit;
    private Spinner spinnerSection,spinnerDepartment,spinnerCourse,spinnerSemster;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtonMale, radioButtonFemale;
    private DatabaseReference databaseReferenceUserRef,databaseReference;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog,pd;
    private String currentUser;
    private StorageReference firebaseStorageUserProfileRef;
    private Uri dataUri,downloadUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_detail);




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Personal Details");

        }



        pd = new ProgressDialog(this);
        pd.show();
        getIntentFromLastAtivity();
        viewIntialization();

        loadBranchSpinner(CollegeName);

        calendar = Calendar.getInstance();
        date = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);

        year = 1998;

        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton_male:
                        Gender = "Male";
                        break;
                    case R.id.radioButton_Female:
                        Gender = "Female";

                        break;

                }
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validationOfFields();
            }
        });

    }


    private void loadBranchSpinner(String collegeName) {
        databaseReference.child("College").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    final List<String> listBranch = new ArrayList<String>();
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {

                        listBranch.add(snap.getKey());
                    }

                    final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter <String>(
                            PersonalDetailActivity.this, android.R.layout.simple_list_item_1, listBranch);
                    spinnerDepartment.setAdapter(spinnerArrayAdapter);
                    pd.dismiss();
                    spinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                            PersonalDetailActivity.this, android.R.layout.simple_list_item_1, listCourse);
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
                            PersonalDetailActivity.this, android.R.layout.simple_list_item_1, listSemseterValue);
                    spinnerSemster.setAdapter(spinnerArrayAdapter);

                    spinnerSemster.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
                            String semesterName = listSemseter.get(position);

                            loadSectionSpinner(semesterName, CourseName);
                            //  loadSubjectSpinner(semesterName);
                            ///section passs like Semester1st ,
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
                            PersonalDetailActivity.this, android.R.layout.simple_list_item_1, listSectionValue);
                    spinnerSection.setAdapter(spinnerArrayAdapter);


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void validationOfFields() {

        String rollNum = editTextRollNumber.getText().toString().trim();
        String MoNum = editTextNumber.getText().toString().trim();

        String DOB = editTextDOB.getText().toString().trim();


        if (TextUtils.isEmpty(DOB)) {
            editTextDOB.requestFocus();
            editTextDOB.setError(getString(R.string.fill_this));
        } else if (MoNum.isEmpty() || MoNum.length() < 10) {
            editTextNumber.setError(getString(R.string.field_must_be_10));
            editTextNumber.requestFocus();
        }  else {
            try {
                String section = spinnerSection.getSelectedItem().toString();
                String course = spinnerCourse.getSelectedItem().toString();
                String department = spinnerDepartment.getSelectedItem().toString();
                String semester = spinnerSemster.getSelectedItem().toString();

                updateDataTOfirebaseDatabase(department, rollNum, MoNum, course, section, DOB,semester);


            } catch (Exception e) {
                progressDialog.dismiss();
                Toast.makeText(PersonalDetailActivity.this, R.string.something_went_wrong+e.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void updateDataTOfirebaseDatabase(final String department, final String rollNum, final String moNum, final String course, final String section, final String DOB,final  String semester) {

        progressDialog.setTitle("Saving info");
        progressDialog.setMessage("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        final StorageReference filepath = firebaseStorageUserProfileRef.child(currentUser + ".jpg");
        filepath.putFile(dataUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task <Uri>>() {
            @Override
            public Task <Uri> then(@NonNull Task <UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        throw Objects.requireNonNull(task.getException());
                    }
                }
                return filepath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener <Uri>() {
            @Override
            public void onComplete(@NonNull Task <Uri> task) {
                if (task.isSuccessful()) {
                    downloadUri = task.getResult();
                    databaseReferenceUserRef.child(getString(R.string.user_profile_image)).setValue(downloadUri.toString()).addOnCompleteListener(new OnCompleteListener <Void>() {
                        @Override
                        public void onComplete(@NonNull Task <Void> task) {
                            if (task.isSuccessful()) {
                                Calendar calendardate = Calendar.getInstance();
                                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                String saveCurrentDate = currentDate.format(calendardate.getTime());

                                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
                                String saveCurrenttime = currentTime.format(calendardate.getTime());


                                UserGetterAndSetter userGetterAndSetter = new UserGetterAndSetter();
                                userGetterAndSetter.setCollegeName(CollegeName);
                                userGetterAndSetter.setSection(section);
                                userGetterAndSetter.setName(Fullname);
                                userGetterAndSetter.setEmailID(EmailId);
                                userGetterAndSetter.setDepartment(department);
                                userGetterAndSetter.setUid(currentUser);
                                userGetterAndSetter.setDateOfBirth(DOB);
                                userGetterAndSetter.setGender(Gender);
                                Log.d("GENDERCHECKING", "onComplete: Gender is :"+Gender);
                                userGetterAndSetter.setUserType(User);
                                userGetterAndSetter.setAdmissionNumber(AdmisionNumber);
                                userGetterAndSetter.setMobileNumber(moNum);
                                userGetterAndSetter.setRollNumber(rollNum);
                                userGetterAndSetter.setCourse(course);
                                userGetterAndSetter.setSemester(semester);
                                userGetterAndSetter.setJoiningDate(saveCurrentDate);
                                userGetterAndSetter.setJoiningTime(saveCurrenttime);
                                userGetterAndSetter.setProfileImage(downloadUri.toString());

                                databaseReferenceUserRef.setValue(userGetterAndSetter).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful())
                                        {
                                            databaseReference.child(course).orderByValue().equalTo(semester).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    String semKey = null;
                                                    for (DataSnapshot a:dataSnapshot.getChildren()) {

                                                        semKey= a.getKey();

                                                    }

                                                    databaseReference.child("Students").child(semKey+section).child(currentUser).setValue(true).addOnCompleteListener(new OnCompleteListener <Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task <Void> task) {
                                                            if(task.isSuccessful()) {

                                                                FirebaseInstanceId.getInstance().getInstanceId()
                                                                        .addOnCompleteListener(new OnCompleteListener <InstanceIdResult>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task <InstanceIdResult> task) {
                                                                                if (!task.isSuccessful()) {
                                                                                    return;
                                                                                }

                                                                                // Get new Instance ID token
                                                                                String Device_token = task.getResult().getToken();

                                                                                databaseReferenceUserRef.child("deviceToken").setValue(Device_token).addOnCompleteListener(new OnCompleteListener <Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task <Void> task) {
                                                                                        if(task.isSuccessful()){
                                                                                            Toast.makeText(PersonalDetailActivity.this, R.string.Your_Account_SuccesfullyCrearted, Toast.LENGTH_SHORT).show();
                                                                                            Intent intent = new Intent(PersonalDetailActivity.this,MainActivity.class);
                                                                                            databaseReference.child("StudentRequest").child()
                                                                                            startActivity(intent);
                                                                                            finish();
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        });


                                                            }

                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });



                                            Toast.makeText(PersonalDetailActivity.this,R.string.Account_detail_update_succesfully,Toast.LENGTH_LONG).show();
                                            //TODO after main activity sending process
                                            startActivity(new Intent(PersonalDetailActivity.this,MainActivity.class));
                                            progressDialog.dismiss();
                                        }
                                        else
                                        {
                                            progressDialog.dismiss();
                                            Toast.makeText(PersonalDetailActivity.this, R.string.error_occourd+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(PersonalDetailActivity.this, R.string.error_occourd + ":upload failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });



    }

    private void viewIntialization() {
        editTextNumber = findViewById(R.id.editText_number);
        editTextRollNumber = findViewById(R.id.edit_roll_no);
        editTextDOB = findViewById(R.id.edit_date_Of_Birth);
        buttonSubmit = findViewById(R.id.button_submit);

        spinnerSection = findViewById(R.id.spinner_section);
        spinnerCourse = findViewById(R.id.spinner_course);
        spinnerDepartment = findViewById(R.id.spinner_branch);
        spinnerSemster = findViewById(R.id.spinner_semester);

        radioGroupGender = findViewById(R.id.radioGroup);
        radioButtonMale= findViewById(R.id.radioButton_male);
        radioButtonFemale= findViewById(R.id.radioButton_Female);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        firebaseStorageUserProfileRef = FirebaseStorage.getInstance().getReference().child(getString(R.string.storage_image)).child(currentUser).child(getString(R.string.firebase_storage_profile_picture_node));
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReferenceUserRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.datbase_user_node)).child(currentUser);
    }

    private void getIntentFromLastAtivity() {
        User = getIntent().getStringExtra(ConstantForApp.USER);
        Fullname = getIntent().getStringExtra(ConstantForApp.FULLNAME);
        AdmisionNumber = getIntent().getStringExtra(ConstantForApp.ADDMISION_NUMBER);
        CollegeName = getIntent().getStringExtra(ConstantForApp.COLLEGE_NAME);
        EmailId = getIntent().getStringExtra(ConstantForApp.EMAIL_ID);
        dataUri = Uri.parse(getIntent().getStringExtra(ConstantForApp.PROFILE_IMAAGE));


    }
}
