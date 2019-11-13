package cat.cifo.hospitalet.tripmemoriessidemenu.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import model.Trip;
import model.TripRepository;

public class HomeViewModel extends AndroidViewModel {

    //Variables
    private MutableLiveData<String> mText;
    private final TripRepository tripRepository;
    private LiveData<List<Trip>> mAllTrips;
    private LiveData<Trip> mInsertResult;

    //Constructor
    public HomeViewModel (@NonNull Application application) {
        super(application);
        tripRepository = new TripRepository(application);
        mAllTrips = tripRepository.getAllTrips();
        mInsertResult = getInsertResult();
    }

    //Get all trips
    LiveData<List<Trip>> getAllTrips(){
        return mAllTrips;
    }

    //Insert trip
    void insert(Trip trip){
        mInsertResult = tripRepository.insertTrip(trip);
    }

    LiveData<Trip> getInsertResult(){
        return mInsertResult;
    }


}