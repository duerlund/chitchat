package dk.lysesort.chitchat.chatroom;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import dk.lysesort.chitchat.R;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {
    private static final String TAG = "MessageAdapter";
    private List<ChatMessage> data;

    public ChatMessageAdapter() {
        data = new ArrayList<>();
    }

    public void setData(List<ChatMessage> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessage chatMessage) {
        Log.d(TAG, "Adding message: " + chatMessage);
        data.add(chatMessage);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(parent.getContext())
            .inflate(R.layout.chat_message, parent, false);
        return new ChatMessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.user.setText(data.get(position).getUser());
        holder.message.setText(data.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView user;
        public TextView message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.username);
            message = itemView.findViewById(R.id.message);
        }
    }
}
