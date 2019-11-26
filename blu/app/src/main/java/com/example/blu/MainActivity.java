package com.example.blu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private ListView list;

    private ArrayAdapter<String> mArrayAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket btSocket;
    private ArrayList<BluetoothDevice> btDeviceArray = new ArrayList<BluetoothDevice>();
    private ConnectAsyncTask connectAsyncTask;

    private BluetoothAdapter mBTAdapter;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    Button btn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();


        //inicia BT

        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(mArrayAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = btDeviceArray.get(position);
                connectAsyncTask.execute(device);
                Toast.makeText(getApplicationContext(),"Conectado",Toast.LENGTH_SHORT).show();
            }
        });

        // Instance AsyncTask
        connectAsyncTask = new ConnectAsyncTask();

        //Get Bluettoth Adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Check smartphone support Bluetooth
        if(mBluetoothAdapter == null){
            //Device does not support Bluetooth
            Toast.makeText(getApplicationContext(), "Not support bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Check Bluetooth enabled
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        // Queryng paried devices
        Set<BluetoothDevice> pariedDevices = mBluetoothAdapter.getBondedDevices();
        if(pariedDevices.size() > 0){
            for(BluetoothDevice device : pariedDevices){
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                btDeviceArray.add(device);
            }
        }


        btn1 = (Button) findViewById(R.id.btn1);


        //dispEmparejados = (ListView)findViewById(R.id.lista);


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Conexion en proceso",Toast.LENGTH_SHORT).show();


                OutputStream mmOutStream = null;

                try {
                    mmOutStream = btSocket.getOutputStream();
                    mmOutStream.write(new String("1").getBytes());

                } catch (IOException e) { }


            }
        });

    }

   /*@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        BluetoothDevice device = btDeviceArray.get(position);
        connectAsyncTask.execute(device);
        Toast.makeText(getApplicationContext(),"Conectado",Toast.LENGTH_SHORT).show();

    }*/



    private class ConnectAsyncTask extends AsyncTask<BluetoothDevice, Integer, BluetoothSocket> {

        private BluetoothSocket mmSocket;
        private BluetoothDevice mmDevice;

        @Override
        protected BluetoothSocket doInBackground(BluetoothDevice... device) {

            mmDevice = device[0];

            try {

                String mmUUID = "00001101-0000-1000-8000-00805F9B34FB";
                mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(mmUUID));
                mmSocket.connect();

            } catch (Exception e) { }

            return mmSocket;
        }

        @Override
        protected void onPostExecute(BluetoothSocket result) {

            btSocket = result;

        }




    }



}
