package com.rwn.rwnstudy.activities;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.firebase.messaging.FirebaseMessaging;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment {

    RecyclerView recyclerViewRecentStattus;

    static public CircleImageView circleImageViewOwn;
    TextView textViewOwnName, textViewOwnStatus;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    StorageReference firebaseStorageDocumentuplod;
    ImageView imageViewMore;
    String CURRENT_USER, friendId, userType;
    View view;
    String TAG = "THEREDING";
    String saveCurrentDate;
    int saveCurrentDateinInt, saveCurrentHour, saveCurrentMin;
    LinearLayout linearLayout;
    int count = 0, counter = 0;
    ImageView imageViewForAD;
    TextView textViewForadTitel, textViewForAdDesc;
    LinearLayout linearLayoutForAd;


    public StatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_status, container, false);

        textViewOwnName = view.findViewById(R.id.textView_chatting_userProfile_Fullname);
        linearLayout = view.findViewById(R.id.linearlayout_bar);
        circleImageViewOwn = view.findViewById(R.id.circularView_student_profile);
        textViewOwnStatus = view.findViewById(R.id.textView_Satus);
        recyclerViewRecentStattus = view.findViewById(R.id.recyclerViewRecentStatus);
        imageViewMore = view.findViewById(R.id.imageView_more);
        imageViewForAD = view.findViewById(R.id.imageView_for_Ad);
        textViewForAdDesc = view.findViewById(R.id.textView_description_of_ad);
        textViewForadTitel = view.findViewById(R.id.textView_titel_of_ad);
        linearLayoutForAd = view.findViewById(R.id.linearlayout_forStatus_ad);


        recyclerViewRecentStattus.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager;
        linearLayoutManager = new LinearLayoutManager(getContext());

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewRecentStattus.setLayoutManager(linearLayoutManager);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        CURRENT_USER = mAuth.getCurrentUser().getUid();
        firebaseStorageDocumentuplod = FirebaseStorage.getInstance().getReference().child("Images").child("StatusImages").child(CURRENT_USER);


        textViewOwnStatus.setText("Click on + icon for update Status");
        Calendar calendardate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd");
        saveCurrentDate = currentDate.format(calendardate.getTime());


        SimpleDateFormat currentDateFor = new SimpleDateFormat("dd");
        saveCurrentDateinInt = Integer.parseInt(currentDateFor.format(calendardate.getTime()));

        SimpleDateFormat currentHour = new SimpleDateFormat("kk");
        saveCurrentHour = Integer.parseInt(currentHour.format(calendardate.getTime()));

        SimpleDateFormat currentMin = new SimpleDateFormat("mm");
        saveCurrentMin = Integer.parseInt(currentMin.format(calendardate.getTime()));

        viewIntializing();


        databaseReference.child("Users").child(CURRENT_USER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserGetterAndSetter userGetterAndSetter = dataSnapshot.getValue(UserGetterAndSetter.class);

                    userType = userGetterAndSetter.getUserType();
                    String name = userGetterAndSetter.getName();
                    String profileImage = userGetterAndSetter.getProfileImage();

                    Picasso.get().load(profileImage).resize(50, 50).noFade().into(circleImageViewOwn);
                    textViewOwnName.setText(name);
                    textViewOwnStatus.setText("Click on + icon for update Status");


                    personalAdFOrApp(userGetterAndSetter);


                   //adFunctionForStatus(container.getContext());
                    Start();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        return view;

    }

    private void personalAdFOrApp(UserGetterAndSetter userGetterAndSetter) {

        databaseReference.child("Advertisement").child(userGetterAndSetter.getCourse()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    final String image = dataSnapshot.child("image").getValue(String.class);
                    String title = dataSnapshot.child("title").getValue(String.class);
                    String decs = dataSnapshot.child("decs").getValue(String.class);
                    if (image != null) {
                        imageViewForAD.setVisibility(View.VISIBLE);
                        Picasso.get().load(image).resize(400, 50).noFade().into(imageViewForAD);
                    } else if (title != null) {
                        textViewForAdDesc.setVisibility(View.VISIBLE);
                        textViewForadTitel.setVisibility(View.VISIBLE);
                        textViewForadTitel.setText(title);
                        textViewForAdDesc.setText(decs);
                    }

                    linearLayoutForAd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                          String productId=  dataSnapshot.child("product_id").getValue(String.class);
                          String Storename=  dataSnapshot.child("store_name").getValue(String.class);

                          Intent intent= new Intent(getContext(), NotificationShowerActivity.class);

                          intent.putExtra("product_id",productId);
                          intent.putExtra("store_name",Storename);

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

//    private void adFunctionForStatus(Context context) {
//
//
//        MobileAds.initialize(context, getString(R.string.banner_home_footer));
//        AdView adView = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//            adView = new AdView(Objects.requireNonNull(getContext()));
//            adView.setAdSize(AdSize.BANNER);
//            adView.setAdUnitId(getString(R.string.banner_home_footer));
//
//
//            mAdView = view.findViewById(R.id.adView);
//            AdRequest adRequest = new AdRequest.Builder().build();
//            mAdView.loadAd(adRequest);
//        }
//        mAdView.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                textViewForAdDesc.setVisibility(View.GONE);
//                textViewForAdDesc.setVisibility(View.GONE);
//                imageViewForAD.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//
//                Log.d("ADMOBTESTING", "onAdLoaded:  StatusFragment Activity : failed " + errorCode);
//            }
//
//            @Override
//            public void onAdOpened() {
//
//                Log.d("ADMOBTESTING", "onAdLoaded:  StatusFragment Activity : opened ");
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//
//                Log.d("ADMOBTESTING", "onAdLoaded:  StatusFragment Activity : ad left application ");
//            }
//
//            @Override
//            public void onAdClosed() {
//
//                Log.d("ADMOBTESTING", "onAdLoaded:  StatusFragment Activity : ad Closed ");
//            }
//        });
//    }


    public void Start() {


        Log.d("Checking", "onStart: ");


        Query query = databaseReference.child("DocumentAccessAuthority").child(CURRENT_USER).orderByValue().equalTo(true);

        FirebaseRecyclerOptions <Boolean> options =
                new FirebaseRecyclerOptions.Builder <Boolean>()
                        .setQuery(query, Boolean.class)
                        .build();
        FirebaseRecyclerAdapter <Boolean, FriendStatusViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter <Boolean, FriendStatusViewHolder>(options) {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    protected void onBindViewHolder(@NonNull final FriendStatusViewHolder holder, int position, @NonNull Boolean model) {

                        holder.itemView.setVisibility(View.GONE);
                        final int pod = position;
                        friendId = getRef(position).getKey();
                        String node = null;

                        if (userType.equals(ConstantForApp.TEACHER)) {

                            node = "Teachers";
                        } else {
                            node = "Students";
                        }
                        final String finalNode = node;

                        final String fid = friendId;
                        final FriendStatusViewHolder holder1 = holder;
                        final int position1 = position;
                        final String finalNode1 = node;
                        databaseReference.child("Status").child(node).child(Objects.requireNonNull(getRef(position).getKey())).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {
                                    holder1.itemView.setVisibility(View.VISIBLE);

                                    MyThread myThread;
                                    ;


                                    counter = (int) dataSnapshot.getChildrenCount();


                                    count = 0;
                                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                                        String key = d.getKey();
                                        databaseReference.child("StatusViewer").child(key).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    if (dataSnapshot.hasChild(CURRENT_USER)) {
                                                        count++;
                                                        statusNotification(count, holder1);
                                                    }
                                                } else
                                                    statusNotification(count, holder1);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        StatusGetterSetter statusGetterSetter = d.getValue(StatusGetterSetter.class);

                                        Uri link = Uri.parse(statusGetterSetter.getLink());

                                        Log.d(TAG, "onDataChange: before condition" + key);
                                        int dateForCom = Integer.parseInt(statusGetterSetter.getDate());
                                        int hourForCom = Integer.parseInt(statusGetterSetter.getHour());
                                        int minForCom = Integer.parseInt(statusGetterSetter.getMin());

                                        if (dateForCom < saveCurrentDateinInt) {
                                            if (hourForCom <= saveCurrentHour) {
                                                if (hourForCom == saveCurrentHour) {
                                                    if (minForCom < saveCurrentMin) {
                                                        Log.d(TAG, "onDataChange: " + key);
                                                        myThread = new MyThread(key, getRef(pod).getKey(), finalNode1);
                                                        myThread.start();
                                                    }
                                                } else {
                                                    myThread = new MyThread(key, getRef(pod).getKey(), finalNode1);
                                                    myThread.start();
                                                }
                                            } else {
                                                int a = Integer.parseInt(String.valueOf(hourForCom));
                                                if (a++ < saveCurrentDateinInt) {
                                                    myThread = new MyThread(key, getRef(pod).getKey(), finalNode1);
                                                    myThread.start();
                                                }
                                            }
                                        }


                                        Picasso.get().load(link).resize(50, 50).noFade().into(holder.circleImageView);


                                        String uid = statusGetterSetter.getUid();

                                        String date;
                                        if (saveCurrentDate.equals(statusGetterSetter.getDate())) {
                                            date = "Today: ";
                                        } else {
                                            date = "Yesterday: ";
                                        }

                                        holder1.textViewTime.setText(date + statusGetterSetter.getHour() + ":" + statusGetterSetter.getMin());
                                        databaseReference.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    UserGetterAndSetter userGetterAndSetter = dataSnapshot.getValue(UserGetterAndSetter.class);
                                                    holder1.textViewName.setText(userGetterAndSetter.getName());

                                                    holder1.itemView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            String friendIds = getRef(position1).getKey();

                                                            Intent intent = new Intent(getContext(), StatusImageShowActivity.class);
                                                            intent.putExtra("node", finalNode);
                                                            intent.putExtra("friendsId", friendIds);
                                                            startActivity(intent);
                                                            Log.d("ETETET", "onClick: " + friendIds + "node is" + finalNode);

                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                    }


                                } else {
                                    ViewGroup.LayoutParams params = holder.linearLayout.getLayoutParams();
                                    params.height = 0;
                                    params.width = 0;
                                    holder.linearLayout.setLayoutParams(params);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }


                    @NonNull
                    @Override
                    public FriendStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.status_layout_for_only_status, parent, false);
                        FriendStatusViewHolder friendRequestViewHolder = new FriendStatusViewHolder(view);
                        return friendRequestViewHolder;
                    }
                };
        recyclerViewRecentStattus.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    private void statusNotification(int count, FriendStatusViewHolder holder1) {
        int a = counter - count;

        if (a >= 1) {
            holder1.frameLayout.setVisibility(View.VISIBLE);

            holder1.textView.setText(String.valueOf(a));

        } else {
            holder1.frameLayout.setVisibility(View.INVISIBLE);
        }
    }


    public static class FriendStatusViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView textViewName, textViewTime;
        FrameLayout frameLayout;
        TextView textView;
        LinearLayout linearLayout;


        public FriendStatusViewHolder(View itemView) {

            super(itemView);


            circleImageView = itemView.findViewById(R.id.circularView_student_profile);
            textViewName = itemView.findViewById(R.id.textView_chatting_userProfile_Fullname);
            textViewTime = itemView.findViewById(R.id.textView_Satus);
            frameLayout = itemView.findViewById(R.id.counterValuePanel);
            textView = itemView.findViewById(R.id.textView_count);
            linearLayout = itemView.findViewById(R.id.linearlayout_Status_only_Stattus);

        }


    }

    private void viewIntializing() {


        databaseReference.child("Users").child(CURRENT_USER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final UserGetterAndSetter userGetterAndSetter = dataSnapshot.getValue(UserGetterAndSetter.class);

                    userType = userGetterAndSetter.getUserType();


                    String collegename = userGetterAndSetter.getCollegeName().replace(" ", "");

                    Log.d("ndndndnd", "onDataChange: " + collegename);
                    //    FirebaseMessaging.getInstance().subscribeToTopic();

                    if (!userGetterAndSetter.getDepartment().isEmpty()) {
                        String dep = userGetterAndSetter.getDepartment();
                        FirebaseMessaging.getInstance().subscribeToTopic(dep);
                    }
                    Log.d("gfujne", "onDataChange: "+ CURRENT_USER);

                    if (!userGetterAndSetter.getCourse().isEmpty() && !userGetterAndSetter.getCourse().equals("N/A")) {
                        String corse = userGetterAndSetter.getCourse();
                        FirebaseMessaging.getInstance().subscribeToTopic(corse);



                        databaseReference.child(userGetterAndSetter.getCourse()).orderByValue().equalTo(userGetterAndSetter.getSemester()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot dq :
                                            dataSnapshot.getChildren()) {

                                        String a = dq.getKey();

                                        FirebaseMessaging.getInstance().subscribeToTopic(a);

                                        FirebaseMessaging.getInstance().subscribeToTopic(a + userGetterAndSetter.getSection());

                                        Log.d("ndndndnd", "onDataChange: " + a + userGetterAndSetter.getSection());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    final String node;


                    if (userType.equals(ConstantForApp.TEACHER)) {

                        node = "Teachers";
                    } else {
                        node = "Students";
                    }

                    statusTimeChecker(node, userGetterAndSetter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void statusTimeChecker(final String node, final UserGetterAndSetter userGetterAndSetter) {

        final String name = userGetterAndSetter.getName();
        databaseReference.child("Status").child(node).child(CURRENT_USER).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    imageViewMore.setVisibility(View.VISIBLE);
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        String key = d.getKey();
                        StatusGetterSetter statusGetterSetter = d.getValue(StatusGetterSetter.class);

                        Uri link = Uri.parse(statusGetterSetter.getLink());

                        int dateForCom = Integer.parseInt(statusGetterSetter.getDate());
                        int hourForCom = Integer.parseInt(statusGetterSetter.getHour());
                        int minForCom = Integer.parseInt(statusGetterSetter.getMin());
                        if (dateForCom < saveCurrentDateinInt) {
                            if (hourForCom <= saveCurrentHour) {
                                if (hourForCom == saveCurrentHour) {
                                    if (minForCom < saveCurrentMin) {
                                        Log.d(TAG, "onDataChange: " + key);
                                        MyThread myThread = new MyThread(key, CURRENT_USER, node);
                                        myThread.start();
                                    }
                                } else {
                                    MyThread myThread = new MyThread(key, CURRENT_USER, node);
                                    myThread.start();
                                }
                            }
                        }


                        Picasso.get().load(link).resize(50, 50).noFade().into(circleImageViewOwn);

                        textViewOwnName.setText(name);
                        textViewOwnStatus.setText("Click on + icon for update Status");


                        String date;
                        if (saveCurrentDate.equals(statusGetterSetter.getDate())) {
                            date = "Today: ";
                        } else {
                            date = "Yesterday: ";
                        }

                        textViewOwnStatus.setText(date + statusGetterSetter.getHour() + ":" + statusGetterSetter.getMin());


                        linearLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), StatusImageShowActivity.class);
                                intent.putExtra("node", node);
                                intent.putExtra("friendsId", CURRENT_USER);
                                startActivity(intent);
                            }
                        });


                    }

                    imageViewMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), VerticalyStatusForDdeleting.class);
                            startActivity(intent);
                        }
                    });

                } else {


                    String profileImage = userGetterAndSetter.getProfileImage();

                    Picasso.get().load(profileImage).into(circleImageViewOwn);
                    textViewOwnName.setText(name);
                    textViewOwnStatus.setText("Click on + icon for update Status");


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });


    }


    private class MyThread extends Thread {

        String key, friendId1, finalNode1;

        private MyThread(String key, String friendId, String finalNode1) {

            this.key = key;
            this.friendId1 = friendId;
            this.finalNode1 = finalNode1;
        }

        @Override
        synchronized public void run() {
            super.run();


            Log.d(TAG, "run: thread is start");

            databaseReference.child("Status").child(finalNode1).child(friendId1).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if (dataSnapshot.exists()) {
                        StatusGetterSetter statusGetterSetter = dataSnapshot.getValue(StatusGetterSetter.class);
                        String storagePath = null;
                        if (statusGetterSetter != null) {
                            storagePath = statusGetterSetter.getLink();
                        }
                        FirebaseStorage storageReference = FirebaseStorage.getInstance().getReference().getStorage();
                        StorageReference photoRef = null;
                        if (storagePath != null) {
                            photoRef = storageReference.getReferenceFromUrl(storagePath);
                        }


                        Log.d(TAG, "onDataChange: path is " + storagePath + "\n key is " + key);
                        photoRef.delete().addOnCompleteListener(new OnCompleteListener <Void>() {
                            @Override
                            public void onComplete(@NonNull Task <Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: storage delete succesfully");
                                    databaseReference.child("Status").child(finalNode1).child(friendId1).child(key).removeValue().addOnCompleteListener(new OnCompleteListener <Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task <Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "onComplete: status node deleted");
                                                databaseReference.child("StatusViewer").child(key).removeValue().addOnCompleteListener(new OnCompleteListener <Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task <Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "onComplete: stattusviewrnode delete");
                                                        } else {

                                                            Log.d(TAG, "onComplete: status viewer node deletetion failed");
                                                        }
                                                    }
                                                });
                                            } else {

                                                Log.d(TAG, "onComplete: status deleteion faild");
                                            }

                                        }
                                    });
                                } else {

                                    Log.d(TAG, "onComplete: " + task.getException().getMessage());
                                    databaseReference.child("Status").child(finalNode1).child(friendId1).child(key).removeValue().addOnCompleteListener(new OnCompleteListener <Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task <Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "onComplete: status node deleted" +
                                                        task.getException());
                                                databaseReference.child("StatusViewer").child(key).removeValue().addOnCompleteListener(new OnCompleteListener <Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task <Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "onComplete: stattusviewrnode delete");
                                                        } else {

                                                            Log.d(TAG, "onComplete: statusviewr nod edeleteion faild");
                                                        }
                                                    }
                                                });
                                            } else {

                                                Log.d(TAG, "onComplete: status deleteion faild");
                                            }

                                        }
                                    });
                                    Log.d(TAG, "onComplete: storage deletion faild" + task.getException().getMessage());

                                }
                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }
}