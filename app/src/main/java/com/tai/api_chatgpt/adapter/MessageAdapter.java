package com.tai.api_chatgpt.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tai.api_chatgpt.R;
import com.tai.api_chatgpt.model.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    public void setFilteredList(List<Message> filteredList) {
        this.messageList = filteredList;
        notifyDataSetChanged();
    }

    public String getData() {
        Gson gson = new Gson();
        return gson.toJson(messageList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(chatView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (message.getSentBy().equals(Message.SENT_BY_HUMAN)) {
            holder.lnAiChat.setVisibility(View.GONE);
            holder.trHumanChat.setVisibility(View.VISIBLE);
            holder.tvHumanChat.setText(message.getMessage());
        } else {
            holder.trHumanChat.setVisibility(View.GONE);
            holder.lnAiChat.setVisibility(View.VISIBLE);
            holder.tvAiChat.setText(message.getMessage());
        }

        clickShare(holder, message);
        clickCopy(holder, message);
    }
    private void clickCopy(ViewHolder holder, Message message) {
        holder.ivCopy.setOnClickListener(v -> {
            Animation copyAnimation = AnimationUtils.loadAnimation(v.getContext(), androidx.appcompat.R.anim.abc_fade_in);
            holder.ivCopy.startAnimation(copyAnimation);

            int currentPosition = holder.getAdapterPosition();
            String copiedText = "";
            if (currentPosition > 0) {
                // Nếu currentPosition > 0 thì lấy cả message trước message hiện tại
                Message previousMessage = messageList.get(currentPosition - 1);
                copiedText += "Question: " + "\n" + previousMessage.getMessage() + "\n\n";
            }
            copiedText += "Answer: " + "\n" + message.getMessage();

            ClipboardManager clipboardManager = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("message", copiedText);
            //gán clipData vừa tạo làm message chính của bộ nhớ tạm
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(v.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
        });
    }

    private void clickShare(ViewHolder holder, Message message) {
        holder.ivShare.setOnClickListener(v -> {
            Animation shareAnimation = AnimationUtils.loadAnimation(v.getContext(), androidx.appcompat.R.anim.abc_fade_in);
            holder.ivShare.startAnimation(shareAnimation);

//            StringBuilder textToShare = new StringBuilder();
//            for (int i = 0; i <= holder.getAdapterPosition(); i++) {
//                Message msg = messageList.get(i);
//                if (msg.getSentBy().equals(Message.SENT_BY_HUMAN)) {
//                    textToShare.append("Question: ").append("\n");
//                } else {
//                    textToShare.append("Answer: ").append("\n");
//                }
//                textToShare.append(msg.getMessage()).append("\n\n");
//            }

            int currentPosition = holder.getAdapterPosition();
            String textToShare = "";
            if (currentPosition > 0) {
                // Nếu currentPosition > 0 thì lấy cả message trước message hiện tại
                Message previousMessage = messageList.get(currentPosition - 1);
                textToShare += "Question: " + "\n" + previousMessage.getMessage() + "\n\n";
            }
            textToShare += "Answer: " + "\n" + message.getMessage();

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare.toString());
            shareIntent.setType("text/plain");

            // Tạo một Intent mới để hiển thị danh sách các ứng dụng chia sẻ khả dụng trên thiết bị
            Intent shareChooser = Intent.createChooser(shareIntent, null);

            //Kiểm tra có ứng dụng chia sẻ khả dụng trên thiết bị không
            if (shareIntent.resolveActivity(v.getContext().getPackageManager()) != null) {
                v.getContext().startActivity(shareChooser);
            } else {
                Toast.makeText(v.getContext(), "There won't be any applications for sharing.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TableRow trHumanChat;
        LinearLayout lnAiChat;
        TextView tvHumanChat, tvAiChat;
        ImageView ivShare, ivCopy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lnAiChat = itemView.findViewById(R.id.ln_ai_chat);
            tvAiChat = itemView.findViewById(R.id.tv_ai_chat);
            trHumanChat = itemView.findViewById(R.id.tr_human_chat);
            tvHumanChat = itemView.findViewById(R.id.tv_human_chat);
            ivCopy = itemView.findViewById(R.id.iv_copy);
            ivShare = itemView.findViewById(R.id.iv_share);
        }
    }
}
