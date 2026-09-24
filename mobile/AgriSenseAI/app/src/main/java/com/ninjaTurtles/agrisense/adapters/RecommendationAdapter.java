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
import com.ninjaTurtles.agrisense.models.Recommendation;

import java.util.ArrayList;
import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {

    private List<Recommendation> recommendations = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Recommendation item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setRecommendations(List<Recommendation> list) {
        this.recommendations = list != null ? list : new ArrayList<Recommendation>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommendation, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recommendation item = recommendations.get(position);
        holder.bind(item, listener);

        // Staggered slide-up animation
        Animation anim = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_up);
        anim.setStartOffset(position * 80L);
        holder.itemView.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvTitle, tvDescription, tvUrgency;

        ViewHolder(View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvRecCategory);
            tvTitle = itemView.findViewById(R.id.tvRecTitle);
            tvDescription = itemView.findViewById(R.id.tvRecDescription);
            tvUrgency = itemView.findViewById(R.id.tvRecUrgency);
        }

        void bind(final Recommendation item, final OnItemClickListener listener) {
            tvCategory.setText(item.getCategory());
            tvTitle.setText(item.getTitle());
            tvDescription.setText(item.getDescription());
            tvUrgency.setText(item.getUrgency());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(item);
                    }
                }
            });
        }
    }
}
