package xyz.homapay.hampay.mobile.android.impl.comparator;

import java.util.Comparator;

import xyz.homapay.hampay.common.core.model.response.dto.PaymentInfoDTO;

/**
 * Created by amir on 3/28/16.
 */
public class PaymentAmountComparator implements Comparator<PaymentInfoDTO> {
    @Override
    public int compare(PaymentInfoDTO paymentInfoDTO1, PaymentInfoDTO paymentInfoDTO2) {
//        return paymentInfoDTO1.getAmount().compareTo(paymentInfoDTO2.getAmount());
        return 1;
    }
}
