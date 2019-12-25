package com.theftfound.ocrscanning.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.theftfound.ocrscanning.Adapters.RecordingRecyclerViewAdapter;
import com.theftfound.ocrscanning.DatabaseUtils.DatabaseRecordingHelper;
import com.theftfound.ocrscanning.R;

import java.util.ArrayList;

public class RecordingHistoryFragment extends Fragment {
    private RecyclerView scanRecyclerViewID;
    private ArrayList<Object> productArrayList;
    DatabaseRecordingHelper db;
    private RelativeLayout emptyLayout;
    private RecordingRecyclerViewAdapter recordingRecyclerViewAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recording_history, container, false);

        //Casting Widget's
        scanRecyclerViewID = view.findViewById(R.id.scanRecyclerViewID);
        emptyLayout = view.findViewById(R.id.empty_layout);
        loadProductList();


        return view;
    }

    private void loadProductList() {
        db= new DatabaseRecordingHelper(getContext());
        productArrayList = db.getAllProduct();

        if(!productArrayList.isEmpty()){
            recordingRecyclerViewAdapter = new RecordingRecyclerViewAdapter(getContext(), productArrayList);
            scanRecyclerViewID.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            scanRecyclerViewID.setLayoutManager(mLayoutManager);
            scanRecyclerViewID.setAdapter(recordingRecyclerViewAdapter);
            emptyLayout.setVisibility(View.GONE);
        }
        else{
            emptyLayout.setVisibility(View.VISIBLE);
        }

    }
}
