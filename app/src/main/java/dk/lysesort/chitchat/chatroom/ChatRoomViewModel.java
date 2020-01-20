package dk.lysesort.chitchat.chatroom;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class ChatRoomViewModel extends ViewModel {
    public static final String TAG = "Message";
    private ChatMessageRepository repository;

    public ChatRoomViewModel() {
        repository = new ChatMessageRepository("DZ0euDebpMaeEhK7gsGl");
    }

    public LiveData<ChatMessage> getMessages() {
        return repository.getMessages();
    }

    public void getNewMessages() {
        repository.getNewMessages();
    }



    public void sendMessage(String user, String message) {
        repository.sendMessage(user, message);
    }
}
