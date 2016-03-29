package xyz.homapay.hampay.mobile.android.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import xyz.homapay.hampay.mobile.android.R;

/**
 * Created by amir on 3/29/16.
 */
public class DateUtil {

    private NumberFormat timeFormat;
    private PersianEnglishDigit persianEnglishDigit;

    public DateUtil(){
        timeFormat = new DecimalFormat("00");
        persianEnglishDigit = new PersianEnglishDigit();
    }

    public String remainingTime(Date date1, Date date2){
        long diff = date1.getTime() - date2.getTime();
        String diffSeconds = timeFormat.format(diff / 1000 % 60);
        String diffMinutes = timeFormat.format(diff / (60 * 1000) % 60);
        String diffHours = timeFormat.format(diff / (60 * 60 * 1000) % 24);
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffDays == 0) {
             return  "زمان باقی مانده"
                    + "\n"
                    + persianEnglishDigit.E2P(diffHours + ":" + diffMinutes + ":" + diffSeconds)
                    + " " + "ساعت";
        }else {
             return  "زمان باقی مانده"
                    + "\n"
                    + persianEnglishDigit.E2P(diffHours + ":" + diffMinutes + ":" + diffSeconds)
                    + " " + "ساعت"
                    + "\n"
                    + persianEnglishDigit.E2P(diffDays + "")
                    + " " + "روز";
        }
    }

}
