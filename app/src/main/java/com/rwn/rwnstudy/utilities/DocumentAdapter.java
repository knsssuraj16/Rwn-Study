package com.rwn.rwnstudy.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rwn.rwnstudy.R;
import com.rwn.rwnstudy.activities.ImageViewerActivity;
import com.rwn.rwnstudy.activities.PersonalDocumantUploadingActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DocumentAdapter extends RecyclerView.Adapter <DocumentAdapter.DocumentViewHolder>  {

    List <DocumentGetterSetter> list;
    String subject, unit;
    private boolean checking = false;
    private int pos;
    private List <String> listForChecking = new ArrayList <>();
    private List <String> listForUserType = new ArrayList <>();
    private List <String> listForSubject = new ArrayList <>();
    private List <String> listForUnit = new ArrayList <>();
    private List <DocumentGetterSetter> DownoloadingQueue = new ArrayList <>();
    private ActionBar actionBar;
    private FirebaseAuth mAuth;
    int b = 0;
    private static Context context;
    private TextView textView, textViewDelte;
    String SubjectName, UnitName;
     DatabaseReference databaseReference;
    static public String filename = "";
    static public  DocumentGetterSetter documentGetterSetter1;
    public DocumentAdapter(Context context, List <DocumentGetterSetter> list, String SubjectName, String UnitName) {


        this.UnitName = UnitName;
        this.SubjectName = SubjectName;
        this.context = context;
        this.list = list;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.topic_layout, parent, false);
        mAuth = FirebaseAuth.getInstance();
        actionBar = ((AppCompatActivity) context).getSupportActionBar();

        databaseReference = FirebaseDatabase.getInstance().getReference();



        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DocumentViewHolder holder, int position) {

        final DocumentGetterSetter documentGetterSetter = list.get(position);




        final int postion1 = position;
        holder.textViewDescription.setVisibility(View.VISIBLE);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint({"InflateParams", "ResourceType"})
            @Override
            public boolean onLongClick(View v) {

                DocumentGetterSetter doc = list.get(postion1);
                String user = doc.getUid();
                String usrType = doc.getUserType();
                String Subject = doc.getSubject();
                String unit = doc.getUnit();
                if (user.equals(mAuth.getCurrentUser().getUid())) {

                    String uid = doc.getKey();
                    listForChecking.add(uid);
                    listForSubject.add(Subject);
                    listForUserType.add(usrType);
                    listForUnit.add(unit);


                    checking = true;
                    pos = postion1;
                    holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.notification_color));


                    if (actionBar != null) {
                        actionBar.setDisplayHomeAsUpEnabled(true);

                        actionBar.setDisplayShowCustomEnabled(true);

                        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View action_bar_view = null;
                        if (layoutInflater != null) {
                            action_bar_view = layoutInflater.inflate(R.layout.select_notes_custom, null);

                            textView = action_bar_view.findViewById(R.id.textView_select_Check);
                            textViewDelte = action_bar_view.findViewById(R.id.textView_delete);
                        }
                        actionBar.setCustomView(action_bar_view);


                        textViewDelte.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                        "Yes"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.linearLayout.getContext());

                                builder.setTitle("Confirm Remove selected documents");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (which == 0) {
                                            final ProgressDialog progressDialog = new ProgressDialog(holder.linearLayout.getContext());
                                            progressDialog.show();

                                            for (int i = 0; i < listForChecking.size(); i++) {
                                                final String key = listForChecking.get(i);
                                                String sub = listForSubject.get(i);
                                                String unit = listForUnit.get(i);
                                                String UserType = listForUserType.get(i);
//                                   Log.d("TEXTNM", "onClick: "+);
                                                Log.d("TEXTNM", "onClick: " + key + "\n" + sub + "\n" + unit + "\n" + UserType);


                                                if (UserType.equals("Student")) {
                                                    databaseReference.child("StudentUplodedDocument").child(sub).child(unit).child(mAuth.getCurrentUser().getUid()).child(key).removeValue().addOnCompleteListener(new OnCompleteListener <Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task <Void> task) {
                                                            if (task.isSuccessful()) {
                                                                progressDialog.dismiss();
                                                                Log.d("TEXTNM", "onComplete: Success");

                                                                if (b == (listForChecking.size() - 1)) {
                                                                    Toast.makeText(context, " Documents Removed", Toast.LENGTH_SHORT).show();
                                                                    ((Activity) context).finish();
                                                                }
                                                                b++;

                                                            } else {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                                                Log.d("TEXTNM", "onComplete: faild");
                                                            }
                                                        }
                                                    });
//


                                                } else {
                                                    databaseReference.child("TeacherUplodedDocument").child(sub).child(unit).child(key).removeValue().addOnCompleteListener(new OnCompleteListener <Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task <Void> task) {
                                                            if (task.isSuccessful()) {
                                                                progressDialog.dismiss();
                                                                Log.d("TEXTNM", "onComplete: Success");
                                                                if (b == (listForChecking.size() - 1)) {
                                                                    Toast.makeText(context, " Documents Removed", Toast.LENGTH_SHORT).show();
                                                                    ((Activity) context).finish();
                                                                }
                                                                b++;
                                                            } else {
                                                                progressDialog.dismiss();

                                                                Log.d("TEXTNM", "onComplete: faild");
                                                            }
                                                        }
                                                    });
                                                }
                                            }

                                        }
                                    }


                                });

                                builder.show();


                            }
                        });


                        textView.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(context.getApplicationContext(), PersonalDocumantUploadingActivity.class);

                                intent.putStringArrayListExtra("listdata", (ArrayList <String>) listForChecking);
                                intent.putExtra("SubjectName", SubjectName);
                                intent.putExtra("Unitname", UnitName);
                                intent.putExtra("Userid", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.getApplicationContext().startActivity(intent);
                            }
                        });

                    }
                }

                return true;
            }

        });


        holder.textViewDescription.setText(documentGetterSetter.getUploadingDate() + "    " + documentGetterSetter.getUploadingTime().substring(0, 5));
        String type = documentGetterSetter.getDocumentType();
        if (type.equals("image")) {

            Picasso.get().load(documentGetterSetter.getDocumentLink()).resize(100, 100).noFade().into(holder.imageViewDocument);

        } else if (type.equals("pdf")) {
            Picasso.get().load(R.drawable.images).resize(80, 80).noFade().into(holder.imageViewDocument);
        } else {

            Picasso.get().load(R.drawable.powerpoint).resize(80, 80).noFade().into(holder.imageViewDocument);
        }
        holder.textViewTopic.setText(documentGetterSetter.getTopicName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checking) {

                    DocumentGetterSetter documentGetterSetter1 = list.get(postion1);
                    String uid = documentGetterSetter1.getKey();
                    String usrType = documentGetterSetter1.getUserType();
                    String Subject = documentGetterSetter1.getSubject();
                    String unit = documentGetterSetter1.getUnit();
                    if (listForChecking.contains(uid)) {
                        listForChecking.remove(uid);
                        listForUserType.remove(usrType);
                        listForUnit.remove(unit);
                        listForSubject.remove(Subject);
                        holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
                        if (listForChecking.size() == 0) {
                            checking = false;


                            if (actionBar != null) {
                                actionBar.setDisplayHomeAsUpEnabled(true);

                                actionBar.setDisplayShowCustomEnabled(true);

                                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View action_bar_view = null;
                                if (layoutInflater != null) {
                                    action_bar_view = layoutInflater.inflate(R.layout.select_notes_custom_another, null);
                                }
                                actionBar.setCustomView(action_bar_view);
                            }


                        }


                    } else {
                        listForSubject.add(Subject);
                        listForUserType.add(usrType);
                        listForUnit.add(unit);
                        listForChecking.add(uid);
                        holder.linearLayout.setBackgroundColor(context.getResources().getColor(R.color.notification_color));

                    }

                } else {



                     documentGetterSetter1 = list.get(postion1);

                     if(!DownoloadingQueue.contains(documentGetterSetter1)) {
                         DownoloadingQueue.add(documentGetterSetter1);

                         if (documentGetterSetter1.getDocumentType().equals("pdf")) {
                             File extStore = Environment.getExternalStorageDirectory();


                             String file_name = documentGetterSetter1.getTopicName();
                             filename = "";
                             String[] arrOfStr = file_name.split(" ");
                             for (String con : arrOfStr) {
                                 filename = filename + con + "_";
                             }

                             File myFile = new File(extStore.getAbsolutePath() + "/Android/data/com.rwn.rwnstudy/files/Documents/" + filename + ".pdf");

                             Log.d("jjjj", "findFileIntoDirectory: " + filename);
                             if (myFile.exists()) {

                                 findFileIntoDirectory(filename, true);
                                 DownoloadingQueue.clear();
                             } else {
                                 if (false) {  //mRewardedVideoAd.isLoaded()
                                     Toast.makeText(context, "Document will Automatically downloaded after this Ad", Toast.LENGTH_SHORT).show();

                                 } else {
                                     downloadFile(filename, documentGetterSetter1.getDocumentLink(), ".pdf");
                                     DownoloadingQueue.clear();
                                     Toast.makeText(context, "Downloading Start after downloading Docs will be auto opened", Toast.LENGTH_LONG).show();
                                 }
                             }

                         } else {
                             Intent intent = new Intent(context, ImageViewerActivity.class);
                             intent.putExtra("types", documentGetterSetter1.getDocumentType());
                             intent.putExtra("image", documentGetterSetter1.getDocumentLink());
                             intent.putExtra("fileName", documentGetterSetter1.getTopicName());
                             intent.putExtra("desc", documentGetterSetter1.getDescription());
                             context.startActivity(intent);
                         }
                     }else{
                         Toast.makeText(context, "Please Wait Downloading is in Queue.", Toast.LENGTH_SHORT).show();
                     }
                }
            }
        });


    }


    static public void downloadFile(String filename, String image, String s) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(image));
        request.setDescription(filename + " is downloading");   //appears the same in Notification bar while downloading
        request.setTitle(filename);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalFilesDir(context, "/Documents/", filename + s);


        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        assert manager != null;
        long id = manager.enqueue(request);


        sendBordcastAfterDownload(id, filename, manager);


    }

    private static void sendBordcastAfterDownload(final long id, final String filename, final DownloadManager manager) {


        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(id);
                    //   manager = (DownloadManager)context.getSystemService(DOWNLOAD_SERVICE);
                    Cursor c = manager.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                            String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            Log.d("nnvwvonv", "onReceive: long id" + downloadId + " and " + id + "Cursor " + c + "\n colmn index" + columnIndex + "\n Uri String " + uriString + "\n File name " + filename);

                            findFileIntoDirectory(filename, false);

                        }
                    }
                }
            }
        };
        context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    private static void findFileIntoDirectory(String filename, boolean b) {

        Log.d("jjjj", "findFileIntoDirectory: " + filename);

        File file;

        Uri path = null;
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//            path = Uri.fromFile(file);
//        } else {
//            path = FileProvider.getUriForFile(context, "com.rwn.rwnstudy.fileprovider", file);
//        }
        if (b) {

//            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
//                    "Android/data/com.rwn.rwnstudy/files/Documents/" + filename + ".pdf");

            file = new File(
                    context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()
                            + "/" + filename + ".pdf"
            );

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                path = Uri.fromFile(file);
            } else {
                path = FileProvider.getUriForFile(context, "com.rwn.rwnstudy.fileprovider", file);
            }
        } else {
            //    Log.d("fsdfdsd", "findFileIntoDirectory: "+Environment.getExternalStorageDirectory().getAbsolutePath()+" \n new "+Environment.getExternalStorageDirectory());
            file = new File(Environment.getExternalStorageDirectory(),
                    "Android/data/com.rwn.rwnstudy/files/Documents/" + filename + ".pdf");

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                path = Uri.fromFile((file));
            } else {

                path = FileProvider.getUriForFile(context, "com.rwn.rwnstudy.fileprovider", file);
            }

        }
        Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
        pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pdfOpenintent.setDataAndType(path, "application/pdf");
        try {
            context.startActivity(pdfOpenintent);
        } catch (ActivityNotFoundException e) {

            Log.d("jjjj", "findFileIntoDirectory: " + e);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }







    public class DocumentViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTopic, textViewDescription;
        ImageView imageViewDocument;
        LinearLayout linearLayout;

        public DocumentViewHolder(View itemview) {
            super(itemview);

            textViewTopic = itemview.findViewById(R.id.textview_topic_name);
            textViewDescription = itemview.findViewById(R.id.textView_topic_description);
            imageViewDocument = itemview.findViewById(R.id.ImageView_document);
            linearLayout = itemview.findViewById(R.id.linearlayout_topic);

        }
    }
}
