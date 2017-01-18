package xyz.homapay.hampay.mobile.android.webservice.psp;


import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.util.Hashtable;
import java.util.Vector;


public class CBUArrayOfKeyValueOfstringstring extends Vector< CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring> implements KvmSerializable
{

    public CBUArrayOfKeyValueOfstringstring(){}

    public CBUArrayOfKeyValueOfstringstring(Object inObj, CBUExtendedSoapSerializationEnvelope __envelope)
    {
        if (inObj == null)
            return;
        SoapObject soapObject=(SoapObject)inObj;
        int size = soapObject.getPropertyCount();
        for (int i0=0;i0< size;i0++)
        {
            Object obj = soapObject.getProperty(i0);
            if (obj!=null && obj instanceof AttributeContainer)
            {
                AttributeContainer j =(AttributeContainer) soapObject.getProperty(i0);
                CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring j1= (CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring)__envelope.get(j,CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring.class,false);
                add(j1);
            }
        }
    }

    @Override
    public Object getProperty(int arg0) {
        return this.get(arg0)!=null?this.get(arg0): SoapPrimitive.NullNilElement;
    }

    @Override
    public int getPropertyCount() {
        return this.size();
    }

    @Override
    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
        info.name = "KeyValueOfstringstring";
        info.type = CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring.class;
        info.namespace= "http://schemas.microsoft.com/2003/10/Serialization/Arrays";
    }

    @Override
    public void setProperty(int arg0, Object arg1) {
    }


}