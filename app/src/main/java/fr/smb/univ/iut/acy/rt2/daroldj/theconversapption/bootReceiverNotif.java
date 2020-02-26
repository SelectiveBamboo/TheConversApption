package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class bootReceiverNotif extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            // Set the alarm here.
        }

    }
}