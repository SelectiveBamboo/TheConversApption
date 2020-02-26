package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.provider.Settings.Global.getString;

public class alarmReceiverNotif extends BroadcastReceiver {

    private List<String> listURLs = new ArrayList<>();

    public void onReceive(Context context, Intent intent)
    {
        if (isWebsiteReachable("theconversation.com"))
        {
            List<File> fetchedFiles = new ArrayList<>();

            for (int i=0; i<listURLs.size(); i++)
            {
                String url = listURLs.get(i);

                try {
                    File file = fetchXML(url, context);
                    fetchedFiles.add(file);
                } catch (Exception e) {e.printStackTrace();}
            }

            for (int i=0; i<fetchedFiles.size(); i++)
            {
                File file = fetchedFiles.get(i);

                try (FileInputStream fis = new FileInputStream(file))
                {
                    List<Entry> entries = TheConversationXmlParser.parse(fis);

                    for (Entry entry : entries)
                    {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "TheConversApptionChannel")
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setContentTitle(entry.getTitle())
                                .setContentText(entry.getSummary())
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(entry.getSummary()))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    }
                }
                catch (FileNotFoundException e) {e.printStackTrace();}
                catch (IOException e) {e.printStackTrace();}
                catch (XmlPullParserException e) {e.printStackTrace();}
            }


            //Intent to invoke app when click on notification.
            //In this sample, we want to start/launch this sample app when user clicks on notification
            Intent intentToRepeat = new Intent(context, MainActivity.class);
            //set flag to restart/relaunch the app
            intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            //Pending intent to handle launch of Activity in intent above
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(context, NotificationHelper.ALARM_TYPE_RTC, intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT);

            //Build notification
            Notification repeatedNotification = buildLocalNotification(context, pendingIntent).build();

            //Send local notification
            NotificationHelper.getNotificationManager(context).notify(NotificationHelper.ALARM_TYPE_RTC, repeatedNotification);
        }
    }

    public NotificationCompat.Builder buildLocalNotification(Context context, PendingIntent pendingIntent, String )
    {
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder()
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(android.R.drawable.arrow_up_float)
                        .setContentTitle()
                        .setContentText()
                        .setAutoCancel(true);

        return builder;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    private File fetchXML(String xmlUrl, Context context) throws MalformedURLException
    {
        String date = DateFormat.getDateTimeInstance().format(new Date());
        String filename = context.getString(R.string.app_name) + date.replace(" ", "").replace(":", "").replace(".", "");

        try {
            File.createTempFile(filename, null, context.getCacheDir());
        } catch (IOException e) { e.printStackTrace(); }

        File fileFetchedXml = new File(context.getCacheDir(), filename);

        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE))
        {
            URLConnection cn = new URL(xmlUrl).openConnection();
            cn.connect();
            InputStream fis = cn.getInputStream();

            byte[] buf = new byte[16384];

            while (fis.read(buf) > 0)
            {
                fos.write(buf);
            }
        }
        catch (FileNotFoundException fnfe) {fileFetchedXml = null;}
        catch (IOException ioe) {fileFetchedXml = null;}

        finally
        {
            return fileFetchedXml;
        }
    }

    public boolean isWebsiteReachable(String websiteURL)
    {
        try {
            InetAddress ipAddr = InetAddress.getByName(websiteURL);
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
}
