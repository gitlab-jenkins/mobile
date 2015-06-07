package com.hampay.mobile.android.webservice;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hampay.common.common.request.RequestHeader;
import com.hampay.common.common.request.RequestMessage;
import com.hampay.common.common.response.ResponseMessage;
import com.hampay.common.core.model.request.BankListRequest;
import com.hampay.common.core.model.response.BankListResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by amir on 6/6/15.
 */
public class WebServices  {

    public WebServices(){}

    public void testBankList1() {

        HttpURLConnection connection = null;
        try {

            RequestHeader header = new RequestHeader();
            header.setAuthToken("008ewe");
            header.setVersion("1.0-PA");

            RequestMessage<BankListRequest> message = new RequestMessage<BankListRequest>();
            message.setRequestHeader(header);
            BankListRequest request = new BankListRequest();
            request.setRequestUUID("1234");
            message.setService(request);

            Type requestType = new com.google.gson.reflect.TypeToken<RequestMessage<BankListRequest>>() {}.getType();
            String jsonRequest = new Gson().toJson(message, requestType);

            URL url = new URL("http://176.58.104.158:9093/banks");

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(jsonRequest.getBytes());
            outputStream.flush();

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            Gson gson = new Gson();
            ResponseMessage<BankListResponse> responseMessage = gson.fromJson(reader, new TypeToken<ResponseMessage<BankListResponse>>() {
            }.getType());

            if( responseMessage != null && responseMessage.getService() != null ) {
                System.out.println("Response requestUUID : " + responseMessage.getService().getRequestUUID());
                System.out.println("Response bank list size : " + responseMessage.getService().getBanks().size());
            } else {
                System.out.println("Response is null...");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }

    }

}
