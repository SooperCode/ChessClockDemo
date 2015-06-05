package com.soopercode.chessclock;

import android.app.Activity;
import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final long TOTAL_TIME_MILLIS = 15*60*1000;
    private static final long INTERVAL_MILLIS = 1000;

    private TextView textUpper;
    private TextView textLower;

    private boolean upperCounting;
    //private CountDownTimer counterUpper;
    private CustomCountDown counterUpper;
    private long millisRemainingUpper = TOTAL_TIME_MILLIS;

    private boolean lowerCounting;
    //private CountDownTimer counterLower;
    private CustomCountDown counterLower;
    private long millisRemainingLower = TOTAL_TIME_MILLIS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textUpper = (TextView)findViewById(R.id.textview_upper_upsidedown);
        textLower = (TextView)findViewById(R.id.textview_lower);

        textUpper.setText(getTimeText(TOTAL_TIME_MILLIS));
        textLower.setText(getTimeText(TOTAL_TIME_MILLIS));

        textUpper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!upperCounting && !lowerCounting) {
                    startUpperCounter();
                } else if (!upperCounting && lowerCounting) {
                    // do nothing.
                } else {
                    stopUpperCounter();
                    startLowerCounter();
                }

            }
        });

        textLower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lowerCounting && !upperCounting) { //
                    startLowerCounter();

                } else if (!lowerCounting && upperCounting) {
                    // do nothing.
                } else {
                    stopLowerCounter();
                    startUpperCounter();
                }
            }
        });

        Button buttonReset = (Button)findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLowerCounter();
                stopUpperCounter();
                millisRemainingUpper = TOTAL_TIME_MILLIS;
                millisRemainingLower = TOTAL_TIME_MILLIS;
                textLower.setText(getTimeText(TOTAL_TIME_MILLIS));
                textUpper.setText(getTimeText(TOTAL_TIME_MILLIS));
            }
        });
    }
    /* ************* COUNTER CONTROLS ****************** */

    private void startUpperCounter(){
        upperCounting = true;
        counterUpper = new CustomCountDown(millisRemainingUpper, INTERVAL_MILLIS) {
            @Override
            public void onTick(long millisUntilFinished) {
                textUpper.setText(getTimeText(millisUntilFinished));
                millisRemainingUpper = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                textUpper.setText(getString(R.string.textview_loser));
                // reset everything.
                upperCounting = false;
                millisRemainingUpper = TOTAL_TIME_MILLIS;
                showLoserDialog(true);
            }
        }.start();
    }

    private void stopUpperCounter(){

        if(counterUpper != null){
            counterUpper.cancel();
            counterUpper = null;
        }
        upperCounting = false;
    }

    private void startLowerCounter(){
        lowerCounting = true;
        counterLower = new CustomCountDown(millisRemainingLower, INTERVAL_MILLIS) {
            @Override
            public void onTick(long millisUntilFinished) {
                textLower.setText(getTimeText(millisUntilFinished));
                millisRemainingLower = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                textLower.setText(getString(R.string.textview_loser));
                // reset everything.
                lowerCounting = false;
                millisRemainingLower = TOTAL_TIME_MILLIS;
                showLoserDialog(false);
            }
        }.start();
    }

    private void stopLowerCounter(){
        if(counterLower !=null){
            counterLower.cancel();
            counterLower = null;
        }
        lowerCounting = false;
    }

    private void showLoserDialog(boolean rotate180){
        Dialog dialog = new Dialog(this, R.style.DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loser);
        dialog.setCancelable(true);

        if(rotate180){
            ImageView img = (ImageView)dialog.findViewById(R.id.imageview_dialog_loser);
            img.setRotation(-180);
        }

        MediaPlayer player = MediaPlayer.create(this, R.raw.nelson_ha_ha);
        dialog.show();
        player.start();
    }


    private static String getTimeText(long millisUntilFinished){
        int minutes = (int)(millisUntilFinished/60_000);
        int seconds = (int) (millisUntilFinished/1000 - minutes*60);
        return String.format("%02d:%02d", minutes, seconds);
    }

}
