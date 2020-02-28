package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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

/*
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RSSTheConvNotif extends IntentService {

    private final static String TAG = RSSTheConvNotif.class.getName();

    private static final String ACTION_NOTIF = "fr.smb.univ.iut.acy.rt2.daroldj.theconversapption.action.notif";

    Context context = this.getApplicationContext();

    public RSSTheConvNotif() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.d("Testing", "Service got created");
        Toast.makeText(this, "ServiceClass.onCreate()", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Log.d("Testing", "Service got destroyed");
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Handling intent in RSSTheConvNotif");

        createNotificationChannel();

        if (isWebsiteReachable("theconversation.com")) {
            List<File> fetchedFiles = new ArrayList<>();

            int n = 844523226;

            for (int i = 0; i < listURLs.size(); i++) {
                String url = listURLs.get(i);

                try {
                    File file = fetchXML(url, context);
                    fetchedFiles.add(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < fetchedFiles.size(); i++) {
                File file = fetchedFiles.get(i);

                try (FileInputStream fis = new FileInputStream(file))
                {
                    List<Entry> entries = TheConversationXmlParser.parseTilADayAgo(fis);

                    for (Entry entry : entries)
                    {
                        Intent intentOnClick = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.getLink()) );
                        intentOnClick.setData(Uri.parse(entry.getLink()));

                        PendingIntent pendingIntent =
                                PendingIntent.getActivity(context, 74940, intentOnClick, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "TheConversApptionChannel")
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setContentTitle(entry.getTitle())
                                .setContentText(entry.getSummary())
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(entry.getSummary()))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                        // notificationId is a unique int for each notification that you must define
                        notificationManager.notify(n, builder.build());

                        n++;

                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public static final String CHANNEL_NAME = "fr.smb.univ.acy.rt2.daroldj.theconversapption.NotifRSS";
    public static final String CHANNEL_DESCRIPTION = "A channel for notification";

    private List<String> listURLs = new ArrayList<>();

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_NAME;
            String description = CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_NAME, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private File fetchXML(String xmlUrl, Context context)
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
