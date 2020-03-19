package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RSSTheConvNotif extends IntentService {

    private static final  String CHANNEL_ID = "channel_TheConversApption";

    public static final String CHANNEL_NAME = "fr.smb.univ.acy.rt2.daroldj.theconversapption.NotifRSS";
    public static final String CHANNEL_DESCRIPTION = "A channel for notification";

    private List<String> listURLs = new ArrayList<>();

    private final static String TAG = RSSTheConvNotif.class.getName();

    private static final String ACTION_NOTIF = "fr.smb.univ.iut.acy.rt2.daroldj.theconversapption.action.notif";

    Context context = this;

    private SharedPreferences sharedPref;

    public RSSTheConvNotif() {
        super(TAG);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.e(TAG, "Service's been created in RSSTheConvNotif");
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        Log.d(TAG, "Service's been destroyed");
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.d(TAG, "Handling intent in RSSTheConvNotif");

        Set<String> rssLinks = sharedPref.getStringSet("multiselect_US_feeds", new HashSet<String>());

        createNotificationChannel();

        if (isWebsiteReachable("theconversation.com"))
        {
            Log.d(TAG, "Website reachable");

            List<File> fetchedFiles = new ArrayList<>();

            InputStream inputStreamRss = null;

            int n = 1;

            for (String rssLink : rssLinks)
            {
                try {
                    inputStreamRss  = downloadUrl(rssLink);
                }
                catch (IOException e) { e.printStackTrace(); }
              /*  try {
                    File file1 = do
                        File file = fetchXML(rssLink, context);
                    fetchedFiles.add(file);
                } catch (Exception e) { e.printStackTrace(); }
              */

                // for (int i = 0; i < fetchedFiles.size(); i++) {
               // File file = fetchedFiles.get(i);

                try {
                    List<Entry> entries = TheConversationXmlParser.parseTilADayAgo(inputStreamRss);

                    for (Entry entry : entries)
                    {
                        String linkInEntry = entry.getLink();
                        String summaryInEntry = entry.getSummary();

                        Intent intentOnClick = new Intent(Intent.ACTION_VIEW, Uri.parse(linkInEntry));
                        intentOnClick.setData(Uri.parse(linkInEntry));

                        PendingIntent pendingIntent =
                                PendingIntent.getActivity(context, 74940, intentOnClick, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setContentTitle(summaryInEntry)
                                .setContentText(summaryInEntry)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(entry.getSummary()))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                        // notificationId is a unique int for each notification that you must define
                        notificationManager.notify(n, builder.build());

                        n++;

                        Log.i(TAG, "Notification sent.");
                    }
                }
                catch (FileNotFoundException e) { e.printStackTrace(); }
                catch (IOException e) { e.printStackTrace(); }
                catch (XmlPullParserException e) { e.printStackTrace(); }
                catch (NullPointerException npe) { npe.printStackTrace(); }
            }
        }
    }

//    public NotificationCompat.Builder buildLocalNotification(Context context, PendingIntent pendingIntent)
//    {
//        NotificationCompat.Builder builder =
//                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
//                        .setContentIntent(pendingIntent)
//                        .setSmallIcon(android.R.drawable.arrow_up_float)
//                        .setContentTitle()
//                        .setContentText()
//                        .setAutoCancel(true);
//
//        return builder;
//    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        Log.e(TAG, "notificationChannelCreated !" );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_NAME;
            String description = CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private InputStream downloadUrl(String urlString) throws IOException
    {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }


    private File fetchXML(String xmlUrl, Context context)
    {
        String date = DateFormat.getDateTimeInstance().format(new Date());
        String filename = context.getString(R.string.app_name) + date.replace(" ", "").replace(":", "").replace(".", "");
        File fileFetchedXml = null;

        try {
          fileFetchedXml =  File.createTempFile(filename, null, context.getCacheDir());
        }
        catch (IOException e) { e.printStackTrace(); }

       // File fileFetchedXml = new File(context.getCacheDir(), filename);

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

        return fileFetchedXml;      //can return a null object !
    }

    public boolean isWebsiteReachable(String websiteURL)
    {
        try {
            InetAddress ipAddr = InetAddress.getByName(websiteURL);
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
}
