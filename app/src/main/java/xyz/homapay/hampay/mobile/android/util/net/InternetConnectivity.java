package xyz.homapay.hampay.mobile.android.util.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by amir on 10/17/16.
 */
public class InternetConnectivity {

    byte[] ipAddress = new byte[]{8, 8, 8, 8};
    InetAddress inetAddress;

    public InternetConnectionStatus getStatus() throws UnknownHostException {
        inetAddress = InetAddress.getByAddress(ipAddress);
        if (inetAddress.getHostName().toLowerCase().contains("google")){
            return InternetConnectionStatus.CONNECT;
        }else {
            return InternetConnectionStatus.DISCONNECT;
        }
    }

}
