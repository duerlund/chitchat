package dk.lysesort.chitchat.chatroom;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ChatRoomViewModelFactory implements ViewModelProvider.Factory {

    private String roomId;

    public ChatRoomViewModelFactory(String roomId) {
        this.roomId = roomId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ChatRoomViewModel(roomId);
    }
}
