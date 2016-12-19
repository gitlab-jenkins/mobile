package xyz.homapay.hampay.mobile.android.webservice.newpsp;



import org.ksoap2.serialization.*;
import java.util.Vector;
import java.util.Hashtable;


public class TWAArrayOfKeyValueOfstringstring extends Vector< TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring> implements KvmSerializable
{
    
    public TWAArrayOfKeyValueOfstringstring(){}
    
    public TWAArrayOfKeyValueOfstringstring(java.lang.Object inObj,TWAExtendedSoapSerializationEnvelope __envelope)
    {
        if (inObj == null)
            return;
        SoapObject soapObject=(SoapObject)inObj;
        int size = soapObject.getPropertyCount();
        for (int i0=0;i0< size;i0++)
        {
            java.lang.Object obj = soapObject.getProperty(i0);
            if (obj!=null && obj instanceof AttributeContainer)
            {
                AttributeContainer j =(AttributeContainer) soapObject.getProperty(i0);
                TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring j1= (TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring)__envelope.get(j,TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring.class);
                add(j1);
            }
        }
}
    
    @Override
    public java.lang.Object getProperty(int arg0) {
        return this.get(arg0)!=null?this.get(arg0):SoapPrimitive.NullNilElement;
    }
    
    @Override
    public int getPropertyCount() {
        return this.size();
    }
    
    @Override
    public void getPropertyInfo(int index, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info) {
        info.name = "KeyValueOfstringstring";
        info.type = TWAArrayOfKeyValueOfstringstring_KeyValueOfstringstring.class;
    	info.namespace= "http://schemas.microsoft.com/2003/10/Serialization/Arrays";
    }
    
    @Override
    public void setProperty(int arg0, java.lang.Object arg1) {
    }

}