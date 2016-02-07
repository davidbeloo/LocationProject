package com.thepinkandroid.locationproject.custom;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by DAVID-WORK on 07/02/2016.
 */
public class RunnableToast implements Runnable
{
    private Context mContext;
    private String mText;

    public RunnableToast(Context context, String text)
    {
        mContext = context;
        mText = text;
    }

    @Override
    public void run()
    {
        Toast.makeText(mContext, mText, Toast.LENGTH_SHORT).show();
    }
}
