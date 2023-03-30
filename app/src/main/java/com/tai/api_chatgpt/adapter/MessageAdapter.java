package com.tai.api_chatgpt.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tai.api_chatgpt.R;
import com.tai.api_chatgpt.model.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null);
        return new ViewHolder(chatView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (message.getSentBy().equals(Message.SENT_BY_HUMAN)) {
            holder.lnAiChat.setVisibility(View.GONE);
            holder.lnHumanChat.setVisibility(View.VISIBLE);
            holder.tvHumanChat.setText(message.getMessage());
        } else {
            holder.lnHumanChat.setVisibility(View.GONE);
            holder.lnAiChat.setVisibility(View.VISIBLE);
            holder.tvAiChat.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout lnHumanChat, lnAiChat;
        TextView tvHumanChat, tvAiChat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lnAiChat = itemView.findViewById(R.id.ln_ai_chat);
            tvAiChat = itemView.findViewById(R.id.tv_ai_chat);
            lnHumanChat = itemView.findViewById(R.id.ln_human_chat);
            tvHumanChat = itemView.findViewById(R.id.tv_human_chat);
        }
    }
}
