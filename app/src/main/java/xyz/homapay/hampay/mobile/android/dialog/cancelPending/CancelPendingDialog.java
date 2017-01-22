package xyz.homapay.hampay.mobile.android.dialog.cancelPending;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by amir on 5/29/16.
 */
public class CancelPendingDialog extends DialogFragment implements TextView.OnEditorActionListener, View.OnClickListener {


    String code = "";
    Bundle bundle;
    CancelPendingDialogListener activity;
    private Fragment frg;
    private Rect rect = new Rect();

    public static CancelPendingDialog newInstance(Fragment frg) {
        CancelPendingDialog fragment = new CancelPendingDialog();
        fragment.setFrg(frg);
        return fragment;
    }

    public Fragment getFrg() {
        return frg;
    }

    public void setFrg(Fragment frg) {
        this.frg = frg;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        CancelPendingDialogListener activity = (CancelPendingDialogListener) getFrg();
        activity.onFinishEditDialog(ActionPending.CANCEL);
        this.dismiss();
        return true;
    }

    @Override
    public void onClick(View v) {
        CancelPendingDialogListener activity = (CancelPendingDialogListener) getActivity();
        activity.onFinishEditDialog(ActionPending.CANCEL);
        this.dismiss();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_pending_cancel_confirm, container);
        activity = (CancelPendingDialogListener) getFrg();

        bundle = this.getArguments();
        if (bundle != null) {
            code = bundle.getString(Constants.PENDING_CODE);
        }

        Activity parent = getFrg().getActivity();
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        view.setMinimumWidth((int) (rect.width() * 0.85f));

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        FacedTextView message = (FacedTextView) view.findViewById(R.id.message);
        message.setText(getActivity().getString(R.string.msg_cancel_pending, code));
        FacedTextView cancel_pending_confirm = (FacedTextView) view.findViewById(R.id.cancel_pending_confirm);
        FacedTextView cancel_pending_cancel = (FacedTextView) view.findViewById(R.id.cancel_pending_cancel);

        cancel_pending_confirm.setOnClickListener(v -> {
            dismiss();
            activity.onFinishEditDialog(ActionPending.REMOVE);
        });
        cancel_pending_cancel.setOnClickListener(this);
        return view;
    }

    public interface CancelPendingDialogListener {
        void onFinishEditDialog(ActionPending actionPending);
    }

}
