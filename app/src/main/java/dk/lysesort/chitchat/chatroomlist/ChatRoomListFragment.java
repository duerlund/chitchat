package dk.lysesort.chitchat.chatroomlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dk.lysesort.chitchat.R;
import dk.lysesort.chitchat.login.AuthorizedFragment;

/**
 * Displays a list of chat rooms
 */
public class ChatRoomListFragment extends AuthorizedFragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;
    private ChatRoomAdapter adapter;
    private ChatRoomListViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
            String messageId = getActivity().getIntent().getStringExtra("messageId");

            getActivity().getIntent().removeExtra("chatRoomId");
            getActivity().getIntent().removeExtra("messageId");

            if (chatRoomId != null && messageId != null) {
                NavDirections directions = ChatRoomListFragmentDirections
                    .actionChatRoomListFragmentToChatRoomFragment(chatRoomId);

                Navigation.findNavController(getView()).navigate(directions);
            }
        } catch (NullPointerException e) {
            // ignore
        }

        adapter = new ChatRoomAdapter(position -> Navigation.findNavController(getView()).navigate(viewModel.onChatRoomSelect(position)));
        recyclerView.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(ChatRoomListViewModel.class);
        viewModel.getChatRooms().observe(this, chatRooms -> {
            adapter.setData(chatRooms);
            swipeContainer.setRefreshing(false);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.chat_room_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_signout) {
            viewModel.onSignOut(getContext());
        }
        return super.onOptionsItemSelected(item);
    }
}