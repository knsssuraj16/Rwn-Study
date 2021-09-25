package com.rwn.rwnstudy.activities;

import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
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
import com.rwn.rwnstudy.utilities.ProductGetterSetter;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProductDescriptionActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    String StoreKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Products Description");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getPendingIntent();
        viewIntialiging();

    }

    private void getPendingIntent() {


        Intent intent = getIntent();
        StoreKey = intent.getStringExtra(Intent.EXTRA_TEXT);


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
    private void viewIntialiging() {
        recyclerView = findViewById(R.id.recyclerView_productDescription);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProductDescriptionActivity.this);

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        displayAllProduct();
    }



    private void displayAllProduct() {

        Query query = databaseReference.child("ProductDetail").child(StoreKey);

        FirebaseRecyclerOptions<ProductGetterSetter> options =
                new FirebaseRecyclerOptions.Builder <ProductGetterSetter>()

                        .setQuery(query, ProductGetterSetter.class)
                        .build();


        FirebaseRecyclerAdapter<ProductGetterSetter, ProductViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter <ProductGetterSetter, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ProductViewHolder holder, int position, @NonNull final ProductGetterSetter model) {

                        final int position1= position;
                        if(model.getProductName() != null) {
                            holder.textViewProductName.setVisibility(View.VISIBLE);
                            holder.textViewProductName.setText(model.getProductName());
                        }

                        if(model.getProductDesc() != null) {
                            holder.textViewProductDescription.setVisibility(View.VISIBLE);
                            holder.textViewProductDescription.setText(model.getProductDesc());
                        }
                        if(model.getProductImage() != null)
                        {
                            holder.imageViewPicature.setVisibility(View.VISIBLE);
                            Picasso.get().load(model.getProductImage()).into( holder.imageViewPicature);

                        }





                        if(model.getDiscount() != null){
                            holder.textViewDiscount.setVisibility(View.VISIBLE);
                            holder.textViewDiscount.setText(model.getDiscount());

                        }

                        if(model.getMRP() != null){
                            holder.textViewMRP.setVisibility(View.VISIBLE);

                            holder.textViewMRP.setText(model.getMRP());

                        }


                        if(model.getOther() != null){
                            holder.textViewOthers.setVisibility(View.VISIBLE);


                            holder.textViewOthers.setText(model.getOther());
                        }

                        if(model.getPrice() != null){
                            holder.textViewPrice.setVisibility(View.VISIBLE);


                            holder.textViewPrice.setText(model.getPrice());
                        }
                        if(model.getProductLink() != null){
                            holder.buttonBuyNow.setVisibility(View.VISIBLE);


                        }


                        holder.buttonBuyNow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String USER_IDs = getRef(position1).getKey();

//                                Intent intent = new Intent("com.rwn.rwnstudy.action.WEBVIEW");
//                                intent.setType("text/plain");
//                                intent.putExtra(intent.EXTRA_TEXT,link);

                                Intent intent = new Intent(ProductDescriptionActivity.this,NotificationShowerActivity.class);
                                intent.putExtra("product_id",USER_IDs);
                                intent.putExtra("store_name",StoreKey);
                                startActivity(intent);

                            }
                        });

                    }


                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.product_description_layout, parent, false);
                        ProductViewHolder friendsViewHolder = new ProductViewHolder(view);
                        return friendsViewHolder;
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPicature;
        TextView textViewProductName,textViewProductDescription,textViewMRP,textViewPrice,textViewDiscount,textViewOthers;
        Button buttonBuyNow;


        ProductViewHolder(View itemView) {

            super(itemView);


            imageViewPicature = itemView.findViewById(R.id.imageView_productImage);

            textViewProductName = itemView.findViewById(R.id.textView_product_name);

            textViewProductDescription = itemView.findViewById(R.id.textView_product_Description);

            textViewMRP = itemView.findViewById(R.id.textViewMRP);

            textViewPrice = itemView.findViewById(R.id.textView_price);

            textViewDiscount = itemView.findViewById(R.id.textView_Discount);

            textViewOthers = itemView.findViewById(R.id.textView_others);

            buttonBuyNow = itemView.findViewById(R.id.button_Buy_now);


        }
    }
}
