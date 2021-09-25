package com.rwn.rwnstudy.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rwn.rwnstudy.R;
import com.rwn.rwnstudy.utilities.ConstantForApp;
import com.rwn.rwnstudy.utilities.UserGetterAndSetter;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class UploadDocumentActivity extends AppCompatActivity {


    private static final String TAG = "SURAJ_TAG";
    private Spinner spinnerSubject, spinnerUnit;
    private ImageView imageViewAtaachament;
    private EditText editTextTopic, editTextDecription;
    private Button buttonUploadDocument;
    private FirebaseAuth mAuth;
    String Dtype;
    String CURRENT_USER, userType;
    private DatabaseReference databaseReferenceUserRef, databaseReference;
    private StorageReference firebaseStorageDocumentuplod;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_CODE = 2;
    static final int IMAGE_READER_RQUEST_CODE = 3;
    static final int FILE_PICKER_REQUEST_CODE = 4;
    static final int FILE_PICKER_REQUEST_PPT = 78;
    Boolean mStoragePermission = false;
    Bitmap imageBitmap = null;
    String filePath;
    ProgressDialog progressDialog, pd;
    String section;
    String classSubject;

    Uri documentUri = null, imageUri = null, cameraImageUri, downloadUri, documentUriFor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_document);

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        actionBarDesinging();


        viewIntialization();

        databaseConnectivity();
        clickActionEvent();


    }


    private void setValueOnSpinner(String semstername, String sub) {

// TeacherSubject node from firebase  connection for teacher class(jismay vo jayega padhanay)

        databaseReference.child(sub).child(semstername).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final List <String> listTeacherSubject = new ArrayList <String>();
                            final List <String> listTeacherSubjectValue = new ArrayList <String>();
                            for (DataSnapshot snap : dataSnapshot.getChildren()) {
                                if (userType.equals(ConstantForApp.STUDENT)) {
                                    listTeacherSubjectValue.add(snap.getValue().toString());
                                    listTeacherSubject.add(snap.getKey());
                                } else
                                    listTeacherSubject.add(snap.getKey());
                            }
                            List list;
                            if (userType.equals(ConstantForApp.STUDENT)) {
                                list = listTeacherSubjectValue;
                            } else {
                                list = listTeacherSubject;
                            }

                            final ArrayAdapter <String> spinnerArrayAdapter = new ArrayAdapter <String>(
                                    UploadDocumentActivity.this, android.R.layout.simple_list_item_1, list);
                            spinnerSubject.setAdapter(spinnerArrayAdapter);


                            spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {

                                    if (userType.equals(ConstantForApp.STUDENT)) {

                                        classSubject = listTeacherSubject.get(position) + section;
                                    } else {
                                        classSubject = listTeacherSubject.get(position);
                                    }

                                    loadUnitSpinner(classSubject);
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

    private void loadUnitSpinner(String classSubject) {
        databaseReference.child("UnitsByTeachers").child(classSubject).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {
                    final List <String> listUnitValue = new ArrayList <String>();
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {

                        listUnitValue.add(snap.getKey().toString());
                    }

                    final ArrayAdapter <String> spinnerArrayAdapter = new ArrayAdapter <String>(
                            UploadDocumentActivity.this, android.R.layout.simple_list_item_1, listUnitValue);
                    spinnerUnit.setAdapter(spinnerArrayAdapter);
                    pd.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    private void dispatchTakePictureIntent() {
////        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
////            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
////        }
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//
//            } catch (Exception e) {
//
//            }
//            if (photoFile != null) {
//                Uri photoUri = FileProvider.getUriForFile(
//                        UploadDocumentActivity.this,
//                        "com.rwn.rwnstudy.fileprovider",
//                        photoFile
//                );
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//
//
//                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//            }
//        }
//    }

//    private File createImageFile() {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = null;
//        try {
//            image = File.createTempFile(
//                    imageFileName,
//                    ".jpg",
//                    storageDir);
//            filePath = image.getAbsolutePath();
//
//
//        } catch (IOException e) {
//
//        }
//        return image;
//    }

    private void databaseConnectivity() {

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        if (mAuth != null)
            CURRENT_USER = mAuth.getCurrentUser().getUid();
        databaseReferenceUserRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.datbase_user_node)).child(CURRENT_USER);

        databaseReferenceUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserGetterAndSetter userGetterAndSetter = dataSnapshot.getValue(UserGetterAndSetter.class);
                if (userGetterAndSetter != null) {
                    userType = userGetterAndSetter.getUserType();
                    if (userType.equals(ConstantForApp.TEACHER)) {
                        String semstername = "TeachersSubject";
                        setValueOnSpinner(CURRENT_USER, semstername);
                    } else {

                        String sem = userGetterAndSetter.getSemester();
                        String course = userGetterAndSetter.getCourse();
                        section = userGetterAndSetter.getSection();
                        Query query = databaseReference.child(course).orderByValue().equalTo(sem);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String semstername = null;
                                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                                        semstername = d.getKey();

                                    }

                                    String Sub = "Subject";
                                    setValueOnSpinner(semstername, Sub);

                                } else {
                                    Toast.makeText(UploadDocumentActivity.this, "Data Not Found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    Toast.makeText(UploadDocumentActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        firebaseStorageDocumentuplod = FirebaseStorage.getInstance().getReference().child(getString(R.string.storage_image)).child("Documents");

    }

    private void clickActionEvent() {
        imageViewAtaachament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                checkPermisionAndDoocument();
            }
        });


        buttonUploadDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validation();

            }
        });

    }

    class UploadAsyncTask extends AsyncTask <String, Integer, Integer> {

        NotificationManagerCompat notificationManager;
        int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;
        NotificationCompat.Builder mBuilder;

        int notificationId = new Random().nextInt(60000);
        Uri imageUri, documentUriFor, documentUri;


        UploadAsyncTask(Uri imageUri, Uri documentUriFor, Uri documentUri) {
            this.imageUri = imageUri;
            this.documentUri = documentUri;
            this.documentUriFor = documentUriFor;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            notificationManager = NotificationManagerCompat.from(getApplicationContext());
            mBuilder = new NotificationCompat.Builder(getApplicationContext(), "Suraj");
            mBuilder.setContentTitle("Document Uploading")
                    .setContentText("Uploading in Progress")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);

            notificationManager.notify(notificationId, mBuilder.build());
        }


        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            editTextTopic.setText("");
            editTextDecription.setText("");


            Picasso.get().load(R.drawable.ic_attach_file_black_24dp).placeholder(R.drawable.ic_attach_file_black_24dp).into(imageViewAtaachament);

        }

        @Override
        protected Integer doInBackground(String... strings) {
            documentupload(strings[0], strings[1], strings[2], strings[3], mBuilder, notificationId, PROGRESS_CURRENT, PROGRESS_MAX, notificationManager, imageUri, documentUriFor, documentUri);

            return 0;
        }


    }

    private void checkPermisionAndDoocument() {
        if (mStoragePermission) {
            CharSequence options[] = new CharSequence[]{

                    "PPT",
                    "Image",
                    "PDF"
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(UploadDocumentActivity.this)
                    .setTitle("Select Option");

            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (which == 0) {

                        Intent i = new Intent();
                        i.setType("application/vnd.ms-powerpoint");
                        i.setAction(Intent.ACTION_GET_CONTENT);
                        i.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(i, FILE_PICKER_REQUEST_PPT);
                    } else if (which == 1) {
                        Intent intentGallary = new Intent();
                        intentGallary.setAction(Intent.ACTION_GET_CONTENT);
                        CropImage.activity()
                                .start(UploadDocumentActivity.this);

                    } else {
                        Intent i = new Intent();
                        i.setType("application/pdf");
                        i.setAction(Intent.ACTION_GET_CONTENT);
                        i.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(i, FILE_PICKER_REQUEST_CODE);
                    }

                }

            });

            builder.show();
        } else
            verifyStoragePermission();
    }

    private void validation() {
        String Topic = editTextTopic.getText().toString().trim();
        String Decscription = editTextDecription.getText().toString();
        String Subjects = spinnerSubject.getSelectedItem().toString();
        String Units = spinnerUnit.getSelectedItem().toString();
        if (TextUtils.isEmpty(Topic)) {
            editTextTopic.requestFocus();
            editTextTopic.setError("Topic is necessary");
        } else if (TextUtils.isEmpty(Subjects)) {
            spinnerSubject.requestFocus();

            Toast.makeText(UploadDocumentActivity.this, "Please Select Subject please", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Units)) {
            spinnerUnit.requestFocus();

            Toast.makeText(UploadDocumentActivity.this, "Please Select unit", Toast.LENGTH_SHORT).show();
        } else if (imageUri == null && documentUriFor == null && documentUri == null) {
            Toast.makeText(UploadDocumentActivity.this, "Please Upload A document by click on attach icon", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(UploadDocumentActivity.this, "Uploading Start", Toast.LENGTH_SHORT).show();
            new UploadAsyncTask(imageUri, documentUriFor, documentUri).execute(Topic, Decscription, Subjects, Units);
        }
    }


    private void documentupload(final String topic, final String decscription, final String subjects, final String units, final NotificationCompat.Builder mBuilder, final int notificationId, final int PROGRESS_CURRENT, final int PROGRESS_MAX, final NotificationManagerCompat notificationManager, Uri imageUri, Uri documentUriFor, Uri documentUri) {
//        progressDialog.setTitle("Document Uploading");
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();



        Uri dataUri = null;
        String extension = null;
        String DocumentType = "image";
        final String saveCurrentDate, saveCurrenttime;

        if (documentUriFor != null) {
            dataUri = documentUriFor;
            extension = Dtype;
            DocumentType = "ppt";
        }
        if (documentUri != null) {
            dataUri = documentUri;
            DocumentType = "pdf";
            extension = ".pdf";
        }
        if (imageUri != null) {
            dataUri = imageUri;
            extension = ".jpg";
            DocumentType = "image";
        }

        Calendar calendardate = Calendar.getInstance();
        final SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calendardate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrenttime = currentTime.format(calendardate.getTime());
        //TODO work for others types of docs.
        final StorageReference filepath = firebaseStorageDocumentuplod.child(CURRENT_USER + saveCurrentDate + saveCurrenttime + extension);
        final String finalDocumentType = DocumentType;
        UploadTask uploadTask = filepath.putFile(dataUri);


        uploadTask.addOnProgressListener(UploadDocumentActivity.this, new OnProgressListener <UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "onProgress: Upload is " + progress + "% done");
                int currentprogress = (int) progress;


                mBuilder.setProgress(PROGRESS_MAX, currentprogress, false)
                        .setContentText("Uploading Progress in " + currentprogress + "% done");
                notificationManager.notify(notificationId, mBuilder.build());

            }
        });



        uploadTask.continueWithTask(new Continuation <UploadTask.TaskSnapshot, Task <Uri>>() {
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
                    String db;
                    DatabaseReference user_message_ref;
                    if (userType.equals(ConstantForApp.STUDENT)) {
                        db = "StudentUplodedDocument";

                        user_message_ref = databaseReference.child(db).child(classSubject).child(units).child(CURRENT_USER).push();

                    } else {
                        db = "TeacherUplodedDocument";

                         user_message_ref = databaseReference.child(db).child(classSubject).child(units).push();
                    }
                    final String messagePushid = user_message_ref.getKey();
                    HashMap hashMap = new HashMap();

                    hashMap.put("TopicName", topic);
                    hashMap.put("DocumentType", finalDocumentType);
                    hashMap.put("Description", decscription);
                    hashMap.put(getString(R.string.user_user_type), userType);
                    hashMap.put(getString(R.string.user_uid), CURRENT_USER);
                    hashMap.put(getString(R.string.teacher_uploded_document), downloadUri.toString());
                    hashMap.put("UploadingDate", saveCurrentDate);
                    hashMap.put("UploadingTime", saveCurrenttime);
                    hashMap.put("Subject", classSubject);
                    hashMap.put("Unit", units);
                    hashMap.put("Key", messagePushid);


                    user_message_ref.updateChildren(hashMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {

                                        mBuilder.setContentTitle("Document Uploaded")
                                                .setAutoCancel(true);
                                        Log.d(TAG, "onComplete: on complete");

                                        mBuilder.setProgress(PROGRESS_MAX, 100, false)
                                                .setContentText("Uploading complete of " + topic);

                                        notificationManager.notify(notificationId, mBuilder.build());
                                        progressDialog.dismiss();
                                        Toast.makeText(UploadDocumentActivity.this, "Document Upload Successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.dismiss();

                                        Toast.makeText(UploadDocumentActivity.this, "Something Went wrong", Toast.LENGTH_SHORT).show();
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                            Log.d(TAG, "Upload Document exception: " + Objects.requireNonNull(task.getException()).getMessage());
                                        }
                                    }
                                }
                            });

                }
            }
        });

    }

    private void verifyStoragePermission() {

        String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permission[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permission[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permission[2]) == PackageManager.PERMISSION_GRANTED) {
            mStoragePermission = true;
            checkPermisionAndDoocument();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    permission,
                    REQUEST_CODE

            );
        }
    }

    private void viewIntialization() {

        spinnerSubject = findViewById(R.id.spinner_subject);

        spinnerUnit = findViewById(R.id.spinner_unit);
        imageViewAtaachament = findViewById(R.id.imageView_attatchment);
        editTextTopic = findViewById(R.id.editText_topic_name);
        editTextDecription = findViewById(R.id.editText_description);
        buttonUploadDocument = findViewById(R.id.button_submit_upload);
        progressDialog = new ProgressDialog(this);
    }

    private void actionBarDesinging() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Upload Document");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == FILE_PICKER_REQUEST_PPT && resultCode == RESULT_OK) {

            documentUriFor = data.getData();
            Dtype = ".ppt";
            Picasso.get().load(R.drawable.powerpoint).resize(200, 200).noFade().into(imageViewAtaachament);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imageUri = result.getUri();

                imageViewAtaachament.setImageURI(imageUri);
            }
        }

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {

            documentUri = data.getData();
            Dtype = ".pdf";
            Picasso.get().load(R.drawable.images).into(imageViewAtaachament);

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {

                if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0 && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    checkPermisionAndDoocument();
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
//                }
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
//                } else {
//
//                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
//                }
//                if (grantResults.length > 0
//                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
//
//
//                } else
//                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
//                if (grantResults.length > 0
//                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                } else
//                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                }

                // other 'case' lines to check for other
                // permissions this app might request.
            }
        }
    }


}


