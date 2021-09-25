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
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rwn.rwnstudy.R;
import com.rwn.rwnstudy.utilities.StatusGetterSetter;
import com.rwn.rwnstudy.utilities.UserGetterAndSetter;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatusImageShowActivity extends AppCompatActivity {
    RecyclerView recyclerViewStatusView;
    String friendId,saveCurrentDate,saveCurrentTime;
    String node, CURRENT_USER;

    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    String saveCurrent;
    StorageReference firebaseStorageDocumentuplod;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_image_show);

        fullScreen();
        getttingPendingIntent();
        viewIntializing();
        Calendar calendardate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd");
        saveCurrentDate = currentDate.format(calendardate.getTime());
        SimpleDateFormat saveCurrentTime = new SimpleDateFormat("hh:mm a");
        saveCurrent= saveCurrentTime.format(calendardate.getTime());

        databseConnenting();
        showOnRecyclerView();
    }
    public void fullScreen() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
        } else {
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }
    private void showOnRecyclerView() {


        Query sortPostinDecendingOrder = databaseReference.child("Status").child(node).child(friendId);
        FirebaseRecyclerOptions <StatusGetterSetter> options =
                new FirebaseRecyclerOptions.Builder <StatusGetterSetter>()
                        .setQuery(sortPostinDecendingOrder, StatusGetterSetter.class)
                        .build();
        FirebaseRecyclerAdapter <StatusGetterSetter, ImageViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter <StatusGetterSetter, ImageViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull  ImageViewHolder holder, int position, @NonNull  StatusGetterSetter model) {


                        holder.linearLayout.setVisibility(View.GONE);
                        final String postKey = getRef(position).getKey();
                        Log.d("postKey", "onBindViewHolder: "+postKey);
                        final String imageLink= model.getLink();
                        String date= model.getDate();
                        String hour= model.getHour();
                        String min= model.getMin();
                        final  StatusGetterSetter model1=model;
                        final ImageViewHolder holder1=holder;
                        final int position1= position;
                        databaseReference.child("StatusViewer").child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    int a= (int) dataSnapshot.getChildrenCount();
                                    holder1.imageViewEye.setText(" "+a);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        if(friendId.equals(CURRENT_USER)){
                            holder.imageViewEye.setVisibility(View.VISIBLE);
                            holder.linearLayout.setVisibility(View.GONE);
                        }
                        else{

                        }




                        Picasso.get().load(imageLink).into( holder.imageView);
                        if(saveCurrentDate.equals(date)) {
                            holder.textViewtime.setText("Today :- "+hour+":"+min);
                        }else
                        {

                            holder.textViewtime.setText("Yesterday :- "+hour+":"+min);
                        }
                        holder.textViewCaption.setText(model.getCaption());

                        final ImageViewHolder finalHolder = holder;
                        databaseReference.child("Users").child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    UserGetterAndSetter userGetterAndSetter= dataSnapshot.getValue(UserGetterAndSetter.class);

                                    Picasso.get().load(userGetterAndSetter.getProfileImage()).resize(100, 100).noFade().into(finalHolder.circleImageView);
                                    holder1.textViewName.setText(userGetterAndSetter.getName());




                                    holder1.imageViewEye.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {


                                            final String postKey = getRef(position1).getKey();
                                            Intent intent = new Intent(StatusImageShowActivity.this,SelfStatusViewer.class);
                                            intent.putExtra("Key",postKey);
                                            startActivity(intent);


                                        }
                                    });







                                    if(!(model1.getUid().equals(CURRENT_USER))){
                                        databaseReference.child("StatusViewer").child(postKey).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if(dataSnapshot.hasChild(CURRENT_USER)){

                                                }
                                                else{

                                                    databaseReference.child("StatusViewer").child(postKey).child(CURRENT_USER).setValue(saveCurrent);
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

                        holder.linearLayoutnext.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                recyclerViewStatusView.smoothScrollToPosition(position1+1);
                                Log.d("TEXTNGM", "onClick: done");
                            }
                        });
                        holder.linearLayoutpre.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(position1!= 0)
                                    recyclerViewStatusView.smoothScrollToPosition(position1-1);
                                Log.d("TEXTNGM", "onClick: done");
                            }
                        });

