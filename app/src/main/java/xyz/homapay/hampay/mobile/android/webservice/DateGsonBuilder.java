package xyz.homapay.hampay.mobile.android.webservice;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by amir on 3/7/16.
 */
public class DateGsonBuilder {

    public DateGsonBuilder(){

    }

    public GsonBuilder getDatebuilder(){

        GsonBuilder builder = new GsonBuilder();
          builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });

        return builder;
    }

}
