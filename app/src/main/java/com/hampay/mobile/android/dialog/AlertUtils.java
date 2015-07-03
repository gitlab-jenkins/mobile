package com.hampay.mobile.android.dialog;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hampay.mobile.android.R;


public class AlertUtils {

    private static AlertUtils instance;
    private AlertDialog progressDialogAlert;
    private Handler handler;

    private AlertUtils() {
        if( handler == null )
            handler = new Handler();
    }

    public static AlertUtils getInstance() {
        if( instance == null )
            instance = new AlertUtils();
        return instance;
    }

    public void showConfirmDialog(final Context context, String title, String message) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.confirm_dialog, null, false);
        Button returnButton = (Button)dialogView.findViewById(R.id.confirmDialog_okButton);
        returnButton.setText(context.getResources().getString(R.string.return_button));
        returnButton.setVisibility(View.VISIBLE);

        TextView titleView = (TextView)dialogView.findViewById(R.id.confirmDialog_title);
        titleView.setText(title);

        TextView messageView = (TextView)dialogView.findViewById(R.id.confirmDialog_message);
        messageView.setText(message);

        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(context);
        confirmBuilder.setView(dialogView);
        final AlertDialog confirmAlert = confirmBuilder.create();
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmAlert.dismiss();
            }
        });

        confirmAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmAlert.show();
    }

    public void showLogoutDialog(final Context context, String title, String message,
                                 final View.OnClickListener okButtonClickListener) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.confirm_dialog, null, false);

        Button okButton = (Button)dialogView.findViewById(R.id.confirmDialog_okButton);
        okButton.setText(context.getResources().getString(R.string.ok_button));
        okButton.setVisibility(View.VISIBLE);

        Button cancelButton = (Button)dialogView.findViewById(R.id.confirmDialog_cancelButton);
        cancelButton.setText(context.getResources().getString(R.string.cancel_button));
        cancelButton.setVisibility(View.VISIBLE);

        TextView titleView = (TextView)dialogView.findViewById(R.id.confirmDialog_title);
        titleView.setText(title);
        TextView messageView = (TextView)dialogView.findViewById(R.id.confirmDialog_message);

        messageView.setText(message);

        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(context);
        confirmBuilder.setView(dialogView);
        final AlertDialog confirmAlert = confirmBuilder.create();
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmAlert.dismiss();
                if( okButtonClickListener != null )
                    okButtonClickListener.onClick(v);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmAlert.dismiss();
            }
        });

        confirmAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmAlert.show();
    }

    public void showConfirmDialog(final Context context, String title, String message, final String okButtonText, final String cancelButtonText,
                                 final View.OnClickListener okButtonClickListener) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.confirm_dialog, null, false);

        TextView titleView = (TextView)dialogView.findViewById(R.id.confirmDialog_title);
        titleView.setText(title);
        TextView messageView = (TextView)dialogView.findViewById(R.id.confirmDialog_message);

        messageView.setText(message);

        final AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(context);
        confirmBuilder.setView(dialogView);
        handler.post(new Runnable() {
            @Override
            public void run() {
                final AlertDialog confirmAlert = confirmBuilder.create();

                if( okButtonText != null ) {
                    Button okButton = (Button)dialogView.findViewById(R.id.confirmDialog_okButton);
                    okButton.setText(okButtonText);
                    okButton.setVisibility(View.VISIBLE);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            confirmAlert.dismiss();
                            if( okButtonClickListener != null )
                                okButtonClickListener.onClick(view);
                        }
                    });
                }

                if( cancelButtonText != null ) {
                    Button cancelButton = (Button)dialogView.findViewById(R.id.confirmDialog_cancelButton);
                    cancelButton.setText(cancelButtonText);
                    cancelButton.setVisibility(View.VISIBLE);

                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            confirmAlert.dismiss();
                        }
                    });
                }

                confirmAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                confirmAlert.show();
            }
        });
    }

    public void showConfirmDialog(final Context context, String title, String message, String okButtonText, String cancelButtonText,
                                  final DialogInterface.OnDismissListener onDismissListener, boolean isModal) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.confirm_dialog, null, false);

        TextView titleView = (TextView)dialogView.findViewById(R.id.confirmDialog_title);
        titleView.setText(title);
        TextView messageView = (TextView)dialogView.findViewById(R.id.confirmDialog_message);

        messageView.setText(message);

        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(context);
        confirmBuilder.setView(dialogView);
        final AlertDialog confirmAlert = confirmBuilder.create();

        if( okButtonText != null ) {
            Button okButton = (Button)dialogView.findViewById(R.id.confirmDialog_okButton);
            okButton.setText(okButtonText);
            okButton.setVisibility(View.VISIBLE);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmAlert.dismiss();
                }
            });
        }

        if( cancelButtonText != null ) {
            Button cancelButton = (Button)dialogView.findViewById(R.id.confirmDialog_cancelButton);
            cancelButton.setText(cancelButtonText);
            cancelButton.setVisibility(View.VISIBLE);

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmAlert.dismiss();
                }
            });
        }

        confirmAlert.setOnDismissListener(onDismissListener);
        confirmAlert.setCanceledOnTouchOutside(!isModal);
        confirmAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmAlert.show();
    }

    public void showHelpDialog(final Context context, String title, String message, String okButtonText,
                               final DialogInterface.OnDismissListener onDismissListener, boolean isModal) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.confirm_dialog, null, false);

        TextView titleView = (TextView)dialogView.findViewById(R.id.confirmDialog_title);
        titleView.setText(title);
        TextView messageView = (TextView)dialogView.findViewById(R.id.confirmDialog_message);

        messageView.setText(message);

        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(context);
        confirmBuilder.setView(dialogView);
        final AlertDialog confirmAlert = confirmBuilder.create();

        if( okButtonText != null ) {
            Button okButton = (Button)dialogView.findViewById(R.id.confirmDialog_okButton);
            okButton.setText(okButtonText);
            okButton.setVisibility(View.VISIBLE);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmAlert.dismiss();
                }
            });
        }

        confirmAlert.setOnDismissListener(onDismissListener);
        confirmAlert.setCanceledOnTouchOutside(!isModal);
        confirmAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmAlert.show();

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showProgressDialog(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.progress_dialog, null, false);

        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(context, R.style.AppTheme_Translucent);
        confirmBuilder.setView(dialogView);
        progressDialogAlert = confirmBuilder.create();
        progressDialogAlert.setCanceledOnTouchOutside(false);
        progressDialogAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialogAlert.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        progressDialogAlert.show();
    }

    public void hideProgressDialog() {
        if( progressDialogAlert != null )
            progressDialogAlert.dismiss();
    }

    public boolean isProgressDialogShowing() {
        return progressDialogAlert != null && progressDialogAlert.isShowing();
    }
}
