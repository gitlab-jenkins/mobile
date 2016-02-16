package xyz.homapay.hampay.mobile.android.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import xyz.homapay.hampay.common.core.model.dto.ContactDTO;

/**
 * Created by amir on 2/16/16.
 */
public class UserContacts {

    private Context context;

    public UserContacts(Context context){
        this.context = context;
    }

    public List<ContactDTO> read(){
        List<ContactDTO> contactDTOs = new ArrayList<>();
        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        String cellNumber;
        while (phones.moveToNext()) {
            cellNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (cellNumber.trim().replace(" ", "").startsWith("00989")
                    || cellNumber.trim().replace(" ", "").startsWith("+989")
                    || cellNumber.trim().replace(" ", "").startsWith("09")) {
                ContactDTO contactDTO = new ContactDTO();
                contactDTO.setCellNumber(cellNumber);
                contactDTOs.add(contactDTO);
            }
        }
        phones.close();
        return contactDTOs;
    }

}
