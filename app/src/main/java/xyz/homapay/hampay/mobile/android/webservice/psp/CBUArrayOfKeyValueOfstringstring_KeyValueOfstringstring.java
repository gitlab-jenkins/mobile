package xyz.homapay.hampay.mobile.android.webservice.psp;

import org.ksoap2.serialization.AttributeContainer;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.util.Hashtable;

public class CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring extends AttributeContainer implements KvmSerializable
{

    
    public String Key;
    
    public String Value;

    public CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring()
    {
    }

    public CBUArrayOfKeyValueOfstringstring_KeyValueOfstringstring(Object paramObj, CBUExtendedSoapSerializationEnvelope __envelope)
    {
	    
	    if (paramObj == null)
            return;
        AttributeContainer inObj=(AttributeContainer)paramObj;


        if(inObj instanceof SoapObject)
        {
            SoapObject soapObject=(SoapObject)inObj;
            int size = soapObject.getPropertyCount();
            for (int i0=0;i0< size;i0++)
            {
                //if you have compilation error here, please use a ksoap2.jar and ExKsoap2.jar from libs folder (in the generated zip file)
                PropertyInfo info=soapObject.getPropertyInfo(i0);
                Object obj = info.getValue();
                if (info.name.equals("Key"))
                {
                    if(obj!=null)
                    {
        
                        if (obj.getClass().equals(SoapPrimitive.class))
                        {
                            SoapPrimitive j =(SoapPrimitive) obj;
                            if(j.toString()!=null)
                            {
                                this.Key = j.toString();
                            }
                        }
                        else if (obj instanceof String){
                            this.Key = (String)obj;
                        }
                    }
                    continue;
                }
                if (info.name.equals("Value"))
                {
                    if(obj!=null)
                    {
        
                        if (obj.getClass().equals(SoapPrimitive.class))
                        {
                            SoapPrimitive j =(SoapPrimitive) obj;
                            if(j.toString()!=null)
                            {
                                this.Value = j.toString();
                            }
                        }
                        else if (obj instanceof String){
                            this.Value = (String)obj;
                        }
                    }
                    continue;
                }

            }

        }



    }

    @Override
    public Object getProperty(int propertyIndex) {
        if(propertyIndex==0)
        {
            return this.Key!=null?this.Key: SoapPrimitive.NullNilElement;
        }
        if(propertyIndex==1)
        {
            return this.Value!=null?this.Value: SoapPrimitive.NullNilElement;
        }
        return null;
    }


    @Override
    public int getPropertyCount() {
        return 2;
    }

    @Override
    public void getPropertyInfo(int propertyIndex, @SuppressWarnings("rawtypes") Hashtable arg1, PropertyInfo info)
    {
        if(propertyIndex==0)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "Key";
            info.namespace= "http://schemas.microsoft.com/2003/10/Serialization/Arrays";
        }
        if(propertyIndex==1)
        {
            info.type = PropertyInfo.STRING_CLASS;
            info.name = "Value";
            info.namespace= "http://schemas.microsoft.com/2003/10/Serialization/Arrays";
        }
    }
    
    @Override
    public void setProperty(int arg0, Object arg1)
    {
    }

    
}

