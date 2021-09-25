package com.rwn.rwnstudy.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rwn.rwnstudy.R;
import com.rwn.rwnstudy.utilities.ConstantForApp;
import com.rwn.rwnstudy.utilities.TabPagerAdapter;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE = 20;
    private FirebaseAuth mAuth;
    private TextView textViewCount;
    private String user;
    private DatabaseReference databaseReferenceUserRef, databaseReference;
    CircularProgressDrawable circularProgressDrawable;
    private TabPagerAdapter tabPagerAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Boolean Console = false;
    public static int counter = 0, counterForFrndReq = 0;

    public static boolean isAppRunning = false;
    Uri cameraImageUri, imageUri;
    private static final int ACTION_IMAGE_CAPTURE_FOR_STATUS = 500;

    protected boolean exit = false;
    String filePath;
    FloatingActionButton fab, fabContact;
    String CURRENT_USER;
    private CircleImageView circleImageViewProfilePicture;
    private TextView textViewUsername;
    NavigationView navigationView;
    Boolean mStoragePermission = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isAppRunning = true;
        viewIntialization();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        // Use an activity context to get the rewarded video instance.


        Objects.requireNonNull(getSupportActionBar()).setTitle("RWN");

        circularProgressDrawable = new CircularProgressDrawable(MainActivity.this);


        getPendingIntent();

        //    MyFirebaseMessagingService.notificationManager.cancelAll();
        circularProgressDrawable.start();
        fab = findViewById(R.id.fab);

        fabContact = findViewById(R.id.fab_contact);

        fabContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FriendsActivity.class));

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStoragePermission) {
                    runForGallary();
                } else {
                    Console = true;
                    verifyStoragePermission();
                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        prifileDetailSetImageandName();

        clickActionEvent();

        statusThingArrangment();

        updateCurrentStatus("Online");

        Fabric.with(this, new Crashlytics());
        NotificationUpdate notificationUpdate = new NotificationUpdate();
        notificationUpdate.start();
    }

    private void getPendingIntent() {



        Intent intent = getIntent();
        boolean b =intent.hasExtra("type");
        String type;
        String product_id;
        product_id = intent.getStringExtra("product_id");
        type = intent.getStringExtra("type");
        String store_name;
        store_name = intent.getStringExtra("store_name");
        String visit_user_id = intent.getStringExtra("visit_user_id");
        String username = intent.getStringExtra("username");

       if(type != null) {
           switch (type) {
               case "notification": {
                   Intent intent1 = new Intent(MainActivity.this, NotificationShowerActivity.class);
                   intent1.putExtra("product_id", product_id);
                   intent1.putExtra("store_name", store_name);
                   intent1.putExtra("type", store_name);
                   startActivity(intent1);
                   break;
               }
               case "friendReq": {
                   Intent intent1 = new Intent(MainActivity.this, FriendRequestActivity.class);
                   startActivity(intent1);
                   break;
               }
               case "message": {

                   Intent intent1 = new Intent(MainActivity.this, ChatActivity.class);
                   intent1.putExtra("visit_user_id", visit_user_id);
                   intent1.putExtra("username", username);
                   startActivity(intent1);

                   break;
               }
           }
    }

    }

    private void runForGallary() {
        Intent intentGallary = new Intent();
        intentGallary.setAction(Intent.ACTION_GET_CONTENT);
        CropImage.activity()
                .start(MainActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAppRunning = false;
        updateCurrentStatus("Offline");
    }

    private void statusThingArrangment() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {

                    fabContact.setVisibility(View.INVISIBLE);
                    fab.setVisibility(View.VISIBLE);
                }
                if (tab.getPosition() == 1) {

                    fabContact.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.INVISIBLE);


                }
                if (tab.getPosition() == 2) {
                    if (mStoragePermission) {

                        fabContact.setVisibility(View.INVISIBLE);
                        fab.setVisibility(View.INVISIBLE);

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(getPackageManager()) != null) {

                            File photoFile = null;
                            try {
                                photoFile = createImageFile();

                            } catch (Exception e) {

                            }
                            if (photoFile != null) {
                                Uri photoUri = FileProvider.getUriForFile(
                                        MainActivity.this,
                                        "com.rwn.rwnstudy.fileprovider",
                                        photoFile
                                );
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);


                                startActivityForResult(intent, ACTION_IMAGE_CAPTURE_FOR_STATUS);
                            }
                        }
                    } else {
                        verifyStoragePermission();
                    }
                }


            }


            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void verifyStoragePermission() {

            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permission[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permission[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permission[2]) == PackageManager.PERMISSION_GRANTED) {
            mStoragePermission = true;
            if (Console) {
                runForGallary();
            } else {

                dispatchTakePictureIntent();
            }
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    permission,
                    REQUEST_CODE

            );
        }
    }

    private void clickActionEvent() {
        circleImageViewProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, ProfileVisitActivity.class));

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isOnline()) {
            Toast.makeText(this, "you are in Offline mode Some features may not work proper", Toast.LENGTH_LONG).show();
        }
    }

    public void updateCurrentStatus(String state) {
        String saveCurrentDate, saveCurrentTime;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        Calendar calendarTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendarTime.getTime());

        HashMap hashMapCurrentState = new HashMap();
        hashMapCurrentState.put("Date", saveCurrentDate);
        hashMapCurrentState.put("Time", saveCurrentTime);
        hashMapCurrentState.put("Type", state);

        databaseReferenceUserRef.child(CURRENT_USER).child("UserState").updateChildren(hashMapCurrentState);


    }

    private void dispatchTakePictureIntent() {


        if (mStoragePermission) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {

                File photoFile = null;
                try {
                    photoFile = createImageFile();

                } catch (Exception e) {

                }
                if (photoFile != null) {
                    Uri photoUri = FileProvider.getUriForFile(
                            MainActivity.this,
                            "com.rwn.rwnstudy.fileprovider",
                            photoFile
                    );
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);


                    startActivityForResult(intent, ACTION_IMAGE_CAPTURE_FOR_STATUS);
                }
            }
        } else {
            verifyStoragePermission();
        }
    }

    private File createImageFile() {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir);
            filePath = image.getAbsolutePath();


        } catch (IOException e) {

        }
        return image;

    }


    private void visibleItem() {
        navigationView = findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_facilties_docs).setVisible(true);
    }

    //    @Override
