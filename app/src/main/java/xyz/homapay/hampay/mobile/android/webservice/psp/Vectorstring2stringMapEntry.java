package xyz.homapay.hampay.mobile.android.webservice.psp;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import java.util.Hashtable;
import org.ksoap2.serialization.SoapObject;

import java.util.Vector;

public class Vectorstring2stringMapEntry extends Vector<string2stringMapEntry> implements KvmSerializable {
    
    
    public Vectorstring2stringMapEntry(){}
    
    public Vectorstring2stringMapEntry(SoapObject soapObject)
    {
        if (soapObject == null)
            return;
        if (soapObject != null){
            int size = soapObject.getPropertyCount();
            for (int i0=0;i0<size;i0++){
                Object obj = soapObject.getProperty(i0);
                if (obj!=null && obj.getClass().equals(SoapObject.class)){
                    SoapObject j0 =(SoapObject) soapObject.getProperty(i0);
                    string2stringMapEntry j1= new string2stringMapEntry(j0);
                    add(j1);
                }
            }
        }
    }
    @Override
    public Object getProperty(int arg0) {
        return this.get(arg0);
    }
    
    @Override
    public int getPropertyCount() {
        return this.size();
    }
    
    @Override
    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
        info.name = "string2stringMapEntry";
        info.type = string2stringMapEntry.class;
    }
    
    @Override
    public void setProperty(int arg0, Object arg1) {
    }
    
}
