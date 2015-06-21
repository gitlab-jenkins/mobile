package com.hampay.mobile.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.hampay.mobile.android.R;
import com.hampay.mobile.android.component.FacedTextView;

public class ConfirmInfoActivity extends ActionBarActivity {

    CardView keepOn_CardView;

    Dialog confirm_info_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_info);

        keepOn_CardView = (CardView)findViewById(R.id.keepOn_CardView);
        keepOn_CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Rect displayRectangle = new Rect();
                Activity parent = (Activity) ConfirmInfoActivity.this;
                Window window = parent.getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

                View view = getLayoutInflater().inflate(R.layout.dialog_confirm_info, null);

                FacedTextView check_account = (FacedTextView) view.findViewById(R.id.check_account);
                FacedTextView uncheck_account = (FacedTextView) view.findViewById(R.id.uncheck_account);

                check_account.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirm_info_dialog.dismiss();

                    }
                });


                uncheck_account.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirm_info_dialog.dismiss();
                        Intent intent = new Intent();
                        intent.setClass(ConfirmInfoActivity.this, VerifyAccountNoActivity.class);
                        startActivity(intent);
                    }
                });



                view.setMinimumWidth((int) (displayRectangle.width() * 0.8f));
                confirm_info_dialog = new Dialog(ConfirmInfoActivity.this);
                confirm_info_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                confirm_info_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                confirm_info_dialog.setContentView(view);
                confirm_info_dialog.setTitle(null);
                confirm_info_dialog.setCanceledOnTouchOutside(false);

                confirm_info_dialog.show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_confirm_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
