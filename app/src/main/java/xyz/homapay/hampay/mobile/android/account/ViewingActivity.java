package xyz.homapay.hampay.mobile.android.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;

import xyz.homapay.hampay.mobile.android.R;

public class ViewingActivity extends Activity {


	static long noexclude;
	String last_number;
	long last_time;
	String number = "";

	String n;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_activity);

		Uri uri = getIntent().getData();

		Cursor phonesCursor = getContentResolver().query(uri, null, null, null,
				ContactsContract.CommonDataKinds.Phone.IS_PRIMARY + " DESC");
		if (phonesCursor != null) {
			if (phonesCursor.moveToNext()) {
				String id = phonesCursor.getString(phonesCursor
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
				Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
				while (pCur.moveToNext()) {
					n = pCur.getString(pCur
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					if (TextUtils.isEmpty(n)) continue;
					if (!number.equals("")) number = number + "&";
					n = PhoneNumberUtils.stripSeparators(n);
					//number = number + searchReplaceNumber(getApplicationContext(), n);
				}
				pCur.close();
			}
			phonesCursor.close();

			Log.e("URL", n);

		}
	}

//
//	static private String searchReplaceNumber(Context context,String number) {
//		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//		String pattern = sp.getString(Settings.PREF_SEARCH, Settings.DEFAULT_SEARCH);
//		// Comma should be safe as separator.
//		String[] split = pattern.split(",");
//		// We need exactly 2 parts: search and replace. Otherwise
//		// we just return the current number.
//		if (split.length != 2)
//			return number;
//
//		String modNumber = split[1];
//
//		try {
//			// Compiles the regular expression. This could be done
//			// when the user modify the pattern... TODO Optimize
//			// this, only compile once.
//			Pattern p = Pattern.compile(split[0]);
//			Matcher m = p.matcher(number);
//			// Main loop of the function.
//			if (m.matches()) {
//				for (int i = 0; i < m.groupCount() + 1; i++) {
//					String r = m.group(i);
//					if (r != null) {
//						modNumber = modNumber.replace("\\" + i, r);
//					}
//				}
//			}
//			// If the modified number is the same as the replacement
//			// value, we guess that the user typed a bad replacement
//			// value and we use the original number.
//			if (modNumber.equals(split[1])) {
//				modNumber = number;
//			}
//		} catch (PatternSyntaxException e) {
//			// Wrong pattern syntax. Give back the original number.
//			modNumber = number;
//		}
//
//		// Returns the modified number.
//		return modNumber;
//	}
//

}
