package dk.lysesort.chitchat.chatroom;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dk.lysesort.chitchat.R;

public class ChatRoomFragment extends Fragment {
    private String chatRoomId;
    private ChatRoomViewModel viewModel;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        chatRoomId = ChatRoomFragmentArgs.fromBundle(getArguments()).getChatRoomId();

        View view = inflater.inflate(R.layout.chat_room_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setSmoothScrollbarEnabled(true);
//        layoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);

        EditText editText = view.findViewById(R.id.editText);

        Button button = view.findViewById(R.id.button);
        button.setOnClickListener(v -> {
            String message = editText.getText().toString();
            viewModel.sendMessage("Mr. X", message);
            editText.setText("");
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ChatRoomViewModel.class);

        ChatMessageAdapter adapter = new ChatMessageAdapter();
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) &&
                    newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d("SCROLL", "Reached the end");
                    viewModel.onScrollToEnd();
                }
            }
        });

        viewModel.getMessages()
            .observe(this, chatMessages -> {
                adapter.addMessages(chatMessages);
                viewModel.listenForNewMessages();
            });

        viewModel.getNewMessages().observe(this, adapter::newMessages);
        viewModel.getOldMessages().observe(this, adapter::addMessages);

        Toast.makeText(getActivity(), "Welcome to " + chatRoomId, Toast.LENGTH_SHORT)
            .show();
    }
}
