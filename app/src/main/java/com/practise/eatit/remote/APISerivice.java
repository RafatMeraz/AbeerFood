package com.practise.eatit.remote;

import com.practise.eatit.model.MyResponse;
import com.practise.eatit.model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;

public interface APISerivice {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAYkDNBFM:APA91bEYXtnyLQ0Upf-CONJx8SKsmINiMK7R9vLvXkosqN7AmRdN5t5PUzBE0CfNdYS_DSJyg8LbaBsPOGcoHDaCXzWkjqeigKGP32Uq5l-7-otu3k2C8T7Dt4j3ln9jwXhdhBD9z6gW"
            }
    )

    Call<MyResponse> sendNotification(@Body Sender body);
}
