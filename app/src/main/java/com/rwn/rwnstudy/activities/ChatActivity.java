package com.rwn.rwnstudy.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
import com.rwn.rwnstudy.utilities.MessageAdapter;
import com.rwn.rwnstudy.utilities.MessageGetterSetter;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private ImageView imageButtonSendMessage, imageViewButtonsendImage;
    private String messageRecieverId, messageRecievername, messageSenderId, saveCurrentDate, saveCurrentTime;
    EditText editTextChatBox;
    ChildEventListener valueEventListener;
    ChildEventListener valueEventListener1;
    static final int REQUEST_CODE = 3;

    String Device_token;
    Boolean mStoragePermission = false;
    private final List <MessageGetterSetter> messagelist = new ArrayList <>();
    private final List <String> messagelistKey = new ArrayList <>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;

    private StorageReference firebaseStorageDocumentuplod;
    TextView textViewRecievername;
    CircleImageView circleImageViewRecieverProfileImage;
    DatabaseReference databaseReferenceRootRef, databaseReferenceUserRef, databaseReferenceSenderMessage, databaseReferenceRecieverMessage;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    StorageReference storageReferenceChatImages;
    TextView textViewLastSeen;
    RecyclerView recyclerViewUserMessageList;

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        viewIntializing();


        oStart();


    }

    private void viewIntializing() {


        mAuth = FirebaseAuth.getInstance();
        messageSenderId = mAuth.getCurrentUser().getUid();

        databaseReferenceRootRef = FirebaseDatabase.getInstance().getReference();
        databaseReferenceUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReferenceChatImages = FirebaseStorage.getInstance().getReference().child("ChatsImages").child(messageSenderId).child(messageSenderId + "-_-" + messageRecieverId);


        progressDialog = new ProgressDialog(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom, null);
        actionBar.setCustomView(action_bar_view);

        messageRecieverId = getIntent().getExtras().get("visit_user_id").toString();
        messageRecievername = getIntent().getExtras().get("username").toString();

        updateCurrentStatus("online");
        imageButtonSendMessage = findViewById(R.id.imageButton_send_message);
        imageViewButtonsendImage = findViewById(R.id.imageButton_send_Image);
        editTextChatBox = findViewById(R.id.editText_chatMessage);


        circleImageViewRecieverProfileImage = findViewById(R.id.circularView_Chat_Reciverprofile_image);
        textViewRecievername = findViewById(R.id.textView_chat_RecieverName);
        textViewLastSeen = findViewById(R.id.textView_chat_lastSeen);


        recyclerViewUserMessageList = findViewById(R.id.recyclerView_privateMessage);


        messageAdapter = new MessageAdapter(messagelist,messageRecieverId);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewUserMessageList.setHasFixedSize(true);
        recyclerViewUserMessageList.setLayoutManager(linearLayoutManager);
        recyclerViewUserMessageList.setAdapter(messageAdapter);

        databaseReferenceSenderMessage = databaseReferenceRootRef.child("Messages").child(messageSenderId).child(messageRecieverId);
        // databaseReferenceSenderMessage.keepSynced(true);
        databaseReferenceRecieverMessage = databaseReferenceRootRef.child("Messages").child(messageRecieverId).child(messageSenderId);
        //   databaseReferenceRecieverMessage.keepSynced(true);


        displayRecieverInfo();


        editTextChatBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    imageButtonSendMessage.setEnabled(true);
                } else {
                    imageButtonSendMessage.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editTextChatBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});


        imageButtonSendMessage.setEnabled(false);

        imageViewButtonsendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkPermisionAndDoocument();

            }
        });

        imageButtonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


        //seenMessage(messageRecieverId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        seenMessageForMe(messageSenderId);
        seenMessage(messageSenderId);


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener <InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task <InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new Instance ID token
                        Device_token = task.getResult().getToken();

                        databaseReferenceRootRef.child("Users").child(messageSenderId).child("deviceToken").setValue(Device_token);

                    }
                });

    }

    public void updateCurrentStatus(String state) {
        String saveCurrentDate, saveCurrentTime;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        Calendar calendarTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendarTime.getTime());

        HashMap hashMapCurrentState = new HashMap();
        hashMapCurrentState.put("Date", saveCurrentDate);
        hashMapCurrentState.put("Time", saveCurrentTime);
        hashMapCurrentState.put("Type", state);

        databaseReferenceUserRef.child(messageSenderId).child("UserState").updateChildren(hashMapCurrentState);


    }


    protected void oStart() {


        messagelist.clear();
        messagelistKey.clear();
        databaseReferenceRootRef.child("Messages").child(messageSenderId).child(messageRecieverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        MessageGetterSetter messageGetterSetter = dataSnapshot.getValue(MessageGetterSetter.class);
                        messagelist.add(messageGetterSetter);
                        messagelistKey.add(messageGetterSetter.getMsgId());
                        messageAdapter.notifyDataSetChanged();
                        recyclerViewUserMessageList.scrollToPosition(messagelist.size() - 1);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        MessageGetterSetter messageGetterSette =dataSnapshot.getValue(MessageGetterSetter.class);


                        String key=dataSnapshot.getKey();

                        int index=messagelistKey.indexOf(key);
                        messagelist.set(index,messageGetterSette); //TODO crash analisis
                        messageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        String key=dataSnapshot.getKey();

                        int index=messagelistKey.indexOf(key);
                        messagelist.remove(index);
                        messagelistKey.remove(index);
                        messageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void displayRecieverInfo() {
        textViewRecievername.setText(messageRecievername);
        databaseReferenceUserRef
                .child(messageRecieverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String profileImage;
                final String type;
                final String lastDate;
                final String lastTime;
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("profileImage")) {
                        profileImage = dataSnapshot.child("profileImage").getValue().toString();
                        Picasso.get().load(profileImage).resize(100, 100).noFade().into(circleImageViewRecieverProfileImage);
                    }
                    if (dataSnapshot.hasChild("UserState")) {
                        type = dataSnapshot.child("UserState").child("Type").getValue().toString();
                        lastDate = dataSnapshot.child("UserState").child("Date").getValue().toString();
                        lastTime = dataSnapshot.child("UserState").child("Time").getValue().toString();

                        if (type.equals("Online")) {
                            textViewLastSeen.setText("online");

                        } else {
                            textViewLastSeen.setText("last seen: " + lastTime + " " + lastDate);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    private void sendMessage() {
        String message = editTextChatBox.getText().toString();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "please type a message first", Toast.LENGTH_SHORT).show();
        } else {


            editTextChatBox.setText("");

            DatabaseReference user_message_ref = databaseReferenceSenderMessage.push();
            final String messagePushid = user_message_ref.getKey();

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate = currentDate.format(calendar.getTime());
            SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm  aa");
            saveCurrentTime = currentTime.format(calendar.getTime());

            final HashMap messageTextBody = new HashMap();
            messageTextBody.put("Message", message);
            messageTextBody.put("Date", saveCurrentDate);
            messageTextBody.put("Time", saveCurrentTime);
            messageTextBody.put("Type", "text");
            messageTextBody.put("From", messageSenderId);
            messageTextBody.put("MsgId", messagePushid);
            messageTextBody.put("To", messageRecieverId);
            messageTextBody.put("isSeen", "false");


            databaseReferenceSenderMessage.child(messagePushid).updateChildren(messageTextBody)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {

                                databaseReferenceRecieverMessage.child(messagePushid).updateChildren(messageTextBody).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {

                                        } else {


                                            Toast.makeText(ChatActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });

        }
    }


    private void checkPermisionAndDoocument() {
        if (mStoragePermission) {
            Intent intentGallary = new Intent();
            intentGallary.setAction(Intent.ACTION_GET_CONTENT);
            CropImage.activity()
                    .start(ChatActivity.this);
        } else
            verifyStoragePermission();
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Toast.makeText(this, "Image will upload and visible soon automatic be paitence", Toast.LENGTH_LONG).show();
                Uri imageUri = result.getUri();

                Calendar calendardate = Calendar.getInstance();
                final SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                saveCurrentDate = currentDate.format(calendardate.getTime());

                SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
                saveCurrentTime = currentTime.format(calendardate.getTime());
                //TODO work for others types of docs.
                final StorageReference filepath = storageReferenceChatImages.child(messageSenderId + saveCurrentDate + saveCurrentTime + ".jpg");
                final String finalDocumentType = "image";
                UploadTask uploadTask = filepath.putFile(imageUri);


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
                            Uri downloadUri = task.getResult();


                            DatabaseReference user_message_ref = databaseReferenceSenderMessage.push();
                            final String messagePushid = user_message_ref.getKey();


                            final HashMap messageTextBody = new HashMap();


                            messageTextBody.put("Message", " ");
                            messageTextBody.put("image", downloadUri.toString());
                            messageTextBody.put("Date", saveCurrentDate);
                            messageTextBody.put("Time", saveCurrentTime);
                            messageTextBody.put("Type", "image");
                            messageTextBody.put("From", messageSenderId);
                            messageTextBody.put("MsgId", messagePushid);
                            messageTextBody.put("To", messageRecieverId);
                            messageTextBody.put("isSeen", "false");


                            databaseReferenceSenderMessage.child(messagePushid).updateChildren(messageTextBody)
                                    .addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()) {

                                                databaseReferenceRecieverMessage.child(messagePushid).updateChildren(messageTextBody).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {
                                                        if (task.isSuccessful()) {

                                                        } else {


                                                            Toast.makeText(ChatActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });


                        }
                    }
                });


            }
        }


    }


