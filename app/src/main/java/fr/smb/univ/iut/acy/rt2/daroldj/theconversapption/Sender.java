package fr.smb.univ.iut.acy.rt2.daroldj.theconversapption;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


public class Sender extends Thread {

    private final static String TAG = Sender.class.getName();

    Socket socket;
    InetSocketAddress endpoint;

    Activity activity;
    String ip;
    String message;

    public Sender(Activity activity, String ip, String message)
    {
        this.activity = activity;
        this.ip = ip;
        this.message = message;

        super.start();
    }

    public void connectToServer(InetSocketAddress endpoint) throws IOException
    {
        Log.d(TAG, "Connection...");
        socket.connect(endpoint);
        Log.d(TAG, "Connected !");
    }

    public void write(Socket socket, byte[] request) throws IOException
    {
        socket.getOutputStream().write(request);
        socket.getOutputStream().flush();
    }

    public void run()
    {
        this.endpoint = new InetSocketAddress(ip, 8080);
        try
        {
            socket = new Socket();
            connectToServer(endpoint);
            write(socket, message.getBytes());
            //System.out.println(new String(read(socket)));
        }
        catch (IOException e)
        {

            Log.e(TAG, "error while sending message / opening socket");
            e.printStackTrace();

            try
            {
                socket.close();
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
                Log.e(TAG, "error while closing socket");

                this.activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.e(TAG,"error while closing socket");
                    }
                });

            }
        }

        this.activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.v(TAG,"feedback sent");
                Toast.makeText(Sender.this.activity, "Sent.", Toast.LENGTH_LONG).show();

            }
        });


        try
        {
            socket.close();
        }
        catch (IOException e1)
        {
            Log.e(TAG, "error while closing socket");

            this.activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.e(TAG, "error while closing socket");
                }
            });

            e1.printStackTrace();
        }
    }
}
