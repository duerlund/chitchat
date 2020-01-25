package dk.lysesort.chitchat.chatroomlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import dk.lysesort.chitchat.R;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {
    private List<ChatRoom> data;
    private OnChatRoomClickListener onChatRoomClickListener;

    public ChatRoomAdapter(OnChatRoomClickListener onChatRoomClickListener) {
        this.onChatRoomClickListener = onChatRoomClickListener;
        this.data = new ArrayList<>();
    }

    public void setData(List<ChatRoom> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(ViewGroup parent,
        int viewType) {
        ConstraintLayout view = (ConstraintLayout) LayoutInflater.from(parent.getContext())
            .inflate(R.layout.chat_room_list_item, parent, false);
        return new ChatRoomViewHolder(view, onChatRoomClickListener);
    }

    @Override
    public void onBindViewHolder(ChatRoomViewHolder holder, int position) {
        holder.name.setText(data.get(position).getName());
        holder.description.setText(data.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnChatRoomClickListener {
        void onClick(int position);
    }

    public static class ChatRoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        public TextView description;
        OnChatRoomClickListener listener;

        ChatRoomViewHolder(ConstraintLayout v, OnChatRoomClickListener listener) {
            super(v);
            this.listener = listener;
            name = v.findViewById(R.id.text_view_name);
            description = v.findViewById(R.id.text_view_description);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(getAdapterPosition());
        }
    }
}