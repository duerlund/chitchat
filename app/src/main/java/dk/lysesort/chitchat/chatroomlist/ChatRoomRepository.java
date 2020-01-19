package dk.lysesort.chitchat.chatroomlist;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ChatRoomRepository {
    private static final String TAG = "ROOM";
    private FirebaseFirestore db;
    private MutableLiveData<List<ChatRoom>> chatRooms;

    public ChatRoomRepository() {
        db = FirebaseFirestore.getInstance();
        chatRooms = new MutableLiveData<>();
        fetchChatRooms();
    }

    public void fetchChatRooms() {
        db.collection("rooms")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener(task -> {
                List<ChatRoom> chatRooms = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ChatRoom chatRoom = document.toObject(ChatRoom.class);
                        chatRooms.add(chatRoom);
                    }
                    this.chatRooms.postValue(chatRooms);
                } else {
                    Log.e(TAG, "Error getting chatrooms", task.getException());
                }
            });
    }

    public LiveData<List<ChatRoom>> getChatRooms() {
        return chatRooms;
    }
}
