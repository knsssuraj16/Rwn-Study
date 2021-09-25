package com.rwn.rwnstudy.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rwn.rwnstudy.R;
import com.rwn.rwnstudy.utilities.MessageGetterSetter;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    RecyclerView recyclerView;
    DatabaseReference databaseReferenceChatting, databaseReferenceUserRef;
    FirebaseAuth mAuth;
    String CurrentUserID;
    View view;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat2, container, false);


        recyclerView = view.findViewById(R.id.recyclerViewChatting);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        databaseReferenceChatting = FirebaseDatabase.getInstance().getReference().child("Messages").child(CurrentUserID);

        databaseReferenceUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        Start();
        return view;
    }


    private void Start() {


        Query query = databaseReferenceChatting.orderByChild("Date");
        FirebaseRecyclerOptions <MessageGetterSetter> options =
                new FirebaseRecyclerOptions.Builder <MessageGetterSetter>()
                        .setQuery(query, MessageGetterSetter.class)
                        .build();
        FirebaseRecyclerAdapter <MessageGetterSetter, ChattingViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter <MessageGetterSetter, ChattingViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChattingViewHolder holder, int position, @NonNull MessageGetterSetter model) {

                        final String uid = getRef(position).getKey();
                        final String[] name = new String[1];
                        final int pos = position;

                        databaseReferenceUserRef.child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String profile = dataSnapshot.child("profileImage").getValue().toString();
                                    name[0] = dataSnapshot.child("name").getValue().toString();
                                    Picasso.get()
                                            .load(profile).resize(100, 100).noFade().into(holder.circleImageView);

                                    holder.textView.setText(name[0]);
                                }
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), ChatActivity.class);
                                intent.putExtra("visit_user_id", uid);
                                intent.putExtra("username", name[0]);
                                startActivity(intent);

                            }
                        });

                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                final String uid = getRef(pos).getKey();
                                Log.d("dgdfgddf", "onLongClick: " + uid);
                                CharSequence options[] = new CharSequence[]{
                                        "Yes"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Delete Chats");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (which == 0) {
                                            databaseReferenceChatting.child(uid).removeValue().addOnCompleteListener(new OnCompleteListener <Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task <Void> task) {
                                                    Toast.makeText(getContext(), "Chat Deleted Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            });


                                        }
                                    }


                                });

                                builder.show();

                                return true;
                            }
                        });



                        databaseReferenceChatting.child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int a= 0;


                                for (DataSnapshot d :dataSnapshot.getChildren()) {

                                    Log.d("hhhhfufu", "onChildAdded: "+ d.toString());
                                    if(d.hasChild("From"))
                                    {
                                        String che = d.child("From").getValue().toString();
                                        if(!che.equals(CurrentUserID)) {
                                            if (d.hasChild("isSeen")) {
                                                String check = d.child("isSeen").getValue().toString();
                                                if (check.equals("false")) {

                                                    a++;
                                                }
                                            }
                                        }
                                    }
                                }

                                Log.d("colllll", "onChildAdded: "+ a);
                                if(a>0){
                                    holder.frameLayout.setVisibility(View.VISIBLE);
                                    holder.textViewCounter.setText(Integer.toString(a));
                                }
                                else {

                                    holder.frameLayout.setVisibility(View.INVISIBLE);
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                        databaseReferenceChatting.child(uid).orderByChild("Date").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                                Log.d("llllllll", "onChildAdded: "+ dataSnapshot.toString());
                                if (dataSnapshot.hasChild("Message")) {
                                    String message = dataSnapshot.child("Message").getValue().toString();
                                    if(message.trim().equals("") || message.isEmpty()){
                                        holder.textViewlastMessage.setText("*IMAGE");
                                    }else {
                                        holder.textViewlastMessage.setText(message);
                                    }
                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }


                    @NonNull
                    @Override
                    public ChattingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.chat_view_layout, parent, false);
                        ChattingViewHolder friendRequestViewHolder = new ChattingViewHolder(view);
                        return friendRequestViewHolder;
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }


    public static class ChattingViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView textView, textViewlastMessage,textViewCounter;
        FrameLayout frameLayout;

        public ChattingViewHolder(View itemView) {

            super(itemView);
            circleImageView = itemView.findViewById(R.id.circularView_chatting_profile);
            textView = itemView.findViewById(R.id.textView_chatting_userProfile_Fullname);
            textViewlastMessage = itemView.findViewById(R.id.textView_last_message);
            frameLayout = itemView.findViewById(R.id.counterValuePanel);
            textViewCounter = itemView.findViewById(R.id.textView_count);

        }
    }

}
