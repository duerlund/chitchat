package dk.lysesort.chitchat.chatroom;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        chatRoomId = ChatRoomFragmentArgs.fromBundle(getArguments()).getChatRoomId();

        View view = inflater.inflate(R.layout.chat_room_fragment, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(false);

        recyclerView.setLayoutManager(layoutManager);

        EditText editText = view.findViewById(R.id.editText);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEND ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    String message = textView.getText().toString();


                    if (message.trim().isEmpty()) {
                        return true;
                    }
                    viewModel.sendMessage(message);
                    textView.setText("");
                    return true;
                }

                return false;
            }
        });

        ImageView send = view.findViewById(R.id.button);
        send.setOnClickListener(v -> {
            String message = editText.getText().toString();
            if (message.trim().isEmpty()) {
                return;
            }
            viewModel.sendMessage(message);
            editText.setText("");
        });

        ImageView cameraButton = view.findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(v -> openCamera());

        ImageView imageButton = view.findViewById(R.id.upload_button);
        imageButton.setOnClickListener(v -> chooseImage());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(
            this,
            new ChatRoomViewModelFactory(chatRoomId))
            .get(ChatRoomViewModel.class);

        recyclerView.setAdapter(viewModel.getAdapter());

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (!recyclerView.canScrollVertically(1) &&
//                    newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    Log.d("SCROLL", "Reached the end");
//                    viewModel.onScrollToEnd();
//                }
//            }
//        });

        viewModel.listenForUpdates(this);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.chat_room, menu);

        boolean isSubscribed = ChatRoomPreferenceRepository
            .isSubscribedToPush(getContext(), chatRoomId);
        menu.findItem(R.id.action_toggle_notifications)
            .setIcon(isSubscribed ? R.drawable.baseline_notifications_active_24
                                  : R.drawable.baseline_notifications_off_24);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_notifications) {
            boolean state = viewModel.onToggleNotifications(getContext());
            item.setIcon(state ? R.drawable.baseline_notifications_active_24
                               : R.drawable.baseline_notifications_off_24);
        }

        return super.onOptionsItemSelected(item);
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
        startActivityForResult(Intent.createChooser(intent,
                                                    getString(R.string.gallery_intent_title)),
                               PICK_IMAGE_REQUEST);
    }

    private File createImageFile() throws IOException {
        UUID uuid = UUID.randomUUID();
        String imageFileName = "IMG" + uuid;
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentImagePath = image.getAbsolutePath();
        return image;
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
    public void onResume() {
        super.onResume();
        viewModel.showPushNotificationDialog(getContext());
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
}
