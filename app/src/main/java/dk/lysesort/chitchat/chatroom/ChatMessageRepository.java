package dk.lysesort.chitchat.chatroom;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.MutableLiveData;

/**
 * The chat message repository can retrieve old messages and post new ones to a chat room.
 * <p>
 * Retrieving messages: Messages are fetched in a fixed page size of 50. Observing {@link
 * #getMessages()} will return the first 50 messages and after that, it is updated with new chat
 * messages.
 * <p>
 * To retrieve old messages, observe {@link #getOldMessages()} and call {@link #getNextPage()}
 * <p>
 * Sending messages: To send a chat message, use one of the following methods:
 * <p>
 * {@link #sendMessage(String)}
 * <p>
 * {@link #sendMessage(StorageReference)}
 */
public class ChatMessageRepository {
    private static final String TAG = "Message";
    private static final int MESSAGES_PER_PAGE = 50;

    private CollectionReference messageCollection;
    private DocumentReference chatRoomReference;
    private ListenerRegistration listenerRegistration;

    private DocumentSnapshot lastDocument;
    private MutableLiveData<List<ChatMessage>> messages;
    private MutableLiveData<List<ChatMessage>> oldMessages;
    private boolean updating = false;
    private boolean onLastPage = false;

    public ChatMessageRepository(String chatRoomId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        messages = new MutableLiveData<>();
        oldMessages = new MutableLiveData<>();

        messageCollection = db.collection("chatrooms/" + chatRoomId + "/messages");
        chatRoomReference = db.document("chatrooms/" + chatRoomId);
    }

    public MutableLiveData<List<ChatMessage>> getMessages() {
        return messages;
    }

    public void listenForNewMessages() {
        listenForNewMessages(null);
    }

    public void stopListening() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    private void listenForNewMessages(DocumentSnapshot snapshot) {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }

        listenerRegistration = newMessageQuery(snapshot).addSnapshotListener(
            (querySnapshot, exception) -> {
                if (exception != null) {
                    Log.e(TAG, "Database error", exception);
                    return;
                }

                if (querySnapshot.isEmpty()) {
                    return;
                }
                List<ChatMessage> newMessages = parseChatMessages(querySnapshot);

                getMessages().postValue(newMessages);
                updateLastDocumentCursor(querySnapshot);
                listenForNewMessages(querySnapshot.getDocuments().get(0));
            }
        );
    }

    private Query newMessageQuery(DocumentSnapshot snapshot) {
        if (snapshot == null) {
            return messageCollection.orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(MESSAGES_PER_PAGE);
        }

        return messageCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .endBefore(snapshot);
    }

    public void sendMessage(String message) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String profileImageUrl = currentUser.getPhotoUrl().toString();

        Map<String, Object> chatMessage = new HashMap<>();
        chatMessage.put("user", currentUser.getDisplayName());
        chatMessage.put("profileImageUrl", profileImageUrl);
        chatMessage.put("text", message);

        sendChatMessage(chatMessage);
    }

    private void sendChatMessage(Map<String, Object> chatMessage) {
        Timestamp now = Timestamp.now();
        chatMessage.put("timestamp", now);
        messageCollection
            .add(chatMessage)
            .addOnSuccessListener(documentReference -> updateChatRoomTimestamp(now))
            .addOnFailureListener(exception -> Log.e(TAG, "Failed to send message", exception));
    }

    private void updateChatRoomTimestamp(Timestamp timeStamp) {
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", timeStamp);
        chatRoomReference.set(data, SetOptions.merge())
            .addOnFailureListener(exception -> Log.e(TAG, "Failed to update timestamp", exception));
    }

    public void sendMessage(StorageReference imageReference) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String profileImageUrl = currentUser.getPhotoUrl().toString();

        Map<String, Object> chatMessage = new HashMap<>();
        chatMessage.put("user", currentUser.getDisplayName());
        chatMessage.put("profileImageUrl", profileImageUrl);

        imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            chatMessage.put("imageReference", imageReference.toString());
            chatMessage.put("imageUrl", uri.toString());
            sendChatMessage(chatMessage);
        });
    }

    /**
     * Fetches the next page of old messages, result is posted on {@link #getOldMessages()}.
     */
    public void getNextPage() {
        if (updating) {
            return;
        }

        if (lastDocument == null) {
            return;
        }

        if (onLastPage) {
            return;
        }

        updating = true;
        messageCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .startAfter(lastDocument)
            .limit(MESSAGES_PER_PAGE)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<ChatMessage> messages = parseChatMessages(querySnapshot);
                updateLastDocumentCursor(querySnapshot);
                getOldMessages().postValue(messages);
                updating = false;
            });
    }

    private List<ChatMessage> parseChatMessages(QuerySnapshot snapshot) {
        List<ChatMessage> messages = new ArrayList<>();

        for (QueryDocumentSnapshot document : snapshot) {
            ChatMessage message = document.toObject(ChatMessage.class);
            messages.add(message);
        }

        return messages;
    }

    private void updateLastDocumentCursor(QuerySnapshot snapshot) {
        if (snapshot == null || snapshot.isEmpty()) {
            return;
        }

        onLastPage = snapshot.size() < MESSAGES_PER_PAGE;
        lastDocument = snapshot.getDocuments().get(snapshot.size() - 1);
    }

    public MutableLiveData<List<ChatMessage>> getOldMessages() {
        return oldMessages;
    }
}