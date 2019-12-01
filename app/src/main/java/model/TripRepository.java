package model;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripRepository {

    //Variables
    private final RestApiService apiService;
    private ArrayList<Trip> mTrips = new ArrayList<>();
    private MutableLiveData<List<Trip>> mutableLiveDataTripList = new MutableLiveData<>();
    private MutableLiveData<Trip> mutableLiveDataTrip = new MutableLiveData<>();
    private MutableLiveData<Trip> mutableLiveDataTripInserted = new MutableLiveData<>();
    private Application application;
    private File filesDir;

    //Constructor
    public TripRepository(Application application) {
        this.application = application;
        apiService = RetrofitInstance.getApiService();
        filesDir = application.getApplicationContext().getFilesDir();
    }

    //Function to get all trips
    public MutableLiveData<List<Trip>> getAllTrips() {

        Call<ArrayList<Trip>> call = apiService.getAllTrips();
        call.enqueue(new Callback<ArrayList<Trip>>() {

            @Override
            public void onResponse(Call<ArrayList<Trip>> call, Response<ArrayList<Trip>> response){
                ArrayList<Trip> mTrips = response.body();
                mutableLiveDataTripList.setValue(mTrips);
            }

            @Override
            public void onFailure(Call<ArrayList<Trip>> call, Throwable t) {
                Log.e("ERROR TRIPPING", t.getMessage());
            }
        });
        return mutableLiveDataTripList;
    }

    //Function that retrieves a trip by id
    public MutableLiveData<Trip> getTripById(UUID uuid) {
        Call<ArrayList<Trip>> call = apiService.getTripById(uuid.toString());
        call.enqueue(new Callback<ArrayList<Trip>>() {

           @Override
           public void onResponse(Call<ArrayList<Trip>> call, Response<ArrayList<Trip>> response) {
               ArrayList<Trip> mTrip = response.body();
               if (mTrip.size() > 0) {
                   mutableLiveDataTrip.setValue(mTrip.get(0));
               }
           }
           @Override
           public void onFailure(Call<ArrayList<Trip>> call, Throwable t) {
               Log.e("ERROR TRIPPING", t.getMessage());
           }
        });
        return mutableLiveDataTrip;
    }

    //Function that inserts a trip
    public LiveData<Trip> insertTrip(Trip trip) {
        Call<Trip> call = apiService.insertTrip(trip);
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                mutableLiveDataTripInserted.setValue(response.body());
            }
            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                Log.e("ERROR TRIPPING", t.getMessage());
            }
        });
        return mutableLiveDataTripInserted;
    }

    //Function that deletes a trip (changes its status to 1 on table/database)
    public LiveData<Trip> deleteTrip(Trip trip) {
        Call<Trip> call = apiService.deleteTrip(trip);
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                mutableLiveDataTripInserted.setValue(response.body());
            }
            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                Log.e("ERROR TRIPPING", t.getMessage());
            }
        });
        return mutableLiveDataTripInserted;
    }

    //Function that finds the photo file by it's name
    public File getPhotoFile(Trip trip){
        return new File(filesDir,trip.getPhoto());
    }


}
