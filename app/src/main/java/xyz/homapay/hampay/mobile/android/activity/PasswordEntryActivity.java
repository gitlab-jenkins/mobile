package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.animation.Collapse;
import xyz.homapay.hampay.mobile.android.animation.Expand;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.PasswordComplexity;

public class PasswordEntryActivity extends AppCompatActivity implements View.OnClickListener{

    private Activity activity;
    private int passwordMode = 0;
    private String inputPasswordValue = "";
    private String inputRePasswordValue = "";
    private FacedTextView input_digit_1;
    private FacedTextView input_digit_2;
    private FacedTextView input_digit_3;
    private FacedTextView input_digit_4;
    private FacedTextView input_digit_5;
    private RelativeLayout password_1_rl;
    private RelativeLayout password_2_rl;
    private LinearLayout keyboard;
    private LinearLayout password_holder;


    public void userManual(View view){
        Intent intent = new Intent();
        intent.setClass(activity, UserManualActivity.class);
        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_pass_code_entry);
        intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_pass_code_entry);
        startActivity(intent);
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
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_entry);

        activity = PasswordEntryActivity.this;

        keyboard = (LinearLayout)findViewById(R.id.keyboard);
        password_holder = (LinearLayout)findViewById(R.id.password_holder);
        password_holder.setOnClickListener(this);

        password_1_rl = (RelativeLayout)findViewById(R.id.password_1_rl);
        password_2_rl = (RelativeLayout)findViewById(R.id.password_2_rl);

        input_digit_1 = (FacedTextView)findViewById(R.id.input_digit_1);
        input_digit_2 = (FacedTextView)findViewById(R.id.input_digit_2);
        input_digit_3 = (FacedTextView)findViewById(R.id.input_digit_3);
        input_digit_4 = (FacedTextView)findViewById(R.id.input_digit_4);
        input_digit_5 = (FacedTextView)findViewById(R.id.input_digit_5);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.password_holder:
                if (keyboard.getVisibility() != View.VISIBLE)
                    new Expand(keyboard).animate();
                break;
            case R.id.keyboard_dismiss:
                if (keyboard.getVisibility() == View.VISIBLE)
                    new Collapse(keyboard).animate();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (keyboard.getVisibility() == View.VISIBLE){
            new Collapse(keyboard).animate();
        }
        else {
            new HamPayDialog(activity).exitRegistrationDialog();
        }
    }

    public void pressKey(View view){
        if (view.getTag().toString().equals("*")){
            new Collapse(keyboard).animate();
        }
        else {
            inputDigit(view.getTag().toString());
        }
    }

    private void inputDigit(String digit){

        switch (passwordMode){
            case 0:
                if (digit.contains("d")){
                    if (inputPasswordValue.length() > 0) {
                        inputPasswordValue = inputPasswordValue.substring(0, inputPasswordValue.length() - 1);
                        if (inputPasswordValue.length() == 4){
                            input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_5.setText("");
                        }
                        else if (inputPasswordValue.length() == 3){
                            input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_4.setText("");
                        }
                        else if (inputPasswordValue.length() == 2){
                            input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_3.setText("");
                        }
                        else if (inputPasswordValue.length() == 1){
                            input_digit_2.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_2.setText("");
                        }
                        else if (inputPasswordValue.length() == 0){
                            input_digit_1.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_1.setText("");
                        }
                    }
                    return;
                }
                else {
                    if (inputPasswordValue.length() <= 5) {
                        inputPasswordValue += digit;
                    }
                }

                if (inputPasswordValue.length() <= 5) {
                    switch (inputPasswordValue.length()) {
                        case 1:
                            input_digit_1.setBackgroundResource(R.drawable.pass_value_placeholder);
                            input_digit_2.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                            break;

                        case 2:
                            input_digit_2.setBackgroundResource(R.drawable.pass_value_placeholder);
                            input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                            break;
                        case 3:
                            input_digit_3.setBackgroundResource(R.drawable.pass_value_placeholder);
                            input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                            break;
                        case 4:
                            input_digit_4.setBackgroundResource(R.drawable.pass_value_placeholder);
                            input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                            break;
                        case 5:
                            input_digit_5.setBackgroundResource(R.drawable.pass_value_placeholder);
                            input_digit_1.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_2.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                            int passwordComplexity = new PasswordComplexity(inputPasswordValue).check();
                            if (passwordComplexity != 1){
                                inputPasswordValue = "";
                                Toast.makeText(activity, getString(passwordComplexity), Toast.LENGTH_SHORT).show();
                                return;
                            }else {
                                passwordMode = 1;
                                password_1_rl.setVisibility(View.GONE);
                                password_2_rl.setVisibility(View.VISIBLE);
                            }
                            break;
                    }
                }
                break;

            case 1:
                if (digit.contains("d")){
                    if (inputRePasswordValue.length() > 0) {
                        inputRePasswordValue = inputRePasswordValue.substring(0, inputRePasswordValue.length() - 1);
                        if (inputRePasswordValue.length() == 4){
                            input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_5.setText("");
                        }
                        else if (inputRePasswordValue.length() == 3){
                            input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_4.setText("");
                        }
                        else if (inputRePasswordValue.length() == 2){
                            input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_3.setText("");
                        }
                        else if (inputRePasswordValue.length() == 1){
                            input_digit_2.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_2.setText("");
                        }
                        else if (inputRePasswordValue.length() == 0){
                            input_digit_1.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_1.setText("");
                        }
                    }
                    return;
                }
                else {
                    if (inputRePasswordValue.length() <= 5) {
                        inputRePasswordValue += digit;
                    }
                }

                if (inputRePasswordValue.length() <= 5) {
                    switch (inputRePasswordValue.length()) {
                        case 1:
                            input_digit_1.setBackgroundResource(R.drawable.pass_value_placeholder);
                            input_digit_2.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                            break;

                        case 2:
                            input_digit_2.setBackgroundResource(R.drawable.pass_value_placeholder);
                            input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                            break;
                        case 3:
                            input_digit_3.setBackgroundResource(R.drawable.pass_value_placeholder);
                            input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
                            input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                            break;
                        case 4:
                            input_digit_4.setBackgroundResource(R.drawable.pass_value_placeholder);
                            input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                            break;
                        case 5:
                            input_digit_5.setBackgroundResource(R.drawable.pass_value_placeholder);
                            new Collapse(keyboard).animate();
                            if (inputPasswordValue.equalsIgnoreCase(inputRePasswordValue)) {
                                Intent intent = new Intent();
                                intent.setClass(PasswordEntryActivity.this, MemorableWordEntryActivity.class);
                                intent.putExtra(Constants.USER_ENTRY_PASSWORD, inputPasswordValue);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();
                                startActivity(intent);
                            } else {
                                passwordMode = 0;
                                (new HamPayDialog(this)).showDisMatchPasswordDialog();
                                password_1_rl.setVisibility(View.VISIBLE);
                                password_2_rl.setVisibility(View.GONE);
                                inputPasswordValue = "";
                                inputRePasswordValue = "";
                                input_digit_1.setBackgroundResource(R.drawable.pass_value_empty);
                                input_digit_2.setBackgroundResource(R.drawable.pass_value_empty);
                                input_digit_3.setBackgroundResource(R.drawable.pass_value_empty);
                                input_digit_4.setBackgroundResource(R.drawable.pass_value_empty);
                                input_digit_5.setBackgroundResource(R.drawable.pass_value_empty);
                            }
                            break;
                    }
                }
                break;
        }
    }
}
