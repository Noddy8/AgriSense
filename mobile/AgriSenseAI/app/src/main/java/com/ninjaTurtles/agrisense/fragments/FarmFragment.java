package com.ninjaTurtles.agrisense.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.activities.FarmDetailsActivity;
import com.ninjaTurtles.agrisense.adapters.FarmAdapter;
import com.ninjaTurtles.agrisense.models.Farm;
import com.ninjaTurtles.agrisense.viewmodels.FarmViewModel;

import java.util.List;

public class FarmFragment extends Fragment {

    private FarmViewModel viewModel;
    private RecyclerView rvFarms;
    private FarmAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_farm, container, false);
        rvFarms = v.findViewById(R.id.rvFarms);
        rvFarms.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FarmAdapter();
        rvFarms.setAdapter(adapter);

        adapter.setOnFarmClickListener(new FarmAdapter.OnFarmClickListener() {
            @Override
            public void onFarmClick(Farm farm) {
                Intent intent = new Intent(getActivity(), FarmDetailsActivity.class);
                intent.putExtra("farm_name", farm.getName());
                intent.putExtra("crop_type", farm.getCropType());
                intent.putExtra("acres", farm.getAcres());
                intent.putExtra("soil_type", farm.getSoilType());
                intent.putExtra("health_score", farm.getHealthScore());
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FarmViewModel.class);
        viewModel.getFarms().observe(getViewLifecycleOwner(), new Observer<List<Farm>>() {
            @Override
            public void onChanged(List<Farm> farms) {
                adapter.setFarms(farms);
            }
        });
    }
}
