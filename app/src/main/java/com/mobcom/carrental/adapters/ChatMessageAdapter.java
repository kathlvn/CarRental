package com.mobcom.carrental.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mobcom.carrental.R;
import com.mobcom.carrental.models.ChatMessage;
import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatViewHolder> {

    private final List<ChatMessage> messages = new ArrayList<>();
    private final String currentRole;

    public ChatMessageAdapter(String currentRole) {
        this.currentRole = currentRole;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        boolean isMine = currentRole.equalsIgnoreCase(message.getSenderRole());

        holder.tvMessage.setText(message.getContent());
        holder.tvSender.setText(message.getSenderName());

        ViewGroup.LayoutParams params = holder.container.getLayoutParams();
        if (params instanceof RecyclerView.LayoutParams) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) params;
            if (isMine) {
                holder.container.setBackgroundResource(R.drawable.bg_chat_bubble_me);
                lp.setMargins(90, 6, 12, 6);
            } else {
                holder.container.setBackgroundResource(R.drawable.bg_chat_bubble_other);
                lp.setMargins(12, 6, 90, 6);
            }
            holder.container.setLayoutParams(lp);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void submitList(List<ChatMessage> newItems) {
        messages.clear();
        if (newItems != null) {
            messages.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        View container;
        TextView tvSender;
        TextView tvMessage;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.messageContainer);
            tvSender = itemView.findViewById(R.id.tvSender);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }
}
