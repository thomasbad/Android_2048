package com.example.a2048game;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.GridLayout;
import android.widget.Toast;

import java.util.Random;

public class GameView extends GridLayout {

    public static GameView gameView;

    private Card[][] cards = new Card[4][4];

    public GameView(Context context) {
        super(context);
        gameView = this;
        initGame();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gameView = this;
        initGame();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gameView = this;
        initGame();
    }

    public void initGame(){
        this.setBackgroundColor(getResources().getColor(R.color.gameViewBackgroundColor, null));
        setColumnCount(4);

        int cardWidth = GetCardWidth();
        addCards(cardWidth, cardWidth);

        randomCreateCard(2);

        setListener();
    }

    public void replayGame(){
        MainActivity.mainActivity.clearScore();
        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 4; ++j){
                cards[i][j].setNum(0);
            }
        }
        randomCreateCard(2);
    }

    /*
     * Monitoring the touch event to move the tiles
     */
    private void setListener(){
        setOnTouchListener(new OnTouchListener() {
            private float staX,  staY, endX, endY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        staX = event.getX();
                        staY = event.getY();
                        break;

                    case MotionEvent.ACTION_UP:
                        endX = event.getX();
                        endY = event.getY();

                        boolean swiped = false;//record the event and see if it is correctly swiped

                        //move more horizontally
                        if(Math.abs(endX - staX) > Math.abs(endY - staY)){
                            if(endX - staX > 10){
                                if(swipeRight()){
                                    swiped = true;
                                }
                            }
                            else if(endX - staX < -10){
                                if(swipeLeft()){
                                    swiped = true;
                                }
                            }
                        }
                        else{
                            if(endY - staY < -10){
                                if(swipeUp()){
                                    swiped = true;
                                }
                            }
                            else if(endY - staY > 10){
                                if(swipeDown()){
                                    swiped = true;
                                }
                            }
                        }
                        //create new tile after swipe the tiles
                        if(swiped){
                            randomCreateCard(1);
                            if(!canSwipe()){
                                gameOver();
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    /*
     * return the result and see if the tile valid（any tile move or combined）
     */
    private boolean swipeUp(){
        boolean flag = false;
        for(int j = 0; j < 4; ++j){
            int ind = 0;
            //process it from up to down
            for(int i = 1; i < 4; ++i){
                //if there is a number, cal it up
                //there is only 4x4 tiles, so i should be always smaller than 4 if swipe and cal
                if(cards[i][j].getNum() != 0){
                    for(int ii = i - 1; ii >= ind; --ii){
                        //if the space is empty, move the number going up
                        if(cards[ii][j].getNum() == 0){
                            cards[ii][j].setNum(cards[i][j].getNum());
                            cards[i][j].setNum(0);
                            i--;//move up
                            flag = true;
                        }
                        //If two tiles contain same number, combine it
                        // combined tiles cannot combine twice at one action
                        // renew ind and do not cal the combined tiles
                        else if(cards[ii][j].getNum() == cards[i][j].getNum()){
                            cards[ii][j].setNum((cards[i][j].getNum() * 2));
                            cards[i][j].setNum(0);
                            flag = true;
                            ind = ii + 1;//if tile combined, no need to combine again
                            MainActivity.mainActivity.addScore(cards[ii][j].getNum() / 2);
                            //play the tile combine animation
                            playMergeAnimation(ii, j);
                            break;
                        }
                        //If the tile contain different number, break the loop
                        else break;
                    }
                }
            }
        }
        return flag;
    }

    private boolean swipeDown(){
        boolean flag = false;
        for(int j = 0; j < 4; ++j){
            int ind = 4;
            for(int i = 2; i >= 0; --i){
                if(cards[i][j].getNum() != 0){
                    for(int ii = i + 1; ii < ind; ++ii){
                        if(cards[ii][j].getNum() == 0){
                            cards[ii][j].setNum(cards[i][j].getNum());
                            cards[i][j].setNum(0);
                            flag = true;
                            i++;
                        }
                        else if(cards[ii][j].getNum() == cards[i][j].getNum()){
                            cards[ii][j].setNum((cards[i][j].getNum() * 2));
                            cards[i][j].setNum(0);
                            flag = true;
                            ind = ii;
                            MainActivity.mainActivity.addScore(cards[ii][j].getNum() / 2);
                            playMergeAnimation(ii, j);
                            break;
                        }
                        else break;
                    }
                }
            }
        }
        return flag;
    }

    private boolean swipeLeft(){
        boolean flag = false;
        for(int i = 0; i < 4; ++i){
            int ind = 0;
            for(int j = 1; j < 4; ++j){
                if(cards[i][j].getNum() != 0){
                    for(int jj = j - 1; jj >= ind; --jj){
                        if(cards[i][jj].getNum() == 0){
                            cards[i][jj].setNum(cards[i][j].getNum());
                            cards[i][j].setNum(0);
                            flag = true;
                            j--;
                        }
                        else if(cards[i][jj].getNum() == cards[i][j].getNum()){
                            cards[i][jj].setNum((cards[i][j].getNum() * 2));
                            cards[i][j].setNum(0);
                            flag = true;
                            ind = jj + 1;
                            MainActivity.mainActivity.addScore(cards[i][jj].getNum() / 2);
                            playMergeAnimation(i, jj);
                            break;
                        }
                        else break;
                    }
                }
            }
        }
        return flag;
    }

    private boolean swipeRight(){
        boolean flag = false;
        for(int i = 0; i < 4; ++i){
            int ind = 4;
            for(int j = 2; j >= 0; --j){
                if(cards[i][j].getNum() != 0){
                    for(int jj = j + 1; jj < ind; ++jj){
                        if(cards[i][jj].getNum() == 0){
                            cards[i][jj].setNum(cards[i][j].getNum());
                            cards[i][j].setNum(0);
                            flag = true;
                            j++;
                        }
                        else if(cards[i][jj].getNum() == cards[i][j].getNum()){
                            cards[i][jj].setNum((cards[i][j].getNum() * 2));
                            cards[i][j].setNum(0);
                            flag = true;
                            ind = jj;
                            MainActivity.mainActivity.addScore(cards[i][jj].getNum() / 2);
                            playMergeAnimation(i, jj);
                            break;
                        }
                        else break;
                    }
                }
            }
        }
        return flag;
    }

    /**
     *If the game board contain space, or tiles with the same number nearby
     * Then User able to keep swipe and continue the game
     */
    private boolean canSwipe(){
        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 4; ++j){
                if(cards[i][j].getNum() == 0){
                    return true;
                }
                else if(i != 3 && cards[i][j].getNum() == cards[i + 1][j].getNum()){
                    return true;
                }
                else if(j != 3 && cards[i][j].getNum() == cards[i][j + 1].getNum()){
                    return true;
                }
            }
        }
        return false;
    }

    private void addCards(int width, int height){
        Card c;
        for(int i = 0; i < 4; ++i){
            for(int j = 0; j < 4; ++j){
                c = new Card(getContext());
                addView(c, width, height);
                cards[i][j] = c;
            }
        }
    }

    private void gameOver(){
        Toast.makeText(getContext(), "Game Over, Nice Try!", Toast.LENGTH_SHORT).show();
    }

    private int GetCardWidth() {
        //Get the monitor information
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        //Makes the GameView Activity remain 90% of the monitor, and /4 to be the tiles width
        return (int)((displayMetrics.widthPixels * 0.9f) / 4);
    }

    /*
     * Random value for create the tile
     */
    private void randomCreateCard(int cnt){
        Random random = new Random();
        int r = random.nextInt(4);
        int c = random.nextInt(4);

        //If the space already contain a number, randomized r & c
        if(cards[r][c].getNum() != 0){
            randomCreateCard(cnt);
            return;
        }

        int rand = random.nextInt(10);

        if(rand >= 2) rand = 2;
        else rand = 4;

        cards[r][c].setNum(rand);

        //Play create Animation
        playCreateAnimation(r, c);

        if(cnt >= 2){
            randomCreateCard(cnt - 1);
        }
    }

    /*
     * Play the new created tile animation
     */
    private void playCreateAnimation(int r, int c){
        AnimationSet animationSet = new AnimationSet(true);

        //Rotate Animation
        RotateAnimation anim = new RotateAnimation(0,360,RotateAnimation.RELATIVE_TO_SELF,0.5f, RotateAnimation.RELATIVE_TO_SELF,0.5f);
        anim.setDuration(150);
        anim.setRepeatCount(0);
        anim.setInterpolator(new LinearInterpolator());

        //Zoom in-out animation
        ScaleAnimation anim2 = new ScaleAnimation(0,1,0,1,
                Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f
        );
        anim2.setDuration(250);
        anim2.setRepeatCount(0);

        animationSet.addAnimation(anim);
        animationSet.addAnimation(anim2);

        cards[r][c].startAnimation(animationSet);
    }

    /*
     * Play the combine tile animation
     */
    private void playMergeAnimation(int r, int c){
        ScaleAnimation anim = new ScaleAnimation(1,1.2f,1,1.2f,
                Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f
        );
        anim.setDuration(150);
        anim.setRepeatCount(0);

        anim.setRepeatMode(Animation.REVERSE);

        cards[r][c].startAnimation(anim);
    }
}

