package dk.lysesort.chitchat.chatroomlist;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class ChatRoomListViewModel extends ViewModel {
    public static final String TAG = "Chatroom";
    private ChatRoomRepository repository = new ChatRoomRepository();

    public LiveData<List<ChatRoom>> getChatRooms() {
        return repository.getChatRooms();
    }

    public void refreshChatRooms() {
        repository.fetchChatRooms();
    }


}
