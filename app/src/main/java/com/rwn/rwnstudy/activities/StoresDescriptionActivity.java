package com.rwn.rwnstudy.activities;

import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.rwn.rwnstudy.R;
import com.rwn.rwnstudy.utilities.StoresInfoGetterSetter;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class StoresDescriptionActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    String StoreType;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores_description);

        gettingPendingaIntent();

        viewIntializing();
    }

    private void viewIntializing() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Store Description");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView_storeDescription);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(StoresDescriptionActivity.this);

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        displayAllStore();
    }

    private void gettingPendingaIntent() {
        Intent intent = getIntent();
        StoreType = intent.getStringExtra(Intent.EXTRA_TEXT);

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

        Query query = databaseReference.child("StoreType").child(StoreType);

        FirebaseRecyclerOptions<StoresInfoGetterSetter> options =
                new FirebaseRecyclerOptions.Builder <StoresInfoGetterSetter>()

                        .setQuery(query, StoresInfoGetterSetter.class)
                        .build();


        FirebaseRecyclerAdapter<StoresInfoGetterSetter, StoreViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter <StoresInfoGetterSetter, StoreViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final StoreViewHolder holder, int position, @NonNull final StoresInfoGetterSetter model) {

                        final int position1= position;
                        Picasso.get().load(model.getStoreImage()).into( holder.imageViewPicature);
                        holder.textViewAddress.setText(model.getStoreAdd());
                        holder.textViewStoreName.setText(model.getStoreName());
                        holder.textViewDescription.setText(model.getStoreDesc());

                        holder.buttonSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String USER_IDs = getRef(position1).getKey();

                                Intent intent = new Intent("com.rwn.rwnstudy.action.SHOWPRODUCT");
                                intent.setType("text/plain");
                                intent.putExtra(intent.EXTRA_TEXT,USER_IDs);
                                startActivity(intent);

                            }
                        });

                    }


                    @NonNull
                    @Override
                    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.stores_description_layout, parent, false);
                        StoreViewHolder friendsViewHolder = new StoreViewHolder(view);
                        return friendsViewHolder;
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class StoreViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPicature;
        TextView textViewStoreName,textViewDescription,textViewAddress;
        Button buttonSubmit;


        StoreViewHolder(View itemView) {

            super(itemView);


            imageViewPicature = itemView.findViewById(R.id.imageView_storeImage);

            textViewStoreName = itemView.findViewById(R.id.textView_Store_name);

            textViewDescription = itemView.findViewById(R.id.textView_Store_Description);

            textViewAddress = itemView.findViewById(R.id.textView_Store_address);

            buttonSubmit = itemView.findViewById(R.id.button_see_offer);



        }
    }
}
