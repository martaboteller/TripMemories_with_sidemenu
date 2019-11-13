package model;

import androidx.room.Delete;

import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
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

    @POST("cifotestpost.php")
    //@GET("post.php")
    Call<Trip> insertTrip(@Body Trip trip);

    //@DELETE("cifotestpostpf.php")
    @HTTP(method = "DELETE", path = "cifotestpost.php", hasBody = true)
    Call<Trip> deleteTrip(@Body Trip trip);


}
