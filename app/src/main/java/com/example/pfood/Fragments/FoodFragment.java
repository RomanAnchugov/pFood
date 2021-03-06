package com.example.pfood.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pfood.Classes.AppSettings;
import com.example.pfood.Classes.Food;
import com.example.pfood.Classes.FoodAdapter;
import com.example.pfood.Classes.FoodCategory;
import com.example.pfood.R;
import com.example.pfood.model.FoodItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FoodFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerView;
    private FoodAdapter mAdapter;
    private ArrayList<Food> foodList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("FRAGMENT_TEST", "ENTERED FOOD FRAGMENT");
        rootView = inflater.inflate(R.layout.recycler_activity, container, false);

        foodList = new ArrayList<>();

        getActivity().setTitle(AppSettings.getInstance().clickedCategory.getName());

        recyclerView = rootView.findViewById(R.id.recyclerview_id);
        mAdapter = new FoodAdapter(getActivity());
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(mAdapter);
        AppSettings.getInstance().foodAdapter = mAdapter;

        return rootView;
    }
}
