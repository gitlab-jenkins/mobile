package com.hampay.mobile.android.messaging;

import org.apache.http.conn.ssl.SSLSocketFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;

//import javax.security.cert.X509Certificate;


/**
 * @author Yelin Eshmali
 */
public class RestSSLSocketFactory extends SSLSocketFactory {
    private final String TAG = getClass().getName();
	SSLContext sslContext = SSLContext.getInstance("TLS");

    public RestSSLSocketFactory(KeyStore truststore)
            throws NoSuchAlgorithmException, KeyManagementException,
            KeyStoreException, UnrecoverableKeyException {
        super(truststore);

//        TrustManager tm = new X509TrustManager() {
//            public void checkClientTrusted(X509Certificate[] chain,
//                                           String authType) throws CertificateException {
//            }
//
//            public void checkServerTrusted(X509Certificate[] chain,
//                                           String authType) throws CertificateException {
//            }
//
//            public X509Certificate[] getAcceptedIssuers() {
//                return null;
//            }
//        };
        TrustManager[] tm = createTrustManagers(truststore);

//        sslContext.init(null, new TrustManager[] { tm }, null);
        sslContext.init(null, tm , null);
    }

//	public RestSSLSocketFactory(final X509Certificate ca, SSLContext context,final KeyStore truststore)
//			throws NoSuchAlgorithmException, KeyManagementException,
//			KeyStoreException, UnrecoverableKeyException {
//		super(truststore);
//        sslContext = context;
//        TrustManagerFactory tmf = TrustManagerFactory
//                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
//        tmf.init(truststore);
//
//        // build our own trust manager
//        X509TrustManager tm = new X509TrustManager() {
//            public X509Certificate[] getAcceptedIssuers() {
//                // nothing to do
//                return new X509Certificate[0];
//            }
//
//            @Override
//            public void checkClientTrusted(final X509Certificate[] chain,
//                                           final String authType)
//                    throws CertificateException {
//                // nothing to do
//            }
//
//            @Override
//            public void checkServerTrusted(final X509Certificate[] chain,
//                                           final String authType) throws CertificateException {
//                if( chain != null && chain.length > 0 ) {
//                    X509Certificate certificate = chain[0];
//                    try {
//                        certificate.verify(ca.getPublicKey());
//                    } catch (NoSuchAlgorithmException e) {
//                        e.printStackTrace();
//                        throw new CertificateException(e);
//                    } catch (InvalidKeyException e) {
//                        e.printStackTrace();
//                        throw new CertificateException(e);
//                    } catch (NoSuchProviderException e) {
//                        e.printStackTrace();
//                        throw new CertificateException(e);
//                    } catch (SignatureException e) {
//                        e.printStackTrace();
//                        throw new CertificateException(e);
//                    }
//
//                }
//            }
//        };
//        sslContext.init(null, new X509TrustManager[]{tm}, null);
//	}

//    @Override
//    public Socket connectSocket(Socket sock, String host, int port, InetAddress
//            localAddress, int localPort,
//                                HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException
//    {
//        int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
//        int soTimeout = HttpConnectionParams.getSoTimeout(params);
//        InetSocketAddress remoteAddress = new InetSocketAddress(host, port);
//        SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock : createSocket());
//        if ((localAddress != null) || (localPort > 0)) {
//            // we need to bind explicitly
//            if (localPort < 0) {
//                localPort = 0; // indicates �any�
//            }
//            InetSocketAddress isa = new InetSocketAddress(localAddress, localPort);
//            sslsock.bind(isa);
//        }
//        sslsock.connect(remoteAddress, connTimeout);
//        sslsock.setSoTimeout(soTimeout);
//        return sslsock;
//    }

	@Override
	public Socket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException {
		return sslContext.getSocketFactory().createSocket(socket, host, port,
				autoClose);
	}

	@Override
	public Socket createSocket() throws IOException {
		return sslContext.getSocketFactory().createSocket();
	}

    public javax.net.ssl.SSLSocketFactory getSSLSocketFactory(){
        return sslContext.getSocketFactory();
    }

    private static TrustManager[] createTrustManagers(final KeyStore keystore)
            throws KeyStoreException, NoSuchAlgorithmException
        {
            if (keystore == null) {
                throw new IllegalArgumentException("Keystore may not be null");
            }
//            LOG.debug("Initializing trust manager");
            TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
            tmfactory.init(keystore);
            TrustManager[] trustmanagers = tmfactory.getTrustManagers();
            for (int i = 0; i < trustmanagers.length; i++) {
                if (trustmanagers[i] instanceof X509TrustManager) {
                    trustmanagers[i] = new AuthSSLX509TrustManager(
                        (X509TrustManager)trustmanagers[i]);
                }
            }
            return trustmanagers;
        }
}