package xyz.homapay.hampay.mobile.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PersianEnglishDigit;

public class MainBillsTopUpActivity extends AppCompatActivity implements View.OnClickListener{

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
    private FacedTextView cellNumberText;
    private String cellNumber = "";

    public void backActionBar(View view){
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        HamPayApplication.setAppSate(AppState.Paused);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HamPayApplication.setAppSate(AppState.Stoped);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        context = this;
        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        persian = new PersianEnglishDigit();

        keyboard = (LinearLayout)findViewById(R.id.keyboard);

        billsTool = (RelativeLayout) findViewById(R.id.billsTool);
        billsTool.setOnClickListener(this);
        topUpTool = (RelativeLayout) findViewById(R.id.topUpTool);
        topUpTool.setOnClickListener(this);
        billsLayout = (LinearLayout)findViewById(R.id.billsLayout);
        topUpLayout = (LinearLayout)findViewById(R.id.topUpLayout);
        billsTriangle = (ImageView)findViewById(R.id.billsTriangle);
        topUpTriangle = (ImageView)findViewById(R.id.topUpTriangle);
        mobileBill = (LinearLayout)findViewById(R.id.mobileBills);
        mobileBill.setOnClickListener(this);
        serviceBills = (LinearLayout)findViewById(R.id.serviceBills);
        serviceBills.setOnClickListener(this);
        cellNumberText = (FacedTextView)findViewById(R.id.cellNumberText);
        cellNumberText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
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
        }
    }

    private void setLayout(int layout){
        switch (layout){
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
        if (keyboard.getVisibility() == View.VISIBLE){
            new Collapse(keyboard).animate();
        }
        else {
            finish();
        }
    }

    public void pressKey(View view) {
        if (view.getTag().toString().equals("*")) {
            new Collapse(keyboard).animate();
        }else if (view.getTag().toString().equals("|")) {
            new Expand(keyboard).animate();
        }else {
            inputDigit(view.getTag().toString());
        }
    }

    private void inputDigit(String digit){
        if (digit.endsWith("d")){
        }else {
            if (cellNumber.length() > 8) return;
            cellNumber += digit;
        }

        if (digit.endsWith("d")){
            if (cellNumber.length() == 0) return;
            cellNumber = cellNumber.substring(0, cellNumber.length() - 1);
            cellNumberText.setText(persian.E2P(cellNumber));

        }else {
            cellNumberText.setText(persian.E2P(cellNumber));
        }
    }
}
