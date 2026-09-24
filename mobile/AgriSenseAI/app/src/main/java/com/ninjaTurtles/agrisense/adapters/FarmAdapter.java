package com.ninjaTurtles.agrisense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ninjaTurtles.agrisense.R;
import com.ninjaTurtles.agrisense.models.Farm;

import java.util.ArrayList;
import java.util.List;

public class FarmAdapter extends RecyclerView.Adapter<FarmAdapter.ViewHolder> {

    private List<Farm> farms = new ArrayList<>();
    private OnFarmClickListener listener;

    public interface OnFarmClickListener {
        void onFarmClick(Farm farm);
    }

    public void setOnFarmClickListener(OnFarmClickListener listener) {
        this.listener = listener;
    }

    public void setFarms(List<Farm> list) {
        this.farms = list != null ? list : new ArrayList<Farm>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_farm, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Farm farm = farms.get(position);
        holder.bind(farm, listener);

        Animation anim = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_up);
        anim.setStartOffset(position * 90L);
        holder.itemView.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return farms.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFarmName, tvCropType, tvAcres, tvHealthScore;

        ViewHolder(View itemView) {
            super(itemView);
            tvFarmName = itemView.findViewById(R.id.tvFarmName);
            tvCropType = itemView.findViewById(R.id.tvCropType);
            tvAcres = itemView.findViewById(R.id.tvAcres);
            tvHealthScore = itemView.findViewById(R.id.tvHealthScore);
        }

        void bind(final Farm farm, final OnFarmClickListener listener) {
            tvFarmName.setText(farm.getName());
            tvCropType.setText("Crop: " + farm.getCropType());
            tvAcres.setText("Area: " + farm.getAcres() + " Acres");
            tvHealthScore.setText(farm.getHealthScore() + "%");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onFarmClick(farm);
                    }
                }
            });
        }
    }
}
