package cat.cifo.hospitalet.tripmemoriessidemenu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.List;
import java.util.UUID;
import cat.cifo.hospitalet.tripmemoriessidemenu.ui.detail.DetailFragment;
import cat.cifo.hospitalet.tripmemoriessidemenu.ui.detail.DetailViewModel;
import cat.cifo.hospitalet.tripmemoriessidemenu.ui.home.HomeFragment;
import cat.cifo.hospitalet.tripmemoriessidemenu.ui.utils.Constants;
import cat.cifo.hospitalet.tripmemoriessidemenu.ui.utils.FetchAddressIntentService;
import model.Trip;

public class MainActivity extends AppCompatActivity implements HomeFragment.Callbacks,
        DetailFragment.Callbacks,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private FusedLocationProviderClient mFusedLocationClient;
    private Location lastLocation;//Geocoding
    private AddressResultReceiver resultReceiver; //Geocoding
    private AppBarConfiguration mAppBarConfiguration;
    private static final int MY_LOCATION_REQUEST_CODE = 0;
    private TextView mLocation;
    private List<Trip> mTrips;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_map, R.id.nav_about)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        //Ask for geoloc permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_LOCATION_REQUEST_CODE);
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>(){
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    lastLocation = location;
                                    if (location != null) {
                                        /*Toast.makeText(MainActivity.this,
                                                location.getLatitude()+""+location.getLongitude(),
                                                Toast.LENGTH_LONG).show();*/
                                    }
                                }
        });

        resultReceiver = new AddressResultReceiver(null);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Ara podràs fer servir mapes",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Assegura't de donar permisos a l'aplicació per acceir a la teva localització", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public void onTripSelected(UUID trip) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("trip_id", trip);
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.nav_detail, bundle);
    }


    @Override
    public void onTripDeleted(UUID trip) {
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.nav_home);
    }


    public void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation);
        startService(intent);
    }

    //Checking geolocalization
    @Override
    public void onConnected(Bundle bundle){

    }

    @Override
    public void onConnectionSuspended (int number){

    }

    @Override
    public void onConnectionFailed (ConnectionResult connRes){

    }


    //Set Trips
    public void setTrips (List<Trip> mTrips){
        this.mTrips = mTrips;
    }


    //Get Trips
    public List<Trip> getTrips(){
        return mTrips;
    }


    //Search address by its coordinates
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData == null) {
                return;
            }
            // Display the address string
            // or an error message sent from the intent service.
            String addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            if (addressOutput == null) {
                addressOutput = "";
            }

            displayAddressOutput(addressOutput);

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //Toast.makeText(getApplication().getBaseContext(),getString(R.string.address_found),Toast.LENGTH_LONG);
            }
        }
    }


    //Function that sets the address on the corresponding TextView
    private void displayAddressOutput(final String addressText){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLocation = findViewById(R.id.location);
                mLocation.setText(addressText);
            }
        });
     }




}
