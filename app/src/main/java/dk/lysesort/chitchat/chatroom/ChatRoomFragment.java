package dk.lysesort.chitchat.chatroom;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dk.lysesort.chitchat.R;

public class ChatRoomFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int OPEN_CAMERA_REQUEST = 2;
    private String chatRoomId;
    private ChatRoomViewModel viewModel;
    private RecyclerView recyclerView;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

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

        Button imageButton = view.findViewById(R.id.upload_button);
        imageButton.setOnClickListener(v -> openCamera());

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

    private void openCamera() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getActivity().getPackageManager()) == null) {
            return;
        }

        File image = null;

        try {
            image = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (image == null) {
            return;
        }

        Uri imageUri = FileProvider.getUriForFile(
            getContext(), "dk.lysesort.chitchat.fileprovider", image);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, OPEN_CAMERA_REQUEST);
    }

    private File createImageFile() throws IOException {
        UUID uuid = UUID.randomUUID();
        String imageFileName = "IMG_" + uuid;
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
}
