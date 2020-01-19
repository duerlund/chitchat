package dk.lysesort.chitchat.chatroom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import dk.lysesort.chitchat.R;

public class ChatRoomFragment extends Fragment {
    private String chatRoomId;
    private ChatRoomViewModel viewModel;

    public static ChatRoomFragment newInstance() {
        return new ChatRoomFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        chatRoomId = ChatRoomFragmentArgs.fromBundle(getArguments()).getChatRoomId();

        return inflater.inflate(R.layout.chat_room_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ChatRoomViewModel.class);
        Toast.makeText(getActivity(), "Welcome to " + chatRoomId, Toast.LENGTH_SHORT)
            .show();
    }
}
