package xyz.homapay.hampay.mobile.android.webservice.psp;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import java.util.Hashtable;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

public class string2stringMapEntry implements KvmSerializable {
    
    public String key;
    public String value;
    
    public string2stringMapEntry(){}
    
    public string2stringMapEntry(SoapObject soapObject)
    {
        if (soapObject == null)
            return;
        if (soapObject.hasProperty("key"))
        {
            Object obj = soapObject.getProperty("key");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                key = j.toString();
            }else if (obj!= null && obj instanceof String){
                key = (String) obj;
            }
        }
        if (soapObject.hasProperty("value"))
        {
            Object obj = soapObject.getProperty("value");
            if (obj != null && obj.getClass().equals(SoapPrimitive.class)){
                SoapPrimitive j =(SoapPrimitive) obj;
                value = j.toString();
            }else if (obj!= null && obj instanceof String){
                value = (String) obj;
            }
        }
    }
    @Override
    public Object getProperty(int arg0) {
        switch(arg0){
            case 0:
                return key;
            case 1:
                return value;
        }
        return null;
    }
    
    @Override
    public int getPropertyCount() {
        return 2;
    }
    
    @Override
    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
        switch(index){
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "key";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "value";
                break;
        }
    }
    

    
    @Override
    public void setProperty(int arg0, Object arg1) {
    }
    
}
