package dk.lysesort.chitchat.login;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import dk.lysesort.chitchat.R;

public class LoginFragment extends Fragment {
    public static final int RC_SIGN_IN = 123;
    private AuthRepository authRepository = new AuthRepository();
    private boolean firstTime = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!authRepository.isSignedIn()) {
            if (firstTime) {
                startActivityForResult(authRepository.getSignInIntent(), RC_SIGN_IN);
                firstTime = false;
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.login_alert_title)
                    .setMessage(R.string.login_alert_message)
                    .setPositiveButton(
                        R.string.login_alert_button_positive, (dialog, which) ->
                            startActivityForResult(authRepository.getSignInIntent(), RC_SIGN_IN))
                    .setNegativeButton(
                        R.string.login_alert_button_negative, (dialog, which) -> {
                            if (getActivity() != null) {
                                getActivity().finish();
                            }
                        })
                    .create()
                    .show();
            }
        } else {
            NavDirections navDirections = LoginFragmentDirections.actionLoginFragmentToChatRoomListFragment();
            Navigation.findNavController(getView()).navigate(navDirections);
        }
    }
}
