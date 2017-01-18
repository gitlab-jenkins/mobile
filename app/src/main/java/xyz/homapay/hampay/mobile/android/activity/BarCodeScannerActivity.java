package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Manifest;
import xyz.homapay.hampay.mobile.android.dialog.permission.camera.PermissionCameraDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.permission.PermissionListener;
import xyz.homapay.hampay.mobile.android.permission.RequestPermissions;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class BarCodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private SharedPreferences prefs;
    private Context context;
    private ZXingScannerView mScannerView;
    private ArrayList<PermissionListener> permissionListeners = new ArrayList<>();
    private Activity activity;
    private final Handler handler = new Handler();

    public void backActionBar(View view) {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        HamPayApplication.setAppSate(AppState.Paused);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HamPayApplication.setAppSate(AppState.Stoped);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        HamPayApplication.setAppSate(AppState.Resumed);
        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(context, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if ((System.currentTimeMillis() - prefs.getLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis()) > Constants.MOBILE_TIME_OUT_INTERVAL)) {
            Intent intent = new Intent();
            intent.setClass(context, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = BarCodeScannerActivity.this;

        context = this;
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);

        mScannerView = new ZXingScannerView(activity);
        List<BarcodeFormat> barcodeFormatList = new ArrayList<>();
        barcodeFormatList.add(BarcodeFormat.CODE_128);
        mScannerView.setFormats(barcodeFormatList);
        setContentView(mScannerView);

        requestCamera();

    }

    @Override
    public void handleResult(Result rawResult) {
        mScannerView.resumeCameraPreview(this);
        Bundle scanResult = new Bundle();
        scanResult.putString(Constants.BAR_CODE_SCAN_RESULT, rawResult.getText());
        Intent intent = new Intent();
        intent.putExtras(scanResult);
        setResult(RESULT_OK, intent);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        for (PermissionListener permissionListener : permissionListeners)
            if (permissionListener.onResult(requestCode, permissions, grantResults)) {
                permissionListeners.remove(permissionListener);
            }
    }

    private void requestCamera() {
        String[] permissions = new String[]{Manifest.permission.CAMERA};

        permissionListeners = new RequestPermissions().request(activity, Constants.CAMERA, permissions, new PermissionListener() {
            @Override
            public boolean onResult(int requestCode, String[] requestPermissions, int[] grantResults) {
                if (requestCode == Constants.CAMERA) {
                    if (requestPermissions[0].equals(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE);
                            if (showRationale) {
                                handler.post(() -> {
                                    PermissionCameraDialog permissionCameraDialog = new PermissionCameraDialog();
                                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.add(permissionCameraDialog, null);
                                    fragmentTransaction.commitAllowingStateLoss();
                                });
                            } else {
                                finish();
                            }
                        } else {
                            handler.post(() -> {
                                PermissionCameraDialog permissionCameraDialog = new PermissionCameraDialog();
                                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.add(permissionCameraDialog, null);
                                fragmentTransaction.commitAllowingStateLoss();
                            });
                        }
                    }
                    return true;
                }

                return false;
            }
        });
    }
}
