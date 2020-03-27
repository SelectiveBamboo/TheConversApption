package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import java.util.Locale;
import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat implements TimePickerDialog.OnTimeSetListener {

    private Preference timeNotifPref;
    private SwitchPreferenceCompat allowNotifPref;
    private MultiSelectListPreference UsFeedSelections;

    private SharedPreferences sharedPrefs;

    private Context context;

    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        getPreference();
        configListener();

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext()));

        context = this.getContext();
    }

    private void getPreference()
    {
        timeNotifPref = findPreference("set_time_notification");
        allowNotifPref = findPreference("switch_allow_notif");
        UsFeedSelections = findPreference("multiselect_US_feeds");
    }

    private void configListener()
    {
        if (timeNotifPref != null)
        {
            timeNotifPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    SettingsFragment.this.showTimeDialog(preference);
                    return true;
                }
            });
        }

        if (allowNotifPref != null)
        {
            allowNotifPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference)
                {
                    boolean isNotifEnabled = sharedPrefs.getBoolean("switch_allow_notif", true);
                    Log.d(this.getClass().getName(), "isNotifEnabled in settings Fragment: " + isNotifEnabled);

                    if (!isNotifEnabled)
                    {
                        Toast.makeText(context, "notifications disabled", Toast.LENGTH_LONG).show();

                        //cancel the next scheduled notifications
                        Intent intentToCancel = new Intent(context, createNotifReceiver.class);
                        PendingIntent pendingIntentToCancel = PendingIntent.getBroadcast(context, 0, intentToCancel, PendingIntent.FLAG_UPDATE_CURRENT);

                        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                        am.cancel(pendingIntentToCancel);
                        pendingIntentToCancel.cancel();
                    }
                    else if (isNotifEnabled)
                    {
                        Toast.makeText(context, "notifications enabled", Toast.LENGTH_LONG).show();

                        Intent scheduleNotifIntent = new Intent(getContext(), scheduleNotifService.class);
                        getActivity().startService(scheduleNotifIntent);
                    }

                    return true;
                }
            });
        }
    }

    private void showTimeDialog(Preference preference)
    {
        String value = preference.getSharedPreferences().getString("set_time_notification", "12:00");
        String[] time = value.split(":");

        int hours = Integer.parseInt(time[0]);
        int minutes = Integer.parseInt(time[1]);

        if (getFragmentManager() != null)
        {
            new TimePickerFragment(this, hours, minutes)
                    .show(getFragmentManager(), getString(R.string.tag_time_picker));
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int h, int m)
    {
        String time = String.format(Locale.getDefault(),"%02d", h) + ":" + String.format(Locale.getDefault(), "%02d", m);

        sharedPrefs.edit().putString("set_time_notification", time).apply();

        Intent scheduleNotifIntent = new Intent(getContext(), scheduleNotifService.class);
        scheduleNotifService.startService(context);

        Toast.makeText(getContext(), "Notif : " + time, Toast.LENGTH_LONG).show();
        // if you use setOnPreferenceChangeListener on it, use setTime.callChangeListener(time);
    }
}