package com.hampay.mobile.android.async;

/**
 * Created by amir on 7/3/15.
 */
public interface AsyncTaskCompleteListener<T> {

    public void onTaskComplete(T result);
    public void onTaskPreRun();
}
