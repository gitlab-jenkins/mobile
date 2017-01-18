package xyz.homapay.hampay.mobile.android.webservice.psp.bills;


public interface MKAIServiceEvents {
    void Starting();

    void Completed(MKAOperationResult result);
}
