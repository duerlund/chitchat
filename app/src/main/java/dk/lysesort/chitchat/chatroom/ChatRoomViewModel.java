package dk.lysesort.chitchat.chatroom;

import com.google.firebase.storage.StorageReference;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

public class ChatRoomViewModel extends ViewModel {
    public static final String TAG = "Message";
    private ChatMessageRepository repository;
    private ChatMessageAdapter adapter;

    public ChatRoomViewModel() {
        repository = new ChatMessageRepository("DZ0euDebpMaeEhK7gsGl");
        adapter = new ChatMessageAdapter(repository);
    }

    public void listenForUpdates(LifecycleOwner lifecycleOwner) {
        repository.getMessages()
            .observe(lifecycleOwner, chatMessages -> {
                adapter.addMessages(chatMessages);
                listenForNewMessages();
            });

        repository.getNewMessages().observe(lifecycleOwner, adapter::newMessages);
        repository.getOldMessages().observe(lifecycleOwner, adapter::addMessages);
    }

    public void listenForNewMessages() {
        repository.listenForNewMessages();
    }

    public void onScrollToEnd() {
        repository.fetchOlderMessages();
    }

    public void sendMessage(String user, String message) {
        repository.sendMessage(user, message);
    }

    public void sendMessage(String user, StorageReference storageReference) {


        repository.sendMessage(user, storageReference);
    }


    public ChatMessageAdapter getAdapter() {
        return adapter;
    }
}