//
//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(final AdapterView <?> parent, View view, final int position, long id) {
//
//
//
//
//
//
//
//
//
//
//
//
//
//                CharSequence options[] = new CharSequence[]{
//                        "Delete"};
//                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
//
//                builder.setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        if (which == 0) {
//
//
//                            Object s= parent.getItemAtPosition(position);
//                            MessageGetterSetter messageGetterSetter = (MessageGetterSetter)s;
//                            String messageid=messageGetterSetter.getMsgId();
//                            databaseReferenceChatRef.child(messageid).removeValue().addOnCompleteListener(new OnCompleteListener <Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task <Void> task) {
//
//                                    messagesList.remove(position);
//                                    mMessageAdapter.notifyDataSetChanged();
//                                }
//                            });
//
//                        }
//
//                    }
//
//                });
//
//                builder.show();
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//                return true;
//            }
//        });


//        mChildEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//
//
//                MessageGetterSetter messageGetterSetter = dataSnapshot.getValue(MessageGetterSetter.class);
//
//                mMessageAdapter.add(messageGetterSetter);
//                mMessageAdapter.notifyDataSetChanged();
//            }
//
//
//
//
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                String key = dataSnapshot.getKey();
//                int index = messagesList.indexOf(key);
//
//                if (index != -1) {
//                    messagesList.remove(index);
//                    mMessageAdapter.notifyDataSetChanged();
//            }
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };

    //   databaseReferenceChatRef.addChildEventListener(mChildEventListener);


    private void seenMessageForMe(final String userId) {
        valueEventListener1 = databaseReferenceSenderMessage.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("Message")) {
                        MessageGetterSetter messageGetterSetter = dataSnapshot.getValue(MessageGetterSetter.class);

                        Log.d("bill", "onDataChange: " + messageGetterSetter.toString());
                        String from = messageGetterSetter.getFrom();

                        if (!from.equals(messageSenderId)) {
                            HashMap hashMap = new HashMap();
                            hashMap.put("isSeen", "true");
                            dataSnapshot.getRef().updateChildren(hashMap);
                        }
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void seenMessage(final String userId) {
        valueEventListener = databaseReferenceRecieverMessage.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){


                    MessageGetterSetter messageGetterSetter = dataSnapshot.getValue(MessageGetterSetter.class);

                    if(dataSnapshot.child("isSeen").getValue().toString().equals("false")) {
                        Log.d("bill", "onDataChange: " + messageGetterSetter.toString());
                        String from = messageGetterSetter.getFrom();
                        if (!from.equals(userId)) {/// crashing point //TODO crash analisis
                            HashMap hashMap = new HashMap();
                            hashMap.put("isSeen", "true");
                            dataSnapshot.getRef().updateChildren(hashMap);
                        }
                    }


                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        databaseReferenceSenderMessage.addChildEventListener(valueEventListener1);
        databaseReferenceRecieverMessage.addChildEventListener(valueEventListener);
        updateCurrentStatus("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReferenceSenderMessage.removeEventListener(valueEventListener1);
        databaseReferenceRecieverMessage.removeEventListener(valueEventListener);

        updateCurrentStatus("Offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReferenceSenderMessage.removeEventListener(valueEventListener1);
        databaseReferenceRecieverMessage.removeEventListener(valueEventListener);
        updateCurrentStatus("Offline");
    }
}