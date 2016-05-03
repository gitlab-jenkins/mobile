package xyz.homapay.hampay.mobile.android.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

/**
 * Created by amir on 5/3/16.
 */
public class RequestPermissions {


    public RequestPermissions(){
    }

    public ArrayList<PermissionListener> request(Activity activity, int requestCode, String[] requestPermissions, PermissionListener permissionListener){
        int[] grantResults = new int[requestPermissions.length];
        ArrayList<PermissionListener> permissionListeners = new ArrayList<>();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> list_notGranted = new ArrayList<>();

            for (String requestPermission : requestPermissions) {
                int granted = ContextCompat.checkSelfPermission(activity, requestPermission);

                if (granted != PackageManager.PERMISSION_GRANTED)
                    list_notGranted.add(requestPermission);
            }

            if (list_notGranted.size() > 0) {
                permissionListeners.add(permissionListener);

                requestPermissions = list_notGranted.toArray(new String[list_notGranted.size()]);

                activity.requestPermissions(requestPermissions, requestCode);
            } else {
                for (int i = 0; i < grantResults.length; i++)
                    grantResults[i] = PackageManager.PERMISSION_GRANTED;

                if (permissionListener != null)
                    permissionListener.onResult(requestCode, requestPermissions, grantResults);
            }
        } else {
            for (int i = 0; i < grantResults.length; i++)
                grantResults[i] = PackageManager.PERMISSION_GRANTED;

            if (permissionListener != null)
                permissionListener.onResult(requestCode, requestPermissions, grantResults);
        }

        return permissionListeners;
    }

}
