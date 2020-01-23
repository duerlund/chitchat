package dk.lysesort.chitchat.chatroom;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import dk.lysesort.chitchat.R;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MessageAdapter";
    private static final int VIEW_TYPE_MESSAGE = 1;
    private static final int VIEW_TYPE_IMAGE = 2;

    private ChatMessageRepository repository;
    private List<ChatMessage> data;

    public ChatMessageAdapter(ChatMessageRepository repository) {
        this.repository = repository;
        data = new ArrayList<>();
    }

    public void setData(List<ChatMessage> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void newMessages(List<ChatMessage> newData) {
        data.addAll(0, newData);
        // notify ranges
        notifyDataSetChanged();
    }

    public void addMessages(List<ChatMessage> newData) {
        data.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_IMAGE) {
            LinearLayout view = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_image, parent, false);
            return new ChatMessageAdapter.ImageViewHolder(view);
        }

        LinearLayout view = (LinearLayout) LayoutInflater.from(parent.getContext())
            .inflate(R.layout.chat_message, parent, false);
        return new ChatMessageAdapter.MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = data.get(position);

        String user = message.getTimestamp().toDate() + ": " + message.getUser();
        String text = message.getText();

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE:
                Log.d("Adapter", "message holder");
                MessageViewHolder viewHolder = (MessageViewHolder) holder;
                viewHolder.user.setText(user);
                viewHolder.message.setText(text);
                break;
            case VIEW_TYPE_IMAGE:
                Log.d("Adapter", "image holder:" + message.getImageUrl());
                String imageUrl = message.getImageUrl();


                message.getImageUrl();
                ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                imageViewHolder.user.setText(user + " IMG");
                Glide.with(imageViewHolder.image)
                    .load(imageUrl)
                    .centerCrop()
                    .into(imageViewHolder.image);

//                imageViewHolder.image.
                break;
        }
//        holder.getItemViewType()
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = data.get(position);
        if (message.getImageUrl() != null) {
            return VIEW_TYPE_IMAGE;
        }

        return VIEW_TYPE_MESSAGE;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private int getItemLayoutResource(int viewType) {
        switch (viewType) {
            case VIEW_TYPE_IMAGE:
                return R.layout.chat_message_image;

            case VIEW_TYPE_MESSAGE:
            default:
                return R.layout.chat_message;
        }
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView user;
        public TextView message;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.username);
            message = itemView.findViewById(R.id.message);
        }
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView user;
        public ImageView image;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.username);
            image = itemView.findViewById(R.id.image);
        }
    }
}
