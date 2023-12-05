package com.example.a2048game;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final String FILE_NAME = "MyData";
    private final String HIGHEST_SCORE = "highest_score";

    private TextView textScore;
    private TextView textHighestScore;
    private Button buttonReplay;

    private int score = 0;
    private int highestScore = 0;

    public static MainActivity mainActivity;

    public MainActivity(){
        mainActivity = this;
    }

    public void addScore(int score) {
        this.score += score;
        textScore.setText("Score : " + this.score);
        //Renew higher score
        updateHighestScore(this.score);
    }

    private void updateHighestScore(int score){
        if(score > highestScore){
            highestScore = score;
            textHighestScore.setText("HighestScore : " + score);
            //Save the highest score in the mobile as Preferences
            SharedPreferences shp = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = shp.edit();

            editor.putInt(HIGHEST_SCORE, highestScore);
            editor.apply();
        }
    }

    public void clearScore(){
        score = 0;
        textScore.setText("Score : " + 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textScore = findViewById(R.id.textScore);
        textHighestScore = findViewById(R.id.textHighestScore);
        buttonReplay = findViewById(R.id.buttonReplay);

        //Read the highest score and show it
        SharedPreferences shp = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        highestScore = shp.getInt(HIGHEST_SCORE, 0);
        textHighestScore.setText("HighestScore : " + highestScore);

        buttonReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameView.gameView.replayGame();
            }
        });
    }

    private boolean isExit = false;

    class ExitHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                isExit = false;//edit the status as exit
            }
        }

    };

    ExitHandler mHandler = new ExitHandler();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) { //When click the 'back button', ask user if they want to quit
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(!isExit){
                isExit = true;
                Toast.makeText(this, "Push one more time to exit the game", Toast.LENGTH_SHORT).show();
                //delay send empty message
                mHandler.sendEmptyMessageDelayed(0, 2000);
            }
            else{
                finish();
            }
        }
        return false;
    }
}

