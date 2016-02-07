package com.thepinkandroid.locationproject.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.thepinkandroid.locationproject.custom.RunnableToast;

/**
 * Created by DAVID-WORK on 07/02/2016.
 */
public class UpdateLocationIntentService extends IntentService
{
    private Handler mHandler;

    public UpdateLocationIntentService()
    {
        super(UpdateLocationIntentService.class.getSimpleName());
        mHandler = new Handler();
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UpdateLocationIntentService(String name)
    {
        super(name);
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.
        long endTime = System.currentTimeMillis() + 30 * 1000;
        while (System.currentTimeMillis() < endTime)
        {
            synchronized (this)
            {
                try
                {
                    Log.e("DAVID", "In the service!!!");
                    mHandler.post(new RunnableToast(this, "Toast: In the service!!!"));
                    wait(5000);
                }
                catch (Exception e)
                {
                }
            }
        }
    }
}
