package com.hampay.mobile.android.async;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.hampay.mobile.android.account.AccountGeneral;
import com.hampay.mobile.android.account.ContactsManager;
import com.hampay.mobile.android.account.HamPayContact;
import com.hampay.mobile.android.model.EnabledHamPay;

import java.util.List;

/**
 * Created by amir on 7/3/15.
 */
public class RequestUpdateEnabledHamPay extends AsyncTask<List<EnabledHamPay>, Void, List<EnabledHamPay>> {

    private static final String TAG = "RequestUpdateEnabledHamPay";

    private Activity context;
    private AsyncTaskCompleteListener<List<EnabledHamPay>> listener;


    public RequestUpdateEnabledHamPay(Activity context, AsyncTaskCompleteListener<List<EnabledHamPay>> listener)
    {
        this.context = context;
        this.listener = listener;
    }


    protected void onPreExecute()
    {
        super.onPreExecute();
        listener.onTaskPreRun();
    }

    @Override
    protected List<EnabledHamPay> doInBackground(List<EnabledHamPay>... params) {

//        WebServices webServices = new WebServices(context);

        for (EnabledHamPay enabledHamPay : params[0]){
                            addNewAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
                ContactsManager.addContact(context, new HamPayContact("",
                        enabledHamPay.getDisplayName(),
                        "",
                        enabledHamPay.getCellNumber()));

                Log.e("Create", enabledHamPay.getDisplayName());
        }

        return params[0];
    }


    @Override
    protected void onPostExecute(List<EnabledHamPay> tacResponseMessage)
    {
        super.onPostExecute(tacResponseMessage);
        listener.onTaskComplete(tacResponseMessage);
    }

    private void addNewAccount(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = AccountManager.get(context)
                .addAccount(accountType, authTokenType, null, null, context, new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle bnd = future.getResult();
                            Log.i("", "Account was created");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, null);
    }


}

