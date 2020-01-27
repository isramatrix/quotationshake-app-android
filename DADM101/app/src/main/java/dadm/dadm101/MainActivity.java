package dadm.dadm101;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("IT'S A ME MAAARIOOO!!");
    }
}
