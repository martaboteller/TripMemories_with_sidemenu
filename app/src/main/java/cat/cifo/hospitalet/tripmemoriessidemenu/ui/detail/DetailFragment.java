package cat.cifo.hospitalet.tripmemoriessidemenu.ui.detail;


import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;
import cat.cifo.hospitalet.tripmemoriessidemenu.R;
import cat.cifo.hospitalet.tripmemoriessidemenu.ui.utils.DatePickerFragment;
import model.Trip;


public class DetailFragment extends Fragment implements DatePickerFragment.Callbacks {

    //Variables
    private DetailViewModel mViewModel;
    private static final String ARG_TRIP_ID = "trip_id";
    private static final String CURRENT_UUID_ARG = "uuid";
    private static final String ARG_DIALOG_FRAGMENT = "date_picker_fragment";
    private static final int REQUEST_DATE = 0;
    private UUID mUUID;
    private Trip mTrip;
    private View v;
    EditText trip_name,country_text;
    private TextView uuid_text;
    private Button mDateButton;



    /*//Constructor
    public static DetailFragment newInstance(UUID tripId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIP_ID,tripId);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onDateSelected(Date date) {
        String mDate = DateFormat.getDateInstance(DateFormat.LONG).format(date);
        mTrip.setDate(date);
        mDateButton.setText(mDate);
    }

    public interface Callbacks {
        void onTripDeleted(UUID trip);
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

    //Get argument from callback as soon as created
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUUID = (UUID) getArguments().getSerializable(ARG_TRIP_ID);
        //Toast.makeText(getActivity(),"Aquest és el id: " + uuid, Toast.LENGTH_SHORT).show();
        setHasOptionsMenu(true);
    }

    //Make menu visible
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.detail_fragment,menu);
    }

    //Indicate action when delete button selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.delete_trip){
            //mViewModel.deleteTrip(mTrip);
            mCallbacks.onTripDeleted(mTrip.getUUID());
            /*Bundle bundle = new Bundle();
            bundle.putSerializable(CURRENT_UUID_ARG,mTrip.getUUID());
            Navigation.findNavController(v).navigate(R.id.nav_home, bundle);*/

        }else if(item.getItemId() == R.id.update_trip){

            mTrip.setName(String.valueOf(trip_name.getText()));
            mTrip.setCountry(String.valueOf(country_text.getText()));
            mViewModel.updateTrip(mTrip);

            ProgressDialog pd = new ProgressDialog(getContext());
            pd.setMessage("updating");
            pd.show();
            mViewModel.getUpdateResult().observe(this, trip-> {
                pd.hide();
                Navigation.findNavController(getActivity(),
                        R.id.nav_host_fragment).navigate(R.id.nav_home, null);
            });
        }
        return super.onOptionsItemSelected(item);
    }


    //Associating resources with views
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_detail,container,false);
        trip_name = v.findViewById(R.id.trip_name);
        //uuid_text = v.findViewById(R.id.uuid_text);
        country_text=v.findViewById(R.id.country_name);
        mDateButton = v.findViewById(R.id.trip_date_button);
        mDateButton.setOnClickListener(view -> {
            DatePickerFragment datePickerFragment = new DatePickerFragment().newInstance(mTrip.getDate());
            datePickerFragment.setTargetFragment(this, REQUEST_DATE);
            datePickerFragment.show(getFragmentManager(), ARG_DIALOG_FRAGMENT);
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DetailViewModel.class);

        mViewModel.tripLiveData.observe(getViewLifecycleOwner(), trip -> {
            mTrip = trip;
            updateUI();
        });

        mViewModel.loadUUID(mUUID);
    }


    /*public void onViewCreated(@NonNull  final View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        if(getArguments() != null){
            mUUID = (UUID) getArguments().getSerializable(CURRENT_UUID_ARG);
            Toast.makeText(getActivity(), "UUID "+ mUUID, Toast.LENGTH_SHORT).show();
        }
    }*/





    //Retrieve text from screen
    private void updateUI() {
        trip_name.setText(mTrip.getName());
        //uuid_text.setText(String.valueOf(mTrip.getUUID()));
        country_text.setText(mTrip.getCountry());
        mDateButton.setText(DateFormat.getDateInstance(DateFormat.LONG).format(mTrip.getDate()));
    }


}
