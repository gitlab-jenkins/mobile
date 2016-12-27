package xyz.homapay.hampay.mobile.android.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import xyz.homapay.hampay.common.core.model.response.dto.TransactionDTO;

/**
 * Created by mohammad on 12/27/16.
 */

public class TransactionStatusEnumDeserilizer implements JsonDeserializer<TransactionDTO.TransactionStatus>{
    @Override
    public TransactionDTO.TransactionStatus deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            JsonElement name = ((JsonObject) jsonElement).get("name");
            if (name == null)
                return null;
            TransactionDTO.TransactionStatus transactionStatus = TransactionDTO.TransactionStatus.valueOf(name.getAsString());
            JsonElement description = ((JsonObject) jsonElement).get("description");
            if (description != null)
                transactionStatus.setDescription(description.getAsString());
            return transactionStatus;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
