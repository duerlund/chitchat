package dk.lysesort.chitchat.chatroom;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import androidx.core.content.FileProvider;
import dk.lysesort.chitchat.R;

public class ImageRepository {
    private String currentCameraImagePath;

    public Intent getCameraIntent(Context context) {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(context.getPackageManager()) == null) {
            return null;
        }

        File image = null;

        try {
            image = createImageFile(context);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (image == null) {
            return null;
        }

        Uri imageUri = FileProvider.getUriForFile(
            context, "dk.lysesort.chitchat.fileprovider", image);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        return intent;
    }

    private File createImageFile(Context context) throws IOException {
        UUID uuid = UUID.randomUUID();
        String imageFileName = "IMG" + uuid;
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentCameraImagePath = image.getAbsolutePath();
        return image;
    }

    public void onCameraIntentResult(Intent data, ChatMessageRepository repository) {
        File file = new File(currentCameraImagePath);
        Uri uri = Uri.fromFile(file);
        uploadImage(uri, repository);
    }

    /**
     * Uploads an image and posts a message to the chat message repository.
     *
     * @param imageFile             Image file to upload.
     * @param chatMessageRepository Repository to post the image in.
     */
    public void uploadImage(Uri imageFile, ChatMessageRepository chatMessageRepository) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        String suffix = MimeTypeMap.getFileExtensionFromUrl(imageFile.toString());
        StorageReference imageReference = storageReference.child(
            "images/" + "IMG" + UUID.randomUUID() + "." + (suffix.isEmpty() ? "jpg" : suffix));

        imageReference.putFile(imageFile)
            .addOnFailureListener(e -> Log.e("UPLOAD", "Failed to upload image", e))
            .addOnSuccessListener(taskSnapshot -> chatMessageRepository.sendMessage(imageReference));
    }

    public Intent getChooseFromGalleryIntent(Context context) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return Intent.createChooser(
            intent, context.getString(R.string.gallery_intent_title));
    }

    public void onChooseFromGalleryResult(Intent data, ChatMessageRepository repository) {
        if (data == null) {
            return;
        }
        Uri uri = data.getData();
        ImageRepository r = new ImageRepository();
        r.uploadImage(uri, repository);

    }
}
