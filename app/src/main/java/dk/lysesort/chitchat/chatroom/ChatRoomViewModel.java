package dk.lysesort.chitchat.chatroom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import dk.lysesort.chitchat.R;

public class ChatRoomViewModel extends ViewModel {
    private String chatRoomId;
    private ChatMessageRepository repository;
    private ChatMessageAdapter adapter;
    private ImageRepository imageRepository = new ImageRepository();

    public ChatRoomViewModel(String chatRoomId) {
        this.chatRoomId = chatRoomId;
        repository = new ChatMessageRepository(chatRoomId);
        adapter = new ChatMessageAdapter(repository);
    }

    public void listenForUpdates(LifecycleOwner lifecycleOwner) {
        repository.getMessages()
            .observe(lifecycleOwner, chatMessages -> adapter.prependMessages(chatMessages));
        repository.getOldMessages()
            .observe(lifecycleOwner, chatMessages -> adapter.appendMessages(chatMessages));

        lifecycleOwner.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source,
                @NonNull Lifecycle.Event event) {

                switch (event) {
                    case ON_PAUSE:
                        repository.stopListening();
                        break;
                    case ON_RESUME:
                        repository.listenForNewMessages();
                }
            }
        });
    }

    public void sendMessage(String message) {
        repository.sendMessage(message);
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

    public void onScrollEnded() {
        repository.getNextPage();
    }

    public Intent onClickOpenGallery(View v) {
        return imageRepository.getChooseFromGalleryIntent(v.getContext());
    }

    public void onOpenGalleryResult(Intent data) {
        imageRepository.onChooseFromGalleryResult(data, repository);
    }

    public Intent onTakePhoto(View v) {
        return imageRepository.getCameraIntent(v.getContext());
    }

    public void onTakePhotoResult() {
        imageRepository.onCameraIntentResult(repository);
    }
}
