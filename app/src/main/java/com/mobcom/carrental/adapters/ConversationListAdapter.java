package com.mobcom.carrental.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mobcom.carrental.R;
import com.mobcom.carrental.models.Conversation;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ConversationListAdapter extends RecyclerView.Adapter<ConversationListAdapter.ViewHolder> {

    private List<Conversation> conversations;
    private final OnConversationClickListener listener;

    public interface OnConversationClickListener {
        void onConversationClick(Conversation conversation);
    }

    public ConversationListAdapter(List<Conversation> conversations, OnConversationClickListener listener) {
        this.conversations = conversations;
        this.listener = listener;
    }

    public void updateList(List<Conversation> newConversations) {
        this.conversations = newConversations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(conversations.get(position));
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvPeerName;
        private final TextView tvLastMessage;
        private final TextView tvTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPeerName = itemView.findViewById(R.id.tvPeerName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }

        public void bind(Conversation conversation) {
            tvPeerName.setText(conversation.peerName);
            tvLastMessage.setText(conversation.lastMessage);

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
            tvTimestamp.setText(sdf.format(conversation.timestamp));

            itemView.setOnClickListener(v -> listener.onConversationClick(conversation));
        }
    }
}
