package xyz.homapay.hampay.mobile.android.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;

import java.io.File;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.RemoveUserImageRequest;
import xyz.homapay.hampay.common.core.model.response.RemoveUserImageResponse;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.ChangeUserImageActivity;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestRemoveUserImage;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by amir on 1/4/16.
 */
public class UserEditPhotoDialog extends DialogFragment implements TextView.OnEditorActionListener, View.OnClickListener {


    Context context;
    UserEditPhotoDialogListener activity;

    @Override
    public void onClick(View v) {
        UserEditPhotoDialogListener activity = (UserEditPhotoDialogListener) getActivity();
        activity.onFinishEditDialog("Amir");
        this.dismiss();
    }

    public interface UserEditPhotoDialogListener {
        void onFinishEditDialog(String inputText);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_user_image_profile, container);

        context = getContext();

        activity = (UserEditPhotoDialogListener) getActivity();

        FacedTextView camera_choose = (FacedTextView)view.findViewById(R.id.camera_choose);
        camera_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent = new Intent();
                intent.setClass(getContext(), ChangeUserImageActivity.class);
                intent.putExtra(Constants.IMAGE_PROFILE_SOURCE, Constants.CAMERA_SELECT);
                startActivityForResult(intent, 5000);
            }
        });

        FacedTextView gallary_choose = (FacedTextView)view.findViewById(R.id.gallary_choose);
        gallary_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent = new Intent();
                intent.setClass(getContext(), ChangeUserImageActivity.class);
                intent.putExtra(Constants.IMAGE_PROFILE_SOURCE, Constants.CONTENT_SELECT);
                startActivityForResult(intent, 5000);
            }
        });


        FacedTextView remove_choose = (FacedTextView)view.findViewById(R.id.remove_choose);
        remove_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                RemoveUserImageRequest removeUserImageRequest = new RemoveUserImageRequest();
                RequestRemoveUserImage requestRemoveUserImage = new RequestRemoveUserImage(getContext(), new RequestRemovePhotoTaskCompleteListener(removeUserImageRequest));
                requestRemoveUserImage.execute(removeUserImageRequest);
            }
        });

        FacedTextView cancel_choose = (FacedTextView)view.findViewById(R.id.cancel_choose);
        cancel_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


//        mEditText = (EditText) view.findViewById(R.id.txt_your_name);
//        getDialog().setTitle("Hello");
//
//        // Show soft keyboard automatically
//        mEditText.requestFocus();
//        getDialog().getWindow().setSoftInputMode(
//                LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        cancel_choose.setOnClickListener(this);

        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {


//        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
            UserEditPhotoDialogListener activity = (UserEditPhotoDialogListener) getActivity();
            activity.onFinishEditDialog("Amir");
            this.dismiss();
            return true;
//        }
//        return false;
    }

    public class RequestRemovePhotoTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RemoveUserImageResponse>> {

        private RequestRemoveUserImage requestRemoveUserImage = null;
        private RemoveUserImageRequest removeUserImageRequest;

        public RequestRemovePhotoTaskCompleteListener(RemoveUserImageRequest removeUserImageRequest){
            this.removeUserImageRequest = removeUserImageRequest;

        }

        @Override
        public void onTaskComplete(ResponseMessage<RemoveUserImageResponse> removeUserImageResponseMessage)
        {


            if (removeUserImageResponseMessage != null) {
                if (removeUserImageResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {


//                    UserEditPhotoDialogListener activity = (UserEditPhotoDialogListener) getActivity();
                    activity.onFinishEditDialog("Amir");

//                    String filePath = getContext().getFilesDir().getPath().toString() + "/" + "userImage.jpeg";
//                    File file = new File(filePath);
//                    if (file.exists()) {
//                        file.delete();
//                    }

                }
                else {
                    requestRemoveUserImage = new RequestRemoveUserImage(getActivity(), new RequestRemovePhotoTaskCompleteListener(removeUserImageRequest));
//                    new HamPayDialog(getActivity()).showFailRemovePhoto(requestRemoveUserImage, removeUserImageRequest,
//                            removeUserImageResponseMessage.getService().getResultStatus().getCode(),
//                            removeUserImageResponseMessage.getService().getResultStatus().getDescription());


                }
            }else {
                RequestRemoveUserImage requestChangeEmail = new RequestRemoveUserImage(getActivity(), new RequestRemovePhotoTaskCompleteListener(removeUserImageRequest));
//                new HamPayDialog(getActivity()).showFailRemovePhoto(requestChangeEmail, removeUserImageRequest,
//                        Constants.LOCAL_ERROR_CODE,
//                        getActivity().getString(R.string.msg_gail_change_email));

            }
        }

        @Override
        public void onTaskPreRun() {
//            showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

}
