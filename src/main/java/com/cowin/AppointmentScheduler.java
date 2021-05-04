package com.cowin;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Logger;

public class AppointmentScheduler {
    private static Logger LOGGER = Logger.getLogger(AppointmentScheduler.class.getName());
    private static final String GENERATE_OTP_API = "https://cdn-api.co-vin.in/api/v2/auth/public/generateOTP";
    private static final String CONFIRM_OTP_API = "https://cdn-api.co-vin.in/api/v2/auth/confirmOTP";

    public static void generateOtp(boolean confirmOtp) throws Exception {
        String generateOtpInput = "{\"mobile\":\"9886136498\"}";
        CloseableHttpResponse response = submitRequest(generateOtpInput, GENERATE_OTP_API);

        if (response.getStatusLine().getStatusCode() == 200) {
            //Successful generation of OTP
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String responseStr = parseResponse(responseEntity);

                //parse the response for transaction Id
                JSONObject responsePayload = new JSONObject(responseStr);
                String transactionID = (String) responsePayload.get("txnId");

                if (confirmOtp) {
                    confirmOtpRequest(responseStr, responsePayload, transactionID);
                }
            }
        }
    }

    private static void confirmOtpRequest(String responseStr, JSONObject responsePayload, String transactionID) throws IOException {
        Scanner sc = new Scanner(System.in);    //System.in is a standard input stream
        System.out.print("*******************");
        System.out.print("Enter Your OTP - ");
        int otp = sc.nextInt();
        //Initiate the confirm OTP request
        String confirmOtpInput = "{\"otp\":\"" + otp + "\", \"txnId\":\"" + transactionID + "\"}";
        CloseableHttpResponse confirmOtpResponse = submitRequest(confirmOtpInput, CONFIRM_OTP_API);

        if (confirmOtpResponse.getStatusLine().getStatusCode() == 200) {
            HttpEntity confirmOtpResponseEntity = confirmOtpResponse.getEntity();
            if (confirmOtpResponseEntity != null) {
                String response2Str = parseResponse(confirmOtpResponseEntity);

                //fetch the token
                JSONObject response2Payload = new JSONObject(responseStr);
                String token = (String) responsePayload.get("token");
            }
        }
    }

    private static String parseResponse(HttpEntity responseEntity) throws IOException {
        InputStream instream = responseEntity.getContent();
        byte[] bytes = IOUtils.toByteArray(instream);
        String responseStr = new String(bytes, "UTF-8");
        instream.close();
        return responseStr;
    }

    private static CloseableHttpResponse submitRequest(String generateOtpInput, String api) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(api);
        StringEntity entity = new StringEntity(generateOtpInput);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);
        return response;
    }
}
