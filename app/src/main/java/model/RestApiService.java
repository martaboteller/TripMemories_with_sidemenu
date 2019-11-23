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

    //@GET("cifotestp4.php")
    @GET("get.php")
    Call<ArrayList<Trip>> getAllTrips();

    //@GET("cifotestp4.php")
    @GET("get.php")
    // Call<ArrayList<Trip>> getTripById(@Query("uuid") String uuid);
    Call<ArrayList<Trip>> getTripById(@Query("tripuuid") String tripid);

    //@POST("cifotestpostp4.php")
    //@POST("post.php")
    @HTTP(method= "INSERT", path = "post.php", hasBody = true)
    Call<Trip> insertTrip(@Body Trip trip);

    //@DELETE("cifotestpostp4.php")
    @HTTP(method = "DELETE", path = "post.php", hasBody = true)
    Call<Trip> deleteTrip(@Body Trip trip);


}
