package xyz.homapay.hampay.mobile.android.util;

/**
 * Created by amir on 3/29/16.
 */
public class CurrencyFormatter {

    public CurrencyFormatter(){

    }

    public String format(Long value){
        return String.format("%,d", value).replace(".", ",");
    }

}
