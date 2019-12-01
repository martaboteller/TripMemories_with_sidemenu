package model;


import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Query;

public interface RestApiService {

    //@GET("cifotestp4.php") //teacher service
    @GET("getv2.php")
    Call<ArrayList<Trip>> getAllTrips();

    //@GET("cifotestp4.php") //teacher service
    @GET("getv2.php")
    Call<ArrayList<Trip>> getTripById(@Query("tripuuid") String tripid);

    //POST("cifotestpostp4.php") //teacher service
    @HTTP(method= "INSERT", path = "postv2.php", hasBody = true)
    Call<Trip> insertTrip(@Body Trip trip);

    //@DELETE("cifotestpostp4.php") //teacher service
    @HTTP(method = "DELETE", path = "postv2.php", hasBody = true)
    Call<Trip> deleteTrip(@Body Trip trip);


}
