package com.ninjaTurtles.agrisense.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.activities.LoginActivity;
import com.ninjaTurtles.agrisense.activities.SensorHistoryActivity;
import com.ninjaTurtles.agrisense.utils.AnimationHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class ProfileFragment extends Fragment {

    private ImageView imgProfileAvatar;
    private MaterialButton btnQuickEditProfile;
    private MaterialCardView cardOptionLogout;
    private View itemFarmParcels, itemSoilHistory, itemIotSensors;
    private View itemLanguage, itemUnits, itemDialect;
    private View itemAlerts, itemSync, itemKvkSupport, itemAbout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        imgProfileAvatar = v.findViewById(R.id.imgProfileAvatar);
        btnQuickEditProfile = v.findViewById(R.id.btnQuickEditProfile);
        cardOptionLogout = v.findViewById(R.id.cardOptionLogout);

        itemFarmParcels = v.findViewById(R.id.itemFarmParcels);
        itemSoilHistory = v.findViewById(R.id.itemSoilHistory);
        itemIotSensors = v.findViewById(R.id.itemIotSensors);
        itemLanguage = v.findViewById(R.id.itemLanguage);
        itemUnits = v.findViewById(R.id.itemUnits);
        itemDialect = v.findViewById(R.id.itemDialect);
        itemAlerts = v.findViewById(R.id.itemAlerts);
        itemSync = v.findViewById(R.id.itemSync);
        itemKvkSupport = v.findViewById(R.id.itemKvkSupport);
        itemAbout = v.findViewById(R.id.itemAbout);

        if (btnQuickEditProfile != null) {
            btnQuickEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "Opening Profile Editor...", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (itemSoilHistory != null) {
            itemSoilHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), SensorHistoryActivity.class));
                }
            });
        }

        if (itemLanguage != null) {
            itemLanguage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "Language Preferences (English, Hindi, Punjabi)", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (itemKvkSupport != null) {
            itemKvkSupport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "Calling KVK Toll-free Helpline: 1800-180-1551", Toast.LENGTH_LONG).show();
                }
            });
        }

        if (cardOptionLogout != null) {
            cardOptionLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
                    } catch (Exception ignored) {}
                    Toast.makeText(getContext(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
        }

        return v;
    }
}
