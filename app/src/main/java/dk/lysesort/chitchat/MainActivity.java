package dk.lysesort.chitchat;

import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.getInstance();
        FirebaseInstanceId.getInstance().getInstanceId();

        try {
            // Let user see splash screen
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
