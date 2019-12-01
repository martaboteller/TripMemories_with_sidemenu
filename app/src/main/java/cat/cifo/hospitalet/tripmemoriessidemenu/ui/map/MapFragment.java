package cat.cifo.hospitalet.tripmemoriessidemenu.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;
import cat.cifo.hospitalet.tripmemoriessidemenu.MainActivity;
import cat.cifo.hospitalet.tripmemoriessidemenu.R;
import model.Trip;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    //Variables
    private MapViewModel mapViewModel;
    private GoogleMap mMap;
    private List<Trip> mTrips;

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return root;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Load Markers on map
        mTrips = ((MainActivity) getActivity()).getTrips();

        for(Trip trip : mTrips)
        {
            Double latLoc = Double.valueOf(trip.getLatitude());
            Double longLoc = Double.valueOf(trip.getLongitude());
            String name = trip.getName();
            LatLng newMarker = new LatLng(latLoc, longLoc);

            mMap.addMarker(new MarkerOptions().position(newMarker).icon(BitmapDescriptorFactory
                    .defaultMarker(229)).snippet(trip.getComp())).showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(newMarker));
            //TODO:Perzonalize marker
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
              == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(getContext(), "Assegura't de donar permisos a l'aplicació epr accedir " +
                    "a la teva localització", Toast.LENGTH_LONG).show();
        }
    }



}