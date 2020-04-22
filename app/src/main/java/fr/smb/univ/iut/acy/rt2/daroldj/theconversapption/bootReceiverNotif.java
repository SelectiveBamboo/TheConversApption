package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

public class bootReceiverNotif extends BroadcastReceiver {

    private final static String TAG = bootReceiverNotif.class.getName(); //DEBUG

    public void onReceive(Context context, Intent intent)
    {
        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED"))
        {
            scheduleNotifService.startService(context);
        }
    }
}