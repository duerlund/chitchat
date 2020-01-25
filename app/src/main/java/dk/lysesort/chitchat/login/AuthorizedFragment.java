package dk.lysesort.chitchat.login;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class AuthorizedFragment extends Fragment {
    public static final int RC_SIGN_IN = 123;
    private AuthRepository authRepository = new AuthRepository();

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getContext(), "Logging in...", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Oh noes! An error occurred", Toast.LENGTH_LONG)
                    .show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!authRepository.isSignedIn()) {
            startActivityForResult(authRepository.getSignInIntent(), RC_SIGN_IN);
        }
    }
}
