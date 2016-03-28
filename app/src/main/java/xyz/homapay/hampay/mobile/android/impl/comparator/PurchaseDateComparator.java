package xyz.homapay.hampay.mobile.android.impl.comparator;

import java.util.Comparator;

import xyz.homapay.hampay.common.core.model.response.dto.PurchaseInfoDTO;

/**
 * Created by amir on 3/28/16.
 */
public class PurchaseDateComparator implements Comparator<PurchaseInfoDTO> {
    @Override
    public int compare(PurchaseInfoDTO purchaseInfoDTO1, PurchaseInfoDTO purchaseInfoDTO2) {
        return purchaseInfoDTO2.getCreatedBy().compareTo(purchaseInfoDTO1.getCreatedBy());
    }
}
