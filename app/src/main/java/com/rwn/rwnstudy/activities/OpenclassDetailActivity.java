package com.rwn.rwnstudy.activities;

import android.app.ProgressDialog;
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
import com.rwn.rwnstudy.utilities.UserGetterAndSetter;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class OpenclassDetailActivity extends AppCompatActivity {
    TextView textView;
    RecyclerView recyclerViewStudentlist;
    String sectionKey;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference, databaseReferenceDocumentAccessRequest, databaseReferenceFriendRef;
    FirebaseAuth mAuth;
    String dialog;
    String CUREENT_USER, name, currentState = "not_firends", PersonID, teacher = null;
    final String TAG = "OpenClass";


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openclass_detail);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Class Students detail");


    }

        Intent intent = getIntent();
        sectionKey = intent.getStringExtra("classWithSection");
        teacher = intent.getStringExtra("teacher");

        viewIntializing();
        dataBaseConnection();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("kfofkfo", "onDestroy: ");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("kfofkfo", "pause: ");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("kfofkfo", "resume: ");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void dataBaseConnection() {

        mAuth = FirebaseAuth.getInstance();

            CUREENT_USER = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        loadStudentsList();
        databaseReferenceDocumentAccessRequest = FirebaseDatabase.getInstance().getReference().child("DocumentAcccessRequest");
        databaseReferenceFriendRef = FirebaseDatabase.getInstance().getReference().child("DocumentAccessAuthority");
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

    private void loadStudentsList() {

        String node = null;
        if (teacher == null) {
            node = "Students";
        } else {
            node = "Teachers";
        }

        Query sortPostinDecendingOrder = databaseReference.child(node).child(sectionKey).orderByValue().equalTo(true);
        FirebaseRecyclerOptions <Boolean> options =
                new FirebaseRecyclerOptions.Builder <Boolean>()
                        .setQuery(sortPostinDecendingOrder, Boolean.class)
                        .build();

        final FirebaseRecyclerAdapter <Boolean, StudentViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter <Boolean, StudentViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final StudentViewHolder holder,  int position1, @NonNull final Boolean model) {

                       final int position = position1;

                        final String userId = getRef(position).getKey();

                        databaseReference.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {


                                    final UserGetterAndSetter userGetterAndSetter = dataSnapshot.getValue(UserGetterAndSetter.class);
                                    name = userGetterAndSetter.getName();




                                    String admisioon = userGetterAndSetter.getAdmissionNumber();

                                    if(admisioon.equals("N/A")){

                                    }
                                    else{
                                        holder.textViewAddmisionNumber.setText(admisioon);
                                    }
                                    holder.textViewUsername.setText(userGetterAndSetter.getName());
                                    Picasso.get().load(userGetterAndSetter.getProfileImage()).resize(80,80).noFade().placeholder(R.drawable.profile).into(holder.circleImageView);

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {


                                            PersonID = getRef(position).getKey();

                                            Log.d(TAG, "onClick: " + name + "\n" +
                                                    "position =" + position + "\n visit_userid_" + PersonID);

                                            progressDialog = new ProgressDialog(holder.itemView.getContext());
                                            progressDialog.setCanceledOnTouchOutside(false);
                                            progressDialog.show();
                                            maintaneceOfButton(PersonID,userGetterAndSetter);

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    @NonNull
                    @Override
                    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.studentlayout, parent, false);
                        StudentViewHolder subjectViewHolder = new StudentViewHolder(view);
                        return subjectViewHolder;
                    }
                };
        recyclerViewStudentlist.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    private void unFriendExistingFriends() {
        databaseReferenceFriendRef.child(CUREENT_USER).child(PersonID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener <Void>() {
                    @Override
                    public void onComplete(@NonNull Task <Void> task) {
                        if (task.isSuccessful()) {
                            databaseReferenceFriendRef.child(PersonID).child(CUREENT_USER)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener <Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task <Void> task) {
                                            if (task.isSuccessful()) {

                                                Toast.makeText(OpenclassDetailActivity.this, "Authority Cancel Succesfully", Toast.LENGTH_SHORT).show();


                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void sendFriendRequest() {

        databaseReferenceDocumentAccessRequest.child(CUREENT_USER).child(PersonID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener <Void>() {
                    @Override
                    public void onComplete(@NonNull Task <Void> task) {
                        if (task.isSuccessful()) {
                            databaseReferenceDocumentAccessRequest.child(PersonID).child(CUREENT_USER)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener <Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task <Void> task) {
                                            if (task.isSuccessful()) {
                                                DatabaseReference databaseReference1 = databaseReference.child("Notification").child(PersonID).push();

                                                HashMap hashMap= new HashMap();
                                                hashMap.put("from",CUREENT_USER);
                                                hashMap.put("type","request");
                                                databaseReference1.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {

                                                        if(task.isSuccessful()){
                                                            Toast.makeText(OpenclassDetailActivity.this, "Request succesfully send", Toast.LENGTH_SHORT).show();
                                                            currentState = "request_sent";
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


    private void acceptFriendRequest() {


        databaseReferenceFriendRef.child(CUREENT_USER).child(PersonID).setValue(true)
                .addOnCompleteListener(new OnCompleteListener <Void>() {
                    @Override
                    public void onComplete(@NonNull Task <Void> task) {

                        if (task.isSuccessful()) {
                            databaseReferenceFriendRef.child(PersonID).child(CUREENT_USER).setValue(true)
                                    .addOnCompleteListener(new OnCompleteListener <Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task <Void> task) {

                                            if (task.isSuccessful()) {
                                                databaseReferenceDocumentAccessRequest.child(CUREENT_USER).child(PersonID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener <Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task <Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    databaseReferenceDocumentAccessRequest.child(PersonID).child(CUREENT_USER)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener <Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task <Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        currentState = "friends";
                                                                                        Toast.makeText(OpenclassDetailActivity.this, "Both are Friends", Toast.LENGTH_SHORT).show();


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
                });


    }


    private void cancelFriendRequest() {
        databaseReferenceDocumentAccessRequest.child(CUREENT_USER).child(PersonID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener <Void>() {
                    @Override
                    public void onComplete(@NonNull Task <Void> task) {
                        if (task.isSuccessful()) {
                            databaseReferenceDocumentAccessRequest.child(PersonID).child(CUREENT_USER)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener <Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task <Void> task) {
                                            if (task.isSuccessful()) {

                                                DatabaseReference databaseReference1 = databaseReference.child("Notification").child(PersonID);
                                                
                                                databaseReference1.removeValue().addOnCompleteListener(new OnCompleteListener <Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task <Void> task) {
                                                        currentState = "not_firends";
                                                        Toast.makeText(OpenclassDetailActivity.this, "Reqeust revert successfull", Toast.LENGTH_SHORT).show();

                                                    }
                                                });


                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView textViewUsername;
        TextView textViewAddmisionNumber;

        public StudentViewHolder(View itemView) {

            super(itemView);
            circleImageView = itemView.findViewById(R.id.circularView_student_profile);
            textViewUsername = itemView.findViewById(R.id.textView_chatting_userProfile_Fullname);
            textViewAddmisionNumber = itemView.findViewById(R.id.textView_addmision);

        }

    }

    private void maintaneceOfButton(final String personID, final UserGetterAndSetter userGetterAndSetter) {


        databaseReferenceDocumentAccessRequest.child(CUREENT_USER).child(personID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String currentState="not_firends";
                        if (dataSnapshot.hasChild("request_type")) {
                            String request_type = dataSnapshot.child("request_type").getValue().toString();


                            if (request_type.equals("sent")) {
                                currentState = "request_sent";

                                forUpdateCurrentStates(currentState,userGetterAndSetter,personID);
                            } else if (request_type.equals("received")) {
                                currentState = "request_recieved";
                                forUpdateCurrentStates(currentState,userGetterAndSetter,personID);

                            }
                            else{

                                forUpdateCurrentStates(currentState,userGetterAndSetter,personID);
                            }
                        } else {
                            databaseReferenceFriendRef.child(CUREENT_USER)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(personID)) {
                                                String currentState = "friends";

                                                forUpdateCurrentStates(currentState,userGetterAndSetter,personID);
                                            }
                                            else{

                                               String currentState = "not_firends";
                                                forUpdateCurrentStates(currentState,userGetterAndSetter,personID);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    void forUpdateCurrentStates(final String currentState, UserGetterAndSetter userGetterAndSetter, String personID){
        if (!(personID.equals(CUREENT_USER))) {

            String name = userGetterAndSetter.getName();


            if (currentState.equals("not_firends")) {
                dialog = "Send Request to ";
            } else if (currentState.equals("request_sent")) {
                dialog = "Friend request  revert  from ";
            } else if (currentState.equals("request_recieved")) {
                dialog = "Accept Request ";
            } else if (currentState.equals("friends")) {
                dialog = "Unfriend of ";
            }


            CharSequence options[] = new CharSequence[]{dialog + name};
            AlertDialog.Builder builder = new AlertDialog.Builder(OpenclassDetailActivity.this)
                    .setTitle("Access Doc's Authority");

            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (which == 0) {

                        if (currentState.equals("not_firends")) {
                            sendFriendRequest();
                        } else if (currentState.equals("request_sent")) {
                            cancelFriendRequest();
                        } else if (currentState.equals("request_recieved")) {
                            acceptFriendRequest();
                        } else if (currentState.equals("friends")) {
                            unFriendExistingFriends();
                        }


                    }
                }

            });
            progressDialog.dismiss();

            builder.show();

        } else {
            progressDialog.dismiss();
            Toast.makeText(OpenclassDetailActivity.this, "sorry you can send self request", Toast.LENGTH_SHORT).show();
        }
    }


    private void viewIntializing() {
        textView = findViewById(R.id.textview_studentof_class_bca);
        recyclerViewStudentlist = findViewById(R.id.recyclerView_studentlist);
        recyclerViewStudentlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OpenclassDetailActivity.this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        recyclerViewStudentlist.setLayoutManager(linearLayoutManager);


    }
}
