package dk.lysesort.chitchat.chatroomlist;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import dk.lysesort.chitchat.R;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {
    private List<ChatRoom> data;

    public static class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView description;

        public ChatRoomViewHolder(LinearLayout v) {
            super(v);
            name = v.findViewById(R.id.text_view_name);
            description = v.findViewById(R.id.text_view_description);
        }
    }

    public ChatRoomAdapter() {
        this.data = new ArrayList<>();
    }

    public void setData(List<ChatRoom> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public ChatRoomViewHolder onCreateViewHolder(ViewGroup parent,
        int viewType) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(parent.getContext())
            .inflate(R.layout.chat_room_list_item, parent, false);

        ChatRoomViewHolder vh = new ChatRoomViewHolder(view);
        return vh;
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
}

