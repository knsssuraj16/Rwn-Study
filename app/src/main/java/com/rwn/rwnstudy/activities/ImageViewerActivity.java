package com.rwn.rwnstudy.activities;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rwn.rwnstudy.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImageViewerActivity extends AppCompatActivity {
    ImageView imageView, imageViewDownload;
    ProgressDialog progressDialog;
    String filepath;
    String image;

    TextView textViewViewDescription;
    String extention;
    String filename="";
    final String TAG = "ImageViewvr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        imageViewDownload = findViewById(R.id.imageview_download);
        imageView = findViewById(R.id.imageView_photoView);
        textViewViewDescription = findViewById(R.id.textView_descrption);
        textViewViewDescription.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);


        Intent intent = getIntent();

        String descrption = intent.getStringExtra("desc");
        image = intent.getStringExtra("image");
        final String file_name = intent.getStringExtra("fileName");
        final String File_type = intent.getStringExtra("types");

        switch (File_type) {
            case "ppt":
                extention = ".pptx";
                Picasso.get().load(R.drawable.powerpoint).resize(400, 400).into(imageView);
                progressDialog.dismiss();


                break;
            case "pdf":
                extention = ".pdf";
                Picasso.get().load(R.drawable.images).resize(400, 400).into(imageView);
                progressDialog.dismiss();
                break;
            default:
                extention = ".jpg";
                Picasso.get().load(image).into(imageView);
                progressDialog.dismiss();
                break;
        }

        if (!TextUtils.isEmpty(descrption)) {

            textViewViewDescription.setVisibility(View.VISIBLE);
            textViewViewDescription.setText(descrption);
        }

        String[] arrOfStr = file_name.split(" ");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());

        for (String con:arrOfStr) {
            filename = filename +con+"_";
        }

     //   filename = arrOfStr[0] + "_" + timeStamp;

        imageViewDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = isDownloadManagerAvailable(ImageViewerActivity.this);
                if (result) {

                    imageView.setEnabled(false);
                    Toast.makeText(ImageViewerActivity.this, "File Downloading start", Toast.LENGTH_SHORT).show();
                    downloadFile();


                } else {
                    Toast.makeText(ImageViewerActivity.this, "Downloader is busy", Toast.LENGTH_SHORT).show();
                }


            }
        });


        fullScreen();
    }

    public void downloadFile() {
        String DownloadUrl = image;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DownloadUrl));
        request.setDescription(filename + " is downloading");   //appears the same in Notification bar while downloading
        request.setTitle(filename);
        if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalFilesDir(getApplicationContext(), "/Documents/",  filename + extention);

        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        long id=manager.enqueue(request);
        sendBordcastAfterDownload(id,filename,manager);

    }

    private void sendBordcastAfterDownload(final long id, final String filename, final DownloadManager manager) {


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
//                            findFileIntoDirectory(filename);
                            Toast.makeText(context, "Document Downloaded Successfully ", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    public static boolean isDownloadManagerAvailable(Context context) {
        try {
            if (Build.VERSION.SDK_INT < VERSION_CODES.GINGERBREAD) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClassName("com.rwn.rwnstudy", "com.rwn.rwnstudy.activities.ImageViewerActivity");
            List <ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void fullScreen() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)
        // BEGIN_INCLUDE (toggle_ui_flags)
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.d(TAG, "Turning immersive mode mode off. ");
        } else {
            Log.d(TAG, "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }
}