//    private void uploadNewPhoto(Uri imageUri) {
//        BackGroundImageResizer backGroundImageResizer= new BackGroundImageResizer(null);
//        .execute(imageUri);
//
//    }
//    private void uploadNewPhoto(Bitmap imageBitmap) {
//        BackGroundImageResizer backGroundImageResizer= new BackgroundImageResizer(imageBitmap);
//        Uri uri= null;
//        resize.execute(uri);
//    }
//
//    public  class  BackGroundImageResizer extends AsyncTask<Uri,Integer,byte[]>{
//
//
//        Bitmap bitmap;
//        public  BackGroundImageResizer(Bitmap bp){
//
//            if(bp != null)
//            bitmap = bp;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            Toast.makeText(UploadDocumentActivity.this, "Compresing Image", Toast.LENGTH_SHORT).show();
//
//        }
//
//        @Override
//        protected void onPostExecute(byte[] bytes) {
//            super.onPostExecute(bytes);
//            bitmap= bytes;
//        }
//
//        @Override
//        protected byte[] doInBackground(Uri... uris) {
//
//            if(imageBitmap == null) {
//                try {
//                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uris[0]);
//                    Log.d(TAG, "doInBackground : bitmp size : mb : " + imageBitmap.getByteCount() / MB + "MB");
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//                byte[] bytes = null;
//                for(int i = 1; i<11;i++){
//                    if(i==10){
//                        Toast.makeText(UploadDocumentActivity.this, "That Image is to large.", Toast.LENGTH_SHORT).show();
//                        break;
//                    }
//                    bytes= getBytesFromBitmap(imageBitmap,100/i);
//                    Log.d(TAG,"doInBackground : megaBytes : (" + ( 11-i) + "0%)"+ bytes.length/MB+"MB");
//                    if(bytes.length/MB <MB_THRESHOLD){
//                        return bytes;
//                    }
//                }
//
//                return bytes;
//            }
//
//
//
//    }
//
//    private byte[] getBytesFromBitmap(Bitmap imageBitmap, int quality) {
//        ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
//        imageBitmap.compress(Bitmap.CompressFormat.JPEG,quality,byteArrayOutputStream);
//
//        return byteArrayOutputStream.toByteArray();
//    }