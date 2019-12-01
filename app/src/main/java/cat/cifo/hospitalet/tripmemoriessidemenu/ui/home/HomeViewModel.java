package cat.cifo.hospitalet.tripmemoriessidemenu.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;
import model.Trip;
import model.TripRepository;

public class HomeViewModel extends AndroidViewModel {

    //Variables
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

    //Function that returns all trips
    LiveData<List<Trip>> getAllTrips(){
        return mAllTrips;
    }

    //Function that inserts a trip
    void insert(Trip trip){
        mInsertResult = tripRepository.insertTrip(trip);
    }

    //Function that returns the inserted result
    LiveData<Trip> getInsertResult(){
        return mInsertResult;
    }


}