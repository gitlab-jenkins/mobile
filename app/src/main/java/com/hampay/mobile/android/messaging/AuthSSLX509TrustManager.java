package com.hampay.mobile.android.messaging;

/**
 * @date: 1/13/15
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import javax.net.ssl.X509TrustManager;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
//import org.apache.log4j.Logger;


public class AuthSSLX509TrustManager implements X509TrustManager{



/**
 * <p>
 * AuthSSLX509TrustManager can be used to extend the default {@link javax.net.ssl.X509TrustManager}
 * with additional trust decisions.
 * </p>
 *
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 *
 * <p>
 * DISCLAIMER: HttpClient developers DO NOT actively support this component.
 * The component is provided as a reference material, which may be inappropriate
 * for use without additional customization.
 * </p>
 */


    private X509TrustManager defaultTrustManager = null;

    /** Log object for this class. */
//    static Logger log = Logger.getLogger(AuthSSLX509TrustManager.class.getName());

    /**
     * Constructor for AuthSSLX509TrustManager.
     */
    public AuthSSLX509TrustManager(final X509TrustManager defaultTrustManager) {
        super();
        if (defaultTrustManager == null) {
            throw new IllegalArgumentException("Trust manager may not be null");
        }
        this.defaultTrustManager = defaultTrustManager;
    }

    /**
     * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[],String authType)
     */
    public void checkClientTrusted(X509Certificate[] certificates,String authType) throws CertificateException {
        if (/*log.isInfoEnabled() && */certificates != null) {
            for (int c = 0; c < certificates.length; c++) {
                X509Certificate cert = certificates[c];
//                log.info(" Client certificate " + (c + 1) + ":");
//                log.info("  Subject DN: " + cert.getSubjectDN());
//                log.info("  Signature Algorithm: " + cert.getSigAlgName());
//                log.info("  Valid from: " + cert.getNotBefore() );
//                log.info("  Valid until: " + cert.getNotAfter());
//                log.info("  Issuer: " + cert.getIssuerDN());
            }
        }
        defaultTrustManager.checkClientTrusted(certificates,authType);
    }

    /**
     * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[],String authType)
     */
    public void checkServerTrusted(X509Certificate[] certificates,String authType) throws CertificateException {
        if (/*log.isInfoEnabled() &&*/ certificates != null) {
            for (int c = 0; c < certificates.length; c++) {
                X509Certificate cert = certificates[c];
//                log.info(" Server certificate " + (c + 1) + ":");
//                log.info("  Subject DN: " + cert.getSubjectDN());
//                log.info("  Signature Algorithm: " + cert.getSigAlgName());
//                log.info("  Valid from: " + cert.getNotBefore() );
//                log.info("  Valid until: " + cert.getNotAfter());
//                log.info("  Issuer: " + cert.getIssuerDN());
                try {
                    cert.verify(cert.getPublicKey());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    throw new CertificateException(e.getMessage(), e.getCause());
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                    throw new CertificateException(e.getMessage(), e.getCause());
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                    throw new CertificateException(e.getMessage(), e.getCause());
                } catch (SignatureException e) {
                    e.printStackTrace();
                    throw new CertificateException(e.getMessage(), e.getCause());
                }
            }
        }
        defaultTrustManager.checkServerTrusted(certificates,authType);
    }

    /**
     * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
     */
    public X509Certificate[] getAcceptedIssuers() {
        return this.defaultTrustManager.getAcceptedIssuers();
    }
}



