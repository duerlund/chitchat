package dk.lysesort.chitchat;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.getInstance();

        FirebaseInstanceId.getInstance()
            .getInstanceId()
            .addOnSuccessListener(instanceIdResult -> {
                String token = instanceIdResult.getToken();
                Log.d("FCM", "InstanceId: " + token);
            });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
    }
}
