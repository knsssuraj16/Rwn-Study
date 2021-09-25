package com.rwn.rwnstudy.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rwn.rwnstudy.R;
import com.rwn.rwnstudy.utilities.ConstantForApp;
import com.rwn.rwnstudy.utilities.StatusGetterSetter;
import com.rwn.rwnstudy.utilities.UserGetterAndSetter;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

//import com.rwn.rwnstudy.utilities.Constant;

public class VerticalyStatusForDdeleting extends AppCompatActivity {
    RecyclerView recyclerView ;
    DatabaseReference databaseReference ;
    FirebaseAuth mAuth;
    String CURRENT_USER,node;
    StorageReference firebaseStorageDocumentuplod;
    String saveCurrentDate,saveCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verticaly_status_for_ddeleting);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("My Status");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat saveCurrentTime = new SimpleDateFormat("hh:mm a");
        saveCurrent= saveCurrentTime.format(calendar.getTime());
        viewInitalizing();

    }

    private void viewInitalizing() {
        recyclerView = findViewById(R.id.recyclerView_verticalStatus);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(VerticalyStatusForDdeleting.this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);

        databaseReference= FirebaseDatabase.getInstance().getReference();
        mAuth= FirebaseAuth.getInstance();
        CURRENT_USER = mAuth.getCurrentUser().getUid();
        firebaseStorageDocumentuplod = FirebaseStorage.getInstance().getReference().child("Images").child("StatusImages").child(CURRENT_USER);


        databaseReference.child("Users").child(CURRENT_USER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserGetterAndSetter userGetterAndSetter = dataSnapshot.getValue(UserGetterAndSetter.class);
                String userType=  userGetterAndSetter.getUserType();

                if(userType.equals(ConstantForApp.STUDENT)){
                    node = "Students";
                }else{
                    node= "Teachers";
                }
                showOnRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }



    private void showOnRecyclerView() {


        Query sortPostinDecendingOrder = databaseReference.child("Status").child(node).child(CURRENT_USER);
        FirebaseRecyclerOptions<StatusGetterSetter> options =
                new FirebaseRecyclerOptions.Builder <StatusGetterSetter>()
                        .setQuery(sortPostinDecendingOrder, StatusGetterSetter.class)
                        .build();
        FirebaseRecyclerAdapter<StatusGetterSetter, VerticallyImageViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter <StatusGetterSetter, VerticallyImageViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final VerticallyImageViewHolder holder, int position, @NonNull  StatusGetterSetter model) {


                        final String postKey = getRef(position).getKey();
                        Log.d("postKey", "onBindViewHolder: "+postKey);
                        final String imageLink= model.getLink();
                        String date= model.getDate();
                        String hour= model.getHour();
                        String min= model.getMin();
                        final  int position1= position;

                        final VerticallyImageViewHolder holder1= holder;


                        databaseReference.child("StatusViewer").child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    int a= (int) dataSnapshot.getChildrenCount();
                                    holder1.textViewEye.setText(a+" ");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(VerticalyStatusForDdeleting.this, StatusImageShowActivity.class);
                                intent.putExtra("node", node);
                                intent.putExtra("friendsId", CURRENT_USER);
                                startActivity(intent);
                            }
                        });

                        holder1.textViewEye.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {



                                Intent intent = new Intent(VerticalyStatusForDdeleting.this,SelfStatusViewer.class);
                                intent.putExtra("Key",postKey);
                                startActivity(intent);


                            }
                        });
                        if(saveCurrentDate.equals(date)){
                            holder.textViewTime.setText("Today, "+hour+":"+min);
                        }
                        else
                        {

                            holder.textViewTime.setText("Yesterday, "+hour+":"+min);
                        }
                        Picasso.get().load(imageLink).resize(100, 100).noFade().into(holder.imageViewStatus);



                        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {



                                CharSequence options[] = new CharSequence[]{
                                        "Delete"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder1.itemView.getContext());

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (which == 0) {



                                            final ProgressDialog progressDialog= new ProgressDialog(holder1.itemView.getContext());
                                            progressDialog.setCanceledOnTouchOutside(false);
                                            progressDialog.show();

                                            final String postKey = getRef(holder1.getAdapterPosition()).getKey();

                                            databaseReference.child("Status").child(node).child(CURRENT_USER).child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    StatusGetterSetter  statusGetterSetter = dataSnapshot.getValue(StatusGetterSetter.class);
                                                    String storagePath=   Objects.requireNonNull(statusGetterSetter).getLink();

                                                    FirebaseStorage firebaseStorage= FirebaseStorage.getInstance().getReference().getStorage();

                                                    StorageReference storageReference= firebaseStorage.getReferenceFromUrl(storagePath);


                                                    storageReference.delete().addOnCompleteListener(new OnCompleteListener <Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                databaseReference.child("Status").child(node).child(CURRENT_USER).child(postKey).removeValue().addOnCompleteListener(new OnCompleteListener <Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task <Void> task) {
                                                                        if(task.isSuccessful()){
                                                                            databaseReference.child("StatusViewer").child(postKey).removeValue().addOnCompleteListener(new OnCompleteListener <Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task <Void> task) {
                                                                                    if(task.isSuccessful()){

                                                                                        // notifyItemRemoved(holder.getAdapterPosition());
                                                                                        notifyDataSetChanged();
                                                                                        progressDialog.dismiss();
                                                                                        Toast.makeText(VerticalyStatusForDdeleting.this, "Status deleted", Toast.LENGTH_SHORT).show();
                                                                                    }else{

                                                                                        progressDialog.dismiss();
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                        else {
                                                                            databaseReference.child("StatusViewer").child(postKey).removeValue();
                                                                            progressDialog.dismiss();
                                                                        }
                                                                    }
                                                                });
                                                            }else{
                                                                databaseReference.child("Status").child(node).child(CURRENT_USER).child(postKey).removeValue();
                                                                databaseReference.child("StatusViewer").child(postKey).removeValue();

                                                                progressDialog.dismiss();
                                                            }
                                                        }
                                                    });

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }

                                    }

                                });

                                builder.show();
                                return true;

                            }
                        });










                    }

                    @NonNull
                    @Override
                    public VerticallyImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.image_layout_for_deleting, parent, false);
                        VerticallyImageViewHolder postViewHolder = new VerticallyImageViewHolder(view);
                        return postViewHolder;
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class VerticallyImageViewHolder extends RecyclerView.ViewHolder {


        CircleImageView imageViewStatus;
        TextView textViewTime;
        TextView textViewEye;
        LinearLayout linearLayout;

        public VerticallyImageViewHolder(View itemView) {

            super(itemView);
            imageViewStatus = itemView.findViewById(R.id.circularView_student_profile);
            textViewTime = itemView.findViewById(R.id.textView_chatting_userProfile_Fullname);
            textViewEye = itemView.findViewById(R.id.imageView_eyeView);

            linearLayout = itemView.findViewById(R.id.linearlayout);

        }
    }



}
