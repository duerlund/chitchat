package dk.lysesort.chitchat.login;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    public boolean showLogin() {
        return true;
    }

    public void onSignOut(Context context ) {
        AuthUI.getInstance()
            .signOut(context)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.e("login", "Sign out");
                }
            });
    }

    /**
     * Creates an intent for logging in.
     *
     * @return Intent for login
     */
    public Intent getLoginIntent() {
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
