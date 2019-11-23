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
import android.net.Uri;
import android.os.Build;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Target;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import cat.cifo.hospitalet.tripmemoriessidemenu.R;
import cat.cifo.hospitalet.tripmemoriessidemenu.ui.utils.DatePickerFragment;
import cat.cifo.hospitalet.tripmemoriessidemenu.ui.utils.PictureUtils;
import model.Trip;


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
    EditText trip_name,country_text;
    private TextView uuid_text;
    private Button mDateButton;
    private Button mTripSendButton;
    private Button mTripContactButton;
    private ImageView mTripPhoto;
    private ImageButton mCameraButton;
    private File mPhotoFile;
    private Uri mPhotoUri; //points to our directory
    private File filesDir;

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
            mViewModel.deleteTrip(mTrip);
            //mCallbacks.onTripDeleted(mTrip.getUUID());
            //Toast.makeText(getActivity(),"Aquest és el nom del trip esborrat: " + mTrip.getName(), Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putSerializable(CURRENT_UUID_ARG,mTrip.getUUID());
            Navigation.findNavController(v).navigate(R.id.nav_home, bundle);

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
        country_text=v.findViewById(R.id.country_name);
        mDateButton = v.findViewById(R.id.trip_date_button);
        mTripSendButton = v.findViewById(R.id.trip_send_button);
        mTripContactButton = v.findViewById(R.id.trip_contact_button);
        mCameraButton = v.findViewById(R.id.trip_camera_button);
        mTripPhoto = v.findViewById(R.id.trip_photo);

        //Pick a date
        mDateButton.setOnClickListener(view -> {
            DatePickerFragment datePickerFragment = new DatePickerFragment().newInstance(mTrip.getDate());
            datePickerFragment.setTargetFragment(this, REQUEST_DATE);
            datePickerFragment.show(getFragmentManager(), ARG_DIALOG_FRAGMENT);
        });

        //Send the trip to a friend
        mTripSendButton.setOnClickListener(view -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_INTENT, setTextToSend());
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent,null);
            startActivity(shareIntent);
        });

        //Select mate
        mTripContactButton.setOnClickListener(view ->{
            Intent contactIntent = new Intent();
            contactIntent.setAction(Intent.ACTION_PICK);
            contactIntent.setData(ContactsContract.Contacts.CONTENT_URI);
            contactIntent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(contactIntent,REQUEST_CONTACT);
        });

        //Get the trip's data
        mViewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        mViewModel.tripLiveData.observe(getViewLifecycleOwner(), trip -> {
            mTrip = trip;
            mPhotoFile = mViewModel.getPhoto(trip);

            try {
                mPhotoUri = FileProvider.getUriForFile(requireActivity(),
                        "cat.cifo.hospitalet.tripmemoriessidemenu.fileprovider",
                        mPhotoFile);
            }catch (Exception e){
                e.printStackTrace();
            }

            //If image does not exist yet
            if (mPhotoUri == null){

                mPhotoFile = getContext().getFileStreamPath("FirstImage.png");

                //File filePath = getContext().getFilesDir();
                //String mypath = filePath.getAbsolutePath();

                mPhotoUri = FileProvider.getUriForFile(requireActivity(),
                        "cat.cifo.hospitalet.tripmemoriessidemenu.fileprovider",
                        mPhotoFile );
            }

        //Take a photo
        final Intent captureImage = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager packageManager = getActivity().getPackageManager();
        boolean canTakePhoto = mPhotoFile != null &&
                               captureImage.resolveActivity(packageManager) != null;
        updatePhotoView(); //In case not new
        mCameraButton.setEnabled(canTakePhoto);
        mCameraButton.setOnClickListener(view -> {
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
            List<ResolveInfo> cameraActivities = getActivity().getPackageManager().
                    queryIntentActivities(captureImage,PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo activity: cameraActivities){
                getActivity().grantUriPermission(activity.activityInfo.packageName,
                        mPhotoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            startActivityForResult(captureImage, REQUEST_PHOTO);
        });

        updateUI();

     });

     mViewModel.loadUUID(mUUID);

     return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode, data);
        if(requestCode == REQUEST_CONTACT && data!=null) {
            Uri contactUri = data.getData();
            Cursor c = getActivity().getContentResolver().query(contactUri,null,null, null, null);

                try {
                    if (c.getCount() == 0) {
                        return;
                    }
                    c.moveToFirst();
                    String company = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String number  = c.getString (c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    number = number.replace("(",""); //delete (
                    number = number.replace (")",""); //delete )
                    number = number.replace ("-",""); //delete -
                    number = number.replaceAll("\\s","");

                    mTrip.setComp(company);
                    mTripContactButton.setText(company + " (" + number + ") ");
                } finally {
                    c.close();
                }
        //For security reasons
        } else if (requestCode == REQUEST_PHOTO){
            getActivity().revokeUriPermission(mPhotoUri,
                   Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }






  //Retrieve text from screen
    private void updateUI() {
        trip_name.setText(mTrip.getName());
        //uuid_text.setText(String.valueOf(mTrip.getUUID()));
        country_text.setText(mTrip.getCountry());
        mDateButton.setText(DateFormat.getDateInstance(DateFormat.LONG).format(mTrip.getDate()));
        updatePhotoView();
    }

    //Prepare text to send to mate
    private String setTextToSend() {
        return getString(R.string.viatge_text_per_enviar, mTrip.getName(),mTrip.getCountry(), mTrip.getComp());
    }

    //Update Photo using PictureUtils
    private void updatePhotoView(){

        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mTripPhoto.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            InputStream input = null;
            try {
                input = getActivity().getContentResolver().openInputStream(mPhotoUri);
                try {
                    Bitmap bitmapRotated = PictureUtils.rotateImageIfRequired(bitmap,input);
                    mTripPhoto.setImageBitmap(bitmapRotated);
                } catch (IOException e) {
                    e.printStackTrace();
                    mTripPhoto.setImageBitmap(bitmap);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

}

