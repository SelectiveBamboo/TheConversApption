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

import org.threeten.bp.Clock;
import org.threeten.bp.Instant;
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

public class RSSTheConvNotif extends JobIntentService {

    private static final  String CHANNEL_ID = "channel_TheConversApption";

    public static final String CHANNEL_NAME = "New articles";
    public static final String CHANNEL_DESCRIPTION = "Notifications for the new articles";

    private final static String TAG = RSSTheConvNotif.class.getName();

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
        Set<String> rssLinks = new HashSet<>();

        String[] regionFeeds = getResources().getStringArray(R.array.regionCode_Feeds);

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

            //When was the last article notified about
            String lastPublishedDate = sharedPref.getString("instant_lastParsed", Instant.now(Clock.systemUTC()).minusSeconds(42400).toString());
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
                        List<Entry> entries = TheConversationXmlParser.parseUntilInstant(inputStreamRss, lastPublishedDate);

                        if (entries.size() > 0) {
                            for (int i = 0; i < entries.size(); i++) {
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
                            }
                            Log.v(TAG, "Notification sent");

                            //To get the date of the most recent article in notifications
                            if (Instant.parse(entries.get(0).getPublished()).isAfter(Instant.parse(lastPublishedDate))) {
                                lastPublishedDate = Instant.parse(entries.get(0).getPublished()).toString();
                                Log.d(TAG, "onHandleWork: LastPublishedDate: ---- " + lastPublishedDate);
                            }
                        }
                    }
                    catch (IOException ioe) { Log.e(TAG, "error IOException : " + ioe.getMessage()); }
                    catch (XmlPullParserException XMLppe) { Log.e(TAG, "error XMLPullParserException : " + XMLppe.getMessage()); }
                    catch (NullPointerException npe) { Log.e(TAG, "error nullPointerException : " + npe.getMessage()); }
                }

                sharedPref.edit().putString("instant_lastParsed", lastPublishedDate).commit();
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

        return fileFetchedXml;
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
