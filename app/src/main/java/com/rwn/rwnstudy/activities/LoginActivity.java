package com.rwn.rwnstudy.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.rwn.rwnstudy.R;
import com.rwn.rwnstudy.utilities.ConstantForApp;

import java.util.Objects;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    // UI references.
    private String user;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private TextView textViewRegister, textViewForget;

    private Button mEmailSignInButton;
    private ProgressBar progressBar;
    LinearLayout linearLayoutGrey;

    private FirebaseAuth mAuth;
    private boolean emailAddressChecker;
    private DatabaseReference databaseReferenceUserRef;
    int a = 0;
    String usertype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Log in with your Account");




        userIdentificatioTeacherOrStudent();

        viewIntialization();

        buttonClickAction();


    }

    @Override
    protected void onStart() {
        super.onStart();


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

    private void buttonClickAction() {

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isOnline()) {
                    validation();
                } else {
                    Toast.makeText(LoginActivity.this, "No Internet Connection found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra(ConstantForApp.USER, user);
                startActivity(intent);
                finish();
            }
        });
        textViewForget.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordResertActivity.class);
                intent.putExtra("Email", mEmailView.getText());
                startActivity(intent);
            }
        });

    }

    private void validation() {
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();


        if (password.isEmpty()) {
            mPasswordView.setError("Field must be Fill.");
            mPasswordView.requestFocus();
        } else if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            mEmailView.setError("Enter Valid Mail");
            mEmailView.requestFocus();
        } else {
            setUpProgressBar(View.VISIBLE);
            viewEnabledAndDisabled(false);
            singInfrommailIdandPassword(email, password);
        }
    }

    private void singInfrommailIdandPassword(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener <AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task <AuthResult> task) {
                        if (task.isSuccessful()) {


                            final String CURRENT_USER_ID = mAuth.getCurrentUser().getUid();


                            databaseReferenceUserRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.datbase_user_node));
                            final DatabaseReference databaseReferenceUserRefernce = FirebaseDatabase.getInstance().getReference().child(getString(R.string.datbase_user_node)).child(CURRENT_USER_ID);

                            databaseReferenceUserRefernce.child("userType").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                    if (dataSnapshot.exists()) {
                                        usertype = dataSnapshot.getValue().toString();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            databaseReferenceUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                    if (usertype.equals(user)) {

                                        if (!dataSnapshot.hasChild(CURRENT_USER_ID) && !dataSnapshot.child(CURRENT_USER_ID).hasChild(getString(R.string.user_profile_image))) {

                                            if (a == 0) {

                                                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                                                finish();
                                                setUpProgressBar(View.GONE);
                                                viewEnabledAndDisabled(true);
                                                a++;
                                            }
                                        } else {
                                            if (a == 0) {


                                                FirebaseInstanceId.getInstance().getInstanceId()
                                                        .addOnCompleteListener(new OnCompleteListener <InstanceIdResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task <InstanceIdResult> task) {
                                                                if (!task.isSuccessful()) {
                                                                    return;
                                                                }

                                                                // Get new Instance ID token
                                                                String Device_token = task.getResult().getToken();

                                                                databaseReferenceUserRefernce.child("deviceToken").setValue(Device_token).addOnCompleteListener(new OnCompleteListener <Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task <Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                                            saveSharedPrefrence();
                                                                            finish();
                                                                            setUpProgressBar(View.GONE);
                                                                            viewEnabledAndDisabled(true);
                                                                            a++;
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        });


                                            }
                                        }
                                    } else {
                                        setUpProgressBar(View.GONE);
                                        viewEnabledAndDisabled(true);
                                        Toast.makeText(LoginActivity.this, "Account not found in " + user + "Directory", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    String s = databaseError.getMessage();
                                    setUpProgressBar(View.GONE);
                                    viewEnabledAndDisabled(true);
                                    Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {


                            String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();

                            switch (errorCode) {

                                case "ERROR_INVALID_CUSTOM_TOKEN":
                                    setUpProgressBar(View.GONE);
                                    viewEnabledAndDisabled(true);
                                    Toast.makeText(LoginActivity.this, "The custom token format is incorrect. Please check the documentation.", Toast.LENGTH_LONG).show();
                                    break;

                                case "ERROR_CUSTOM_TOKEN_MISMATCH":
                                    setUpProgressBar(View.GONE);
                                    viewEnabledAndDisabled(true);
                                    Toast.makeText(LoginActivity.this, "The custom token corresponds to a different audience.", Toast.LENGTH_LONG).show();
                                    break;

                                case "ERROR_INVALID_CREDENTIAL":
                                    setUpProgressBar(View.GONE);
                                    viewEnabledAndDisabled(true);
                                    Toast.makeText(LoginActivity.this, "The supplied auth credential is malformed or has expired.", Toast.LENGTH_LONG).show();
                                    break;

                                case "ERROR_INVALID_EMAIL":
                                    setUpProgressBar(View.GONE);
                                    viewEnabledAndDisabled(true);
                                    Toast.makeText(LoginActivity.this, "The email address is invalid.", Toast.LENGTH_LONG).show();

                                    break;

                                case "ERROR_WRONG_PASSWORD":
                                    setUpProgressBar(View.GONE);
                                    viewEnabledAndDisabled(true);
                                    Toast.makeText(LoginActivity.this, "The password is invalid.", Toast.LENGTH_LONG).show();

                                    break;

                                case "ERROR_USER_MISMATCH":
                                    setUpProgressBar(View.GONE);
                                    viewEnabledAndDisabled(true);
                                    Toast.makeText(LoginActivity.this, "The supplied credentials do not correspond to the previously signed in user.", Toast.LENGTH_LONG).show();
                                    break;

                                case "ERROR_REQUIRES_RECENT_LOGIN":
                                    setUpProgressBar(View.GONE);
                                    viewEnabledAndDisabled(true);
                                    Toast.makeText(LoginActivity.this, "This operation is sensitive and requires recent authentication. Log in again before retrying this request.", Toast.LENGTH_LONG).show();
                                    break;

                                case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                    setUpProgressBar(View.GONE);
                                    viewEnabledAndDisabled(true);
                                    Toast.makeText(LoginActivity.this, "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.", Toast.LENGTH_LONG).show();
                                    break;

                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    setUpProgressBar(View.GONE);
                                    viewEnabledAndDisabled(true);
                                    Toast.makeText(LoginActivity.this, "The email address is already in use by another account.   ", Toast.LENGTH_LONG).show();

                                    break;

                                case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                    setUpProgressBar(View.GONE);
                                    viewEnabledAndDisabled(true);
                                    Toast.makeText(LoginActivity.this, "This credential is already associated with a different user account.", Toast.LENGTH_LONG).show();
                                    break;

                                case "ERROR_USER_DISABLED":
                                    setUpProgressBar(View.GONE);
                                    viewEnabledAndDisabled(true);
                                    Toast.makeText(LoginActivity.this, "The user account has been disabled by an administrator.", Toast.LENGTH_LONG).show();
                                    break;

                                case "ERROR_USER_TOKEN_EXPIRED":
                                    setUpProgressBar(View.GONE);
                                    viewEnabledAndDisabled(true);
                                    Toast.makeText(LoginActivity.this, "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                                    break;

                                case "ERROR_USER_NOT_FOUND":
                                    setUpProgressBar(View.GONE);
                                    viewEnabledAndDisabled(true);
                                    Toast.makeText(LoginActivity.this, "There is no user record.", Toast.LENGTH_LONG).show();
                                    break;

                                case "ERROR_INVALID_USER_TOKEN":
                                    setUpProgressBar(View.GONE);
                                    viewEnabledAndDisabled(true);
                                    Toast.makeText(LoginActivity.this, "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                                    break;

                                case "ERROR_OPERATION_NOT_ALLOWED":
                                    setUpProgressBar(View.GONE);
                                    viewEnabledAndDisabled(true);
                                    Toast.makeText(LoginActivity.this, "This operation is not allowed. You must enable this service in the console.", Toast.LENGTH_LONG).show();
                                    break;

                                case "ERROR_WEAK_PASSWORD":
                                 setUpProgressBar(View.GONE);
                                 viewEnabledAndDisabled(true);
                                    Toast.makeText(LoginActivity.this, "The given password is invalid.", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }


                    }
                });
    }

    private void saveSharedPrefrence() {


        SharedPreferences sharedPreferences = getSharedPreferences("logindetail", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Login", "AlreadyLogin");
        editor.apply();

    }


    private void viewIntialization() {


        mEmailView =  findViewById(R.id.editText_email);
        textViewRegister = findViewById(R.id.textView_register);
        mEmailSignInButton =  findViewById(R.id.email_sign_in_button);
        mPasswordView =  findViewById(R.id.editText_password);
        textViewForget = findViewById(R.id.textView_forgotPassword);

        progressBar = findViewById(R.id.progress_circular);
        linearLayoutGrey = findViewById(R.id.linear_layout_grey);


        setUpProgressBar(View.GONE);
        viewEnabledAndDisabled(true);
        mAuth = FirebaseAuth.getInstance();
    }

    private void viewEnabledAndDisabled(boolean b) {

        textViewForget.setEnabled(b);
        textViewRegister.setEnabled(b);
        mEmailSignInButton.setEnabled(b);
        mPasswordView.setEnabled(b);
        mEmailView.setEnabled(b);

    }


    private void setUpProgressBar(int visible) {
        progressBar.setVisibility(visible);
        linearLayoutGrey.setVisibility(visible);
    }


    private void userIdentificatioTeacherOrStudent() {

        user = getIntent().getStringExtra(ConstantForApp.USER);


    }
}


