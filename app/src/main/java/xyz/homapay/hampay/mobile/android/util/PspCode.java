package xyz.homapay.hampay.mobile.android.util;

import android.content.Context;

import java.util.HashMap;

import xyz.homapay.hampay.mobile.android.R;

/**
 * Created by amir on 8/15/16.
 */
public class PspCode {

    String[] pspCodeList;
    String[] pspDescriptionList;
    private HashMap<String, String> pspResult = new HashMap<>();

    public PspCode(Context context){
        pspCodeList = context.getResources().getStringArray(R.array.psp_code);
        pspDescriptionList = context.getResources().getStringArray(R.array.psp_description);
        int index = 0;
        for (String code: pspCodeList){
            pspResult.put(code, pspDescriptionList[index++]);
        }
    }

    public String getDescription(String pspCode){
        return pspResult.get(pspCode);
    }

}
