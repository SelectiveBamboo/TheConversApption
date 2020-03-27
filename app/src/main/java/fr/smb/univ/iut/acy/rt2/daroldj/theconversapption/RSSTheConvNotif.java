package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.JobIntentService;
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
public class RSSTheConvNotif extends JobIntentService {

    private static final  String CHANNEL_ID = "channel_TheConversApption";

    public static final String CHANNEL_NAME = "New articles";
    public static final String CHANNEL_DESCRIPTION = "Notifications for the new articles";

    private final static String TAG = RSSTheConvNotif.class.getName();

    private static final String ACTION_NOTIF = "Action.notif";

    Context context = this;

    private SharedPreferences sharedPref;

    public static void startService(Context context)
    {
        enqueueWork(context, RSSTheConvNotif.class, 1001, new Intent());
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    protected void onHandleWork(Intent intent)
    {
        Log.v(TAG, "Handling intent in RSSTheConvNotif");

        Set<String> rssLinks = new HashSet<>();

        String[] regionFeeds = {"US", "FR", "AU", "AF", "UK", "GP", "CA_en", "ID", "ES", "CA_fr", "NL"};

        for (String region : regionFeeds)
        {
            Set<String> feedOfARegion = sharedPref.getStringSet("multiselect_"+region+"_feeds", null);

            if (feedOfARegion != null)
            {
                rssLinks.addAll(feedOfARegion);
            }
        }

        createNotificationChannel();

        if (isWebsiteReachable("theconversation.com"))
        {
            Log.v(TAG, "theconversation is reachable");

            InputStream inputStreamRss = null;

            int n = 1;

            if (rssLinks.size() > 0)
            {
                for (String rssLink : rssLinks)
                {
                    Log.d(TAG, rssLink);

                    try {
                        inputStreamRss  = downloadUrl(rssLink);
                    }
                    catch (Exception e) { e.printStackTrace(); }

                    try {
                        List<Entry> entries = TheConversationXmlParser.parseTilADayAgo(inputStreamRss);

                        for (int i = 0; i<entries.size(); i++)
                        {
                            Entry entry = entries.get(i);

                            String titleInEntry = entry.getTitle();
                            String linkInEntry = entry.getLink();
                            String summaryInEntry = entry.getSummary();

                            Intent intentOnClick = new Intent(Intent.ACTION_VIEW, Uri.parse(linkInEntry));
                            intentOnClick.setData(Uri.parse(linkInEntry));

                            PendingIntent pendingIntent =
                                    PendingIntent.getActivity(context, 74940, intentOnClick, PendingIntent.FLAG_UPDATE_CURRENT);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_notififcation)
                                    .setColor(0xD8352A)
                                    .setContentTitle(titleInEntry)
                                    .setContentText(summaryInEntry)
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .bigText(summaryInEntry))
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setCategory(NotificationCompat.CATEGORY_SOCIAL)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                            // notificationId is a unique int for each notification
                            notificationManager.notify(n, builder.build());

                            n++;

                            Log.v(TAG, "Notification sent.");
                        }
                    }
                    catch (FileNotFoundException e) { Log.e(TAG, "error FileNotFoundException : " + e.getMessage()); }
                    catch (IOException e) { Log.e(TAG, "error IOException : " + e.getMessage()); }
                    catch (XmlPullParserException e) { Log.e(TAG, "error XMLPullParserException : " + e.getMessage()); }
                    catch (NullPointerException npe) { Log.e(TAG, "error nullPointerexception : " + npe.getMessage()); }
                }
            }
            else
            {
                Log.v(TAG, "There was not any feed selected for notification");
            }
        }
    }

    private void createNotificationChannel()
    {
        Log.v(TAG, "notificationChannelCreated !" );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
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

        }
        catch (Exception e)
        {
            return false;
        }
    }
}
