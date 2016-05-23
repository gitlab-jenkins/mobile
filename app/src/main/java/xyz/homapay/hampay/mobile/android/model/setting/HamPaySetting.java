package xyz.homapay.hampay.mobile.android.model.setting;

/**
 * Created by amir on 5/23/16.
 */
public class HamPaySetting {

    private SettingStatus settingStatus;
    private String title;

    public SettingStatus getSettingStatus() {
        return settingStatus;
    }

    public void setSettingStatus(SettingStatus settingStatus) {
        this.settingStatus = settingStatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitlel(String title) {
        this.title = title;
    }
}
