package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;

import java.io.IOException;
import java.util.UUID;

import xyz.homapay.hampay.common.common.encrypt.EncryptionException;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.request.IllegalAppListRequest;
import xyz.homapay.hampay.common.core.model.response.IllegalAppListResponse;
import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestIllegalAppList;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.model.AppState;
import xyz.homapay.hampay.mobile.android.security.KeyExchange;
import xyz.homapay.hampay.mobile.android.util.Constants;
import xyz.homapay.hampay.mobile.android.util.DeviceInfo;

public class StartActivity extends AppCompatActivity {

    private FacedTextView start_button;
    private String encryptionId = "";
    private KeyExchange keyExchange;
    private Activity activity;
    private SharedPreferences.Editor editor;
    private IllegalAppListRequest illegalAppListRequest;
    private RequestIllegalAppList requestIllegalAppList;

    public void userManual(View view){
        Intent intent = new Intent();
        intent.setClass(activity, UserManualActivity.class);
        intent.putExtra(Constants.USER_MANUAL_TEXT, R.string.user_manual_text_start_activity);
        intent.putExtra(Constants.USER_MANUAL_TITLE, R.string.user_manual_title_start_activity);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        activity = StartActivity.this;

        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();
        encryptionId = UUID.randomUUID().toString();
        editor.putString(Constants.ENCRYPTION_ID, encryptionId);
        editor.commit();

        keyExchange = new KeyExchange(activity, encryptionId);
        new KeyExchangeTask().execute();

        start_button = (FacedTextView) findViewById(R.id.start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HamPayDialog(StartActivity.this).showTcPrivacyDialog();
            }
        });


        WebView webView = (WebView)findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/certification.html");
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

    public class RequestIllegalAppListTaskCompleteListener implements AsyncTaskCompleteListener<ResponseMessage<IllegalAppListResponse>> {


        @Override
        public void onTaskComplete(ResponseMessage<IllegalAppListResponse> illegalAppListResponseMessage) {

            if (illegalAppListResponseMessage != null) {

                if (illegalAppListResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS) {

                    String downloadedAppNames = new DeviceInfo(activity).getDownloadedAppNames();
                    for (String illegalAppName : illegalAppListResponseMessage.getService().getIllegalAppList()){
                        if (downloadedAppNames.equalsIgnoreCase(illegalAppName)){
                            requestIllegalAppList = new RequestIllegalAppList(activity, new RequestIllegalAppListTaskCompleteListener());
                            new HamPayDialog(activity).showFailIllegalAppListDialog(requestIllegalAppList, illegalAppListRequest,
                                    illegalAppName,
                                    getString(R.string.msg_found_illegal_app));

                            editor.putBoolean(Constants.FORCE_FETCH_ILLEGAL_APPS, true).commit();

                            return;
                        }
                    }

                } else {
                    illegalAppListResponseMessage.getService().getResultStatus().getDescription();
                    requestIllegalAppList = new RequestIllegalAppList(activity, new RequestIllegalAppListTaskCompleteListener());
                    new HamPayDialog(activity).showFailIllegalAppListDialog(requestIllegalAppList, illegalAppListRequest,
                            illegalAppListResponseMessage.getService().getResultStatus().getCode(),
                            illegalAppListResponseMessage.getService().getResultStatus().getDescription());
                }
            } else {
                requestIllegalAppList = new RequestIllegalAppList(activity, new RequestIllegalAppListTaskCompleteListener());
                new HamPayDialog(activity).showFailIllegalAppListDialog(requestIllegalAppList, illegalAppListRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_illegal_app_list));
            }
        }

        @Override
        public void onTaskPreRun() {
        }
    }

    private class KeyExchangeTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                keyExchange.exchange();
            } catch (EncryptionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            if (keyExchange.getKey() != null && keyExchange.getIv() != null) {
                illegalAppListRequest = new IllegalAppListRequest();
                requestIllegalAppList = new RequestIllegalAppList(activity, new RequestIllegalAppListTaskCompleteListener());
                requestIllegalAppList.execute(illegalAppListRequest);
            }
        }

        @Override
        protected void onPreExecute() {}

    }
}
