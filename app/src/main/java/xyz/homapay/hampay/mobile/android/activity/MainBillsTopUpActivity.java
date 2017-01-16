package xyz.homapay.hampay.mobile.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import xyz.homapay.hampay.common.common.ChargePackage;
import xyz.homapay.hampay.common.common.Operator;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.TopUpInfoResponse;
import xyz.homapay.hampay.common.core.model.response.TopUpResponse;
import xyz.homapay.hampay.common.core.model.response.dto.UserProfileDTO;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.common.charge.ChargeAdapterModel;
import xyz.homapay.hampay.mobile.android.common.charge.ChargeType;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSelectChargeAmount;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSelectChargeType;
import xyz.homapay.hampay.mobile.android.common.messages.MessageSetOperator;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.component.topup.TopUpCellNumber;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.dialog.common.ChargeAmountChooserDialog;
import xyz.homapay.hampay.mobile.android.dialog.common.ChargeTypeChooserDialog;
import xyz.homapay.hampay.mobile.android.dialog.common.ProgressDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpCreate;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpCreateImpl;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpCreateView;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpInfo;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpInfoImpl;
import xyz.homapay.hampay.mobile.android.p.topup.TopUpInfoView;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.Dialoger;
import xyz.homapay.hampay.mobile.android.util.ModelLayerImpl;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;
import xyz.homapay.hampay.mobile.android.util.TelephonyUtils;

public class MainBillsTopUpActivity extends AppCompatActivity implements View.OnClickListener, TopUpInfoView, TopUpCreateView {

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
    private RelativeLayout rlChargeType;
    private RelativeLayout rlChargeAmount;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills_top_up);

        dlg = new HamPayDialog(this);

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
                operator = new MessageSetOperator(Operator.MCI).getOperator();
                operatorName = new MessageSetOperator(Operator.MCI).getOperatorName();
                selectOperatorView(true);
                break;
            case R.id.imgMTN:
                operator = new MessageSetOperator(Operator.MTN).getOperator();
                operatorName = new MessageSetOperator(Operator.MTN).getOperatorName();
                selectOperatorView(true);
                break;
            case R.id.imgRIGHTEL:
                operator = new MessageSetOperator(Operator.RAYTEL).getOperator();
                operatorName = new MessageSetOperator(Operator.RAYTEL).getOperatorName();
                selectOperatorView(true);
                break;
            case R.id.rlChargeType:
                if (infos != null) {
                    List<String> items = new ArrayList<>();
                    for (xyz.homapay.hampay.common.common.TopUpInfo item : infos) {
                        items.add(item.getDescription());
                    }

                    ArrayList<ChargeAdapterModel> itemsAdapter = new ArrayList<>();
                    for (int i = 0; i < infos.size(); i++) {
                        ChargeAdapterModel model = new ChargeAdapterModel(i, infos.get(i).getChargeType(), infos.get(i).getDescription(), i == ((int) tvChargeType.getTag()));
                        itemsAdapter.add(model);
                    }
                    ChargeTypeChooserDialog.show(context, itemsAdapter);
                }
                break;
            case R.id.rlChargeAmount:
                if (infos != null) {
                    int index = (int) tvChargeType.getTag();
                    List<String> items = new ArrayList<>();
                    for (ChargePackage item : infos.get(index).getChargePackages()) {
                        items.add(item.getAmount() + "");
                    }

                    ChargeAmountChooserDialog.show(context, items, ((int) tvChargeAmount.getTag()));
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
            // user BoD suggests using Intent.ACTION_PICK instead of .ACTION_GET_CONTENT to avoid the chooser
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            // BoD con't: CONTENT_TYPE instead of CONTENT_ITEM_TYPE
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();

                if (uri != null) {
                    Cursor c = null;
                    try {
                        c = getContentResolver().query(uri, new String[]{
                                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                                        ContactsContract.CommonDataKinds.Phone.TYPE},
                                null, null, null);

                        if (c != null && c.moveToFirst()) {
                            String number = c.getString(0);
                            int type = c.getInt(1);
                            showNumber(number);
                        }
                    } finally {
                        if (c != null) {
                            c.close();
                        }
                    }
                }
            }
        }
    }

    private void createPayment() {
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
                topUpCreate.create(operator, str, chargePackage, chargeType);
            }
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
        this.operator = messageSetOperator.getOperator();
        this.operatorName = messageSetOperator.getOperatorName();
        new Collapse(keyboard).animate();
        selectOperatorView(false);
        topUpInfo.getInfo(messageSetOperator.getOperator());
    }

    private void selectOperatorView(boolean manual) {
        if (manual)
            topUpInfo.getInfo(operator);
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
        ProgressDialog.cancel();
        Dialoger.GENERAL.show(context, getString(R.string.err_general), getString(R.string.err_general_text));
    }

    @Override
    public void onCreated(boolean state, ResponseMessage<TopUpResponse> data, String message) {
        if (state) {
            Intent intent = new Intent(context, ServiceTopUpDetailActivity.class);
            data.getService().getTopUpInfoDTO().setImageId(operatorName);
            intent.putExtra(Constants.TOP_UP_INFO, data.getService().getTopUpInfoDTO());
            intent.putExtra(Constants.CHARGE_TYPE, ChargeType.DIRECT.ordinal());
            startActivity(intent);
        } else {
            onError();
        }
    }

    @Override
    public void onInfoLoaded(boolean state, ResponseMessage<TopUpInfoResponse> data, String message) {
        if (state) {
            infos = data.getService().getTopUpInfoList();
        } else
            onError();
    }

    @Subscribe
    public void onTypeSelect(MessageSelectChargeType type) {
        this.chargeType = type.getSelectedType();
        tvChargeType.setText(type.getSelectedDescrption());
        tvChargeType.setTag(type.getIndex());
    }

    @Subscribe
    public void onAmountSelect(MessageSelectChargeAmount amount) {
        this.amount = Long.parseLong(amount.getAmount());
        tvChargeAmount.setText(amount.getAmount() + " " + getString(R.string.currency_rials));
        tvChargeAmount.setTag(amount.getIndex());
    }
}
