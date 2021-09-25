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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.rwn.rwnstudy.R;
import com.rwn.rwnstudy.utilities.NotificationGetterSetter;
import com.rwn.rwnstudy.utilities.ProductGetterSetter;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class NotificationShowActivity extends AppCompatActivity {
RecyclerView recyclerView;
DatabaseReference databaseReference,databaseReference2;
FirebaseAuth mAuth;
String CURRENT_USER;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_show);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Notifications");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        recyclerView= findViewById(R.id.recyclerView_notification);
        recyclerView.setHasFixedSize(true);
        mAuth =FirebaseAuth.getInstance();

        CURRENT_USER = mAuth.getCurrentUser().getUid();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NotificationShowActivity.this);

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        displayAllStore();

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


    private void displayAllStore() {

        Query query = databaseReference.child("Notification").child("RwnStudyAdmin").orderByChild("timeStamp");

        FirebaseRecyclerOptions<NotificationGetterSetter> options =
                new FirebaseRecyclerOptions.Builder <NotificationGetterSetter>()

                        .setQuery(query, NotificationGetterSetter.class)
                        .build();


        FirebaseRecyclerAdapter<NotificationGetterSetter,NotificaitonViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter <NotificationGetterSetter, NotificaitonViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final NotificaitonViewHolder holder, int position, @NonNull final NotificationGetterSetter model) {



                        final int position1= position;
                        final String from= model.getFrom();
                        final String node= model.getNode();
                        String parentNode= model.getParentNode();

                        final String productId = getRef(position1).getKey();

                        databaseReference.child("NotificationViewer").child(productId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(CURRENT_USER)){

                                        holder.linearLayout.setBackgroundColor(getResources().getColor(R.color.notification_color));

                                }else {
                                    holder.linearLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                     DatabaseReference databaseReferenceForNode ;
                     if(!(parentNode.equals(""))) {
                         databaseReferenceForNode = databaseReference.child(parentNode).child(node).child(from);
                     }
                     else {
                         databaseReferenceForNode= databaseReference.child(node).child(from);
                     }

                     databaseReferenceForNode.addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                             ProductGetterSetter productGetterSetter=dataSnapshot.getValue(ProductGetterSetter.class);
                             if(productGetterSetter!= null) {
                                 String name = productGetterSetter.getProductName();
                                String link= productGetterSetter.getProductLink();
                                if(!link.equals("")) {
                                    Picasso.get().load(link).resize(80, 80).noFade().placeholder(R.drawable.profile).into(holder.imageViewPicature);

                                }
                                 holder.imageViewPicature.setVisibility(View.GONE);
                                holder.textViewTitel.setText(name);


                                 String desc= productGetterSetter.getOther();
                                 if(!(desc.equals("")))
                                 holder.textViewDescription.setText(desc);

                                 final String timeStam=model.getTimeStamp();
                                 if(!timeStam.equals("")) {
                                     String year = timeStam.substring(0, 4);
                                     String month = timeStam.substring(4, 6);
                                     String day = timeStam.substring(6, 8);
                                     String hour = timeStam.substring(8, 10);
                                     String min = timeStam.substring(10, 12);
                                     holder.textViewTime.setText(hour + ":" + min + " " + day + "/" + month + "/" + year);

                                 }
                                 holder.itemView.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         final String productId = getRef(position1).getKey();
                                         NotificationUpdate notificationUpdate= new NotificationUpdate(productId);
                                         notificationUpdate.start();

                                         Intent intent = new Intent(NotificationShowActivity.this,NotificationShowerActivity.class);
                                         intent.putExtra("product_id",from);
                                         intent.putExtra("store_name",node);
                                         startActivity(intent);




                                     }
                                 });

                                 holder.linearLayout.setVisibility(View.VISIBLE);
                             }
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError databaseError) {

                         }
                     });

                    }


                    @NonNull
                    @Override
                    public NotificaitonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.notification_messages_layout, parent, false);
                        NotificaitonViewHolder friendsViewHolder = new NotificaitonViewHolder(view);
                        return friendsViewHolder;
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class NotificaitonViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPicature;
        TextView textViewTitel,textViewDescription;
        LinearLayout linearLayout;
        TextView textViewTime;



        NotificaitonViewHolder(View itemView) {

            super(itemView);


            imageViewPicature = itemView.findViewById(R.id.imageview_notification_image);


            textViewDescription = itemView.findViewById(R.id.textView_notification_desc);

            textViewTitel = itemView.findViewById(R.id.textview_notification_titel);

            linearLayout= itemView.findViewById(R.id.linearlayout_back);

            textViewTime = itemView.findViewById(R.id.textView_time);




        }
    }

    class NotificationUpdate extends Thread {

        String menuItem;

        NotificationUpdate(String menuItem) {
            this.menuItem = menuItem;
        }

        @Override
        public void run() {
            super.run();

            databaseReference.child("NotificationViewer").child(menuItem).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()){

                        if(dataSnapshot.hasChild(CURRENT_USER)){
                            databaseReference.child("NotificationViewer").child(menuItem).child(CURRENT_USER).removeValue();
                            onRemoveProduct();
                        }
                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }
    public void onRemoveProduct() {
        MainActivity.counter--;
        invalidateOptionsMenu();

    }
}
