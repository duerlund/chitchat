package dk.lysesort.chitchat.chatroomlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dk.lysesort.chitchat.R;

public class ChatRoomListFragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;
    private ChatRoomAdapter adapter;
    private ChatRoomListViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_room_list_fragment, container, false);



        recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        swipeContainer = view.findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(() -> viewModel.refreshChatRooms());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            String chatRoomId = getActivity().getIntent().getStringExtra("chatRoomId");
            getActivity().getIntent().removeExtra("chatRoomId");

            if (chatRoomId != null) {
                Toast.makeText(getActivity(), "Go to " + chatRoomId, Toast.LENGTH_LONG).show();
                ChatRoomListFragmentDirections.ActionChatRoomListFragmentToChatRoomFragment action = ChatRoomListFragmentDirections
                    .actionChatRoomListFragmentToChatRoomFragment(chatRoomId);

                Navigation.findNavController(getView()).navigate(action);
            }
        } catch (NullPointerException e) {
            // ignore
        }


        adapter = new ChatRoomAdapter(position -> {
            ChatRoom room = viewModel.getChatRooms().getValue().get(position);
            ChatRoomListFragmentDirections.ActionChatRoomListFragmentToChatRoomFragment action = ChatRoomListFragmentDirections
                .actionChatRoomListFragmentToChatRoomFragment(room.getId());

            Navigation.findNavController(getView()).navigate(action);

        });
        recyclerView.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(ChatRoomListViewModel.class);
        viewModel.getChatRooms().observe(this, chatRooms -> {
            adapter.setData(chatRooms);
            swipeContainer.setRefreshing(false);
        });
    }
}
