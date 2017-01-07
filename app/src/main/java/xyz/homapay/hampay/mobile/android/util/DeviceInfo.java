package xyz.homapay.hampay.mobile.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import java.util.List;
import java.util.Locale;

/**
 * Created by amir on 6/22/15.
 */
public class DeviceInfo {

    Context context;

    TelephonyManager telephonyManager;

    public DeviceInfo(Context context) {

        this.context = context;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

    }

    public String getIMEI() {
        if (telephonyManager.getDeviceId() != null) {
            return telephonyManager.getDeviceId();
        } else {
            return "";
        }
    }

    public String getIMSI() {
        if (telephonyManager.getSubscriberId() != null) {
            return telephonyManager.getSubscriberId();
        } else {
            return "";
        }
    }

    public String getSimcardSerial() {
        if (telephonyManager.getSimSerialNumber() != null) {
            return telephonyManager.getSimSerialNumber();
        } else {
            return "";
        }
    }

    public String getNetworkOperator() {
        if (telephonyManager.getSubscriberId() != null) {
            if (telephonyManager.getSubscriberId().length() > 0) {
                return telephonyManager.getSubscriberId().substring(0, 5);
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    public String getManufacture() {
        return Build.MANUFACTURER;
    }

    public String getBrand() {
        return Build.BRAND;
    }


    public String getCpu_abi() {
//        return Build.CPU_ABI;
        return "";
    }

    public String getDeviceAPI() {
        return Build.VERSION.SDK_INT + "";
    }

    public String getOsVersion() {
        return android.os.Build.VERSION.RELEASE + "";
    }

    public String getDisplaySize() {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;
        return screenWidth + "," + screenHeight;
    }

    public String getDisplaymetrics() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.densityDpi + "";
    }

    public String getSimState() {

        String SIM_State = "";

        switch (telephonyManager.getSimState()) {
            case TelephonyManager.SIM_STATE_ABSENT:
                SIM_State = "SIM_STATE_ABSENT";
                // do something
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                SIM_State = "SIM_STATE_NETWORK_LOCKED";
                // do something
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                SIM_State = "SIM_STATE_PIN_REQUIRED";
                // do something
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                SIM_State = "SIM_STATE_PUK_REQUIRED";
                // do something
                break;
            case TelephonyManager.SIM_STATE_READY:
                SIM_State = "SIM_STATE_READY";
                // do something
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                SIM_State = "SIM_STATE_UNKNOWN";
                // do something
                break;
        }

        return SIM_State;
    }

    public String getAppNames() {
        String allAppsName = "";

        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);

            allAppsName += p.applicationInfo.loadLabel(context.getPackageManager()).toString() + ",";
        }

        return allAppsName;

    }

    public String getDownloadedAppNames() {
        String downloadedAppsName = "";

        PackageManager pm = context.getPackageManager();
        List<PackageInfo> list = pm.getInstalledPackages(0);

        for (PackageInfo pi : list) {
            ApplicationInfo ai = null;
            try {
                ai = pm.getApplicationInfo(pi.packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                downloadedAppsName += pi.applicationInfo.loadLabel(context.getPackageManager()).toString() + ",";
            }
        }

        return downloadedAppsName;

    }


    public String getNetworkOperatorName() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkOperatorName();
    }

    public String getLocale() {
        Locale locale = context.getResources().getConfiguration().locale;
        return locale.getLanguage();

    }

    public String getAndroidId() {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public String getMacAddress() {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
    }

}
