package dk.lysesort.chitchat.chatroom;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ChatMessageRepository {
    private static final String TAG = "Message";
    private static final int MESSAGES_TO_LOAD = 25;
    private FirebaseFirestore db;
    private MutableLiveData<List<ChatMessage>> messages;
    private MutableLiveData<List<ChatMessage>> newMessages;
    private MutableLiveData<List<ChatMessage>> oldMessages;
    private String chatRoomId;

    private CollectionReference messageCollection;
    private ListenerRegistration listenerRegistration;
    private DocumentSnapshot newestMessage;
    private DocumentSnapshot oldestMessage;

    public ChatMessageRepository(String chatRoomId) {
        db = FirebaseFirestore.getInstance();
        messages = new MutableLiveData<>();
        newMessages = new MutableLiveData<>();
        oldMessages = new MutableLiveData<>();

        this.chatRoomId = chatRoomId;
        messageCollection = db.collection("rooms/" + chatRoomId + "/messages");
        fetchInitial();
    }

    public void fetchInitial() {
        messageCollection.orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(MESSAGES_TO_LOAD)
            .get()
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "Failed to fetch messages", task.getException());
                    return;
                }

                List<ChatMessage> messages = new ArrayList<>();

                for (QueryDocumentSnapshot result : task.getResult()) {
                    ChatMessage message = result.toObject(ChatMessage.class);
                    Log.d(TAG, "fetched:" + message);
                    messages.add(message);
                }

                this.messages.postValue(messages);

                if (task.getResult().size() > 0) {
                    List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                    newestMessage = documentSnapshots.get(0);
                    oldestMessage = documentSnapshots.get(documentSnapshots.size() - 1);

                    update(newestMessage);
                }
            });
    }

    public void listenForNewMessages() {
        update(newestMessage);
    }

    private void update(DocumentSnapshot newestMessage) {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }

        listenerRegistration = messageCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .endBefore(newestMessage)
            .addSnapshotListener((queryDocumentSnapshots, e) -> {
                Log.d(TAG, "onEvent");
                List<ChatMessage> messages = new ArrayList<>();
                for (QueryDocumentSnapshot result : queryDocumentSnapshots) {
                    ChatMessage message = result.toObject(ChatMessage.class);
                    Log.d(TAG, "new:" + message);
                    messages.add(result.toObject(ChatMessage.class));
                }
                newMessages.postValue(messages);

                if (!queryDocumentSnapshots.getDocuments().isEmpty()) {
                    update(queryDocumentSnapshots.getDocuments().get(0));
                }
            });
    }

    public LiveData<List<ChatMessage>> getMessages() {
        return messages;
    }

    public MutableLiveData<List<ChatMessage>> getNewMessages() {
        return newMessages;
    }

    public void sendMessage(String user, String message) {
        Map<String, Object> chatMessage = new HashMap<>();
        chatMessage.put("user", user);
        chatMessage.put("text", message);
        chatMessage.put("timestamp", Timestamp.now());

        db.collection("rooms/" + chatRoomId + "/messages")
            .add(chatMessage)
            .addOnSuccessListener(documentReference -> {
                Log.d(TAG, "Sending message " + user + " -> " + message);
            })
            .addOnFailureListener(exception -> {
                Log.e(TAG, "Failed to send message", exception);
            });
    }

    public void fetchOlderMessages() {
        getOlderMessages(oldestMessage);
    }

    private void getOlderMessages(DocumentSnapshot oldestMessage) {
        if (oldestMessage == null) {
            return;
        }

        messageCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .startAfter(oldestMessage)
            .limit(MESSAGES_TO_LOAD)
            .get()
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "Failed to fetch messages", task.getException());
                    return;
                }

                List<ChatMessage> messages = new ArrayList<>();

                for (QueryDocumentSnapshot result : task.getResult()) {
                    ChatMessage message = result.toObject(ChatMessage.class);
                    Log.d(TAG, "fetched:" + message);
                    messages.add(message);
                }

                this.oldMessages.postValue(messages);

                if (task.getResult().size() > 0) {
                    List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                    this.oldestMessage = documentSnapshots.get(documentSnapshots.size() - 1);
                }
            });
    }

    public LiveData<List<ChatMessage>> getOldMessages() {
        return oldMessages;
    }
}