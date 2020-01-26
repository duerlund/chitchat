package dk.lysesort.chitchat.chatroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import dk.lysesort.chitchat.R;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_MESSAGE = 1;
    private static final int VIEW_TYPE_IMAGE = 2;

    private List<ChatMessage> data;

    public ChatMessageAdapter() {
        data = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_IMAGE) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_image, parent, false);
            return new ChatMessageAdapter.ImageViewHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.chat_message, parent, false);
        return new ChatMessageAdapter.MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = data.get(position);

        String user = message.getUser();
        String timestamp = message.getTimestamp().toDate().toLocaleString();
        String text = message.getText();

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE:
                MessageViewHolder viewHolder = (MessageViewHolder) holder;
                viewHolder.user.setText(user);
                viewHolder.timestamp.setText(timestamp);

                Glide.with(viewHolder.profileImage)
                    .load(message.getProfileImageUrl())
                    .centerCrop()
                    .into(viewHolder.profileImage);

                viewHolder.message.setText(text);

                break;
            case VIEW_TYPE_IMAGE:
                String imageUrl = message.getImageUrl();

                message.getImageUrl();
                ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                imageViewHolder.user.setText(user);
                imageViewHolder.timestamp.setText(timestamp);

                Glide.with(imageViewHolder.profileImage)
                    .load(message.getProfileImageUrl())
                    .error(R.drawable.baseline_person_24)
                    .centerCrop()
                    .into(imageViewHolder.profileImage);

                Glide.with(imageViewHolder.image)
                    .load(imageUrl)
                    .error(R.drawable.baseline_broken_image_24)
                    .centerCrop()
                    .into(imageViewHolder.image);
                break;
        }
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
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void appendMessages(List<ChatMessage> newData) {
        data.addAll(newData);
        notifyDataSetChanged();
    }

    public void prependMessages(List<ChatMessage> newData) {
        data.addAll(0, newData);
        notifyDataSetChanged();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView user;
        public TextView timestamp;
        public ImageView profileImage;
        public TextView message;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.username);
            timestamp = itemView.findViewById(R.id.timestamp);
            profileImage = itemView.findViewById(R.id.profile_image);
            message = itemView.findViewById(R.id.message);
        }
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView user;
        public TextView timestamp;
        public ImageView profileImage;
        public ImageView image;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.username);
            timestamp = itemView.findViewById(R.id.timestamp);
            profileImage = itemView.findViewById(R.id.profile_image);
            image = itemView.findViewById(R.id.image);
        }
    }
}
