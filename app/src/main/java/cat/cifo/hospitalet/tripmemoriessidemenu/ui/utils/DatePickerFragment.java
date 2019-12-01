package cat.cifo.hospitalet.tripmemoriessidemenu.ui.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;



public class DatePickerFragment extends DialogFragment {

    //Variables
    private final String ARG_DATE = "date";
    private final static String SELECTED_DATE_ARG = "date";

    public interface Callbacks {
        void onDateSelected(Date date);
    }

    public DatePickerFragment newInstance (Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){

        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int initialYear = calendar.get(Calendar.YEAR);
        int initialMonth = calendar.get(Calendar.MONTH);
        int initialDay = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getContext(),dateListener,initialYear,initialMonth,initialDay);
    }

        DatePickerDialog.OnDateSetListener dateListener = (view, year, month, day) -> {
        Date resultDate = new GregorianCalendar(year, month, day).getTime();
        Callbacks frag = (Callbacks) getTargetFragment();
        frag.onDateSelected(resultDate);

    };




}
