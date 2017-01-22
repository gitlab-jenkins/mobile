package xyz.homapay.hampay.mobile.android.util;

import android.content.Context;

import xyz.homapay.hampay.common.common.Operator;
import xyz.homapay.hampay.common.core.model.enums.FundType;
import xyz.homapay.hampay.common.core.model.response.dto.FundDTO;
import xyz.homapay.hampay.mobile.android.R;

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

}
