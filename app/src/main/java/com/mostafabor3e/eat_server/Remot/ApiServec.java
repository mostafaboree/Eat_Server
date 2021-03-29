package com.mostafabor3e.eat_server.Remot;

import com.mostafabor3e.eat_server.Model.Response;
import com.mostafabor3e.eat_server.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiServec {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAArQePlOg:APA91bEClnzJPZIGcECgtnthrpT_proFFKjvEBkHb6_ujlW856IGTJwqvjGPfobYAje7o0bEYA5J1TYGKBq0uLpz4rdJRAFH8zNirxHCPXnBNeM8jyyFBn314NFU1I_SnIffxLitGW-4"

            }
    )
    @POST("fcm/send")
    Call<Response> sendNotifacation (@Body Sender sender );
}



