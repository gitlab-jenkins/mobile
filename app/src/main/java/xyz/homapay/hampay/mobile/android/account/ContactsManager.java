package xyz.homapay.hampay.mobile.android.account;

import java.util.ArrayList;
import java.util.List;


import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Entity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.Settings;
import android.webkit.WebChromeClient.CustomViewCallback;

public class ContactsManager {
	//	private static String MIMETYPE = "vnd.android.cursor.item/com.sample.profile";
	private static String MIMETYPE = "vnd.android.cursor.item/com.hampay.mobile";

	public static void addContact(Context context, HamPayContact contact) {
		ContentResolver resolver = context.getContentResolver();
//		resolver.delete(RawContacts.CONTENT_URI, RawContacts.ACCOUNT_TYPE + " = ?", new String[]{AccountGeneral.ACCOUNT_TYPE});

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		ops.add(ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(RawContacts.CONTENT_URI, true))
				.withValue(RawContacts.ACCOUNT_NAME, AccountGeneral.ACCOUNT_NAME)
				.withValue(RawContacts.ACCOUNT_TYPE, AccountGeneral.ACCOUNT_TYPE)
						//.withValue(RawContacts.SOURCE_ID, 12345)
						//.withValue(RawContacts.AGGREGATION_MODE, RawContacts.AGGREGATION_MODE_DISABLED)
				.build());

		ops.add(ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(Settings.CONTENT_URI, true))
				.withValue(RawContacts.ACCOUNT_NAME, AccountGeneral.ACCOUNT_NAME)
				.withValue(RawContacts.ACCOUNT_TYPE, AccountGeneral.ACCOUNT_TYPE)
				.withValue(Settings.UNGROUPED_VISIBLE, 1)
				.build());

		ops.add(ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(Data.CONTENT_URI, true))
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
				.withValue(StructuredName.GIVEN_NAME, contact.name)
				.withValue(StructuredName.FAMILY_NAME, contact.lastName)
				.build());

		ops.add(ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(Data.CONTENT_URI, true))
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
//				.withValue(Phone.NUMBER, "09129479928")
				.withValue(Phone.NUMBER, contact.phone)
				.build());


		ops.add(ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(Data.CONTENT_URI, true))
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
//	             .withValue(Email.DATA, "ahooman@email.com")
				.withValue(Email.DATA, contact.mail)
				.build());


		ops.add(ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(Data.CONTENT_URI, true))
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(Data.MIMETYPE, MIMETYPE)
				.withValue(Data.DATA1, 1001)
//				.withValue(Data.DATA2, "Hooman")
//				.withValue(Data.DATA3, "Amini")
				.withValue(Data.DATA2, contact.name)
				.withValue(Data.DATA3, contact.lastName)
				.build());
		try {
			ContentProviderResult[] results = resolver.applyBatch(ContactsContract.AUTHORITY, ops);
			if (results.length == 0)
				;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Uri addCallerIsSyncAdapterParameter(Uri uri, boolean isSyncOperation) {
		if (isSyncOperation) {
			return uri.buildUpon()
					.appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true")
					.build();
		}
		return uri;
	}

	public static List<HamPayContact> getHamPayContacts() {
		return null;
	}


	public static void updateHamPayContact(Context context, String name) {
		int id = -1;
		Cursor cursor = context.getContentResolver().query(Data.CONTENT_URI, new String[] { Data.RAW_CONTACT_ID, Data.DISPLAY_NAME, Data.MIMETYPE, Data.CONTACT_ID },
				StructuredName.DISPLAY_NAME + "= ?",
				new String[] {name}, null);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				id = cursor.getInt(0);
				Log.i(cursor.getString(0));
				Log.i(cursor.getString(1));
				Log.i(cursor.getString(2));
				Log.i(cursor.getString(3));

			} while (cursor.moveToNext());
		}
		if (id != -1) {
			ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

			ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
					.withValue(Data.RAW_CONTACT_ID, id)
					.withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
//				.withValue(Email.DATA, "ahooman@gmail.com")
					.withValue(Email.DATA, "ahooman@gmail.com")
					.build());

			ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
					.withValue(Data.RAW_CONTACT_ID, id)
					.withValue(Data.MIMETYPE, MIMETYPE)
					.withValue(Data.DATA1, "profile")
					.withValue(Data.DATA2, "profile")
					.withValue(Data.DATA3, "profile")
					.build());

			try {
				context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			Log.i("id not found");
		}


	}

}