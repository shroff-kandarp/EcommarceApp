package com.general.files;

import android.os.Handler;
import android.util.Log;

/**
 * Created by Admin on 06-07-2016.
 */
public class UpdateFrequentTask implements Runnable {
    int mInterval;
    private Handler mHandler_UpdateTask;

    OnTaskRunCalled onTaskRunCalled;

    boolean isKilled = false;

    public UpdateFrequentTask(int mInterval) {
        this.mInterval = mInterval;
        mHandler_UpdateTask = new Handler();
    }

    public void changeInterval(int mInterval) {
        this.mInterval = mInterval;
    }

    @Override
    public void run() {
        Log.d("updateFrequentTask", "Run");

        if (onTaskRunCalled != null) {
            onTaskRunCalled.onTaskRun();
        }
        if (isKilled == true) {
            return;
        }
        mHandler_UpdateTask.postDelayed(this, mInterval);
    }

    public void startRepeatingTask() {
        isKilled = true;
        this.run();
    }

    public void stopRepeatingTask() {
        Log.d("Stopp", "yaaaa");
        mHandler_UpdateTask.removeCallbacks(this);
        isKilled = true;
    }

    public interface OnTaskRunCalled {
        void onTaskRun();
    }

    public void setTaskRunListener(OnTaskRunCalled onTaskRunCalled) {
        this.onTaskRunCalled = onTaskRunCalled;
    }
}
