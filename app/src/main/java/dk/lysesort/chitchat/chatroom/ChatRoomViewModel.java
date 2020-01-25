package dk.lysesort.chitchat.chatroom;

import android.app.AlertDialog;
import android.content.Context;

import com.google.firebase.storage.StorageReference;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import dk.lysesort.chitchat.R;

public class ChatRoomViewModel extends ViewModel {
    private String chatRoomId;
    private ChatMessageRepository repository;
    private ChatMessageAdapter adapter;

    public ChatRoomViewModel(String chatRoomId) {
        this.chatRoomId = chatRoomId;
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

    /**
     * Displays a dialog asking the user if he/she want to receive push notifications for this chat
     * room.
     * <p>
     * It is only displayed if it is the first time the user is in the chat room.
     *
     * @param context Context for the dialog
     */
    public void showPushNotificationDialog(Context context) {
        if (ChatRoomPreferenceRepository.hasAnswered(context, chatRoomId)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.notification_dialog_message)
            .setPositiveButton(
                R.string.notification_dialog_button_positive,
                (dialog, which) -> ChatRoomPreferenceRepository.subscribe(context, chatRoomId)
            )
            .setNegativeButton(
                R.string.notification_dialog_button_negative,
                (dialog, which) -> ChatRoomPreferenceRepository.unsubscribe(context, chatRoomId)
            )
            .create()
            .show();
    }

    public boolean onToggleNotifications(Context context) {
        return ChatRoomPreferenceRepository.toggleSubscription(context, chatRoomId);
    }

    public ChatMessageAdapter getAdapter() {
        return adapter;
    }
}
