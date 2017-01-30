package xyz.homapay.hampay.mobile.android.common.friendsinvitation;

import com.github.tamir7.contacts.Contact;

/**
 * Created by mohammad on 1/30/17.
 */

public class FriendsObject {

    private Contact contact;
    private boolean selected;
    private String normalizedNumber;

    public FriendsObject(Contact contact, String normalizedNumber, boolean selected) {
        this.contact = contact;
        this.selected = selected;
        this.normalizedNumber = normalizedNumber;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getNormalizedNumber() {
        return normalizedNumber;
    }

    public void setNormalizedNumber(String normalizedNumber) {
        this.normalizedNumber = normalizedNumber;
    }
}
