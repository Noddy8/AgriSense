package com.ninjaTurtles.agrisense.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.activities.LoginActivity;
import com.ninjaTurtles.agrisense.activities.SettingsActivity;
import com.google.android.material.card.MaterialCardView;

public class ProfileFragment extends Fragment {

    private ImageView imgProfileAvatar;
    private View containerProfileOptions;
    private MaterialCardView cardOptionSettings, cardOptionLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        imgProfileAvatar = v.findViewById(R.id.imgProfileAvatar);
        containerProfileOptions = v.findViewById(R.id.containerProfileOptions);
        cardOptionSettings = v.findViewById(R.id.cardOptionSettings);
        cardOptionLogout = v.findViewById(R.id.cardOptionLogout);

        cardOptionSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });

        cardOptionLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getContext() != null) {
            // Profile image scale + fade
            Animation scaleAvatar = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
            imgProfileAvatar.startAnimation(scaleAvatar);

            // Staggered options slide up
            Animation slideOptions = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
            slideOptions.setStartOffset(150);
            containerProfileOptions.startAnimation(slideOptions);
        }
    }
}
