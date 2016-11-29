package xyz.homapay.hampay.mobile.android.util;

import android.content.Context;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Created by amir on 8/1/15.
 */
public class RootUtil {

    Context context;

    public RootUtil(Context context){
        this.context = context;
    }


    public boolean checkRootedDevice(){
        if (firstApproach() || secondApproach() || thirdApproach() || forthApproach() || fifthApproach()){
            return true;
        }
        else {
            return false;
        }
    }

    private boolean firstApproach() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private boolean secondApproach() {
        String[] paths = {
                "/system/app/Superuser.apk",
                "/sbin/su",
                "/system/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su",
                "/system/bin/.ext/.su",
                "/su/bin/su"
        };

        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private boolean thirdApproach() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    private boolean forthApproach(){
        String path = "/etc/security";
        File f = new File(path);
        File file[] = f.listFiles();
        for (int i=0; i < file.length; i++)
        {
            if (file[i].getName().toLowerCase().contains("otacerts")) {
                return false;
            }
        }
        return true;
    }

    private boolean fifthApproach(){

        String[] bundle_ids = {
            "com.noshufou.android.su",
            "com.thirdparty.superuser",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser"
        };

        for (String bundle_id : bundle_ids){
            if (appInstalledOrNot(bundle_id)){

                return true;
            }
        }

        return false;

    }


    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
}
