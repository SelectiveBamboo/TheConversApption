package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat implements TimePickerDialog.OnTimeSetListener {


    private Preference setTime;

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        getPreference();
        configListener();
    }

    private void getPreference() {
        setTime = findPreference("set_time_notification");
    }

    private void configListener() {
        if (setTime != null){
            setTime.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SettingsFragment.this.showTimeDialog(preference);
                    return true;
                }
            });
        }
    }

    private void showTimeDialog(Preference preference) {
        String value = preference.getSharedPreferences().getString("set_time_notification", "19:00");
        String[] time = value.split(":");
        int hours = Integer.parseInt(time[0]);
        int minutes = Integer.parseInt(time[1]);
        if (getFragmentManager() != null) {
            new TimePickerFragment(this, hours, minutes)
                    .show(getFragmentManager(), getString(R.string.tag_time_picker));
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int h, int m) {
        String time = String.format(Locale.getDefault(),"%02d", h) + ":" + String.format(Locale.getDefault(), "%02d", m);
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this.getContext());
        sharedPreferences.edit().putString("set_time", time).apply();
        // if you use setOnPreferenceChangeListener on it, use setTime.callChangeListener(time);
    }
}
