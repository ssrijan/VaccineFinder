package com.cowin;


import com.cowin.notification.EmailNotification;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Component
public class CowinVaccineFinder {
    private static Logger LOGGER = Logger.getLogger(CowinVaccineFinder.class.getName());
    private static final String PWD = "yyyy";
    private static final String FROM_SENDER = "xxxx@gmail.com";
    private static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws Exception {
        // AppointmentScheduler.generateOtp(true);
        //AppointmentScheduler.schedule();
        fetchResponse();
    }

    @Scheduled(cron = "0/45 * * * * ?")
    public static void fetchResponse() throws Exception {
        String pattern = "dd-MM-YYYY";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(new Date());

        LOGGER.info(String.format("Fetching CoWin details at - %s for BANGALORE from Date - %s"  , new Date(), date));
        //JSONObject json = new JSONObject(IOUtils.toString(new URL("https://cdn-api.co-vin.in/api/v2/appointment/sessions/calendarByDistrict?district_id=294&date=" + date), Charset.forName("UTF-8")));
        JSONObject json = new JSONObject(IOUtils.toString(new URL("https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/calendarByDistrict?district_id=294&date=" + date), Charset.forName("UTF-8")));
        JSONArray centers = ((JSONArray) json.get("centers"));
        for (Object center : centers) {
            JSONObject jsonObject = (JSONObject) center;
            JSONArray sesionList = (JSONArray) jsonObject.get("sessions");
            for (Object o : sesionList) {
                JSONObject sessionObj = (JSONObject) o;

                try {
                    Integer ageLimit = (Integer) sessionObj.get("min_age_limit");
                    Integer availableCapacity = (Integer) sessionObj.get("available_capacity");
                    String feeType = (String) jsonObject.get("fee_type");
                    String hospitalName = (String) jsonObject.get("name");

                    if (ageLimit == 18 && availableCapacity > 0 && !hospitalName.contains("Newbagalur Layout UPHC")) { //&& !feeType.equalsIgnoreCase("Free")
                        LOGGER.info("******************************************************************");
                        LOGGER.info("************************ALERT WE HAVE A SLOT**********************");
                        LOGGER.info("PinCode - " + jsonObject.get("pincode"));
                        LOGGER.info("Hospital Name - " + jsonObject.get("name"));
                        LOGGER.info("Fee Type - " + jsonObject.get("fee_type"));
                        LOGGER.info("Date - " + sessionObj.get("date"));
                        LOGGER.info("Available Capacity - " + availableCapacity);
                        LOGGER.info("******************************************************************");


                        String message = String.format("We have a center available : \n" +
                                        "Pincode - %s \n" +
                                        "HospitalName - %s \n" +
                                        "Fee Type - %s \n" +
                                        "Date - %s \n" +
                                        "Available Slots - %s",
                                jsonObject.get("pincode"), jsonObject.get("name"), jsonObject.get("fee_type"), sessionObj.get("date"), availableCapacity);

                        //Generate OTP
                        //AppointmentScheduler.generateOtp(false);

                        //send alert
                        EmailNotification.send(FROM_SENDER,PWD, "yyyy@gmail.com", "Vaccine Alert", message);
                    }
                } catch (Exception exe) {
                    //ignore
                }
            }
        }
    }
}
