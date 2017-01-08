package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.UploadImageRequest;
import xyz.homapay.hampay.common.core.model.response.UploadImageResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestUploadImage;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.cropper.CropImageView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;


public class ChangeUserImageActivity extends AppCompatActivity {


    Bitmap croppedImage;
    Bitmap resizedImage;
    UploadImageRequest uploadImageRequest;
    RequestUploadImage requestUploadImage;
    HamPayDialog hamPayDialog;
    Context context;
    Activity activity;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;
    private Bundle bundle;
    private String user_selected_source;
    private CropImageView cropImageView;
    private FacedTextView user_profile_image_cancel;
    private FacedTextView user_profile_image_select;
    private int mAspectRatioX = Constants.DEFAULT_ASPECT_RATIO_VALUES;
    private int mAspectRatioY = Constants.DEFAULT_ASPECT_RATIO_VALUES;

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                maxImageSize / realImage.getWidth(),
                maxImageSize / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    @Override
    protected void onPause() {
        super.onPause();
        HamPayApplication.setAppSate(AppState.Paused);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HamPayApplication.setAppSate(AppState.Stoped);

        if (requestUploadImage != null) {
            if (!requestUploadImage.isCancelled())
                requestUploadImage.cancel(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    protected void onSaveInstanceState(@SuppressWarnings("NullableProblems") Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(Constants.ASPECT_RATIO_X, mAspectRatioX);
        bundle.putInt(Constants.ASPECT_RATIO_Y, mAspectRatioY);
    }

    @Override
    protected void onRestoreInstanceState(@SuppressWarnings("NullableProblems") Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mAspectRatioX = bundle.getInt(Constants.ASPECT_RATIO_X);
        mAspectRatioY = bundle.getInt(Constants.ASPECT_RATIO_Y);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_image);


        context = this;
        activity = ChangeUserImageActivity.this;
        hamPayDialog = new HamPayDialog(activity);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();


        cropImageView = (CropImageView) findViewById(R.id.cropImageView);


        user_profile_image_cancel = (FacedTextView) findViewById(R.id.user_profile_image_cancel);
        user_profile_image_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        user_profile_image_select = (FacedTextView) findViewById(R.id.user_profile_image_select);
        user_profile_image_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putLong(Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
                editor.commit();
                try {
                    croppedImage = cropImageView.getCroppedImage();
                    resizedImage = scaleDown(croppedImage, 100, true);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    resizedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] resizedImageByteArray = stream.toByteArray();
                    if (resizedImageByteArray.length <= 1024 * 1024) {
                        ImageView croppedImageView = (ImageView) findViewById(R.id.croppedImageView);
                        cropImageView.setVisibility(View.INVISIBLE);
                        if (croppedImage != null) {
                            croppedImageView.setImageBitmap(croppedImage);
                        }
                        uploadImageRequest = new UploadImageRequest();
                        requestUploadImage = new RequestUploadImage(context, new RequestUploadImageTaskCompleteListener());
                        uploadImageRequest.setImage(resizedImageByteArray);
                        requestUploadImage.execute(uploadImageRequest);
                    } else {
                        Toast.makeText(context, getString(R.string.msg_violation_image_size), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("Error", "Error");
                }

            }
        });


        bundle = getIntent().getExtras();

        user_selected_source = bundle.getString(Constants.IMAGE_PROFILE_SOURCE);


        if (user_selected_source.equalsIgnoreCase(Constants.CAMERA_SELECT)) {
            startActivityForResult(getPickImageCameraChooserIntent(), 200);
        } else if (user_selected_source.equalsIgnoreCase(Constants.CONTENT_SELECT)) {
            startActivityForResult(getPickImageChooserIntent(), 200);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
//            cropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
            cropImageView.setFixedAspectRatio(true);
            Uri imageUri = getPickImageResultUri(data);
            cropImageView.setImageUri(imageUri);
        } else {
            finish();
        }
    }

    public Intent getPickImageChooserIntent() {

        // Determine Uri of ic_camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));


        return chooserIntent;
    }


    public Intent getPickImageCameraChooserIntent() {

//        List<Intent> yourIntentsList = new ArrayList<Intent>();
//
//        Intent camIntent = new Intent("android.media.action.IMAGE_CAPTURE");
//
//        PackageManager packageManager = getPackageManager();
//
//        List<ResolveInfo> listCam = packageManager.queryIntentActivities(camIntent, 0);
//        for (ResolveInfo res : listCam) {
//            final Intent finalIntent = new Intent(camIntent);
//            finalIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
//            yourIntentsList.add(finalIntent);
//        }
//
//        Intent mainIntent = yourIntentsList.get(yourIntentsList.size() - 1);
//        for (Intent intent : yourIntentsList) {
//            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
//                mainIntent = intent;
//                break;
//            }
//        }
//        yourIntentsList.remove(mainIntent);
//
//        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
//
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, yourIntentsList.toArray(new Parcelable[yourIntentsList.size()]));
//        return chooserIntent;
//
//        return yourIntentsList;

        Uri outputFileUri = getCaptureImageOutputUri();
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//                intent.putExtra("return-data", true);
            }
            allIntents.add(intent);
        }
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));
        return chooserIntent;
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }


    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    public void backActionBar(View view) {
        finish();
    }

    private void forceLogout() {
        editor.remove(Constants.LOGIN_TOKEN_ID);
        editor.commit();
        Intent intent = new Intent();
        intent.setClass(context, HamPayLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (activity != null) {
            finish();
            startActivity(intent);
        }
    }

    public class RequestUploadImageTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<UploadImageResponse>> {
        public RequestUploadImageTaskCompleteListener() {
        }

        @Override
        public void onTaskComplete(ResponseMessage<UploadImageResponse> uploadImageResponseMessage) {
            hamPayDialog.dismisWaitingDialog();
            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(context);
            if (uploadImageResponseMessage != null && uploadImageResponseMessage.getService().getResultStatus() != null) {

                if (uploadImageResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.UPLOAD_IMAGE_SUCCESS;
                    croppedImage.recycle();
                    cropImageView.setImageBitmap(null);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", 5000);
                    setResult(5000);
                    finish();

                } else if (uploadImageResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.UPLOAD_IMAGE_FAILURE;
                    forceLogout();
                } else {
                    serviceName = ServiceEvent.UPLOAD_IMAGE_FAILURE;
                    requestUploadImage = new RequestUploadImage(getApplicationContext(), new RequestUploadImageTaskCompleteListener());
                    new HamPayDialog(activity).showFailUploadImage(requestUploadImage, uploadImageRequest,
                            uploadImageResponseMessage.getService().getResultStatus().getCode(),
                            uploadImageResponseMessage.getService().getResultStatus().getDescription());
                    croppedImage.recycle();
                    finish();
                }
            } else {
                serviceName = ServiceEvent.UPLOAD_IMAGE_FAILURE;
                Toast.makeText(context, "این سرویس هنوز فعال نمی باشد", Toast.LENGTH_LONG).show();
                requestUploadImage = new RequestUploadImage(getApplicationContext(), new RequestUploadImageTaskCompleteListener());
                new HamPayDialog(activity).showFailUploadImage(requestUploadImage, uploadImageRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_upload_image));
                croppedImage.recycle();

                finish();
            }
            logEvent.log(serviceName);
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }
}
