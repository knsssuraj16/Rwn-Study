package com.rwn.rwnstudy.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.rwn.rwnstudy.utilities.ConstantForApp;
import com.rwn.rwnstudy.utilities.FriendRequestGetterSetter;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView textViewFriendRequests;
    DatabaseReference databaseReferenceFriendReqRef, databaseReferencePostRef, databaseReferenceUserRef, databaseReferenceFriendRef, databaseReferenceFriendRequest;
    FirebaseAuth mAuth;
    String receiverUserId, CURRENT_USER;
    private String saveCurrentDate;

    TextView textView;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Documents Access Request");

        }


        progressDialog = new ProgressDialog(this);
        viewIntialization();
        datbaseLinkUp();
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

    private void viewIntialization() {
        textViewFriendRequests = findViewById(R.id.textView_friendRequest);
        recyclerView = findViewById(R.id.recyclerView_FriendRequest);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FriendRequestActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        textView = findViewById(R.id.textView_send_friend_request);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToFriendPage();
            }
        });
    }

    private void sendToFriendPage() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(CURRENT_USER).child("userType").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String user = dataSnapshot.getValue(String.class);


                    if (user.equals(ConstantForApp.TEACHER)) {
                        databaseReferenceUserRef.child(CURRENT_USER).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                final String dep =dataSnapshot.child("department").getValue().toString();
                                Intent intent = new Intent(FriendRequestActivity.this, OpenclassDetailActivity.class);
                                intent.putExtra("classWithSection", dep);
                                intent.putExtra("teacher", "teacher");
                                startActivity(intent);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        startActivity(new Intent(FriendRequestActivity.this, AskForDocsActivity.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void datbaseLinkUp() {
        mAuth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            CURRENT_USER = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        }

        CURRENT_USER = mAuth.getCurrentUser().getUid();
        databaseReferenceUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReferenceFriendReqRef = FirebaseDatabase.getInstance().getReference().child("DocumentAcccessRequest").child(CURRENT_USER);
        databaseReferenceFriendRef = FirebaseDatabase.getInstance().getReference().child("DocumentAccessAuthority");
        databaseReferenceFriendRequest = FirebaseDatabase.getInstance().getReference().child("DocumentAcccessRequest");
        showFriendRequuest();
    }

    private void showFriendRequuest() {

        Query query = databaseReferenceFriendReqRef.orderByChild("request_type").equalTo("received");
        FirebaseRecyclerOptions <FriendRequestGetterSetter> options =
                new FirebaseRecyclerOptions.Builder <FriendRequestGetterSetter>()
                        .setQuery(query, FriendRequestGetterSetter.class)
                        .build();
        FirebaseRecyclerAdapter <FriendRequestGetterSetter, FriendRequestViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter <FriendRequestGetterSetter, FriendRequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FriendRequestViewHolder holder, int position, @NonNull FriendRequestGetterSetter model) {

                        receiverUserId = getRef(position).getKey();

                        final int positioon2 = position;

                        databaseReferenceUserRef.child(receiverUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.hasChild("profileImage")) {
                                        String profile = dataSnapshot.child("profileImage").getValue().toString();
                                        Picasso.get()
                                                .load(profile).into(holder.circleImageView);
                                    }
                                    String name = dataSnapshot.child("name").getValue().toString();


                                    holder.textView.setText(name);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        holder.buttonConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                progressDialog.setTitle("Waiting");
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();

                                receiverUserId = getRef(positioon2).getKey();
                                acceptFriendRequest(receiverUserId);
                            }
                        });
                        holder.buttonDecline.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                progressDialog.setTitle("Waiting");
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                                receiverUserId = getRef(positioon2).getKey();
                                cancelFriendRequest(receiverUserId);
                            }
                        });
                    }


                    @NonNull
                    @Override
                    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.friend_request_layout, parent, false);
                        FriendRequestViewHolder friendRequestViewHolder = new FriendRequestViewHolder(view);
                        return friendRequestViewHolder;
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void acceptFriendRequest(String receiverUserId) {

        final String RUID = receiverUserId;// reciever user id
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        databaseReferenceFriendRef.child(CURRENT_USER).child(RUID).setValue(true)
                .addOnCompleteListener(new OnCompleteListener <Void>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task <Void> task) {

                        if (task.isSuccessful()) {
                            databaseReferenceFriendRef.child(RUID).child(CURRENT_USER).setValue(true)
                                    .addOnCompleteListener(new OnCompleteListener <Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task <Void> task) {

                                            if (task.isSuccessful()) {
                                                databaseReferenceFriendRequest.child(CURRENT_USER).child(RUID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener <Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task <Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    databaseReferenceFriendRequest.child(RUID).child(CURRENT_USER)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener <Void>() {
                                                                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                                                @Override
                                                                                public void onComplete(@NonNull Task <Void> task) {
                                                                                    if (task.isSuccessful()) {

                                                                                        progressDialog.dismiss();
                                                                                        Toast.makeText(FriendRequestActivity.this, "You Are now Friends", Toast.LENGTH_SHORT).show();


                                                                                    } else {
                                                                                        progressDialog.dismiss();
                                                                                        Toast.makeText(FriendRequestActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();

                                                                                    }
                                                                                }
                                                                            });
                                                                } else {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(FriendRequestActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();

                                                                }
                                                            }
                                                        });
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(FriendRequestActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                        } else {

                            progressDialog.dismiss();
                            Toast.makeText(FriendRequestActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void cancelFriendRequest(String receiverUserId) {


        final String RUID = receiverUserId;// reciever user id
        databaseReferenceFriendRequest.child(CURRENT_USER).child(RUID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener <Void>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task <Void> task) {
                        if (task.isSuccessful()) {
                            databaseReferenceFriendRequest.child(RUID).child(CURRENT_USER)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener <Void>() {
                                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                        @Override
                                        public void onComplete(@NonNull Task <Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(FriendRequestActivity.this, "You decline request succesfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(FriendRequestActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(FriendRequestActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    public static class FriendRequestViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        Button buttonConfirm, buttonDecline;
        TextView textView;

        public FriendRequestViewHolder(View itemView) {

            super(itemView);

            buttonConfirm = itemView.findViewById(R.id.button_frndReqconfirm);
            buttonDecline = itemView.findViewById(R.id.button_frndReqDecline);
            circleImageView = itemView.findViewById(R.id.circularView_FrndReq_profile);
            textView = itemView.findViewById(R.id.textView_frndReq_alluserProfile_Fullname);

        }


    }

}
