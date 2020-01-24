package dk.lysesort.chitchat.chatroom;

import com.google.firebase.storage.StorageReference;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

public class ChatRoomViewModel extends ViewModel {
    public static final String TAG = "Message";
    private ChatMessageRepository repository;
    private ChatMessageAdapter adapter;

    public ChatRoomViewModel(String chatRoomId) {
        repository = new ChatMessageRepository(chatRoomId);
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

    public void sendMessage(String message) {
        repository.sendMessage(message);
    }

    public void sendMessage(StorageReference storageReference) {
        repository.sendMessage(storageReference);
    }

    public ChatMessageAdapter getAdapter() {
        return adapter;
    }
}
