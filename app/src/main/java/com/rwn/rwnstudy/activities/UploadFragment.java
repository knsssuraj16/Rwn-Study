package com.rwn.rwnstudy.activities;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rwn.rwnstudy.R;

/**
 * A simple {@link Fragment} subclass.
 */


public class UploadFragment extends Fragment {
    static  private  final  String TAG="UploadFragment";

    public UploadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.d(TAG, "onCreate: created on create of upload fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        Log.d(TAG, "onCreateView: created on create of upload fragment");


        return inflater.inflate(R.layout.fragment_upload, container, false);
    }


}

