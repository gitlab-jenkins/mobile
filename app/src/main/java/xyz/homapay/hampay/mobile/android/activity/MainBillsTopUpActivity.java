package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import xyz.homapay.hampay.common.common.ChargePackage;
import xyz.homapay.hampay.common.common.Operator;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.common.core.model.response.TopUpInfoResponse;
import xyz.homapay.hampay.common.core.model.response.TopUpResponse;
import xyz.homapay.hampay.common.core.model.response.dto.UserProfileDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.Manifest;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.async.RequestCredentialEntry;
import xyz.homapay.hampay.mobile.android.common.charge.ChargeAdapterModel;
import xyz.homapay.hampay.mobile.android.common.charge.ChargeType;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSelectChargeAmount;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSelectChargeType;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSetOperator;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.topup.TopUpCellNumber;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.dialog.permission.ActionPermission;
import xyz.homapay.hampay.mobile.android.dialog.permission.PermissionContactDialog;
import xyz.homapay.hampay.mobile.android.dialog.permission.PermissionDeviceDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpCreate;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpCreateImpl;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpCreateView;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpInfo;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpInfoImpl;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpInfoView;
import xyz.homapay.hampay.mobile.android.permission.PermissionListener;
import xyz.homapay.hampay.mobile.android.permission.RequestPermissions;
import xyz.homapay.hampay.mobile.android.util.AppManager;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;
import xyz.homapay.hampay.mobile.android.util.ModelLayerImpl;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.TelephonyUtils;
import xyz.homapay.hampay.mobile.android.util.UserContacts;

public class MainBillsTopUpActivity extends AppCompatActivity implements View.OnClickListener, TopUpInfoView, TopUpCreateView, PermissionContactDialog.PermissionContactDialogListener{

    private static int selectedType = 0;
    private SharedPreferences prefs;
    private Context context;
    private PersianEnglishDigit persian;
    private LinearLayout keyboard;
    private RelativeLayout billsTool;
    private RelativeLayout topUpTool;
    private LinearLayout billsLayout;
    private LinearLayout topUpLayout;
    private ImageView billsTriangle;
    private ImageView topUpTriangle;
    private LinearLayout mobileBill;
    private LinearLayout serviceBills;
    private TopUpCellNumber cellNumberText;
    private FacedTextView tvChargeType;
    private FacedTextView tvChargeAmount;
    private FacedTextView btnTopUpPay;
    private ImageView imgMCI;
    private ImageView imgMTN;
    private ImageView imgRIGHTEL;
    private String cellNumber = "";
    private TopUpInfo topUpInfo;
    private TopUpCreate topUpCreate;
    private Operator operator;
    private List<xyz.homapay.hampay.common.common.TopUpInfo> infos;
    private long amount;
    private String chargeType;
    private String operatorName;
    private HamPayDialog dlg;
    private UserProfileDTO userProfile;
    private ImageView cellNumberIcon;
    private List<xyz.homapay.hampay.common.common.TopUpInfo> MCI_INFO;
    private List<xyz.homapay.hampay.common.common.TopUpInfo> MTN_INFO;
    private List<xyz.homapay.hampay.common.common.TopUpInfo> RIGHTEL_INFO;
    private ArrayList<PermissionListener> permissionListeners = new ArrayList<>();
    private final Handler handler = new Handler();
    private Activity activity;

    public void backActionBar(View view) {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        HamPayApplication.setAppSate(AppState.Paused);
    }

    @Override
    protected void onStop() {
//        EventBus.getDefault().unregister(this);
        super.onStop();
        HamPayApplication.setAppSate(AppState.Stoped);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        for (PermissionListener permissionListener : permissionListeners)
            if (permissionListener.onResult(requestCode, permissions, grantResults)) {
                permissionListeners.remove(permissionListener);
            }
    }

