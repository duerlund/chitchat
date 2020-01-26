package dk.lysesort.chitchat.chatroomlist;

import android.content.Context;

import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavDirections;
import dk.lysesort.chitchat.login.AuthRepository;

public class ChatRoomListViewModel extends ViewModel {
    public static final String TAG = "Chatroom";
    private ChatRoomRepository repository = new ChatRoomRepository();

    public void refreshChatRooms() {
        repository.fetchChatRooms();
    }

    public NavDirections onChatRoomSelect(int position) {
        ChatRoom chatRoom = getChatRooms().getValue().get(position);
        NavDirections directions = ChatRoomListFragmentDirections.actionChatRoomListFragmentToChatRoomFragment(
            chatRoom.getId());
        return directions;
    }

    public LiveData<List<ChatRoom>> getChatRooms() {
        return repository.getChatRooms();
    }

    public void onSignOut(Context context, OnSuccessListener successListener) {
        AuthRepository authRepository = new AuthRepository();
        authRepository.signOut(context, successListener);
    }
}