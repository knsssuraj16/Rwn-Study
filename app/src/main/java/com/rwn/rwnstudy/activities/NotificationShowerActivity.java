package com.rwn.rwnstudy.activities;

import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rwn.rwnstudy.R;
import com.rwn.rwnstudy.utilities.ProductGetterSetter;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class NotificationShowerActivity extends AppCompatActivity {
    ImageView imageViewPicature;
    TextView textViewProductName,textViewProductDescription,textViewMRP,textViewPrice,textViewDiscount,textViewOthers;
    Button buttonBuyNow;
    LinearLayout linearLayoutPrice;
    TextView textViewPriceOnlyText;

    String StoreKey,ProductKey;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_shower);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Details");
        }
        imageViewPicature = findViewById(R.id.imageView_productImage);

        textViewProductName =  findViewById(R.id.textView_product_name);

        textViewProductDescription = findViewById(R.id.textView_product_Description);

        textViewMRP =  findViewById(R.id.textViewMRP);

        textViewPrice =  findViewById(R.id.textView_price);

        textViewDiscount =  findViewById(R.id.textView_Discount);

        textViewOthers =  findViewById(R.id.textView_others);

        buttonBuyNow =  findViewById(R.id.button_Buy_now);

        linearLayoutPrice= findViewById(R.id.linearlayout_price);


        Intent intent = getIntent();
         ProductKey= intent.getStringExtra("product_id");
        StoreKey= intent.getStringExtra("store_name");


        Log.d("ndmddl", "onCreate: "+ ProductKey);
        Log.d("ndmddl", "onCreate: "+ StoreKey);

        textViewPriceOnlyText = findViewById(R.id.textView_realPrice);

        databaseReference = FirebaseDatabase.getInstance().getReference();












        databaseReference.child("ProductDetail").child(StoreKey).child(ProductKey).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                buttonBuyNow.setVisibility(View.GONE);
                if(dataSnapshot.exists()){

                   ProductGetterSetter productGetterSetter= dataSnapshot.getValue(ProductGetterSetter.class);

                  String other= Objects.requireNonNull(productGetterSetter).getOther();
                  if(!(TextUtils.isEmpty(other))){
                      textViewOthers.setVisibility(View.VISIBLE);
                      textViewOthers.setText(other);
                  }


                 String Discount= productGetterSetter.getDiscount();
                  if(!(TextUtils.isEmpty(Discount))){
                      textViewDiscount.setVisibility(View.VISIBLE);
                      textViewDiscount.setText(Discount);
                  }

                  String productImage = productGetterSetter.getProductImage();

                  if(!(TextUtils.isEmpty(productImage))){
                     imageViewPicature.setVisibility(View.VISIBLE);
                      Picasso.get().load(productImage).into(imageViewPicature);
                  }

                  String productName= productGetterSetter.getProductName();

                  if(!(TextUtils.isEmpty(productName))){
                      textViewProductName.setVisibility(View.VISIBLE);
                      textViewProductName.setText(productName);

                  }

                  String Price= productGetterSetter.getPrice();
                  if(!(TextUtils.isEmpty(Price))){
                      linearLayoutPrice.setVisibility(View.VISIBLE);
                      textViewPrice.setVisibility(View.VISIBLE);
                      textViewPrice.setText(Price);

                  }


                String desc=  productGetterSetter.getProductDesc();
                  if(!(TextUtils.isEmpty(desc))){
                      textViewProductDescription.setVisibility(View.VISIBLE);
                      textViewProductDescription.setText(desc);
                  }

                  String MRP= productGetterSetter.getMRP();
                  if(!(TextUtils.isEmpty(MRP))){
                      textViewMRP.setVisibility(View.VISIBLE);
                        textViewPriceOnlyText.setVisibility(View.VISIBLE);
                      buttonBuyNow.setVisibility(View.VISIBLE);
                      textViewMRP.setText(MRP);
                  }

                  final String productLink= productGetterSetter.getProductLink();
                  if(!TextUtils.isEmpty(productLink)){
                      buttonBuyNow.setVisibility(View.VISIBLE);

                    }

                    buttonBuyNow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                                         Intent intent = new Intent("com.rwn.rwnstudy.action.WEBVIEW");
                                                         intent.setType("text/plain");
                                                     intent.putExtra(Intent.EXTRA_TEXT,productLink);
                                                     startActivity(intent);
                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
}
