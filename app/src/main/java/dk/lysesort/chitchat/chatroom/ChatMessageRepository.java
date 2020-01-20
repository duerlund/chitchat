package dk.lysesort.chitchat.chatroom;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ChatMessageRepository {
    private static final String TAG = "Message";
    private FirebaseFirestore db;
    private MutableLiveData<ChatMessage> messages;
    private String chatRoomId;

    public ChatMessageRepository(String chatRoomId) {
        db = FirebaseFirestore.getInstance();
        messages = new MutableLiveData<>();
        this.chatRoomId = chatRoomId;
//        fetchMessages();
    }

    public void fetchMessages() {
        db.collection("rooms/" + chatRoomId + "/messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener(task -> {
                List<ChatMessage> messages = new ArrayList<>();
                if (!task.isSuccessful()) {
                    Log.e(TAG, "Error getting messages", task.getException());
                }

                for (QueryDocumentSnapshot document : task.getResult()) {
                    ChatMessage message = document.toObject(ChatMessage.class);
                    Log.d(TAG, message + "");
                    this.messages.postValue(message);
                }
            });
    }

    public void getNewMessages() {
        db.collection("rooms/" + chatRoomId + "/messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value,
                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.e(TAG, "Listen failed", e);
                        return;
                    }

                    for (QueryDocumentSnapshot doc : value) {
                        ChatMessage message = doc.toObject(ChatMessage.class);
                        Log.d(TAG, "Received new message:" + message);
                        messages.postValue(message);
                    }
                }
            });
    }

    public LiveData<ChatMessage> getMessages() {
        return messages;
    }

    public void sendMessage(String user, String message) {
        Map<String, Object> chatMessage = new HashMap<>();
        chatMessage.put("user", user);
        chatMessage.put("text", message);
        chatMessage.put("timestamp", new Timestamp(new Date()));

        db.collection("rooms/" + chatRoomId + "/messages")
            .add(chatMessage)
            .addOnSuccessListener(documentReference -> {
                Log.d(TAG, "Sending message " + user + " -> " + message);
            })
            .addOnFailureListener(exception -> {
                Log.e(TAG, "Failed to send message", exception);
            });
    }
}