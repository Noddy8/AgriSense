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
import com.ninjaTurtles.agrisense.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> messages = new ArrayList<>();

    public void setMessages(List<ChatMessage> list) {
        this.messages = list != null ? list : new ArrayList<ChatMessage>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ChatMessage.TYPE_USER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_user, parent, false);
            return new UserViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_ai, parent, false);
            return new AiViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).bind(message);
            Animation anim = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_in_right);
            holder.itemView.startAnimation(anim);
        } else if (holder instanceof AiViewHolder) {
            ((AiViewHolder) holder).bind(message);
            Animation anim = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_in_left);
            holder.itemView.startAnimation(anim);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;
        UserViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvUserMessage);
            tvTime = itemView.findViewById(R.id.tvUserTime);
        }
        void bind(ChatMessage msg) {
            tvMessage.setText(msg.getText());
            tvTime.setText(msg.getTimestamp());
        }
    }

    static class AiViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;
        AiViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvAiMessage);
            tvTime = itemView.findViewById(R.id.tvAiTime);
        }
        void bind(ChatMessage msg) {
            tvMessage.setText(msg.getText());
            tvTime.setText(msg.getTimestamp());
        }
    }
}
