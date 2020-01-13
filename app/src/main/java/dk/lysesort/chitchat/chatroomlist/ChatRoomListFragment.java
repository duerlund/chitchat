package dk.lysesort.chitchat.chatroomlist;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dk.lysesort.chitchat.R;

public class ChatRoomListFragment extends Fragment {

    private ChatRoomListViewModel mViewModel;

    public static ChatRoomListFragment newInstance() {
        return new ChatRoomListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_room_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ChatRoomListViewModel.class);
        // TODO: Use the ViewModel
    }
}
