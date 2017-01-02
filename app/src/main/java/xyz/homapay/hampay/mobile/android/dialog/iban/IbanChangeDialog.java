package xyz.homapay.hampay.mobile.android.dialog.iban;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
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
 * Created by amir on 9/13/16.
 */
public class IbanChangeDialog extends DialogFragment implements TextView.OnEditorActionListener, View.OnClickListener {

    private Bundle bundle;
    private String ibanNumber;
    private String ibanOwnerName;
    private String ibanOwnerFamily;
    private String ibanBankName;
    private PersianEnglishDigit persian = new PersianEnglishDigit();

    public interface IbanChangeDialogListener {
        void onFinishEditDialog(IbanAction ibanAction);
    }

    IbanChangeDialogListener activity;
    private Rect rect = new Rect();


    @Override
    public void onClick(View view) {
        IbanChangeDialogListener activity = (IbanChangeDialogListener) getActivity();
        activity.onFinishEditDialog(IbanAction.REJECT);
        this.dismiss();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        IbanChangeDialogListener activity = (IbanChangeDialogListener) getActivity();
        activity.onFinishEditDialog(IbanAction.REJECT);
        this.dismiss();
        return true;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        bundle = getArguments();

        ibanNumber = bundle.getString(Constants.IBAN_NUMBER);
        ibanOwnerName = bundle.getString(Constants.IBAN_OWNER_NAME);
        ibanOwnerFamily = bundle.getString(Constants.IBAN_OWNER_FAMILY);
        ibanBankName = bundle.getString(Constants.IBAN_BANK_NAME);

        View view = inflater.inflate(R.layout.dialog_request_iban_confirm, container);


        FacedTextView ibanText = (FacedTextView)view.findViewById(R.id.ibanNumber);
        ibanText.setText("IR " + persian.E2P(ibanNumber));

        FacedTextView ibanOwnInfo = (FacedTextView)view.findViewById(R.id.ibanOwnInfo);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getString(R.string.iban_question_part2_text, ibanOwnerName + " " + ibanOwnerFamily, ibanBankName));
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.rgb(Constants.HAMPAY_RED, Constants.HAMPAY_GREEN, Constants.HAMPAY_BLUE));
        spannableStringBuilder.setSpan(foregroundColorSpan, 9, (ibanOwnerName + " " + ibanOwnerFamily).length() + 9, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        foregroundColorSpan = new ForegroundColorSpan(Color.rgb(Constants.HAMPAY_RED, Constants.HAMPAY_GREEN, Constants.HAMPAY_BLUE));
        spannableStringBuilder.setSpan(foregroundColorSpan, (ibanOwnerName + " " + ibanOwnerFamily).length() + 14, (ibanOwnerName + " " + ibanOwnerFamily + ibanBankName).length() + 15, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        ibanOwnInfo.setText(spannableStringBuilder);

        FacedTextView ibanRequestConfirm = (FacedTextView) view.findViewById(R.id.iban_request_confirm);
        ibanRequestConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onFinishEditDialog(IbanAction.ACCEPT);
                dismiss();
            }
        });
        FacedTextView cancelRequest = (FacedTextView) view.findViewById(R.id.cancel_request);
        cancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onFinishEditDialog(IbanAction.REJECT);
                dismiss();
            }
        });

        activity = (IbanChangeDialogListener) getActivity();

        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        view.setMinimumWidth((int) (rect.width() * 0.85f));

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return view;
    }
}
