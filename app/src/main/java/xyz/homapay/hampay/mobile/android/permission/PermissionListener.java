package xyz.homapay.hampay.mobile.android.permission;

/**
 * Created by amir on 5/3/16.
 */
public interface PermissionListener {
    boolean onResult(int requestCode, String[] requestPermissions, int[] grantResults);
}
