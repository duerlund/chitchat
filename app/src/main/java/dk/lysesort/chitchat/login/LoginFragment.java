package dk.lysesort.chitchat.login;

import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.Navigation;
import dk.lysesort.chitchat.R;

public class LoginFragment extends Fragment {
    public static final int RC_LOGIN = 123;
    private LoginViewModel viewModel;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (viewModel.showLogin()) {
            startActivityForResult(viewModel.getLoginIntent(), RC_LOGIN);
        } else {
            Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_chatRoomListFragment);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_chatRoomListFragment);
            }
        }
    }
}
