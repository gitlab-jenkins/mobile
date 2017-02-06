package xyz.homapay.hampay.mobile.android.fragment.billtopup;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.homapay.hampay.common.common.ChargePackage;
import xyz.homapay.hampay.common.common.Operator;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.TopUpInfoResponse;
import xyz.homapay.hampay.common.core.model.response.TopUpResponse;
import xyz.homapay.hampay.common.core.model.response.dto.UserProfileDTO;
import xyz.homapay.hampay.mobile.android.Manifest;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.ActivityBillsTopUp;
import xyz.homapay.hampay.mobile.android.activity.ServiceTopUpDetailActivity;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.common.charge.ChargeAdapterModel;
import xyz.homapay.hampay.mobile.android.common.charge.ChargeType;
import xyz.homapay.hampay.mobile.android.common.messages.MessageKeyboardStateChanged;
import xyz.homapay.hampay.mobile.android.common.messages.MessageOnBackPressed;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSelectChargeAmount;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSelectChargeType;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSetOperator;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.topup.TopUpCellNumber;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.dialog.permission.ActionPermission;
import xyz.homapay.hampay.mobile.android.dialog.permission.PermissionContactDialog;
import xyz.homapay.hampay.mobile.android.dialog.permission.PermissionDeviceDialog;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpCreate;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpCreateImpl;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpCreateView;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpInfo;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpInfoImpl;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpInfoView;
import xyz.homapay.hampay.mobile.android.permission.RequestPermissions;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.ModelLayerImpl;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.TelephonyUtils;

/**
 * Created by mohammad on 1/23/2017 AD.
 */

public class FrgTopUp extends Fragment implements View.OnClickListener, TopUpInfoView, TopUpCreateView, PermissionContactDialog.PermissionContactDialogListener {

