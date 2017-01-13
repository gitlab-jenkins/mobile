package xyz.homapay.hampay.mobile.android.dialog.permission.camera;

import android.app.Activity;
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

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.permission.ActionPermission;

/**
 * Created by amir on 5/17/16.
 */
public class PermissionCameraDialog extends DialogFragment implements TextView.OnEditorActionListener, View.OnClickListener {

    public interface PermissionCameraDialogListener {
        void onFinishCameraDialog(ActionPermission actionPermission);
    }
    PermissionCameraDialogListener activity;
    private Rect rect = new Rect();

    @Override
    public void onClick(View v) {
        PermissionCameraDialogListener activity = (PermissionCameraDialogListener) getActivity();
        activity.onFinishCameraDialog(ActionPermission.DENY);
        this.dismiss();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_persmission, container);
        activity = (PermissionCameraDialogListener) getActivity();

        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        view.setMinimumWidth((int) (rect.width() * 0.85f));

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        FacedTextView permission_grant = (FacedTextView)view.findViewById(R.id.permission_grant);
        permission_grant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                activity.onFinishCameraDialog(ActionPermission.GRANT);
            }
        });

        FacedTextView permission_deny = (FacedTextView)view.findViewById(R.id.permission_deny);
        permission_deny.setOnClickListener(this);

        return view;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        PermissionCameraDialogListener activity = (PermissionCameraDialogListener) getActivity();
        activity.onFinishCameraDialog(ActionPermission.DENY);
        this.dismiss();
        return true;
    }
}
