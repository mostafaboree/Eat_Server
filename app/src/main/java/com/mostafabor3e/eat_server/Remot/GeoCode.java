package com.mostafabor3e.eat_server.Remot;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeoCode {
    @GET("maps/api/geocode/json")
    Call<String> getCoeCode (@Query("address") String address);
    @GET("maps/api/directions/json")
    Call<String>getDirections(@Query("origin")String origin,@Query("destination")String destination);
}
