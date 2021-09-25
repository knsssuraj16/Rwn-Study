package com.rwn.rwnstudy.activities;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileImageUploaderActivity extends AppCompatActivity {
    private CircleImageView circleImageViewUploadImage;
    private Button buttonSubmit;
    private String User;
    private ProgressBar progressBar;
    LinearLayout linearLayoutGrey;

    private DatabaseReference databaseReferenceUserRef,databaseReference;
    private Uri downloadUri, dataUri;
    private StorageReference firebaseStorageUserProfileRef;
    private String currentUser;
    private String Fullname, AdmissionNo;
    private String Department;
    private String EmailID;
    private String CollegeName;
    private String DOB, Gender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image_uploader);


        Objects.requireNonNull(getSupportActionBar()).setTitle("Profile Image Upload");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewIntializing();


        getPendingIntent();
        clickActionOnimageAndButton();

    }
    private void saveSharedPrefrence() {


        SharedPreferences sharedPreferences = getSharedPreferences("logindetail",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("Login","AlreadyLogin");
        editor.apply();

    }


    private void clickActionOnimageAndButton() {
        circleImageViewUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentGallary = new Intent();
                intentGallary.setAction(Intent.ACTION_GET_CONTENT);
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(ProfileImageUploaderActivity.this);
            }
        });
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(dataUri == null)) {
                    progressBar.setVisibility(View.VISIBLE);
                    linearLayoutGrey.setVisibility(View.VISIBLE);
                    buttonSubmit.setEnabled(false);
                    circleImageViewUploadImage.setEnabled(false);

                    if (User.equals(ConstantForApp.TEACHER)) {
                        uploadImageTofireBaseDatabase();
                    } else
                        sendUserToNextPage(Fullname, EmailID, AdmissionNo, CollegeName);
                } else
                    Toast.makeText(ProfileImageUploaderActivity.this, R.string.Please_upload_Profile_Picture, Toast.LENGTH_SHORT).show();
            }

        });
    }


    private void viewIntializing() {
        circleImageViewUploadImage = findViewById(R.id.circularView_upload_pic);
        buttonSubmit = findViewById(R.id.button_upload);
        progressBar = findViewById(R.id.progress_circular);
        linearLayoutGrey = findViewById(R.id.linear_layout_grey);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        firebaseStorageUserProfileRef = FirebaseStorage.getInstance().getReference().child(getString(R.string.storage_image)).child(currentUser).child(getString(R.string.firebase_storage_profile_picture_node));

        databaseReferenceUserRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.datbase_user_node)).child(currentUser);

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void getPendingIntent() {


        User = getIntent().getStringExtra(ConstantForApp.USER);
        if (User != null) {
            switch (User) {
                case ConstantForApp.TEACHER:


                    Fullname = getIntent().getStringExtra(ConstantForApp.FULLNAME);
                    Department = getIntent().getStringExtra(ConstantForApp.DEPARTMENT);
                    EmailID = getIntent().getStringExtra(ConstantForApp.EMAIL_ID);
                    CollegeName = getIntent().getStringExtra(ConstantForApp.COLLEGE_NAME);
                    DOB = getIntent().getStringExtra(ConstantForApp.DOB);
                    Gender = getIntent().getStringExtra(ConstantForApp.GENDER);


                    break;
                case ConstantForApp.STUDENT:

                    Fullname = getIntent().getStringExtra(ConstantForApp.FULLNAME);
                    AdmissionNo = getIntent().getStringExtra(ConstantForApp.ADDMISION_NUMBER);
                    EmailID = getIntent().getStringExtra(ConstantForApp.EMAIL_ID);
                    CollegeName = getIntent().getStringExtra(ConstantForApp.COLLEGE_NAME);


                    break;
                default:
                    Toast.makeText(this, R.string.error_occourd, Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            databaseReferenceUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if (dataSnapshot.exists()) {
                        UserGetterAndSetter teacherOrStudentsActivity = dataSnapshot.getValue(UserGetterAndSetter.class);
                        if(teacherOrStudentsActivity != null) {
                            CollegeName = teacherOrStudentsActivity.getCollegeName();
                            DOB = teacherOrStudentsActivity.getDateOfBirth();
                            Department = teacherOrStudentsActivity.getDepartment();
                            Gender = teacherOrStudentsActivity.getGender();
                            Fullname = teacherOrStudentsActivity.getName();
                            User = teacherOrStudentsActivity.getUserType();
                            EmailID = teacherOrStudentsActivity.getEmailID();
                            AdmissionNo = teacherOrStudentsActivity.getAdmissionNumber();
                        }
                    }
                }
                @Override
                public void onCancelled (@NonNull DatabaseError databaseError){

                }
            });
        }

    }

    private void sendUserToNextPage(String fullname, String emailID, String admissionNo, String CollegeName) {

        Intent intent = new Intent(ProfileImageUploaderActivity.this, PersonalDetailActivity.class);
        intent.putExtra(ConstantForApp.USER, ConstantForApp.STUDENT);
        intent.putExtra(ConstantForApp.FULLNAME, fullname);
        intent.putExtra(ConstantForApp.ADDMISION_NUMBER, admissionNo);
        intent.putExtra(ConstantForApp.COLLEGE_NAME, CollegeName);
        intent.putExtra(ConstantForApp.EMAIL_ID, emailID);
        intent.putExtra(ConstantForApp.PROFILE_IMAAGE, dataUri.toString());
        startActivity(intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                dataUri = result.getUri();



                Picasso.get().load(dataUri).placeholder(R.drawable.profile).into(circleImageViewUploadImage);

            }
        }
    }

    private void uploadImageTofireBaseDatabase() {




        final StorageReference filepath = firebaseStorageUserProfileRef.child(currentUser + ".jpg");
        filepath.putFile(dataUri).continueWithTask(new Continuation <UploadTask.TaskSnapshot, Task <Uri>>() {
            @Override
            public Task <Uri> then(@NonNull Task <UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
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
                                userGetterAndSetter.setName(Fullname);
                                userGetterAndSetter.setEmailID(EmailID);
                                userGetterAndSetter.setDepartment(Department);
                                userGetterAndSetter.setUid(currentUser);
                                userGetterAndSetter.setDateOfBirth(DOB);//
                                userGetterAndSetter.setGender(Gender);//
                                userGetterAndSetter.setUserType(User);//
                                userGetterAndSetter.setMobileNumber("N/A");
                                userGetterAndSetter.setRollNumber("N/A");
                                userGetterAndSetter.setAdmissionNumber("N/A");
                                userGetterAndSetter.setCourse("N/A");
                                userGetterAndSetter.setJoiningDate(saveCurrentDate);
                                userGetterAndSetter.setJoiningTime(saveCurrenttime);
                                userGetterAndSetter.setProfileImage(downloadUri.toString());

                                Log.d("jjj", "onComplete: "+ userGetterAndSetter.toString());


                                databaseReferenceUserRef.setValue(userGetterAndSetter).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {

                                            databaseReference.child("Teachers").child(Department).child(currentUser).setValue(true).addOnCompleteListener(new OnCompleteListener <Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task <Void> task) {
                                                    if(task.isSuccessful()){

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

                                                                                    databaseReference.child("TeacherRequest").child(currentUser).setValue("pending").addOnCompleteListener(new OnCompleteListener <Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task <Void> task) {
                                                                                            if(task.isSuccessful()){
                                                                                                progressBar.setVisibility(View.GONE);
                                                                                                linearLayoutGrey.setVisibility(View.GONE);
                                                                                                buttonSubmit.setEnabled(true);
                                                                                                circleImageViewUploadImage.setEnabled(true);
                                                                                                Toast.makeText(ProfileImageUploaderActivity.this, R.string.Your_Account_SuccesfullyCrearted, Toast.LENGTH_SHORT).show();
                                                                                                Intent intent = new Intent(ProfileImageUploaderActivity.this,MainActivity.class);
                                                                                                saveSharedPrefrence();
                                                                                                startActivity(intent);
                                                                                                finish();
                                                                                            }
                                                                                        }
                                                                                    });


                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                });


                                                    }

                                                }
                                            });


                                        } else {

                                            progressBar.setVisibility(View.GONE);
                                            linearLayoutGrey.setVisibility(View.GONE);
                                            buttonSubmit.setEnabled(true);
                                            circleImageViewUploadImage.setEnabled(true);
                                            Toast.makeText(ProfileImageUploaderActivity.this, R.string.error_occourd + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            } else {
                                progressBar.setVisibility(View.GONE);
                                linearLayoutGrey.setVisibility(View.GONE);
                                buttonSubmit.setEnabled(true);
                                circleImageViewUploadImage.setEnabled(true);
                                Toast.makeText(ProfileImageUploaderActivity.this, R.string.error_occourd + ":upload failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}