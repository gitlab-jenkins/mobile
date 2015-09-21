package com.hampay.mobile.android.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by amir on 9/21/15.
 */
public class TimeConvert {

    long timeStamp;

    public TimeConvert(long timeStamp){
        this.timeStamp = timeStamp;
    }

    public String timeStampToTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        java.util.Date currenTimeZone = new java.util.Date(timeStamp);

        return sdf.format(currenTimeZone);
    }

}
