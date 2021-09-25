package com.rwn.rwnstudy.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.rwn.rwnstudy.R;
//import com.rwn.rwnstudy.utilities.Constant;

import java.util.Objects;

public class ForgetPasswordResertActivity extends AppCompatActivity {

    EditText editTextEmail;
    Button button;
    FirebaseAuth mAuth;
    String user;
    ProgressBar progressBar;
    LinearLayout linearLayoutGreay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_resert);

        mAuth= FirebaseAuth.getInstance();


        Objects.requireNonNull(getSupportActionBar()).setTitle("Enter Email For Reset Password");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = getIntent().getStringExtra("Email");
        button= findViewById(R.id.button_send_reset_link);
        editTextEmail= findViewById(R.id.editText_email);
        progressBar= findViewById(R.id.progress_circular);
        linearLayoutGreay = findViewById(R.id.linear_layout_grey);

        editTextEmail.setText(user);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()) {
                    String userEmail = editTextEmail.getText().toString();
                    if (TextUtils.isEmpty(userEmail)) {
                        Toast.makeText(ForgetPasswordResertActivity.this, "Please Enter email id", Toast.LENGTH_SHORT).show();

                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        linearLayoutGreay.setVisibility(View.VISIBLE);
                        editTextEmail.setEnabled(false);
                        button.setEnabled(false);
                        mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener <Void>() {
                            @SuppressLint("NewApi")
                            @Override
                            public void onComplete(@NonNull Task <Void> task) {

                                if (task.isSuccessful()) {
                                    finish();

                                    progressBar.setVisibility(View.GONE);
                                    linearLayoutGreay.setVisibility(View.GONE);
                                    Toast.makeText(ForgetPasswordResertActivity.this, "Please Check your Mail id,\n for reset password", Toast.LENGTH_SHORT).show();
                                } else {
                                    editTextEmail.setEnabled(true);
                                    button.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    linearLayoutGreay.setVisibility(View.GONE);
                                    Toast.makeText(ForgetPasswordResertActivity.this, "Error Occurred :" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                    }
                }else{
                    Toast.makeText(ForgetPasswordResertActivity.this, "No Internet Connection Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}
