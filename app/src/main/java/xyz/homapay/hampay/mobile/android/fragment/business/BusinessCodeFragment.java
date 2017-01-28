package xyz.homapay.hampay.mobile.android.fragment.business;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.PurchaseInfoRequest;
import xyz.homapay.hampay.common.core.model.response.PurchaseInfoResponse;
import xyz.homapay.hampay.common.core.model.response.dto.PspInfoDTO;
import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.ActivityBusiness;
import xyz.homapay.hampay.mobile.android.activity.HamPayLoginActivity;
import xyz.homapay.hampay.mobile.android.activity.RequestBusinessPayDetailActivity;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestPurchaseInfo;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.firebase.LogEvent;
import xyz.homapay.hampay.mobile.android.firebase.service.ServiceEvent;
import xyz.homapay.hampay.mobile.android.util.Constants;

/**
 * Created by mohammad on 1/22/17.
 */

public class BusinessCodeFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.keyboard)
    LinearLayout keyboard;
    String inputPurchaseCode = "";
    @BindView(R.id.input_digit_1)
    FacedTextView input_digit_1;
    @BindView(R.id.input_digit_2)
    FacedTextView input_digit_2;
    @BindView(R.id.input_digit_3)
    FacedTextView input_digit_3;
    @BindView(R.id.input_digit_4)
    FacedTextView input_digit_4;
    @BindView(R.id.input_digit_5)
    FacedTextView input_digit_5;
    @BindView(R.id.input_digit_6)
    FacedTextView input_digit_6;
    SharedPreferences prefs;
    HamPayDialog hamPayDialog;
    @BindView(R.id.letter_layout)
    LinearLayout letter_layout;
    @BindView(R.id.digit_layout)
    LinearLayout digit_layout;
    @BindView(R.id.displayKeyboard)
    LinearLayout displayKeyboard;
    @BindView(R.id.payment_button)
    ImageView payment_button;
    private View rootView;
    private PurchaseInfoDTO purchaseInfoDTO = null;
    private PspInfoDTO pspInfoDTO = null;
    private SharedPreferences.Editor editor;
    private RequestPurchaseInfo requestPurchaseInfo;
    private PurchaseInfoRequest purchaseInfoRequest;

    public static BusinessCodeFragment newInstance() {
        BusinessCodeFragment fragment = new BusinessCodeFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_business_code, container, false);

        ButterKnife.bind(this, rootView);

        displayKeyboard.setOnClickListener(this);
        payment_button.setOnClickListener(this);

        prefs = getActivity().getSharedPreferences(Constants.APP_PREFERENCE_NAME, getActivity().MODE_PRIVATE);
        editor = getActivity().getSharedPreferences(Constants.APP_PREFERENCE_NAME, getActivity().MODE_PRIVATE).edit();
        hamPayDialog = new HamPayDialog(getActivity());

        return rootView;
    }

    @OnClick({R.id.digit_1, R.id.digit_2, R.id.digit_3, R.id.digit_4, R.id.digit_5, R.id.digit_6, R.id.digit_7, R.id.digit_8, R.id.digit_9, R.id.digit_0, R.id.switch_letter,
            R.id.letter_q, R.id.letter_w, R.id.letter_e, R.id.letter_r, R.id.letter_t, R.id.letter_y, R.id.letter_u, R.id.letter_i, R.id.letter_o, R.id.letter_p, R.id.letter_a, R.id.letter_s, R.id.letter_d
            , R.id.letter_f, R.id.letter_g, R.id.letter_h, R.id.letter_j, R.id.letter_k, R.id.letter_l, R.id.letter_z, R.id.letter_x, R.id.letter_c, R.id.letter_v, R.id.letter_b, R.id.letter_n, R.id.letter_m, R.id.switch_digit,
            R.id.backspace, R.id.letter_backspace})
    public void pressKey(View view) {
        if (view.getTag().toString().equals("-")) {
            letter_layout.setVisibility(View.GONE);
            digit_layout.setVisibility(View.VISIBLE);
        } else if (view.getTag().toString().equals("+")) {
            letter_layout.setVisibility(View.VISIBLE);
            digit_layout.setVisibility(View.GONE);
        } else {
            inputDigit(view.getTag().toString());
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {

            case R.id.businesses_list:
                intent = new Intent();
                intent.setClass(getActivity(), ActivityBusiness.class);
                startActivity(intent);
                break;

            case R.id.payment_button:

                new Collapse(keyboard).animate();

                if (inputPurchaseCode.length() == 6) {
                    requestPurchaseInfo = new RequestPurchaseInfo(getActivity(), new RequestPurchaseInfoTaskCompleteListener());
                    purchaseInfoRequest = new PurchaseInfoRequest();
                    purchaseInfoRequest.setPurchaseCode(inputPurchaseCode);
                    requestPurchaseInfo.execute(purchaseInfoRequest);
                    input_digit_1.setText("");
                    input_digit_2.setText("");
                    input_digit_3.setText("");
                    input_digit_4.setText("");
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    inputPurchaseCode = "";
                } else {
                    Toast.makeText(getActivity(), getString(R.string.msg_incorrect_pending_payment_code), Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.displayKeyboard:
                if (keyboard.getVisibility() != View.VISIBLE)
                    new Expand(keyboard).animate();
                break;

            case R.id.keyboard_dismiss:
                if (keyboard.getVisibility() == View.VISIBLE)
                    new Collapse(keyboard).animate();
                break;
        }
    }

    private void forceLogout() {
        editor.remove(Constants.LOGIN_TOKEN_ID);
        editor.commit();
        Intent intent = new Intent();
        intent.setClass(getActivity(), HamPayLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (getActivity() != null) {
            getActivity().finish();
            startActivity(intent);
        }
    }

    private void inputDigit(String digit) {
        if (inputPurchaseCode.length() <= 5) {

            switch (inputPurchaseCode.length()) {
                case 0:
                    if (digit.equals("d")) {
                        input_digit_1.setText("");
                    } else {
                        input_digit_1.setText(digit);
                    }
                    input_digit_2.setText("");
                    input_digit_3.setText("");
                    input_digit_4.setText("");
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    break;

                case 1:
                    if (digit.equals("d")) {
                        input_digit_2.setText("");
                    } else {
                        input_digit_2.setText(digit);
                    }
                    input_digit_3.setText("");
                    input_digit_4.setText("");
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    break;
                case 2:
                    if (digit.equals("d")) {
                        input_digit_3.setText("");
                    } else {
                        input_digit_3.setText(digit);
                    }
                    input_digit_4.setText("");
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    break;
                case 3:
                    if (digit.equals("d")) {
                        input_digit_4.setText("");
                    } else {
                        input_digit_4.setText(digit);
                    }
                    input_digit_5.setText("");
                    input_digit_6.setText("");
                    break;
                case 4:
                    if (digit.equals("d")) {
                        input_digit_5.setText("");
                    } else {
                        input_digit_5.setText(digit);
                    }
                    input_digit_6.setText("");
                    break;
                case 5:
                    if (digit.equals("d")) {
                        input_digit_6.setText("");
                    } else {
                        input_digit_6.setText(digit);
                    }
                    break;
            }

        }

        if (digit.contains("d")) {
            if (inputPurchaseCode.length() > 0) {
                inputPurchaseCode = inputPurchaseCode.substring(0, inputPurchaseCode.length() - 1);
                if (inputPurchaseCode.length() == 5) {
                    input_digit_6.setText("");
                }
                if (inputPurchaseCode.length() == 4) {
                    input_digit_5.setText("");
                } else if (inputPurchaseCode.length() == 3) {
                    input_digit_4.setText("");
                } else if (inputPurchaseCode.length() == 2) {
                    input_digit_3.setText("");
                } else if (inputPurchaseCode.length() == 1) {
                    input_digit_2.setText("");
                } else if (inputPurchaseCode.length() == 0) {
                    input_digit_1.setText("");
                }
            }
        } else {
            if (inputPurchaseCode.length() <= 5) {
                inputPurchaseCode += digit;
            }
        }
    }


//    public void pressKey(View view) {
//        if (view.getTag().toString().equals("-")) {
//            letter_layout.setVisibility(View.GONE);
//            digit_layout.setVisibility(View.VISIBLE);
//        } else if (view.getTag().toString().equals("+")) {
//            letter_layout.setVisibility(View.VISIBLE);
//            digit_layout.setVisibility(View.GONE);
//        } else {
//            inputDigit(view.getTag().toString());
//        }
//    }

    public class RequestPurchaseInfoTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<PurchaseInfoResponse>> {

        @Override
        public void onTaskComplete(ResponseMessage<PurchaseInfoResponse> purchaseInfoResponseMessage) {

            hamPayDialog.dismisWaitingDialog();
            ServiceEvent serviceName;
            LogEvent logEvent = new LogEvent(getActivity());

            if (purchaseInfoResponseMessage != null) {
                if (purchaseInfoResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {
                    serviceName = ServiceEvent.PURCHASE_INFO_SUCCESS;
                    purchaseInfoDTO = purchaseInfoResponseMessage.getService().getPurchaseInfo();
                    pspInfoDTO = purchaseInfoResponseMessage.getService().getPurchaseInfo().getPspInfo();

                    if (purchaseInfoDTO != null) {
                        Intent intent = new Intent();
                        intent.putExtra(Constants.PURCHASE_INFO, purchaseInfoDTO);
                        intent.putExtra(Constants.PSP_INFO, pspInfoDTO);
                        intent.setClass(getActivity(), RequestBusinessPayDetailActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.msg_not_found_pending_payment_code), Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }

                } else if (purchaseInfoResponseMessage.getService().getResultStatus() == ResultStatus.AUTHENTICATION_FAILURE) {
                    serviceName = ServiceEvent.PURCHASE_INFO_FAILURE;
                    forceLogout();
                } else {
                    serviceName = ServiceEvent.PURCHASE_INFO_FAILURE;
                    requestPurchaseInfo = new RequestPurchaseInfo(getActivity(), new RequestPurchaseInfoTaskCompleteListener());
                    new HamPayDialog(getActivity()).showFailPurchaseInfoDialog(
                            purchaseInfoResponseMessage.getService().getResultStatus().getCode(),
                            purchaseInfoResponseMessage.getService().getResultStatus().getDescription());
                }
            } else {
                serviceName = ServiceEvent.PURCHASE_INFO_FAILURE;
                requestPurchaseInfo = new RequestPurchaseInfo(getActivity(), new RequestPurchaseInfoTaskCompleteListener());
                new HamPayDialog(getActivity()).showFailPurchaseInfoDialog(
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_fetch_latest_payment));
            }
            logEvent.log(serviceName);

        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

}
