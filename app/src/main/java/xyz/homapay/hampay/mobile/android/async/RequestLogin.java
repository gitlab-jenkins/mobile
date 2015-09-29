package xyz.homapay.hampay.mobile.android.async;

import android.content.Context;
import android.os.AsyncTask;

import xyz.homapay.hampay.mobile.android.model.LoginData;
import xyz.homapay.hampay.mobile.android.model.SuccessLoginResponse;
import xyz.homapay.hampay.mobile.android.webservice.LoginStream;
import xyz.homapay.hampay.mobile.android.webservice.WebServices;

/**
 * Created by amir on 7/23/15.
 */
public class RequestLogin extends AsyncTask<LoginData, Void, String> {

    private static final String TAG = "RequestLogin";

    private Context context;
    private AsyncTaskCompleteListener<String> listener;
    private String loginResponse;


    public RequestLogin(Context context, AsyncTaskCompleteListener<String> listener)
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
    protected String doInBackground(LoginData... params) {


        LoginStream loginStream = new LoginStream(context, params[0]);

        String resultLogin = "";

        try {
            switch (loginStream.resultCode()){

                case 200:
                    resultLogin = loginStream.successLogin();
                    break;

                case 401:
                    resultLogin = loginStream.failLogin();
                    break;

                case 404:
                    resultLogin = loginStream.failLogin();
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultLogin;

//        WebServices webServices = new WebServices(context);
//
//        try {
//            loginResponse = webServices.sendLoginRequest(params[0]);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return loginResponse;

    }


    @Override
    protected void onPostExecute(String loginResponse)
    {
        super.onPostExecute(loginResponse);
        listener.onTaskComplete(loginResponse);
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
        cancel(true);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        cancel(true);
    }


}

