package model;

import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestApiService {

    @GET("cifotest.php")
    //@GET("get.php")
    Call<ArrayList<Trip>> getAllTrips();

    @GET("cifotest.php")
    //@GET("get.php")
    Call<ArrayList<Trip>> getTripById(@Query("tripid") String tripid);

    @POST("cifotestpost")
    //@GET("post.php")
    Call<Trip> insertTrip(@Body Trip trip);

    @DELETE("cifotestpost.php")
    Call<Trip> deleteTrip(@Path("tripid") String tripid);


}
