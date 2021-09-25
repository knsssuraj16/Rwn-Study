package com.rwn.rwnstudy.activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rwn.rwnstudy.R;
import com.rwn.rwnstudy.utilities.ConstantForApp;
import com.rwn.rwnstudy.utilities.UserGetterAndSetter;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class StatusUploadActivity extends AppCompatActivity {
    EditText editText;
    ImageView imageView, imageViewSend;
    Uri uri, downloadUri;
    String CURRENT_USER, userType;
    String Text;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    private Bitmap thumb_bitmap= null;
    String saveCurrentDate, saveCurrenttime,saveCurrenttimeInMinutes;

    private DatabaseReference databaseReferenceUserRef, databaseReference;
    private StorageReference firebaseStorageDocumentuplod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_upload);

        imageViewSend = findViewById(R.id.imageView_send);
        imageView = findViewById(R.id.imageView_picture);
        editText = findViewById(R.id.editText_status_text);




        progressDialog = new ProgressDialog(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        imageView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        imageView.setAdjustViewBounds(false);
        //  imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Intent intent = getIntent();
        String image = intent.getStringExtra("Uri");
        uri = Uri.parse(image);
        Picasso.get().load(uri).noFade().into(imageView);


        imageViewSend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                new MyAsyncTask().execute(uri);

            }
        });

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void clickAction() {


//        progressDialog.setTitle("Uploading");
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();

        Text = editText.getText().toString();

        mAuth = FirebaseAuth.getInstance();
        CURRENT_USER = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        databaseReferenceUserRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.datbase_user_node)).child(CURRENT_USER);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        firebaseStorageDocumentuplod = FirebaseStorage.getInstance().getReference().child("Images").child("StatusImages").child(CURRENT_USER);

        databaseReferenceUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserGetterAndSetter userGetterAndSetter = dataSnapshot.getValue(UserGetterAndSetter.class);
                String userType = userGetterAndSetter.getUserType();
                if (userType.equals(ConstantForApp.STUDENT)) {

                    String course = userGetterAndSetter.getCourse();
                    String semester = userGetterAndSetter.getSemester();
                    DatabaseReference node = databaseReference.child("Status").child("Students");

                    sendDataToDatabase(node);
                } else {
                    DatabaseReference node = databaseReference.child("Status").child("Teachers");

                    sendDataToDatabase(node);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendDataToDatabase(final DatabaseReference node) {

        Calendar calendardate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd");
        saveCurrentDate = currentDate.format(calendardate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH");
        saveCurrenttime = currentTime.format(calendardate.getTime());


        SimpleDateFormat currentDatefor = new SimpleDateFormat("dd-MMMM-yyyy");
        String cuDate = currentDatefor.format(calendardate.getTime());

        SimpleDateFormat currentTimefor = new SimpleDateFormat("HH:mm:ss");
        String cuTime = currentTimefor.format(calendardate.getTime());

        SimpleDateFormat currentminutes = new SimpleDateFormat("mm");
        saveCurrenttimeInMinutes = currentminutes.format(calendardate.getTime());
        final String storagePath= CURRENT_USER + cuDate + cuTime + ".jpg";

        final StorageReference filepath = firebaseStorageDocumentuplod.child(storagePath);





        File thumb_file_path = new File (uri.getPath());

        try{
            thumb_bitmap=new Compressor(StatusUploadActivity.this)
                    .setMaxWidth(400)
                    .setMaxHeight(400)
                    .setQuality(70)
                    .compressToBitmap(thumb_file_path);


        }
        catch(IOException e){
            e.printStackTrace();

        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,70,byteArrayOutputStream);
        final byte thum_byte []= byteArrayOutputStream.toByteArray();




        filepath.putBytes(thum_byte).continueWithTask(new Continuation <UploadTask.TaskSnapshot, Task <Uri>>() {
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




                    DatabaseReference user_message_ref = node.child(CURRENT_USER).push();
                    final String messagePushid = user_message_ref.getKey();
                    HashMap hashMap = new HashMap();

                    hashMap.put("caption", Text);
                    hashMap.put(getString(R.string.user_uid), CURRENT_USER);
                    hashMap.put("link", downloadUri.toString());
                    hashMap.put("date", saveCurrentDate);
                    hashMap.put("hour", saveCurrenttime);
                    hashMap.put("min", saveCurrenttimeInMinutes);
                    hashMap.put("key", messagePushid);


                    user_message_ref.updateChildren(hashMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(StatusUploadActivity.this, "Status upload Succesfully", Toast.LENGTH_SHORT).show();


                                        finish();
                                    } else {
                                        progressDialog.dismiss();

                                        Toast.makeText(StatusUploadActivity.this, "Something Went wrong", Toast.LENGTH_SHORT).show();
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        }
                                    }
                                }
                            });

                }
            }
        });


    }
    private class MyAsyncTask extends AsyncTask<Uri, String, Bitmap> {
        @Override
        protected Bitmap doInBackground(Uri... uris) {
            clickAction();
            return null;
        }

        protected void onPreExecute() {
            Log.d("TOTOD", "onPreExecute: before" );
            setResult(57);
            finish();

            Log.d("TOTOD", "onPreExecute: after" );
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            setResult(4);

            Log.d("TOTOD", "onPostExecute: Execute" );
        }
        //        protected Bitmap doInBackground(String... strings) {
//            // Some long-running task like downloading an image.
//        //    Bitmap = downloadImageFromUrl(strings[0]);
//         return someBitmap;
    }



}

