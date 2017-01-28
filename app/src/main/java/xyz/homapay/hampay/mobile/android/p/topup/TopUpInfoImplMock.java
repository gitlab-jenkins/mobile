package xyz.homapay.hampay.mobile.android.p.topup;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

import xyz.homapay.hampay.common.common.ChargePackage;
import xyz.homapay.hampay.common.common.Operator;
import xyz.homapay.hampay.common.common.response.ResponseMessage;
import xyz.homapay.hampay.common.core.model.response.TopUpInfoResponse;
import xyz.homapay.hampay.mobile.android.m.common.ModelLayer;
import xyz.homapay.hampay.mobile.android.p.common.Presenter;

/**
 * Created by mohammad on 1/11/17.
 */

public class TopUpInfoImplMock extends Presenter<TopUpInfoView> implements TopUpInfo {

    public TopUpInfoImplMock(ModelLayer modelLayer, TopUpInfoView view) {
        super(modelLayer, view);
    }

    @Override
    public void onKeyExchangeDone() {

    }

    @Override
    public void onKeyExchangeError() {

    }

    @Override
    public void getInfo(Operator operator) {
        try {
            view.showProgress();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                TopUpInfoResponse response = new TopUpInfoResponse();
                List<xyz.homapay.hampay.common.common.TopUpInfo> lstInfo = new ArrayList<>();

                xyz.homapay.hampay.common.common.TopUpInfo info1 = new xyz.homapay.hampay.common.common.TopUpInfo();
                info1.setChargeType("مستقیم");
                info1.setDescription("شارژ به صورت مستقیم");
                ChargePackage chargePackage1 = new ChargePackage();
                chargePackage1.setAmount(2000);
                ChargePackage chargePackage2 = new ChargePackage();
                chargePackage2.setAmount(5000);
                ChargePackage chargePackage3 = new ChargePackage();
                chargePackage3.setAmount(10000);
                ArrayList<ChargePackage> charges = new ArrayList<>();
                charges.add(chargePackage1);
                charges.add(chargePackage2);
                charges.add(chargePackage3);
                info1.setChargePackages(charges);
                lstInfo.add(info1);

                xyz.homapay.hampay.common.common.TopUpInfo info2 = new xyz.homapay.hampay.common.common.TopUpInfo();
                info2.setChargeType("شگفت انگیز");
                info2.setDescription("شارژ به صورت شگفت انگیز");
                ChargePackage chargePackage4 = new ChargePackage();
                chargePackage4.setAmount(1000);
                ChargePackage chargePackage5 = new ChargePackage();
                chargePackage5.setAmount(3000);
                ArrayList<ChargePackage> charges2 = new ArrayList<>();
                charges2.add(chargePackage4);
                charges2.add(chargePackage5);
                info2.setChargePackages(charges2);
                lstInfo.add(info2);

                response.setTopUpInfoList(lstInfo);

                ResponseMessage<TopUpInfoResponse> responseResponseMessage = new ResponseMessage<>(response);
                view.cancelProgress();
                view.onInfoLoaded(true, responseResponseMessage, "ok");
            }, 2000);

        } catch (Exception e) {
            e.printStackTrace();
            view.onError();
        }
    }

}
