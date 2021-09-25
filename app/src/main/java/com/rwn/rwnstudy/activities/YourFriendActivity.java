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
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class YourFriendActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference databaseReference, databaseReferenceFriendRef,databaseReferenceFriendRefForUnfriend, databaseReferenceUserRef;
    Button button;
    String CURRENT_USER;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_friend);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Friend");

            getSupportActionBar().setHomeButtonEnabled(true);
        }


        viewIntializing();


        databseLinkup();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(YourFriendActivity.this, FriendRequestActivity.class));
            }
        });
    }



    private void databseLinkup() {

        mAuth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            CURRENT_USER = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReferenceFriendRef = FirebaseDatabase.getInstance().getReference().child("DocumentAccessAuthority").child(CURRENT_USER);
        databaseReferenceFriendRefForUnfriend = FirebaseDatabase.getInstance().getReference().child("DocumentAccessAuthority");
        databaseReferenceUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        displayAllFriends();
    }

    private void viewIntializing() {
        recyclerView = findViewById(R.id.recyclerView_studentlist);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(YourFriendActivity.this);

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        button = findViewById(R.id.button_friendRequest);
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

    private void displayAllFriends() {

        Query query = databaseReferenceFriendRef;

        FirebaseRecyclerOptions <Boolean> options =
                new FirebaseRecyclerOptions.Builder <Boolean>()

                        .setQuery(query, Boolean.class)
                        .build();


        FirebaseRecyclerAdapter <Boolean, FriendsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter <Boolean, FriendsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FriendsViewHolder holder,  int position, @NonNull final Boolean model) {

                        holder.Date.setVisibility(View.GONE);
                        final String USER_IDs = getRef(position).getKey();
                        databaseReferenceUserRef.child(USER_IDs)
                                .addValueEventListener(new ValueEventListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()) {
                                            final String userName;

                                            userName = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();

                                            if (dataSnapshot.hasChild("profileImage")) {
                                                final String profileImage = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();

                                                Picasso.get().load(profileImage).resize(80,80).noFade().into(holder.ProfileImage);
                                            }

                                            holder.Fullname.setText(userName);


                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {


                                                    final String USER_IDs = getRef(holder.getAdapterPosition()).getKey();
                                                    Intent intent = new Intent(YourFriendActivity.this,PersonalDocumantUploadingActivity.class);
                                                    intent.putExtra("Userid",USER_IDs);
                                                    startActivity(intent);


                                                }
                                            });


                                            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                                                @Override
                                                public boolean onLongClick(View v) {

                                                    CharSequence options[] = new CharSequence[]{

                                                            "Remove from document Access"
                                                    };
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(YourFriendActivity.this)
                                                            .setTitle("Select Option");

                                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            if (which == 0) {

                                                                final String USER_IDs = getRef(holder.getAdapterPosition()).getKey();
                                                                unFriendExistingFriends(USER_IDs);

                                                            }

                                                        }

                                                    });

                                                    builder.show();

                                                    return true;
                                                }
                                            });

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                    }


                    @NonNull
                    @Override
                    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.all_user_display_from_search, parent, false);
                        FriendsViewHolder friendsViewHolder = new FriendsViewHolder(view);
                        return friendsViewHolder;
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        TextView Fullname, Date;
        CircleImageView ProfileImage;


        FriendsViewHolder(View itemView) {

            super(itemView);

            Fullname = itemView.findViewById(R.id.textView_search_alluserProfile_Fullname);
            ProfileImage = itemView.findViewById(R.id.circularView_search_profile);
            Date = itemView.findViewById(R.id.textView_search_alluserProfile_status);


        }
    }

    private void unFriendExistingFriends(final String USER_IDs) {
        final String s= USER_IDs;
        databaseReferenceFriendRefForUnfriend.child(CURRENT_USER).child(USER_IDs)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener <Void>() {
                    @Override
                    public void onComplete(@NonNull Task <Void> task) {
                        if (task.isSuccessful()) {
                            databaseReferenceFriendRefForUnfriend.child(s ).child(CURRENT_USER)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener <Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task <Void> task) {
                                            if (task.isSuccessful()) {


                                                Toast.makeText(YourFriendActivity.this, "Succesfully remove Authority", Toast.LENGTH_SHORT).show();


                                            }
                                        }
                                    });
                        }
                    }
                });
    }

}
