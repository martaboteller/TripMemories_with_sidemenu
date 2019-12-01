package cat.cifo.hospitalet.tripmemoriessidemenu.ui.detail;


import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import cat.cifo.hospitalet.tripmemoriessidemenu.MainActivity;
import cat.cifo.hospitalet.tripmemoriessidemenu.R;
import cat.cifo.hospitalet.tripmemoriessidemenu.ui.utils.DatePickerFragment;
import cat.cifo.hospitalet.tripmemoriessidemenu.ui.utils.PictureUtils;
import model.Trip;
import static android.text.TextUtils.isEmpty;



public class DetailFragment extends Fragment implements DatePickerFragment.Callbacks {

    //Variables
    private DetailViewModel mViewModel;
    private static final String ARG_TRIP_ID = "trip_id";
    private static final String CURRENT_UUID_ARG = "uuid";
    private static final String ARG_DIALOG_FRAGMENT = "date_picker_fragment";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;
    private UUID mUUID;
    private Trip mTrip;
    private View v;
    EditText mName,mCountry,mDate,mComp;
    TextView mLocation,mLatitudeCoord,mLongitudeCoord;
    private String number;
    private String mPhotobase;
    private ImageButton mDateButton;
    private ImageButton mCompButton;
    private ImageButton mLocationButton;
    private ImageButton mPhoneButton;
    private ImageButton mPhotoButton;
    private Button mSendButton;
    private ImageView mTripPhoto;
    private File mPhotoFile;
    private Uri mPhotoUri; //points to our directory
    private File filesDir;
    private FusedLocationProviderClient fusedLocationClient;
    private Location lastKnownLocation;
    private Uri uriContact;
    private String contactID;

