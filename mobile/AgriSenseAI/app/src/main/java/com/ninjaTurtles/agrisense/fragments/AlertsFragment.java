package com.ninjaTurtles.agrisense.fragments;

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
import com.ninjaTurtles.agrisense.adapters.AlertAdapter;
import com.ninjaTurtles.agrisense.models.AlertNotification;
import com.ninjaTurtles.agrisense.viewmodels.AlertsViewModel;

import java.util.List;

public class AlertsFragment extends Fragment {

    private AlertsViewModel viewModel;
    private RecyclerView rvAlerts;
    private AlertAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_alerts, container, false);

        rvAlerts = v.findViewById(R.id.rvAlerts);
        rvAlerts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AlertAdapter();
        rvAlerts.setAdapter(adapter);

        adapter.setOnAlertClickListener(new AlertAdapter.OnAlertClickListener() {
            @Override
            public void onAlertClick(AlertNotification alert) {
                viewModel.markAsRead(alert);
                adapter.notifyDataSetChanged();
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AlertsViewModel.class);

        viewModel.getAlerts().observe(getViewLifecycleOwner(), new Observer<List<AlertNotification>>() {
            @Override
            public void onChanged(List<AlertNotification> alerts) {
                adapter.setAlerts(alerts);
            }
        });
    }
}
