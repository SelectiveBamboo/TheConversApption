package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class createNotifReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent)
    {
        Intent intent1 = new Intent(context, RSSTheConvNotif.class);
        Log.e("createNotifReceiver", "forNotifreceiver made the intent");
        context.startService(intent1);
    }
}
