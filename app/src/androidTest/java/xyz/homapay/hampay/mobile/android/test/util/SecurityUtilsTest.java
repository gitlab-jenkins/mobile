package xyz.homapay.hampay.mobile.android.test.util;

import junit.framework.TestCase;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import xyz.homapay.hampay.mobile.android.util.SecurityUtils;

/**
 * Created by amir on 10/10/15.
 */
public class SecurityUtilsTest extends TestCase {


    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void testGeneratePassword() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String password = SecurityUtils.getInstance().generatePassword("12345", "باقاله", "5388f34843269e07", "28beff00-fcca-48df-a097-347e82c5e204");

        assertEquals("91831c08c3f3a7b65953ff6f81b6af10dfb6d3100951dde7eb4fca1dca58d36f", password);
    }

}
