package cat.cifo.hospitalet.tripmemoriessidemenu.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.UUID;

import cat.cifo.hospitalet.tripmemoriessidemenu.R;
import model.Trip;

public class HomeFragment extends Fragment {


    //Variables
    private HomeViewModel homeViewModel;
    private View v;
    private List<Trip> trips;
    private TripListAdapter adapter;
    public static UUID mUUID;
    private static final String CURRENT_UUID_ARG = "uuid";

    //First constructor
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    //Associating resources with views
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        v = inflater.inflate(R.layout.fragment_home, container, false);
        return v;
    }

    public interface Callbacks {
        void onTripSelected(UUID trip);
    }

    private Callbacks mCallbacks = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    //Indicating there is a menu as soon as created
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    //Making the menu visible
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.main_fragment,menu);
    }

    //Indicate action when insert button selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if(item.getItemId() == R.id.new_trip){
            //Date d1 = new Date(10,10,2018);
            Trip mtrip = new Trip("","");
            homeViewModel.insert(mtrip);
            homeViewModel.getInsertResult().observe(this,result ->
                    mCallbacks.onTripSelected(mtrip.getUUID()));

            //Bundle bundle = new Bundle();
            //bundle.putSerializable(CURRENT_UUID_ARG,trip.getUUID());
            //Navigation.findNavController(v).navigate(R.id.nav_detail, bundle);
        }
        return super.onOptionsItemSelected(item);
    }



    //For recyclerview use
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Create an adapter
        adapter = new TripListAdapter(getContext());
        //Create a recyclerView
        RecyclerView recyclerView = v.findViewById(R.id.recyclerview);
        //Set the adapter to the recyclerView
        recyclerView.setAdapter(adapter);
        //Measure and position each itemView in recyclerView
        //Determine the policy for when to recycle itemViews no longer visible to the user
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Create an instance to the ViewModel with ViewModelProviders
        //LiveData is a background thread, need to be observed
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

    }


    @Override
    public void onResume(){
        super.onResume();
        homeViewModel.getAllTrips().observe(this, new Observer<List<Trip>>() {
            @Override
            public void onChanged(@Nullable final List<Trip> trips) {
                adapter.setTrips(trips);
            }
        });
    }


    //Class moved from outside
    public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.TripViewHolder> {

        //Variables
        private final LayoutInflater mInflater;
        private List<Trip> mTrips;

        //Constructor
        TripListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        //Interaction between itemView and textView in layout
        public class TripViewHolder extends RecyclerView.ViewHolder {
            private final TextView tripName;

            private TripViewHolder(View itemView) {
                super(itemView);
                tripName = itemView.findViewById(R.id.textView);
            }
        }

        @NonNull
        @Override
        public HomeFragment.TripListAdapter.TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
            return new TripListAdapter.TripViewHolder(itemView);
        }

        //With the position and the holder element get the current Trip and assign the data (tripName)
        @Override
        public void onBindViewHolder(@NonNull TripListAdapter.TripViewHolder holder, int position) {
            final Trip current = mTrips.get(position);

            holder.tripName.setText(current.getName());
            //Navitate with each interaction of the element's list
            holder.tripName.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View v){
                    /*//Toast.makeText(getActivity(),"Trip pressed!", Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(CURRENT_UUID_ARG,current.getUUID());
                    Navigation.findNavController(v).navigate(R.id.nav_detail, bundle);*/
                    mCallbacks.onTripSelected(current.getUUID());
                }
            });
        }

        //Set the private variable mTrips (list of Trips)
        void setTrips(List<Trip> trips) {
            mTrips = trips;
            notifyDataSetChanged();
        }

        //Get the number of Trips from the private variable mTrips
        @Override
        public int getItemCount() {
            if (mTrips != null)
                return mTrips.size();
            else return 0;
            }
    }


}
