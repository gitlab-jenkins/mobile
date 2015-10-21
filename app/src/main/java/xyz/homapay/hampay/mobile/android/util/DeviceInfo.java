package xyz.homapay.hampay.mobile.android.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

/**
 * Created by amir on 6/22/15.
 */
public class DeviceInfo {

    Context context;

    TelephonyManager telephonyManager;

    public DeviceInfo(Context context){

        this.context = context;
        telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

    }

    public String getIMEI(){
        if (telephonyManager.getDeviceId() != null) {
            return telephonyManager.getDeviceId();
        }
        else {
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
        }else {
            return "";
        }
    }

    public String getNetworkOperator() {
        if (telephonyManager.getSubscriberId() != null) {
            return telephonyManager.getSubscriberId().substring(0, 5);
        }else {
            return "";
        }
    }

    public String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    public String getManufacture(){
        return Build.MANUFACTURER;
    }

    public String getBrand(){
        return Build.BRAND;
    }



    public String getCpu_abi(){
        return Build.CPU_ABI;
    }

    public String getDeviceAPI(){
        return Build.VERSION.SDK_INT + "";
    }

    public String getOsVersion(){
        return android.os.Build.VERSION.RELEASE + "";
    }

    public String getDisplaySize(){

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getWidth() + "," + display.getHeight();
    }

    public String getDisplaymetrics(){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return  displayMetrics.densityDpi + "";
    }

    public String getSimState(){

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

    public String getAppNames(){
        String allAppsName = "";

        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packs.size();i++) {
            PackageInfo p = packs.get(i);

            allAppsName += p.applicationInfo.loadLabel(context.getPackageManager()).toString() + ",";
        }

        return allAppsName;

    }

    public String getDownloadedAppNames(){
        String downloadedAppsName = "";

        PackageManager pm = context.getPackageManager();
        List<PackageInfo> list = pm.getInstalledPackages(0);

        for(PackageInfo pi : list) {
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


    private String userDeviceEmail = "";

    public String getDeviceEmailAccount(){
        AccountManager accManager = AccountManager.get(context);
        Account accounts[] = accManager.getAccounts();
        int accCount = accounts.length;
        for(int i = 0; i < accCount; i++){
            if (accounts[i].type.equalsIgnoreCase("com.google")){
                userDeviceEmail = accounts[i].name;
            }
        }

        if (userDeviceEmail.length() == 0){
            if (accounts.length >= 1){
                userDeviceEmail = accounts[0].name;
            }
        }

        return userDeviceEmail;
    }

    public String getNetworkOperatorName(){
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkOperatorName();
    }

    public String getLocale(){
        Locale locale = context.getResources().getConfiguration().locale;
        return locale.getLanguage();

    }

    public String getAndroidId(){
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public String getMacAddress(){
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
       return info.getMacAddress();
    }

    public String getNetworkIp(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());

                        return ip;
                    }
                }
            }
        } catch (SocketException ex) { }
        return "";
    }

}
