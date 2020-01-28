package dadm.dadm101;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private final static String LIFECYCLE = "LifeCycle";
    private final static int SECOND_ACTIVITY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText(R.string.changed_mario);

        Log.d(LIFECYCLE, "onCreate");
    }

    public void launchActivity(View view){
        final Intent intent = new Intent(
                MainActivity.this,
                SecondaryActivity.class);
        intent.putExtra("message", "Este es el mensaje");
        startActivityForResult(intent, SECOND_ACTIVITY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LIFECYCLE, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LIFECYCLE, "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LIFECYCLE, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LIFECYCLE, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LIFECYCLE, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LIFECYCLE, "oDestroy");
    }
}
