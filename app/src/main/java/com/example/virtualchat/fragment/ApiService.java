package com.example.virtualchat.fragment;

import com.example.virtualchat.notification.MyResponse;
import com.example.virtualchat.notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAgWA7aZA:APA91bEusrU0Mt3ANFOWqAbHjfYSgmO4G-rQo3Iun0BIV7zZOjTxOvJ_xRmG7BVeQ9NeFSkpChptTArN8HRJ0x99v3BZcRhTrXMCZAxXSwGCtqcPyvutgmEC9HrSESH2Q0b12r2BW9Sr"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