//    protected void onResume() {
//        super.onResume();
//
//        updateCurrentStatus("Online");
//    }
    private void invisibleItem() {
        navigationView = findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_update_Subject).setVisible(false);
    }

    private void prifileDetailSetImageandName() {

        databaseReferenceUserRef.child(CURRENT_USER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {

                    String profileLink = null;
                    if (dataSnapshot.hasChild("profileImage")) {
                        profileLink = dataSnapshot.child("profileImage").getValue().toString();
                    }
                    Picasso.get().load(profileLink).resize(200, 200).noFade().placeholder(R.drawable.profile).into(circleImageViewProfilePicture);



                    String Fullname = dataSnapshot.child("name").getValue().toString();

                    textViewUsername.setText(Fullname);
                    user = dataSnapshot.child("userType").getValue().toString();
                    if (user.equals(ConstantForApp.TEACHER)) {


                    } else {

                        visibleItem();
                        invisibleItem();

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void viewIntialization() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);


        viewPager = findViewById(R.id.pageviewer_main_tab_pager);
        tabLayout = findViewById(R.id.tabLayout_MainTab);

        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(tabPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);


        circleImageViewProfilePicture = navigationView.getHeaderView(0).findViewById(R.id.circularView_profile_Image);
        textViewUsername = navigationView.getHeaderView(0).findViewById(R.id.textview_username);

        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            CURRENT_USER = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        }


        FirebaseMessaging.getInstance().subscribeToTopic("Update");
       // FirebaseMessaging.getInstance().subscribeToTopic("testingOreo");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReferenceUserRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.datbase_user_node));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            if (exit) {
                finish();
            } else {
                Toast.makeText(this, "Double click for exit", Toast.LENGTH_SHORT).show();
            }
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 2000);
            exit = true;
        }
    }

    public static Drawable convertLayoutToImage(Context mContext, int count, int drawableId) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.notification_layout, null);
        ((ImageView) view.findViewById(R.id.icon_badge)).setImageResource(drawableId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.textView_count);
            textView.setText("" + count);
        }

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(mContext.getResources(), bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem menuItem = menu.findItem(R.id.notification_action);
        final MenuItem menuItem2 = menu.findItem(R.id.notification_frnd_req);

        menuItem.setIcon(convertLayoutToImage(MainActivity.this, counter, R.drawable.ic_notifications_black_24dp));

        menuItem2.setIcon(convertLayoutToImage(MainActivity.this, counterForFrndReq, R.drawable.ic_person_add_black_24dp));


        return true;
    }

    public void onAddProduct() {
        counter++;
        invalidateOptionsMenu();


    }

    public void onRemoveProduct() {
        counter--;
        invalidateOptionsMenu();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.notification_action) {
            startActivity(new Intent(MainActivity.this, NotificationShowActivity.class));
            return true;
        }
        if (id == R.id.notification_frnd_req) {
            startActivity(new Intent(MainActivity.this, FriendRequestActivity.class));
            return true;
        }
        if (id == R.id.notification_more) {

            databaseReference.child("AppLinks").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                      String links=  dataSnapshot.child("ApkLinks").getValue(String.class);

                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(links));

                        startActivity(i);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return true;
        }     if (id == R.id.notification_playstore) {

            databaseReference.child("AppLinks").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                      String links=  dataSnapshot.child("links").getValue(String.class);

                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(links));

                        startActivity(i);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return true;
        }

        return super.onOptionsItemSelected(item);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_facilties_docs) {

            startActivity(new Intent(MainActivity.this, FacultiesDocumentActivity.class));
        } else if (id == R.id.nav_personal_docs) {
            Intent intent = new Intent(MainActivity.this, PersonalDocumantUploadingActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_update_Subject) {
            startActivity(new Intent(MainActivity.this, UpdateWhatClassTeacherStudied.class));

        } else if (id == R.id.nav_log_out) {
            saveSharedPrefrence();

            logout();


        } else if (id == R.id.nav_video) {

            if (isOnline()) {


                databaseReference.child("VideoUrl").addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            String link = Objects.requireNonNull(dataSnapshot.child("URL").getValue()).toString();
                            Intent intent = new Intent("com.rwn.rwnstudy.action.WEBVIEW");
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT, link);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                Toast.makeText(this, "No Internet Connection Found", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_ask_for_docs) {
            if (user.equals(ConstantForApp.TEACHER)) {
                databaseReferenceUserRef.child(CURRENT_USER).child("department").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()) {

                            String dep =dataSnapshot.getValue(String.class);
                            Intent intent = new Intent(MainActivity.this, OpenclassDetailActivity.class);
                            intent.putExtra("classWithSection", dep);
                            intent.putExtra("teacher", "teacher");
                            startActivity(intent);
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                startActivity(new Intent(MainActivity.this, AskForDocsActivity.class));
            }


        } else if (id == R.id.nav_your_friend) {
            startActivity(new Intent(MainActivity.this, YourFriendActivity.class));

        } else if (id == R.id.nav_offers) {
            startActivity(new Intent(MainActivity.this, OfferPageActivity.class));

        } else if (id == R.id.nav_share) {

            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "RWN Study");
                String sAux = "\n   Hey i am sharing you our college complete app for Study notes, Social App, and also Deals and many offers on another platform like Amazon,Flipkart,Paytm,etc" +
                        "    \n !!!! Hurry Download\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=com.rwn.rwnstudy\n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            } catch (Exception e) {
                //e.toString();
            }


        } else if (id == R.id.nav_send) {

            Intent intent = new Intent(MainActivity.this, FeedBackActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void saveSharedPrefrence() {


        SharedPreferences sharedPreferences = getSharedPreferences("logindetail", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Login", "");
        editor.apply();

    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        updateCurrentStatus("Offline");
//    }


    private void logout() {
        updateCurrentStatus("Offline");

        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, TeacherOrStudentsActivity.class));
        finish();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ACTION_IMAGE_CAPTURE_FOR_STATUS && resultCode == RESULT_OK) {
            cameraImageUri = Uri.fromFile(new File(filePath));

            tabLayout.getTabAt(0).select();

            Intent intent = new Intent(MainActivity.this, StatusUploadActivity.class);
            intent.putExtra("Uri", cameraImageUri.toString());
            startActivityForResult(intent, 303);


        } else if (resultCode == RESULT_CANCELED) {

            tabLayout.getTabAt(0).select();
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                imageUri = result.getUri();
                Intent intent = new Intent(MainActivity.this, StatusUploadActivity.class);
                intent.putExtra("Uri", imageUri.toString());
                startActivityForResult(intent, 303);
            } else if (resultCode == RESULT_CANCELED) {
                tabLayout.getTabAt(0).select();
            }
        }
        if (requestCode == 303) {
            if (resultCode == 57) {
                Picasso.get().load(R.drawable.progress).resize(100, 100).noFade().into(StatusFragment.circleImageViewOwn);


            } else if (resultCode == 4) {
                Toast.makeText(this, "Status Uploaded", Toast.LENGTH_SHORT).show();
            }
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "grant permission", Toast.LENGTH_SHORT).show();
//                } else {
//
//                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
//                }
//                if (grantResults.length > 0
//                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "grant permission", Toast.LENGTH_SHORT).show();
//
//
//                } else
//                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
//                if (grantResults.length > 0
//                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "grant permission", Toast.LENGTH_SHORT).show();
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                } else
//                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show();

                if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0 && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
                }


            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    class NotificationUpdate extends Thread {

        MenuItem menuItem;

        NotificationUpdate() {

        }

        @Override
        public void run() {
            super.run();

            Query query2 = databaseReference.child("DocumentAcccessRequest").child(CURRENT_USER).orderByChild("request_type").equalTo("received");
            query2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    counterForFrndReq = (int) dataSnapshot.getChildrenCount();
                    Log.d("FALLAANA fr", "onDataChange: " + counterForFrndReq);

                    fun(counterForFrndReq);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            Query query = databaseReference.child("NotificationViewer").orderByChild(CURRENT_USER).equalTo("false");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    counter = (int) dataSnapshot.getChildrenCount();
                    Log.d("FALLAANA", "onDataChange: " + counter);

                    fun(counter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    private void fun(int counter) {

        invalidateOptionsMenu();

    }
}