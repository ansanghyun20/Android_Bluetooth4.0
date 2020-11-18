package com.example.pratyush.ble_chat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ClientActivity extends Activity {
    private long backKeyPressedTime = 0;
    private Toast toast;


    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat mFormat2 = new SimpleDateFormat("hh:mm:ss");

    String I_Sensor="Infrared", P_Sensor="PT-1000";
    String M_Sencor;
    String Temperature;
    int count =0, count2 =0;
    int a,b;
    float temp_result=0;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanCallback mScanCallback;
    private BluetoothGatt mGatt;
    TextView DeviceInfoTextView;
    TextView DeviceText;

    Button startScanning;
    Button stopScanning;
    Button disconnect;
    Button PHPB;
    Button PHPSB;


    public ListView lv;
    public List<BluetoothDevice> mDevices;
    boolean mEchoInitialized;
    public static String SERVICE_STRING = "00010203-0405-0607-0809-0A0B0C0D1911";
    private String mMsgStr = "";
    public static String CHARACTERISTIC_ECHO_STRING = "00010203-0405-0607-0809-0A0B0C0D2B19";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);




        setContentView(R.layout.activity_client);
        mDevices = new ArrayList<BluetoothDevice>();
        DeviceInfoTextView = (TextView) findViewById(R.id.client_device_info_text_view);
        startScanning = (Button) findViewById(R.id.start_scanning_button);
        stopScanning = (Button) findViewById(R.id.stop_scanning_button);
        disconnect = (Button) findViewById(R.id.disconnect_button);
        PHPB = (Button) findViewById(R.id.phpbutton);
        PHPSB = (Button) findViewById(R.id.phpsearchbutton);
        DeviceText = (TextView) findViewById(R.id.device_text);


        Button confirm = (Button) findViewById(R.id.send_message_confirm);
        Button send = (Button) findViewById(R.id.send_message_button);
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        TextView ipadder = (TextView) findViewById(R.id.ipadder);
        final TextView temp_measure = (TextView) findViewById(R.id.temp_Measure);


        Intent intent1 = getIntent(); /*데이터 수신*/
        final String ipname = intent1.getExtras().getString("ipname");
        ipadder.setText(ipname);




        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(ClientActivity.this,ClientActivity.class);

                startActivity(intent);

                finish();                  // 화면 재설정     C,E 버튼
                confirm();*/
            }
        });



        startScanning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();

            }
        });
        stopScanning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopScan();

            }
        });
        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //disconnectGattServer();
            }
        });



    lv = (ListView) findViewById(R.id.device_list);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object o = lv.getItemAtPosition(i);

                lv.setVisibility(View.GONE);
                DeviceText.setVisibility(View.GONE);
                String device = o.toString();
                connect(mDevices.get(i));
                count++;
            }
        });
        lv.setVisibility(View.GONE);
        DeviceText.setVisibility(View.GONE);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count>0) {
                    sendMessage();
                }else{
                    Toast.makeText(getApplicationContext(), "디바이스를 선택해 주세요.", Toast.LENGTH_SHORT).show();

                }

            }
        });



        final NumberPicker picker1 = (NumberPicker)findViewById(R.id.picker1);
        final NumberPicker picker2 = (NumberPicker)findViewById(R.id.picker2);




        PHPB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = new Date();
                String Date = mFormat.format(date);
                String Time = mFormat2.format(date);
                Intent intentmain = new Intent(getApplicationContext(), PHP.class);
                intentmain.putExtra("temp",Temperature); /*송신*/
                intentmain.putExtra("date",Date); /*송신*/
                intentmain.putExtra("ipname",ipname); /*송신*/
                intentmain.putExtra("time",Time); /*송신*/
                intentmain.putExtra("sensor",M_Sencor); /*송신*/
                a=picker1.getValue();
                b=picker2.getValue();
                if(temp_result<a || temp_result>b) {
                    intentmain.putExtra("result","X"); /*송신*/
                }else{
                    intentmain.putExtra("result","O"); /*송신*/
                }

                startActivity(intentmain);

            }

         });


        PHPSB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentmain = new Intent(getApplicationContext(), PHPSEARCH.class);
                intentmain.putExtra("ipname",ipname); /*송신*/
                startActivity(intentmain);

            }

        });






        //--------------------------------------------------------------------------------------------------------------
        final SwipeRefreshLayout swipe=findViewById(R.id.refresh_layout);;
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(false);

                temp_measure.setText(Temperature);

                int temp_result1=Integer.valueOf((int) temp_result);
                if(count2>0) {
                    picker1.setMinValue(temp_result1 - 10);
                    picker1.setMaxValue(temp_result1 + 10);
                    picker1.setWrapSelectorWheel(false);


                    picker2.setMinValue(picker1.getValue());
                    picker2.setMaxValue(picker1.getValue() + 20);
                    picker2.setValue(picker1.getValue() + 20);
                    picker2.setWrapSelectorWheel(false);
                }
            }
        });
        //--------------------------------------------------------------------------------------------------------------






    }





    public void onBackPressed() {

    }



    @Override
    protected void onResume() {
        super.onResume();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver3);
        mBluetoothAdapter.cancelDiscovery();
        mGatt.disconnect();
        mGatt.close();

    }

    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action != null && action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                device.getName();
                device.getAddress();
                if(device!=null)
                    mDevices.add(device);
                lv.setAdapter(new ArrayAdapter<BluetoothDevice>(ClientActivity.this, R.layout.simpleitem, mDevices));

            }
        }
    };


    private void startScan() {
        mBluetoothAdapter.startDiscovery();
        IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        lv.setVisibility(View.VISIBLE);
        DeviceText.setVisibility(View.VISIBLE);
    }

    private void stopScan() {
        mBluetoothAdapter.cancelDiscovery();
    }

    private void connect(BluetoothDevice device) {
        final BluetoothDevice device1 = device;
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (device1 != null) {
                    mGatt = device1.connectGatt(getApplicationContext(),
                            false, mCallback);
                    stopScan();
                }
            }
        });
    }



    private void sendMessage() {

        TextView messageTextview = (TextView) findViewById(R.id.messageee);
        BluetoothGattCharacteristic characteristic = BluetoothUtils.findEchoCharacteristic(mGatt);
        if (characteristic == null) {
            Log.i("Characteristic", "Unable to find echo characteristic.");
            try
            {
                mGatt.disconnect();
                mGatt.close();
            }
            catch(Exception e)
            {
                Log.i("Disconnect", "Problem disconnecting.");
            }
            return;
        }
//-------------------------------------------------------------------------------------
        Date date = new Date();
        String date2 = mFormat.format(date);
        String date3 = mFormat2.format(date);
        String message=("D,"+date2+",E");                    //보내는 데이터값
        String message2=("T,"+date3+"E");
        String textdate=(date2);                             //텍스트에 표시하는 데이터값
        messageTextview.setText(textdate);                  //텍스트 표시
//---------------------------------------------------------------------------------------
        Log.i("send", "Sending message: " + message);
        byte[] messageBytes = StringUtils.bytesFromString(message);
        byte[] messageBytes2 = StringUtils.bytesFromString(message2);
        if (messageBytes.length == 0) {
            return;
        }
        characteristic.setValue(messageBytes);
        characteristic.setValue(messageBytes2);
        boolean success = mGatt.writeCharacteristic(characteristic);
        if (success) {
            Log.i("write", "Wrote: " + StringUtils.byteArrayInHexFormat(messageBytes));
        } else {
            Log.i("write", "Failed to write data");
        }
    }

    //------------------------------------------------------------------------------------------------------------------*
    private void confirm() {
        BluetoothGattCharacteristic characteristic = BluetoothUtils.findEchoCharacteristic(mGatt);
        if (characteristic == null) {
            Log.i("Characteristic", "Unable to find echo characteristic.");
            try
            {
                mGatt.disconnect();
                mGatt.close();
            }
            catch(Exception e)
            {
                Log.i("Disconnect", "Problem disconnecting.");
            }
            return;
        }
//-------------------------------------------------------------------------------------

        String message=("C,E");                    //보내는 데이터값



//---------------------------------------------------------------------------------------
        //String message = messageEditText.getText().toString();

        Log.i("send", "Sending message: " + message);

        byte[] messageBytes = StringUtils.bytesFromString(message);


        if (messageBytes.length == 0) {
            //logError("Unable to convert message to bytes");
            return;
        }

        characteristic.setValue(messageBytes);
        //characteristic.setValue(messageBytes2);

        boolean success = mGatt.writeCharacteristic(characteristic);
        if (success) {
            Log.i("write", "Wrote: " + StringUtils.byteArrayInHexFormat(messageBytes));
        } else {
            Log.i("write", "Failed to write data");
        }
    }

    public void initializeEcho() {
        mEchoInitialized = true;
    }

    //------------------------------------------------------------------------------------------------------------------*

    public BluetoothGattCallback mCallback = new BluetoothGattCallback() {


        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    final BluetoothGatt mGatt = gatt;
                    Handler handler;
                    handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mGatt.discoverServices();
                        }
                    });
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    try
                    {
                        Log.i("no_conn", "Connection unsuccessful with status"+status);
                        mGatt.close();
                    }
                    catch(Exception e)
                    {
                    }
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.i("Not success", "Device service discovery unsuccessful, status " + status);
                return;
            }
            //List<BluetoothGattService> matchingServices = gatt.getServices();
            gatt.getService(UUID.fromString(SERVICE_STRING));
            List<BluetoothGattCharacteristic> matchingCharacteristics = BluetoothUtils.findCharacteristics(gatt);
            if (matchingCharacteristics.isEmpty()) {
                Log.i("No characteristics", "Unable to find characteristics.");
                return;
            }

            //log("Initializing: setting write type and enabling notification");
            for (BluetoothGattCharacteristic characteristic : matchingCharacteristics) {
                characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                enableCharacteristicNotification(gatt, characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
               Log.i("Write successful", "Characteristic written successfully");
            } else {
                Log.i("Unsuccessful", "Characteristic write unsuccessful, status: " + status);
                //mGatt.disconnect();
                mGatt.close();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("Read success", "Characteristic read successfully");


            } else {
                Log.i("Read unsuccessful", "Characteristic read unsuccessful, status: " + status);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.i("Characteristic change", "Characteristic changed, " + characteristic.getUuid().toString());
            readCharacteristic(characteristic);
        }

        private void enableCharacteristicNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            boolean characteristicWriteSuccess = gatt.setCharacteristicNotification(characteristic, true);
            if (characteristicWriteSuccess) {
                Log.i("Notific success", "Characteristic notification set successfully for " + characteristic.getUuid().toString());
                if (BluetoothUtils.isEchoCharacteristic(characteristic)) {
                    initializeEcho();
                }
            } else {
                Log.i("Notification failure.", "Characteristic notification set failure for " + characteristic.getUuid().toString());
            }
        }

        private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
            byte[] messageBytes = characteristic.getValue();
            Log.i("read", "Read: " + StringUtils.byteArrayInHexFormat(messageBytes));
            String message = StringUtils.stringFromBytes(messageBytes);
            if (message == null) {
                return;
            }
            Log.i("Received", "Received message: " + message);
            TextView messagerecevie = (TextView) findViewById(R.id.recevie);
            byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                String str = new String(data);
                    mMsgStr = mMsgStr + str +"\n";
                    if (mMsgStr.startsWith("R,")) {
                    String[] dataArr = mMsgStr.split(",");
                    messagerecevie.setText(P_Sensor+" : "+dataArr[1]+"℃");
                        M_Sencor=P_Sensor;
                        Temperature=dataArr[1];
                        temp_result= Float.valueOf(dataArr[1]);
                        count2++;

                } else if (mMsgStr.startsWith("I,")) {

                    String[] dataArr = mMsgStr.split(",");
                        M_Sencor=I_Sensor;
                        messagerecevie.setText(I_Sensor+" : "+dataArr[1]+"℃");
                        Temperature=dataArr[1];
                        temp_result= Float.valueOf(dataArr[1]);
                        count2++;

                    }
                    if (!str.endsWith(",E")) {
                        return;
                    }
            }
            mMsgStr = "";
        }

    };

}
