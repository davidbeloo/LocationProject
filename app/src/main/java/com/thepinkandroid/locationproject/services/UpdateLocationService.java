package com.thepinkandroid.locationproject.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * Created by DAVID-WORK on 07/02/2016.
 */
public class UpdateLocationService extends Service
{
    public static final String LOG_TAG = UpdateLocationService.class.getSimpleName();

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // Do some work here
        final Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                int counter = 0;
                while (true)
                {
                    synchronized (this)
                    {
                        try
                        {
                            Log.e(LOG_TAG, "In the service!, counter = " + String.valueOf(counter));
                            counter++;
                            wait(TimeUnit.SECONDS.toMillis(3));
                        }
                        catch (Exception e)
                        {
                        }
                    }

                    if (counter > 5)
                    {
                        Log.e(LOG_TAG, "Service self stopped");
                        stopSelf();
                        break;
                    }
                }
            }
        });
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
