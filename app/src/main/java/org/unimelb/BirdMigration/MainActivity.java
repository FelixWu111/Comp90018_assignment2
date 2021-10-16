package org.unimelb.BirdMigration;

import android.content.Intent;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private final Handler handler = new Handler();
    // Elements
    private TextView scoreLabel, startLabel;
    private ImageView catcher;
    // Size
    private int screenHeight, screenWidth;
    private int frameHeight;
    private int catcherSize;
    // Position
    private float catcherY;
    // Speed
    private int catcherSpeed;
    // Score
    private int score;
    // Timer
    private Timer timer = new Timer();
    // Flag
    private boolean actionFlag = false;
    private boolean startFlag = false;

    // SoundPlayer
    private SoundPlayer soundPlayer;

    // Object
    private Food food, food2, bomb;

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightEventListener;

    private Gyroscope gyroscope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreLabel = findViewById(R.id.scoreLabel);
        startLabel = findViewById(R.id.startLabel);
        catcher = findViewById(R.id.catcher);

        // Screen Size
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;   // 1080
        screenHeight = size.y;  // 1794

        // Init soundPlayer
        soundPlayer = new SoundPlayer(this);

        // Init objects
        food = new Food(10, 20, Math.round(screenWidth / 90.0f), findViewById(R.id.object1), soundPlayer.loadSound(this, R.raw.hit));
        food2 = new Food(30, 3000, Math.round(screenWidth / 54.0f), findViewById(R.id.object2), soundPlayer.loadSound(this, R.raw.hit1));
        bomb = new Food(0, 10, Math.round(screenWidth / 60.0f), findViewById(R.id.bomb), soundPlayer.loadSound(this, R.raw.over));

        // Speed
        catcherSpeed = Math.round(screenHeight / 90.0f); // 1794 / 90 = 20

        // Show the score
        scoreLabel.setText(getString(R.string.score, score));

        // Load the background music
        Intent intent = new Intent(getApplicationContext(), MusicServer.class);
        startService(intent);

        //gyroscope sensor
        gyroscope = new Gyroscope(this);
        gyroscope.setListener(new Gyroscope.Listener() {
            @Override
            public void onRotation(float rx, float ty, float rz) {
                if(rx < -1.0f){
                    actionFlag = true;
                }else if(rx > 1.0f){
                    actionFlag = false;
                }
            }
        });

        // Declare light sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // In case the sensor does not exist. 
        if (lightSensor == null) {
            Toast.makeText(this, "Light sensor is missing.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Activities on light sensor value intake in every 2 seconds
        lightEventListener = new SensorEventListener() {
            private long lastTimestamp;

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                long currentTimestamp = sensorEvent.timestamp;

                // Read sensor data every 2 seconds
                if (currentTimestamp - lastTimestamp >= TimeUnit.SECONDS.toNanos(2)) {
                    lastTimestamp = currentTimestamp;
                    float value = sensorEvent.values[0];

                    // Limit the value in a valid range
                    int brightness = (int) (255f * value / lightSensor.getMaximumRange());

                    // Adjust system brightness
                    // Does not take effect in android emulators.
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.screenBrightness = brightness;
                    getWindow().setAttributes(lp);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        gyroscope.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightEventListener);
        gyroscope.unregister();

    }

    // This method defines the moving position and distance of each object after starting game
    public void changePos() {

        // Evoke hitCheck function
        hitCheck(food, false);
        hitCheck(food2, false);
        hitCheck(bomb, true);

        // Update food/bomb position
        food.updatePos(screenWidth, frameHeight);
        food2.updatePos(screenWidth, frameHeight);
        bomb.updatePos(screenWidth, frameHeight);

        // Update catcher position
        if (actionFlag) {
            // Touching
            catcherY -= catcherSpeed;
        } else {
            // Releasing
            catcherY += catcherSpeed;
        }

        // Ensure the catcher won't jump out of the home screen
        if (catcherY < 0) catcherY = 0;
        if (catcherY > frameHeight - catcherSize) catcherY = frameHeight - catcherSize;
        catcher.setY(catcherY);

        scoreLabel.setText(getString(R.string.score, score));
    }


    // This method determines whether increase scores or end the game when the box touches the object
    public void hitCheck(Food food, boolean isOver) {

        // Get the X,Y of midpoint
        float centerX = food.getX() + food.getImage().getWidth() / 2.0f;
        float centerY = food.getY() + food.getImage().getHeight() / 2.0f;

        // Boundary condition judgment
        if (0 < centerX && centerX <= catcherSize &&
                catcherY <= centerY && centerY <= catcherY + catcherSize) {
            food.setX(-100.0f);
            score += food.getScore();
            soundPlayer.playSound(food.getSound());
            if (isOver) {
                // Game over
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                // Show the score, and jump to the result interface (ResultActivity)
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                // transport single data
                intent.putExtra("Score", score);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!startFlag) {
            startFlag = true;

            // FrameHeight
            FrameLayout frameLayout = findViewById(R.id.frame);
            frameHeight = frameLayout.getHeight();

            // Box
            catcherY = catcher.getY();
            catcherSize = catcher.getHeight();

            // GONE will hide the label, and the interface won't reserve the space occupied by the label (different with INVISIBLE)
            startLabel.setVisibility(View.GONE);

            // Take 'timer' method as a timer, and run the changePos method per 15 millisecond period
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(() -> changePos());
                }
            }, 0, 15);
        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                actionFlag = true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                actionFlag = false;
            }
        }
        return super.onTouchEvent(event);
    }

    // This method is executed when the phone presses the 'back' button
    @Override
    public void onBackPressed() {
    }
}