package xyz.homapay.hampay.mobile.android.impl.comparator;

import java.util.Comparator;

import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;

/**
 * Created by amir on 3/28/16.
 */
public class PurchaseAmountComparator implements Comparator<PurchaseInfoDTO> {
    @Override
    public int compare(PurchaseInfoDTO purchaseInfoDTO1, PurchaseInfoDTO purchaseInfoDTO2) {
//        return purchaseInfoDTO1.getAmount().compareTo(purchaseInfoDTO2.getAmount());
        return 1;
    }
}
