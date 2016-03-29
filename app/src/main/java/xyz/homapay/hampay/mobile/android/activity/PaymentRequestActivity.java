package xyz.homapay.hampay.mobile.android.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.List;

import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.common.response.ResultStatus;
import xyz.homapay.hampay.common.core.model.dto.ContactDTO;
import xyz.homapay.hampay.common.core.model.dto.UserVerificationStatus;
import xyz.homapay.hampay.common.core.model.request.ContactsHampayEnabledRequest;
import xyz.homapay.hampay.common.core.model.request.LatestInvoiceContactsRequest;
import xyz.homapay.hampay.common.core.model.response.ContactsHampayEnabledResponse;
import xyz.homapay.hampay.common.core.model.response.LatestInvoiceContactsResponse;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.adapter.HamPayEnabledContactAdapter;
import xyz.homapay.hampay.mobile.android.async.AsyncTaskCompleteListener;
import xyz.homapay.hampay.mobile.android.async.RequestContactHampayEnabled;
import xyz.homapay.hampay.mobile.android.async.RequestLatestInvoiceContacts;
import xyz.homapay.hampay.mobile.android.component.FacedTextView;
import xyz.homapay.hampay.mobile.android.dialog.HamPayDialog;
import xyz.homapay.hampay.mobile.android.util.Constants;

public class PaymentRequestActivity extends AppCompatActivity implements View.OnClickListener{


    private Context context;
    private Activity activity;
    private ListView paymentRequestList;
    List<ContactDTO> contacts;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private HamPayEnabledContactAdapter hamPayContactAdapter;

    private RequestLatestInvoiceContacts requestLatestInvoiceContacts;
    private LatestInvoiceContactsRequest latestInvoiceContactsRequest;

    private RelativeLayout recent_rl;
    private FacedTextView recent_title;
    private View recent_sep;
    private RelativeLayout hampay_rl;
    private FacedTextView hampay_title;
    private View hampay_sep;
    private FacedTextView selectedMenu;

    private Dialog dialog;

    ContactsHampayEnabledRequest contactsHampayEnabledRequest;
    RequestContactHampayEnabled requestContactHampayEnabled;

    private HamPayDialog hamPayDialog;

    public void backActionBar(View view){
        finish();
    }

