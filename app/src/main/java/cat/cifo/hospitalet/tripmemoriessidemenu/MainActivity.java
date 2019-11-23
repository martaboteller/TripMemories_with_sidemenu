package cat.cifo.hospitalet.tripmemoriessidemenu;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.UUID;

import cat.cifo.hospitalet.tripmemoriessidemenu.ui.detail.DetailFragment;
import cat.cifo.hospitalet.tripmemoriessidemenu.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity implements HomeFragment.Callbacks, DetailFragment.Callbacks{

    private AppBarConfiguration mAppBarConfiguration;
    private static final int MY_LOCATION_REQUEST_CODE = 0;

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
        /*if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_LOCATION_REQUEST_CODE);
        }*/

    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) { if (permissions.length == 1 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) { Toast.makeText(this, "Ara podràs fer servir mapes",
                Toast.LENGTH_LONG).show(); } else {
            Toast.makeText(this, "Assegura't de donar permisos a l'aplicació per acceir a la teva localització", Toast.LENGTH_LONG).show();
        } }
    }*/

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

}
