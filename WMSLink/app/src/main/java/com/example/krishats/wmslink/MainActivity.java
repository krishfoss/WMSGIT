/*
Wireless Measurement Systems

This programming supports to connecting HC-05 bluetooth device.
Sending Command to WMS device to control measurements

 */

package com.example.krishats.wmslink;

import android.bluetooth.BluetoothAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import android.app.ProgressDialog;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.logging.LogRecord;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import org.apache.http.message.BasicListHeaderIterator;

public class MainActivity extends ActionBarActivity {


    //Declaring Controls
    public ProgressDialog progressdialog;

    private BluetoothDevice device = null;
    private BluetoothSocket socket = null;
    private InputStream receiveStream = null;
    private OutputStream sendStream = null;
    TextView inp1;TextView  inp2;TextView inp3;TextView inp4;
    ToggleButton toggleButton;

    private ReceiverThread receiverThread; //Thread declaration for read data
    boolean ThreadInt=true; //Thread boolean
    int sCheck=0; //Device check

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inp1=(TextView)findViewById(R.id.input1);
        inp2=(TextView)findViewById(R.id.input2);
        inp3=(TextView)findViewById(R.id.input3);
        inp4=(TextView)findViewById(R.id.input4);

        progressdialog = new ProgressDialog(MainActivity.this);
        progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressdialog.setTitle("WIRELESS MEASUREMENT SYSTEMS");
        progressdialog.setMessage("Connecting...");
        progressdialog.setCanceledOnTouchOutside(false);
        progressdialog.show();


        sCheck=0;

        //Start thread to connect device
        new socketConnect().execute("");

    }

    //Dummy coding
    public void connect() {
        try {
            socket.connect();
            Thread.sleep(1000);

            Toast.makeText(MainActivity.this, "Connection Established ....", Toast.LENGTH_LONG).show();
            progressdialog.hide();

            sendData("1");


        } catch (IOException e) {
            progressdialog.hide();
            Toast.makeText(MainActivity.this, "HC-05 is not available ....", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //Close the socket
    public void close() {
        try {
            if(sCheck==1) {
                sendData("0");
                Thread.sleep(10);

                Toast.makeText(MainActivity.this, "Socket is closed ....", Toast.LENGTH_LONG).show();

                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Thread is for connecting the device
    class socketConnect extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String data="";
            sCheck=0;
            try {

                Set<BluetoothDevice> setpairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
                BluetoothDevice[] pairedDevices = (BluetoothDevice[]) setpairedDevices.toArray(new BluetoothDevice[setpairedDevices.size()]);

                for (int i = 0; i < pairedDevices.length; i++) {
                    if (pairedDevices[i].getName().contains("HC-05")) {
                        device = pairedDevices[i];
                        try {
                            socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                            receiveStream = socket.getInputStream();
                            sendStream = socket.getOutputStream();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }

                socket.connect();

                sendData("1");

                publishProgress("Connection established...");
                sCheck=1;
            }
            catch (Exception ex){
                publishProgress("WMS is not available... \n Please try again" );
            }
            return data;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            try {


                progressdialog.hide();
                Toast.makeText(MainActivity.this, progress[0], Toast.LENGTH_LONG).show();


            } catch (Exception ex) {
            }
        }
    }


    //Thread is for receiving the data
        class ReceiverThread extends AsyncTask<String, String, String> {
            @Override
            protected String doInBackground(String... params) {
                String data="";
                ThreadInt   = true;

                while(ThreadInt){

                    try {

                        Thread.sleep(1000);

                        if(receiveStream.available() > 0) {

                            byte buffer[] = new byte[100];
                            int k = receiveStream.read(buffer, 0, 100);

                            if(k > 0) {
                                byte rawdata[] = new byte[k];
                                for(int i=0;i<k;i++)
                                    rawdata[i] = buffer[i];

                                data = new String(rawdata);
                                publishProgress(data);

                            }
                        }

                    } catch (IOException e) {
                        Log.v("Error : ",e.toString());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                return data;
            }

            @Override
            protected void onProgressUpdate(String... progress) {
            try {

                String mVal="";

                int st = progress[0].indexOf("A");
                int ed = progress[0].indexOf("E");

                if (st > -1 && ed > -1) {
                    mVal=progress[0].substring(st + 1, ed);
                    inp1.setText(mVal);
                }

                st = progress[0].indexOf("B");
                ed = progress[0].indexOf("F");

                if (st > -1 && ed > -1) {
                    mVal=progress[0].substring(st + 1, ed);
                    inp2.setText(mVal);
                }

                st = progress[0].indexOf("C");
                ed = progress[0].indexOf("G");

                if (st > -1 && ed > -1) {
                    mVal=progress[0].substring(st + 1, ed);
                    inp3.setText(mVal);
                }

                st = progress[0].indexOf("D");
                ed = progress[0].indexOf("H");

                if (st > -1 && ed > -1) {
                    mVal=progress[0].substring(st + 1, ed);
                    inp4.setText(mVal);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            }

            @Override
            protected void onPreExecute() {
                Toast.makeText(MainActivity.this, "Measurement is Started ....", Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onPostExecute(String result) {
                Toast.makeText(MainActivity.this, "Measurement is stopped ....", Toast.LENGTH_LONG).show();
            }
        }


    public void sendData(String data) {

        sendData(data, false);
    }

    //Sending the data to WMS
    public void sendData(String data, boolean deleteScheduledData) {
        try {
            sendStream.write(data.getBytes());
            sendStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Event is for start and stop the measurements
    public void onToggleClicked(View view) {
        // Is the toggle on?
        try {
            boolean on = ((ToggleButton) view).isChecked();

            if (on) {
                sendData("2");
                receiverThread = new ReceiverThread();
                receiverThread.execute("");

            } else {

                ThreadInt = false;
                sendData("3");
            }
        }
        catch (Exception ex) {
            Toast.makeText(MainActivity.this, "WMS is not available \n please try again", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    protected void  onDestroy()
    {
        ThreadInt=false;
        close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
       // }

        return super.onOptionsItemSelected(item);
    }
}