    private void requestAndLoadUserContact() {
        String[] permissions = new String[]{Manifest.permission.READ_CONTACTS};

        permissionListeners = new RequestPermissions().request(activity, Constants.READ_CONTACTS, permissions, (requestCode, requestPermissions, grantResults) -> {
            if (requestCode == Constants.READ_CONTACTS) {
                if (grantResults.length > 0 && requestPermissions[0].equals(Manifest.permission.READ_CONTACTS) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, 1);
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS);
                        if (showRationale){
                            handler.post(() -> {
                                PermissionContactDialog permissionContactDialog = new PermissionContactDialog();
                                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.add(permissionContactDialog, null);
                                fragmentTransaction.commitAllowingStateLoss();
                            });
                        }else {

                        }
                    }else {
                        handler.post(() -> {
                            PermissionDeviceDialog permissionDeviceDialog = new PermissionDeviceDialog();
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills_top_up);

        dlg = new HamPayDialog(this);
        activity = MainBillsTopUpActivity.this;
        context = this;
        topUpInfo = new TopUpInfoImpl(new ModelLayerImpl(context), this);
        topUpCreate = new TopUpCreateImpl(new ModelLayerImpl(context), this);
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        persian = new PersianEnglishDigit();

        userProfile = (UserProfileDTO) getIntent().getSerializableExtra(Constants.USER_PROFILE);

        keyboard = (LinearLayout) findViewById(R.id.keyboard);

        billsTool = (RelativeLayout) findViewById(R.id.billsTool);
        billsTool.setOnClickListener(this);
        topUpTool = (RelativeLayout) findViewById(R.id.topUpTool);
        topUpTool.setOnClickListener(this);
        billsLayout = (LinearLayout) findViewById(R.id.billsLayout);
        topUpLayout = (LinearLayout) findViewById(R.id.topUpLayout);
        billsTriangle = (ImageView) findViewById(R.id.billsTriangle);
        topUpTriangle = (ImageView) findViewById(R.id.topUpTriangle);
        mobileBill = (LinearLayout) findViewById(R.id.mobileBills);
        mobileBill.setOnClickListener(this);
        serviceBills = (LinearLayout) findViewById(R.id.serviceBills);
        serviceBills.setOnClickListener(this);
        cellNumberText = (TopUpCellNumber) findViewById(R.id.cellNumberText);
        cellNumberText.setOnClickListener(this);
        imgMCI = (ImageView) findViewById(R.id.imgMCI);
        imgMTN = (ImageView) findViewById(R.id.imgMTN);
        imgRIGHTEL = (ImageView) findViewById(R.id.imgRIGHTEL);
        cellNumberIcon = (ImageView) findViewById(R.id.cellNumberIcon);
        btnTopUpPay = (FacedTextView) findViewById(R.id.btnTopUpPay);

        tvChargeType = (FacedTextView) findViewById(R.id.tvChargeType);
        tvChargeAmount = (FacedTextView) findViewById(R.id.tvChargeAmount);

        tvChargeAmount.setTag(0);
        tvChargeType.setTag(0);

        imgMCI.setOnClickListener(this);
        imgMTN.setOnClickListener(this);
        imgRIGHTEL.setOnClickListener(this);
        btnTopUpPay.setOnClickListener(this);
        cellNumberText.setCellNumberIcon(cellNumberIcon);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.billsTool:
                setLayout(1);
                break;
            case R.id.topUpTool:
                setLayout(2);
                break;
            case R.id.mobileBills:
                intent = new Intent(context, MobileBillsActivity.class);
                startActivity(intent);
                break;
            case R.id.serviceBills:
                intent = new Intent(context, ServiceBillsActivity.class);
                startActivity(intent);
                break;
            case R.id.cellNumberText:
                new Expand(keyboard).animate();
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
                        dlg.showChargeTypeChooser(this, itemsAdapter);
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
                        dlg.showChargeAmountChooser(context, items);
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
        this.cellNumber = TelephonyUtils.fixPhoneNumber(context, userProfile.getCellNumber()).substring(2);
        cellNumberText.setText(new PersianEnglishDigit().E2P(this.cellNumber));
    }

    private void showNumber(String cellNumber) {
        this.cellNumber = TelephonyUtils.fixPhoneNumber(context, cellNumber).substring(2);
        cellNumberText.setText(new PersianEnglishDigit().E2P(this.cellNumber));
    }

    private void startActivityForGetContactsNumbers() {
        try {
            requestAndLoadUserContact();
//            Intent intent= new Intent(Intent.ACTION_PICK,  ContactsContract.Contacts.CONTENT_URI);
//            // user BoD suggests using Intent.ACTION_PICK instead of .ACTION_GET_CONTENT to avoid the chooser
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            // BoD con't: CONTENT_TYPE instead of CONTENT_ITEM_TYPE
//            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
//            startActivityForResult(intent, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (1) :
                cellNumber = "";
                cellNumberText.setText("");
                if (resultCode == Activity.RESULT_OK) {
                    Cursor cursor = managedQuery(data.getData(), null, null, null, null);
                    while (cursor.moveToNext()) {
                        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (hasPhone.equalsIgnoreCase("1"))
                            hasPhone = "true";
                        else
                            hasPhone = "false";

                        if (Boolean.parseBoolean(hasPhone)) {
                            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
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
                    imgMCI.setImageResource(R.mipmap.irancell_inactive);
                    imgMTN.setImageResource(R.mipmap.hamrah_inactive);
                    imgRIGHTEL.setImageResource(R.mipmap.rightel_inactive);
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

    private void createPayment() {
        if (!isNumberOk())
            return;
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

    private void setLayout(int layout) {
        switch (layout) {
            case 1:
                billsTool.setBackgroundResource(R.color.tool_bar_selected);
                billsTriangle.setVisibility(View.VISIBLE);
                billsLayout.setVisibility(View.VISIBLE);
                topUpTool.setBackgroundResource(R.color.tool_bar_unselected);
                topUpTriangle.setVisibility(View.GONE);
                topUpLayout.setVisibility(View.GONE);
                new Collapse(keyboard).animate();
                break;

            case 2:
                topUpTool.setBackgroundResource(R.color.tool_bar_selected);
                topUpTriangle.setVisibility(View.VISIBLE);
                topUpLayout.setVisibility(View.VISIBLE);
                billsTool.setBackgroundResource(R.color.tool_bar_unselected);
                billsTriangle.setVisibility(View.GONE);
                billsLayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (keyboard.getVisibility() == View.VISIBLE) {
            new Collapse(keyboard).animate();
        } else {
            finish();
        }
    }

    public void pressKey(View view) {
        if (view.getTag().toString().equals("*")) {
            new Collapse(keyboard).animate();
        } else if (view.getTag().toString().equals("|")) {
            new Expand(keyboard).animate();
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
                imgMCI.setImageResource(R.mipmap.hamrah_active);
                imgMTN.setImageResource(R.mipmap.irancell_inactive);
                imgRIGHTEL.setImageResource(R.mipmap.rightel_inactive);
                break;
            case MTN:
                imgMCI.setImageResource(R.mipmap.hamrah_inactive);
                imgMTN.setImageResource(R.mipmap.irancell_active);
                imgRIGHTEL.setImageResource(R.mipmap.rightel_inactive);
                break;
            case RAYTEL:
                imgMCI.setImageResource(R.mipmap.hamrah_inactive);
                imgMTN.setImageResource(R.mipmap.irancell_inactive);
                imgRIGHTEL.setImageResource(R.mipmap.rightel_active);
                break;
            default:
                imgMCI.setImageResource(R.mipmap.hamrah_inactive);
                imgMTN.setImageResource(R.mipmap.irancell_inactive);
                imgRIGHTEL.setImageResource(R.mipmap.rightel_inactive);
                break;
        }
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
    public void dismissProgress() {
        dlg.dismisWaitingDialog();
    }

    @Override
    public void onError() {
        btnTopUpPay.setEnabled(true);
        dlg.showFailPendingPaymentDialog(getString(R.string.err_general), getString(R.string.err_general_text));
    }

    @Override
    public void onCreated(boolean state, ResponseMessage<TopUpResponse> data, String message) {
        btnTopUpPay.setEnabled(true);
        if (state) {
            Intent intent = new Intent(context, ServiceTopUpDetailActivity.class);
            data.getService().getTopUpInfoDTO().setImageId(operatorName);
            intent.putExtra(Constants.TOP_UP_INFO, data.getService().getTopUpInfoDTO());
            intent.putExtra(Constants.CHARGE_TYPE, ChargeType.DIRECT.ordinal());
            startActivityForResult(intent, 48);
        } else {
            onError();
        }
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
        switch (actionPermission){
            case GRANT:
                requestAndLoadUserContact();
                break;
            case DENY:
                break;
        }
    }
}
