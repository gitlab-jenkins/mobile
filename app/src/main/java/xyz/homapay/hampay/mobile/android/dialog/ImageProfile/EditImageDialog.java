package xyz.homapay.hampay.mobile.android.dialog.ImageProfile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.RemoveUserImageRequest;
import xyz.homapay.hampay.common.core.model.response.RemoveUserImageResponse;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.ChangeUserImageActivity;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestRemoveUserImage;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayCustomDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by amir on 5/17/16.
 */
public class EditImageDialog  extends DialogFragment implements TextView.OnEditorActionListener, View.OnClickListener {

    public interface EditImageDialogListener {
        void onFinishEditDialog(ActionImage actionImage);
    }

    Context context;
    EditImageDialogListener activity;
    private Rect rect = new Rect();

    @Override
    public void onClick(View v) {
        EditImageDialogListener activity = (EditImageDialogListener) getActivity();
        activity.onFinishEditDialog(ActionImage.NOPE);
        this.dismiss();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_user_image_profile, container);
        context = getContext();
        activity = (EditImageDialogListener) getActivity();

        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        view.setMinimumWidth((int) (rect.width() * 0.85f));

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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
                RequestRemoveUserImage requestRemoveUserImage = new RequestRemoveUserImage(getContext(), new RequestRemovePhotoTaskCompleteListener());
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

        cancel_choose.setOnClickListener(this);


        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        EditImageDialogListener activity = (EditImageDialogListener) getActivity();
        activity.onFinishEditDialog(ActionImage.NOPE);
        this.dismiss();
        return true;
    }

    public class RequestRemovePhotoTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<RemoveUserImageResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<RemoveUserImageResponse> removeUserImageResponseMessage)
        {
            if (removeUserImageResponseMessage != null) {
                if (removeUserImageResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    activity.onFinishEditDialog(ActionImage.REMOVE);

                }
            }
        }

        @Override
        public void onTaskPreRun() {
//            showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

}