    @Override
    public void onDateSelected(Date date) {
        String mDateFormated = DateFormat.getDateInstance(DateFormat.LONG).format(date);
        mTrip.setDate(date);
        mDate.setText(mDateFormated);
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

        //Geocoding
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
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
            mTrip.setDeleted(1); //Do not delete from table just change delete state
            mViewModel.deleteTrip(mTrip);

            //mCallbacks.onTripDeleted(mTrip.getUUID());
            //Toast.makeText(getActivity(),"Aquest és el nom del trip esborrat: " + mTrip.getName(), Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putSerializable(CURRENT_UUID_ARG,mTrip.getUUID());
            Navigation.findNavController(v).navigate(R.id.nav_home, bundle);


        } else if (item.getItemId() == R.id.update_trip){

            //Trip with no name will never be saved
            if (isEmpty(mName.getText())){
                mViewModel.deleteTrip(mTrip);

                Bundle bundle = new Bundle();
                bundle.putSerializable(CURRENT_UUID_ARG,mTrip.getUUID());
                Navigation.findNavController(v).navigate(R.id.nav_home, bundle);

            }else {

                //Set variables to mTrip
                //Name
                mTrip.setName(String.valueOf(mName.getText()));
                //Country
                mTrip.setCountry(String.valueOf(mCountry.getText()));
                //Friend
                mTrip.setComp(String.valueOf(mComp.getText()));
                //Location text
                mTrip.setLocation(String.valueOf(mLocation.getText()));
                //Latitude coordinate
                mTrip.setLatitude(Double.valueOf(String.valueOf(mLatitudeCoord.getText())));
                //Longitude coordinate
                mTrip.setLongitude(Double.valueOf(String.valueOf(mLongitudeCoord.getText())));
                //Image (base64)
                //Check if there is an image loaded if not save "new" instead of Base64 string
                if (mPhotobase == "") {
                    mPhotobase = "new";
                }
                mTrip.setPhotobase(mPhotobase);

                //Finally update mTrip
                mViewModel.updateTrip(mTrip);

                ProgressDialog pd = new ProgressDialog(getContext());
                pd.setMessage("updating");
                pd.show();
                mViewModel.getUpdateResult().observe(this, trip -> {
                    pd.hide();
                    Navigation.findNavController(getActivity(),
                            R.id.nav_host_fragment).navigate(R.id.nav_home, null);
                });
            }


        }else if(item.getItemId() == android.R.id.home){
             if (isEmpty(mName.getText())){ //delete if there is no name
                 mViewModel.deleteTrip(mTrip);
             }
        }
        return super.onOptionsItemSelected(item);
    }


    //Associating resources with views
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_detail,container,false);

        mName = v.findViewById(R.id.name);
        mCountry=v.findViewById(R.id.country);
        mDate = v.findViewById(R.id.date);
        mComp = v.findViewById(R.id.comp);
        mLocation = v.findViewById(R.id.location);
        mLatitudeCoord = v.findViewById(R.id.latitudeCoord);
        mLongitudeCoord = v.findViewById(R.id.longitudeCoord);
        mDateButton = v.findViewById(R.id.date_button);
        mSendButton = v.findViewById(R.id.send_button);
        mCompButton = v.findViewById(R.id.comp_button);
        mPhoneButton = v.findViewById(R.id.phone_button);
        mLocationButton = v.findViewById(R.id.location_button);
        mPhotoButton = v.findViewById(R.id.photo_button);
        mTripPhoto = v.findViewById(R.id.photo);

        //Pick a date listener
        mDateButton.setOnClickListener(view -> {
            DatePickerFragment datePickerFragment = new DatePickerFragment().newInstance(mTrip.getDate());
            datePickerFragment.setTargetFragment(this, REQUEST_DATE);
            datePickerFragment.show(getFragmentManager(), ARG_DIALOG_FRAGMENT);
        });

        //Share buttom listener
        mSendButton.setOnClickListener(view -> {
            //Prepare message to send
            String subject = getString(R.string.trip_subject_to_send, mTrip.getName());
            String message = getString(R.string.trip_message_to_send,mTrip.getName(),mTrip.getCountry(),mTrip.getComp());

            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"youremail@yahoo.com"});
            email.putExtra(Intent.EXTRA_SUBJECT, subject);
            email.putExtra(Intent.EXTRA_TEXT, message);
            email.setType("message/rfc822");
            startActivity(Intent.createChooser(email, "Choose an Email client :"));

        });

        //Select friend listener
        mCompButton.setOnClickListener(view ->{
            Intent contactIntent = new Intent(Intent.ACTION_PICK);
            //contactIntent.setData(ContactsContract.Contacts.CONTENT_URI);
            contactIntent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(contactIntent,REQUEST_CONTACT);
        });


        //Make a phone call if there is a phone number
        mPhoneButton.setOnClickListener(view->{
            if(number!= null){
                Intent phoneCall = new Intent();
                phoneCall.setAction(Intent.ACTION_DIAL);
                phoneCall.setData(Uri.fromParts("tel",number,null));
                startActivity(phoneCall);
            }else{
                mPhotoButton.setEnabled(false);
            }
        });

        //Retrive photo from database
        mViewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        mViewModel.tripLiveData.observe(getViewLifecycleOwner(), trip -> {
            mTrip = trip;
            mPhotoFile = mViewModel.getPhoto(trip);

            try {
                mPhotoUri = FileProvider.getUriForFile(requireActivity(),"cat.cifo.hospitalet.tripmemoriessidemenu.fileprovider",mPhotoFile);
            }catch (Exception e){
                 e.printStackTrace();
            }

        //Camera intent
        final Intent captureImage = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager packageManager = getActivity().getPackageManager();
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;

        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(view -> {

            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
            List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(captureImage,PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo activity: cameraActivities){
                getActivity().grantUriPermission(activity.activityInfo.packageName,mPhotoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            startActivityForResult(captureImage, REQUEST_PHOTO);
        });


        //Get localization listener
        mLocationButton.setOnClickListener(view -> {

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener( new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                lastKnownLocation = location;

                                // In some rare cases the location returned can be null
                                if (lastKnownLocation == null) {
                                    return;
                                }

                                if (!Geocoder.isPresent()) {
                                    Toast.makeText(getActivity(),R.string.no_geocoder_available,Toast.LENGTH_LONG).show();
                                    return;
                                }
                                // Start service and update UI to reflect new location
                                ((MainActivity) getActivity()).startIntentService();

                                Double latLoc = location.getLatitude();
                                DecimalFormat df = new DecimalFormat("#.##");
                                String lat = df.format(latLoc);

                                Double longLoc = location.getLongitude();
                                String longL = df.format(longLoc);

                                mLatitudeCoord.setText(lat);
                                mLongitudeCoord.setText(longL);

                            }
                        });

        });

        updateUI();

     });

     mViewModel.loadUUID(mUUID);

     return v;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode, data);
        if(requestCode == REQUEST_CONTACT && data!=null) {
            Uri uriContact = data.getData();

            Cursor c = getContext().getContentResolver().query(uriContact,null,
                    null,null, null, null);

                try {
                    if (c.getCount() == 0) {
                        return;
                    }
                    c.moveToFirst();

                    //int id = c.getInt(c.getColumnIndex(ContactsContract.Contacts.NAME_RAW_CONTACT_ID));
                    String comp = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String numberComp  = c.getString (c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    mTrip.setComp(comp);
                    mTrip.setPhone(numberComp);
                    mComp.setText(comp);

                } finally {
                    c.close();
                }

        //For security reasons
        } else if (requestCode == REQUEST_PHOTO){
            getActivity().revokeUriPermission(mPhotoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
         }
    }

    //Retrieve text from screen
    private void updateUI() {
        //set name
        mName.setText(mTrip.getName());
        //set country
        mCountry.setText(mTrip.getCountry());
        //set date
        mDate.setText(DateFormat.getDateInstance(DateFormat.LONG).format(mTrip.getDate()));
        //set friend
        mComp.setText(mTrip.getComp());
        //set location text
        mLocation.setText((mTrip.getLocation()));
        //set phone number
        number = mTrip.getPhone();
        //set latitude and longitude coordinates
        mLatitudeCoord.setText(mTrip.getLatitude().toString());
        mLongitudeCoord.setText(mTrip.getLongitude().toString());

        //set photo
        if (mTrip.getPhotobase()  == null || mTrip.getPhotobase().matches("") || mTrip.getPhotobase().matches("new")){
            //Get a drawable resource
            mPhotobase = "";
            int id = getResources().getIdentifier("cat.cifo.hospitalet.tripmemoriessidemenu:drawable/firstimage", null, null);
            mTripPhoto.setImageResource(id);
        }else{
            mPhotobase = mTrip.getPhotobase();
            Bitmap bitmap = PictureUtils.decodeImate(mTrip.getPhotobase());
            bitmap = PictureUtils.resizeBitmap(bitmap);
            mTripPhoto.setImageBitmap(bitmap);
        }
    }


    private void updatePhotoView(){

            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            InputStream input = null;

            try {
                input = getActivity().getContentResolver().openInputStream(mPhotoUri);
                try {
                    Bitmap bitmapRotated = PictureUtils.rotateImageIfRequired(bitmap,input);
                    mTripPhoto.setImageBitmap(bitmapRotated);
                    //Encode to save to Database
                    mPhotobase = PictureUtils.encodeImage(bitmapRotated);

                } catch (IOException e) {
                    e.printStackTrace();
                    mTripPhoto.setImageBitmap(bitmap);
                    //Encode to save to Database
                    mPhotobase = PictureUtils.encodeImage(bitmap);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

    }

}

