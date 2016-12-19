package xyz.homapay.hampay.mobile.android.model;

import xyz.homapay.hampay.mobile.android.webservice.newpsp.TWAArrayOfKeyValueOfstringstring;
import xyz.homapay.hampay.mobile.android.webservice.psp.CBUArrayOfKeyValueOfstringstring;

/**
 * Created by amir on 2/8/16.
 */
public class DoWorkInfo {

    private String userName;
    private String password;
    private String cellNumber;
    private byte langAByte;
    private boolean langABoolean;
    private CBUArrayOfKeyValueOfstringstring vectorstring2stringMapEntry;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    public byte getLangAByte() {
        return langAByte;
    }

    public void setLangAByte(byte langAByte) {
        this.langAByte = langAByte;
    }

    public boolean isLangABoolean() {
        return langABoolean;
    }

    public void setLangABoolean(boolean langABoolean) {
        this.langABoolean = langABoolean;
    }

    public CBUArrayOfKeyValueOfstringstring getVectorstring2stringMapEntry() {
        return vectorstring2stringMapEntry;
    }

    public void setVectorstring2stringMapEntry(CBUArrayOfKeyValueOfstringstring vectorstring2stringMapEntry) {
        this.vectorstring2stringMapEntry = vectorstring2stringMapEntry;
    }

}
