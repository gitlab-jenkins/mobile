package com.hampay.mobile.android.serialize;

import com.hampay.common.core.model.response.dto.UserProfileDTO;

import java.io.Serializable;

/**
 * Created by amir on 7/10/15.
 */
public class UserProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fullName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

//    private UserProfileDTO userProfileDTO;
//
//    public UserProfileDTO getUserProfileDTO() {
//        return userProfileDTO;
//    }
//
//    public void setUserProfileDTO(UserProfileDTO userProfileDTO) {
//        this.userProfileDTO = userProfileDTO;
//    }
}
