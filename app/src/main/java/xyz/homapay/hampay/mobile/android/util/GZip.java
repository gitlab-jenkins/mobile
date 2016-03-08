package xyz.homapay.hampay.mobile.android.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by amir on 3/8/16.
 */
public class GZip {

    private byte[] data;

    public GZip(byte[] data){
        this.data = data;
    }


    public byte[] compress(){
        GZIPOutputStream gzipOutputStream = null;
        try {
            ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
            gzipOutputStream = new GZIPOutputStream(byteArrayOS);
            gzipOutputStream.write(data);
            gzipOutputStream.flush();
            gzipOutputStream.close();
            gzipOutputStream = null;
            return byteArrayOS.toByteArray();
        } catch (Exception e) {
        } finally {
            if (gzipOutputStream != null) {
                try { gzipOutputStream.close(); } catch (Exception ignored) {}
            }
        }
        return null;
    }

    public String decompress() throws IOException {
        final int BUFFER_SIZE = 32;
        ByteArrayInputStream is = new ByteArrayInputStream(data);
        GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
        StringBuilder stringBuilder = new StringBuilder();
        byte[] data = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = gis.read(data)) != -1) {
            stringBuilder.append(new String(data, 0, bytesRead));
        }
        gis.close();
        is.close();
        return stringBuilder.toString();
    }

}
