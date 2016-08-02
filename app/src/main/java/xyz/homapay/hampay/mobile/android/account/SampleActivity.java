package xyz.homapay.hampay.mobile.android.account;



import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import xyz.homapay.hampay.mobile.android.HamPayApplication;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.model.AppState;

public class SampleActivity extends AppCompatActivity {
	private Intent serviceIntent;


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
		setContentView(R.layout.main_activity);
		((Button) findViewById(R.id.addContactButton)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				addNewAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
//				ContactsManager.addContact(SampleActivity.this, new HamPayContact("Hooman", "Amini"));
				/*if (serviceIntent == null)
					serviceIntent = new Intent(SampleActivity.this, ContactUpdateService.class);
				stopService(serviceIntent);
				startService(serviceIntent);
				*/
			}
		});
	}
	
	private void addNewAccount(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = AccountManager.get(this).addAccount(accountType, authTokenType, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bnd = future.getResult();
                    Log.i("Account was created");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, null);
    }
	
	

}