//                        holder.buttonSend.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                               String comment= holder.editTextCommemnt.getText().toString();
//                               if(comment.equals("")){
//
//                               }
//                               else{
//
//                                DatabaseReference userref=   databaseReference.child("Messages").child(friendId).child(CURRENT_USER).push();
//                                String pushkey=userref.getKey();
//
//                                   DatabaseReference databaseReferenceSenderMessage = databaseReference.child("Messages").child(CURRENT_USER).child(friendId);
//                                   final DatabaseReference databaseReferenceRecieverMessage = databaseReference.child("Messages").child(friendId).child(CURRENT_USER);
//
//
//                                   DatabaseReference user_message_ref = databaseReferenceSenderMessage.push();
//                                   final String messagePushid = user_message_ref.getKey();
//
//                                   Calendar calendar = Calendar.getInstance();
//                                   SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
//                                   saveCurrentDate = currentDate.format(calendar.getTime());
//                                   SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm  aa");
//                                   saveCurrentTime = currentTime.format(calendar.getTime());
//
//                                   final HashMap messageTextBody = new HashMap();
//                                   messageTextBody.put("Message", comment);
//                                   messageTextBody.put("Date", saveCurrentDate);
//                                   messageTextBody.put("Time", saveCurrentTime);
//                                   messageTextBody.put("Type", "status");
//                                   messageTextBody.put("image", model1.getLink());
//                                   messageTextBody.put("From", CURRENT_USER);
//                                   messageTextBody.put("MsgId", messagePushid);
//                                   messageTextBody.put("To", friendId);
//                                   messageTextBody.put("isSeen", "false");
//
//
//                                   databaseReferenceSenderMessage.child(messagePushid).updateChildren(messageTextBody)
//                                           .addOnCompleteListener(new OnCompleteListener() {
//                                               @Override
//                                               public void onComplete(@NonNull Task task) {
//                                                   if (task.isSuccessful()) {
//
//                                                       databaseReferenceRecieverMessage.child(messagePushid).updateChildren(messageTextBody).addOnCompleteListener(new OnCompleteListener() {
//                                                           @Override
//                                                           public void onComplete(@NonNull Task task) {
//                                                               if (task.isSuccessful()) {
//                                                                            holder.editTextCommemnt.setText("");
//                                                                   Toast.makeText(StatusImageShowActivity.this, "Message Send", Toast.LENGTH_SHORT).show();
//                                                               } else {
//
//
//                                                                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                                                                       Toast.makeText(StatusImageShowActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
//                                                                   }
//                                                               }
//                                                           }
//                                                       });
//                                                   }
//                                               }
//                                           });
//
//                               }
//                            }
//                        });

                    }

                    @NonNull
                    @Override
                    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.image_layout, parent, false);
                        ImageViewHolder postViewHolder = new ImageViewHolder(view);
                        return postViewHolder;
                    }
                };
        recyclerViewStatusView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        EditText editTextCommemnt;
        ImageView buttonSend;
        TextView textViewName, textViewtime,textViewCaption;
        CircleImageView circleImageView;
        ImageView imageView;
        TextView imageViewEye;
        LinearLayout linearLayout,linearLayoutnext,linearLayoutpre;

        public ImageViewHolder(View itemView) {

            super(itemView);
            imageView = itemView.findViewById(R.id.ImageView_image);
            circleImageView = itemView.findViewById(R.id.circularView_student_profile);
            textViewName = itemView.findViewById(R.id.textView_chatting_userProfile_Fullname);
            textViewtime = itemView.findViewById(R.id.textView_Satus);
            editTextCommemnt = itemView.findViewById(R.id.editText_status_text);
            buttonSend = itemView.findViewById(R.id.imageView_send);
            textViewCaption = itemView.findViewById(R.id.textView_caption);
            imageViewEye= itemView.findViewById(R.id.imageView_eyeView);
            linearLayout= itemView.findViewById(R.id.linearlayout_Status);
            linearLayoutnext = itemView.findViewById(R.id.linearlayout_next);
            linearLayoutpre = itemView.findViewById(R.id.linearlayout_pre);
        }
    }

    private void databseConnenting() {

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        CURRENT_USER = mAuth.getCurrentUser().getUid();
        firebaseStorageDocumentuplod = FirebaseStorage.getInstance().getReference().child("Images").child("StatusImages").child(CURRENT_USER);



    }

    private void viewIntializing() {
        recyclerViewStatusView = findViewById(R.id.recyclerView_image);
        recyclerViewStatusView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(StatusImageShowActivity.this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        recyclerViewStatusView.setLayoutManager(linearLayoutManager);

    }

    private void getttingPendingIntent() {
        Intent intent = getIntent();
        friendId = intent.getStringExtra("friendsId");
        node = intent.getStringExtra("node");

    }
}
