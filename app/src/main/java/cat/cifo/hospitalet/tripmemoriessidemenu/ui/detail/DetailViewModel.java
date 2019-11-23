package cat.cifo.hospitalet.tripmemoriessidemenu.ui.detail;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.io.File;
import java.util.List;
import java.util.UUID;
import model.Trip;
import model.TripRepository;

public class DetailViewModel extends AndroidViewModel {

    //Variables
    private TripRepository tripRepository;
    private LiveData<Trip> mTrip;
    private UUID mUUID;
    private MutableLiveData<UUID> tripIdLiveData = new MutableLiveData<>();
    private LiveData<Trip> mUpdateResult;

    //Constructor
    public DetailViewModel (@NonNull Application application){
        super(application);
        tripRepository = new TripRepository(application);
        mUpdateResult = getUpdateResult();
    }

    //Get one trip by id
    public LiveData<Trip> tripLiveData =
            Transformations.switchMap(tripIdLiveData, mUUID ->
                    tripRepository.getTripById(mUUID));

    //Load UUID
    public void loadUUID (UUID mTripId){
        tripIdLiveData.setValue(mTripId);
    }

    LiveData<Trip> getTrip(){
        return mTrip;
    }

    //Database will distinguish between update or insert
    public void updateTrip(Trip trip){
        mUpdateResult = tripRepository.insertTrip(trip);
    }

    LiveData<Trip> getUpdateResult(){
        return mUpdateResult;
    }

    //Delete function
    public void deleteTrip(Trip trip) {
        tripRepository.deleteTrip(trip);
   }

   //Get the photo given it's location
    File getPhoto(Trip trip){

        return tripRepository.getPhotoFile(trip);
    }


}

