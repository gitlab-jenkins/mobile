package xyz.homapay.hampay.mobile.android.dialog.sms;

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
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

/**
 * Created by amir on 5/29/16.
 */
public class SMSActivationDialog extends DialogFragment implements TextView.OnEditorActionListener, View.OnClickListener {


    String cellNumber = "";
    Bundle bundle;

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        SMSActivationDialogListener activity = (SMSActivationDialogListener) getActivity();
        activity.onFinishEditDialog(ActionSMS.CANCEL);
        this.dismiss();
        return true;
    }

    public interface SMSActivationDialogListener {
        void onFinishEditDialog(ActionSMS actionSMS);
    }

    SMSActivationDialogListener activity;
    private Rect rect = new Rect();

    @Override
    public void onClick(View v) {
        SMSActivationDialogListener activity = (SMSActivationDialogListener) getActivity();
        activity.onFinishEditDialog(ActionSMS.CANCEL);
        this.dismiss();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_sms_confirm, container);
        activity = (SMSActivationDialogListener) getActivity();

        bundle = this.getArguments();
        if (bundle != null) {
            cellNumber  = bundle.getString(Constants.REGISTERED_CELL_NUMBER);
        }

        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        view.setMinimumWidth((int) (rect.width() * 0.85f));

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        FacedTextView sms_user_notify = (FacedTextView) view.findViewById(R.id.sms_user_notify);
        sms_user_notify.setText(getActivity().getString(R.string.sms_verification_text, new PersianEnglishDigit().E2P(cellNumber)));
        FacedTextView confirmation = (FacedTextView) view.findViewById(R.id.confirmation);
        FacedTextView dis_confirmation = (FacedTextView) view.findViewById(R.id.dis_confirmation);

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                activity.onFinishEditDialog(ActionSMS.RESEND);
            }
        });

        dis_confirmation.setOnClickListener(this);


        return view;
    }

}
