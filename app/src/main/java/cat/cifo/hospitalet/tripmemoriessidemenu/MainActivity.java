package cat.cifo.hospitalet.tripmemoriessidemenu;

import android.os.Bundle;

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

}
