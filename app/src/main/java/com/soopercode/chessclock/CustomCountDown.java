package com.soopercode.chessclock;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/**
 * Android's CountDownTimer is not 100% accurate
 * (it adds a delay between ticks).
 * Found this solution & implemented it:
 * http://stackoverflow.com/a/12762416/4393807
 * (Copy the CountDownTimer source code & make some modifications)
 */
public abstract class CustomCountDown {

    /**
     * Millis since epoch when alarm should stop.
     */
    private final long mMillisInFuture;

    /**
     * The interval in millis that the user receives callbacks
     */
    private final long mCountdownInterval;

    private long mStopTimeInFuture;

    private long nextTime;


    /**
     * @param millisInFuture The number of millis in the future from the call
     *   to {@link #start()} until the countdown is done and {@link #onFinish()}
     *   is called.
     * @param countDownInterval The interval along the way to receive
     *   {@link #onTick(long)} callbacks.
     */
    public CustomCountDown(long millisInFuture, long countDownInterval) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
    }

    /**
     * Cancel the countdown.
     */
    public final void cancel() {
        mHandler.removeMessages(MSG);
    }

    /**
     * Start the countdown.
     */
    public synchronized final CustomCountDown start() {
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }
        nextTime = SystemClock.uptimeMillis();
        mStopTimeInFuture = nextTime + mMillisInFuture;

        nextTime += mCountdownInterval;
        mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG), nextTime);
        return this;
    }


    /**
     * Callback fired on regular interval.
     * @param millisUntilFinished The amount of time until finished.
     */
    public abstract void onTick(long millisUntilFinished);

    /**
     * Callback fired when the time is up.
     */
    public abstract void onFinish();


    private static final int MSG = 1;


    // handles counting down
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            synchronized (CustomCountDown.this) {
                final long millisLeft = mStopTimeInFuture - SystemClock.uptimeMillis();

                if (millisLeft <= 0) {
                    onFinish();
                } else {
                    onTick(millisLeft);

                    // Calculate next tick by adding the countdown interval from the
                    // original start time. If the user's onTick() took too long,
                    // skip the intervals that were already missed.
                    long currentTime = SystemClock.uptimeMillis();
                    do{
                        nextTime += mCountdownInterval;
                    } while (currentTime > nextTime);

                    // Make sure this interval doesn't exceed the stop time
                    if(nextTime < mStopTimeInFuture){
                        sendMessageAtTime(obtainMessage(MSG), nextTime);
                    }else{
                        sendMessageAtTime(obtainMessage(MSG), mStopTimeInFuture);
                    }
                }
            }
        }
    };
}
