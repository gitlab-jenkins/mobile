package com.hampay.mobile.android.model;

/**
 * Created by amir on 7/23/15.
 */
public class AppSlideInfo {

    public int getWallImageRes() {
        return wallImageRes;
    }

    public void setWallImageRes(int wallImageRes) {
        this.wallImageRes = wallImageRes;
    }

    private int wallImageRes;
    private int imageRes;
    private int imageDescription;

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public int getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(int imageDescription) {
        this.imageDescription = imageDescription;
    }

}
