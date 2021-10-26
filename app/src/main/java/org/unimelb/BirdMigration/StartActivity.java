package org.unimelb.BirdMigration;

import android.app.Dialog;
import android.content.Intent;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;


public class StartActivity extends AppCompatActivity {

    private TextView weatherLabel;
    private TextView locationLabel;
    private Switch modeSwitch;
    private Switch lightSwitch;
    private Button birdBtn;  // new
    private Button helpButton;     // menu button
    Dialog dialog;
//    private Toast toast;

    // pass to intent
    private boolean motionSign = false;
    private boolean lightSign = false;
    private String weather = "";
    private String cityName = "";   //new

    //Video View
    private VideoView loadingVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        locationLabel = findViewById(R.id.locationInfo);     //city name
        weatherLabel = findViewById(R.id.locationLabel);   //weather

        Intent intent = getIntent();
        cityName = intent.getStringExtra("cityName");
        weather = intent.getStringExtra("weather");
        locationLabel.setText(cityName);
        weatherLabel.setText(weather);

        String helpMessage = "Hello, Bird from " + cityName + "\nTake care, today's weather is " + weather;
        Toast toast = Toast.makeText(getApplicationContext(),"Toast",Toast.LENGTH_SHORT);


        // click on the bird to show weather and city.
        birdBtn = findViewById(R.id.birdBtn);
        birdBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String helpMessage = "Hello, Bird from " + cityName + "\nTake care, today's weather is " + weather;
                Toast toast = Toast.makeText(getApplicationContext(), helpMessage, Toast.LENGTH_SHORT);
                toast.show();
            }
        });





        // help menu
        dialog = new Dialog(StartActivity.this);
        dialog.setContentView(R.layout.help_layout);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.background));   //set corner background for dialog
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);    // quit dialog
        // Menu button
        helpButton = findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });


        // Mode change switch
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
