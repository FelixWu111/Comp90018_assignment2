package org.unimelb.BirdMigration;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;


public class StartActivity extends AppCompatActivity {

    private TextView weatherLabel;
    private Switch modeSwitch;
    private Switch lightSwitch;

    // pass to intent
    private boolean motionSign = false;
    private boolean lightSign = false;
    private String weather = "";
    //Video View
    private VideoView loadingVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        weatherLabel = findViewById(R.id.locationLabel);
        Intent intent = getIntent();
        weather = intent.getStringExtra("weather");
        weatherLabel.setText(weather);

        // Mode change swith
        // Func1
        modeSwitch = (Switch) findViewById(R.id.locationSwitch);
        modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                motionSign = !motionSign;
            }
        });
        // Func2
        lightSwitch = (Switch) findViewById(R.id.lightSwitch);
        lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lightSign = !lightSign;
            }
        });
    }


    // Jump to the MainActivity interface
    public void startGame(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("weather", weather);
        intent.putExtra("modeSign", motionSign);
        intent.putExtra("lightSign", lightSign);

        startActivity(intent);
    }

}
