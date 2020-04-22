package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TimePickerFragment extends DialogFragment {

    private TimePickerDialog.OnTimeSetListener onTimeSetListener;
    private int hours;
    private int minutes;

    TimePickerFragment(TimePickerDialog.OnTimeSetListener onTimeSetListener, int hours, int minutes)
    {
        this.onTimeSetListener = onTimeSetListener;
        this.hours = hours;
        this.minutes = minutes;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        return new TimePickerDialog(getActivity(), R.style.dateTimePicker,
                onTimeSetListener, hours, minutes, DateFormat.is24HourFormat(getActivity()));
    }
}
