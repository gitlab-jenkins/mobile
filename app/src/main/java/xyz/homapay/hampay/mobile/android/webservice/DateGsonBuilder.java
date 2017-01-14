package xyz.homapay.hampay.mobile.android.webservice;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

import xyz.homapay.hampay.common.core.model.response.dto.TransactionDTO;
import xyz.homapay.hampay.mobile.android.util.TransactionStatusEnumDeserilizer;

/**
 * Created by amir on 3/7/16.
 */
public class DateGsonBuilder {

    public DateGsonBuilder() {

    }

    public GsonBuilder getDatebuilder() {

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(json.getAsJsonPrimitive().getAsLong()));
        builder.registerTypeAdapter(TransactionDTO.TransactionStatus.class, new TransactionStatusEnumDeserilizer());

        return builder;
    }

}
