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
    private static final String TAG = "ChatRoomRepository";
    private FirebaseFirestore db;
    private MutableLiveData<List<ChatRoom>> chatRooms;

    public ChatRoomRepository() {
        db = FirebaseFirestore.getInstance();
        chatRooms = new MutableLiveData<>();
        fetchChatRooms();
    }

    /**
     * Fetch chat rooms and order them by newest message.
     * The result is posted in {{@link #getChatRooms()}}
     */
    public void fetchChatRooms() {
        db.collection("chatrooms")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener(task -> {
                List<ChatRoom> chatRooms = new ArrayList<>();
                if (!task.isSuccessful()) {
                    Log.e(TAG, "Error fetching chat rooms", task.getException());
                    return;
                }

                for (QueryDocumentSnapshot document : task.getResult()) {
                    ChatRoom chatRoom = document.toObject(ChatRoom.class);
                    chatRoom.setId(document.getId());
                    chatRooms.add(chatRoom);
                }
                this.chatRooms.postValue(chatRooms);
            });
    }

    public LiveData<List<ChatRoom>> getChatRooms() {
        return chatRooms;
    }
}
