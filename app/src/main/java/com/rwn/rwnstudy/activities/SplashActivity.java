package com.rwn.rwnstudy.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rwn.rwnstudy.R;

public class SplashActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    String CURRENT_USER_ID;
    int a = 0;
    DatabaseReference databaseReferenceUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_splash);


        checking();

    }





    protected void checking() {


        SharedPreferences sharedPreferences = getSharedPreferences("logindetail", Context.MODE_PRIVATE);

        String login = sharedPreferences.getString("Login", "");


        switch (login) {
            case "AlreadyLogin":
                Intent intent = getIntent();
                String type;
                String id = null;
                String name=null;
                String product_id;
                product_id = intent.getStringExtra("product_id");
                Bundle s = intent.getExtras();
                if(s !=null){
                   id=   s.getString("id")  ;
                   name=   s.getString("name")  ;

                }
                Log.d("bbhbbbh", "checking: "+s);
                type = intent.getStringExtra("type");
                String store_name;
                store_name = intent.getStringExtra("store_name");





                if (type != null){
                    Intent intent1 = new Intent(SplashActivity.this,MainActivity.class);
                    intent1.putExtra("product_id",product_id);
                    intent1.putExtra("store_name",store_name);
                    intent1.putExtra("visit_user_id",id);
                    intent1.putExtra("username",name);
                    intent1.putExtra("type",type);

                    startActivity(intent1);
                    finish();
                }
                else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }

                break;
            case "":

                mAuth = FirebaseAuth.getInstance();

                try {
                    CURRENT_USER_ID = mAuth.getCurrentUser().getUid();
                } catch (Exception e) {

                } finally {

                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {

                        databaseReferenceUserRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.datbase_user_node)).child(CURRENT_USER_ID);


                        //databaseReferenceUserRef.keepSynced(true);

                        databaseReferenceUserRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (!(dataSnapshot.hasChild(getString(R.string.user_profile_image)))) {
                                        if (a == 0) {


                                            startActivity(new Intent(SplashActivity.this, ProfileImageUploaderActivity.class));
                                            finish();
                                            a++;}

                                    } else {
                                        Intent intent = getIntent();
                                        String type= null;
                                        String product_id = null;
                                        product_id = intent.getStringExtra("product_id");
                                        type = intent.getStringExtra("type");
                                        String store_name = null;
                                        store_name = intent.getStringExtra("store_name");



                                        String visit_user_id = intent.getStringExtra("visit_user_id");
                                        String username = intent.getStringExtra("username");
                                        if (type != null){
                                            Intent intent1 = new Intent(SplashActivity.this,MainActivity.class);
                                            intent1.putExtra("product_id",product_id);
                                            intent1.putExtra("store_name",store_name);
                                            intent1.putExtra("visit_user_id",visit_user_id);
                                            intent1.putExtra("username",username);
                                            intent1.putExtra("type",type);

                                            startActivity(intent1);
                                            finish();
                                        }else{
                                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                        finish();}
                                    }

                                }

                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                String s = databaseError.getMessage();

                                Toast.makeText(SplashActivity.this, s, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(SplashActivity.this, TeacherOrStudentsActivity.class));
                                finish();
                            }
                        }, 1000);

                    }
                }
                break;
            default:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this, TeacherOrStudentsActivity.class));
                        finish();
                    }
                }, 1000);

                break;
        }
    }

}