    private static int selectedType = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());
    @BindView(R.id.keyboard)
    LinearLayout keyboard;
    @BindView(R.id.cellNumberText)
    TopUpCellNumber cellNumberText;
    @BindView(R.id.tvChargeType)
    FacedTextView tvChargeType;
    @BindView(R.id.tvChargeAmount)
    FacedTextView tvChargeAmount;
    @BindView(R.id.btnTopUpPay)
    FacedTextView btnTopUpPay;
    @BindView(R.id.imgMCI)
    ImageView imgMCI;
    @BindView(R.id.imgMTN)
    ImageView imgMTN;
    @BindView(R.id.imgRIGHTEL)
    ImageView imgRIGHTEL;
    @BindView(R.id.cellNumberIcon)
    ImageView cellNumberIcon;
    @BindView(R.id.rlChargeType)
    RelativeLayout rlChargeType;
    @BindView(R.id.rlChargeAmount)
    RelativeLayout rlChargeAmount;
    @BindView(R.id.imgUserSimNumber)
    ImageView imgUserSimNumber;
    @BindView(R.id.imgContacts)
    ImageView imgContacts;
    private View rootView;
    private TopUpInfo topUpInfo;
    private TopUpCreate topUpCreate;
    private PersianEnglishDigit persian;
    private Operator operator;
    private List<xyz.homapay.hampay.common.common.TopUpInfo> infos;
    private long amount;
    private String chargeType;
    private String operatorName;
    private HamPayDialog dlg;
    private UserProfileDTO userProfile;
    private List<xyz.homapay.hampay.common.common.TopUpInfo> MCI_INFO;
    private List<xyz.homapay.hampay.common.common.TopUpInfo> MTN_INFO;
    private List<xyz.homapay.hampay.common.common.TopUpInfo> RIGHTEL_INFO;
    private String cellNumber = "";

    public static FrgTopUp newInstance() {
        FrgTopUp fragment = new FrgTopUp();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topUpInfo = new TopUpInfoImpl(new ModelLayerImpl(getActivity()), this);
        topUpCreate = new TopUpCreateImpl(new ModelLayerImpl(getActivity()), this);
        persian = new PersianEnglishDigit();
        userProfile = (UserProfileDTO) getActivity().getIntent().getSerializableExtra(Constants.USER_PROFILE);

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.frg_top_up, null);
        ButterKnife.bind(this, rootView);
        tvChargeAmount.setTag(0);
        tvChargeType.setTag(0);
        cellNumberText.setCellNumberIcon(cellNumberIcon);
        cellNumberText.setOnClickListener(this);
        keyboard.setOnClickListener(this);
        imgMCI.setOnClickListener(this);
        imgMTN.setOnClickListener(this);
        imgRIGHTEL.setOnClickListener(this);
        rlChargeType.setOnClickListener(this);
        rlChargeAmount.setOnClickListener(this);
        btnTopUpPay.setOnClickListener(this);
        imgUserSimNumber.setOnClickListener(this);
        imgContacts.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dlg = new HamPayDialog(getActivity());
    }

    @Override
    public void showProgress() {
        dlg.showWaitingDialog("");
    }

    @Override
    public void cancelProgress() {
        dlg.dismisWaitingDialog();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cellNumberText:
                new Expand(keyboard).animate();
                EventBus.getDefault().post(new MessageKeyboardStateChanged(true));
                break;
            case R.id.imgMCI:
                if (operator == Operator.MCI)
                    return;
                operator = new MessageSetOperator(Operator.MCI).getOperator();
                operatorName = new MessageSetOperator(Operator.MCI).getOperatorName();
                selectOperatorView(true);
                break;
            case R.id.imgMTN:
                if (operator == Operator.MTN)
                    return;
                operator = new MessageSetOperator(Operator.MTN).getOperator();
                operatorName = new MessageSetOperator(Operator.MTN).getOperatorName();
                selectOperatorView(true);
                break;
            case R.id.imgRIGHTEL:
                if (operator == Operator.RAYTEL)
                    return;
                operator = new MessageSetOperator(Operator.RAYTEL).getOperator();
                operatorName = new MessageSetOperator(Operator.RAYTEL).getOperatorName();
                selectOperatorView(true);
                break;
            case R.id.rlChargeType:
                try {
                    if (infos != null) {
                        ArrayList<ChargeAdapterModel> itemsAdapter = new ArrayList<>();
                        for (int i = 0; i < infos.size(); i++) {
                            ChargeAdapterModel model = new ChargeAdapterModel(i + 1, infos.get(i).getChargeType(), infos.get(i).getDescription());
                            itemsAdapter.add(model);
                        }
                        dlg.showChargeTypeChooser(getActivity(), itemsAdapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.rlChargeAmount:
                try {
                    if (infos != null) {
                        List<String> items = new ArrayList<>();
                        for (ChargePackage item : infos.get(selectedType).getChargePackages()) {
                            items.add(item.getAmount() + "");
                        }
                        dlg.showChargeAmountChooser(getActivity(), items);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnTopUpPay:
                createPayment();
                break;
            case R.id.imgUserSimNumber:
                if (userProfile != null) {
                    showNumber();
                }
                break;
            case R.id.imgContacts:
                startActivityForGetContactsNumbers();
                break;
        }
    }

    private void showNumber() {
        this.cellNumber = TelephonyUtils.fixPhoneNumber(getActivity(), userProfile.getCellNumber()).substring(2);
        cellNumberText.setText(new PersianEnglishDigit().E2P(this.cellNumber));
    }

    private void showNumber(String cellNumber) {
        this.cellNumber = TelephonyUtils.fixPhoneNumber(getActivity(), cellNumber).substring(2);
        cellNumberText.setText(new PersianEnglishDigit().E2P(this.cellNumber));
    }

    private void startActivityForGetContactsNumbers() {
        try {
            requestAndLoadUserContact();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPayment() {
        if (!isNumberOk()) {
            Toast.makeText(getActivity(), R.string.err_cell_phone_invalid, Toast.LENGTH_SHORT).show();
            return;
        }
        if (chargeType != null && amount != 0) {
            ChargePackage chargePackage = null;
            for (xyz.homapay.hampay.common.common.TopUpInfo item : infos) {
                if (item.getChargeType().equals(chargeType)) {
                    for (ChargePackage item2 : item.getChargePackages()) {
                        if (item2.getAmount() == amount)
                            chargePackage = item2;
                    }
                }
            }
            if (chargePackage != null) {
                PersianEnglishDigit ped = new PersianEnglishDigit();
                String str = ped.P2E(cellNumberText.getText().toString());
                str = "09" + str;
                btnTopUpPay.setEnabled(false);
                topUpCreate.create(operator, str, chargePackage, chargeType);
            }
        }
    }

    private boolean isNumberOk() {
        String phoneNumber = "09" + cellNumberText.getText().toString().trim().replace(" ", "");
        phoneNumber = new PersianEnglishDigit().P2E(phoneNumber);
        if (phoneNumber.length() == 11) {
            return TelephonyUtils.isIranValidNumber(phoneNumber) && new TelephonyUtils().isPrePaid(phoneNumber);
        } else {
            return false;
        }
    }

    @Override
    public void onError() {
        dlg.dismisWaitingDialog();
        btnTopUpPay.setEnabled(true);
        Toast.makeText(getActivity(), getString(R.string.err_general_text), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreated(boolean state, ResponseMessage<TopUpResponse> data, String message) {
        btnTopUpPay.setEnabled(true);
        if (state) {
            Intent intent = new Intent(getActivity(), ServiceTopUpDetailActivity.class);
            data.getService().getTopUpInfoDTO().setImageId(operatorName);
            intent.putExtra(Constants.TOP_UP_INFO, data.getService().getTopUpInfoDTO());
            intent.putExtra(Constants.CHARGE_TYPE, ChargeType.DIRECT.ordinal());
            startActivityForResult(intent, 48);
        } else {
            onError();
        }
    }

    @Subscribe
    public void onBackPressed(MessageOnBackPressed messageOnBackPressed) {
        new Collapse(keyboard).animate();
        EventBus.getDefault().post(new MessageKeyboardStateChanged(false));
    }

    @Override
    public void onInfoLoaded(boolean state, ResponseMessage<TopUpInfoResponse> data, String message) {
        if (state) {
            try {
                if (operator.equals(Operator.MCI))
                    MCI_INFO = data.getService().getTopUpInfoList();
                else if (operator.equals(Operator.MTN))
                    MTN_INFO = data.getService().getTopUpInfoList();
                else if (operator.equals(Operator.RAYTEL))
                    RIGHTEL_INFO = data.getService().getTopUpInfoList();

                infos = data.getService().getTopUpInfoList();
                this.chargeType = infos.get(0).getChargeType();
                this.amount = infos.get(0).getChargePackages().get(0).getAmount();
                tvChargeAmount.setText(AppManager.amountFixer(infos.get(0).getChargePackages().get(0).getAmount()) + " ریال");
                tvChargeType.setText(infos.get(0).getDescription());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            onError();
    }

    public void onInfoLoaded(List<xyz.homapay.hampay.common.common.TopUpInfo> data) {
        try {
            infos = data;
            this.chargeType = infos.get(0).getChargeType();
            this.amount = infos.get(0).getChargePackages().get(0).getAmount();
            tvChargeAmount.setText(AppManager.amountFixer(infos.get(0).getChargePackages().get(0).getAmount()) + " ریال");
            tvChargeType.setText(infos.get(0).getDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onTypeSelect(MessageSelectChargeType type) {
        this.chargeType = type.getSelectedType();
        tvChargeType.setText(type.getSelectedDescrption());
        selectedType = type.getIndex();
        changeAmountData();
    }

    private void changeAmountData() {
        try {
            if (infos != null) {
                this.amount = infos.get(selectedType).getChargePackages().get(0).getAmount();
                String amountText = AppManager.amountFixer(this.amount) + " ریال";
                tvChargeAmount.setText(amountText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onAmountSelect(MessageSelectChargeAmount amount) {
        this.amount = Long.parseLong(amount.getAmount());
        tvChargeAmount.setText(AppManager.amountFixer(this.amount) + " ریال");
    }

    @Override
    public void onFinishEditDialog(ActionPermission actionPermission) {
        switch (actionPermission) {
            case GRANT:
                requestAndLoadUserContact();
                break;
            case DENY:
                break;
        }
    }

    @OnClick({R.id.digit_0, R.id.digit_1, R.id.digit_2,
            R.id.digit_3, R.id.digit_4, R.id.digit_5,
            R.id.digit_6, R.id.digit_7, R.id.digit_8,
            R.id.digit_9, R.id.keyboard_dismiss, R.id.backspace})
    public void pressKey(View view) {
        if (view.getTag().toString().equals("*")) {
            new Collapse(keyboard).animate();
            EventBus.getDefault().post(new MessageKeyboardStateChanged(false));
        } else if (view.getTag().toString().equals("|")) {
            new Expand(keyboard).animate();
            EventBus.getDefault().post(new MessageKeyboardStateChanged(true));
        } else {
            inputDigit(view.getTag().toString());
        }
    }

    private void inputDigit(String digit) {
        if (digit.endsWith("d")) {
        } else {
            if (cellNumber.length() > 8) return;
            cellNumber += digit;
        }

        if (digit.endsWith("d")) {
            if (cellNumber.length() == 0) return;
            cellNumber = cellNumber.substring(0, cellNumber.length() - 1);
            cellNumberText.setText(persian.E2P(cellNumber));

        } else {
            cellNumberText.setText(persian.E2P(cellNumber));
        }
    }

    @Subscribe
    public void onOperatorChanged(MessageSetOperator messageSetOperator) {
        if (messageSetOperator.getOperator() == null)
            return;
        if (messageSetOperator.getOperator() == operator)
            return;
        this.operator = messageSetOperator.getOperator();
        this.operatorName = messageSetOperator.getOperatorName();
        new Collapse(keyboard).animate();
        EventBus.getDefault().post(new MessageKeyboardStateChanged(false));
        selectOperatorView(false);
        changeInfoFromNetwork();
    }

    private void changeInfoFromNetwork() {
        if (operator.equals(Operator.MCI) && MCI_INFO != null) {
            onInfoLoaded(MCI_INFO);
        } else if (operator.equals(Operator.MTN) && MTN_INFO != null) {
            onInfoLoaded(MTN_INFO);
        } else if (operator.equals(Operator.RAYTEL) && RIGHTEL_INFO != null) {
            onInfoLoaded(RIGHTEL_INFO);
        } else
            topUpInfo.getInfo(operator);
    }

    private void selectOperatorView(boolean manual) {
        if (manual)
            changeInfoFromNetwork();
        switch (operator) {
            case MCI:
                imgMCI.setImageResource(R.drawable.hamrah_active);
                imgMTN.setImageResource(R.drawable.irancell_inactive);
                imgRIGHTEL.setImageResource(R.drawable.rightel_inactive);
                break;
            case MTN:
                imgMCI.setImageResource(R.drawable.hamrah_inactive);
                imgMTN.setImageResource(R.drawable.irancell_active);
                imgRIGHTEL.setImageResource(R.drawable.rightel_inactive);
                break;
            case RAYTEL:
                imgMCI.setImageResource(R.drawable.hamrah_inactive);
                imgMTN.setImageResource(R.drawable.irancell_inactive);
                imgRIGHTEL.setImageResource(R.drawable.rightel_active);
                break;
            default:
                imgMCI.setImageResource(R.drawable.hamrah_inactive);
                imgMTN.setImageResource(R.drawable.irancell_inactive);
                imgRIGHTEL.setImageResource(R.drawable.rightel_inactive);
                break;
        }
    }

    private void requestAndLoadUserContact() {
        String[] permissions = new String[]{Manifest.permission.READ_CONTACTS};

        ActivityBillsTopUp.permissionListeners = new RequestPermissions().request(getActivity(), Constants.READ_CONTACTS, permissions, (requestCode, requestPermissions, grantResults) -> {
            if (requestCode == Constants.READ_CONTACTS) {
                if (grantResults.length > 0 && requestPermissions[0].equals(Manifest.permission.READ_CONTACTS) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, 1);
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
                        if (showRationale) {
                            handler.post(() -> {
                                PermissionContactDialog permissionContactDialog = new PermissionContactDialog();
                                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.add(permissionContactDialog, null);
                                fragmentTransaction.commitAllowingStateLoss();
                            });
                        } else {

                        }
                    } else {
                        handler.post(() -> {
                            PermissionDeviceDialog permissionDeviceDialog = new PermissionDeviceDialog();
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.add(permissionDeviceDialog, null);
                            fragmentTransaction.commitAllowingStateLoss();
                        });
                    }
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (1):
                cellNumber = "";
                cellNumberText.setText("");
                if (resultCode == Activity.RESULT_OK) {
                    Cursor cursor = getActivity().managedQuery(data.getData(), null, null, null, null);
                    while (cursor.moveToNext()) {
                        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (hasPhone.equalsIgnoreCase("1"))
                            hasPhone = "true";
                        else
                            hasPhone = "false";

                        if (Boolean.parseBoolean(hasPhone)) {
                            Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                            while (phones.moveToNext()) {
                                cellNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                showNumber(cellNumber);
                            }
                        }
                    }
                }
                break;
        }

        switch (requestCode) {
            case 48:
                if (resultCode == Activity.RESULT_OK) {
                    cellNumberText.setText("");
                    cellNumberIcon.setImageDrawable(null);
                    imgMCI.setImageResource(R.drawable.hamrah_inactive);
                    imgMTN.setImageResource(R.drawable.irancell_inactive);
                    imgRIGHTEL.setImageResource(R.drawable.rightel_inactive);
                    cellNumber = "";
                    try {
                        MCI_INFO = null;
                        MTN_INFO = null;
                        RIGHTEL_INFO = null;
                        infos = null;
                        this.chargeType = "";
                        this.amount = 0;
                        this.operator = null;
                        this.operatorName = "";
                        tvChargeAmount.setTag(0);
                        tvChargeType.setTag(0);
                        tvChargeAmount.setText("ریال");
                        tvChargeType.setText("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (resultCode == Activity.RESULT_CANCELED) {
                }
                break;
        }
    }
}
