package xyz.homapay.hampay.mobile.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import xyz.homapay.hampay.common.common.Operator;
import xyz.homapay.hampay.common.core.model.enums.FundType;
import xyz.homapay.hampay.common.core.model.response.dto.FundDTO;
import xyz.homapay.hampay.mobile.android.R;
import xyz.homapay.hampay.mobile.android.activity.HamPayLoginActivity;
import xyz.homapay.hampay.mobile.android.fragment.pending.FrgPendingRequests;

/**
 * Created by mohammad on 1/7/17.
 */

public class AppManager {

    public static final String getAuthToken(final Context ctx) {
        return AppSettings.getString(ctx, Constants.LOGIN_TOKEN_ID, "");
    }

    public static final void setAuthToken(final Context ctx, String authToken) {
        AppSettings.setValue(ctx, Constants.LOGIN_TOKEN_ID, authToken);
    }

    public static final int getOperatorImageResource(Operator operator) {
        int resId;
        switch (operator) {
            case MCI:
                resId = R.mipmap.hamrah_active;
                break;
            case MTN:
                resId = R.mipmap.irancell_active;
                break;
            case RAYTEL:
                resId = R.mipmap.rightel_active;
                break;
            default:
                resId = R.mipmap.hamrah_active;
                break;
        }
        return resId;
    }

    public static final String amountFixer(long amount) {
        CurrencyFormatter currencyFormatter = new CurrencyFormatter();
        PersianEnglishDigit persianEnglishDigit = new PersianEnglishDigit();
        return persianEnglishDigit.E2P(currencyFormatter.format(amount));
    }

    public static final FundType extractFundTypeFromPaymentType(FundDTO.PaymentType paymentType) {
        FundType fundType = null;
        switch (paymentType) {
            case PAYMENT:
                fundType = FundType.PAYMENT;
                break;
            case PURCHASE:
                fundType = FundType.PURCHASE;
                break;
            case UTILITY_BILL:
                fundType = FundType.UTILITY_BILL;
                break;
            case TOP_UP:
                fundType = FundType.TOP_UP;
                break;
        }
        return fundType;
    }

    public static final FundType getFundType(FrgPendingRequests.FilterState state) {
        FundType type = null;
        switch (state) {
            case ALL:
                type = FundType.ALL;
                break;
            case COMMERCIAL:
                type = FundType.COMMERCIAL;
                break;
            case PERSONAL:
                type = FundType.INDIVIDUAL;
                break;
        }
        return type;
    }

    public static final void logOut(Context ctx) {
        try {
            setAuthToken(ctx, "");
            Intent intent = new Intent();
            intent.setClass(ctx, HamPayLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (ctx instanceof Activity) {
                Activity activity = (Activity) ctx;
                activity.finish();
                activity.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final long getMobileTimeout(Context ctx) {
        return AppSettings.getLong(ctx, Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
    }

    public static final void setMobileTimeout(Context ctx) {
        AppSettings.setValue(ctx, Constants.MOBILE_TIME_OUT, System.currentTimeMillis());
    }

    public static final void setRegisterIdToken(Context ctx, String userIdToken) {
        AppSettings.setValue(ctx, Constants.REGISTERED_USER_ID_TOKEN, userIdToken);
    }

    public static final String getRegisterIdToken(Context ctx) {
        return AppSettings.getString(ctx, Constants.REGISTERED_USER_ID_TOKEN, "");
    }

    public static void setRegisterUserName(Context ctx, String userName) {
        AppSettings.setValue(ctx, Constants.REGISTERED_USER_NAME, userName);
    }

    public static final String getRegisterUserName(Context ctx) {
        return AppSettings.getString(ctx, Constants.REGISTERED_USER_NAME, "");
    }

    public static void setRegisterUserEmail(Context ctx, String userEmail) {
        AppSettings.setValue(ctx, Constants.REGISTERED_USER_EMAIL, userEmail);
    }

    public static final String getRegisterUserEmail(Context ctx) {
        return AppSettings.getString(ctx, Constants.REGISTERED_USER_EMAIL, "");
    }

}
