package dk.lysesort.chitchat.chatroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dk.lysesort.chitchat.R;
import dk.lysesort.chitchat.login.AuthorizedFragment;

public class ChatRoomFragment extends AuthorizedFragment {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int OPEN_CAMERA_REQUEST = 2;
    private String chatRoomId;
    private ChatRoomViewModel viewModel;
    private RecyclerView recyclerView;
    private String currentImagePath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case OPEN_CAMERA_REQUEST:
                File file = new File(currentImagePath);
                Uri uri = Uri.fromFile(file);
                uploadImage(uri);
                break;
            case PICK_IMAGE_REQUEST:
                if (data == null) {
                    return;
                }
                Uri uri2 = data.getData();
                uploadImage(uri2);
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(
            this,
            new ChatRoomViewModelFactory(chatRoomId))
            .get(ChatRoomViewModel.class);

        recyclerView.setAdapter(viewModel.getAdapter());
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

        viewModel.listenForUpdates(this);
    }

    @Override
    public void onResume() {

        super.onResume();

        if (!ChatRoomPreferenceRepository.hasAnswered(getContext(), chatRoomId)) {
            new AlertDialog.Builder(getContext())
                .setMessage("Would you like to receive push notifications for this chat room?")
                .setPositiveButton(
                    "Yes",
                    (dialog, which) -> FirebaseMessaging.getInstance()
                        .subscribeToTopic("chatroom." + chatRoomId)
                        .addOnSuccessListener(
                            aVoid ->
                            {
                                ChatRoomPreferenceRepository.subscribe(getContext(), chatRoomId);
                                Toast.makeText(getActivity(),
                                               "Subscribed to push",
                                               Toast.LENGTH_SHORT).show();
                            })

                )
                .setNegativeButton(
                    "No", (dialog, which) -> FirebaseMessaging.getInstance()
                        .unsubscribeFromTopic("chatroom." + chatRoomId)
                        .addOnSuccessListener(
                            aVoid ->
                            {
                                ChatRoomPreferenceRepository.unsubscribe(getContext(), chatRoomId);
                                Toast.makeText(getActivity(),
                                               "Unsubscribed",
                                               Toast.LENGTH_SHORT).show();
                            }))
                .create()
                .show();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.chat_room_menu, menu);
    }

    private void uploadImage(Uri file) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        String suffix = MimeTypeMap.getFileExtensionFromUrl(file.toString());
        StorageReference imageReference = storageReference.child(
            "images/" + "IMG" + UUID.randomUUID() + "." + (suffix.isEmpty() ? "jpg" : suffix));

        imageReference.putFile(file)
            .addOnFailureListener(e -> Log.e("UPLOAD", "Failed to upload image", e))
            .addOnSuccessListener(
                taskSnapshot -> {
                    viewModel.sendMessage(imageReference);
                    Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        chatRoomId = ChatRoomFragmentArgs.fromBundle(getArguments()).getChatRoomId();

        View view = inflater.inflate(R.layout.chat_room_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setSmoothScrollbarEnabled(true);

        recyclerView.setLayoutManager(layoutManager);

        EditText editText = view.findViewById(R.id.editText);

        ImageView send = view.findViewById(R.id.button);
        send.setOnClickListener(v -> {
            String message = editText.getText().toString();
            viewModel.sendMessage(message);
            editText.setText("");
        });

        ImageView cameraButton = view.findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(v -> openCamera());

        ImageView imageButton = view.findViewById(R.id.upload_button);
        imageButton.setOnClickListener(v -> chooseImage());

        return view;
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

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private File createImageFile() throws IOException {
        UUID uuid = UUID.randomUUID();
        String imageFileName = "IMG" + uuid;
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentImagePath = image.getAbsolutePath();
        return image;
    }
}
