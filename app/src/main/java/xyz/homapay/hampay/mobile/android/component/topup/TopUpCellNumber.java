package xyz.homapay.hampay.mobile.android.component.topup;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import xyz.homapay.hampay.common.common.Operator;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSetOperator;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.TelephonyUtils;

/**
 * Created by mohammad on 1/10/17.
 */

public class TopUpCellNumber extends FacedTextView {

    private ImageView cellNumberIcon;

    public TopUpCellNumber(Context context) {
        super(context);
        init();
    }

    public TopUpCellNumber(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TopUpCellNumber(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String phoneNumber = "09" + editable.toString().trim();
                    phoneNumber = new PersianEnglishDigit().P2E(phoneNumber);
                    if (phoneNumber.length() == 11) {
                        cellNumberIcon.setVisibility(VISIBLE);
                        if (TelephonyUtils.isIranValidNumber(phoneNumber) && new TelephonyUtils().isPrePaid(phoneNumber)) {
                            Operator operator = new TelephonyUtils().getNumberOperator(phoneNumber);
                            MessageSetOperator messageSetOperator = new MessageSetOperator(operator);
                            EventBus.getDefault().post(messageSetOperator);
                        }
                    }
                    if (TelephonyUtils.isIranValidNumber(phoneNumber) && new TelephonyUtils().isPrePaid(phoneNumber)) {
                        cellNumberIcon.setImageResource(R.drawable.right_icon);
                    } else {
                        cellNumberIcon.setImageResource(R.drawable.false_icon);
                    }
                }
            });
        }
    }

    public void setCellNumberIcon(ImageView cellNumberIcon) {
        this.cellNumberIcon = cellNumberIcon;
    }
}
