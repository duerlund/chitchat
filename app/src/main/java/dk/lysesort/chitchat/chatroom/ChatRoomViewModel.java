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

    public LiveData<List<ChatMessage>> getMessages() {
        return repository.getMessages();
    }

    public void listenForNewMessages() {
        repository.listenForNewMessages();
    }

    public void onScrollToEnd() {
        repository.fetchOlderMessages();
    }

    public LiveData<List<ChatMessage>> getNewMessages() {
        return repository.getNewMessages();
    }

    public LiveData<List<ChatMessage>> getOldMessages() {
        return repository.getOldMessages();
    }

    public void sendMessage(String user, String message) {
        repository.sendMessage(user, message);
    }
}
