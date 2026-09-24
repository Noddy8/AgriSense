package com.ninjaTurtles.agrisense.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.adapters.RecommendationAdapter;
import com.ninjaTurtles.agrisense.data.MockDataProvider;

public class RecommendationsActivity extends AppCompatActivity {

    private RecyclerView rvRecommendations;
    private RecommendationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        rvRecommendations = findViewById(R.id.rvFullRecommendations);
        rvRecommendations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecommendationAdapter();
        rvRecommendations.setAdapter(adapter);

        adapter.setRecommendations(MockDataProvider.getMockRecommendations());
    }
}
