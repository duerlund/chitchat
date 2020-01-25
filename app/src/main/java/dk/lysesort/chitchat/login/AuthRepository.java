package dk.lysesort.chitchat.login;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class AuthRepository {

    private static final String TAG = "Authorization";

    /**
     *
     * @return
     */
    public boolean isSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public void signOut(Context context) {
        AuthUI.getInstance()
            .signOut(context)
            .addOnFailureListener(exception -> {
                Log.e(TAG, "Error signing out", exception);
                Toast.makeText(context, "Failed to sign out", Toast.LENGTH_SHORT).show();
            });
    }

    /**
     * @return Returns an intent for signing in.
     */
    public Intent getSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            new AuthUI.IdpConfig.FacebookBuilder().build()
        );

        return AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build();
    }
}
