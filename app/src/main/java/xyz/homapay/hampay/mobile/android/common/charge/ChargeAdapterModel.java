package xyz.homapay.hampay.mobile.android.common.charge;

/**
 * Created by mohammad on 1/11/17.
 */

public class ChargeAdapterModel {

    private int index;
    private String text;
    private boolean selected;

    public ChargeAdapterModel(int index, String text, boolean selected) {
        this.index = index;
        this.text = text;
        this.selected = selected;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