    public void menu(View v){

        Rect displayRectangle = new Rect();
        Activity parent = (Activity) activity;
        Window window = parent.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        View view = activity.getLayoutInflater().inflate(R.layout.dialog_payment_request, null);

        final FacedTextView recent_payment_request = (FacedTextView) view.findViewById(R.id.recent_payment_request);
        FacedTextView hampay_contact_list = (FacedTextView) view.findViewById(R.id.hampay_contact_list);

        recent_payment_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selectedMenu.setText(getString(R.string.recent_payment_request));
                latestInvoiceContactsRequest = new LatestInvoiceContactsRequest();
                requestLatestInvoiceContacts = new RequestLatestInvoiceContacts(activity, new RequestLatestInvoiceContactsTaskCompleteListener());
                requestLatestInvoiceContacts.execute(latestInvoiceContactsRequest);
            }
        });

        hampay_contact_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selectedMenu.setText(getString(R.string.hampay_contact_list));
                contactsHampayEnabledRequest = new ContactsHampayEnabledRequest();
                requestContactHampayEnabled = new RequestContactHampayEnabled(context, new RequestContactHampayEnabledTaskCompleteListener());
                requestContactHampayEnabled.execute(contactsHampayEnabledRequest);
            }
        });

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0);
        dialog.setContentView(view);
        dialog.setTitle(null);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        layoutParams.x = 25;
        layoutParams.y = 20;

        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_request);

        context = this;
        activity = PaymentRequestActivity.this;
        hamPayDialog = new HamPayDialog(activity);

        prefs = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE);
        editor = getSharedPreferences(Constants.APP_PREFERENCE_NAME, MODE_PRIVATE).edit();

        paymentRequestList = (ListView)findViewById(R.id.paymentRequestList);

        recent_rl = (RelativeLayout)findViewById(R.id.recent_rl);
        recent_rl.setOnClickListener(this);
        recent_title = (FacedTextView)findViewById(R.id.recent_title);
        recent_sep = (View)findViewById(R.id.recent_sep);
        hampay_rl = (RelativeLayout)findViewById(R.id.hampay_rl);
        hampay_rl.setOnClickListener(this);
        hampay_title = (FacedTextView)findViewById(R.id.hampay_title);
        hampay_sep = (View)findViewById(R.id.hampay_sep);

        selectedMenu = (FacedTextView)findViewById(R.id.selectedMenu);

        latestInvoiceContactsRequest = new LatestInvoiceContactsRequest();
        requestLatestInvoiceContacts = new RequestLatestInvoiceContacts(activity, new RequestLatestInvoiceContactsTaskCompleteListener());
        requestLatestInvoiceContacts.execute(latestInvoiceContactsRequest);

        paymentRequestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(activity, PaymentRequestDetailActivity.class);
                intent.putExtra(Constants.HAMPAY_CONTACT, contacts.get(position));
                startActivityForResult(intent, 1024);
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.recent_rl:
                recent_title.setTextColor(ContextCompat.getColor(context, R.color.user_change_status));
                recent_sep.setBackgroundColor(ContextCompat.getColor(context, R.color.user_change_status));
                hampay_title.setTextColor(ContextCompat.getColor(context, R.color.normal_text));
                hampay_sep.setBackgroundColor(ContextCompat.getColor(context, R.color.normal_text));
                latestInvoiceContactsRequest = new LatestInvoiceContactsRequest();
                requestLatestInvoiceContacts = new RequestLatestInvoiceContacts(activity, new RequestLatestInvoiceContactsTaskCompleteListener());
                requestLatestInvoiceContacts.execute(latestInvoiceContactsRequest);
                break;

            case R.id.hampay_rl:
                hampay_title.setTextColor(ContextCompat.getColor(context, R.color.user_change_status));
                hampay_sep.setBackgroundColor(ContextCompat.getColor(context, R.color.user_change_status));
                recent_title.setTextColor(ContextCompat.getColor(context, R.color.normal_text));
                recent_sep.setBackgroundColor(ContextCompat.getColor(context, R.color.normal_text));
                contactsHampayEnabledRequest = new ContactsHampayEnabledRequest();
                requestContactHampayEnabled = new RequestContactHampayEnabled(context, new RequestContactHampayEnabledTaskCompleteListener());
                requestContactHampayEnabled.execute(contactsHampayEnabledRequest);
                break;
        }

    }

    public class RequestContactHampayEnabledTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<ContactsHampayEnabledResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<ContactsHampayEnabledResponse> contactsHampayEnabledResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (contactsHampayEnabledResponseMessage != null){
                if (contactsHampayEnabledResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){

                    contacts = contactsHampayEnabledResponseMessage.getService().getContacts();

                    hamPayContactAdapter = new HamPayEnabledContactAdapter(context, contacts,
                            prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
                    paymentRequestList.setAdapter(hamPayContactAdapter);
                }
                else {
                    requestContactHampayEnabled = new RequestContactHampayEnabled(context, new RequestContactHampayEnabledTaskCompleteListener());
                    new HamPayDialog(activity).showFailContactsHamPayEnabledDialog(requestContactHampayEnabled, contactsHampayEnabledRequest,
                            contactsHampayEnabledResponseMessage.getService().getResultStatus().getCode(),
                            contactsHampayEnabledResponseMessage.getService().getResultStatus().getDescription());

                }
            }else {
                requestContactHampayEnabled = new RequestContactHampayEnabled(context, new RequestContactHampayEnabledTaskCompleteListener());
                new HamPayDialog(activity).showFailContactsHamPayEnabledDialog(requestContactHampayEnabled, contactsHampayEnabledRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_contacts_enabled));

            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }

    public class RequestLatestInvoiceContactsTaskCompleteListener implements
            AsyncTaskCompleteListener<ResponseMessage<LatestInvoiceContactsResponse>> {
        @Override
        public void onTaskComplete(ResponseMessage<LatestInvoiceContactsResponse> latestInvoiceContactsResponseMessage) {

            hamPayDialog.dismisWaitingDialog();

            if (latestInvoiceContactsResponseMessage != null){
                if (latestInvoiceContactsResponseMessage.getService().getResultStatus() == ResultStatus.SUCCESS){

                    contacts = latestInvoiceContactsResponseMessage.getService().getContacts();

                    hamPayContactAdapter = new HamPayEnabledContactAdapter(context, contacts,
                            prefs.getString(Constants.LOGIN_TOKEN_ID, ""));
                    paymentRequestList.setAdapter(hamPayContactAdapter);
                }
                else {
                    requestLatestInvoiceContacts = new RequestLatestInvoiceContacts(context, new RequestLatestInvoiceContactsTaskCompleteListener());
                    new HamPayDialog(activity).showFailLatestInvoiceDialog(requestLatestInvoiceContacts, latestInvoiceContactsRequest,
                            latestInvoiceContactsResponseMessage.getService().getResultStatus().getCode(),
                            latestInvoiceContactsResponseMessage.getService().getResultStatus().getDescription());

                }
            }else {
                requestLatestInvoiceContacts = new RequestLatestInvoiceContacts(context, new RequestLatestInvoiceContactsTaskCompleteListener());
                new HamPayDialog(activity).showFailLatestInvoiceDialog(requestLatestInvoiceContacts, latestInvoiceContactsRequest,
                        Constants.LOCAL_ERROR_CODE,
                        getString(R.string.msg_fail_contacts_enabled));
            }
        }

        @Override
        public void onTaskPreRun() {
            hamPayDialog.showWaitingdDialog(prefs.getString(Constants.REGISTERED_USER_NAME, ""));
        }
    }
}



