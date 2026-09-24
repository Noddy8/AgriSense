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
import com.ninjaTurtles.agrisense.models.AlertNotification;

import java.util.ArrayList;
import java.util.List;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.ViewHolder> {

    private List<AlertNotification> alerts = new ArrayList<>();
    private OnAlertClickListener listener;

    public interface OnAlertClickListener {
        void onAlertClick(AlertNotification alert);
    }

    public void setOnAlertClickListener(OnAlertClickListener listener) {
        this.listener = listener;
    }

    public void setAlerts(List<AlertNotification> list) {
        this.alerts = list != null ? list : new ArrayList<AlertNotification>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alert, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlertNotification alert = alerts.get(position);
        holder.bind(alert, listener);

        // Slide down animation for new alerts
        Animation anim = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_up);
        anim.setStartOffset(position * 60L);
        holder.itemView.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTime;
        View viewUnreadIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvAlertTitle);
            tvMessage = itemView.findViewById(R.id.tvAlertMessage);
            tvTime = itemView.findViewById(R.id.tvAlertTime);
            viewUnreadIndicator = itemView.findViewById(R.id.viewUnreadIndicator);
        }

        void bind(final AlertNotification alert, final OnAlertClickListener listener) {
            tvTitle.setText(alert.getTitle());
            tvMessage.setText(alert.getMessage());
            tvTime.setText(alert.getTime());

            if (alert.isUnread()) {
                viewUnreadIndicator.setVisibility(View.VISIBLE);
                Animation pulse = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.pulse);
                viewUnreadIndicator.startAnimation(pulse);
            } else {
                viewUnreadIndicator.clearAnimation();
                viewUnreadIndicator.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onAlertClick(alert);
                    }
                }
            });
        }
    }
}
