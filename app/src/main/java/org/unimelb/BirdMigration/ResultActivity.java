package org.unimelb.BirdMigration;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private String weather = "Clear";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView scoreLabel = findViewById(R.id.scoreLabel);
        TextView highScoreLabel = findViewById(R.id.highScoreLabel);

        // get the value of score from MainActivity
        Intent intetnReturn = getIntent();
        int score = intetnReturn.getIntExtra("Score", 0);
        weather = intetnReturn.getStringExtra("Weather");

        scoreLabel.setText(getString(R.string.final_score, score));

        // High Score
        // 在Android中我们通常使用 一个轻量级的存储类——SharedPreferences来保存用户偏好的参数
        // SharedPreferences也是使用xml文件, 类似于Map集合,使用键-值的形式来存储数据
        // 我们只需要调用SharedPreferences的getXxx(name), 就可以根据键获得对应的值
        SharedPreferences sharedPreferences = getSharedPreferences("Game_Data", Context.MODE_PRIVATE);
        int highScore = sharedPreferences.getInt("High_Score", 0);

        if (score > highScore) {
            // Update HighScore
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("High_Score", score);
            editor.apply();

            //highScoreLabel.setText("High Score : " + score);
            highScoreLabel.setText(getString(R.string.high_score, score));
        } else {
            //highScoreLabel.setText("High Score : " + highScore);
            highScoreLabel.setText(getString(R.string.high_score, highScore));
        }
    }

    // Jump to the MainActivity interface
    public void TryAgain(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    // Jump to the home
    public void ReturnHome(View view) {
        Intent intentReturn = new Intent(getApplicationContext(), StartActivity.class);
        intentReturn.putExtra("weather", weather);
        startActivity(intentReturn);
    }

    // This method is executed when the phone presses the 'back' button
    @Override
    public void onBackPressed() {
    }
}